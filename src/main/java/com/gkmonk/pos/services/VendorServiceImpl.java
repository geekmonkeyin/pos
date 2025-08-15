package com.gkmonk.pos.services;

import com.gkmonk.pos.model.Vendor;
import com.gkmonk.pos.repo.VendorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VendorServiceImpl {

    @Autowired
    private VendorRepo vendorRepo;

    public void saveVendor(Vendor vendor) {
        vendorRepo.save(vendor);
    }

    public List<Vendor> searchByName(String name){
        return vendorRepo.findByNameContainingIgnoreCase(name);
    }
}
