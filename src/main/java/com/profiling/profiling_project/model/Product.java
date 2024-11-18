package com.profiling.profiling_project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

import java.time.LocalDate;

@Data
@Document(collection = "products")
public class Product {
    @Id
    private String id;
    private String name;
    private double price;
    private LocalDate expirationDate;
}
