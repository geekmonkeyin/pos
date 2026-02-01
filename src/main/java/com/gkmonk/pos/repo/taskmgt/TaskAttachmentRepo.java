package com.gkmonk.pos.repo.taskmgt;

import com.gkmonk.pos.model.taskmgt.Attachment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TaskAttachmentRepo extends MongoRepository<Attachment, String> {
    List<Attachment> findByTaskIdOrderByUploadedAtDesc(String taskId);
    void deleteByTaskId(String taskId);
}
