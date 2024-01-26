package com.programmingtechie.productservice.Repository;

import com.programmingtechie.productservice.Entities.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
