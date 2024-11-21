package com.profiling.profiling_project.controller;

import com.profiling.profiling_project.model.AuthRequest;
import com.profiling.profiling_project.model.AuthResponse;
import com.profiling.profiling_project.model.User;
import com.profiling.profiling_project.repository.UserRepository;
import com.profiling.profiling_project.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        logger.info("Enregistrement d'un nouvel utilisateur avec l'email : {}", user.getEmail());

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            logger.warn("Tentative d'enregistrement pour un email existant : {}", user.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("L'utilisateur existe déjà.");
        }

        // Hacher le mot de passe avant de l'enregistrer
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);

        userRepository.save(user); // Enregistrer l'utilisateur dans la base
        logger.info("Utilisateur enregistré avec succès : {}", user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body("Utilisateur créé avec succès.");
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest loginRequest) {
        logger.info("Tentative de connexion pour l'email : {}", loginRequest.getEmail());
        // Chercher l'utilisateur par son email
        Optional<User> user = userRepository.findByEmail(loginRequest.getEmail());

        if (user.isEmpty()) {
            logger.warn("Utilisateur non trouvé pour l'email : {}", loginRequest.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Utilisateur non trouvé.");
        }

        // Vérifier le mot de passe
        if (!BCrypt.checkpw(loginRequest.getPassword(), user.get().getPassword())) {
            logger.warn("Mot de passe incorrect pour l'email : {}", loginRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mot de passe incorrect.");
        }

        // Si tout est bon, générer un token JWT
        String token = jwtUtil.generateToken(user.get().getEmail());
        logger.info("Utilisateur authentifié avec succès : {}", loginRequest.getEmail());
        return ResponseEntity.ok(new AuthResponse(token)); // Renvoie le token

    }
}
