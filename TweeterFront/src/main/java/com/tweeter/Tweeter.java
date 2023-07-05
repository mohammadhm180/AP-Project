package com.tweeter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;


public class Tweeter extends Application {
    private static Stage stage;
    private static Socket server = null;
    private static String token = null;
    private static File file = new File("UserInfo/token.bin");
    private static ObjectOutputStream OOS;
    private static ObjectInputStream OIS;
    private static User client;

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        Tweeter.token = token;
    }

    public static Socket getServer() {
        return server;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //setting up stage
        stage=primaryStage;
        stage.setTitle("Tweeter");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/assets/tweeterIcon.png")));
        connectServer();
    }

    public static void connectServer() throws IOException, ClassNotFoundException {
        try {
            server = new Socket("127.0.0.1", 8000);
        } catch (IOException exception) {
            enterLostConnection();;
        }
        try{
            OOS = new ObjectOutputStream(server.getOutputStream());
            OIS = new ObjectInputStream(server.getInputStream());

            //checking if token already exist in local file
            if (file.exists()) {
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
                token = (String) objectInputStream.readObject();
                objectInputStream.close();
            }
            if (token == null) {
                //we do not have a token in this case
                //here sign up or log in is done
                enterProgram();
            } else {
                OOS.writeObject(new String("checkSignInToken"));
                OOS.writeObject(token);
                String result = (String) OIS.readObject();
                if (result.equals("success")) {
                    client = (User) OIS.readObject();
                    enterHomePage();
                } else {
                    //the token is invalid
                    //here sign up or log in is done
                    enterProgram();
                }
            }
        } catch (NullPointerException e){
            enterLostConnection();
        }
    }

    public static void enterProgram() throws IOException {
        Parent root= FXMLLoader.load(Tweeter.class.getResource("login.fxml"));
        Scene scene=new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void signupPage() throws IOException {
        Parent root= FXMLLoader.load(Tweeter.class.getResource("signup.fxml"));
        Scene scene=new Scene(root);
        stage.setScene(scene);
    }

    public static void enterHomePage() throws IOException {
        Parent root= FXMLLoader.load(Tweeter.class.getResource("HomeScene.fxml"));
        Scene scene=new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void enterProfilePage() throws IOException {
        Parent root= FXMLLoader.load(Tweeter.class.getResource("profile.fxml"));
        Scene scene=new Scene(root);
        stage.setScene(scene);
    }
    public static void enterFollowingsPage(User user) throws IOException {
        FXMLLoader loader=new FXMLLoader(Tweeter.class.getResource("followings.fxml"));
        Parent root= loader.load();
        FollowingsController followingsController=loader.getController();
        followingsController.setUser(user);
        Scene scene=new Scene(root);
        stage.setScene(scene);
    }
    public static void enterFollowersPage(User user) throws IOException {
        FXMLLoader loader=new FXMLLoader(Tweeter.class.getResource("followers.fxml"));
        Parent root= loader.load();
        FollowersController followersController=loader.getController();
        followersController.setUser(user);
        Scene scene=new Scene(root);
        stage.setScene(scene);
    }
    public static void enterEditPage() throws IOException {
        Parent root= FXMLLoader.load(Tweeter.class.getResource("editProfile.fxml"));
        Scene scene=new Scene(root);
        stage.setScene(scene);
    }
    public static void enterSelectedProfile(String username) throws IOException, ClassNotFoundException {
        OOS.writeObject(new String("fetchUser"));
        OOS.writeObject(new String(username));
        User user=(User) OIS.readObject();

        FXMLLoader loader=new FXMLLoader(Tweeter.class.getResource("selectedProfile.fxml"));
        Parent root= loader.load();
        SelectedProfileController selectedProfileController=loader.getController();
        selectedProfileController.setUser(user);
        Scene scene=new Scene(root);
        stage.setScene(scene);
    }
    public static void enterLostConnection() throws IOException {
        Parent root= FXMLLoader.load(Tweeter.class.getResource("lostConnection.fxml"));
        Scene scene=new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void enterAddTweetPage() throws IOException {
        Parent root= FXMLLoader.load(Tweeter.class.getResource("addTweet.fxml"));
        Scene scene=new Scene(root);
        stage.setScene(scene);
    }
    public static void enterSearchHashtagPage() throws IOException {
        Parent root = FXMLLoader.load(Tweeter.class.getResource("searchHashtagScene.fxml"));
        Scene scene = new Scene(root);
        Tweeter.getStage().setScene(scene);
    }
    public static void enterAddQuoteReplyPage(String referringID,boolean isQuote) throws IOException {
        FXMLLoader loader=new FXMLLoader(Tweeter.class.getResource("addQuoteReply.fxml"));
        Parent root= loader.load();
        AddQuoteReplyController addQuoteReplyController=loader.getController();
        addQuoteReplyController.setReferringID(referringID);
        if(isQuote){
            addQuoteReplyController.setQuote();
        } else {
            addQuoteReplyController.setReply();
        }
        Scene scene=new Scene(root);
        stage.setScene(scene);
    }

    public static void enterSearchUserPage() throws IOException {
        Parent root= FXMLLoader.load(Tweeter.class.getResource("searchUser.fxml"));
        Scene scene=new Scene(root);
        stage.setScene(scene);
    }
    public static void enterMessages() throws IOException {
        Parent root= FXMLLoader.load(Tweeter.class.getResource("Messages.fxml"));
        Scene scene=new Scene(root);
        stage.setScene(scene);
    }
    public static void enterShowProfileTweets(User targetUser ) throws IOException {
        FXMLLoader loader=new FXMLLoader(Tweeter.class.getResource("showProfileTweets.fxml"));
        Parent root= loader.load();
        ShowProfileTweetsController showProfileTweetsController=loader.getController();
        showProfileTweetsController.setProfileUser(targetUser);
        Scene scene=new Scene(root);
        stage.setScene(scene);
    }

    public static File getFile() {
        return file;
    }

    public static Stage getStage() {
        return stage;
    }

    public static ObjectOutputStream getOOS() {
        return OOS;
    }

    public static ObjectInputStream getOIS() {
        return OIS;
    }
    public static void setClient(User client) {
        Tweeter.client = client;
    }

    public static User getClient() {
        return client;
    }


    public static ArrayList<String> extractHashtags(String text) {
        ArrayList<String> words = new ArrayList<String>(Arrays.asList(text.split("\\s+")));
        ArrayList<String> hashtags = new ArrayList<String>();
        for (String word : words) {
            if (word.startsWith("#")) {
                hashtags.add(word);
            }
        }
        return hashtags;
    }

    public static void enterDirectPage(User targetUser) throws IOException, ClassNotFoundException {
        FXMLLoader loader=new FXMLLoader(Tweeter.class.getResource("direct.fxml"));
        Parent root= loader.load();
        DirectController directController=loader.getController();
        directController.setUser(targetUser);
        Scene scene=new Scene(root);
        stage.setScene(scene);
    }
    public static String showTweetTime(LocalDateTime tweetDate) {
        Duration duration = Duration.between(tweetDate, LocalDateTime.now());
        long minutes = duration.toMinutes();
        if (minutes < 60) {
            return (minutes + "m");
        } else if (minutes < 60 * 24) {
            return ((minutes / 60) + "h");
        } else {
            return (tweetDate.getDayOfMonth() + " " + tweetDate.getMonth().toString() + " " + tweetDate.getYear());
        }
    }
}

