package pia.views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import pia.*;

public class LoginController {
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button anonLoginButton;


//    public void initialize(URL url, ResourceBundle rb) {
        // Alternative event handling
//        loginButton.setOnAction(new EventHandler<ActionEvent>() {
//
//            public void handle(ActionEvent event) {
//                System.out.println("Hello World");
//            }
//        });
//    }

    @FXML
    private void handleLogin(ActionEvent event){
        login(false);
    }

    @FXML
    private void handleAnonLogin(ActionEvent event){
        login(true);
    }
    
    @FXML
    private void handleActionUsernameTextField(ActionEvent event){
        passwordField.requestFocus();
    }
    
    @FXML
    private void handleActionPasswordField(ActionEvent event){
        loginButton.requestFocus();
    }

    private void login(boolean anon) {
        SingletonDataStore.getInstance().setUser(new UserData(usernameField.getText(),
                passwordField.getText(), anon));
        try {
            SingletonSmack.getInstance().init();
            SingletonViewManager.getInstance().setScene("PIA", false);
        } catch (NotesCommunicatorException ex) {
            SingletonViewManager.getInstance().showError(ex);
        }
    }
}
