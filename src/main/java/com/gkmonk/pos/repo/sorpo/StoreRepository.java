package com.gkmonk.pos.repo.sorpo;

import com.gkmonk.pos.model.sorpo.Store;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends MongoRepository<Store, String> {}
