package com.example.projetpharmacie;

public class Produit {
    private int id;
    private String nom;
    private double prix;
    private int stock;
    private String images;
    private int categorieId;

    public Produit(int id, String nom, double prix, int stock, String images, int categorieId) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.stock = stock;
        this.images = images;
        this.categorieId = categorieId;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public double getPrix() { return prix; }
    public int getStock() { return stock; }
    public String getImages() { return images; }
    public int getCategorieId() { return categorieId; }
}
