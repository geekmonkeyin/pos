package com.gkmonk.pos.services.outbound;

import com.gkmonk.pos.model.outbound.OutboundOrder;
import com.gkmonk.pos.repo.OutboundRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OutboundServiceImpl {

    @Autowired
    private OutboundRepo outboundRepo;

    public void saveManifest(List<OutboundOrder> outboundOrder){
        outboundRepo.saveAll(outboundOrder);
    }

    public OutboundOrder findByAWB(String awb) {
        Optional<OutboundOrder> order = outboundRepo.findByAWB(awb);
        return order.orElse(null);
    }
}
