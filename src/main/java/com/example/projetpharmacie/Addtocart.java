package com.example.projetpharmacie;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Addtocart {

    @FXML
    private VBox centerContainer;

    private final CartManager cartManager = CartManager.getInstance();
    private Label totalLabel = new Label();

    @FXML
    public void initialize() {
        updateCartUI();
    }

    private void updateCartUI() {
        centerContainer.getChildren().clear();

        VBox productsBox = new VBox(10);
        productsBox.setStyle("-fx-padding: 20;");

        for (CartManager.CartItem item : cartManager.getCartItems()) {
            HBox hbox = new HBox(15);
            hbox.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 10; -fx-background-color: #fdfdfd; -fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1),5,0,0,1);");
            hbox.setAlignment(Pos.CENTER_LEFT);

            ImageView img = new ImageView(item.getImage());
            img.setFitWidth(50);
            img.setPreserveRatio(true);

            Label nameLabel = new Label(item.getName());
            nameLabel.setPrefWidth(200);

            Label priceLabel = new Label(item.getPrice() + " DH x " + item.getQuantity() + " = " + item.getTotalPrice() + " DH");
            priceLabel.setPrefWidth(200);

            Button removeBtn = new Button("Remove");
            removeBtn.setOnAction(e -> {
                cartManager.removeProduct(item);
                updateCartUI();
            });

            hbox.getChildren().addAll(img, nameLabel, priceLabel, removeBtn);
            productsBox.getChildren().add(hbox);
        }

        // Total + Buttons
        HBox bottomBox = new HBox(20);
        bottomBox.setAlignment(Pos.CENTER);
        totalLabel.setText("Total: " + cartManager.getTotal() + " DH");
        totalLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        Button orderBtn = new Button("Commander");
        orderBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 5 15; -fx-font-weight: bold;");
        orderBtn.setOnAction(e -> orderProducts());

        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 5 15; -fx-font-weight: bold;");
        backBtn.setOnAction(this::goBack);

        bottomBox.getChildren().addAll(totalLabel, orderBtn, backBtn);

        centerContainer.getChildren().addAll(productsBox, bottomBox);
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Button) event.getSource())
                    .getScene()
                    .getWindow();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/projetpharmacie/store.fxml")
            );
            BorderPane root = loader.load();

            // نحافظو على نفس الحجم
            Scene scene = new Scene(
                    root,
                    stage.getWidth(),
                    stage.getHeight()
            );

            stage.setScene(scene);
            stage.setMaximized(true); // تبقى مكبّرة
            stage.setTitle("SwiftCare / Store");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void orderProducts() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("commande.fxml"));

            Stage stage = (Stage) centerContainer.getScene().getWindow();

            // حفظ الحجم الحالي
            double width = stage.getWidth();
            double height = stage.getHeight();

            Scene scene = new Scene(loader.load(), width, height);

            stage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
