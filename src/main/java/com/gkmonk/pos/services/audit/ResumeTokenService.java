package com.gkmonk.pos.services.audit;

import com.gkmonk.pos.model.audit.ChangeStreamOffset;
import com.gkmonk.pos.repo.audit.ChangeStreamOffsetRepo;
import lombok.RequiredArgsConstructor;
import org.bson.BsonDocument;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResumeTokenService {
    private final ChangeStreamOffsetRepo repo;

    public BsonDocument load(String streamId) {
        return repo.findById(streamId).map(ChangeStreamOffset::getResumeToken).orElse(null);
    }

    public void save(String streamId, BsonDocument token) {
        if (token != null) repo.save(new ChangeStreamOffset(streamId, token));
    }
}

