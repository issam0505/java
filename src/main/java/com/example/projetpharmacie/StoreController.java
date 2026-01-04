package com.example.projetpharmacie;

import javafx.animation.*;
import javafx.event.ActionEvent;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class StoreController {
    @FXML
    private TextField searchField;
    @FXML
    private void goToLogin() {
        try {
            Stage stage = (Stage) menuButton.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/projetpharmacie/login.fxml")
            );

            Scene scene = new Scene(
                    loader.load(),
                    stage.getWidth(),
                    stage.getHeight()
            );

            stage.setScene(scene);
            stage.setTitle("SwiftCare/Login");
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private MenuButton menuButton;



    @FXML
    private FlowPane productContainer;

    private  String search;
    private   String sql = "SELECT produitid, nom, prix, stock, images , new FROM  produit where new = true ";

    @FXML
    public void initialize() {
        MenuItem byCategory = new MenuItem("by category");
        MenuItem panier = new MenuItem("Panier");
        MenuItem profile = new MenuItem("Profile");
        MenuItem logout = new MenuItem("Déconnexion");
        byCategory.setOnAction(e -> loadProducts());
        panier.setOnAction(e -> loadProducts());
        profile.setOnAction(e ->loadProducts());
        logout.setOnAction(e -> goToLogin());

        menuButton.getItems().addAll(
                byCategory,
                panier,
                profile,
                logout
        );
        playTopBarAnimation();
        loadProducts();
    }


    // ================= LOAD PRODUCTS =================
    @FXML
    private void loadProducts(){
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

                Node card = loader.load();

                ProductCardController controller = loader.getController();
                controller.setData(
                        rs.getString("nom"),
                        rs.getDouble("prix"),
                        rs.getInt("stock"),
                        getClass().getResource(
                                "/images/" + rs.getString("images")
                        ).toExternalForm(),
                        rs.getBoolean("new")
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

            Stage stage = (Stage) productContainer.getScene().getWindow();

            Scene scene = new Scene(
                    loader.load(),
                    stage.getWidth(),
                    stage.getHeight()
            );

            ProductDetailsController controller = loader.getController();
            controller.setProduitId(produitId);

            stage.setScene(scene);
            stage.setMaximized(true); // احتياط
            stage.setTitle("SwiftCare/productdetails");
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

            delay += 150;
        }
    }
    @FXML
    private void loadProductall(){
        sql = "SELECT produitid, nom, prix, stock, images , new FROM  produit";
        loadProducts();
    }
    @FXML
    private void recuper(){
        search = searchField.getText();

        try (Connection conn = Database.getConnection()) {
            if (conn != null) {
                String query1 = "SELECT produitid FROM produit WHERE nom=?";
                PreparedStatement pst1 = conn.prepareStatement(query1);
                pst1.setString(1, search);
                ResultSet rs = pst1.executeQuery();
                if (rs.next()) {
                    openProductDetails(rs.getInt("produitid"));
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
