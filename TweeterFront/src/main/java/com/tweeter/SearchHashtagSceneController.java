package com.tweeter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class SearchHashtagSceneController {

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchTextField;

    @FXML
    private ListView<Tweet> tweetsByHashtagListview;

    @FXML
    private Button backButton;

    private ObservableList<Tweet> tweets = FXCollections.observableArrayList();


    @FXML
    protected void initialize(){
        tweetsByHashtagListview.setCellFactory(
                tweetListView -> new TweetCell()
        );
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
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    Tweeter.getOOS().writeObject("searchHashtag");
                    Tweeter.getOOS().writeObject("#"+searchTextField.getText());
//                    tweets.addAll((ArrayList<Tweet>)Tweeter.getOIS().readObject());
//                    tweetsByHashtagListview.setItems(tweets);
//
                    ArrayList<Tweet> tweets=(ArrayList<Tweet>)Tweeter.getOIS().readObject();
                    tweetsByHashtagListview.getItems().addAll(tweets);
                    tweetsByHashtagListview.setCellFactory(tweetListView -> new TweetCell());
                    ObservableList<Tweet> temp = FXCollections.observableArrayList();
                    temp.addAll(tweets);
                    tweetsByHashtagListview.setItems(temp);
                    tweetsByHashtagListview.setCellFactory(
                            tweetListView -> new TweetCell()
                    );

                }catch (Exception e){
                    throw new RuntimeException();
                }
            }
        });
    }
}
