package com.gkmonk.pos.services.purchaseorder;

import com.gkmonk.pos.model.purchase.PurchaseOrder;
import com.gkmonk.pos.repo.purchaseorder.PurchaseOrderRepo;
import com.gkmonk.pos.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PurchaseOrderServiceImp {

    @Autowired
    private PurchaseOrderRepo purchaseOrderRepo;

    public PurchaseOrder save(PurchaseOrder purchaseOrder) {
        return purchaseOrderRepo.save(purchaseOrder);
    }

    public Optional<List<PurchaseOrder>> findByCriteria(String vendorName, String startDate, String endDate) {
        return StringUtils.isNotBlank(vendorName) ? purchaseOrderRepo.findFilteredReceipts(vendorName,startDate, endDate) :
                purchaseOrderRepo.findFilteredReceipts(startDate, endDate);
    }

    public Optional<List<PurchaseOrder>> findAll(String vendorName) {
        return purchaseOrderRepo.findByVendorName(vendorName);
    }
}
