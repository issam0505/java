package com.example.projetpharmacie;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class ProductCardController {

    @FXML
    private ImageView productImage;

    @FXML
    private Text productName;

    @FXML
    private Text productPrice;

    @FXML
    private Text productStock;

    public void setData(String nom, double prix, int stock, String imagePath) {
        productName.setText(nom);
        productPrice.setText("Prix : " + prix + " DH");
        productStock.setText("Stock : " + stock);
        productImage.setImage(new Image(imagePath));
    }
}
