package pia.views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import pia.StreamPlayer;


public class PlayerControlsController {


    @FXML private AnchorPane root;
    @FXML private Button previousTopicButton;
    @FXML private Button seekBackwardButton;
    @FXML private Button playPauseButton;
    @FXML private Button seekForwardButton;
    @FXML private Button nextTopicButton;
    @FXML private Pane playPauseButtonIcon;

    public StreamPlayer player;

    @FXML
    public void previousTopic(ActionEvent event) {
    }

    @FXML
    public void seekBackward(ActionEvent event) {

    }

    @FXML
    private void toggle() {

        //if (player.isPlaying()) {
        //    Platform.runLater(new Runnable() {
        //        @Override
        //        public void run() {
        //            player.pause();
        //        }
        //    });
        //
        //} else if (player.isPaused() || player.isStopped()) {
        //    Platform.runLater(new Runnable() {
        //        @Override
        //        public void run() {
        //            player.play();
        //        }
        //    });
        //}
    }

    @FXML
    public void seekForward(ActionEvent event) {
    }

    @FXML
    public void nextTopic(ActionEvent event) {
    }

    @FXML
    void initialize() {
        nextTopicButton.setDisable(true);

//        player = PIA.streamPlayer;
//
//        if (player.isPlaying())
//            playPauseButtonIcon.setId("pause");
//        else
//            playPauseButtonIcon.setId("play");
//
//        player.addPlayingListener(new PlayerEventListener() {
//            @Override
//            public void actionPerformed(MediaPlayer player) {
//                playPauseButtonIcon.setId("pause");
//            }
//        });
//        player.addPausedListener(new PlayerEventListener() {
//            @Override
//            public void actionPerformed(MediaPlayer player) {
//                playPauseButtonIcon.setId("play");
//            }
//        });

        //PIA.streamPlayer.isPlaying();
        //PIA.streamPlayer.play();
        //System.out.println(PIA.streamPlayer.getMediaPlayer().getMediaState());
        //System.out.println(PIA.streamPlayer);
        //System.out.println(String.valueOf(PIA.streamPlayer.isPlaying()));
    }

}
