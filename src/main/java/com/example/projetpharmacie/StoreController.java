package com.example.projetpharmacie;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class StoreController {

    @FXML
    private MenuButton menuButton;

    @FXML
    private TextField searchField;

    @FXML
    private FlowPane productContainer;

    @FXML
    public void initialize() {

        menuButton.getItems().addAll(
                new MenuItem("Produits"),
                new MenuItem("Clients"),
                new MenuItem("Déconnexion")
        );

        playTopBarAnimation();
        loadProducts();
    }

    // ================= LOAD PRODUCTS =================
    private void loadProducts() {

        String sql = "SELECT produitid, nom, prix, stock, images FROM produit";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            productContainer.getChildren().clear();

            while (rs.next()) {

                int produitId = rs.getInt("produitid");

                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource(
                                "/com/example/projetpharmacie/product-card.fxml"
                        )
                );

                HBox card = loader.load();

                ProductCardController controller = loader.getController();
                controller.setData(
                        rs.getString("nom"),
                        rs.getDouble("prix"),
                        rs.getInt("stock"),
                        getClass().getResource(
                                "/images/" + rs.getString("images")
                        ).toExternalForm()
                );

                card.setOnMouseClicked(e -> openProductDetails(produitId));
                productContainer.getChildren().add(card);
            }

            playCardsAnimation(); // animation ناعمة

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= OPEN DETAILS =================
    private void openProductDetails(int produitId) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/example/projetpharmacie/product-details.fxml"
                    )
            );

            Scene scene = new Scene(loader.load());

            ProductDetailsController controller = loader.getController();
            controller.setProduitId(produitId);

            Stage stage = (Stage) productContainer.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= TOP BAR ANIMATION =================
    private void playTopBarAnimation() {

        HBox topBar = (HBox) menuButton.getParent();
        Node logo = topBar.getChildren().get(0);
        Node search = searchField;
        Node menu = menuButton;

        logo.setOpacity(0);
        search.setOpacity(0);
        menu.setOpacity(0);

        animateTopNode(logo, 0);
        animateTopNode(search, 120);
        animateTopNode(menu, 240);
    }

    private void animateTopNode(Node node, int delay) {

        TranslateTransition slide = new TranslateTransition(
                Duration.millis(500), node
        );
        slide.setFromY(-25);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition fade = new FadeTransition(
                Duration.millis(500), node
        );
        fade.setFromValue(0);
        fade.setToValue(1);

        ParallelTransition anim =
                new ParallelTransition(node, slide, fade);

        anim.setDelay(Duration.millis(delay));
        anim.play();
    }

    // ================= SMOOTH CARDS ANIMATION =================
    private void playCardsAnimation() {

        int delay = 0;

        for (Node card : productContainer.getChildren()) {

            card.setOpacity(0);
            card.setTranslateY(25);

            FadeTransition fade = new FadeTransition(
                    Duration.millis(650), card
            );
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.setInterpolator(Interpolator.EASE_OUT);

            TranslateTransition slide = new TranslateTransition(
                    Duration.millis(650), card
            );
            slide.setFromY(25);
            slide.setToY(0);
            slide.setInterpolator(Interpolator.EASE_OUT);

            ParallelTransition anim =
                    new ParallelTransition(card, fade, slide);

            anim.setDelay(Duration.millis(delay));
            anim.play();

            delay += 150; // بطيئة و مريحة
        }
    }
}
