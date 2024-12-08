package com.profiling.profiling_project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "user_profiles")
public class UserProfile {

    @Id
    private String email; // Identifiant unique (l'email de l'utilisateur)
    private int readOperations; // Nombre d'opérations de lecture
    private int writeOperations; // Nombre d'opérations d'écriture
    private int expensiveProductSearches; // Nombre de recherches de produits chers
    private String profileType; // Type de profil : Reader, Writer, ExpensiveProductSearcher

    // Nouveau champ pour stocker les opérations détaillées
    private Map<String, List<String>> operations; // Catégories : "read", "write", "expensive"

    // Constructeur principal
    public UserProfile(String email) {
        this.email = email;
        this.readOperations = 0;
        this.writeOperations = 0;
        this.expensiveProductSearches = 0;
        this.profileType = "Undefined"; // Type par défaut avant calcul
        this.operations = new HashMap<>(); // Initialisation de la carte des opérations
        this.operations.put("read", new ArrayList<>());
        this.operations.put("write", new ArrayList<>());
        this.operations.put("expensive", new ArrayList<>());
    }

    // Constructeur vide requis par MongoDB
    public UserProfile() {}

    // Getters et Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getReadOperations() {
        return readOperations;
    }

    public void setReadOperations(int readOperations) {
        this.readOperations = readOperations;
    }

    public int getWriteOperations() {
        return writeOperations;
    }

    public void setWriteOperations(int writeOperations) {
        this.writeOperations = writeOperations;
    }

    public int getExpensiveProductSearches() {
        return expensiveProductSearches;
    }

    public void setExpensiveProductSearches(int expensiveProductSearches) {
        this.expensiveProductSearches = expensiveProductSearches;
    }

    public String getProfileType() {
        return profileType;
    }

    public void setProfileType(String profileType) {
        this.profileType = profileType;
    }

    public Map<String, List<String>> getOperations() {
        return operations;
    }

    // Méthodes pour incrémenter les compteurs
    public void incrementReadOperations() {
        this.readOperations++;
    }

    public void incrementWriteOperations() {
        this.writeOperations++;
    }

    public void incrementExpensiveProductSearches() {
        this.expensiveProductSearches++;
    }

    // Méthode pour ajouter une opération
    public void addOperation(String category, String methodName) {
        if (operations.containsKey(category)) {
            operations.get(category).add(methodName);
        } else {
            List<String> newCategory = new ArrayList<>();
            newCategory.add(methodName);
            operations.put(category, newCategory);
        }
    }

    // Déterminer le type de profil en fonction des activités
    public void determineProfileType() {
        if (readOperations >= writeOperations && readOperations >= expensiveProductSearches) {
            this.profileType = "Reader";
        } else if (writeOperations >= readOperations && writeOperations >= expensiveProductSearches) {
            this.profileType = "Writer";
        } else if (expensiveProductSearches > readOperations && expensiveProductSearches > writeOperations) {
            this.profileType = "ExpensiveProductSearcher";
        } else {
            this.profileType = "Undefined"; // En cas d'égalité ou d'activité non significative
        }
    }
}
