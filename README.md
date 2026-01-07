# SwiftCare (JavaFX)

🏥💊  
Application desktop moderne et performante de gestion de pharmacie, conçue pour centraliser et optimiser la gestion des médicaments, des ventes, des commandes et des utilisateurs.

---

## Description Générale

**SwiftCare** est une application logicielle robuste développée en **Java avec JavaFX**, destinée à faciliter la gestion quotidienne d’une pharmacie.  
L’application repose sur l’architecture **MVC (Model–View–Controller)** afin d’assurer une séparation claire des responsabilités, ce qui améliore la maintenabilité, la lisibilité du code et l’évolutivité du système.

SwiftCare permet aux pharmaciens et aux administrateurs de gérer efficacement les stocks de médicaments, les ventes, les commandes clients et les utilisateurs, tout en offrant une interface graphique intuitive, réactive et sécurisée.

---

## ✨ Fonctionnalités Clés

- **Gestion des Médicaments** :  
  Ajout, consultation, modification et suppression des médicaments avec suivi des stocks et des dates de péremption.
- **Gestion des Ventes et Facturation** :  
  Enregistrement des ventes, calcul automatique des montants et génération de factures.
- **Gestion des Commandes Clients** :  
  Création, suivi et consultation de l’historique des commandes.
- **Gestion des Utilisateurs** :  
  Système de rôles (Administrateur, Pharmacien, Client) avec contrôle des accès.
- **Tableau de Bord (Dashboard)** :  
  Vue globale sur les statistiques (stocks, ventes, commandes).
- **Notifications** :  
  Alertes automatiques en cas de stock faible ou d’événements importants.
- **Interface Graphique Moderne** :  
  Interface JavaFX claire, ergonomique et facile à utiliser.

---

## 🛠️ Stack Technique

- **Langage** : Java  
- **Interface Graphique** : JavaFX (FXML)  
- **Architecture** : MVC (Modèle – Vue – Contrôleur)  
- **Base de Données** : MySQL  
- **Accès aux Données** : JDBC  
- **Outils** : Scene Builder, Git  
- **IDE recommandé** : IntelliJ IDEA, Eclipse ou NetBeans  

---

## 📁 Structure du Projet

SwiftCare/
│
├── src/
│ ├── model/ # Classes métier (Médicaments, Utilisateurs, Ventes, etc.)
│ ├── dao/ # Accès à la base de données (JDBC)
│ ├── controller/ # Contrôleurs JavaFX
│ ├── view/ # Interfaces graphiques (FXML)
│ └── utils/ # Classes utilitaires (connexion BD, helpers)
│
├── resources/
│ ├── fxml/ # Fichiers FXML
│ ├── css/ # Feuilles de style CSS
│ └── images/ # Images et icônes
│
├── database/
│ └── swiftcare.sql # Script SQL de la base de données
│
└── Main.java # Classe principale JavaFX

yaml
Copier le code

---

## 🚀 Installation et Exécution

1. Cloner le projet :
   ```bash
   git clone https://github.com/username/swiftcare.git
Ouvrir le projet dans votre IDE Java.

Créer la base de données MySQL et importer le fichier :

sql
Copier le code
pharmacie.sql
Configurer les paramètres de connexion JDBC (URL, utilisateur, mot de passe).

Lancer la classe principale Main.java.

🔐 Sécurité
Authentification des utilisateurs

Gestion des rôles et des permissions

Protection et confidentialité des données sensibles

📌 Notes
Les administrateurs disposent d’un accès complet à toutes les fonctionnalités.

Les pharmaciens peuvent gérer les médicaments, les ventes et les commandes.

Les clients peuvent consulter les produits et suivre leurs commandes.

L’architecture MVC facilite la maintenance et l’évolution du projet.

📈 Perspectives d’Évolution
Génération de rapports au format PDF

Statistiques avancées avec graphiques

Sauvegarde automatique de la base de données

Intégration future d’une API REST ou d’une version web



