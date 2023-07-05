package com.tweeter;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;

import javafx.scene.media.*;
import javafx.util.Duration;
import javafx.event.ActionEvent;



public class AddTweetController {

    @FXML
    private VBox root;

    @FXML
    private TextArea tweetText;

    @FXML
    private Button photoButton;

    @FXML
    private Button videoButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button tweetButton;
    @FXML
    private CheckBox checkBox;
    @FXML private TextField option1;
    @FXML private TextField option2;
    @FXML private TextField option3;
    @FXML private TextField option4;
    @FXML private Label errorMessage;
    private boolean isVote=false;

    private File selectedPhoto;
    private File selectedVideo;

    @FXML
    private ImageView imageView;
    @FXML
    private MediaView mediaView;
    @FXML private Button playAndPause;
    @FXML private boolean playing=false;


    public void choosePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Photo");
        selectedPhoto = fileChooser.showOpenDialog(root.getScene().getWindow());


        if (selectedPhoto != null) {
            Image image = new Image(selectedPhoto.toURI().toString());

            double maxWidth = 1600;
            double maxHeight = 900;
            double width = image.getWidth();
            double height = image.getHeight();
            if (width > maxWidth || height > maxHeight) {
                errorMessage.setText("Image most have a maximum size of 1600*900 pixels");
                selectedPhoto=null;
            } else {
                imageView.setImage(image);
                imageView.setVisible(true);
            }
        }
    }

    public void chooseVideo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Video");
        selectedVideo = fileChooser.showOpenDialog(root.getScene().getWindow());

        if (selectedVideo != null) {
            long fileSizeInBytes = selectedVideo.length();
            long fileSizeInMB = fileSizeInBytes / (1024 * 1024); // Convert bytes to megabytes

            if (fileSizeInMB > 20) {
                errorMessage.setText("Video size cannot exceed 20 MB.");
                selectedVideo = null;
            } else {
                Media media = new Media(selectedVideo.toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                mediaView.setMediaPlayer(mediaPlayer);
                mediaView.setVisible(true);
                mediaPlayer.setOnEndOfMedia(
                        new Runnable() {
                            @Override
                            public void run() {
                                playing = false;
                                mediaPlayer.seek(Duration.ZERO);
                                mediaPlayer.pause();
                                playAndPause.setText("Play");
                            }
                        }
                );
                playAndPause.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        playing = !playing;
                        if(playing){
                            mediaPlayer.play();
                            playAndPause.setText("Pause");
                        } else {
                            mediaPlayer.pause();
                            playAndPause.setText("Play");
                        }
                    }
                });
            }
        }
    }

    public void deletePhoto(){
        selectedPhoto = null;
        imageView.setImage(null);
        imageView.setVisible(false);
    }
    public void deleteVideo(){
        selectedVideo = null;
        mediaView.setMediaPlayer(null);
        mediaView.setVisible(false);
    }

    public void cancel() throws IOException {
        Tweeter.enterHomePage();
    }

    public void addTweet() throws IOException {
        String text = tweetText.getText();
        if (text.length() > 280) {
             errorMessage.setText("tweet text has a maximum of 280 characters");
        } else {
            byte[] video=null;
            byte[] photo=null;
            if (selectedPhoto != null) {
                photo= Files.readAllBytes(selectedPhoto.toPath());
            }
            if (selectedVideo != null) {
                video= Files.readAllBytes(selectedVideo.toPath());
            }
            Tweet tweet=null;
            if(isVote){
                tweet=new Vote(text,photo,video,LocalDateTime.now(),Tweeter.getClient().getUsername(),Tweeter.extractHashtags(text),option1.getText(),option2.getText(),option3.getText(),option4.getText());
            } else {
                tweet=new Tweet(text,photo,video, LocalDateTime.now(),Tweeter.getClient().getUsername(),Tweeter.extractHashtags(text));
            }
            Tweeter.getClient().getTweets().add(tweet);
            ObjectOutputStream OOS=Tweeter.getOOS();
            String command = "addTweet";
            OOS.writeObject(command);
            OOS.writeObject(tweet);
            Tweeter.enterHomePage();
        }
    }

    public void initialize(){
        option1.setStyle("-fx-opacity: 0.6; -fx-background-color: #CCCCCC; -fx-text-fill: #555555;");
        option2.setStyle("-fx-opacity: 0.6; -fx-background-color: #CCCCCC; -fx-text-fill: #555555;");
        option3.setStyle("-fx-opacity: 0.6; -fx-background-color: #CCCCCC; -fx-text-fill: #555555;");
        option4.setStyle("-fx-opacity: 0.6; -fx-background-color: #CCCCCC; -fx-text-fill: #555555;");
        option1.editableProperty().bind(checkBox.selectedProperty());
        option2.editableProperty().bind(checkBox.selectedProperty());
        option3.editableProperty().bind(checkBox.selectedProperty());
        option4.editableProperty().bind(checkBox.selectedProperty());
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                isVote=true;
                option1.setStyle("");
                option2.setStyle("");
                option3.setStyle("");
                option4.setStyle("");
            } else {
                isVote=false;
                option1.setStyle("-fx-opacity: 0.6; -fx-background-color: #CCCCCC; -fx-text-fill: #555555;");
                option2.setStyle("-fx-opacity: 0.6; -fx-background-color: #CCCCCC; -fx-text-fill: #555555;");
                option3.setStyle("-fx-opacity: 0.6; -fx-background-color: #CCCCCC; -fx-text-fill: #555555;");
                option4.setStyle("-fx-opacity: 0.6; -fx-background-color: #CCCCCC; -fx-text-fill: #555555;");
            }
        });
    }
}