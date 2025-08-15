package com.gkmonk.pos.repo.logs;

import com.gkmonk.pos.model.logs.TaskLogs;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TaskLogRepo extends MongoRepository<TaskLogs,Long> {

    @Query("{}")
    Optional<List<TaskLogs>> findAllLast30();

}
