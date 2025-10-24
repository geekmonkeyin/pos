package com.gkmonk.pos.repo.counter;

import com.gkmonk.pos.model.counter.ShopifyCursor;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShopifyCursorRepo extends MongoRepository<ShopifyCursor, String> {
}
