package com.example.projetpharmacie;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class
ProductDetailsController {

    @FXML
    private ImageView productImage;

    @FXML
    private Text productName;

    @FXML
    private Text productPrice;

    @FXML
    private Text productStock;

    private int produitId;

    public void setProduitId(int produitId) {
        this.produitId = produitId;
        loadProduit();
    }

    private void loadProduit() {

        String sql = "SELECT nom, prix, stock, images FROM produit WHERE produitid = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, produitId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                productName.setText(rs.getString("nom"));
                productPrice.setText("Prix : " + rs.getDouble("prix") + " DH");
                productStock.setText("Stock : " + rs.getInt("stock"));
                productImage.setImage(
                        new Image(
                                getClass().getResource(
                                        "/images/" + rs.getString("images")
                                ).toExternalForm()
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
