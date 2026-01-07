package com.example.projetpharmacie;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/pharmacie?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static boolean tablesCreated = false;

    public static Connection getConnection() {
        try {
            Connection cnx = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion réussie");

            // Create tables on first connection
            if (!tablesCreated) {
                createTablesIfNotExist(cnx);
                tablesCreated = true;
            }

            return cnx;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void createTablesIfNotExist(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Add 'new' column to produit table if not exists
            try {
                stmt.executeUpdate("ALTER TABLE produit ADD COLUMN `new` BOOLEAN DEFAULT FALSE");
                System.out.println("✅ Colonne 'new' ajoutée à la table produit");
            } catch (Exception e) {
                // Column might already exist, ignore error
            }

            // Create ordonnance table
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS ordonnance (" +
                            "ordonnanceID INT AUTO_INCREMENT PRIMARY KEY, " +
                            "nomClient VARCHAR(255) NOT NULL, " +
                            "dateOrdonnance DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                            "pharmacienID INT, " +
                            "total DOUBLE DEFAULT 0, " +
                            "FOREIGN KEY (pharmacienID) REFERENCES pharmacien(id)" +
                            ")");

            // Create ligneordonnance table
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS ligneordonnance (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "ordonnanceID INT NOT NULL, " +
                            "produitID INT NOT NULL, " +
                            "quantite INT NOT NULL, " +
                            "prix DOUBLE NOT NULL, " +
                            "FOREIGN KEY (ordonnanceID) REFERENCES ordonnance(ordonnanceID), " +
                            "FOREIGN KEY (produitID) REFERENCES produit(produitid)" +
                            ")");

            System.out.println("✅ Tables ordonnance créées/vérifiées");
        } catch (Exception e) {
            System.err.println("Erreur création tables: " + e.getMessage());
        }
    }
}
