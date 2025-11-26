package com.gkmonk.pos.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gkmonk.pos.constants.ShopifyReportConstants;
import com.gkmonk.pos.exception.CSVFileException;
import com.gkmonk.pos.model.Inventory;
import com.gkmonk.pos.model.Product;
import com.gkmonk.pos.model.StockHistory;
import com.gkmonk.pos.repo.InventoryRepo;
import com.gkmonk.pos.services.shopify.ShopifyServiceImpl;
import com.gkmonk.pos.utils.MapperUtils;
import com.gkmonk.pos.utils.StringUtils;
import com.mongodb.client.MongoClient;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class InventoryServiceImpl {

    @Autowired
    private InventoryRepo inventoryRepo;

    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ShopifyServiceImpl shopifyServiceImpl;
    @Autowired
    private ImageDBServiceImpl imageDBService;

    //parse csv file MultiPart to Product Object
    public String parseCSVFile(MultipartFile file) {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {


            try (CSVReader readerCS = new CSVReaderBuilder(new InputStreamReader(file.getInputStream())).withSkipLines(1).build()) {
                String headerFields = reader.readLine();
                Map<String, Integer> headerMap = MapperUtils.convertCSVHeaderToMap(headerFields.split(","));
                List<String[]> allData = readerCS.readAll();
                for (String[] fields : allData) {
                    // Create a new Product object and set its fields
                    Inventory inventory = new Inventory();
                    MapperUtils.toBean(fields, inventory, headerMap);
                    saveInventory(inventory);
                }
            }
        } catch (Exception ex) {
           throw new CSVFileException(ex.getMessage());
        }
        return "SUCCESS";
    }

    private void saveProduct(Product product) {

    }

    public void saveInventory(Inventory inventory) {
        Optional<Inventory> existingInventory  = inventoryRepo.findByUPCId(inventory.getUpcId());
        if(existingInventory.isPresent()){
            existingInventory.get().setShopifyQuantity(inventory.getShopifyQuantity());
            existingInventory.get().setPrice(inventory.getPrice());
            existingInventory.get().setProductVariantSku(inventory.getProductVariantSku());
            existingInventory.get().setImageUrl(inventory.getImageUrl());
            existingInventory.get().setProductType(inventory.getProductType());
            inventoryRepo.save(existingInventory.get());
        }else {
            inventoryRepo.save(inventory);
        }
    }

    public Inventory fetchProductDetails(String sku) {

        return inventoryRepo.findBySku(sku);
    }

    public void saveTempInventory(Inventory inventory) {
        inventory.setTemporarayInventory(true);
        inventoryRepo.save(inventory);
        // temp
    }



    public List<Inventory> getAllNotListedProducts(){
        return inventoryRepo.findAllTempProducts(Boolean.TRUE);
    }
    public Page<Inventory> fetchAllNotListedProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return inventoryRepo.findAllTempProducts(Boolean.TRUE,pageable);
    }

    public List<Inventory> searchProductsByName(String query) {
       Optional<List<Inventory>> inventories =  inventoryRepo.findAllByNameRegex(query);
        if(inventories.isPresent()) {
            List<Inventory> filteredInventory =  inventories.get();
            filteredInventory.forEach(fi ->{
               // List<byte[]> images = getImageAsMultiPart(fi.getImages());
                //fi.setResources(images);
                 shopifyServiceImpl.updateShopifyDetails(fi);
                 updateInventoryDetailsFromShopify(fi);
            });
            return filteredInventory;
        }
        return new ArrayList<>();
    }

    private void updateInventoryDetailsFromShopify(Inventory fi) {
        Query query = new Query(Criteria.where("_id").is(fi.getUpcId()));
        Update update = new Update().set("productVariantId", fi.getProductVariantId())
                .set("shopifyQuantity", fi.getShopifyQuantity())
                .set("productId", fi.getProductId())
                .set("imageUrl",fi.getImageUrl());
        mongoTemplate.updateFirst(query, update, Inventory.class);
    }

    public void exportToLink(String targetUrl,int page,int size) {
        addInventoryOnTargetServer(targetUrl);
    }

    private void addInventoryOnTargetServer(String targetUrl) {
       int page = 0;
       int size = 5;

        while(true) {
            System.out.println("Fetching details for page no: "+page);
            String url = "http://" + targetUrl + ":8080/v1/inventory/exportAllInventory/" + page++ + "/" + size;
            RestTemplate restTemplate = new RestTemplate();
           // removeRecordsFromServer(targetUrl);
            try {
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    String responseBody = response.getBody();
                    List<Inventory> inventoryList = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(new JSONObject(responseBody).get("content").toString(), new TypeReference<List<Inventory>>() {
                    });
                    if(inventoryList == null || inventoryList.isEmpty()){
                        break;
                    }
                    System.out.println("Fetching details for page no: "+page + " records found:"+ inventoryList.size());
                    inventoryList.forEach(inv -> System.out.println("Inventory: " + inv.getProductTitle() ));
                    inventoryList.forEach(inventory -> {
                        List<byte[]> imagesList = inventory.getResources();
                        List<String> imageIds =  imageDBService.saveImages(imagesList);
                        inventory.setImages(imageIds);
                        inventory.setResources(null);
                    });
                    inventoryRepo.saveAll(inventoryList);
                } else {
                    System.out.println("Failed to get response: " + response.getStatusCode());
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void removeRecordsFromServer(String targetUrl) {
        String url = "http://" + targetUrl + ":8080/v1/inventory/deleteEnteries?";
        RestTemplate restTemplate = new RestTemplate();

        try {
            List<String> updatedProductIds = inventoryRepo.findUpdatedProductIds();
            String updatedProductAsString = String.join(",",updatedProductIds);

            ResponseEntity<String> response = restTemplate.getForEntity(url+"?productIds="+updatedProductAsString, String.class);

        } catch (RestClientException e) {
            e.printStackTrace();;
        }
    }

    private boolean isProductAlreadyAdded(Inventory inv) {
        Optional<Inventory> inventory =  inventoryRepo.findById(new ObjectId(inv.getUpcId()));
        return inventory.isPresent();
    }

    public List<byte[]> getImageAsMultiPart(List<String> images) {
        //List<Resource> imageList = new ArrayList<>();
        List<byte[]> imageList = new ArrayList<>();
        String commaSeparatedImages = String.join(",", images);
        List<byte[]> imagesAsByteArr = imageDBService.fetchInventoryImagesById(commaSeparatedImages);
        if(imagesAsByteArr != null) {
            for(byte[] image : imagesAsByteArr) {
                MockMultipartFile file = new MockMultipartFile("images", image);
                //imageList.add(file.getResource());
                imageList.add(image);
            }
        }
        return imageList;
    }


    public void updateProductId(String upcId, String productId) {
        Query query = new Query(Criteria.where("upcId").is(upcId));
        Update update = new Update().set("productVariantId", productId).set("temporarayInventory", false);
        mongoTemplate.updateFirst(query, update, Inventory.class);
    }

    public void deleteInventory(String id) {
        ObjectId upcId = new ObjectId(id);
        inventoryRepo.deleteByUPCId(upcId);
    }

    public void updateInventory(String productId, String location, Integer quantity, List<MultipartFile> images, String remarks, String deviceName,String empId) {
        List<Inventory> inventoryList = getInventoryByProductId(productId);
        inventoryList.forEach(inventory -> {
           try {
                if(StringUtils.isNotBlank(inventory.getProductVariantId()) && !productId.equalsIgnoreCase(inventory.getProductVariantId())){
                    System.out.println("Product ID mismatch for inventory: " + inventory.getProductVariantId());
                }else {
                    inventory.setStorage(location);
                    inventory.setQuantity(quantity);
                    inventory.setUpdatedDate(LocalDate.now());
                    updateStockHistory(inventory, images,remarks,deviceName,empId);
                    inventoryRepo.save(inventory);
                }
           }catch (Exception e){
               e.printStackTrace();
           }
        });

    }

    private String getDefaultDeviceName(String deviceName) {
        return StringUtils.isNotBlank(deviceName) ? deviceName : "NA";
    }

    private void updateStockHistory(Inventory inventory, List<MultipartFile> images, String remarks, String deviceName, String empId) throws IOException {
        List<String> imageIds = new ArrayList<>();
        if(images  != null) {
            for (MultipartFile image : images) {
                // Save or process each image as needed
                String fileId = imageDBService.saveImages(image.getInputStream(), image.getOriginalFilename());
                imageIds.add(fileId);
            }
        }
        StockHistory stockHistory = new StockHistory(inventory.getQuantity(),inventory.getShopifyQuantity(), inventory.getStorage(),imageIds, LocalDateTime.now());
        stockHistory.setDeviceName(getDefaultDeviceName(deviceName));
        stockHistory.setEmpId(empId);
        stockHistory.setRemarks(remarks == null ? ShopifyReportConstants.EMPTY : remarks);
        List<StockHistory> stockHistories = inventory.getStockHistory();
        stockHistories.add(stockHistory);

    }

    private List<Inventory> getInventoryByProductId(String productId) {

        Optional<List<Inventory>> inventories = inventoryRepo.findAllByProductId(productId);
        return inventories.orElseGet(ArrayList::new);
    }

    @Cacheable(value = "inventoryList", key = "#query")
    public List<Inventory> fetchProductsUsingId(String query) {
        List<Inventory> inventoryList = new ArrayList<>();
        Optional<List<Inventory>> inventories =  inventoryRepo.findAllByProductId(String.valueOf(query));
//        if(inventories.isPresent() && !inventories.get().isEmpty()){
//            inventories.get().forEach(inventory -> shopifyServiceImpl.updateShopifyDetails(inventory));
//            inventories.get().forEach(this::updateInventoryDetailsFromShopify);
//            inventoryList.addAll(inventories.get());
//        }else {
            List<Inventory> shopifyInventory = shopifyServiceImpl.fetchProductFromShopify(query);
            if (shopifyInventory == null || shopifyInventory.isEmpty()) {
                return inventoryList;
            } else {
                shopifyInventory.forEach(this::saveInventory);
                inventoryList = shopifyInventory;
            }
        //}
         return inventoryList;

    }

    public Optional<List<Inventory>> findMismatchedStock() {
        return inventoryRepo.findByMismatchedQty();
    }

    public Page<Inventory> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return inventoryRepo.findAll(pageable);
    }

    public void resetProduct(String productId) {
         inventoryRepo.deleteByProductid(productId);
    }

    public Optional<List<Inventory>> findReportsNoStorage() {
       return  inventoryRepo.findByNoStorage();
    }
}

