package com.gkmonk.pos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gkmonk.pos.model.Inventory;
import com.gkmonk.pos.model.ReportDetails;
import com.gkmonk.pos.model.logs.TaskLogs;
import com.gkmonk.pos.model.notification.OrderNotification;
import com.gkmonk.pos.services.GSTServiceImpl;
import com.gkmonk.pos.services.InventoryServiceImpl;
import com.gkmonk.pos.services.ReportServiceImpl;
import com.gkmonk.pos.services.StateServiceImpl;
import com.gkmonk.pos.services.logs.TaskLogsServiceImpl;
import com.gkmonk.pos.services.notification.NotificationServiceImpl;
import com.gkmonk.pos.utils.GSTUtils;
import com.gkmonk.pos.utils.MapperUtils;
import com.gkmonk.pos.utils.TaskUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/v1/report")
public class ReportController {

    private static final Logger log = LoggerFactory.getLogger(ReportController.class);
    private static int PRODUCT_TITLE = 0;
    private static int PRODUCT_TYPE = 1;
    @Autowired
    private ReportServiceImpl reportServiceImpl;
    @Autowired
    private StateServiceImpl stateServiceImpl;
    @Autowired
    private GSTServiceImpl gstServiceImpl;
    @Autowired
    private InventoryServiceImpl inventoryService;
    @Autowired
    private TaskLogsServiceImpl taskLogsService;
    @Autowired
    private NotificationServiceImpl notificationService;
    @PostMapping("/uploadJSONL")
    public String uploadJSONLReport(@RequestParam("file") MultipartFile file) {
        ModelAndView model = new ModelAndView();
        model.setViewName("shopifyreport");

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            ObjectMapper objectMapper = new ObjectMapper();
            List<ReportDetails> reportDetailsList = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                JSONObject jsonObject = new JSONObject(line);
                ReportDetails reportDetails = new ReportDetails();
                MapperUtils.toBeanFromJson(jsonObject, reportDetails);
                reportDetails.setId(reportDetails.getOrderId()+reportDetails.getProductVariantSku());
                reportDetailsList.add(reportDetails);
            }
            reportServiceImpl.saveReportDetails(reportDetailsList);
            model.addObject("reportDetailsList", reportDetailsList);
        } catch (Exception e) {
            // Handle exception
            System.out.println(e.getMessage());
        }
        return "redirect:/v1/report/shopifyreport";
    }

    @GetMapping("/shopifyreport")
    public ModelAndView getShopifyReport() {
        ModelAndView model = new ModelAndView();
        List<ReportDetails> reportDetailsList = reportServiceImpl.getReportDetailsByDate("2025-01-01","2025-09-30");
        stateServiceImpl.updateStateCodes(reportDetailsList);
        gstServiceImpl.updateHSNCode(reportDetailsList);
        reportDetailsList.forEach(report -> report.setProductTitle(report.getProductTitle().replaceAll(",", "")));
        reportDetailsList.forEach(GSTUtils::updateGST);
        model.addObject("reportDetailsList", reportDetailsList);
        model.setViewName("shopifyreport");
        return model;
    }

    @PostMapping("/upload")
    public ModelAndView uploadReport(@RequestParam("file") MultipartFile file) {
        ModelAndView model = new ModelAndView();
        model.setViewName("shopifyreport");

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String fileContent = reader.lines().collect(Collectors.joining("\n"));

            List<ReportDetails> reportDetailsList = new ArrayList();
            String[] lines = fileContent.split("\n");
            int i= 0;
            for (String line : lines) {
                if(i++ == 0){
                    continue;
                }
                String[] fields = line.split(",");
                ReportDetails reportDetails = new ReportDetails();
                reportDetailsList.add(reportDetails);
            }
            model.addObject("reportDetailsList", reportDetailsList);
            // Code to process the reportDetailsList
        } catch (Exception e) {
            // Handle exception
            System.out.println(e.getMessage());
        }
        return model;
    }


    @GetMapping("")
    public ModelAndView generateReport() {
        // Code to generate report
        ModelAndView model = new ModelAndView();
        model.setViewName("reports");
        return model;
    }

    @RequestMapping("/generate/lastmonth")
    public String generateLastMonthReport() {
        // Code to generate last month report
        return "";
    }

    @RequestMapping("/uploadCSV")
    public ModelAndView uploadCSV() {
        ModelAndView model = new ModelAndView();
        model.setViewName("uploadcsv");
        return model;
    }

    @GetMapping("/inventoryfilterreport")
    public ModelAndView getInventoryFilterReport() {
        ModelAndView model = new ModelAndView();
        model.setViewName("inventoryfilterreport");
        Optional<List<Inventory>> inventoryList = inventoryService.findReportsNoStorage();
        if(inventoryList.isPresent() && !inventoryList.get().isEmpty()) {
            model.addObject("reportDetailsList", inventoryList.get());
        }
        return model;
    }

    @GetMapping("/dailytasksreport")
    public ModelAndView getDailyTasksReport(){
        ModelAndView model = new ModelAndView("dailytasksreport");
        Optional<List<TaskLogs>> taskLogs = taskLogsService.getAllTasks();
            List<TaskLogs> logsWithPoints = taskLogs.get().stream()
                    .filter(log -> true)//log.getPoints() > 0)
                    .collect(Collectors.toList());
            // Use logsWithPoints as needed
            logsWithPoints.forEach(log -> {
                    log.setDeviceId(TaskUtils.extractDeviceId(log.getMetaData()));
                    log.setEmpName(getEmpName(log.getEmpId()));
            });
             model.addObject("taskLogs", logsWithPoints);

       return model;
    }

    private String getEmpName(String empId) {
        return empId == null ? "Unknown" : switch (empId) {
            case "1" -> "Suhani";
            case "2" -> "Radhika";
            case "3" -> "Poonam";
            default -> "Unknown";
        };
    }

    @GetMapping("/whatsappeventsreport")
    public ModelAndView getWhatsAppEventsReport() {
        ModelAndView model = new ModelAndView("whatsappeventsreport");
        //  List<TaskLogs> taskLogs = taskLogsService.getWhatsAppEvents();
        //model.addObject("taskLogs", taskLogs);
        return model;
    }



    @GetMapping("/notificationbyorderid/{orderid}")
    public ResponseEntity<OrderNotification> findNotificationDetailsByOrderId(@PathVariable("orderid") String orderId){
       log.info("Fetching records from db for :"+orderId);
        OrderNotification orderNotification =  notificationService.findByOrderId(orderId);
        if(orderNotification != null) {
            log.info("Record found for:"+orderId);
            return ResponseEntity.ok(orderNotification);
        } else {
            log.info("No record found for:"+orderId);
            return ResponseEntity.notFound().build();
        }
    }
}
