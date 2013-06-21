/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialogs;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * SingletonClass which manages the View of this Application. The methods of
 * this class allows you to change the View, where you are currently in.
 * Additionaly it is possible to show errors or ask for an String via dialogs.
 * Before <code>setScene()</code> can be used, scenes must be defined via
 * <code>addViewLocation()</code>. Also, the <Code>Stage</code>, which controls
 * the JavaFX window, needs to be specified via <code>setStage()</code>.
 * @author kaisky89
 */
public class SingletonViewManager {

    private static SingletonViewManager instance = new SingletonViewManager();
    private Stage stage;
    private Map<String, String> viewLocation = new HashMap<>();
    
    /**
     * Private Constructor. Access to instance of this Class only via the static
     * method <code>getInstance()</code>
     */
    private SingletonViewManager(){
    }
    
    /**
     * Returns the instance of this Singleton.
     * @return instance of <code>SingletonViewManager</code>
     */
    public static SingletonViewManager getInstance(){
        return instance;
    }

    /**
     * Sets the Stage, which will be used for View Management.
     * @param stage JavaFX Stage for the View Management.
     */
    public void setStage(Stage stage){
        this.stage = stage;
    }

    /**
     * Returns the stage, which is currently used for View Management.
     * @return currently used JavaFX Stage for the View Management.
     */
    public Stage getStage(){
        return stage;
    }

    /**
     * Changes the View to the new Scene. Old Scene will be unloaded 
     * automatically. Scenes you want to use here need to be previously specified
     * using <code>addViewLocation()</code>. If loading fails, the error is printed
     * and neither scene nor stage are changed.
     * @param sceneName The Name of the new Scene. Is specified in <code>
     * addViewLocation()</code>.
     */
    public void setScene(String sceneName){
        setScene(sceneName, true);
    }

    /**
     * Changes the View to the new Scene. Old Scene will be unloaded
     * automatically. Scenes you want to use here need to be previously specified
     * using <code>addViewLocation()</code>. If loading fails, the error is printed
     * and neither scene nor stage are changed.
     * @param sceneName The Name of the new Scene. Is specified in <code>
     * addViewLocation()</code>.
     * @param newStage Create new Stage for this Scene?
     */
    public void setScene(String sceneName, boolean newStage) {
        String filename = getViewLocation(sceneName);
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource(filename));
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            return;
        }
        Scene scene = new Scene(root);
        if (newStage) {
            stage = new Stage();
            stage.setTitle(sceneName);
        }
        stage.setScene(scene);
        stage.setMinHeight(scene.getHeight());
        stage.setMinWidth(scene.getWidth());
        stage.show();
    }

    /**
     * Specifies a new Scene with its name and the fxml file which describes the
     * scene.
     * @param name String of the name. Will be used to reference to the Scene in
     * <code>setScene()</code>.
     * @param location Path to the fxml file which describes the scene.
     */
    public void addViewLocation(String name, String location){
        viewLocation.put(name, location);
    }

    /**
     * Returns the path to the fxml file for the specified scene.
     * @param name The Scene name.
     * @return The path of the fxml file as String.
     */
    public String getViewLocation(String name){
        return viewLocation.get(name);
    }

    /**
     * Shows an error dialog with the exception message.
     * @param ex The exception which caused the error.
     */
    public void showError(Exception ex) {
        ex.printStackTrace();
        Dialogs.showErrorDialog(stage, ex.getLocalizedMessage(), "An Error Occured", "Error", ex);
    }

    /**
     * Shows a Dialog to ask for a User Input. Input will be returned as a 
     * String.
     * @param title The title of the dialog Window.
     * @param subtitle The subtitle of the dialog Window.
     * @param operation 
     * @return 
     */
    public String askInput(String title, String subtitle, String operation){
        return Dialogs.showInputDialog(stage, operation, subtitle, title);
    }
}