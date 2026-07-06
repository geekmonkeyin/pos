package com.gkmonk.pos.repo.zoho;

import com.gkmonk.pos.model.zoho.ZohoItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ZohoRepo extends MongoRepository<ZohoItem, String> {

}
