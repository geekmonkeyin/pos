package com.gkmonk.pos.services.sorpo;

import com.gkmonk.pos.model.sorpo.Store;
import com.gkmonk.pos.repo.sorpo.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

@Service
public class StoreServiceImpl {

    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private GridFsTemplate gridFsTemplate;

    public void addNewStore(Store store){
        storeRepository.save(store);
    }


}
