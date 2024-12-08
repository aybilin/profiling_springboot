package com.profiling.profiling_project.service;

import com.profiling.profiling_project.model.Product;
import com.profiling.profiling_project.repository.ProductRepository;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    private final Tracer tracer;

    public ProductService(Tracer tracer) {
        this.tracer = tracer;
    }

    public List<Product> getAllProducts() {

        Span span = tracer.spanBuilder("getAllProducts").startSpan();

        try {
            return productRepository.findAll();

        }catch (Exception e) {
            span.recordException(e); // Enregistrer les erreurs dans la trace
            throw e;
        }finally {
            span.end(); // Terminer la trace
        }
    }

    public Product getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product with ID " + id + " not found"));
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product with ID " + id + " not found");
        }
        productRepository.deleteById(id);
    }

    public Product updateProduct(String id, Product updatedProduct) {
        Product existingProduct = getProductById(id);
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setExpirationDate(updatedProduct.getExpirationDate());
        existingProduct.setImage(updatedProduct.getImage()); // Mise à jour de l'image
        return productRepository.save(existingProduct);
    }
}
