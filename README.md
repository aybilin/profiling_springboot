# 🛠️ Backend et Frontend - Gestion de Produits avec MongoDB

Ce projet est une application complète comprenant un **backend en Java Spring Boot**  pour gérer des produits avec persistance via une base de données **MongoDB**. Le projet inclut des fonctionnalités de logging pour profiler les utilisateurs.

---

## 📦 Fonctionnalités

### 🔹 Backend

- **CRUD Produits :**
  - Ajouter, afficher, mettre à jour et supprimer des produits avec ID, nom, prix, et date d'expiration.
- **CRUD Utilisateurs :**
  - Gestion des utilisateurs avec ID, nom, âge, email, et mot de passe.
- **Logging :**
  - Insertion automatique des Logs avec **Spoon**
  - Utilisation de **Logback** avec **SLF4J** pour le logging.
  - Création de profils d'utilisateurs basés sur leurs opérations (lecture, écriture, recherche).
- **Base de Données :**
  - Persistance avec **MongoDB**.

## 🚀 Installation

### Prérequis

- **Java Development Kit (JDK)** : Version 8 ou supérieure (23 recommmandé).
- **Maven** : Pour la gestion des dépendances.
- **MongoDB** : Version 5 ou supérieure.
- **MongoDB Compass** : Pour la visualisation des données.
- **Spoon** : Utilisé pour l'injection automatique des logs.
- **Postman** ou un outil similaire : Pour tester les API REST.
- **IDE** : IntelliJ IDEA ou Eclipse recommandé.

### Étapes d'Installation

#### 1️⃣ Cloner le Dépôt

```bash
git clone https://github.com/aybilin/profiling_springboot.git
cd profiling_springboot
```


 **Configurer `application.properties` :**

   ```properties
    spring.data.mongodb.uri=mongodb://localhost:27017/prof_database
   ```


## 📊 Logging 

### Backend Logging

Les logs sont générés automatiquement à l'aide de Spoon et sont stockés au format JSON dans logs/application-logs.json ou lps-logs.json. Les profils utilisateurs générés sont sauvegardés dans la base de donnée dans user-profiles.


---

## 🧪 Scénarios de Test

1. **Créer plusieurs utilisateurs.**
2. **Exécuter diverses opérations :**
   - Lire les produits.
   - Ajouter et supprimer des produits.
   - Mettre à jour les informations des produits.

Ces scénarios génèrent des logs pour profiler les utilisateurs selon leurs actions.
