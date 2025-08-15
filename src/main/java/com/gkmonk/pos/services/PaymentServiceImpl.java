package com.gkmonk.pos.services;

import com.gkmonk.pos.model.PaymentReceipts;
import com.gkmonk.pos.repo.PaymentRepo;
import com.gkmonk.pos.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

@Service
public class PaymentServiceImpl {

    @Autowired
    private PaymentRepo paymentRepo;

    public PaymentReceipts saveReceipt(PaymentReceipts paymentReceipts){
        return paymentRepo.save(paymentReceipts);
    }

    public List<PaymentReceipts> findAllReceipts() {
        SortOperation sortByPopDesc = sort(Sort.by(Sort.Direction.DESC, "date")).
                and(Sort.by(Sort.Direction.DESC, "vendor"))
                .and(Sort.by(Sort.Direction.DESC, "amount"));
        return paymentRepo.findAll(sortByPopDesc);
    }


    public List<PaymentReceipts> findFilteredReceipts(String vendorName,String paymentTo, Double minAmount, Double maxAmount, String startDate, String endDate) {
        return StringUtils.isNotBlank(vendorName) ? paymentRepo.findFilteredReceipts(vendorName,paymentTo, minAmount, maxAmount, startDate, endDate) :
                paymentRepo.findFilteredReceipts(minAmount, maxAmount, startDate, endDate);
    }

}
