package com.tweeter;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;

public class EditProfileController {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField locationField;

    @FXML
    private TextField webAddressField;

    @FXML
    private TextArea bioArea;
    @FXML
    private ImageView twitterLogo;
    @FXML
    private Label errorMessageLabel;
    @FXML
    private ImageView avatarImageView;

    @FXML
    private ImageView headerImageView;
    @FXML
    private Button deleteAvatarButton;
    @FXML
    private Button deleteHeaderButton;
    private File selectedAvatar;
    private File selectedHeader;
    private boolean deletingAvatar = false;
    private boolean deletingHeader=false;



    @FXML
    public void saveProfile() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String location = locationField.getText();
        String webAddress = webAddressField.getText();
        String bio = bioArea.getText();
        if (bio.length() > 160) {
            errorMessageLabel.setText("bio has a maximum of 160 characters");
        } else{
            String command = "updateUser";
            try {
                //change user  here in client side and then pass to server to change in server side
                User user=Tweeter.getClient();
                ObjectOutputStream OOS=Tweeter.getOOS();
                if(!firstName.trim().equals("")){
                    user.setFirstName(firstName);
                }
                if(!lastName.trim().equals("")){
                    user.setLastName(lastName);
                }
                if(!location.trim().equals("")){
                    user.setLocation(location);
                }
                if(!webAddress.trim().equals("")){
                    user.setWebAddress(webAddress);
                }
                if(!bio.trim().equals("")){
                    user.setBio(bio);
                }
                if(selectedAvatar!=null){
                    Tweeter.getClient().setAvatar(Files.readAllBytes(selectedAvatar.toPath()));
                }
                if (selectedHeader != null) {
                    Tweeter.getClient().setHeader(Files.readAllBytes(selectedHeader.toPath()));
                }
                if(deletingAvatar){
                    user.setAvatar(null);
                }
                if(deletingHeader){
                    user.setHeader(null);
                }
                OOS.writeObject(command);
                OOS.writeObject(user);
                Tweeter.enterProfilePage();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteAvatar(){
        deletingAvatar=!deletingAvatar;
        if(deletingAvatar){
            deleteAvatarButton.setText("Current avatar will be deleted");
        } else {
            deleteAvatarButton.setText("Delete current avatar");
        }
    }
    public void deleteHeader(){
        deletingHeader=!deletingHeader;
        if(deletingHeader){
            deleteHeaderButton.setText("Current header will be deleted");
        } else {
            deleteHeaderButton.setText("Delete current header");
        }
    }
    @FXML
    private void selectAvatarImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Avatar Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        Stage stage =Tweeter.getStage();
        selectedAvatar = fileChooser.showOpenDialog(stage);
        if (selectedAvatar != null) {
            Image image = new Image(selectedAvatar.toURI().toString());
            avatarImageView.setImage(image);
        }
    }

    @FXML
    private void selectHeaderImage() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Header Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        Stage stage =Tweeter.getStage();
        selectedHeader = fileChooser.showOpenDialog(stage);

        if (selectedHeader != null) {
            Image image = new Image(selectedHeader.toURI().toString());
            headerImageView.setImage(image);
        }
    }
    @FXML
    public void backSelected() throws IOException {
        Tweeter.enterProfilePage();
    }
    public void initialize(){
        twitterLogo.setImage(new Image(getClass().getResourceAsStream("/assets/twitterLogo.png")));
    }
}