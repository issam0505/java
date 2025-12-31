package com.example.projetpharmacie;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Text errorLabel;
    @FXML
    private VBox loginCard;

    private String email;
    private String password;

    // ===== ANIMATION ONLY =====
    @FXML
    public void initialize() {
        if (loginCard == null) return;

        loginCard.setOpacity(0);
        loginCard.setTranslateY(120);
        loginCard.setScaleX(0.88);
        loginCard.setScaleY(0.88);

        FadeTransition fade =
                new FadeTransition(Duration.millis(1500), loginCard);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide =
                new TranslateTransition(Duration.millis(1500), loginCard);
        slide.setFromY(120);
        slide.setToY(0);

        ScaleTransition scale =
                new ScaleTransition(Duration.millis(1500), loginCard);
        scale.setFromX(0.88);
        scale.setFromY(0.88);
        scale.setToX(1);
        scale.setToY(1);

        fade.play();
        slide.play();
        scale.play();
    }
    @FXML
    private void goToStore(ActionEvent event) {
        try {
            email = usernameField.getText();
            password = passwordField.getText();
            errorLabel.setText("");

            if (email.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Veuillez remplir tous les champs ❌");
                return;
            }

            try (Connection conn = Database.getConnection()) {
                if (conn != null) {
                    String query1 = "SELECT * FROM client WHERE email=? AND motpasse=?";
                    String query2 = "SELECT * FROM admin WHERE email=? AND motpasse=?";
                    String query3 = "SELECT * FROM Pharmacien WHERE email=? AND motpasse=?";

                    PreparedStatement pst1 = conn.prepareStatement(query1);
                    PreparedStatement pst2 = conn.prepareStatement(query2);
                    PreparedStatement pst3 = conn.prepareStatement(query3);

                    pst1.setString(1, email);
                    pst1.setString(2, password);
                    pst2.setString(1, email);
                    pst2.setString(2, password);
                    pst3.setString(1, email);
                    pst3.setString(2, password);

                    ResultSet rs1 = pst1.executeQuery();
                    ResultSet rs2 = pst2.executeQuery();
                    ResultSet rs3 = pst3.executeQuery();

                    if (rs1.next()) {

                        Stage stage = (Stage) ((Node) event.getSource())
                                .getScene().getWindow();

                        FXMLLoader loader = new FXMLLoader(
                                getClass().getResource("/com/example/projetpharmacie/store.fxml")
                        );

                        Scene scene = new Scene(loader.load(),
                                stage.getWidth(),
                                stage.getHeight());

                        stage.setScene(scene);
                        stage.setMaximized(true);

                    } else if (rs2.next()) {

                        Stage stage = (Stage) ((Node) event.getSource())
                                .getScene().getWindow();

                        FXMLLoader loader = new FXMLLoader(
                                getClass().getResource("/com/example/projetpharmacie/Admin.fxml")
                        );

                        Scene scene = new Scene(loader.load(),
                                stage.getWidth(),
                                stage.getHeight());

                        stage.setScene(scene);
                        stage.setMaximized(true);
                        stage.setTitle("SwiftCare/Admin");

                    } else if (rs3.next()) {

                        Stage stage = (Stage) ((Node) event.getSource())
                                .getScene().getWindow();

                        FXMLLoader loader = new FXMLLoader(
                                getClass().getResource("/com/example/projetpharmacie/store.fxml")
                        );

                        Scene scene = new Scene(loader.load(),
                                stage.getWidth(),
                                stage.getHeight());

                        stage.setScene(scene);
                        stage.setTitle("SwiftCare/Store");
                        stage.setMaximized(true);

                    } else {
                        errorLabel.setText("Email ou mot de passe incorrect ❌");
                    }

                } else {
                    errorLabel.setText("Erreur connexion DB");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToSign(ActionEvent event) {
        try {
            Stage stage =
                    (Stage) ((Node) event.getSource()).getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/projetpharmacie/Sign.fxml")
            );

            Scene scene = new Scene(loader.load(),
                    stage.getWidth(),
                    stage.getHeight());

            stage.setScene(scene);
            stage.setTitle("SwiftCare/Sign");
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
