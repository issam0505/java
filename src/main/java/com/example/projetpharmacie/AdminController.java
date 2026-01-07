package com.example.projetpharmacie;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class AdminController {

    @FXML
    private Text clientCount;
    @FXML
    private Text produitCount;
    @FXML
    private Text pharmacienCount;
    @FXML
    private Text lowStockCount;

    @FXML
    private VBox topProductsChart;
    @FXML
    private VBox stockAlertsChart;

    @FXML
    private TableView<Client> clientTable;
    @FXML
    private TableColumn<Client, String> clientIdCol;
    @FXML
    private TableColumn<Client, String> clientNomCol;
    @FXML
    private TableColumn<Client, String> clientEmailCol;
    @FXML
    private TableColumn<Client, String> clientTelCol;
    @FXML
    private TableColumn<Client, String> clientActionsCol;

    @FXML
    private TableView<Pharmacien> pharmacienTable;
    @FXML
    private TableColumn<Pharmacien, String> pharmacienIdCol;
    @FXML
    private TableColumn<Pharmacien, String> pharmacienNomCol;
    @FXML
    private TableColumn<Pharmacien, String> pharmacienEmailCol;
    @FXML
    private TableColumn<Pharmacien, String> pharmacienContactCol;
    @FXML
    private TableColumn<Pharmacien, String> pharmacienActionsCol;

    @FXML
    private TableView<Produit> produitTable;
    @FXML
    private TableColumn<Produit, String> produitIdCol;
    @FXML
    private TableColumn<Produit, String> produitNomCol;
    @FXML
    private TableColumn<Produit, String> produitPrixCol;
    @FXML
    private TableColumn<Produit, String> produitStockCol;
    @FXML
    private TableColumn<Produit, String> produitActionsCol;

    @FXML
    public void initialize() {
        loadStatistics();
        loadCharts();
        setupClientTable();
        loadClients();
        setupPharmacienTable();
        loadPharmaciens();
        setupProduitTable();
        loadProduits();
    }

    // ================= LOAD STATISTICS =================
    private void loadStatistics() {
        try (Connection conn = Database.getConnection()) {

            // Clients
            ResultSet rs1 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM client");
            if (rs1.next())
                clientCount.setText(String.valueOf(rs1.getInt(1)));

            // Produits
            ResultSet rs2 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM produit");
            if (rs2.next())
                produitCount.setText(String.valueOf(rs2.getInt(1)));

            // Pharmaciens
            ResultSet rs3 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM pharmacien");
            if (rs3.next())
                pharmacienCount.setText(String.valueOf(rs3.getInt(1)));

            // Stock faible (< 5)
            ResultSet rs4 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM produit WHERE stock < 5");
            if (rs4.next())
                lowStockCount.setText(String.valueOf(rs4.getInt(1)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= LOAD CHARTS =================
    private void loadCharts() {
        loadTopProductsChart();
        loadStockAlertsChart();
    }

    private void loadTopProductsChart() {
        topProductsChart.getChildren().clear();

        try (Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT nom, stock FROM produit ORDER BY stock DESC LIMIT 5")) {

            int maxStock = 0;
            java.util.List<ProductData> products = new java.util.ArrayList<>();

            while (rs.next()) {
                String nom = rs.getString("nom");
                int stock = rs.getInt("stock");
                products.add(new ProductData(nom, stock));
                if (stock > maxStock)
                    maxStock = stock;
            }

            // Créer les barres
            for (ProductData product : products) {
                HBox bar = createBarItem(product.nom, product.stock, maxStock);
                topProductsChart.getChildren().add(bar);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HBox createBarItem(String name, int value, int maxValue) {
        HBox container = new HBox(10);
        container.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(name);
        nameLabel.setPrefWidth(120);
        nameLabel.setStyle("-fx-text-fill: #0f766e; -fx-font-weight: bold; -fx-font-size: 13px;");

        Region bar = new Region();
        double percentage = maxValue > 0 ? ((double) value / maxValue) * 100 : 0;
        bar.setPrefWidth(percentage * 3); // Multiplier pour rendre visible
        bar.setPrefHeight(25);
        bar.setStyle("-fx-background-color: linear-gradient(to right, #14b8a6, #0f766e); " +
                "-fx-background-radius: 5;");

        Label valueLabel = new Label(String.valueOf(value));
        valueLabel.setStyle("-fx-text-fill: #0f766e; -fx-font-weight: bold; -fx-font-size: 13px;");

        container.getChildren().addAll(nameLabel, bar, valueLabel);
        return container;
    }

    private void loadStockAlertsChart() {
        stockAlertsChart.getChildren().clear();

        try (Connection conn = Database.getConnection()) {

            // Compter les produits avec stock < 5 et stock >= 5
            ResultSet rs1 = conn.createStatement().executeQuery(
                    "SELECT COUNT(*) FROM produit WHERE stock < 5");
            int lowStock = rs1.next() ? rs1.getInt(1) : 0;

            ResultSet rs2 = conn.createStatement().executeQuery(
                    "SELECT COUNT(*) FROM produit WHERE stock >= 5");
            int normalStock = rs2.next() ? rs2.getInt(1) : 0;

            // Créer le pie chart simplifié
            VBox pieContainer = createPieChart(lowStock, normalStock);
            stockAlertsChart.getChildren().add(pieContainer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createPieChart(int lowStock, int normalStock) {
        VBox container = new VBox(15);
        container.setAlignment(Pos.CENTER);

        int total = lowStock + normalStock;
        if (total == 0) {
            Label noData = new Label("Aucune donnée");
            noData.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");
            container.getChildren().add(noData);
            return container;
        }

        double lowPercentage = ((double) lowStock / total) * 100;
        double normalPercentage = ((double) normalStock / total) * 100;

        // Cercle simulé avec des labels
        VBox circleBox = new VBox(5);
        circleBox.setAlignment(Pos.CENTER);

        Circle circle = new Circle(60);
        circle.setStyle("-fx-fill: linear-gradient(to bottom, #ef4444, #dc2626);");

        Label centerText = new Label(lowStock + "");
        centerText.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");

        javafx.scene.layout.StackPane circleStack = new javafx.scene.layout.StackPane(circle, centerText);

        circleBox.getChildren().add(circleStack);

        // Légende
        VBox legend = new VBox(8);
        legend.setAlignment(Pos.CENTER);

        HBox lowLegend = new HBox(8);
        lowLegend.setAlignment(Pos.CENTER_LEFT);
        Region lowColor = new Region();
        lowColor.setPrefSize(15, 15);
        lowColor.setStyle("-fx-background-color: #ef4444; -fx-background-radius: 3;");
        Label lowLabel = new Label(String.format("Stock Bas (%.0f%%)", lowPercentage));
        lowLabel.setStyle("-fx-text-fill: #0f766e; -fx-font-size: 12px;");
        lowLegend.getChildren().addAll(lowColor, lowLabel);

        HBox normalLegend = new HBox(8);
        normalLegend.setAlignment(Pos.CENTER_LEFT);
        Region normalColor = new Region();
        normalColor.setPrefSize(15, 15);
        normalColor.setStyle("-fx-background-color: #fbbf24; -fx-background-radius: 3;");
        Label normalLabel = new Label(String.format("Stock Normal (%.0f%%)", normalPercentage));
        normalLabel.setStyle("-fx-text-fill: #0f766e; -fx-font-size: 12px;");
        normalLegend.getChildren().addAll(normalColor, normalLabel);

        legend.getChildren().addAll(lowLegend, normalLegend);

        container.getChildren().addAll(circleBox, legend);
        return container;
    }

    // Classe pour stocker les données produit
    private static class ProductData {
        String nom;
        int stock;

        ProductData(String nom, int stock) {
            this.nom = nom;
            this.stock = stock;
        }
    }

    private void setupClientTable() {
        clientIdCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
        clientNomCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNom()));
        clientEmailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        clientTelCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTelephone()));

        clientActionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Supprimer");
            {
                deleteBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; " +
                        "-fx-background-radius: 8; -fx-padding: 8 15; -fx-cursor: hand; " +
                        "-fx-font-size: 13px;");
                deleteBtn.setOnAction(e -> {
                    Client client = getTableView().getItems().get(getIndex());
                    deleteClient(client);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(10, deleteBtn);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });
    }

    // ================= LOAD CLIENTS =================
    private void loadClients() {
        ObservableList<Client> clients = FXCollections.observableArrayList();

        try (Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM client")) {

            while (rs.next()) {
                clients.add(new Client(
                        rs.getInt("clientID"),
                        rs.getString("nom"),
                        rs.getString("email"),
                        rs.getString("telephone"),
                        rs.getString("motpasse")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        clientTable.setItems(clients);
    }

    // ================= ADD CLIENT =================
    @FXML
    private void showAddClient() {
        // Créer une Dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un Client");
        dialog.setHeaderText(null);

        // Créer les champs
        TextField nomField = new TextField();
        nomField.setPromptText("Nom complet");
        nomField.setStyle("-fx-pref-width: 300;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField telField = new TextField();
        telField.setPromptText("Téléphone");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Mot de passe");

        // Tous les champs avec le style admin-input
        nomField.getStyleClass().add("admin-input");
        emailField.getStyleClass().add("admin-input");
        telField.getStyleClass().add("admin-input");
        passField.getStyleClass().add("admin-input");

        // Layout
        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(15);
        content.getChildren().addAll(
                new Label("NOM COMPLET"),
                nomField,
                new Label("EMAIL"),
                emailField,
                new Label("TÉLÉPHONE"),
                telField,
                new Label("MOT DE PASSE"),
                passField);
        content.setPadding(new javafx.geometry.Insets(20));
        content.getStyleClass().add("dialog-container");

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Validation et ajout
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String nom = nomField.getText().trim();
                String email = emailField.getText().trim();
                String tel = telField.getText().trim();
                String pass = passField.getText().trim();

                // Validation
                if (nom.isEmpty() || email.isEmpty() || tel.isEmpty() || pass.isEmpty()) {
                    showAlert("Erreur", "Veuillez remplir tous les champs !", "ERROR");
                    return;
                }

                if (!email.contains("@")) {
                    showAlert("Erreur", "Email invalide !", "ERROR");
                    return;
                }

                // Ajout en base
                try (Connection conn = Database.getConnection();
                        PreparedStatement ps = conn.prepareStatement(
                                "INSERT INTO client (nom, email, telephone, motpasse) VALUES (?, ?, ?, ?)")) {

                    ps.setString(1, nom);
                    ps.setString(2, email);
                    ps.setString(3, tel);
                    ps.setString(4, pass);
                    ps.executeUpdate();

                    showAlert("Succès", "Client ajouté avec succès !", "INFO");
                    loadClients();
                    loadStatistics();

                } catch (Exception e) {
                    showAlert("Erreur", "Erreur lors de l'ajout : " + e.getMessage(), "ERROR");
                }
            }
        });
    }

    // ================= DELETE CLIENT =================
    private void deleteClient(Client client) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer le client " + client.getNom() + " ?");
        confirm.setContentText("Cette action est irréversible.");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try (Connection conn = Database.getConnection();
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM client WHERE clientID = ?")) {

                ps.setInt(1, client.getId());
                ps.executeUpdate();

                showAlert("Succès", "Client supprimé !", "INFO");
                loadClients();
                loadStatistics();

            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la suppression : " + e.getMessage(), "ERROR");
            }
        }
    }

    private void showAlert(String title, String message, String type) {
        Alert alert = type.equals("ERROR") ? new Alert(Alert.AlertType.ERROR) : new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
    
    @FXML
    private void handleLogout() {
        try {
            UserSession.getInstance().clear();

            Stage stage = (Stage) clientTable.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("home-view.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    private void setupPharmacienTable() {
        pharmacienIdCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
        pharmacienNomCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNom()));
        pharmacienEmailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        pharmacienContactCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getContact()));

        
        pharmacienActionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Supprimer");
            {
                deleteBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; " +
                        "-fx-background-radius: 8; -fx-padding: 8 15; -fx-cursor: hand; " +
                        "-fx-font-size: 13px;");
                deleteBtn.setOnAction(e -> {
                    Pharmacien pharmacien = getTableView().getItems().get(getIndex());
                    deletePharmacien(pharmacien);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(10, deleteBtn);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });
    }

    private void loadPharmaciens() {
        ObservableList<Pharmacien> pharmaciens = FXCollections.observableArrayList();

        try (Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM pharmacien")) {

            while (rs.next()) {
                pharmaciens.add(new Pharmacien(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("email"),
                        rs.getString("contact"),
                        rs.getString("motpasse")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        pharmacienTable.setItems(pharmaciens);
    }
    @FXML
    private void showAddPharmacien() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un Pharmacien");
        dialog.setHeaderText(null);

        TextField nomField = new TextField();
        nomField.setPromptText("Nom complet");
        nomField.setStyle("-fx-pref-width: 300;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField contactField = new TextField();
        contactField.setPromptText("Contact");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Mot de passe");

        nomField.getStyleClass().add("admin-input");
        emailField.getStyleClass().add("admin-input");
        contactField.getStyleClass().add("admin-input");
        passField.getStyleClass().add("admin-input");

        VBox content = new VBox(15);
        content.getChildren().addAll(
                new Label("NOM COMPLET"),
                nomField,
                new Label("EMAIL"),
                emailField,
                new Label("CONTACT"),
                contactField,
                new Label("MOT DE PASSE"),
                passField);
        content.setPadding(new javafx.geometry.Insets(20));
        content.getStyleClass().add("dialog-container");

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String nom = nomField.getText().trim();
                String email = emailField.getText().trim();
                String contact = contactField.getText().trim();
                String pass = passField.getText().trim();

                if (nom.isEmpty() || email.isEmpty() || contact.isEmpty() || pass.isEmpty()) {
                    showAlert("Erreur", "Veuillez remplir tous les champs !", "ERROR");
                    return;
                }

                if (!email.contains("@")) {
                    showAlert("Erreur", "Email invalide !", "ERROR");
                    return;
                }

                try (Connection conn = Database.getConnection();
                        PreparedStatement ps = conn.prepareStatement(
                                "INSERT INTO pharmacien (nom, email, contact, motpasse) VALUES (?, ?, ?, ?)")) {

                    ps.setString(1, nom);
                    ps.setString(2, email);
                    ps.setString(3, contact);
                    ps.setString(4, pass);
                    ps.executeUpdate();

                    showAlert("Succès", "Pharmacien ajouté avec succès !", "INFO");
                    loadPharmaciens();
                    loadStatistics();

                } catch (Exception e) {
                    showAlert("Erreur", "Erreur lors de l'ajout : " + e.getMessage(), "ERROR");
                }
            }
        });
    }
    private void deletePharmacien(Pharmacien pharmacien) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer le pharmacien " + pharmacien.getNom() + " ?");
        confirm.setContentText("Cette action est irréversible.");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try (Connection conn = Database.getConnection();
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM pharmacien WHERE id = ?")) {

                ps.setInt(1, pharmacien.getId());
                ps.executeUpdate();

                showAlert("Succès", "Pharmacien supprimé !", "INFO");
                loadPharmaciens();
                loadStatistics();

            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la suppression : " + e.getMessage(), "ERROR");
            }
        }
    }
    public static class Client {
        private final int id;
        private final String nom;
        private final String email;
        private final String telephone;
        private final String motpasse;

        public Client(int id, String nom, String email, String telephone, String motpasse) {
            this.id = id;
            this.nom = nom;
            this.email = email;
            this.telephone = telephone;
            this.motpasse = motpasse;
        }

        public int getId() {
            return id;
        }

        public String getNom() {
            return nom;
        }

        public String getEmail() {
            return email;
        }

        public String getTelephone() {
            return telephone;
        }

        public String getMotpasse() {
            return motpasse;
        }
    }
    public static class Pharmacien {
        private final int id;
        private final String nom;
        private final String email;
        private final String contact;
        private final String motpasse;

        public Pharmacien(int id, String nom, String email, String contact, String motpasse) {
            this.id = id;
            this.nom = nom;
            this.email = email;
            this.contact = contact;
            this.motpasse = motpasse;
        }

        public int getId() {
            return id;
        }

        public String getNom() {
            return nom;
        }

        public String getEmail() {
            return email;
        }

        public String getContact() {
            return contact;
        }

        public String getMotpasse() {
            return motpasse;
        }
    }

    // ================= PRODUIT CLASS =================
    public static class Produit {
        private final int id;
        private final String nom;
        private final double prix;
        private final int stock;

        public Produit(int id, String nom, double prix, int stock) {
            this.id = id;
            this.nom = nom;
            this.prix = prix;
            this.stock = stock;
        }

        public int getId() {
            return id;
        }

        public String getNom() {
            return nom;
        }

        public double getPrix() {
            return prix;
        }

        public int getStock() {
            return stock;
        }
    }

    // ================= PRODUIT TABLE SETUP =================
    private void setupProduitTable() {
        produitIdCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
        produitNomCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNom()));
        produitPrixCol.setCellValueFactory(
                data -> new SimpleStringProperty(String.format("%.2f", data.getValue().getPrix())));
        produitStockCol
                .setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getStock())));
        produitActionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button addStockBtn = new Button("Ajouter Stock");
            {
                addStockBtn.setStyle("-fx-background-color: #14b8a6; -fx-text-fill: white; " +
                        "-fx-background-radius: 8; -fx-padding: 8 15; -fx-cursor: hand; " +
                        "-fx-font-size: 12px; -fx-font-weight: bold;");
                addStockBtn.setOnAction(e -> {
                    Produit produit = getTableView().getItems().get(getIndex());
                    showAddStockDialog(produit);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(10, addStockBtn);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });
    }

    // ================= LOAD PRODUITS =================
    private void loadProduits() {
        ObservableList<Produit> produits = FXCollections.observableArrayList();

        try (Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT produitid, nom, prix, stock FROM produit")) {

            while (rs.next()) {
                produits.add(new Produit(
                        rs.getInt("produitid"),
                        rs.getString("nom"),
                        rs.getDouble("prix"),
                        rs.getInt("stock")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        produitTable.setItems(produits);
    }

    private void showAddStockDialog(Produit produit) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Ajouter au Stock");
        dialog.setHeaderText(null);

        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantité à ajouter");
        quantityField.setStyle("-fx-pref-width: 300;");
        quantityField.getStyleClass().add("admin-input");

        VBox content = new VBox(15);
        content.getChildren().addAll(
                new Label("PRODUIT: " + produit.getNom()),
                new Label("STOCK ACTUEL: " + produit.getStock()),
                new Label("QUANTITÉ À AJOUTER"),
                quantityField);
        content.setPadding(new javafx.geometry.Insets(20));
        content.getStyleClass().add("dialog-container");

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String qtyText = quantityField.getText().trim();

                if (qtyText.isEmpty()) {
                    showAlert("Erreur", "Veuillez entrer une quantité !", "ERROR");
                    return;
                }

                try {
                    int quantityToAdd = Integer.parseInt(qtyText);

                    if (quantityToAdd <= 0) {
                        showAlert("Erreur", "La quantité doit être supérieure à 0 !", "ERROR");
                        return;
                    }

                    int newStock = produit.getStock() + quantityToAdd;

                    try (Connection conn = Database.getConnection();
                            PreparedStatement ps = conn.prepareStatement(
                                    "UPDATE produit SET stock = ? WHERE produitid = ?")) {

                        ps.setInt(1, newStock);
                        ps.setInt(2, produit.getId());
                        ps.executeUpdate();

                        showAlert("Succès", quantityToAdd + " unités ajoutées au stock de " +
                                produit.getNom() + " !", "INFO");
                        loadProduits();
                        loadStatistics();
                        loadCharts();

                    }

                } catch (NumberFormatException e) {
                    showAlert("Erreur", "Quantité invalide !", "ERROR");
                } catch (Exception e) {
                    showAlert("Erreur", "Erreur lors de l'ajout : " + e.getMessage(), "ERROR");
                }
            }
        });
    }
    @FXML
    private void exportProduitsToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter les produits");
        fileChooser.setInitialFileName("produits.csv");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));
        File file = fileChooser.showSaveDialog(produitTable.getScene().getWindow());

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                // Write header
                writer.println("ID,Nom,Prix,Stock,Images");
                try (Connection conn = Database.getConnection();
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT produitid, nom, prix, stock, images FROM produit")) {

                    while (rs.next()) {
                        int id = rs.getInt("produitid");
                        String nom = rs.getString("nom");
                        double prix = rs.getDouble("prix");
                        int stock = rs.getInt("stock");
                        String images = rs.getString("images");
                        nom = nom.replace("\"", "\"\"");
                        if (images != null) {
                            images = images.replace("\"", "\"\"");
                        } else {
                            images = "";
                        }

                        writer.printf("%d,\"%s\",%.2f,%d,\"%s\"%n",
                                id, nom, prix, stock, images);
                    }

                    showAlert("Succès", "Export réussi ! " + file.getName(), "INFO");

                } catch (Exception e) {
                    showAlert("Erreur", "Erreur lors de la lecture des données : " + e.getMessage(), "ERROR");
                }

            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de l'export : " + e.getMessage(), "ERROR");
            }
        }
    }
}
