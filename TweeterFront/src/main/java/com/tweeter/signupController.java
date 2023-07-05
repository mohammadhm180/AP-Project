package com.tweeter;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.*;
import java.time.LocalDateTime;

public class signupController {
    @FXML
    private TextField usernameField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField repeatPasswordField;

    @FXML
    private DatePicker birthdatePicker;

    @FXML
    private Label errorMessageLabel;
    @FXML
    private ComboBox<String> comboBox;

    @FXML
    public void signupButtonPressed( ) throws IOException, ClassNotFoundException {
        String username=usernameField.getText().trim();
        String firstname =firstNameField.getText().trim();
        String lastname = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phoneNumber = phoneField.getText().trim();
        String password = passwordField.getText().trim();
        String repeatPassword=repeatPasswordField.getText().trim();
        String country = comboBox.getValue();
        LocalDateTime birthdate=null;
        if(country==null){
            errorMessageLabel.setText("Select your country");
        } else {
            try {
                birthdate= birthdatePicker.getValue().atStartOfDay();
                if(email.equals("") && phoneNumber.equals("")){
                    errorMessageLabel.setText("At least one of phone number or email must be filled");
                } else if(!password.equals(repeatPassword)) {
                    errorMessageLabel.setText("passwords does not match!");
                }else if(username.equals("")){
                    errorMessageLabel.setText("Fill username");
                } else if(firstname.equals("")){
                    errorMessageLabel.setText("Fill your first name");
                } else {
                    LocalDateTime signUpDate = LocalDateTime.now();
                    User client = new User(username, firstname, lastname, email, phoneNumber, password, country, birthdate, signUpDate);
                    //sending request
                    ObjectOutputStream OOS=Tweeter.getOOS();
                    ObjectInputStream OIS=Tweeter.getOIS();
                    OOS.writeObject(new String("signUp"));
                    //sending user
                    OOS.writeObject(client);
                    //getting result
                    String result = (String) OIS.readObject();
                    if (result.equals("success")) {
                        File file=Tweeter.getFile();
                        //getting token
                        String token=(String) OIS.readObject();
                        Tweeter.setToken(token);
                        Tweeter.setClient(client);
                        //save token to local directory
                        file.getParentFile().mkdirs();
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
                        objectOutputStream.writeObject(token);
                        objectOutputStream.close();
                        Tweeter.enterHomePage();
                    } else {
                        errorMessageLabel.setText(result);
                    }
                }
            } catch (NullPointerException e){
                errorMessageLabel.setText("Fill Birthdate field");
            }
        }
    }
    @FXML
    public void backButtonPressed() throws IOException {
        Tweeter.enterProgram();
    }
    public void initialize(){
        String[] countries = {"Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Antigua and Barbuda", "Argentina",
                "Armenia", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus",
                "Belgium", "Belize", "Benin", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana", "Brazil", "Brunei",
                "Bulgaria", "Burkina Faso", "Burundi", "Cabo Verde", "Cambodia", "Cameroon", "Canada", "Central African Republic",
                "Chad", "Chile", "China", "Colombia", "Comoros", "Congo", "Costa Rica", "Croatia", "Cuba", "Cyprus", "Czechia",
                "Denmark", "Djibouti", "Dominica", "Dominican Republic", "East Timor", "Ecuador", "Egypt", "El Salvador",
                "Equatorial Guinea", "Eritrea", "Estonia", "Eswatini", "Ethiopia", "Fiji", "Finland", "France", "Gabon",
                "Gambia", "Georgia", "Germany", "Ghana", "Greece", "Grenada", "Guatemala", "Guinea", "Guinea-Bissau",
                "Guyana", "Haiti", "Honduras", "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland",
                "Israel", "Italy", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Korea, North", "Korea, South",
                "Kosovo", "Kuwait", "Kyrgyzstan", "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein",
                "Lithuania", "Luxembourg", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands",
                "Mauritania", "Mauritius", "Mexico", "Micronesia", "Moldova", "Monaco", "Mongolia", "Montenegro", "Morocco",
                "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands", "New Zealand", "Nicaragua", "Niger",
                "Nigeria", "North Macedonia", "Norway", "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay",
                "Peru", "Philippines", "Poland", "Portugal", "Qatar", "Romania", "Russia", "Rwanda", "Saint Kitts and Nevis",
                "Saint Lucia", "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia",
                "Senegal", "Serbia", "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands", "Somalia",
                "South Africa", "South Sudan", "Spain", "Sri Lanka", "Sudan", "Suriname", "Sweden", "Switzerland", "Syria",
                "Taiwan", "Tajikistan", "Tanzania", "Thailand", "Togo", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey",
                "Turkmenistan", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States of America",
                "Uruguay", "Uzbekistan", "Vanuatu", "Vatican City", "Venezuela", "Vietnam", "Yemen", "Zambia", "Zimbabwe"};

        comboBox.setItems(FXCollections.observableArrayList(countries));
    }
}