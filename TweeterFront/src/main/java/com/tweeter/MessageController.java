package com.tweeter;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;

public class MessageController {
    @FXML
    private VBox userItems;
    @FXML private ImageView twitterLogo=new ImageView();
    @FXML private Label userFullName=new Label();
    @FXML private Hyperlink hyperlink=new Hyperlink();
    private boolean isFollowing;
    private User user;

    public void hyperlinkPressed() throws IOException, ClassNotFoundException {
        Tweeter.enterProfilePage();
    }
    public void backButton() throws IOException, ClassNotFoundException {
        Tweeter.enterHomePage();
    }

    private void addUser(User tempUser) {
        HBox hbox = new HBox();
        hbox.setId("userItemTemplate");
        hbox.setPrefHeight(100.0);
        hbox.setPrefWidth(200.0);
        hbox.setSpacing(10.0);
        hbox.getStyleClass().add("user-item");
        hbox.getStylesheets().add("followings.css");
        ImageView imageView=new ImageView();
        try{
            imageView.setImage(new Image(new ByteArrayInputStream(tempUser.getAvatar())));
        } catch (NullPointerException e){
            imageView.setImage(new Image(getClass().getResourceAsStream("/assets/emptyProfile.png")));
        }
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);
        HBox.setMargin(imageView, new Insets(0));

        VBox vbox = new VBox();
        vbox.setPrefHeight(95.0);
        vbox.setPrefWidth(276.0);
        vbox.setSpacing(7.0);

        Hyperlink username = new Hyperlink();
        username.setId("username");
        username.setPrefHeight(50);
        username.setPrefWidth(271.0);
        username.setText(tempUser.getUsername());

        Label fullName = new Label();
        fullName.setId("fullName");
        fullName.setPrefHeight(39.0);
        fullName.setPrefWidth(276.0);
        fullName.setText(tempUser.getFirstName()+" "+tempUser.getLastName());

        vbox.getChildren().addAll(username, fullName);

        Button messageButton = new Button();
        messageButton.setMnemonicParsing(false);
        messageButton.setPrefHeight(60);
        messageButton.setPrefWidth(170);
        messageButton.getStylesheets().add(String.valueOf(getClass().getResource("style.css")));
        messageButton.getStyleClass().add("button2");
        messageButton.setText("Message");
        messageButton.setOnAction(e -> {
            try {
                message(tempUser);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
        username.setOnAction(e -> {
            try {
                Tweeter.enterSelectedProfile(tempUser.getUsername());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        Label bio=new Label(tempUser.getBio());
        bio.wrapTextProperty().setValue(true);
        bio.setStyle("-fx-font:bold 10pt Arial;");
        bio.setPrefHeight(100);
        bio.setPrefWidth(600);

        hbox.getChildren().addAll(imageView, vbox,bio, messageButton);

        userItems.getChildren().add(hbox);
    }

    private void message(User targetUser) throws IOException, ClassNotFoundException {
        Tweeter.enterDirectPage(targetUser);
    }
    public void newDirect() throws IOException {
        Tweeter.enterSearchUserPage();
    }
    public void initialize() throws IOException, ClassNotFoundException {
        user = Tweeter.getClient();
        twitterLogo.setImage(new Image(getClass().getResourceAsStream("/assets/twitterLogo.png")));
        userFullName.setText(user.getFirstName()+" "+user.getLastName());
        hyperlink.setText("@"+user.getUsername());

        for (Direct direct:Tweeter.getClient().getDirects()){
            String username=direct.getUsername();
            ObjectOutputStream OOS=Tweeter.getOOS();
            ObjectInputStream OIS=Tweeter.getOIS();
            OOS.writeObject(new String("incompleteUserFetch"));
            OOS.writeObject(username);
            User tempUser = (User) OIS.readObject();
            addUser(tempUser);
        }
    }

}