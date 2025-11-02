package com.gkmonk.pos.controller.employee;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gkmonk.pos.model.emp.EmployeeDetails;
import com.gkmonk.pos.services.ImageDBServiceImpl;
import com.gkmonk.pos.services.employee.EmployeeDetailsService;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v1/employees")
public class EmployeeController {

    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFsOperations gridFsOps;

    @Value("${gm.admin.bcryptHash:}")
    private String adminBcryptHash;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private EmployeeDetailsService employeeDetailsService;
    private String plainAdminPassword = "admin1234"; // Set this if you want plaintext fallback
    @Autowired
    private ImageDBServiceImpl imageService;

    @GetMapping("/search/{id}")
    public ResponseEntity<EmployeeDetails> searchEmployeeById(@PathVariable("id") String id) {
        // Implementation to search employee by ID
        Optional<EmployeeDetails> employeeDetails = employeeDetailsService.findEmployeeById(id);
        return employeeDetails.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.ok(new EmployeeDetails()));
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateEmployee(@RequestBody Map<String, Object> payload) {
        String empId = (String) payload.get("empId");
        if (empId == null || empId.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "Employee ID is required"));
        }

        Optional<EmployeeDetails> employeeDetails = employeeDetailsService.findEmployeeById(empId);
        if (employeeDetails.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("ok", false, "message", "Employee not found"));
        }

        EmployeeDetails updatedRecord = mapPayloadToEmployee(payload,employeeDetails,null);

        employeeDetailsService.saveEmployeeDetails(updatedRecord);
        Map<String, Object> res = new HashMap<>();
        res.put("ok", true);
        res.put("message", "Employee updated");
        res.put("empId", updatedRecord.getEmpId());
        return ResponseEntity.ok(res);
    }

        @GetMapping("/mgt")
    public ModelAndView employeeManagement() {
        ModelAndView modelAndView = new ModelAndView("empmgt");
        return modelAndView;
    }

    @GetMapping("/auth")
    public ResponseEntity<?> authAdmin(@RequestParam("password") String password) {
        if (!StringUtils.hasText(password)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("ok", false, "message", "Password required"));
        }

        boolean ok = false;

        // Preferred: BCrypt check
        if (StringUtils.hasText(adminBcryptHash)) {
            try {
                ok = BCrypt.checkpw(password, adminBcryptHash);
            } catch (IllegalArgumentException ex) {
                // Hash format invalid
                ok = false;
            }
        }

        // Optional plaintext fallback (uncomment ONLY if you configured it)
         if (!ok && StringUtils.hasText(plainAdminPassword)) {
             ok = plainAdminPassword.equals(password);
         }

        if (ok) {
            return ResponseEntity.ok(Map.of("ok", true));
        } else {
            // 401 so the client can handle redirect to /v1/home as you do
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("ok", false, "message", "Invalid password"));
        }
    }

    @PostMapping(
            value = "/update-with-photo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> updateWithPhoto(
            @RequestPart("data") String json,
            @RequestPart(value = "photo", required = false) MultipartFile photo
    ) {

        try {
            Map<String, Object> payload  = objectMapper.readValue(json, Map.class);

            String empId = (String) payload.get("empId");
            if (empId == null || empId.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "Employee ID is required"));
            }

            Optional<EmployeeDetails> employeeDetails = employeeDetailsService.findEmployeeById(empId);
            if (employeeDetails.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("ok", false, "message", "Employee not found"));
            }

            EmployeeDetails empDetails = mapPayloadToEmployee(payload, employeeDetails, photo);
            employeeDetailsService.saveEmployeeDetails(empDetails);
            System.out.println("Payload: " + payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok(Map.of(
                "ok", true,
                "message", "Employee updated",
                "empId", "",
                "photoUrl","" //StringUtils.hasText(emp.getPhotoUrl()) ? emp.getPhotoUrl() : photoUrl
        ));
    }

    private EmployeeDetails mapPayloadToEmployee(Map<String, Object> payload, Optional<EmployeeDetails> employeeDetails, MultipartFile photo) {

        String empName = (String) payload.get("empName");
        String email = (String) payload.get("email");
        String phone = (String) payload.get("phone");
        String dept = (String) payload.get("dept");
        String role = (String) payload.get("role");
        String status = (String) payload.get("status");
        Double salary = payload.get("salary") != null ? ((Number) payload.get("salary")).doubleValue() : null;

        EmployeeDetails emp = employeeDetails.get();
        if (com.gkmonk.pos.utils.StringUtils.isNotBlank(empName)) {
            emp.setEmpName(empName);
        }
        if (com.gkmonk.pos.utils.StringUtils.isNotBlank(email)) {
            emp.setEmail(email);
        }
        if (com.gkmonk.pos.utils.StringUtils.isNotBlank(phone)) {
            emp.setContactNumber(phone);
        }
        if (com.gkmonk.pos.utils.StringUtils.isNotBlank(dept)) {
            emp.setDepartment(dept);
        }
        if (com.gkmonk.pos.utils.StringUtils.isNotBlank(role)) {
            emp.setRole(role);
        }
        if (com.gkmonk.pos.utils.StringUtils.isNotBlank(status)) {
            emp.setStatus(status);
        }
        if (salary != null) {
            emp.setSalary(salary);
        }
        if(photo != null){
            String fileId = null;
            try {
                fileId = imageService.saveImages(photo.getInputStream(), photo.getOriginalFilename());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            emp.setPhotoGridFsId(fileId);
        }
        return emp;
    }


    @GetMapping("/photo/{id}")
    public ResponseEntity<?> getPhoto(@PathVariable("id") String id) throws IOException {

        List<byte[]> bytes = imageService.fetchInventoryImagesById(id);        // your storage fetch
        MediaType type = MediaType.IMAGE_JPEG;                 // or detect dynamically
        return ResponseEntity.ok()
                .contentType(type)
                .cacheControl(CacheControl.noCache())          // or as you prefer
                .body(bytes.get(0));
    }

}