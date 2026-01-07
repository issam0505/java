package com.example.projetpharmacie;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.animation.FadeTransition;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProfileController {

    @FXML private Label lblNom;
    @FXML private Label lblEmail;
    @FXML private Label lblTelephone;
    @FXML private GridPane profileForm;

    @FXML
    public void initialize() {
        loadUserProfile();
        playFormAnimation();
    }

    private void loadUserProfile() {
        try {
            int clientId = UserSession.getInstance().getClientId();

            Connection conn = Database.getConnection();
            PreparedStatement pst = conn.prepareStatement(
                    "SELECT nom, email, telephone FROM client WHERE clientID=?"
            );
            pst.setInt(1, clientId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                lblNom.setText(rs.getString("nom"));
                lblEmail.setText(rs.getString("email"));
                lblTelephone.setText(rs.getString("telephone"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playFormAnimation() {
        profileForm.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(600), profileForm);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    // 🔙 BACK BUTTON
    @FXML
    private void goBack() {
        try {
            Stage stage = (Stage) profileForm.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/projetpharmacie/store.fxml")
            );

            Parent root = loader.load();
            Scene scene = new Scene(
                    root,
                    stage.getWidth(),
                    stage.getHeight()
            );

            stage.setScene(scene);
            stage.setMaximized(true); // باش تبقى كيفما كانت
            stage.setTitle("SwiftCare / Store");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
