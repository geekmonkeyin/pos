package com.gkmonk.pos.services.zoho;

import com.gkmonk.pos.model.zoho.ZohoItem;
import com.gkmonk.pos.repo.zoho.ZohoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZohoServiceImpl {

    @Autowired
    private ZohoRepo zohoRepo;
    private List<ZohoItem> zohoItems;

    public List<ZohoItem> findAll() {
       if(zohoItems == null || zohoItems.isEmpty()) {
           zohoItems = zohoRepo.findAll();
       }
        return zohoItems;
    }
}
