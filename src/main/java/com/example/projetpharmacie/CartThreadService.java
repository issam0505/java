package com.example.projetpharmacie;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;

public class CartThreadService {
    DAO dao = new DAO();

    public synchronized void misestock(int idproduit, int quantity) {

        String query1 = dao.misestock();

        try (Connection conn = Database.getConnection()) {
            if (conn != null) {
                PreparedStatement pst1 = conn.prepareStatement(query1);

                pst1.setInt(1, quantity);
                pst1.setInt(2, idproduit);

                pst1.executeUpdate();
                System.out.println("✅ Produit " + idproduit + " stock mis à jour");
            } else {
                System.out.println("❌ No connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void ajouterlc(int id,int idproduit,int quantity) {
         dao.ajoutelc(id,idproduit, quantity);
    }
        }



