package com.example.projetpharmacie;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Store {

  private Scene scene;

  public Store(Stage stage) {
    try {
      FXMLLoader loader = new FXMLLoader(
              getClass().getResource("/com/example/projetpharmacie/Store.fxml")
      );
      AnchorPane root = loader.load();
      scene = new Scene(root, stage.getWidth(), stage.getHeight());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Scene getScene() {
    return scene;
  }
}
