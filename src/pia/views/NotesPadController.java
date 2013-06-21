package pia.views;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import pia.PIA;
import pia.StreamPlayer;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;

import java.io.IOException;
import java.util.Vector;


public class NotesPadController {

    private Vector<Parent> notes = new Vector<Parent>();
    private Double time = 0.0;
    public StreamPlayer player;
    Timeline scroller;

    @FXML
    private AnchorPane root;

    @FXML
    private Pane notesPad;

    public NotesPadController() {
    }

    /**
     * Get the next available position in the pad from the left side.
     * @return position from left
     */
    private double getAvailablePosLeft() {
        double widths = 0;
        for (Parent note: notes) {
            widths += note.getLayoutBounds().getWidth();
        }
        return notesPad.getPadding().getLeft() + widths;
    }
    /**
     * Get the next available position in the pad from the right side.
     * Please keep in mind that this is only a point. You might want to subtract
     * the new note's width.
     * @return position from right
     */
    private double getAvailablePosRight() {
        return notesPad.getWidth()-notesPad.getPadding().getRight();
    }

    private void autoScroll() {
        final double frametime = 1.0/24;
    }

    private void adjustNotePositions() {
        for (Parent note: notes)
            note.setLayoutX(note.getLayoutX()-time);
    }

    @FXML
    public Parent addNewNote(MouseEvent event) throws IOException {
        Parent newNote = createNote();
        notesPad.getChildren().add(newNote);
        // TODO: find out the width dynamically
        newNote.setLayoutX(event.getX()-60);
        newNote.setLayoutY(0);
        notes.add(newNote);
        return newNote;
    }

    private Parent createNote() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Note.fxml"));
        return (Parent) fxmlLoader.load();
    }

    /**
     * Preload a note so that new notes are created with a smaller time penalty.
     * @throws IOException
     */
    private void preloadNote() throws IOException {
        Stage stage = new Stage();
        Parent note = createNote();
        Scene scene = new Scene(note);
        stage.setScene(scene);
        stage.show();
        stage.hide();
        stage.close();
   }

    @FXML
    void initialize() throws IOException {
        // Preload a note
        preloadNote();

        double frametime = 1.0/30;
        scroller = new Timeline(new KeyFrame(Duration.seconds(frametime),
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        //time += frametime;
                        //System.out.println(player.player.getTime());
                        //System.out.println(player.player.isSeekable());
                        //System.out.println(player.player.getMediaState());

                    }
                }));
        scroller.setCycleCount(Timeline.INDEFINITE);
        //scroller.play();



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

                    }

                    @Override
                    public void paused(MediaPlayer player) {

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
                        System.out.println("pos changed");
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

        autoScroll();





    }
}
