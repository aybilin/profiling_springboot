package com.profiling.profiling_project.controller;

import com.profiling.profiling_project.model.Product;
import com.profiling.profiling_project.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/products/{id}")
    public Product getProductById(@PathVariable String id) {
        return productService.getProductById(id);
    }

    @PostMapping("/products")
    public ResponseEntity<?> addProduct(@RequestParam("name") String name,
                                        @RequestParam("price") double price,
                                        @RequestParam("expirationDate") String expirationDate,
                                        @RequestParam("image") MultipartFile image) throws IOException {

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setExpirationDate(LocalDate.parse(expirationDate));
        product.setImage(image.getBytes());  // Convertir l'image en tableau de bytes

        productService.addProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body("Product created successfully with image.");
    }
    @PutMapping(value = "/products/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProductWithImage(
            @PathVariable String id,
            @RequestParam("name") String name,
            @RequestParam("price") double price,
            @RequestParam("expirationDate") String expirationDate,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        Product product = productService.getProductById(id);
        product.setName(name);
        product.setPrice(price);
        product.setExpirationDate(LocalDate.parse(expirationDate));

        if (image != null && !image.isEmpty()) {
            product.setImage(image.getBytes());
        }

        productService.updateProduct(id, product);
        return ResponseEntity.ok("Product updated successfully!");
    }


    @DeleteMapping("/products/{id}")
    public void deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
    }

    // Endpoint pour récupérer l'image d'un produit
    @GetMapping("/products/{id}/image")
    public ResponseEntity<byte[]> getProductImage(@PathVariable String id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(product.getImage());
    }
}
