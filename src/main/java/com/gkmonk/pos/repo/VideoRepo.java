package com.gkmonk.pos.repo;

import com.gkmonk.pos.model.VideoRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepo extends MongoRepository<VideoRequest,String> {


}
