package pia.views;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import pia.*;

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
    public Parent addNewNote(MouseEvent event) {
        Parent newNote = createNote();
        notesPad.getChildren().add(newNote);
        // TODO: find out the width dynamically
        newNote.setLayoutX(event.getX()-60);
        newNote.setLayoutY(0);
        notes.add(newNote);
        return newNote;
    }

    public Parent addNewNote(NoteInformation ni) {
        Parent note = createNote();
        double time = ni.getTimePosition();
        note.setLayoutX(time);
        note.setLayoutY(0);
        if (ni.getNoteType().equals(NoteType.TEXT)) {
            TextNoteInformation textnote = (TextNoteInformation) ni;
            Node textareanode = note.lookup("#noteTextArea");
            TextArea textarea = (TextArea) textareanode;
            textarea.setText(textnote.getText());
        }
        notes.add(note);
        notesPad.getChildren().add(note);
        return note;
    }

    private Parent createNote() {
        //System.out.println("creating new note");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Note.fxml"));
        try {
            return (Parent) fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Preload a note so that new notes are created with a smaller time penalty.
     * @throws IOException
     */
    private void preloadNote() {
        Stage stage = new Stage();
        Parent note = createNote();
        Scene scene = new Scene(note);
        stage.setScene(scene);
        stage.show();
        stage.hide();
        stage.close();
   }

    @FXML
    void initialize() {
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

        //
        //autoScroll();
        //
        //
        //
        // add note
        //int i = PIA.notesManager.addNote(NoteType.TEXT);
        //
        //// lock the note
        //PIA.notesManager.lockNote(i);
        //
        //// get the note
        //List<NoteInformation> allNotes = PIA.notesManager.getAllNotes();
        //NoteInformation noteInformation = allNotes.get(i);
        //
        //// edit the note
        //String string = "Dies ist ein neuer Text";
        //((TextNoteInformation) noteInformation).setText(string);
        //((TextNoteInformation) noteInformation).setTimePosition(400l);
        //
        //// save it to notesManager
        //PIA.notesManager.refreshNote(i, noteInformation);
        //
        //// unlock the note
        //PIA.notesManager.unlockNote(i);



        //List<NoteInformation> storedNotes = PIA.notesManager.getAllNotes();
        //for (NoteInformation note: storedNotes) {
        //    addNewNote(note);
        //}

        //TextNoteInformation testnote = new TextNoteInformation(100l, "dies ist ein text");
        //addNewNote(testnote);


        PIA.notesManager.addListener(new NotesManagerListener() {
            @Override
            public void onAdd(int indexOfAddedNote) {
                NoteInformation note = PIA.notesManager.getAllNotes().get(indexOfAddedNote);
                addNewNote(note);
            }

            @Override
            public void onChange(int indexOfChangedNote) {

            }

            @Override
            public void onDelete(int indexOfDeletedNote) {

            }

            @Override
            public void onLocked(int indexOfLockedNote) {

            }

            @Override
            public void onUnlocked(int indexOfUnlockedNote) {

            }
        });


    }
}
