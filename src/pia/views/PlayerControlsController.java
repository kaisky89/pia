package pia.views;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import pia.PIA;
import pia.PlayerEventListener;
import pia.StreamPlayer;
import uk.co.caprica.vlcj.player.MediaPlayer;


public class PlayerControlsController {


    @FXML private AnchorPane root;
    @FXML private Button previousTopicButton;
    @FXML private Button seekBackwardButton;
    @FXML private Button playPauseButton;
    @FXML private Button seekForwardButton;
    @FXML private Button nextTopicButton;
    @FXML private Slider volumeSlider;
    @FXML private Pane playPauseButtonIcon;

    public StreamPlayer player;

    @FXML
    public void previousTopic(ActionEvent event) {
        // TODO: do really seek the last topic
        player.seek(0);
    }

    @FXML
    public void seekBackward(ActionEvent event) {
        player.seekRelative(-30);
    }

    @FXML
    private void toggle() {
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

    @FXML
    public void seekForward(ActionEvent event) {
        player.seekRelative(30);
    }

    @FXML
    public void nextTopic(ActionEvent event) {
        player.seekRelative(-30);
    }

    public void changeVolume(Number volume) {
        player.setVolume(volume.intValue());
    }

    @FXML
    void initialize() {
        // TODO: enable if there are any topics
        nextTopicButton.setDisable(true);
        //previousTopicButton.setDisable(true);

        // change volume whenever and however the slider value changes
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> value, Number oldValue,
                                Number newValue) {
                changeVolume(newValue);
            }
        });

        player = PIA.streamPlayer;

        if (player.isPlaying())
            playPauseButtonIcon.setId("pause");
        else
            playPauseButtonIcon.setId("play");

        player.addPlayingListener(new PlayerEventListener() {
            @Override
            public void actionPerformed(MediaPlayer player) {
                playPauseButtonIcon.setId("pause");
            }
        });
        player.addPausedListener(new PlayerEventListener() {
            @Override
            public void actionPerformed(MediaPlayer player) {
                playPauseButtonIcon.setId("play");
            }
        });
        player.addFinishedListener(new PlayerEventListener() {
            @Override
            public void actionPerformed(MediaPlayer player) {
                playPauseButtonIcon.setId("play");
            }
        });
    }
}
