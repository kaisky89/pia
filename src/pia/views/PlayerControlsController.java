package pia.views;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import pia.PIA;
import pia.StreamPlayer;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.binding.internal.libvlc_state_t;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;


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
            libvlc_state_t state = player.player.getMediaState();
            if (state == libvlc_state_t.libvlc_Playing) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        player.player.pause();
                    }
                });

            } else if (state == libvlc_state_t.libvlc_Paused) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        player.player.play();
                    }
                });

            }
        }
    }


    @FXML
    void initialize() {
        root.setDisable(true);
        nextTopicButton.setDisable(true);
        PIA.playerProperty.addListener(new ChangeListener<StreamPlayer>() {
            @Override
            public void changed(ObservableValue<? extends StreamPlayer> value,
                                StreamPlayer oldPlayer, StreamPlayer newPlayer) {
                player = newPlayer;
                root.setDisable(false);
                player.player.addMediaPlayerEventListener(new MediaPlayerEventListener() {
                    @Override
                    public void mediaChanged(MediaPlayer player, libvlc_media_t t, String s) {

                    }
                    @Override
                    public void opening(MediaPlayer player) {

                    }
                    @Override
                    public void buffering(MediaPlayer player, float v) {

                    }
                    @Override
                    public void playing(MediaPlayer player) {
                        System.out.println("playing");
                        playPauseButtonIcon.setId("pause");
                    }

                    @Override
                    public void paused(MediaPlayer player) {
                        System.out.println("paused");
                        playPauseButtonIcon.setId("play");
                    }

                    @Override
                    public void stopped(MediaPlayer player) {

                    }
                    @Override
                    public void forward(MediaPlayer player) {

                    }
                    @Override
                    public void backward(MediaPlayer player) {

                    }
                    @Override
                    public void finished(MediaPlayer player) {

                    }
                    @Override
                    public void timeChanged(MediaPlayer player, long l) {

                    }
                    @Override
                    public void positionChanged(MediaPlayer player, float v) {
                        //System.out.println("pos changed");
                    }
                    @Override
                    public void seekableChanged(MediaPlayer player, int i) {

                    }
                    @Override
                    public void pausableChanged(MediaPlayer player, int i) {

                    }
                    @Override
                    public void titleChanged(MediaPlayer player, int i) {

                    }
                    @Override
                    public void snapshotTaken(MediaPlayer player, String s) {

                    }
                    @Override
                    public void lengthChanged(MediaPlayer player, long l) {

                    }
                    @Override
                    public void videoOutput(MediaPlayer player, int i) {

                    }
                    @Override
                    public void error(MediaPlayer player) {

                    }
                    @Override
                    public void mediaMetaChanged(MediaPlayer player, int i) {

                    }
                    @Override
                    public void mediaSubItemAdded(MediaPlayer player, libvlc_media_t t) {

                    }
                    @Override
                    public void mediaDurationChanged(MediaPlayer player, long l) {

                    }
                    @Override
                    public void mediaParsedChanged(MediaPlayer player, int i) {

                    }
                    @Override
                    public void mediaFreed(MediaPlayer player) {

                    }
                    @Override
                    public void mediaStateChanged(MediaPlayer player, int i) {

                    }

                    @Override
                    public void newMedia(MediaPlayer player) {

                    }
                    @Override
                    public void subItemPlayed(MediaPlayer player, int i) {

                    }
                    @Override
                    public void subItemFinished(MediaPlayer player, int i) {

                    }
                    @Override
                    public void endOfSubItems(MediaPlayer player) {

                    }
                });
            }
        });
    }
}
