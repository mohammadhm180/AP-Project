module com.example.tweeterfront {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;
    requires javafx.web;


    opens com.tweeter to javafx.fxml;
    exports com.tweeter;
}