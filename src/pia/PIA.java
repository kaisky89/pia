package pia;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class PIA extends Application {

    public static SingletonViewManager viewManager;
    public static StreamPlayer streamPlayer;
    public static NotesPersistenceManager notesPersistenceManager;
    // stream URL
    //final String url = "http://familie-wittmann.dyndns.org/downloads/kurzerSong.mp3";
    public static final String url = "http://meta.metaebene.me/media/mm/mm112-in-werbung-bewegen.mp3";

    @Override
    public void start(Stage stage) throws Exception {

        // start View Manager
        viewManager = SingletonViewManager.getInstance();

        // start Stream Player
        streamPlayer = new StreamPlayer(url);

        // set the locations of the different views
        viewManager.addViewLocation("PIA Login", "views/Login.fxml");
        viewManager.addViewLocation("PIA", "views/MainWindow.fxml");

        
        // set the starting scene
        viewManager.setStage(stage);
        viewManager.setScene("PIA Login");

        // close cleanly
        viewManager.getStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (streamPlayer != null) {
                    streamPlayer.release();
                }
            }
        });
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