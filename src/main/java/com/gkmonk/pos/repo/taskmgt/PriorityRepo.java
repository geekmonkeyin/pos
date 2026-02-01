package com.gkmonk.pos.repo.taskmgt;

import com.gkmonk.pos.model.taskmgt.Priority;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PriorityRepo extends MongoRepository<Priority, String> {
    List<Priority> findAllByOrderByOrderAsc();
}
