package com.tweeter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.ResourceBundle;

public class DirectController {
    @FXML
    private VBox userItems;
    @FXML private ImageView profilePhoto=new ImageView();
    @FXML private Label userFullName=new Label();
    @FXML private Hyperlink hyperlink=new Hyperlink();
    @FXML ImageView twitterLogo;
    @FXML
    TextField sendingMessage;
    private User user;
    private Direct direct;

    public void hyperlinkPressed() throws IOException, ClassNotFoundException {
        Tweeter.enterSelectedProfile(user.getUsername());
    }
    public void backButton() throws IOException, ClassNotFoundException {
        Tweeter.enterMessages();
    }

    public void send() throws IOException {
        if(sendingMessage.getText().trim().equals("")){
            //ignore
        } else {
            ObjectOutputStream OOS=Tweeter.getOOS();
            String command = "addDirect";
            OOS.writeObject(command);
            Message message=new Message(Tweeter.getClient().getUsername(), user.getUsername(), sendingMessage.getText(), LocalDateTime.now());
            OOS.writeObject(message);
            direct.getSentMessages().add(message);
            addMessage(message,true);
            sendingMessage.clear();
            sendingMessage.setPromptText("Message");
        }
    }
    private void addMessage(Message message,boolean isSender) {
        HBox hbox = new HBox();
        hbox.setId("userItemTemplate");
        hbox.setPrefHeight(600);
        hbox.setPrefWidth(2500);
        hbox.setSpacing(10.0);
        hbox.getStyleClass().add("user-item");
        hbox.getStylesheets().add("followings.css");


        VBox vbox = new VBox();
        vbox.setPrefHeight(1000);
        vbox.setPrefWidth(2500);
        vbox.setSpacing(10);

        Label messageInfo = new Label();
        messageInfo.setPrefHeight(200);
        messageInfo.setPrefWidth(2000);
        messageInfo.setWrapText(true);

        String time= ((message.getDate().getHour()<10) ? "0" : "")+message.getDate().getHour()+" "+((message.getDate().getMinute()<10) ? "0" : "")+message.getDate().getMinute();
        if(isSender){
            messageInfo.setText(time);
            messageInfo.setAlignment(Pos.CENTER_RIGHT);
        } else {
            messageInfo.setText(user.getFirstName()+" "+user.getLastName()+"  "+time);
            messageInfo.setAlignment(Pos.CENTER_LEFT);
        }


        Label text=new Label(message.getText());
        text.wrapTextProperty().setValue(true);
        text.setStyle("-fx-font:bold 15pt Arial;");
        text.setPrefHeight(100);
        text.setPrefWidth(2000);

        if(isSender){
            text.setAlignment(Pos.CENTER_RIGHT);
        } else {
            text.setAlignment(Pos.CENTER_LEFT);
        }


        vbox.getChildren().addAll(messageInfo,text);

        hbox.getChildren().addAll(vbox);

        userItems.getChildren().add(hbox);
    }

    public void initializePage() throws IOException, ClassNotFoundException {
        sendingMessage.setPromptText("Message");
        twitterLogo.setImage(new Image(getClass().getResourceAsStream("/assets/twitterLogo.png")));
        try{
            profilePhoto.setImage(new Image(new ByteArrayInputStream(user.getAvatar())));
        } catch (NullPointerException e){
            profilePhoto.setImage(new Image(getClass().getResourceAsStream("/assets/emptyProfile.png")));
        }
        userFullName.setText(user.getFirstName()+" "+user.getLastName());
        hyperlink.setText("@"+user.getUsername());

        for (Direct tempDirect:Tweeter.getClient().getDirects()){
            if(tempDirect.getUsername().equals(user.getUsername())){
                direct=tempDirect;
                break;
            }
        }
        if(direct==null){
            direct=new Direct(user.getUsername());
            Tweeter.getClient().getDirects().add(direct);
        } else {
            int receiveIndex=0;
            for(Message sentMessage:direct.getSentMessages()){
                if(receiveIndex==direct.getReceivedMessages().size()){
                    addMessage(sentMessage,true);
                }
                for (int i=receiveIndex;i<direct.getReceivedMessages().size();i++){
                    if(sentMessage.getDate().compareTo(direct.getReceivedMessages().get(i).getDate())<0){
                        addMessage(sentMessage,true);
                        break;
                    } else {
                        addMessage(direct.getReceivedMessages().get(i),false);
                        receiveIndex++;
                        if(receiveIndex==direct.getReceivedMessages().size()){
                            addMessage(sentMessage,true);
                        }
                    }
                }
            }
            if(receiveIndex!=direct.getReceivedMessages().size()){
                for (int i=receiveIndex;i<direct.getReceivedMessages().size();i++){
                    addMessage(direct.getReceivedMessages().get(i),false);
                }
            }
        }

    }

    public void setUser(User user) throws IOException, ClassNotFoundException {
        this.user = user;
        initializePage();
    }
}