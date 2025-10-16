package com.gkmonk.pos.repo;

import com.gkmonk.pos.model.PaymentReceipts;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentRepo extends MongoRepository<PaymentReceipts,Long> {

    @Query(value = "{$and:[ {'vendorName' : ?0}, {'paymentTo':{$regex:?1,$options:'i'}},{'amount' : { $gte: ?2, $lte: ?3 }}, {'date' : { $gte: ?4, $lte: ?5 }}] }",sort = "{date:-1,vendor:-1,amount:-1}")
    List<PaymentReceipts> findFilteredReceipts(String vendorName,String paymentTo, Double minAmount, Double maxAmount, String startDate, String endDate);

    @Query(value = "{$and:[ {'amount' : { $gte: ?0, $lte: ?1 }}, {'date' : { $gte: ?2, $lte: ?3 }}] }",sort = "{date:-1,vendor:-1,amount:-1}")
    List<PaymentReceipts> findFilteredReceipts( Double minAmount, Double maxAmount, String startDate, String endDate);

    @Query(value = "{}")
    List<PaymentReceipts> findAll(SortOperation sortByPopDesc);

    @Query(value = "{vendorName : ?0}",sort = "{date:-1,vendor:-1,amount:-1}")
    Optional<List<PaymentReceipts>> findByVendorName(String vendorName);
}
