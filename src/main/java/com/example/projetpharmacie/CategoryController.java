package com.example.projetpharmacie;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.animation.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryController {

    @FXML
    private VBox categoriesContainer;

    private DAO dao = new DAO();

    @FXML
    public void initialize() {
        loadCategories();
    }

    private void loadCategories() {
        categoriesContainer.getChildren().clear();

        List<Categorie> categories = dao.getAllCategories();

        for (Categorie cat : categories) {
            VBox catBox = new VBox(10);

            // Nom de la catégorie
            Text catName = new Text(cat.getNom());
            catName.getStyleClass().add("category-title");

            // FlowPane pour produits filtrés
            FlowPane productsPane = new FlowPane();
            productsPane.setHgap(20);
            productsPane.setVgap(20);

            List<Produit> filtered = dao.getAllProduits().stream()
                    .filter(p -> p.getCategorieId() == cat.getId())
                    .collect(Collectors.toList());

            for (Produit prod : filtered) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(
                            "/com/example/projetpharmacie/product-card.fxml"
                    ));
                    Node card = loader.load();
                    ProductCardController controller = loader.getController();
                    controller.setData(
                            prod.getNom(),
                            prod.getPrix(),
                            prod.getStock(),
                            getClass().getResource("/images/"+prod.getImages()).toExternalForm(),
                            false
                    );

                    // Animation d'apparition pour chaque produit
                    card.setOpacity(0);
                    TranslateTransition slide = new TranslateTransition(Duration.millis(400), card);
                    slide.setFromY(25);
                    slide.setToY(0);
                    FadeTransition fade = new FadeTransition(Duration.millis(400), card);
                    fade.setFromValue(0);
                    fade.setToValue(1);
                    ParallelTransition anim = new ParallelTransition(card, slide, fade);
                    anim.play();

                    productsPane.getChildren().add(card);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            catBox.getChildren().addAll(catName, productsPane);
            categoriesContainer.getChildren().add(catBox);

            // Animation pour le VBox de la catégorie
            catBox.setOpacity(0);
            TranslateTransition slideCat = new TranslateTransition(Duration.millis(500), catBox);
            slideCat.setFromY(20);
            slideCat.setToY(0);
            FadeTransition fadeCat = new FadeTransition(Duration.millis(500), catBox);
            fadeCat.setFromValue(0);
            fadeCat.setToValue(1);
            ParallelTransition animCat = new ParallelTransition(catBox, slideCat, fadeCat);
            animCat.play();
        }
    }
    @FXML
    private void goBackToStore() {
        try {
            Stage stage = (Stage) categoriesContainer.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/projetpharmacie/store.fxml")
            );

            Scene scene = new Scene(
                    loader.load(),
                    stage.getWidth(),
                    stage.getHeight()
            );

            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setTitle("SwiftCare / Store");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
