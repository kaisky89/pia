/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.stage.Stage;

/**
 *
 * @author kaisky89
 */
public class PIA extends Application {

    public SingletonViewManager viewManager;
    public static SimpleObjectProperty<StreamPlayer> playerProperty = new
            SimpleObjectProperty<StreamPlayer>();
    public StreamPlayer streamPlayer;
    public NotesManager notesManager;

    @Override
    public void start(Stage stage) throws Exception {
        // stream URL
        //final String url = "http://familie-wittmann.dyndns.org/downloads/kurzerSong.mp3";
        final String url = "http://meta.metaebene.me/media/mm/mm112-in-werbung-bewegen.mp3";

        // init all managers
        viewManager = SingletonViewManager.getInstance();

        // set the locations of the different views
        viewManager.addViewLocation("PIA Login", "views/Login.fxml");
        viewManager.addViewLocation("PIA", "views/MainWindow.fxml");
        //viewManager.addViewLocation("Note", "views/Note.fxml");
        //viewManager.addViewLocation("NotesTest", "views/NotesTest.fxml");
        viewManager.addViewLocation("XMPP Test", "views/XmppTest.fxml");

        
        // set the starting scene
        viewManager.setStage(stage);
        viewManager.setScene("PIA");

        // close cleanly
        viewManager.getStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (streamPlayer != null) {
                    streamPlayer.destroy();
                }
            }
        });

        NotesCommunicator smack = SingletonSmack.getInstance();
        SingletonDataStore.getInstance().setUser(new UserData("user1", "123"));
        smack.init();
        if (smack.getSessionIds().size() > 0) {
            Integer sessionID = smack.getSessionIds().get(0);
            smack.setUsingSession(sessionID);
        } else {
            Integer sessionID = smack.addSession(new SessionInformation("test session", url,
                    "description should not be empty"));
            smack.setUsingSession(sessionID);
        }
        NotesManagerListener notesListener = new NotesManagerListener();
        NotesManager manager = new NotesManager(notesListener);


        // Stream player needs to be run in an other thread
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                streamPlayer = new StreamPlayer(url);
                //streamPlayer.playStream();
                //((MainWindowController) viewManager.getController("PIA")).setPlayer
                // (streamPlayer);
                playerProperty.setValue(streamPlayer);
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