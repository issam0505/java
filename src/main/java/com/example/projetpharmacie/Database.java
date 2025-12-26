package com.example.projetpharmacie;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/pharmacie?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        try {
            Connection cnx = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion réussie");
            return cnx;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
