package com.tweeter;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.MediaView;

import java.io.IOException;

public class TweetSceneController {

    @FXML
    private Button backButton;

    @FXML
    private ListView<Tweet> tweetShower;

    private Tweet tweet;
    private ObservableList<Tweet> tweetAsList = FXCollections.observableArrayList();

    @FXML
    protected void initialize(){
        if(tweet != null){
            tweetShower.setCellFactory(
                    tweetListView -> new TweetCell()
            );
            tweetAsList.add(tweet);
            tweetShower.setItems(tweetAsList);
        }
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    Tweeter.enterHomePage();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    public void setTweet(Tweet tweet){
        this.tweet = tweet;
        initialize();
    }
}