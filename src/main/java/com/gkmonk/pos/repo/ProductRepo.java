package com.gkmonk.pos.repo;

import com.gkmonk.pos.model.Product;
import org.springframework.data.repository.Repository;

public interface ProductRepo extends Repository<Product, Long> {
    Product save(Product product);
    Product findById(Long id);
    void delete(Product product);
    void deleteById(Long id);
    Iterable<Product> findAll();
    long count();
    boolean existsById(Long id);

}
