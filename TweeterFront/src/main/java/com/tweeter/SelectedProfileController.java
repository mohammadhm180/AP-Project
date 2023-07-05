package com.tweeter;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;

import javafx.scene.web.WebView;


import javafx.stage.Stage;

public class SelectedProfileController {
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
    private Button blockButton;
    @FXML
    private Button followButton;
    private boolean isFollowing=false;
    private boolean isBlocked=false;

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

    public void addTweet() throws IOException {
        Tweeter.enterAddTweetPage();
    }
    public void showFollowers() throws IOException {
        Tweeter.enterFollowersPage(user);
    }
    public void showFollowings() throws IOException {
        Tweeter.enterFollowingsPage(user);
    }
    public void searchUser() throws IOException {
        Tweeter.enterSearchUserPage();
    }
    public void searchHashtag() throws IOException {
        Tweeter.enterSearchHashtagPage();
    }
    public void showTweets() throws IOException {
        Tweeter.enterShowProfileTweets(user);
    }
    public void followSelected() {
        if(isBlocked){
            //ignore
        } else {
            isFollowing=!isFollowing;
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
                    OOS.writeObject(user.getUsername());
                    //adding to user followings
                    OOS.writeObject(new String("fetchUser"));
                    OOS.writeObject(user.getUsername());
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
                    OOS.writeObject(user.getUsername());
                    followButton.getStyleClass().clear();
                    followButton.getStyleClass().add("button2");
                    followButton.setText("Follow");
                    //remove from users following
                    Iterator<User> following = Tweeter.getClient().getFollowings().iterator();
                    while (following.hasNext()) {
                        if (following.next().getUsername().equals(user.getUsername())) {
                            following.remove();
                            break;
                        }
                    }
                } catch (IOException e){
                    throw new RuntimeException();
                }
            }
        }
    }
    public void Message() throws IOException, ClassNotFoundException {
        if(isBlocked){
            //ignore
        } else {
            Tweeter.enterDirectPage(user);
        }
    }
    public void blockSelected(){
        isBlocked=!isBlocked;
        if(isBlocked){
            ObjectOutputStream OOS=Tweeter.getOOS();
            try {
                blockButton.setText("Blocked");
                String command = "block";
                OOS.writeObject(command);
                OOS.writeObject(Tweeter.getClient().getUsername());
                OOS.writeObject(user.getUsername());
                Tweeter.getClient().getBlockedUsers().add(user.getUsername());
                if(isFollowing){
                    try{
                        isFollowing=!isFollowing;
                        String command2 = "unfollow";
                        OOS.writeObject(command2);
                        OOS.writeObject(Tweeter.getClient().getUsername());
                        OOS.writeObject(user.getUsername());
                        followButton.getStyleClass().clear();
                        followButton.getStyleClass().add("button2");
                        followButton.setText("Follow");
                        //remove from users following
                        Iterator<User> following = Tweeter.getClient().getFollowings().iterator();
                        while (following.hasNext()) {
                            if (following.next().getUsername().equals(user.getUsername())) {
                                following.remove();
                                break;
                            }
                        }
                    } catch (IOException e){
                        throw new RuntimeException();
                    }
                }

            } catch (IOException e){
                throw new RuntimeException();
            }

        } else {
            blockButton.setText("Block");
            ObjectOutputStream OOS=Tweeter.getOOS();
            try {
                String command = "unblock";
                OOS.writeObject(command);
                OOS.writeObject(Tweeter.getClient().getUsername());
                OOS.writeObject(user.getUsername());
                Tweeter.getClient().getBlockedUsers().remove(user.getUsername());
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
    }

    public void setUser(User user) {
        iconImage.setImage(new Image(getClass().getResourceAsStream("/assets/twitterLogo.png")));
        this.user = user;
        //initialize
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
        tweetList.setCellFactory(tweetListView -> new TweetCell());

        followButton.getStylesheets().add(String.valueOf(getClass().getResource("style.css")));
        blockButton.getStylesheets().add(String.valueOf(getClass().getResource("style.css")));
        blockButton.getStyleClass().add("button3");
        for(String username:Tweeter.getClient().getBlockedUsers()) {
            if(username.equals(user.getUsername())){
                isBlocked=true;
                isFollowing=false;
                break;
            }
        }
        if(isBlocked){
            blockButton.setText("Blocked");
        } else {
            blockButton.setText("Block");
        }
        for(User user1:Tweeter.getClient().getFollowings()){
            if(user1.getUsername().equals(user.getUsername())){
                isFollowing=true;
                break;
            }
        }
        if(isFollowing){
            followButton.getStyleClass().clear();
            followButton.getStyleClass().add("button3");
            followButton.setText("Following");
        } else {
            followButton.getStyleClass().clear();
            followButton.getStyleClass().add("button2");
            followButton.setText("Follow");
        }
    }
}
