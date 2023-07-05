package com.tweeter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
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

public class FollowersController {
    @FXML
    private VBox userItems;
    @FXML private ImageView twitterLogo=new ImageView();
    @FXML private Label userFullName=new Label();
    @FXML private Hyperlink hyperlink=new Hyperlink();
    private boolean isFollowing;
    private User user;

    public void hyperlinkPressed() throws IOException, ClassNotFoundException {
        if(user.getUsername().equals(Tweeter.getClient().getUsername())){
            Tweeter.enterProfilePage();
        } else {
            Tweeter.enterSelectedProfile(user.getUsername());
        }
    }
    public void backButton() throws IOException, ClassNotFoundException {
        if(user.getUsername().equals(Tweeter.getClient().getUsername())){
            Tweeter.enterProfilePage();
        } else {
            Tweeter.enterSelectedProfile(user.getUsername());
        }
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

        Button followButton = new Button();
        followButton.setMnemonicParsing(false);
        followButton.setPrefHeight(60);
        followButton.setPrefWidth(170);
        followButton.getStylesheets().add(String.valueOf(getClass().getResource("style.css")));
        isFollowing=false;
        for(User user1:Tweeter.getClient().getFollowings()){
            if(user1.getUsername().equals(tempUser.getUsername())){
                isFollowing=true;
                break;
            }
        }
        followButton.getStyleClass().clear();
        if(isFollowing){
            followButton.getStyleClass().add("button3");
            followButton.setText("Following");
        } else {
            followButton.getStyleClass().add("button2");
            followButton.setText("Follow");
        }
        if(tempUser.getUsername().equals(Tweeter.getClient().getUsername())){
            followButton.visibleProperty().setValue(false);
        } else {
            followButton.setOnAction(e -> followUser(tempUser.getUsername(),followButton));
        }
        if(tempUser.getUsername().equals(Tweeter.getClient().getUsername())) {
            followButton.visibleProperty().setValue(false);
        } else {
            followButton.setOnAction(e -> followUser(tempUser.getUsername(),followButton));
        }
        username.setOnAction(e -> {
            if(tempUser.getUsername().equals(Tweeter.getClient().getUsername())){
                try {
                    Tweeter.enterProfilePage();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                try {
                    Tweeter.enterSelectedProfile(tempUser.getUsername());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        Label bio=new Label(tempUser.getBio());
        bio.wrapTextProperty().setValue(true);
        bio.setStyle("-fx-font:bold 10pt Arial;");
        bio.setPrefHeight(100);
        bio.setPrefWidth(600);

        hbox.getChildren().addAll(imageView, vbox,bio, followButton);

        userItems.getChildren().add(hbox);
    }

    private void followUser(String username,Button followButton) {
        //here isFollowing is after clicking follow button
        isFollowing=true;
        for (User user:Tweeter.getClient().getFollowings()){
            if(user.getUsername().equals(username)){
                isFollowing=false;
                break;
            }
        }
        if(isFollowing){
            ObjectOutputStream OOS=Tweeter.getOOS();
            ObjectInputStream OIS=Tweeter.getOIS();
            try {
                followButton.getStyleClass().clear();
                followButton.getStyleClass().add("button3");
                followButton.setText("Following");
                String command = "follow";
                OOS.writeObject(command);
                OOS.writeObject(Tweeter.getClient().getUsername());
                OOS.writeObject(username);
                //adding to user followings
                OOS.writeObject(new String("fetchUser"));
                OOS.writeObject(username);
                User following = (User) OIS.readObject();
                Tweeter.getClient().getFollowings().add(following);
            } catch (IOException | ClassNotFoundException e){
                throw new RuntimeException();
            }
        } else {
            try{
                String command = "unfollow";
                ObjectOutputStream OOS=Tweeter.getOOS();
                OOS.writeObject(command);
                OOS.writeObject(Tweeter.getClient().getUsername());
                OOS.writeObject(username);
                followButton.getStyleClass().clear();
                followButton.getStyleClass().add("button2");
                followButton.setText("Follow");
                //remove from users following
                Iterator<User> following = Tweeter.getClient().getFollowings().iterator();
                while (following.hasNext()) {
                    if (following.next().getUsername().equals(username)) {
                        following.remove();
                        break;
                    }
                }
            } catch (IOException e){
                throw new RuntimeException();
            }
        }
    }
    public void setUser(User user){
        this.user = user;
        //initialize
        twitterLogo.setImage(new Image(getClass().getResourceAsStream("/assets/twitterLogo.png")));
        userFullName.setText(user.getFirstName()+" "+user.getLastName());
        hyperlink.setText("@"+user.getUsername());
        for(User user1:user.getFollowers()){
            User tempUser=new User(user1.getUsername(),user1.getFirstName(),user1.getLastName(),null,null,null,null,null,null);
            tempUser.setAvatar(user1.getAvatar());
            tempUser.setBio(user1.getBio());
            addUser(tempUser);
        }
    }
}