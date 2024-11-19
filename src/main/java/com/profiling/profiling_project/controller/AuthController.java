package com.profiling.profiling_project.controller;

import com.profiling.profiling_project.model.AuthRequest;
import com.profiling.profiling_project.model.AuthResponse;
import com.profiling.profiling_project.model.User;
import com.profiling.profiling_project.repository.UserRepository;
import com.profiling.profiling_project.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("L'utilisateur existe déjà.");
        }

        // Hacher le mot de passe avant de l'enregistrer
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);

        userRepository.save(user); // Enregistrer l'utilisateur dans la base
        return ResponseEntity.status(HttpStatus.CREATED).body("Utilisateur créé avec succès.");
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest loginRequest) {
        // Chercher l'utilisateur par son email
        Optional<User> user = userRepository.findByEmail(loginRequest.getEmail());

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Utilisateur non trouvé.");
        }

        // Vérifier le mot de passe
        if (!BCrypt.checkpw(loginRequest.getPassword(), user.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mot de passe incorrect.");
        }
        System.out.println("good untill here");

        // Si tout est bon, générer un token JWT
        String token = jwtUtil.generateToken(user.get().getEmail());
        return ResponseEntity.ok(new AuthResponse(token)); // Renvoie le token

    }
}
