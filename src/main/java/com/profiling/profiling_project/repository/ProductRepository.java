package com.profiling.profiling_project.repository;

import com.profiling.profiling_project.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository  extends MongoRepository<Product, String> {

}
