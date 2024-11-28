package com.profiling.profiling_project.controller;

import com.profiling.profiling_project.util.LogAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @org.springframework.beans.factory.annotation.Autowired
    private com.profiling.profiling_project.repository.UserRepository userRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private com.profiling.profiling_project.security.JwtUtil jwtUtil;



    @PostMapping("/auth/register")
    public org.springframework.http.ResponseEntity<?> register(@RequestBody
    com.profiling.profiling_project.model.User user) {
        logger.info("Utilisateur: " + SecurityContextHolder.getContext().getAuthentication().getName() + ", Méthode appelée: register");
        // logger.info("Enregistrement d'un nouvel utilisateur avec l'email : {}", user.getEmail());
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            // logger.warn("Tentative d'enregistrement pour un email existant : {}", user.getEmail());
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST).body("L'utilisateur existe déjà.");
        }
        // Hacher le mot de passe avant de l'enregistrer
        String hashedPassword = org.springframework.security.crypto.bcrypt.BCrypt.hashpw(user.getPassword(), org.springframework.security.crypto.bcrypt.BCrypt.gensalt());
        user.setPassword(hashedPassword);
        userRepository.save(user);// Enregistrer l'utilisateur dans la base

        // logger.info("Utilisateur enregistré avec succès : {}", user.getEmail());
        return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body("Utilisateur créé avec succès.");
    }

    @PostMapping("/auth/login")
    public org.springframework.http.ResponseEntity<?> login(@RequestBody
    com.profiling.profiling_project.model.AuthRequest loginRequest) {
        logger.info("Utilisateur: " + SecurityContextHolder.getContext().getAuthentication().getName() + ", Méthode appelée: login");
        // logger.info("Tentative de connexion pour l'email : {}", loginRequest.getEmail());
        // Chercher l'utilisateur par son email
        java.util.Optional<com.profiling.profiling_project.model.User> user = userRepository.findByEmail(loginRequest.getEmail());
        if (user.isEmpty()) {
            // logger.warn("Utilisateur non trouvé pour l'email : {}", loginRequest.getEmail());
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST).body("Utilisateur non trouvé.");
        }
        // Vérifier le mot de passe
        if (!org.springframework.security.crypto.bcrypt.BCrypt.checkpw(loginRequest.getPassword(), user.get().getPassword())) {
            // logger.warn("Mot de passe incorrect pour l'email : {}", loginRequest.getEmail());
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).body("Mot de passe incorrect.");
        }
        // Si tout est bon, générer un token JWT
        String token = jwtUtil.generateToken(user.get().getEmail());
        // logger.info("Utilisateur authentifié avec succès : {}", loginRequest.getEmail());
        return org.springframework.http.ResponseEntity.ok(new com.profiling.profiling_project.model.AuthResponse(token));// Renvoie le token

    }
}