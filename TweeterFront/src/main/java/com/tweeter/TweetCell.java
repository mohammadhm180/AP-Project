package com.tweeter;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.*;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import javax.security.auth.callback.Callback;
import java.awt.*;
import java.awt.TextField;
import java.io.*;
import java.util.Iterator;

public class TweetCell extends ListCell<Tweet> {
    boolean playing = false;

    public TweetCell() {

    }
    public VBox tweetCellCreator(Tweet tweet) throws IOException, ClassNotFoundException {
        ObjectOutputStream OOS=Tweeter.getOOS();
        ObjectInputStream OIS=Tweeter.getOIS();
        OOS.writeObject(new String("isLiked"));
        OOS.writeObject(tweet.getTweetID());
        OOS.writeObject(Tweeter.getClient().getUsername());
        String isLiked = (String) OIS.readObject();

        boolean isRetweeted=false;
        for (Tweet retweet : Tweeter.getClient().getTweets()) {
            if(retweet instanceof Retweet){
                if(((Retweet) retweet).getReferredTweetID().equals(tweet.getTweetID())){
                    isRetweeted=true;
                    break;
                }
            }
        }

        Hyperlink username = new Hyperlink();
        Label tweetText = new Label();
        tweetText.setWrapText(true);
        tweetText.setMaxWidth(250);
        Label tweetDate = new Label();
        ImageView userAvatar = new ImageView();
        ImageView imageView = new ImageView();
        MediaView mediaView = new MediaView();
        Button likeButton = new Button();
        if(isLiked.equals("yes")){
            likeButton.setText("Unlike");
        } else {
            likeButton.setText("Like");
        }
        Button retweetButton = new Button();
        Button commentsButton = new Button("Comments");
        if(isRetweeted){
            retweetButton.setText("Undo Retweet");
        } else {
            retweetButton.setText("Retweet");
        }

        Button quoteButton = new Button("Quote");
        Label likeCount = new Label(tweet.getLikeCount()+"");
        Label replyCount = new Label(tweet.getReplies().size()+"");
        Label retweetCount = new Label(tweet.getRetweetCount()+"");
        VBox UsText = new VBox(8.0, username, tweetText);
        HBox AvUsText = new HBox(8.0, userAvatar, UsText, tweetDate);
        VBox b1 = new VBox(8.0, likeButton, likeCount);
        VBox b2 = new VBox(8.0, commentsButton, replyCount);
        VBox b3 = new VBox(8.0, retweetButton, retweetCount);
        VBox b4 = new VBox(8.0, quoteButton);
        HBox buttons = new HBox(60, b1, b2, b3, b4);
        Button playAndPause = new Button("Play");
        VBox videoPlayer = new VBox(2, playAndPause, mediaView);
        VBox tweetCell = new VBox(AvUsText, imageView, videoPlayer, buttons);
        userAvatar.setFitWidth(120);
        userAvatar.setFitHeight(80);
        userAvatar.setPreserveRatio(true);
        userAvatar.setPickOnBounds(true);

        username.setPrefWidth(USE_COMPUTED_SIZE);
        username.setPrefHeight(USE_COMPUTED_SIZE);

        tweetText.setWrapText(true);
        tweetText.setPrefWidth(USE_COMPUTED_SIZE);
        tweetText.setPrefHeight(USE_COMPUTED_SIZE);

        UsText.setPrefWidth(USE_COMPUTED_SIZE);
        UsText.setPrefHeight(USE_COMPUTED_SIZE);

        AvUsText.setPrefWidth(USE_COMPUTED_SIZE);
        AvUsText.setPrefHeight(USE_COMPUTED_SIZE);

        imageView.setFitHeight(USE_COMPUTED_SIZE);
        imageView.setFitWidth(600);
        imageView.setTranslateX(250);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);
        imageView.setViewport(new Rectangle2D(0, 0, 0, 0));


        mediaView.setFitHeight(USE_COMPUTED_SIZE);
        mediaView.setFitWidth(600);
        mediaView.setPreserveRatio(true);
        mediaView.setPickOnBounds(true);
        videoPlayer.setTranslateX(250);
        playAndPause.setMaxWidth(600);


