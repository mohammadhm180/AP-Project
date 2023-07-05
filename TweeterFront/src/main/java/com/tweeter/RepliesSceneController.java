package com.tweeter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;

public class RepliesSceneController {

    private Tweet tweet;

    private ObservableList<Tweet> replies = FXCollections.observableArrayList();

    @FXML
    private Button addReplyButton;

    @FXML
    private Button backButton;

    @FXML
    private ListView<Tweet> repliesListView;

    @FXML
    private TextField titleTextField;


    @FXML
    protected void initialize(){
        repliesListView.setCellFactory(
                tweetListView -> new TweetCell()
        );
        if(tweet != null){
            replies.addAll(tweet.getReplies());
            repliesListView.setItems(replies);
        }
        addReplyButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    Tweeter.enterAddQuoteReplyPage(tweet.getTweetID(),false);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
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
