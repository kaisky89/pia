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
        //login(false);
    }
    
    @FXML
    private void handleActionUsernameTextField(ActionEvent event){
        passwordField.requestFocus();
    }
    
    @FXML
    private void handleActionPasswordField(ActionEvent event) {
        login(false);
    }

    private void login(boolean anon) {
        SingletonDataStore.getInstance().setUser(new UserData(usernameField.getText(),
                passwordField.getText(), anon));
        try {
            SingletonSmack.getInstance().init();

            NotesCommunicator smack = SingletonSmack.getInstance();

            smack.init();
            if (smack.getSessionIds().size() > 0) {
                Integer sessionID = smack.getSessionIds().get(0);
                smack.setUsingSession(sessionID);
            } else {
                Integer sessionID = smack.addSession(new SessionInformation("test session", PIA.url,
                        "description should not be empty"));
                smack.setUsingSession(sessionID);
            }

            PIA.notesPersistenceManager = new NotesPersistenceManager();


            SingletonViewManager.getInstance().setScene("PIA", false);
        } catch (NotesCommunicatorException ex) {
            SingletonViewManager.getInstance().showError(ex);
        }

    }
}
