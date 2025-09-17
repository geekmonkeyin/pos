package com.gkmonk.pos.controller;

import com.gkmonk.pos.model.CartonRequest;
import com.gkmonk.pos.model.InboundData;
import com.gkmonk.pos.model.VideoRequest;
import com.gkmonk.pos.model.logs.TaskStatusType;
import com.gkmonk.pos.model.logs.TaskType;
import com.gkmonk.pos.services.InboundServiceImpl;
import com.gkmonk.pos.services.logs.TaskLogsServiceImpl;
import com.gkmonk.pos.utils.InboundStatus;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v1/inbound")
public class InboundController {



    @Autowired
    private TaskLogsServiceImpl taskLogsService;
    @Autowired
    private InboundServiceImpl inboundService;

    @PostMapping("/completeOrder")
    public ResponseEntity<String> completeOrder(@RequestBody InboundData inboundData) {
        // Set the status to COMPLETED
        inboundData.setStatus(InboundStatus.CLOSED);
       boolean updated =  inboundService.updateStatus(inboundData.getId(),inboundData.getClosedBy(),inboundData.getStatus());
       String metaData = "Status:"+updated+"Vendor Name: " + inboundData.getVendorName() + ", Vendor ID: " + inboundData.getVendorName() +
                ", Number of Boxes: " + inboundData.getNumberOfBoxes() + ", Inbound Date: " + inboundData.getReceivedDate();
        taskLogsService.addLogs(TaskType.INBOUND_INVENTORY.name(), TaskStatusType.COMPLETED.name(), metaData, LocalDate.now().toString());
        return updated ? ResponseEntity.ok("Order completed successfully") : ResponseEntity.ok("Order failed to close.");
    }

    @PostMapping("/initiateBoarding")
    public ResponseEntity<Map<String, String>> initiateBoarding(@RequestBody InboundData inboundData) {
        // Set the status to DRAFT
        String metaData = "Vendor Name: " + inboundData.getVendorName() + ", Vendor ID: " + inboundData.getVendorName() +
                ", Number of Boxes: " + inboundData.getNumberOfBoxes() + ", Inbound Date: " + inboundData.getReceivedDate();

       taskLogsService.addLogs(TaskType.INBOUND_INVENTORY.name(), TaskStatusType.START.name(), metaData, LocalDate.now().toString());
        inboundData.setStatus(InboundStatus.DRAFT);
        // Save the inbound data
        Map<String,String> vendorDetails = new HashMap<>();

        if(inboundData.getId() == 0){
            inboundData.setId(inboundService.generateSequence("inbound_sequence"));
            inboundData = inboundService.saveInboundData(inboundData);

        }
        Integer currentStep = inboundService.fetchCurrentStep(inboundData.getId());
        if(currentStep > inboundData.getNumberOfBoxes()){
            //change the status to completed
            inboundData.setStatus(InboundStatus.CLOSED);
            inboundService.saveInboundData(inboundData);
            taskLogsService.addLogs(TaskType.INBOUND_INVENTORY.name(), TaskStatusType.COMPLETED.name(), metaData, LocalDate.now().toString());

            return ResponseEntity.ok(new HashMap<>());
        }
        vendorDetails.put("inboundId", String.valueOf(inboundData.getId()));
        vendorDetails.put("currentStep", ""+currentStep);
        vendorDetails.put("cartonNo", String.valueOf(inboundData.getNumberOfBoxes()));
        taskLogsService.addLogs(TaskType.INBOUND_INVENTORY.name(), TaskStatusType.IN_PROGRESS.name(), metaData, LocalDate.now().toString());

        return ResponseEntity.ok(vendorDetails);

    }

    @GetMapping("/steps")
    public ModelAndView getInboundSteps(@RequestParam("inboundId") String inboundId,@RequestParam("currentStep") String step) {
        ModelAndView modelAndView = new ModelAndView();
        InboundData inboundData = inboundService.getInboundDataById(Long.parseLong(inboundId));
        List<CartonRequest> cartonRequests = inboundService.getCartonRequestsByStep(inboundId,step);
        if (inboundData == null) {
            modelAndView.setViewName("error");
            modelAndView.addObject("message", "Inbound data not found for ID: " + inboundId);
            return modelAndView;
        }
        if(cartonRequests != null) {
            modelAndView.addObject("cartons", cartonRequests);
        }
        modelAndView.addObject("inboundData", inboundData);
        modelAndView.setViewName("inboundsteps");
        return modelAndView;
    }



    @GetMapping("")
    public ModelAndView inbound() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("inbound");
        return modelAndView;
    }

    @PostMapping("/saveVideo")
    public String saveVideo(@RequestBody VideoRequest videoRequest) {
        try {
            // Call the service to save video data
            inboundService.saveVideoData(videoRequest);
            return "Video data saved successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to save video data: " + e.getMessage();
        }
    }

    @GetMapping("getDraftInboundOrders/{vendorName}")
    public ResponseEntity<InboundData> getDraftInboundOrders(@PathVariable String vendorName) {
        return ResponseEntity.ok(inboundService.getDraftInboundOrders(vendorName));
    }

    @PostMapping("/saveCarton")
    public ResponseEntity<String> saveCarton(@RequestParam("inboundId") String inboundId,
                                             @RequestParam("cartonNo") int cartonNo,
                                             @RequestParam("products") String productsJson) {
        try {

            JSONArray products = new JSONArray(productsJson);
            inboundService.saveCartons(inboundId,cartonNo, products);

            //inboundService.saveCartonDetails(inboundId, cartonNo, video, image, products);

            return ResponseEntity.ok("Carton saved successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save carton: " + e.getMessage());
        }
    }

    @PostMapping("/saveImage")
    public ResponseEntity<String> saveImage(@RequestBody CartonRequest cartonRequest) {
        try {
            // Validate the image file
            if (cartonRequest.getImage().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Image file is empty");
            }
            // Save the image using the service layer
           // ProductImages imageData = new ProductImages(productId, cartonNo, image.getBytes(),inboundId);
            inboundService.saveImageData(cartonRequest);
            return ResponseEntity.ok("Image saved successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save image: " + e.getMessage());
        }
    }

    @GetMapping("/inboundreport")
    public ModelAndView inboundReport(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("inboundreport");
        return modelAndView;
    }

    @GetMapping("/getcartonrequest/{inboundId}")
    public ResponseEntity<List<CartonRequest>> getCartonRequest(@PathVariable("inboundId") String inboundId
                                                                 ) {
        try {
            List<CartonRequest> cartonRequests = inboundService.getCartonRequests(inboundId);
            return ResponseEntity.ok(cartonRequests);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getInboundData/{vendorName}")
    public ResponseEntity<List<InboundData>> getInboundDetailsByVendorName(@PathVariable String vendorName){

        Optional<List<InboundData>> inboundDataList = inboundService.getInboundDetailsByVendorName(vendorName);
        return inboundDataList.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PostMapping("/updatePurchaseAmount")
    public ResponseEntity<String> updatePurchaseAmount(@RequestBody Map<String, Object> requestData) {
        try {
            // Extract inboundId and purchaseAmount from the request body
            Long inboundId = Long.valueOf(requestData.get("inboundId").toString());
            Double purchaseAmount = Double.valueOf(requestData.get("purchaseAmount").toString());

            // Update the purchase amount using the service layer
            boolean isUpdated = inboundService.updatePurchaseAmount(inboundId, purchaseAmount);

            if (isUpdated) {
                return ResponseEntity.ok("Purchase amount updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Inbound ID not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update purchase amount: " + e.getMessage());
        }
    }
}
