package pia.views;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import pia.NoteInformation;
import pia.NotesPersistenceManager;
import pia.PIA;
import pia.TextNoteInformation;

import java.io.IOException;

public class VisibleTextNote {

    private NotesPersistenceManager notesPersistenceManager;
    private Parent noteNode;
    private TextArea textArea;
    private TextNoteInformation noteInformation;
    private NoteController noteController;
    private int index;
    private boolean isLockedByAnother = false;

    private VisibleTextNote() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Note.fxml"));
        try {
            noteNode = (Parent) fxmlLoader.load();
            textArea = (TextArea) noteNode.lookup("#noteTextArea");

            noteController = fxmlLoader.<NoteController>getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
        noteController.setNoteControllerListener(new NoteControllerListener() {
            @Override
            public void onEdit() {
                if (!isLockedByAnother) {
                    final String text = textArea.getText();

                    noteInformation.setText(text);
                    notesPersistenceManager.refreshNote(index, noteInformation);
                    noteInformation = (TextNoteInformation) notesPersistenceManager.getAllNotes().get(index);
                }
            }

            @Override
            public void onStartEditing() {
                if (!isLockedByAnother) {
                    lock();
                }
            }

            @Override
            public void onEndEditing() {
                if (!isLockedByAnother) {
                    unlock();
                }
            }
        });
    }

    public VisibleTextNote(NoteInformation ni) {
        this(ni, PIA.notesPersistenceManager);
    }

    public VisibleTextNote(NoteInformation noteInformation1, NotesPersistenceManager notesPersistenceManager){
        this();

        this.notesPersistenceManager = notesPersistenceManager;
        this.noteInformation = (TextNoteInformation) noteInformation1;
        noteNode.setLayoutX(noteInformation1.getTimePosition());
        noteNode.setLayoutY(0);

        textArea.setText(noteInformation.getText());
    }

    // should be called from UI
    public void setText(String text) {
        textArea.setText(text);
        noteInformation.setText(text);
        // save it to notesPersistenceManager
        PIA.notesPersistenceManager.refreshNote(index, noteInformation);
    }

    // should be called from UI
    public void setTime(long time) {
        noteNode.setLayoutX(time);

        // save it to notesPersistenceManager
        lock();
        noteInformation.setTimePosition(time);
        PIA.notesPersistenceManager.refreshNote(index, noteInformation);
        unlock();
    }

    // should be called from UI
    public void lock() {
        notesPersistenceManager.lockNote(index);
        noteInformation = (TextNoteInformation) notesPersistenceManager.getAllNotes().get(index);
    }

    // should be called from UI
    public void unlock() {
        notesPersistenceManager.unlockNote(index);
        noteInformation = (TextNoteInformation) notesPersistenceManager.getAllNotes().get(index);
    }

    public Parent getNoteNode() {
        return noteNode;
    }

    public void setIndex(int index) {
        this.index = index;
        isLockedByAnother = notesPersistenceManager.isLockedByAnother(index);
        refreshLockedView();
    }

    public int getIndex() {
        return index;
    }

    // should be called from NotesManager
    public void onChange(NoteInformation noteInformation) {
        // set the Text in the view
        TextNoteInformation textNoteInformation = (TextNoteInformation) noteInformation;
        textArea.setText(textNoteInformation.getText());

        // set the position from timestamp
        getNoteNode().setLayoutX(noteInformation.getTimePosition());

        // save the new noteInformation
        this.noteInformation = textNoteInformation;
    }


    // should be called only from notesPersistenceManager
    public void gotLocked() {
        isLockedByAnother = true;
        refreshLockedView();
    }

    // should be called only from notesPersistenceManager
    public void gotUnlocked() {
        isLockedByAnother = false;
        refreshLockedView();
    }

    private void refreshLockedView() {

        if (isLockedByAnother) {
            if (textArea.getText().startsWith("!!!")) {
                return;
            } else {
                textArea.setText("!!!" + textArea.getText());
                return;
            }
        } else {
            if (textArea.getText().startsWith("!!!")) {
                textArea.setText(textArea.getText().substring(3));
                return;
            } else {
                return;
            }
        }
    }


}
