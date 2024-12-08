package com.profiling.profiling_project.controller;

import com.profiling.profiling_project.util.LogAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/user-profiles")
public class UserProfileController {

    @Autowired
    private LogAnalyzer logAnalyzer;

    @PostMapping("/analyze")
    public ResponseEntity<String> analyzeAndSaveProfiles() {
        try {
            logAnalyzer.analyzeAndSaveProfiles();
            return ResponseEntity.ok("Profils analysés et sauvegardés avec succès.");
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Erreur lors de l'analyse des logs : " + e.getMessage());
        }
    }
}
