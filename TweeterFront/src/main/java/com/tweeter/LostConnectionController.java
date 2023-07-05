package com.tweeter;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.Socket;

public class LostConnectionController {
    @FXML
    private ImageView lostIcon;

    public void tryAgain() throws IOException {
        try {
            Tweeter.connectServer();
        } catch (IOException exception) {
            Tweeter.enterLostConnection();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public void initialize(){
        lostIcon.setImage(new Image(getClass().getResourceAsStream("/assets/lost3.jpg")));
    }
}
