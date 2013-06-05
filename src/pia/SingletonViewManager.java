/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialogs;
import javafx.stage.Stage;
import org.jivesoftware.smack.XMPPException;

/**
 *
 * @author kaisky89
 */
public class SingletonViewManager {
    private static SingletonViewManager instance = new SingletonViewManager();
    
    private SingletonViewManager(){
    }
    
    public static SingletonViewManager getInstance(){
        return instance;
    }
    
    
    
    private Stage stage;
    private Map<String, String> viewLocation = new HashMap<>();
    
    public void setStage(Stage stage){
        this.stage = stage;
    }
    
    public Stage getStage(){
        return stage;
    }

    void setScene(String sceneString) {
        Parent root = null;
        String filename = getViewLocation(sceneString);
        
        try {
            root = FXMLLoader.load(getClass().getResource(filename));
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        
        Scene scene = new Scene(root);
        
        getStage().setScene(scene);
        getStage().show();
    }
    
    public void setViewLocation(String name, String location){
        viewLocation.put(name, location);
    }
    
    public String getViewLocation(String name){
        return viewLocation.get(name);
    }

    void showError(XMPPException ex) {
        Dialogs.showErrorDialog(stage, ex.getLocalizedMessage(), "An Error Occured", "Error", ex);
    }
    
    
}
