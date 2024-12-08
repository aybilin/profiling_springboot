package com.profiling.profiling_project.controller;

import com.profiling.profiling_project.model.Product;
import com.profiling.profiling_project.util.LogAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @org.springframework.beans.factory.annotation.Autowired
    private com.profiling.profiling_project.service.ProductService productService;
    @Autowired
    private LogAnalyzer logAnalyzer;

    @GetMapping()
    public java.util.List<com.profiling.profiling_project.model.Product> getAllProducts() {
        logger.info("Utilisateur: " + SecurityContextHolder.getContext().getAuthentication().getName() + ", Méthode appelée: getAllProducts");
        try {
            logAnalyzer.analyzeAndSaveProfiles();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public com.profiling.profiling_project.model.Product getProductById(@PathVariable
    String id) {
        logger.info("Utilisateur: " + SecurityContextHolder.getContext().getAuthentication().getName() + ", Méthode appelée: getProductById");
        try {
            logAnalyzer.analyzeAndSaveProfiles();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return productService.getProductById(id);
    }
    @GetMapping("/most-expensive")
    public java.util.List<com.profiling.profiling_project.model.Product> getMostExpensiveProducts() {
        // Log de l'utilisateur authentifié et de l'action
        logger.info("Utilisateur: " + SecurityContextHolder.getContext().getAuthentication().getName() + ", Méthode appelée: getMostExpensiveProducts");

        try {
            logAnalyzer.analyzeAndSaveProfiles();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Retourne les produits les plus chers
        return productService.getMostExpensiveProducts();
    }


    @PostMapping()
    public org.springframework.http.ResponseEntity<?> addProduct(@RequestParam("name")
    String name, @RequestParam("price")
    double price, @RequestParam("expirationDate")
    String expirationDate, @RequestParam("image")
    org.springframework.web.multipart.MultipartFile image) throws java.io.IOException {
        logger.info("Utilisateur: " + SecurityContextHolder.getContext().getAuthentication().getName() + ", Méthode appelée: addProduct");
        com.profiling.profiling_project.model.Product product = new com.profiling.profiling_project.model.Product();
        product.setName(name);
        product.setPrice(price);
        product.setExpirationDate(java.time.LocalDate.parse(expirationDate));
        product.setImage(image.getBytes());// Convertir l'image en tableau de bytes

        productService.addProduct(product);
        try {
            logAnalyzer.analyzeAndSaveProfiles();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body("Product created successfully with image.");
    }
    @PutMapping(value = "/products/{id}", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public org.springframework.http.ResponseEntity<?> updateProductWithImage(@PathVariable
                                                                             String id, @RequestParam("name")
                                                                             String name, @RequestParam("price")
                                                                             double price, @RequestParam("expirationDate")
                                                                             String expirationDate, @RequestParam(value = "image", required = false)
                                                                             org.springframework.web.multipart.MultipartFile image) throws java.io.IOException {
        logger.info("Utilisateur: " + SecurityContextHolder.getContext().getAuthentication().getName() + ", Méthode appelée: updateProductWithImage");
        com.profiling.profiling_project.model.Product product = productService.getProductById(id);
        product.setName(name);
        product.setPrice(price);
        product.setExpirationDate(java.time.LocalDate.parse(expirationDate));
        if ((image != null) && (!image.isEmpty())) {
            product.setImage(image.getBytes());
        }
        productService.updateProduct(id, product);
        try {
            logAnalyzer.analyzeAndSaveProfiles();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return org.springframework.http.ResponseEntity.ok("Product updated successfully!");
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable
    String id) {
        logger.info("Utilisateur: " + SecurityContextHolder.getContext().getAuthentication().getName() + ", Méthode appelée: deleteProduct");
        try {
            logAnalyzer.analyzeAndSaveProfiles();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        productService.deleteProduct(id);
    }

    // Endpoint pour récupérer l'image d'un produit
    @GetMapping("/{id}/image")
    public org.springframework.http.ResponseEntity<byte[]> getProductImage(@PathVariable
    String id) {
        logger.info("Utilisateur: " + SecurityContextHolder.getContext().getAuthentication().getName() + ", Méthode appelée: getProductImage");
        com.profiling.profiling_project.model.Product product = productService.getProductById(id);
        try {
            logAnalyzer.analyzeAndSaveProfiles();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return org.springframework.http.ResponseEntity.ok().contentType(org.springframework.http.MediaType.IMAGE_JPEG).body(product.getImage());
    }
}
