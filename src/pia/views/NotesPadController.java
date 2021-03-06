package pia.views;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import pia.*;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;


public class NotesPadController implements Initializable{

    private Vector<Parent> notes = new Vector<>();
    private List<VisibleTextNote> visibleTextNotes = new LinkedList<>();
    private Double time = 0.0;
    private NotesPersistenceManager notesPersistenceManager = PIA.notesPersistenceManager;
    public StreamPlayer player;
    Timeline scroller;
    private Parent noteHint;

    @FXML
    private AnchorPane root;

    @FXML
    private Pane notesPad;

    public NotesPadController() {
    }

    /**
     * Get the next available position in the pad from the left side.
     *
     * @return position from left
     */
    private double getAvailablePosLeft() {
        double widths = 0;
        for (Parent note : notes) {
            widths += note.getLayoutBounds().getWidth();
        }
        return notesPad.getPadding().getLeft() + widths;
    }

    /**
     * Get the next available position in the pad from the right side.
     * Please keep in mind that this is only a point. You might want to subtract
     * the new note's width.
     *
     * @return position from right
     */
    private double getAvailablePosRight() {
        return notesPad.getWidth() - notesPad.getPadding().getRight();
    }

    private void autoScroll() {
        final double frametime = 1.0 / 24;
    }

    private void adjustNotePositions() {
        for (Parent note : notes)
            note.setLayoutX(note.getLayoutX() - time);
    }

    /**
     * Prepare a note that is used when hovering over the notespad.
     */
    private void prepareNoteHint() {
        if (noteHint == null) {
            noteHint = preCreateNote();
        }
        noteHint.setOpacity(0.5);
        noteHint.lookup("#noteTextArea").setVisible(false);
        notesPad.getChildren().add(noteHint);
        noteHint.setVisible(false);
    }

    @FXML
    public void showNoteHint(MouseEvent event) {
        noteHint.setVisible(true);
        if (event != null)
            noteHint.setLayoutX(event.getX());
    }

    @FXML
    public void hideNoteHint(MouseEvent event) {
        noteHint.setVisible(false);
    }

    @FXML
    public void moveNoteHint(MouseEvent event) {
        boolean hide = false;
        for (Parent note: notes) {
            // hide if cursor is closer than one note length to this note
            double width = note.getLayoutBounds().getWidth();
            double mid = note.getLayoutX()+width/2;
            if (event.getX() < mid+width && event.getX() > mid-width) {// within the range
                hide = true;
                hideNoteHint(null);
                break;
            }
        }
        if (hide == false) {
            showNoteHint(null);
        }
        noteHint.setLayoutX(event.getX() - noteHint.getLayoutBounds().getWidth() / 2);
    }

    @FXML
    /**
     * This method shall be called when the user adds a note.
     */
    public void addNewNoteFromUser(MouseEvent event) {

        // create the the visibleNote, add it to pane
        VisibleTextNote newNote = createNoteFromUser();
        notesPad.getChildren().add(newNote.getNoteNode());

        // set the time
        newNote.setTime((long) event.getX() - 60);

        notes.add(newNote.getNoteNode());
    }


