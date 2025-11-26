package com.gkmonk.pos.controller.sorpo;

import com.gkmonk.pos.model.sorpo.PurchaseOrder;
import com.gkmonk.pos.model.sorpo.Store;
import com.gkmonk.pos.model.sorpo.StoreSalesReport;
import com.gkmonk.pos.repo.sorpo.PurchaseOrderRepository;
import com.gkmonk.pos.repo.sorpo.SalesReportRepository;
import com.gkmonk.pos.repo.sorpo.StoreRepository;
import com.gkmonk.pos.services.sorpo.SorCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/po")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final SalesReportRepository salesReportRepository;
    private final StoreRepository storeRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SorCalculationService sorCalculationService;

    @PostMapping("/generate/{storeId}/{cycleMonth}")
    public PurchaseOrder generatePo(@PathVariable String storeId,
                                    @PathVariable String cycleMonth) {

        StoreSalesReport report = salesReportRepository
                .findByStoreIdAndCycleMonth(storeId, cycleMonth)
                .orElseThrow(() -> new RuntimeException("Sales report not found"));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));

        PurchaseOrder po = sorCalculationService.calculate(report, store);

        // simple PO number
        po.setPoNo("GM-SOR-" + storeId + "-" + cycleMonth);
        return purchaseOrderRepository.save(po);
    }

    @GetMapping("/{poId}")
    public PurchaseOrder getPo(@PathVariable String poId) {
        return purchaseOrderRepository.findById(poId).orElseThrow();
    }
}