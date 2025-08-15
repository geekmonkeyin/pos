package com.gkmonk.pos.services;

import com.gkmonk.pos.model.pod.PackedOrder;
import com.gkmonk.pos.repo.orders.OrderReportRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderReportsServiceImpl {
    @Autowired
    private OrderReportRepo orderReportRepo;

    public void saveReport(PackedOrder orderReport) {
        orderReportRepo.save(orderReport);
    }

    public Optional<List<PackedOrder>> findByAWB(String awb) {
        return orderReportRepo.findByAWB(awb);
    }
}
