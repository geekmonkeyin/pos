package com.gkmonk.pos.services;

import com.gkmonk.pos.model.CartonRequest;
import com.gkmonk.pos.model.Counters;
import com.gkmonk.pos.model.InboundData;
import com.gkmonk.pos.model.MaxCartonProjection;
import com.gkmonk.pos.model.VideoRequest;
import com.gkmonk.pos.repo.InboundRepo;
import com.gkmonk.pos.repo.ProductImagesRepository;
import com.gkmonk.pos.repo.VideoRepo;
import com.gkmonk.pos.utils.InboundStatus;
import com.gkmonk.pos.utils.POSConstants;
import com.mongodb.client.result.UpdateResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InboundServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(InboundServiceImpl.class);
    @Autowired
    private InboundRepo inboundRepo;
    @Autowired
    private MongoOperations mongoOperations;
    @Autowired
    private VideoRepo videoRepo;
    @Autowired
    private ProductImagesRepository cartonRepo;



    public InboundData saveInboundData(InboundData data) {
        return inboundRepo.save(data);
    }

    public long generateSequence(String seqName) {
        Query query = new Query(Criteria.where("_id").is(seqName));
        Update update = new Update().inc("seq", 1);
        Counters counter = mongoOperations.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                Counters.class
        );
        return counter != null ? counter.getSeq() : 1;
    }


    public void saveVideoData(VideoRequest videoRequest) {
        VideoRequest request = videoRepo.save(videoRequest);
        System.out.println("Video:" + request.getInboundId() + " , carton no:" + request.getCartonNumber());

    }

    public InboundData getDraftInboundOrders(String vendorName) {
        Optional<List<InboundData>> inboundData = inboundRepo.findByVendorNameAndStatus(vendorName, InboundStatus.DRAFT.name());
        return inboundData.map(data -> data.get(0)).orElse(null);
    }

    public InboundData getInboundDataById(long id) {
        Optional<InboundData> response = inboundRepo.findById(id);
        return response.orElse(null);
    }

    public void saveImageData(CartonRequest productImages) {
        productImages.setUniqueId(productImages.getInboundId() + "_" + productImages.getCarton() + "_" + productImages.getProductId());
        cartonRepo.save(productImages);
    }

    public void saveCartons(String inboundId, int cartonNo, JSONArray products) {
        List<CartonRequest> cartons = convertJsonToCartonRequest(products, inboundId, cartonNo);
        if(cartons == null || cartons.isEmpty()) {
            log.warn("No products found for inboundId: " + inboundId + " and cartonNo: " + cartonNo);
            return;
        }
        for (CartonRequest carton : cartons) {
            log.info("Saving Carton: " + carton.getUniqueId());
            updateProductsInCartons(carton);
        }

        log.info("Size:"+cartons.size());
    }

    private void updateProductsInCartons(CartonRequest carton) {
        Query query = new Query(Criteria.where("_id").is(carton.getUniqueId()));
        Update update = new Update();
        update.set("productName", carton.getProductName());
        update.set("quantity", carton.getQuantity());
        update.set("productCost", carton.getProductCost());
        UpdateResult result = mongoOperations.updateFirst(query, update, CartonRequest.class);
        if(result.getMatchedCount() == 0){
            log.info("No existing carton found, saving new carton: " + carton.getUniqueId());
            cartonRepo.save(carton);
        } else {
            log.info("Updated existing carton: " + carton.getUniqueId());
        }

    }

    private List<CartonRequest> convertJsonToCartonRequest(JSONArray products, String inboundId, int cartonNo) {
        List<CartonRequest> cartonRequests = new ArrayList<>();
        if (products == null || products.length() == 0) {
            return cartonRequests;
        }
        for (int i = 0; i < products.length(); i++) {
            try {

                CartonRequest cartonRequest = new CartonRequest();
                cartonRequest.setInboundId(inboundId);
                cartonRequest.setCarton(cartonNo);
                cartonRequest.setProductName(products.getJSONObject(i).getString("productName"));
                cartonRequest.setProductId(products.getJSONObject(i).getString("productId"));

                cartonRequest.setQuantity(products.getJSONObject(i).getInt("quantity"));
                cartonRequest.setUniqueId(inboundId + "_" + cartonNo + "_" + products.getJSONObject(i).getString("productId"));
                cartonRequests.add(cartonRequest);
            } catch (JSONException e) {
                log.error(e.getLocalizedMessage());
            }
        }
        return cartonRequests;

    }

    public Integer fetchCurrentStep(long id) {
        Optional<MaxCartonProjection> maxCarton = cartonRepo.findByInboundId(""+id);
        return maxCarton.map(maxCartonProjection -> maxCartonProjection.getMaxCounterNo() + 1).orElseGet(() -> POSConstants.DEFAULT_STEP);
    }

    public List<CartonRequest> getCartonRequests(String inboundId) {
         Optional<List<CartonRequest>> cartonRequestList =  cartonRepo.findCartonsByInbound(inboundId);
        return cartonRequestList.orElseGet(ArrayList::new);
    }

    public List<CartonRequest> getCartonRequestsByStep(String inboundId,String step) {
        Optional<List<CartonRequest>> cartonRequestList =  cartonRepo.findCartonsByInboundAndStep(inboundId,Long.valueOf(step));
        return cartonRequestList.orElseGet(ArrayList::new);
    }

    public Optional<List<InboundData>> getInboundDetailsByVendorName(String vendorName) {
        return inboundRepo.findByVendorName(vendorName);
    }

   public boolean updatePurchaseAmount(Long inboundId, Double purchaseAmount) {
       Query query = new Query(Criteria.where("id").is(inboundId));
       Update update = new Update().set("totalPurchaseAmount", purchaseAmount);
       // Perform the update operation
       UpdateResult result = mongoOperations.updateFirst(query, update, InboundData.class);
       // Check if any document was modified
       return result.getModifiedCount() > 0;
   }
}
