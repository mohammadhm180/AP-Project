package com.tweeter;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class HomeSceneController {

    @FXML
    private Button HomeButton;

    @FXML
    private Button directButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button searchHashtagButton;

    @FXML
    private Button searchUserButton;

    @FXML
    private TextField homeTextField;

    @FXML
    private ImageView iconImage;

    @FXML
    private Button profileButton;

    @FXML
    private ListView<Tweet> tweetList;

    public void addTweet() throws IOException {
        Tweeter.enterAddTweetPage();
    }
    @FXML
    public void initialize() throws IOException {
        Image image = new Image(getClass().getResourceAsStream("/assets/twitterLogo.png"));
        iconImage.setImage(image);
        homeTextField.setEditable(false);
        homeTextField.setText("Home");

        HomeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    Tweeter.enterHomePage();
                } catch (Exception e) {
                    try {
                        Tweeter.enterLostConnection();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        directButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    Tweeter.enterMessages();
                } catch (IOException e) {
                    try {
                        Tweeter.enterLostConnection();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        profileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    Tweeter.enterProfilePage();
                } catch (IOException e) {
                    try {
                        Tweeter.enterLostConnection();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }

            }
        });
        searchUserButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    Tweeter.enterSearchUserPage();
                } catch (IOException e) {
                    try {
                        Tweeter.enterLostConnection();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        searchHashtagButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    Tweeter.enterSearchHashtagPage();
                } catch (IOException e) {
                    try {
                        Tweeter.enterLostConnection();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }

            }
        });
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Tweeter.getStage().close();
            }
        });

        //retrieving tweets for timeline
        ArrayList<Tweet> timeline = new ArrayList<>();
        try {
            Tweeter.getOOS().writeObject("getFavstars");
            timeline = (ArrayList<Tweet>) Tweeter.getOIS().readObject();
        } catch (Exception e) {
            Tweeter.enterLostConnection();
        }
        for (User user : Tweeter.getClient().getFollowings()) {
            timeline.addAll(user.getTweets());
        }

        ObservableList<Tweet> temp = FXCollections.observableArrayList();
        temp.addAll(timeline);
        tweetList.setItems(temp);

        tweetList.setCellFactory(
                tweetListView -> new TweetCell()
        );
    }
}

