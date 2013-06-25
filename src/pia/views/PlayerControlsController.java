package pia.views;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import pia.PIA;
import pia.StreamPlayer;


public class PlayerControlsController {

    @FXML private AnchorPane root;
    @FXML private Button previousTopicButton;
    @FXML private Button seekBackButton;
    @FXML private Button playPauseButton;
    @FXML private Pane playPauseButtonIcon;
    @FXML private Button seekForwardButton;
    @FXML private Button nextTopicButton;

    public StreamPlayer player;

    @FXML
    private void toggle() {
        if (player != null) {
            if (player.isPlaying()) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        player.pause();
                    }
                });

            } else if (player.isPaused() || player.isStopped()) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        player.play();
                    }
                });

            }
        }
    }

    @FXML
    void initialize() {
        //root.setDisable(true);
        nextTopicButton.setDisable(true);
        player = PIA.streamPlayer;
        //PIA.streamPlayer.isPlaying();
        //PIA.streamPlayer.play();
        //System.out.println(PIA.streamPlayer.getMediaPlayer().getMediaState());
        //System.out.println(PIA.streamPlayer);
        //System.out.println(String.valueOf(PIA.streamPlayer.isPlaying()));
    }
}
