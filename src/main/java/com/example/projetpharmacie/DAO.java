package com.example.projetpharmacie;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DAO {
    public String selectAll(String Tabl) {
        String select = "SELECT * FROM " + Tabl;
        return select;
    }

    public String misestock() {
        return "UPDATE produit SET stock = stock - ? WHERE produitid = ?";
    }

    public void ajoutelc(int id,int idproduit, int quantity) {
        String query1 = " INSERT INTO lignecommande (commandeID,produitID, quantite) VALUES ( ?,?, ?) ;";
        try (Connection conn = Database.getConnection()) {
            if (conn != null) {
                PreparedStatement pst1 = conn.prepareStatement(query1);
                pst1.setInt(1, id);
                pst1.setInt(2, idproduit);
                pst1.setInt(3, quantity);
                pst1.executeUpdate();

            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }


    public int insertcommande(UserSession userSession, LocalDate today, String statu, double total) {
        int generatedId = 0;
        String query = "INSERT INTO commande(clientID, datecommande, statu, total) VALUES (?,?,?,?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pst = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pst.setInt(1, userSession.getClientId());
            pst.setString(2, today.toString());
            pst.setString(3, statu);
            pst.setDouble(4, total);

            pst.executeUpdate();
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return generatedId;
    }
    public List<Produit> getAllProduits() {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT produitid, nom, prix, stock, images, categorieid FROM produit";

        try (Connection conn = Database.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                produits.add(new Produit(
                        rs.getInt("produitid"),
                        rs.getString("nom"),
                        rs.getDouble("prix"),
                        rs.getInt("stock"),
                        rs.getString("images"),
                        rs.getInt("categorieid") // must match column name in DB
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return produits;
    }

    public List<Categorie> getAllCategories() {
        List<Categorie> categories = new ArrayList<>();
        String sql = "SELECT categorieid, nom FROM categorie";

        try (Connection conn = Database.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                categories.add(new Categorie(
                        rs.getInt("categorieid"),
                        rs.getString("nom")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return categories;
    }



}


