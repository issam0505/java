package com.example.projetpharmacie;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
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
    @FXML
    private Label newBadge;
    @FXML
    private Label outStockBadge;

    public void setData(String nom, double prix, int stock,
            String imagePath, boolean isNew) {

        productName.setText(nom);
        productPrice.setText(prix + " DH");
        productStock.setText("Stock : " + stock);
        productImage.setImage(new Image(imagePath));

        // Badge NEW
        newBadge.setVisible(isNew);

        // 👉 ÉTAPE 3 : ACTIVER OUT OF STOCK
        if (stock == 0) {
            outStockBadge.setVisible(true);
        } else {
            outStockBadge.setVisible(false);
        }
    }

}
