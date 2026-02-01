package com.gkmonk.pos.repo.taskmgt;

import com.gkmonk.pos.model.taskmgt.TeamType;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TeamTypeRepo extends MongoRepository<TeamType, String> {}