    public Parent addNewNoteFromOutside(NoteInformation ni, int index) {

        // create a new visibleTextNote
        VisibleTextNote visibleTextNote = createNoteFromOutside(index);
        visibleTextNote.setIndex(index);

        final Parent noteNode = visibleTextNote.getNoteNode();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                notesPad.getChildren().add(noteNode);
            }
        });


        notes.add(visibleTextNote.getNoteNode());
        return visibleTextNote.getNoteNode();
    }

    /**
     * creates a new Note and adds this to the notesPersistenceManager. This method should be called, when
     * the user adds a note.
     * @return a VisibleTextNote with a reference to the FXML Text.
     */
    private VisibleTextNote createNoteFromUser(){

        // add a new note in notesPersistenceManager
        final int index = notesPersistenceManager.addNote(NoteType.TEXT);

        // gets the note from notesPersistenceManager
        final NoteInformation noteInformation = notesPersistenceManager.getAllNotes().get(index);

        // create a visibleNote
        VisibleTextNote visibleTextNote = new VisibleTextNote(noteInformation);
        visibleTextNote.setIndex(index);

        // add the visibleNote to the List
        visibleTextNotes.add(visibleTextNote);

        // and return the expected reference
        return visibleTextNote;
    }

    private VisibleTextNote createNoteFromOutside(int index){

        // gets the note from notesPersistenceManager
        final NoteInformation noteInformation = notesPersistenceManager.getAllNotes().get(index);

        // create a visibleNote
        VisibleTextNote visibleTextNote = new VisibleTextNote(noteInformation);
        visibleTextNote.setIndex(index);

        // add the visibleNote to the List
        visibleTextNotes.add(visibleTextNote);

        // and return the expected reference
        return visibleTextNote;
    }

    private Parent preCreateNote() {
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
     *
     * @throws IOException
     */
    private void preloadNote() {
        Stage stage = new Stage();
        Parent note = preCreateNote();
        Scene scene = new Scene(note);
        stage.setScene(scene);
        stage.show();
        stage.hide();
        stage.close();
    }

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Preload a note
        preloadNote();
        // Prepare a note for creation hint
        prepareNoteHint();

        double frametime = 1.0 / 30;
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
        //int i = PIA.notesPersistenceManager.addNote(NoteType.TEXT);
        //
        //// lock the note
        //PIA.notesPersistenceManager.lockNote(i);
        //
        //// get the note
        //List<NoteInformation> allNotes = PIA.notesPersistenceManager.getAllNotes();
        //NoteInformation noteInformation = allNotes.get(i);
        //
        //// edit the note
        //String string = "Dies ist ein neuer Text";
        //((TextNoteInformation) noteInformation).setText(string);
        //((TextNoteInformation) noteInformation).setTimePosition(400l);
        //
        //// save it to notesPersistenceManager
        //PIA.notesPersistenceManager.refreshNote(i, noteInformation);
        //
        //// unlock the note
        //PIA.notesPersistenceManager.unlockNote(i);


        //List<NoteInformation> storedNotes = PIA.notesPersistenceManager.getAllNotes();
        //for (NoteInformation note: storedNotes) {
        //    addNewNoteFromUser(note);
        //}

        //TextNoteInformation testnote = new TextNoteInformation(100l, "dies ist ein text");
        //addNewNoteFromUser(testnote);

        int index = 0;
        for (NoteInformation noteInformation : notesPersistenceManager.getAllNotes()) {
            addNewNoteFromOutside(noteInformation, index);
            index++;
        }


        PIA.notesPersistenceManager.addListener(new NotesPersistenceManagerListener() {
            @Override
            public void onAdd(int indexOfAddedNote) {
                System.out.println("onAdd is Called!!!!!!!!!!!!!!!!!");
                NoteInformation note = PIA.notesPersistenceManager.getAllNotes().get(indexOfAddedNote);
                addNewNoteFromOutside(note, indexOfAddedNote);
            }

            @Override
            public void onChange(int indexOfChangedNote) {
                System.out.println("onChange is Called!!!!!!!!!!!!!!!!!");
                System.out.println("     size before: " + visibleTextNotes.size());
                VisibleTextNote visibleTextNote = null;

                // find the visibleTextNote which was changed
                for (VisibleTextNote textNote : visibleTextNotes) {
                    if (textNote.getIndex() == indexOfChangedNote) {
                        visibleTextNote = textNote;
                    }
                }

                // set the visibleTextNote
                visibleTextNote.onChange(notesPersistenceManager.getAllNotes().get(indexOfChangedNote));
                System.out.println("     size after: " + visibleTextNotes.size());
            }

            @Override
            public void onDelete(int indexOfDeletedNote) {

            }

            @Override
            public void onLocked(int indexOfLockedNote) {

                // get the accordingly note
                VisibleTextNote note = null;
                for (VisibleTextNote visibleTextNote : visibleTextNotes) {
                    if (visibleTextNote.getIndex() == indexOfLockedNote){
                        note = visibleTextNote;
                        break;
                    }
                }

                // delegate to the note
                note.gotLocked();
            }

            @Override
            public void onUnlocked(int indexOfUnlockedNote) {

                // get the accordingly note
                VisibleTextNote note = null;
                for (VisibleTextNote visibleTextNote : visibleTextNotes) {
                    if (visibleTextNote.getIndex() == indexOfUnlockedNote){
                        note = visibleTextNote;
                        break;
                    }
                }

                // delegate to the note
                note.gotUnlocked();
            }
        });


    }
}