        b1.setPrefHeight(USE_COMPUTED_SIZE);
        b1.setPrefWidth(USE_COMPUTED_SIZE);
        b1.setAlignment(Pos.CENTER);
        b2.setPrefHeight(USE_COMPUTED_SIZE);
        b2.setPrefWidth(USE_COMPUTED_SIZE);
        b2.setAlignment(Pos.CENTER);
        b3.setPrefHeight(USE_COMPUTED_SIZE);
        b3.setPrefWidth(USE_COMPUTED_SIZE);
        b3.setAlignment(Pos.CENTER);
        b4.setPrefHeight(USE_COMPUTED_SIZE);
        b4.setPrefWidth(USE_COMPUTED_SIZE);
        buttons.setPrefHeight(USE_COMPUTED_SIZE);
        buttons.setPrefWidth(USE_COMPUTED_SIZE);


        likeButton.getStylesheets().add(String.valueOf(getClass().getResource("style.css")));
        likeButton.getStyleClass().clear();
        likeButton.getStyleClass().add("button22");
        quoteButton.getStylesheets().add(String.valueOf(getClass().getResource("style.css")));
        quoteButton.getStyleClass().clear();
        quoteButton.getStyleClass().add("button22");
        commentsButton.getStylesheets().add(String.valueOf(getClass().getResource("style.css")));
        commentsButton.getStyleClass().clear();
        commentsButton.getStyleClass().add("button22");
        retweetButton.getStylesheets().add(String.valueOf(getClass().getResource("style.css")));
        retweetButton.getStyleClass().clear();
        retweetButton.getStyleClass().add("button22");


