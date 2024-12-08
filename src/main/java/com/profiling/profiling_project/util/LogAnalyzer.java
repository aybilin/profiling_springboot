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

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class LogAnalyzer {

    private static final String LOG_FILE_PATH = "logs/application-logs.json";
    private static final String LPS_LOG_FILE_PATH = "logs/lps-logs.json";
    private static final String READ_PROFILES_FILE_PATH = "logs/read-profiles.json";
    private static final String WRITE_PROFILES_FILE_PATH = "logs/write-profiles.json";
    private static final String EXPENSIVE_PROFILES_FILE_PATH = "logs/expensive-profiles.json";

    @Autowired
    private UserProfileRepository userProfileRepository;

    public void analyzeAndSaveProfiles() throws IOException {
        Map<String, UserProfile> userProfiles = analyzeLogsAndGenerateLPS();
        saveProfilesToDatabase(userProfiles);
        saveProfilesToJsonFiles(userProfiles);
    }

    public Map<String, UserProfile> analyzeLogsAndGenerateLPS() throws IOException {
        Map<String, UserProfile> profiles = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        List<LPS> lpsLogs = new ArrayList<>();

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
                                    .withUser(new User(email, "user"))
                                    .withAction(new Action(extractMethodName(message), null))
                                    .withEvent(new Event("success", message))
                                    .build();

                            // Ajouter le LPS à la liste
                            lpsLogs.add(lps);

                            // Identifier le type d'opération
                            if (message.contains("getProducts") || message.contains("getAllProducts")) {
                                userProfile.incrementReadOperations();
                                userProfile.addOperation("read", extractMethodName(message));
                            } else if (message.contains("getMostExpensiveProducts")) {
                                userProfile.incrementExpensiveProductSearches();
                                userProfile.addOperation("expensive", extractMethodName(message));
                            } else if (message.contains("addProduct") || message.contains("updateProduct") || message.contains("deleteProduct")) {
                                userProfile.incrementWriteOperations();
                                userProfile.addOperation("write", extractMethodName(message));
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
        if (message.contains("Méthode appelée: ")) {
            int startIndex = message.indexOf("Méthode appelée: ") + "Méthode appelée: ".length();
            int endIndex = message.indexOf(",", startIndex);
            if (endIndex == -1) {
                endIndex = message.length();
            }
            return message.substring(startIndex, endIndex).trim();
        }
        return "Unknown";
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

    private void saveProfilesToJsonFiles(Map<String, UserProfile> profiles) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        // Filtrage et structuration des profils
        List<Map<String, Object>> readProfiles = profiles.values().stream()
                .filter(profile -> profile.getProfileType().equals("Reader"))
                .sorted(Comparator.comparingInt(UserProfile::getReadOperations).reversed()) // Ordre décroissant par opérations de lecture
                .map(profile -> Map.of(
                        "email", profile.getEmail(),
                        "read_operations_count", profile.getReadOperations(),
                        "operations", Map.of(
                                "read", profile.getOperations().get("read"),
                                "write", profile.getOperations().get("write"),
                                "expensive", profile.getOperations().get("expensive")
                        )
                ))
                .collect(Collectors.toList());

        List<Map<String, Object>> writeProfiles = profiles.values().stream()
                .filter(profile -> profile.getProfileType().equals("Writer"))
                .sorted(Comparator.comparingInt(UserProfile::getWriteOperations).reversed()) // Ordre décroissant par opérations d'écriture
                .map(profile -> Map.of(
                        "email", profile.getEmail(),
                        "write_operations_count", profile.getWriteOperations(),
                        "operations", Map.of(
                                "read", profile.getOperations().get("read"),
                                "write", profile.getOperations().get("write"),
                                "expensive", profile.getOperations().get("expensive")
                        )
                ))
                .collect(Collectors.toList());

        List<Map<String, Object>> expensiveProfiles = profiles.values().stream()
                .filter(profile -> profile.getProfileType().equals("ExpensiveProductSearcher"))
                .sorted(Comparator.comparingInt(UserProfile::getExpensiveProductSearches).reversed()) // Ordre décroissant par recherches de produits chers
                .map(profile -> Map.of(
                        "email", profile.getEmail(),
                        "expensive_operations_count", profile.getExpensiveProductSearches(),
                        "operations", Map.of(
                                "read", profile.getOperations().get("read"),
                                "write", profile.getOperations().get("write"),
                                "expensive", profile.getOperations().get("expensive")
                        )
                ))
                .collect(Collectors.toList());

        // Sauvegarde des fichiers
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(READ_PROFILES_FILE_PATH), readProfiles);
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(WRITE_PROFILES_FILE_PATH), writeProfiles);
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(EXPENSIVE_PROFILES_FILE_PATH), expensiveProfiles);

        System.out.println("Profiles saved to JSON files: read, write, expensive");
    }


}
