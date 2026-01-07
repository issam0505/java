package com.example.projetpharmacie;

import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.List;

public class CartManager {

    private static CartManager instance;
    private final List<CartItem> cartItems = new ArrayList<>();

    private CartManager() {}

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addProduct(Image image, String name, double price, int quantity,int getProduitId,int stock) {
        for (CartItem item : cartItems) {
            if (item.getName().equals(name)) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        cartItems.add(new CartItem(image, name, price, quantity, getProduitId,stock));
    }

    public void removeProduct(CartItem item) {
        cartItems.remove(item);
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public double getTotal() {
        return cartItems.stream().mapToDouble(CartItem::getTotalPrice).sum();
    }

    public static class CartItem {
        private Image image;
        private String name;
        private double price;
        private int quantity;
        private int idproduit;
        private int stock;
        public CartItem(Image image, String name, double price, int quantity, int idproduit, int stock) {
            this.image = image;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
            this.idproduit = idproduit;
            this.stock = stock;
        }

        public Image getImage() { return image; }
        public String getName() { return name; }
        public double getPrice() { return price; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public double getTotalPrice() { return price * quantity; }
        public int getIdproduit() { return idproduit; }
        public int getStock() { return stock; }
    }
}
