package com.gkmonk.pos.controller.courier;

import com.gkmonk.pos.model.courier.CourierQuotationRequest;
import com.gkmonk.pos.model.order.CourierOption;
import com.gkmonk.pos.services.courier.IOrdersSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/v1/courier")
public class CourierController {

    @Autowired
    private List<IOrdersSyncService> courierServices;

    @PostMapping("/quotes")
    public ResponseEntity<List<CourierOption>> getQuotes(@RequestBody CourierQuotationRequest courierQuotationRequest) {
        // Dummy data for demonstration purposes
        for(IOrdersSyncService courier : courierServices){
            try {
                if(courier.isActive()) {
                    List<CourierOption> courierOptions = courier.getQuote(courierQuotationRequest);

                    return ResponseEntity.ok(courierOptions);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        CourierOption bluedart = new CourierOption();
        bluedart.setName("BlueDart");
        bluedart.setCost(42);
        bluedart.setEta("2-3 days");
        bluedart.setAggregator("shipmozo");

        List<CourierOption> courierOptions = List.of(
                bluedart
        );
        return ResponseEntity.ok(courierOptions);
    }

    @GetMapping("/convertToMozo")
    public ModelAndView convertToMozo(){
        return new ModelAndView("shopifytomozo");
    }
}
