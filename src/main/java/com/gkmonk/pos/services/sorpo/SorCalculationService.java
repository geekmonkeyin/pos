package com.gkmonk.pos.services.sorpo;

import com.gkmonk.pos.model.sorpo.PurchaseOrder;
import com.gkmonk.pos.model.sorpo.Store;
import com.gkmonk.pos.model.sorpo.StoreSalesReport;
import com.gkmonk.pos.model.sorpo.StoreSalesReportItem;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SorCalculationService {

    public PurchaseOrder calculate(StoreSalesReport report, Store store) {
        double grossSales = report.getItems().stream()
                .mapToDouble(StoreSalesReportItem::getGrossSaleValue)
                .sum();

        double outputGst = grossSales * (store.getOutputGstRate() / 100.0);
        double takeRateValue = grossSales * (store.getTakeRate() / 100.0);

        double basicValue = grossSales - outputGst - takeRateValue;
        double settlementGst = basicValue * (store.getSettlementGstRate() / 100.0);
        double finalValue = basicValue + settlementGst;

        PurchaseOrder po = new PurchaseOrder();
        po.setStoreId(store.getId());
        po.setCycleMonth(report.getCycleMonth());
        po.setGrossSales(grossSales);
        po.setOutputGst(outputGst);
        po.setTakeRateValue(takeRateValue);
        po.setBasicValue(basicValue);
        po.setSettlementGst(settlementGst);
        po.setFinalValue(finalValue);
        po.setStatus("DRAFT");
        po.setCreatedDate(LocalDate.now());

        return po;
    }
}

