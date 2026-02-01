package com.gkmonk.pos.repo.taskmgt;

import com.gkmonk.pos.model.taskmgt.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TaskRepo extends MongoRepository<Task, String> {
    List<Task> findAllByOrderByCreatedAtDesc();
    List<Task> findByStatusOrderByCreatedAtDesc(String status);
    List<Task> findByAssignedToOrderByCreatedAtDesc(String assignedTo);
    List<Task> findByStatusAndAssignedToOrderByCreatedAtDesc(String status, String assignedTo);
}
