package com.gkmonk.pos.repo.audit;

import com.gkmonk.pos.model.audit.AuditEvent;
import com.gkmonk.pos.model.audit.ChangeStreamOffset;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditEventRepo extends MongoRepository<AuditEvent, String> {

}
