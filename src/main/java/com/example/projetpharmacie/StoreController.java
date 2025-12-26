package com.example.projetpharmacie;

import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.ParallelTransition;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.sql.*;

public class StoreController {

    @FXML
    private MenuButton menuButton;

    @FXML
    private VBox productContainer;

    @FXML
    private TextField searchField;

    // ================= INITIALIZE =================
    @FXML
    public void initialize() {

        // ===== MENU =====
        MenuItem produits = new MenuItem("Produits");
        MenuItem clients = new MenuItem("Clients");
        MenuItem logout = new MenuItem("Déconnexion");

        menuButton.getItems().addAll(produits, clients, logout);

        // ===== ANIMATION =====
        HBox logoBox = (HBox) ((HBox) menuButton.getParent()).getChildren().get(0);
        Node search = searchField;
        Node menu = menuButton;

        logoBox.setOpacity(0);
        search.setOpacity(0);
        menu.setOpacity(0);

        ParallelTransition logoAnim = animation(logoBox);
        ParallelTransition searchAnim = animation(search);
        ParallelTransition menuAnim = animation(menu);

        logoAnim.setOnFinished(e -> searchAnim.play());
        searchAnim.setOnFinished(e -> menuAnim.play());
        logoAnim.play();

        // ===== LOAD PRODUCTS =====
        afficherProduitsDirect();
    }

    // ================= DISPLAY PRODUCTS (PreparedStatement) =================
    private void afficherProduitsDirect() {

        productContainer.getChildren().clear();

        String query = "SELECT * FROM produit";

        try (Connection cnx = Database.getConnection();
             PreparedStatement ps = cnx.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                // ===== CARD =====
                HBox card = new HBox(15);
                card.setStyle("""
                    -fx-background-color: white;
                    -fx-padding: 15;
                    -fx-background-radius: 15;
                    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 4);
                """);

                // ===== IMAGE =====
                ImageView imageView = new ImageView();
                imageView.setFitWidth(80);
                imageView.setFitHeight(80);
                imageView.setPreserveRatio(true);

                try {
                    String imgPath =
                            "/com/example/projetpharmacie/images/" +
                                    rs.getString("images");

                    Image img = new Image(
                            getClass().getResourceAsStream(imgPath)
                    );
                    imageView.setImage(img);

                } catch (Exception e) {
                    System.out.println("❌ Image non trouvée");
                }

                // ===== TEXT =====
                VBox info = new VBox(5);

                Text nom = new Text(rs.getString("nom"));
                nom.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

                Text prix = new Text(
                        "Prix : " + rs.getDouble("prix") + " DH"
                );

                Text stock = new Text(
                        "Stock : " + rs.getInt("stock")
                );

                info.getChildren().addAll(nom, prix, stock);

                card.getChildren().addAll(imageView, info);
                productContainer.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= ANIMATION METHOD =================
    private ParallelTransition animation(Node node) {

        TranslateTransition slide =
                new TranslateTransition(Duration.seconds(1), node);
        slide.setFromY(-50);
        slide.setToY(0);

        FadeTransition fade =
                new FadeTransition(Duration.seconds(1), node);
        fade.setFromValue(0);
        fade.setToValue(1);

        return new ParallelTransition(slide, fade);
    }

    // ================= NAVIGATION =================
    @FXML
    private void goBackToLogin(ActionEvent event) {
        try {
            Stage stage =
                    (Stage) ((Node) event.getSource())
                            .getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/example/projetpharmacie/login.fxml"
                    )
            );

            Scene scene = new Scene(
                    loader.load(),
                    stage.getWidth(),
                    stage.getHeight()
            );

            stage.setScene(scene);
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
