package com.gkmonk.pos.services.sorpo;

import com.gkmonk.pos.model.sorpo.Outward;
import com.gkmonk.pos.repo.sorpo.OutwardRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OutwardServiceImpl {

    @Autowired
    private OutwardRepo outwardRepo;

    public Outward saveOutward(Outward outward) {
        return outwardRepo.save(outward);
    }

    public Outward findById(String id) {
        return outwardRepo.findByOutwardId(id).orElse(null);
    }
}
