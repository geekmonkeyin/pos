package com.gkmonk.pos.repo.taskmgt;

import com.gkmonk.pos.model.taskmgt.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByApprovedFalse();
}
