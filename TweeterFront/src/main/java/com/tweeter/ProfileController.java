package com.tweeter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ProfileController {
    private User user;

    @FXML private Label fullNameLabel;
    @FXML private Label usernameLabel;
    @FXML private Label bioLabel;
    @FXML private Label locationLabel; ;
    @FXML private Hyperlink websiteHyperlink;
    @FXML private  Label followingNumberLabel;
    @FXML private Label followerNumberLabel ;
    @FXML private Label tweetNumberLabel;
    @FXML private ImageView mapImageView;
    @FXML private ImageView avatarImageView;
    @FXML private ImageView headerImageView;
    @FXML private ImageView linkImageView;
    @FXML private ImageView iconImage;
    @FXML
    private ListView<Tweet> tweetList;


    @FXML
    public void editSelected() throws IOException {
        Tweeter.enterEditPage();
    }
    @FXML
    public void homeSelected() throws IOException {
        Tweeter.enterHomePage();
    }
    @FXML
    public void messagesSelected() throws IOException {
        Tweeter.enterMessages();
    }
    @FXML
    public void exitSelected(){
        Tweeter.getStage().close();
    }

    public void hyperlinkPressed(){

    }
    public void showFollowers() throws IOException {
        Tweeter.enterFollowersPage(Tweeter.getClient());
    }
    public void showFollowings() throws IOException {
        Tweeter.enterFollowingsPage(Tweeter.getClient());
    }
    public void searchUser() throws IOException {
        Tweeter.enterSearchUserPage();
    }
    public void searchHashtag() throws IOException {
        Tweeter.enterSearchHashtagPage();
    }

    public void addTweet() throws IOException {
        Tweeter.enterAddTweetPage();
    }
    public void showTweets() throws IOException {
        Tweeter.enterShowProfileTweets(user);
    }
    public void initialize() {
        user=Tweeter.getClient();
        iconImage.setImage(new Image(getClass().getResourceAsStream("/assets/twitterLogo.png")));
        linkImageView.setImage(new Image(getClass().getResourceAsStream("/assets/linkIcon.png")));
        mapImageView.setImage(new Image(getClass().getResourceAsStream("/assets/locationIcon.png")));
        try{
            avatarImageView.setImage(new Image(new ByteArrayInputStream(user.getAvatar())));
        } catch (NullPointerException e){
            avatarImageView.setImage(new Image(getClass().getResourceAsStream("/assets/emptyProfile.png")));        }
        try{
            headerImageView.setImage(new Image(new ByteArrayInputStream(user.getHeader())));
        } catch (NullPointerException e){
            headerImageView.setImage(new Image(getClass().getResourceAsStream("/assets/emptyHeader3.png")));
        }
        fullNameLabel.setText(user.getFirstName()+" "+user.getLastName());
        usernameLabel.setText("@"+user.getUsername());
        if(user.getBio()==null){
            bioLabel.setText("No bio has been set");
        } else if(user.getBio().equals("")){
            bioLabel.setText("No bio has been set");
        } else {
            bioLabel.setText(user.getBio());
        }
        if(user.getLocation()==null){
            locationLabel.setText("No Location has been set");
        } else if(user.getLocation().equals("")){
            locationLabel.setText("No Location has been set");
        } else {
            locationLabel.setText(user.getLocation());
        }
        if(user.getWebAddress()==null){
            websiteHyperlink.setText("No Website has been set");
        } else if(user.getWebAddress().equals("")){
            websiteHyperlink.setText("No Website has been set");
        } else {
            websiteHyperlink.setText(user.getWebAddress());
        }
        followerNumberLabel.setText(""+user.getFollowers().size());
        followingNumberLabel.setText(""+user.getFollowings().size());
        tweetNumberLabel.setText(""+user.getTweets().size());

        tweetList.getItems().addAll(user.getTweets());
        tweetList.setCellFactory(tweetListView -> new TweetInProfileCell());
        ObservableList<Tweet> temp = FXCollections.observableArrayList();
        temp.addAll(user.getTweets());
        tweetList.setItems(temp);
        tweetList.setCellFactory(
                tweetListView -> new TweetInProfileCell()
        );
    }
}
