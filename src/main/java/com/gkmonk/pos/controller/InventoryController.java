package com.gkmonk.pos.controller;

import com.gkmonk.pos.exception.CSVFileException;
import com.gkmonk.pos.exception.InventoryException;
import com.gkmonk.pos.model.Inventory;
import com.gkmonk.pos.model.logs.TaskStatusType;
import com.gkmonk.pos.model.logs.TaskType;
import com.gkmonk.pos.services.ImageDBServiceImpl;
import com.gkmonk.pos.services.InventoryServiceImpl;
import com.gkmonk.pos.services.logs.TaskLogsServiceImpl;
import com.gkmonk.pos.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/v1/inventory")
public class InventoryController {

    @Autowired
    private TaskLogsServiceImpl taskLogsService;
    @Autowired
    private InventoryServiceImpl inventoryService;
    @Autowired
    private ImageDBServiceImpl imageService;
    //update inventory

    @RequestMapping("/update")
    public ModelAndView updateInventory() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("updateinventory");
        return modelAndView;
    }

    //delete inventory
    @RequestMapping("/delete")
    public String deleteInventory() {
        return "inventory/delete";
    }

    //view inventory
    @RequestMapping("")
    public ModelAndView viewInventory() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("inventory");
        return modelAndView;
    }

    //add inventory
    @RequestMapping("/add")
    public String addInventory() {
        return "inventory/add";
    }

    //upload csv
    @PostMapping("/uploadCSV")
    public ModelAndView uploadCSV(@RequestParam("file") MultipartFile file) {
        ModelAndView modelAndView = new ModelAndView();
        // Handle the file upload logic here
        modelAndView.setViewName("uploadshopifyinventory");
        // Add any necessary attributes to the model
        String status  = inventoryService.parseCSVFile(file);
        if("ERROR".equalsIgnoreCase(status)){
            throw new CSVFileException("Error in parsing CSV file");
        }

        return modelAndView;
    }
    //uploadShopifyInventory
    @GetMapping("/uploadShopifyInventory")
    public ModelAndView uploadShopifyInventory() {
        ModelAndView modelAndView = new ModelAndView();
        // Handle the file upload logic here
        modelAndView.setViewName("uploadshopifyinventory");
        // Add any necessary attributes to the model
        return modelAndView;
    }

    @GetMapping("/productNotListed")
    public ModelAndView productNotListed() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("productNotListed");
        return modelAndView;
    }

    @PostMapping("/importInventory")
    public ResponseEntity<List<Inventory>> importInventory(@RequestBody Inventory inventory){
        List<Inventory> inventoryList = new ArrayList<>();
        inventoryList.add(inventory);
        return ResponseEntity.ok(inventoryList);
    }

    @GetMapping(value="/exportAllInventoryImages/{inventoryId}",produces = "application/json")
    public ResponseEntity<List<byte[]>> exportAllInventoryImages(@PathVariable("inventoryId")String inventoryId){
                List<byte[]> imagesByts = imageService.fetchInventoryImagesById(inventoryId);
        return ResponseEntity.ok(imagesByts);
    }

    @GetMapping(value="/exportAllInventory/{page}/{size}",produces = "application/json")
    public ResponseEntity<Page<Inventory>> exportAllInventory(@PathVariable(value = "page") int page,
                                                              @PathVariable(value = "size") int size){
       Page<Inventory> inventoryList =  inventoryService.getAllProducts(page,size);
       System.out.println("**********************Fetching records from page: "+page+" and size: "+size+"*************************");

       for(Inventory inventory : inventoryList){
           if(inventory.getImages() == null || inventory.getImages().isEmpty()){
               continue;
           }
             List<byte[]> resource = inventoryService.getImageAsMultiPart(inventory.getImages());
            inventory.setResources(resource);

       }
        return ResponseEntity.ok(inventoryList);
    }

    @PostMapping("/notListed")
    public String notListed(@RequestParam(value ="productName", required = false) String productName,
                            @RequestParam(value ="location", required = false) String location,
                            @RequestParam(value ="quantity", required = false) Integer quantity,
                            @RequestParam(value ="inboundDate", required = false) String inboundDate,
                            @RequestParam(value ="isElectronics",required = false) boolean isElectronics,
                            @RequestParam(value = "type", required = false) String type,
                            @RequestParam(value = "images" , required = false) List<MultipartFile> images) {
        String metaData = "Product Name: " + productName + ", Location: " + location + ", Quantity: " + quantity +
                ", Inbound Date: " + inboundDate + ", Is Electronics: " + isElectronics + ", Type: " + type;

        try {

            taskLogsService.addLogs(TaskType.CREATE_NOT_LISTED_ENTRY.name(), TaskStatusType.START.name()
                    ,metaData,LocalDateTime.now(ZoneId.of("Asia/Kolkata")).toString());
            Inventory inventory = new Inventory();
            inventory.setProductTitle(productName);
            inventory.setStorage(location);
            inventory.setQuantity(quantity);
            DateTimeFormatter dateFormat1 = DateUtils.findDateFormat(inboundDate);
            LocalDate date = LocalDate.parse(inboundDate, dateFormat1);
            inventory.setInboundDate(date);
            inventory.setElectronics(isElectronics);
            if(inventory.isElectronics()) {
                inventory.setElectronicsType(type);
            }
            // Process the images
            for (MultipartFile image : images) {
                // Save or process each image as needed

                String fileId =  imageService.saveImages(image.getInputStream(), image.getOriginalFilename());
                inventory.getImages().add(fileId);
            }
            taskLogsService.addLogs(TaskType.CREATE_NOT_LISTED_ENTRY.name(), TaskStatusType.IN_PROGRESS.name()
                    ,metaData,LocalDateTime.now(ZoneId.of("Asia/Kolkata")).toString());

            inventoryService.saveTempInventory(inventory);
            taskLogsService.addLogs(TaskType.CREATE_NOT_LISTED_ENTRY.name(), TaskStatusType.COMPLETED.name()
                    ,metaData,LocalDateTime.now(ZoneId.of("Asia/Kolkata")).toString());

        } catch (Exception e) {
            taskLogsService.addLogs(TaskType.CREATE_NOT_LISTED_ENTRY.name(), TaskStatusType.FAILED.name()
                    ,metaData,LocalDateTime.now(ZoneId.of("Asia/Kolkata")).toString());

            throw new InventoryException("Not able to save the Inventory: "+e.getMessage());
        }
        return "redirect:/v1/inventory/notlistedsummary";
    }

    @GetMapping("notlistedsummary")
    public ModelAndView notListedSummary() {
        ModelAndView modelAndView = new ModelAndView();
        List<Inventory> inventoryList = inventoryService.getAllNotListedProducts();

        modelAndView.setViewName("notlistedsummary");
        modelAndView.addObject("inventoryList", inventoryList);
        return modelAndView;
    }

    @PostMapping("/images")
    public ResponseEntity<List<byte[]>> getImages(@RequestBody String ids) {
        List<byte[]> inventoryImages = imageService.fetchInventoryImagesById(ids);
        if (inventoryImages != null) {
            return ResponseEntity.ok(inventoryImages);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/checkProductLocation")
    public ModelAndView checkProductLocation() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("checkProductLocation");
        return modelAndView;
    }


   @GetMapping("/search")
   public ResponseEntity<List<Inventory>> searchProducts(@RequestParam("query") String query) {
       try{
        if (query.length() < 3) {
           return ResponseEntity.badRequest().body(null);
       }
       List<Inventory> products = inventoryService.searchProductsByName(query);
       if(products.isEmpty()){
           if(isQueryNumber(query)) {
               products = inventoryService.fetchProductsUsingId(query);

           }
       }
           return ResponseEntity.ok(products);
       }catch (Exception e){
           return ResponseEntity.badRequest().body(List.of() );
       }

   }

    private boolean isQueryNumber(String query) {
        if (query == null || query.isEmpty()) {
            return false; // Null or empty strings are not numbers
        }
        return query.matches("\\d+"); // Returns true if the string contains only digits
    }

    //exportInventory
    @GetMapping("/exportInventory")
    public ModelAndView exportInventory() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("exportInventory");
        return modelAndView;
    }

    //exportInventoryAsLink
    @GetMapping("/exportInventoryAsLink")
    public ResponseEntity<String> exportInventoryAsLink(@RequestParam String targetUrl ) {
        inventoryService.exportToLink(targetUrl,0,10);
        return ResponseEntity.ok("Success");
    }

    @GetMapping(value = "/updateProductId/{upcId}/{productId}", consumes = "application/json")
    public ResponseEntity<String> updateProductId(@PathVariable("upcId") String upcId, @PathVariable("productId") String productId) {
        inventoryService.updateProductId(upcId, productId);
        return ResponseEntity.ok("Product ID updated successfully");
    }

    @GetMapping("/deleteEnteries")
    public String deleteEnteries(@RequestParam("productIds") String productId){

        String[] ids = productId.split(",");
        for(String id : ids){
            inventoryService.deleteInventory(id);
        }
        return "Success";
    }


    @PostMapping("updateShopifyStock")
    public ResponseEntity<String> updateStockInShopify(
            @RequestParam(value="productId",required = false) String productId,
            @RequestParam(value="location",required = false) String location,
            @RequestParam(value="quantity",required = false) Integer quantity,
            @RequestParam(value="deviceName",required = false) String deviceName,
            @RequestParam(value="images",required = false) List<MultipartFile> images,
            @RequestParam(value="remarks",required = false) String remarks) {

        // Process the received data
        String metaData = "Product ID: " + productId + ", Location: " + location + ", Quantity: " + quantity +
                ", Remarks: " + remarks+ ", Device ID: " + deviceName;
        if(productId == null || productId.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product ID is required");
        }
        List<Inventory> inventories = inventoryService.fetchProductsUsingId(productId);
        if(inventories == null || inventories.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product not found in inventory");
        }

        return ResponseEntity.ok("stock updated");
    }

        @PostMapping("updateInventory")
    public ResponseEntity<String> updateInventory(
            @RequestParam(value="productId",required = false) String productId,
            @RequestParam(value="location",required = false) String location,
            @RequestParam(value="quantity",required = false) Integer quantity,
            @RequestParam(value="deviceName",required = false) String deviceName,
            @RequestParam(value="images",required = false) List<MultipartFile> images,
            @RequestParam(value="remarks",required = false) String remarks) {

        // Process the received data
        String metaData = "Product ID: " + productId + ", Location: " + location + ", Quantity: " + quantity +
                ", Remarks: " + remarks+ ", Device ID: " + deviceName;

        taskLogsService.addLogs(TaskType.UPDATE_INVENTORY.name(), TaskStatusType.START.name()
                ,metaData,LocalDateTime.now(ZoneId.of("Asia/Kolkata")).toString());
        inventoryService.updateInventory(productId,location, quantity, images,remarks,deviceName);
        System.out.println(location);
        taskLogsService.addLogs(TaskType.UPDATE_INVENTORY.name(), TaskStatusType.COMPLETED.name()
                ,metaData,LocalDateTime.now(ZoneId.of("Asia/Kolkata")).toString());
         return ResponseEntity.ok("Inventory updated successfully");
    }

    @GetMapping("/printBarcode")
    public ModelAndView printBarcode() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("printBarcode");
        return modelAndView;
    }

    @GetMapping("/resetProduct")
    public String resetProduct(@RequestParam("productId") String productId){
        inventoryService.resetProduct(productId);
        inventoryService.fetchProductsUsingId(productId);
        return "Success";
    }


}


