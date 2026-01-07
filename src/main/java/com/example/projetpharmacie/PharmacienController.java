package com.example.projetpharmacie;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
public class PharmacienController {
    @FXML private FlowPane productContainer;
    @FXML private TextField nameField;
    @FXML  private TextField priceField;
    @FXML private TextField stockField;
    private String selectedImage = "default.png";
    @FXML
    public void initialize() {
        loadProduits();
    }

    @FXML private void logout() {
        try {
            UserSession.getInstance().clear();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) productContainer.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Connexion");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadProduits() {
        String sql = "SELECT produitid, nom, prix, stock, images, new FROM produit";
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            productContainer.getChildren().clear();
            while (rs.next()) {
                int produitId = rs.getInt("produitid");
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/example/projetpharmacie/product-card.fxml"));
                Node card = loader.load();
                ProductCardController controller = loader.getController();
                String imageName = rs.getString("images");
                String imagePath;
                if (imageName != null && !imageName.isEmpty()
                        && getClass().getResource("/images/" + imageName) != null) {
                    imagePath = getClass()
                            .getResource("/images/" + imageName)
                            .toExternalForm();
                } else {
                    imagePath = getClass()
                            .getResource("/images/default.png")
                            .toExternalForm();
                }
                controller.setData(
                        rs.getString("nom"),
                        rs.getDouble("prix"),
                        rs.getInt("stock"),
                        imagePath,
                        rs.getBoolean("new"));
                card.setOnMouseClicked(e -> openEditProduit(produitId));
                productContainer.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void addProduit() {

        String nom = nameField.getText().trim();
        String prixText = priceField.getText().trim();
        String stockText = stockField.getText().trim();
        if (nom.isEmpty() || prixText.isEmpty() || stockText.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs !", "ERROR");
            return;
        }
        try {
            double prix = Double.parseDouble(prixText);
            int stock = Integer.parseInt(stockText);
            if (prix <= 0) {
                showAlert("Erreur", "Le prix doit être supérieur à 0 !", "ERROR");
                return;
            }
            if (stock < 0) {
                showAlert("Erreur", "Le stock ne peut pas être négatif !", "ERROR");
                return;
            }
            String sql = "INSERT INTO produit (nom, prix, stock, images, new) VALUES (?, ?, ?, ?, 1)";

            try (Connection conn = Database.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, nom);
                ps.setDouble(2, prix);
                ps.setInt(3, stock);
                ps.setString(4, selectedImage);
                ps.executeUpdate();
                showAlert("Succès", "Produit ajouté avec succès ! ", "INFO");
                loadProduits();  
                nameField.clear();
                priceField.clear();
                stockField.clear();
                selectedImage = "default.png";  
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Prix ou stock invalide !", "ERROR");
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout : " + e.getMessage(), "ERROR");
            e.printStackTrace();
        }
    }
    private void showAlert(String title, String message, String type) {
        javafx.scene.control.Alert alert;
        if (type.equals("ERROR")) {
            alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        } else {
            alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        }
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

     private void openEditProduit(int produitId) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/projetpharmacie/edit-produit.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            EditProduitController controller = loader.getController();
            controller.setProduitId(produitId);
            controller.setParentController(this);
            stage.setScene(scene);
            stage.setTitle("Modifier produit");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Images", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(
                productContainer.getScene().getWindow());
        if (file != null) {
            try {
                Path targetDir = Path.of(
                        getClass().getResource("/images").toURI());
                Path targetFile = targetDir.resolve(file.getName());
                Files.copy(
                        file.toPath(),
                        targetFile,
                        StandardCopyOption.REPLACE_EXISTING);
                selectedImage = file.getName(); 
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
     public void refresh() {
        loadProduits();
    }
    @FXML
    private void showHistoriqueOrdonnances() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Historique des Ordonnances");
        dialog.setHeaderText(null);
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #f9fafb;");
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT o.ordonnanceID, o.nomClient, o.dateOrdonnance, o.total, " +
                                "GROUP_CONCAT(CONCAT(p.nom, ' (x', l.quantite, ')') SEPARATOR ', ') as produits " +
                                "FROM ordonnance o " +
                                "LEFT JOIN ligneordonnance l ON o.ordonnanceID = l.ordonnanceID " +
                                "LEFT JOIN produit p ON l.produitID = p.produitid " +
                                "GROUP BY o.ordonnanceID " +
                                "ORDER BY o.dateOrdonnance DESC LIMIT 50");
                ResultSet rs = ps.executeQuery()) {
            if (!rs.isBeforeFirst()) {
                Label emptyLabel = new Label("Aucune ordonnance trouvée.");
                emptyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");
                content.getChildren().add(emptyLabel);
            } else {
                while (rs.next()) {
                    VBox ordonnanceBox = new VBox(8);
                    ordonnanceBox.setStyle("-fx-background-color: white; -fx-padding: 15; " +
                            "-fx-background-radius: 8; -fx-border-color: #e5e7eb; " +
                            "-fx-border-radius: 8; -fx-border-width: 1;");
                    HBox headerBox = new HBox(20);
                    headerBox.setAlignment(Pos.CENTER_LEFT);
                    Label idLabel = new Label("Ordonnance N°" + rs.getInt("ordonnanceID"));
                    idLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1f2937;");
                    Label dateLabel = new Label(rs.getTimestamp("dateOrdonnance").toString());
                    dateLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #6b7280;");
                    headerBox.getChildren().addAll(idLabel, dateLabel);
                    Label clientLabel = new Label("Client: " + rs.getString("nomClient"));
                    clientLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #059669;");
                    Label produitsLabel = new Label("Produits: " + rs.getString("produits"));
                    produitsLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #4b5563;");
                    produitsLabel.setWrapText(true);
                    Label totalLabel = new Label(String.format("Total: %.2f DH", rs.getDouble("total")));
                    totalLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #0891b2;");
                    ordonnanceBox.getChildren().addAll(headerBox, clientLabel, produitsLabel, totalLabel);
                    content.getChildren().add(ordonnanceBox);
                }
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement de l'historique : " + e.getMessage(), "ERROR");
            e.printStackTrace();
        }
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);
        scrollPane.setPrefWidth(700);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().setPrefWidth(750);

        dialog.show();
    }
    @FXML
    private void showOrdonnanceDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Créer une Ordonnance");
        dialog.setHeaderText(null);

        TextField clientNameField = new TextField();
        clientNameField.setPromptText("Nom du client");
        clientNameField.setStyle("-fx-pref-width: 400; -fx-font-size: 14px; -fx-padding: 10;");
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        Label clientLabel = new Label("NOM DU CLIENT");
        clientLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        mainContent.getChildren().addAll(clientLabel, clientNameField);
        Label productsLabel = new Label("SÉLECTIONNER LES PRODUITS");
        productsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 10 0 0 0;");
        mainContent.getChildren().add(productsLabel);

        VBox productsBox = new VBox(10);
        productsBox.setStyle("-fx-padding: 10; -fx-background-color: #f3f4f6; -fx-background-radius: 8;");

        List<OrdonnanceItem> ordonnanceItems = new ArrayList<>();

        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT produitid, nom, prix, stock FROM produit WHERE stock > 0 ORDER BY nom");
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int produitId = rs.getInt("produitid");
                String nom = rs.getString("nom");
                double prix = rs.getDouble("prix");
                int stock = rs.getInt("stock");

                HBox productRow = new HBox(10);
                productRow.setAlignment(Pos.CENTER_LEFT);
                productRow.setStyle("-fx-padding: 10; -fx-background-color: white; -fx-background-radius: 5;");
                productRow.setMinHeight(45);

                CheckBox checkBox = new CheckBox();
                checkBox.setStyle("-fx-font-size: 14px;");
                checkBox.setMinWidth(30);

                Label nameLabel = new Label(nom != null ? nom : "Sans nom");
                nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; " +
                        "-fx-text-fill: #1f2937; -fx-min-width: 200; " +
                        "-fx-pref-width: 200; -fx-max-width: 200;");
                nameLabel.setWrapText(false);

