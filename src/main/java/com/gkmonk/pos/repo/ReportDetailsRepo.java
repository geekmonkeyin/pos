package com.gkmonk.pos.repo;

import com.gkmonk.pos.model.ReportDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportDetailsRepo extends MongoRepository<ReportDetails, Long> {

    @Query(value = "{'day': { $gte: ?0, $lte: ?1 }}",sort = "{'day':1}")
    Optional<List<ReportDetails>> findByDate(String startDate,String endDate);
}