        username.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    if(tweet.getAuthorUsername().equals(Tweeter.getClient().getUsername())) {
                        Tweeter.enterProfilePage();
                    } else {
                        Tweeter.enterSelectedProfile(username.getText());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        likeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(likeButton.getText().equals("Like")){
                    tweet.setLikeCount(tweet.getLikeCount()+1);
                    likeCount.setText(tweet.getLikeCount()+"");
                    likeButton.setText("Unlike");
                    String command = "like";
                    try {
                        OOS.writeObject(command);
                        OOS.writeObject(tweet.getTweetID());
                        OOS.writeObject(Tweeter.getClient().getUsername());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    tweet.setLikeCount(tweet.getLikeCount()-1);
                    likeCount.setText(tweet.getLikeCount()+"");
                    likeButton.setText("Like");
                    String command = "unlike";
                    try {
                        OOS.writeObject(command);
                        OOS.writeObject(tweet.getTweetID());
                        OOS.writeObject(Tweeter.getClient().getUsername());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        commentsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    FXMLLoader loader = new FXMLLoader(Tweeter.class.getResource("repliesScene.fxml"));
                    Parent root = loader.load();
                    RepliesSceneController controller = loader.getController();
                    controller.setTweet(tweet);
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(Tweeter.class.getResource("HomeScene.css").toExternalForm());
                    Tweeter.getStage().setScene(scene);
                } catch (Exception e) {
                    try {
                        Tweeter.enterLostConnection();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        retweetButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(retweetButton.getText().equals("Retweet")){
                    Retweet retweet=null;
                    if(tweet instanceof Retweet){
                         retweet = new Retweet(tweet.getText(), tweet.getPhoto(), tweet.getVideo(), tweet.getTweetDate(), Tweeter.getClient().getUsername(), tweet.getHashtag(), ((Retweet) tweet).getReferredTweetID(), tweet.getRetweetCount(), tweet.getReplyCount(), tweet.getLikeCount());

                    } else {
                         retweet = new Retweet(tweet.getText(), tweet.getPhoto(), tweet.getVideo(), tweet.getTweetDate(), Tweeter.getClient().getUsername(), tweet.getHashtag(), tweet.getTweetID(), tweet.getRetweetCount(), tweet.getReplyCount(), tweet.getLikeCount());
                    }

                    try {
                        OOS.writeObject(new String("addTweet"));
                        OOS.writeObject(retweet);
                        tweet.setRetweetCount(tweet.getRetweetCount()+1);
                        retweetButton.setText("Undo Retweet");
                        retweetCount.setText(tweet.getRetweetCount()+"");
                        Tweeter.getClient().getTweets().add(retweet);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }else {
                    String command = "removeTweet";
                    Tweet retweet = null;
                    Iterator<Tweet> tweetIterator = Tweeter.getClient().getTweets().iterator();
                    while (tweetIterator.hasNext()) {
                        retweet = tweetIterator.next();
                        if(retweet instanceof Retweet){
                            if (((Retweet)(retweet)).getReferredTweetID().equals(tweet.getTweetID())){
                                break;
                            }
                        }
                    }
                    try {
                        OOS.writeObject(command);
                        OOS.writeObject(retweet);
                        tweetIterator.remove();
                        tweet.setRetweetCount(tweet.getRetweetCount()-1);
                        retweetButton.setText("Retweet");
                        retweetCount.setText(tweet.getRetweetCount()+"");
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        quoteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    if(tweet instanceof Retweet){
                        Tweeter.enterAddQuoteReplyPage(((Retweet) tweet).getReferredTweetID(),true);
                    } else {
                        Tweeter.enterAddQuoteReplyPage(tweet.getTweetID(),true);
                    }
                } catch (IOException e) {
                    try {
                        Tweeter.enterLostConnection();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        username.setText(tweet.getAuthorUsername());
        tweetText.setText(tweet.getText());
        tweetDate.setText(Tweeter.showTweetTime(tweet.getTweetDate()));

        //loading avatar photo
        DisplayInfo displayInfo = null;
        try {
            Tweeter.getOOS().writeObject("getDisplayInfo");
            Tweeter.getOOS().writeObject(tweet.getTweetID());
            displayInfo = (DisplayInfo) Tweeter.getOIS().readObject();
        } catch (Exception e) {
            try {
                Tweeter.enterLostConnection();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        if (displayInfo != null && displayInfo.getUser().getAvatar() != null) {//have to be changed
            try {
                File tempFile = File.createTempFile("avatarPhoto", ".png");
                FileOutputStream fos = new FileOutputStream(tempFile);
                fos.write(displayInfo.getUser().getAvatar());
                Image image = new Image(tempFile.toURI().toURL().toString());
                userAvatar.setImage(image);
                double centerX = userAvatar.localToScene(userAvatar.getBoundsInLocal()).getMinX() + userAvatar.getBoundsInLocal().getWidth() / 2;
                double centerY = userAvatar.localToScene(userAvatar.getBoundsInLocal()).getMinY() + userAvatar.getBoundsInLocal().getHeight() / 2;
                Circle clip = new Circle();
                clip.setCenterX(centerX);
                clip.setCenterY(centerY);
                clip.setRadius(33);
                userAvatar.setClip(clip);
                fos.close();
            } catch (Exception e) {
                //ignore
            }
        } else {
            //set default avatar photo
            Image avatar = new Image(getClass().getResourceAsStream("/assets/emptyProfile.png"));
            userAvatar.setImage(avatar);
            double centerX = userAvatar.localToScene(userAvatar.getBoundsInLocal()).getMinX() + userAvatar.getBoundsInLocal().getWidth() / 2;
            double centerY = userAvatar.localToScene(userAvatar.getBoundsInLocal()).getMinY() + userAvatar.getBoundsInLocal().getHeight() / 2;
            Circle clip = new Circle();
            clip.setCenterX(centerX);
            clip.setCenterY(centerY);
            clip.setRadius(33);
            userAvatar.setClip(clip);
        }

        if (tweet.getPhoto() == null && tweet.getVideo() == null) {
            tweetCell.setSpacing(0);
        }

        //loading tweet photo:
        if (tweet.getPhoto() != null) {
            Image image;
            try {
                File tempFile = File.createTempFile("tweetPhoto", ".png");
                FileOutputStream fos = new FileOutputStream(tempFile);
                fos.write(tweet.getPhoto());
                image = new Image(tempFile.toURI().toURL().toString());
                imageView.setImage(image);
                fos.close();
            } catch (Exception e) {
                //ignore
            }
        }

        //loading tweet video:
        if (tweet.getVideo() != null) {
            try {
                File tempFile = File.createTempFile("tweetVideo", ".mp4");
                FileOutputStream fos = new FileOutputStream(tempFile);
                fos.write(tweet.getVideo());
                fos.close();
                Media media = new Media(tempFile.toURI().toURL().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                mediaView.setMediaPlayer(mediaPlayer);
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
                        if (playing) {
                            mediaPlayer.play();
                            playAndPause.setText("Pause");
                        } else {
                            mediaPlayer.pause();
                            playAndPause.setText("Play");
                        }
                    }
                });

            } catch (Exception e) {
                //ignore
            }
        } else {
            tweetCell.getChildren().remove(videoPlayer);
        }

        likeCount.setText(String.valueOf(tweet.getLikeCount()));
        replyCount.setText(String.valueOf(tweet.getReplyCount()));
        retweetCount.setText(String.valueOf(tweet.getRetweetCount()));
        tweetCell.setPrefWidth(USE_COMPUTED_SIZE);
        tweetCell.setPrefHeight(USE_COMPUTED_SIZE);
        return tweetCell;
    }

    protected VBox quotedTweetCreator(Tweet tweet) {
        Label username = new Label();
        Label tweetText = new Label();
        tweetText.setWrapText(true);
        tweetText.setMaxWidth(250);
        Label tweetDate = new Label();
        ImageView userAvatar = new ImageView();
        ImageView imageView = new ImageView();
        MediaView mediaView = new MediaView();
        VBox UsText = new VBox(8.0, username, tweetText);
        HBox AvUsText = new HBox(8.0, userAvatar, UsText, tweetDate);
        VBox tweetCell = new VBox(15.0, AvUsText, imageView, mediaView);

        userAvatar.setFitWidth(120);
        userAvatar.setFitHeight(80);
        userAvatar.setPreserveRatio(true);
        userAvatar.setPickOnBounds(true);

        username.setWrapText(true);
        username.setPrefWidth(USE_COMPUTED_SIZE);
        username.setPrefHeight(USE_COMPUTED_SIZE);

        tweetText.setWrapText(true);
        tweetText.setPrefWidth(USE_COMPUTED_SIZE);
        tweetText.setPrefHeight(USE_COMPUTED_SIZE);

        UsText.setPrefWidth(USE_COMPUTED_SIZE);
        UsText.setPrefHeight(USE_COMPUTED_SIZE);

        AvUsText.setPrefWidth(USE_COMPUTED_SIZE);
        AvUsText.setPrefHeight(USE_COMPUTED_SIZE);

        imageView.setFitHeight(USE_COMPUTED_SIZE);
        imageView.setFitWidth(300);
        imageView.setTranslateX(100);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);
        imageView.setViewport(new Rectangle2D(0, 0, 0, 0));

        mediaView.setFitHeight(USE_COMPUTED_SIZE);
        mediaView.setFitWidth(300);
        mediaView.setTranslateX(100);
        mediaView.setPickOnBounds(true);
        mediaView.setPreserveRatio(true);

        username.setText(tweet.getAuthorUsername());
        tweetText.setText(tweet.getText());
        tweetDate.setText(Tweeter.showTweetTime(tweet.getTweetDate()));

        //loading avatar photo
        DisplayInfo displayInfo = null;
        try {
            Tweeter.getOOS().writeObject("getDisplayInfo");
            Tweeter.getOOS().writeObject(tweet.getTweetID());
            displayInfo = (DisplayInfo) Tweeter.getOIS().readObject();
        } catch (Exception e) {
        }
        if (displayInfo != null && displayInfo.getUser().getAvatar() != null) {
            try {
                File tempFile = File.createTempFile("avatarPhoto", ".png");
                FileOutputStream fos = new FileOutputStream(tempFile);
                fos.write(displayInfo.getUser().getAvatar());
                Image image = new Image(tempFile.toURI().toURL().toString());
                userAvatar.setImage(image);
                double centerX = userAvatar.localToScene(userAvatar.getBoundsInLocal()).getMinX() + userAvatar.getBoundsInLocal().getWidth() / 2;
                double centerY = userAvatar.localToScene(userAvatar.getBoundsInLocal()).getMinY() + userAvatar.getBoundsInLocal().getHeight() / 2;
                Circle clip = new Circle();
                clip.setCenterX(centerX);
                clip.setCenterY(centerY);
                clip.setRadius(33);
                userAvatar.setClip(clip);
                fos.close();
            } catch (Exception e) {
                //ignore
            }
        } else {
            //set default avatar photo
            Image avatar = new Image(getClass().getResourceAsStream("/assets/emptyProfile.png"));
            userAvatar.setImage(avatar);
            double centerX = userAvatar.localToScene(userAvatar.getBoundsInLocal()).getMinX() + userAvatar.getBoundsInLocal().getWidth() / 2;
            double centerY = userAvatar.localToScene(userAvatar.getBoundsInLocal()).getMinY() + userAvatar.getBoundsInLocal().getHeight() / 2;
            Circle clip = new Circle();
            clip.setCenterX(centerX);
            clip.setCenterY(centerY);
            clip.setRadius(33);
            userAvatar.setClip(clip);
        }

        if (tweet.getPhoto() == null && tweet.getVideo() == null) {
            tweetCell.setSpacing(0);
        }

        Image image;
        if (tweet.getPhoto() != null) {
            try {
                File tempFile = File.createTempFile("tweetPhoto", ".png");
                FileOutputStream fos = new FileOutputStream(tempFile);
                fos.write(tweet.getPhoto());
                image = new Image(tempFile.toURI().toURL().toString());
                imageView.setImage(image);
                fos.close();
            } catch (Exception e) {
                //ignore
            }
        }

        if (tweet.getVideo() != null) {
            try {
                File tempFile = File.createTempFile("tweetVideo", ".mp4");
                FileOutputStream fos = new FileOutputStream(tempFile);
                fos.write(tweet.getVideo());
                fos.close();
                Media media = new Media(tempFile.toURI().toURL().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                mediaView.setMediaPlayer(mediaPlayer);
            } catch (Exception e) {
                //ignore
            }
        }

        tweetCell.getStyleClass().add("cell-vbox");
        tweetCell.setPrefHeight(USE_COMPUTED_SIZE);
        tweetCell.setPrefWidth(450);
        return tweetCell;
    }

    @Override
    protected void updateItem(Tweet tweet, boolean empty) {
        super.updateItem(tweet, empty);

        if (empty || tweet == null) {
            setText(null);
            setGraphic(null);
            return;
        }
        if (tweet instanceof Retweet) {
            Label retweeterUsername = new Label();
            DisplayInfo displayInfo = null;
            try {
                Tweeter.getOOS().writeObject("getDisplayInfo");
                Tweeter.getOOS().writeObject(tweet.getTweetID());
                displayInfo = (DisplayInfo) Tweeter.getOIS().readObject();
            } catch (Exception e) {
            }
            if (displayInfo != null) {
                retweeterUsername.setText(displayInfo.getUser().getUsername()+"  Retweeted");
            }
            VBox retweetCell = null;
            try {
                retweetCell = new VBox(10.0, retweeterUsername, tweetCellCreator(tweet));
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            retweetCell.getStyleClass().add("cell-vbox");
            retweetCell.setPrefWidth(USE_COMPUTED_SIZE);
            retweetCell.setPrefHeight(USE_COMPUTED_SIZE);
            setGraphic(retweetCell);
        } else if (tweet instanceof Quote) {
            VBox quoteCell = null;
            try {
                quoteCell = tweetCellCreator(tweet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                Tweeter.getOOS().writeObject(new String("getTweet"));
                Tweeter.getOOS().writeObject(((Quote) tweet).getReferredTweetID());
                Tweet referred=(Tweet) Tweeter.getOIS().readObject();
                ((Quote) tweet).setReferredTweet(referred);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }


            VBox quotedTweet = quotedTweetCreator(((Quote) tweet).getReferredTweet());
            javafx.scene.control.TextField forSpace = new javafx.scene.control.TextField();
            forSpace.setEditable(false);
            forSpace.setVisible(false);
            forSpace.setPrefWidth(400);
            Button checkQuotedTweet = new Button("Check Tweet");
            checkQuotedTweet.getStylesheets().add(String.valueOf(getClass().getResource("style.css")));
            checkQuotedTweet.getStyleClass().clear();
            checkQuotedTweet.getStyleClass().add("button22");
            checkQuotedTweet.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    try {
                        FXMLLoader loader = new FXMLLoader(Tweeter.class.getResource("tweetScene.fxml"));
                        Parent root = loader.load();
                        TweetSceneController controller = loader.getController();
                        controller.setTweet(((Quote) tweet).getReferredTweet());
                        Scene newScene = new Scene(root);
                        newScene.getStylesheets().add("TweetScene.css");
                        Tweeter.getStage().setScene(newScene);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            HBox spacer = new HBox(3.0, forSpace, new VBox(3.0, checkQuotedTweet, quotedTweet));
            int insertIndex = quoteCell.getChildren().size() - 1;
            for (Node node : quoteCell.getChildren()) {
                if (node instanceof MediaView) {
                    insertIndex = quoteCell.getChildren().indexOf(node);
                    break;
                }
            }
            quotedTweet.getStyleClass().add("cell-vbox");
            quoteCell.getChildren().add(insertIndex, spacer);
            quoteCell.getStyleClass().add("cell-vbox");
            setGraphic(quoteCell);
        } else if (tweet instanceof Vote) {
            Button voteButton = new Button("Vote");
            voteButton.setVisible(false);
            Vote vote = (Vote) tweet;
            VBox voteCell = null;
            try {
                voteCell = tweetCellCreator(tweet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            VBox voteSection = new VBox(8);
            String[] options = {vote.getOption1(), vote.getOption2(), vote.getOption3(), vote.getOption4()};
            int[] optionsCount = {vote.getOption1Count(), vote.getOption2Count(), vote.getOption3Count(), vote.getOption4Count()};

            RadioButton[] optionsButtons = new RadioButton[4];
            Label[] optionCountLabels = new Label[4];

            ToggleGroup optionsGroup = new ToggleGroup();

            for (int i = 0; i < 4; i++) {
                if (!options[i].equals("") && options[i] != null) {
                    optionsButtons[i] = new RadioButton(options[i]);
                    optionsButtons[i].setToggleGroup(optionsGroup);
                    optionCountLabels[i] = new Label(String.valueOf(optionsCount[i]));
                    optionsButtons[i].getStyleClass().add("radio-button");
                }
            }

            for (int i = 0; i < 4; i++) {
                if (optionsButtons[i] != null) {
                    voteSection.getChildren().add(new HBox(10.0, optionCountLabels[i], optionsButtons[i]));
                }
            }
            voteSection.getChildren().add(voteButton);
            int insertIndex = voteCell.getChildren().size() - 1;
            for (Node node : voteCell.getChildren()) {
                if (node instanceof MediaView) {
                    insertIndex = voteCell.getChildren().indexOf(node);
                    break;
                }
            }
            voteCell.getChildren().add(insertIndex, voteSection);
            voteCell.getStyleClass().add("cell-vbox");
            final int[] index = {0};
            optionsGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                @Override
                public void changed(ObservableValue<? extends Toggle> observableValue, Toggle oldV, Toggle newV) {
                    if (newV != null) {
                        voteButton.setVisible(true);
                        for (int i = 0; i < 4; i++) {
                            if (optionsButtons[i] == newV) {
                                index[0] = i;
                            }
                        }
                    }
                }
            });
            voteButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    for (RadioButton radioButton : optionsButtons) {
                        if (radioButton != null) {
                            radioButton.setDisable(true);
                        }
                    }
                    String res = optionCountLabels[index[0]].getText();
                    res = String.valueOf(Integer.parseInt(res) + 1);
                    optionCountLabels[index[0]].setText(res);
                    voteButton.setVisible(false);
                    try {
                        Tweeter.getOOS().writeObject("vote");
                        Tweeter.getOOS().writeObject(tweet.getTweetID());
                        Tweeter.getOOS().writeObject(String.valueOf(index[0]));
                    } catch (Exception e) {
                    }
                }
            });
            setGraphic(voteCell);
        } else {
            VBox tweetCell = null;
            try {
                tweetCell = tweetCellCreator(tweet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            tweetCell.getStyleClass().add("cell-vbox");
            setGraphic(tweetCell);
        }
    }

}