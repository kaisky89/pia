package pia.views;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import pia.NoteInformation;
import pia.NoteType;
import pia.PIA;
import pia.TextNoteInformation;

import java.io.IOException;
import java.util.List;

public class VisibleTextNote {

    private Parent noteNode;
    private TextArea textArea;
    private TextNoteInformation noteInformation;
    private int id;

    public VisibleTextNote() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Note.fxml"));
        try {
            noteNode = (Parent) fxmlLoader.load();
            textArea = (TextArea) noteNode.lookup("#nodeTextArea");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public VisibleTextNote(NoteInformation ni) {
        this();

        id = PIA.notesManager.addNote(NoteType.TEXT);

        // lock the note
        PIA.notesManager.lockNote(id);

        // get the note
        List<NoteInformation> allNotes = PIA.notesManager.getAllNotes();
        NoteInformation noteInformation = allNotes.get(id);
    }

    public void setText(String text) {
        textArea.setText(text);
        noteInformation.setText(text);
        // save it to notesManager
        PIA.notesManager.refreshNote(id, noteInformation);
    }

    public void setTime(long time) {
        noteInformation.setTimePosition(time);
        // save it to notesManager
        PIA.notesManager.refreshNote(id, noteInformation);
    }

    public void lock() {
        PIA.notesManager.lockNote(id);
    }

    public void unlock() {
        PIA.notesManager.unlockNote(id);
    }
}
