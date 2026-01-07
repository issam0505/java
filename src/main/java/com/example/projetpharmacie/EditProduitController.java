package com.example.projetpharmacie;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EditProduitController {

    @FXML
    private javafx.scene.control.Label productNameLabel;
    @FXML
    private TextField priceField;
    @FXML
    private TextField stockField;

    private int produitId;
    private PharmacienController parentController;

    public void setProduitId(int produitId) {
        this.produitId = produitId;
        loadProduit();
    }

    public void setParentController(PharmacienController controller) {
        this.parentController = controller;
    }

    private void loadProduit() {
        String sql = "SELECT nom, prix, stock FROM produit WHERE produitid = ?";

        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, produitId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                productNameLabel.setText(rs.getString("nom"));
                priceField.setText(String.valueOf(rs.getDouble("prix")));
                stockField.setText(String.valueOf(rs.getInt("stock")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void updateProduit() {
        String sql = "UPDATE produit SET prix = ?, stock = ? WHERE produitid = ?";

        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, Double.parseDouble(priceField.getText()));
            ps.setInt(2, Integer.parseInt(stockField.getText()));
            ps.setInt(3, produitId);

            ps.executeUpdate();

            showAlert("Succès", "Produit modifié avec succès ! ✅", "INFO");
            parentController.refresh();
            close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteProduit() {
        try (Connection conn = Database.getConnection()) {

            PreparedStatement check = conn.prepareStatement("SELECT stock FROM produit WHERE produitid=?");
            check.setInt(1, produitId);
            ResultSet rs = check.executeQuery();

            if (rs.next() && rs.getInt("stock") == 0) {

                PreparedStatement delete = conn.prepareStatement("DELETE FROM produit WHERE produitid=?");
                delete.setInt(1, produitId);
                delete.executeUpdate();

                showAlert("Succès", "Produit supprimé ! 🗑️", "INFO");
                parentController.refresh();
                close();

            } else {
                showAlert("Erreur", "Stock non nul ❌ suppression interdite", "ERROR");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void close() {
        Stage stage = (Stage) priceField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, String type) {
        Alert alert;
        if (type.equals("ERROR")) {
            alert = new Alert(Alert.AlertType.ERROR);
        } else {
            alert = new Alert(Alert.AlertType.INFORMATION);
        }
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
