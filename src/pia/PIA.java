/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author kaisky89
 */
public class PIA extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        // set the Stage for further referencing into ViewManager
        SingletonViewManager.getInstance().setStage(stage);
        
        // set the locations of the different views
        SingletonViewManager.getInstance().setViewLocation("Login", "Login.fxml");
        SingletonViewManager.getInstance().setViewLocation("MainView", "MainView.fxml");
        
        // set the starting scene
        SingletonViewManager.getInstance().setScene("Login");
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}