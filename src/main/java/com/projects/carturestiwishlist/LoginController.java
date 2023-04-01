package com.projects.carturestiwishlist;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    User currUser;

    @FXML
    private Button LoginButton;

    @FXML
    private Button CreateAccountButton;

    @FXML
    private TextField UsernameTF;

    @FXML
    private TextField PasswordTF;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        CreateAccountButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                DBUtils.changeScene(actionEvent, "Signup.fxml", "SignUp", currUser);
            }
        });

        LoginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (!UsernameTF.getText().trim().isEmpty() && !PasswordTF.getText().isEmpty()) {
                    DBUtils.logInUser(actionEvent, UsernameTF.getText(), PasswordTF.getText(),currUser);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("One of the fields is empty");
                    alert.show();
                }
            }
        });
    }


}
