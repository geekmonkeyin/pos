package com.gkmonk.pos.utils;

import com.gkmonk.pos.model.ReportDetails;

public class GSTUtils {

    public static void updateGST(ReportDetails reportDetails){
        if (isOrderFromUK(reportDetails.getStateCode())) {
            reportDetails.setCgstPerc((reportDetails.getGstRate()/2));
            reportDetails.setSgstPerc((reportDetails.getGstRate()/2));
        } else {
            reportDetails.setIgstPerc(reportDetails.getGstRate());
        }
        updateGSTAmounts(reportDetails);
    }

    private static void updateGSTAmounts(ReportDetails reportDetails) {
            double totalAmount = reportDetails.getTotalSales();

            double igstAmount = totalAmount * reportDetails.getIgstPerc()/100;
            double cgstAmount = totalAmount * reportDetails.getCgstPerc()/100;
            double sgstAmount = totalAmount * reportDetails.getSgstPerc()/100;
            double totalTax = igstAmount + cgstAmount + sgstAmount;
            double netSales = totalAmount - totalTax;
            reportDetails.setTaxes(totalTax);
            reportDetails.setIgstAmount(igstAmount);
            reportDetails.setSgstAmount(sgstAmount);
            reportDetails.setCgstAmount(cgstAmount);
            reportDetails.setNetSales(netSales);
    }

    public static boolean isOrderFromUK(String stateCode) {
        return "UK".equalsIgnoreCase(stateCode);
    }

}
