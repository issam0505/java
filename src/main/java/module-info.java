module com.example.projetpharmacie {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens com.example.projetpharmacie to javafx.fxml;
    exports com.example.projetpharmacie;
}