package com.tweeter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.io.IOException;

public class ShowProfileTweetsController {
    private User profileUser;

    @FXML
    private ListView<Tweet> tweetList;

    public void setProfileUser(User profileUser) {
        this.profileUser = profileUser;
        initializePage();
    }
    public void back() throws IOException, ClassNotFoundException {
        if(profileUser.getUsername().equals(Tweeter.getClient().getUsername())){
            Tweeter.enterProfilePage();
        }else {
            Tweeter.enterSelectedProfile(profileUser.getUsername());
        }
    }

    public void initializePage() {
        tweetList.getItems().addAll(profileUser.getTweets());
        tweetList.setCellFactory(tweetListView -> new TweetCell());
        ObservableList<Tweet> temp = FXCollections.observableArrayList();
        temp.addAll(profileUser.getTweets());
        tweetList.setItems(temp);
        if(profileUser.getUsername().equals(Tweeter.getClient().getUsername())){
            tweetList.setCellFactory(
                    tweetListView -> new TweetInProfileCell()
            );
        } else {
            tweetList.setCellFactory(
                    tweetListView -> new TweetCell()
            );
        }

    }
}