                Label priceLabel = new Label(String.format("%.2f DH", prix));
                priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #059669; " +
                        "-fx-font-weight: bold; -fx-min-width: 90; -fx-alignment: center-right;");

                Label stockLabel = new Label("Stock: " + stock);
                stockLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #6b7280; " +
                        "-fx-min-width: 90; -fx-alignment: center;");

                Label qtyLabel = new Label("Qté:");
                qtyLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

                Spinner<Integer> quantitySpinner = new Spinner<>();
                quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, stock, 1));
                quantitySpinner.setEditable(false); 
                quantitySpinner.setPrefWidth(80);
                quantitySpinner.setDisable(true);
                quantitySpinner.setStyle("-fx-font-size: 13px;");

                checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                    quantitySpinner.setDisable(!newVal);
                });

                OrdonnanceItem item = new OrdonnanceItem(produitId, nom, prix, stock, checkBox, quantitySpinner);
                ordonnanceItems.add(item);

                productRow.getChildren().addAll(checkBox, nameLabel, priceLabel, stockLabel,
                        qtyLabel, quantitySpinner);
                productsBox.getChildren().add(productRow);
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement des produits : " + e.getMessage(), "ERROR");
        }
        ScrollPane scrollPane = new ScrollPane(productsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(350);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        mainContent.getChildren().add(scrollPane);
        dialog.getDialogPane().setContent(mainContent);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setPrefWidth(850);
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String clientName = clientNameField.getText().trim();
                if (clientName.isEmpty()) {
                    showAlert("Erreur", "Veuillez entrer le nom du client !", "ERROR");
                    return;
                }
                List<OrdonnanceItem> selectedItems = new ArrayList<>();
                for (OrdonnanceItem item : ordonnanceItems) {
                    if (item.checkBox.isSelected()) {
                        selectedItems.add(item);
                    }
                }
                if (selectedItems.isEmpty()) {
                    showAlert("Erreur", "Veuillez sélectionner au moins un produit !", "ERROR");
                    return;
                }
                for (OrdonnanceItem item : selectedItems) {
                    try {
                        item.quantitySpinner.commitValue();
                    } catch (Exception e) {
                        showAlert("Erreur",
                                String.format("Quantité invalide pour %s ! Veuillez entrer un nombre valide.",
                                        item.nom),
                                "ERROR");
                        return;
                    }
                    int qty = item.quantitySpinner.getValue();
                    if (qty > item.stock) {
                        showAlert("Erreur",
                                String.format(
                                        "Stock insuffisant pour %s !\nStock disponible: %d\nQuantité demandée: %d",
                                        item.nom, item.stock, qty),
                                "ERROR");
                        return;
                    }
                    if (qty <= 0) {
                        showAlert("Erreur",
                                String.format("La quantité pour %s doit être supérieure à 0 !", item.nom),
                                "ERROR");
                        return;
                    }
                }
                createOrdonnance(clientName, selectedItems);
            }
        });
    }
    private void createOrdonnance(String clientName, List<OrdonnanceItem> items) {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                double total = 0;
                for (OrdonnanceItem item : items) {
                    int qty = item.quantitySpinner.getValue();
                    total += item.prix * qty;
                }
                Integer pharmacienId = UserSession.getInstance().getClientId();
                if (pharmacienId == 0) {
                    pharmacienId = null;           
                }
                PreparedStatement psOrd = conn.prepareStatement(
                        "INSERT INTO ordonnance (nomClient, pharmacienID, total) VALUES (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                psOrd.setString(1, clientName);
                if (pharmacienId != null) {
                    psOrd.setInt(2, pharmacienId);
                } else {
                    psOrd.setNull(2, java.sql.Types.INTEGER);
                }
                psOrd.setDouble(3, total);
                psOrd.executeUpdate();
                ResultSet rs = psOrd.getGeneratedKeys();
                int ordonnanceId = 0;
                if (rs.next()) {
                    ordonnanceId = rs.getInt(1);
                }
                PreparedStatement psLigne = conn.prepareStatement(
                        "INSERT INTO ligneordonnance (ordonnanceID, produitID, quantite, prix) VALUES (?, ?, ?, ?)");
                PreparedStatement psStock = conn.prepareStatement(
                        "UPDATE produit SET stock = stock - ? WHERE produitid = ?");

                for (OrdonnanceItem item : items) {
                    int qty = item.quantitySpinner.getValue();
                    // Insert ligne
                    psLigne.setInt(1, ordonnanceId);
                    psLigne.setInt(2, item.produitId);
                    psLigne.setInt(3, qty);
                    psLigne.setDouble(4, item.prix);
                    psLigne.executeUpdate();
                    // Update stock
                    psStock.setInt(1, qty);
                    psStock.setInt(2, item.produitId);
                    psStock.executeUpdate();
                }
                conn.commit();
                showAlert("Succès",
                        String.format(
                                "Ordonnance N°%d créée avec succès pour %s!\nTotal: %.2f DH\n\nL'ordonnance est enregistrée dans la base de données.",
                                ordonnanceId, clientName, total),
                        "INFO");

                loadProduits(); 
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la création de l'ordonnance : " + e.getMessage(), "ERROR");
            e.printStackTrace();
        }
    }
    private static class OrdonnanceItem {
        int produitId;
        String nom;
        double prix;
        int stock;
        CheckBox checkBox;
        Spinner<Integer> quantitySpinner;
        OrdonnanceItem(int produitId, String nom, double prix, int stock,
                CheckBox checkBox, Spinner<Integer> quantitySpinner) {
            this.produitId = produitId;
            this.nom = nom;
            this.prix = prix;
            this.stock = stock;
            this.checkBox = checkBox;
            this.quantitySpinner = quantitySpinner;
        }
    }
}