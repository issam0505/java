package com.example.projetpharmacie;

public class UserSession {

    private static UserSession instance;

    private int clientId;
    private String nom;
    private String email;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setUser(int clientId, String nom, String email) {
        this.clientId = clientId;
        this.nom = nom;
        this.email = email;
    }

    public int getClientId() {
        return clientId;
    }

    public String getNom() {
        return nom;
    }

    public String getEmail() {
        return email;
    }

    public void clear() {
        instance = null;
    }
}
