package com.tweeter;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.*;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorMessageLabel;
    @FXML
    private ImageView twitterLogo;

    @FXML
    public void handleLoginButton() throws IOException, ClassNotFoundException, IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        //sending request
        ObjectOutputStream OOS=Tweeter.getOOS();
        ObjectInputStream OIS=Tweeter.getOIS();
        OOS.writeObject(new String("signIn"));
        OOS.writeObject(username);
        OOS.writeObject(password);
        //getting result
        String result = (String) OIS.readObject();
        if (result.equals("success")) {
            File file=Tweeter.getFile();
            Tweeter.setToken((String) OIS.readObject());
            Tweeter.setClient((User) OIS.readObject());
            //save token to local directory
            file.getParentFile().mkdirs();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
            objectOutputStream.writeObject(Tweeter.getToken());
            objectOutputStream.close();
            Tweeter.enterHomePage();
        } else {
            errorMessageLabel.setText(result);
        }
    }
    @FXML
    public void handleSignupLink() throws IOException {
        Tweeter.signupPage();
    }
    public void initialize(){
        twitterLogo.setImage(new Image(getClass().getResourceAsStream("/assets/twitterLogo.png")));
    }

}
