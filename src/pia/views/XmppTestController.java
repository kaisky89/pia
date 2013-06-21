/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia.views;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import pia.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author kaisky89
 */
public class XmppTestController implements Initializable {

    @FXML private ListView<String> listView;


    private NotesManager notesManager;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // do the stuff for the communicator
        try {
            if (SingletonSmack.getInstance().getSessionIds().size() < 1) {
                SingletonSmack.getInstance().addSession(new SessionInformation(
                        "Test Session", "http://example.com", "Dies ist eine Test Session."));
            }
            final Integer sessionId = SingletonSmack.getInstance().getSessionIds().get(0);
            SingletonSmack.getInstance().setUsingSession(sessionId);
        } catch (NotesCommunicatorException e) {
            SingletonViewManager.getInstance().showError(e);
        }

        // init the notesManager
        notesManager = new NotesManager(new MyNotesManagerListener());

        // refresh the view
        try {
            refresh();
        } catch (InstantiationException e) {
            SingletonViewManager.getInstance().showError(e);
        }
    }

    @FXML
    private void handleButtonAdd() {

        // get the text for the note
        String text = SingletonViewManager.getInstance().askInput(
                "New Note", "Add a new Note", "Please enter the content of the new note.");

        // create the note, lock it
        final int index = notesManager.addNote(NoteType.TEXT);
        notesManager.lockNote(index);

        // get the note for editing
        TextNoteInformation textNote = null;
        try {
            textNote = (TextNoteInformation) notesManager.getAllNotes().get(index);
        } catch (InstantiationException e) {
            SingletonViewManager.getInstance().showError(e);
        }

        // edit the note
        textNote.setText(text);

        // publish the note
        notesManager.refreshNote(index, textNote);

        // unlock the note
        notesManager.unlockNote(index);

        // refresh the view
        try {
            refresh();
        } catch (InstantiationException e) {
            SingletonViewManager.getInstance().showError(e);
        }
    }

    private void refresh() throws InstantiationException {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                List<NoteInformation> allNotes = null;
                try {
                    allNotes = notesManager.getAllNotes();
                } catch (InstantiationException e) {
                    SingletonViewManager.getInstance().showError(e);
                }
                // get all items from NotesManager, set it to the gui list
                for (int i = 0; i < allNotes.size(); i++) {
                    TextNoteInformation textNote = (TextNoteInformation) allNotes.get(i);

                    if (i < listView.getItems().size()) {
                        listView.getItems().set(i, textNote.getText());
                    }
                    else {
                        listView.getItems().add(textNote.getText());
                    }
                }
            }
        });
    }


    private class MyNotesManagerListener implements NotesManagerListener {
        @Override
        public void onAdd(int indexOfAddedNote) {
            try {
                refresh();
            } catch (InstantiationException e) {
                SingletonViewManager.getInstance().showError(e);
            }
        }

        @Override
        public void onChange(int indexOfChangedNote) {
            try {
                refresh();
            } catch (InstantiationException e) {
                SingletonViewManager.getInstance().showError(e);
            }
        }

        @Override
        public void onDelete(int indexOfDeletedNote) {
            try {
                refresh();
            } catch (InstantiationException e) {
                SingletonViewManager.getInstance().showError(e);
            }
        }

        @Override
        public void onLocked(int indexOfLockedNote) {
            try {
                refresh();
            } catch (InstantiationException e) {
                SingletonViewManager.getInstance().showError(e);
            }
        }

        @Override
        public void onUnlocked(int indexOfUnlockedNote) {
            try {
                refresh();
            } catch (InstantiationException e) {
                SingletonViewManager.getInstance().showError(e);
            }
        }
    }
}
