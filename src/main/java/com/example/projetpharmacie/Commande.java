package com.example.projetpharmacie;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;
public class Commande {

    @FXML
    private Label userNameLabel;

    @FXML
    private Label productsLabel;

    @FXML
    private Label totalLabel;

    private final CartManager cartManager = CartManager.getInstance();
    private final UserSession userSession = UserSession.getInstance();
    private final DAO dao = new DAO();

    @FXML
    public void initialize() {

        // user name
        userNameLabel.setText(userSession.getNom());

        // products names
        StringBuilder names = new StringBuilder();
        cartManager.getCartItems()
                .forEach(item ->
                        names.append(item.getName()).append(" | ")
                );
        productsLabel.setText(names.toString());

        // total
        totalLabel.setText(cartManager.getTotal() + " DH");
    }

    // ================= NAVIGATION =================

    @FXML
    private void goLivraison(ActionEvent event) throws InterruptedException {
        loadPage("/com/example/projetpharmacie/livraison.fxml", event);
        double total = cartManager.getTotal()+30;
        LocalDate today = LocalDate.now();
            int idCommande = dao.insertcommande(userSession,today, "livraison", total);
        System.out.println("Commande: " + cartManager.getCartItems().size() + " produits, total: " + cartManager.getTotal() + " DH");
        for(CartManager.CartItem item : cartManager.getCartItems()) {
            int idproduit = item.getIdproduit();
            int quantity = item.getQuantity();
            double price = item.getPrice();
            addtocardthred n1 = new addtocardthred(idproduit, quantity,"rupture");
            addtocardthred n2 = new addtocardthred(idCommande,idproduit, quantity,"ajoute");
            n1.start();
            n1.join();
            n2.start();
            n2.join();
        }
        cartManager.getCartItems().clear();
    }

    @FXML
    private void goCart(ActionEvent event) {
        loadPage("/com/example/projetpharmacie/cart.fxml", event);
    }

    @FXML
    private void goBack() {
        loadPage("/com/example/projetpharmacie/addtocart.fxml", null);
    }

    private void loadPage(String fxml, ActionEvent event) {
        try {
            Stage stage;
            if (event != null) {
                stage = (Stage) ((javafx.scene.Node) event.getSource())
                        .getScene().getWindow();
            } else {
                stage = (Stage) userNameLabel.getScene().getWindow();
            }

            double width = stage.getWidth();
            double height = stage.getHeight();

            Scene scene = new Scene(
                    FXMLLoader.load(getClass().getResource(fxml)),
                    width,
                    height
            );

            stage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
