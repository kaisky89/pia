/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author kaisky89
 */
public class LoginController implements Initializable {
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox anonymLoginCheckBox;
    
    @FXML
    private void handleButtonLogin(ActionEvent event){
        login();
    }
    
    @FXML
    private void handleActionUsernameTextField(ActionEvent event){
        login();
    }
    
    @FXML
    private void handleActionPasswordField(ActionEvent event){
        login();
    }
    
    @FXML
    private void handleCheckAnonym(ActionEvent event){
        // TODO
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    private void login() {
        boolean isAnonymous = false;
        SingletonDataStore.getInstance().setUser(new UserData(usernameField.getText(), passwordField.getText(), isAnonymous));
        try {
            SingletonSmack.getInstance().init();
            SingletonViewManager.getInstance().setScene("MainView");
        } catch (NotesCommunicatorException ex) {
            SingletonViewManager.getInstance().showError(ex);
        }
    }
}
