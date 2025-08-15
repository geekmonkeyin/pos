package com.gkmonk.pos.services;

import com.gkmonk.pos.model.GSTRate;
import com.gkmonk.pos.model.ReportDetails;
import com.gkmonk.pos.repo.GSTRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GSTServiceImpl {

    @Autowired
    private GSTRepo gstRepo;

    public void updateHSNCode(List<ReportDetails> reportDetails){
       List<GSTRate> gstRateList =  gstRepo.findAll();
        for (ReportDetails reportDetail : reportDetails) {
            for (GSTRate gstRate : gstRateList) {
                if (reportDetail.getProductType().equalsIgnoreCase(gstRate.getCategory())) {
                    reportDetail.setGstRate(gstRate.getRate());
                    reportDetail.setHsnCode(gstRate.getHsn());
                }
            }
        }
    }


    // Example method to calculate GST
    public double calculateGST(double amount, double gstRate) {
        return (amount * gstRate) / 100;
    }

    // Example method to validate GST number
    public boolean validateGSTNumber(String gstNumber) {
        // Implement GST number validation logic here
        return true; // Placeholder return value
    }
}
