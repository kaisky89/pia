package pia.views;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import pia.StreamPlayer;

import java.io.IOException;
import java.util.Vector;


public class NotesTestController {

    private Vector<Parent> notes = new Vector<Parent>();
    Double time = 0.0;

    @FXML
    private Pane notesPad;

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
//        final double frametime = 1.0/24;
//        Timeline scroller = new Timeline(new KeyFrame(Duration.seconds(frametime),
//                new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                time += frametime;
//
//            }
//        }));
//        scroller.setCycleCount(Timeline.INDEFINITE);
//        scroller.play();
    }

    private void adjustNotePositions() {
        for (Parent note: notes)
            note.setLayoutX(note.getLayoutX()-time);
    }

    @FXML
    public Parent addNewNote() throws IOException {
        Parent newNote = createNote();
        notesPad.getChildren().add(newNote);
        newNote.setLayoutX(getAvailablePosRight());
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

        StreamPlayer player = new StreamPlayer();
        player.play();

        autoScroll();

        Timeline noteAdder = new Timeline(new KeyFrame(Duration.seconds(1),
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        try {
                            addNewNote();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }));
        noteAdder.setCycleCount(5);
        //noteAdder.play();
    }
}
