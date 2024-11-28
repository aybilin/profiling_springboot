package com.profiling.profiling_project.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.profiling.profiling_project.LPS.Action;
import com.profiling.profiling_project.LPS.Event;
import com.profiling.profiling_project.LPS.LPS;
import com.profiling.profiling_project.LPS.User;
import com.profiling.profiling_project.model.UserProfile;
import com.profiling.profiling_project.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LogAnalyzer {

    private static final String LOG_FILE_PATH = "logs/application-logs.json";
    private static final String LPS_LOG_FILE_PATH = "logs/lps-logs.json";

    @Autowired
    private UserProfileRepository userProfileRepository;

    public void analyzeAndSaveProfiles() throws IOException {
        Map<String, UserProfile> userProfiles = analyzeLogsAndGenerateLPS();
        saveProfilesToDatabase(userProfiles);
    }

    public Map<String, UserProfile> analyzeLogsAndGenerateLPS() throws IOException {
        Map<String, UserProfile> profiles = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        List<LPS> lpsLogs = new ArrayList<>(); // Liste pour stocker les objets LPS

        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    JsonNode log = objectMapper.readTree(line);

                    if (log.has("message")) {
                        String message = log.get("message").asText();
                        String email = extractEmail(message);

                        if (email != null) {
                            UserProfile userProfile = profiles.computeIfAbsent(email, UserProfile::new);

                            // Construire le LPS
                            LPS lps = new LPS.Builder()
                                    .withTimestamp(log.get("@timestamp").asText())
                                    .withUser(new User(email, "user")) // Ajoutez le rôle si disponible
                                    .withAction(new Action(extractMethodName(message), null)) // Extraire la méthode appelée
                                    .withEvent(new Event("success", message))
                                    .build();

                            // Ajouter le LPS à la liste
                            lpsLogs.add(lps);

                            // Identifier le type d'opération
                            if (message.contains("getProducts") || message.contains("getAllProducts")) {
                                userProfile.incrementReadOperations();
                            } else if (message.contains("getMostExpensiveProducts")) {
                                userProfile.incrementExpensiveProductSearches();
                            } else if (message.contains("addProduct") || message.contains("updateProduct") || message.contains("deleteProduct")) {
                                userProfile.incrementWriteOperations();
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors du traitement d'une ligne de log : " + line);
                    e.printStackTrace();
                }
            }
        }

        // Sauvegarder les LPS dans un fichier
        saveLPSLogsToFile(lpsLogs, LPS_LOG_FILE_PATH);

        // Déterminer le type de profil pour chaque utilisateur
        for (UserProfile userProfile : profiles.values()) {
            userProfile.determineProfileType();
        }

        return profiles;
    }

    public static void saveLPSLogsToFile(List<LPS> lpsLogs, String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), lpsLogs);
        System.out.println("LPS logs saved to " + filePath);
    }

    private static String extractMethodName(String message) {
        // Cherche une partie du message qui correspond au modèle "Méthode appelée: <nom>"
        if (message.contains("Méthode appelée: ")) {
            int startIndex = message.indexOf("Méthode appelée: ") + "Méthode appelée: ".length();
            int endIndex = message.indexOf(",", startIndex); // Recherche la fin de la méthode (avant la prochaine virgule)
            if (endIndex == -1) { // S'il n'y a pas de virgule, la méthode est à la fin
                endIndex = message.length();
            }
            return message.substring(startIndex, endIndex).trim();
        }
        return "Unknown"; // Retourne "Unknown" si la méthode ne peut pas être extraite
    }

    private static String extractEmail(String message) {
        try {
            if (message.startsWith("Utilisateur: ")) {
                return message.split(",")[0].replace("Utilisateur: ", "").trim();
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'extraction de l'email: " + message);
        }
        return null;
    }

    private void saveProfilesToDatabase(Map<String, UserProfile> profiles) {
        userProfileRepository.saveAll(profiles.values());
    }
}
