/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author kaisky89
 */
public class LoginController implements Initializable {
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button anonLoginButton;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

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
            SingletonViewManager.getInstance().setScene("MainView");
        } catch (NotesCommunicatorException ex) {
            SingletonViewManager.getInstance().showError(ex);
        }
    }
}
