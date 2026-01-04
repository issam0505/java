package com.example.projetpharmacie;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProductDetailsController {

    @FXML
    private ImageView productImage;

    @FXML
    private Text productName;

    @FXML
    private Text productPrice;

    @FXML
    private Text productStock;

    @FXML
    private javafx.scene.layout.VBox productCard; // animation

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
                                getClass().getResource("/images/" + rs.getString("images")).toExternalForm()
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        playEntranceAnimation(); // animation on load
    }

    // ================= BACK BUTTON =================
    @FXML
    private void goBack(ActionEvent event) {
        try {
            // تحميل FXML ديال Store
            FXMLLoader loader = new FXMLLoader(getClass().getResource("store.fxml"));
            BorderPane storeRoot = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // احتافض على الوضعية و الحجم الحالي
            boolean wasMaximized = stage.isMaximized();
            double width = stage.getWidth();
            double height = stage.getHeight();

            Scene scene = new Scene(storeRoot, width, height);

            stage.setScene(scene);
            stage.setMaximized(wasMaximized);
            stage.setTitle("SwiftCare/Store");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= ANIMATION =================
    private void playEntranceAnimation() {
        if (productCard == null) return;

        productCard.setOpacity(0);
        productCard.setTranslateY(40);

        FadeTransition fade = new FadeTransition(Duration.millis(600), productCard);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(600), productCard);
        slide.setFromY(40);
        slide.setToY(0);

        ParallelTransition animation = new ParallelTransition(fade, slide);
        animation.setInterpolator(Interpolator.EASE_OUT);
        animation.play();
    }
}
