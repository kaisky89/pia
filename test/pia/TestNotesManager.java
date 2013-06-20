/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

import org.jivesoftware.smack.XMPPException;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author kaisky89
 */
public class TestNotesManager {

    static NotesCommunicator communicator;

    static Integer sessionInteger1;
    static Integer sessionInteger2;

    private NotesManager notesManager;

    public TestNotesManager() {
    }

    @BeforeClass
    public static void setUpClass() throws NotesCommunicatorException, XMPPException {
        SingletonDataStore.getInstance().setServerAdress("localhost");
        SingletonDataStore.getInstance().setUser(new UserData("user1", "123"));
        communicator = SingletonSmack.getInstance();
        communicator.init();
        ((SingletonSmack) communicator).resetAll();
        sessionInteger1 = communicator.addSession(new SessionInformation("TestSession1", "url", "TS"));
        sessionInteger2 = communicator.addSession(new SessionInformation("TestSession2", "url", "TS"));
    }

    @AfterClass
    public static void tearDownClass() throws XMPPException {
        ((SingletonSmack) communicator).resetAll();
        communicator.close();
    }

    @Before
    public void setUp() throws NotesCommunicatorException {
        notesManager = new NotesManager(new MyNotesManagerListener());
        communicator.setUsingSession(sessionInteger1);
    }

    @After
    public void tearDown() {
    }
    
    @Test
    public void testAddNote(){
        notesManager.addNote(NoteType.TEXT);
    }

    @Test
    public void testAddNoteRaisesInt() {
        // add some notes, save the indexes in a list
        List<Integer> integers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            integers.add(notesManager.addNote(NoteType.TEXT));
        }

        // check, if the indexes increments by one
        int difference = integers.get(0);
        for (int i = 0; i < integers.size(); i++) {
            assertTrue(i + difference == integers.get(i));
        }
    }

    @Test
    public void addNoteAndCheckIfIdExists() {
        int i = notesManager.addNote(NoteType.TEXT);
        NoteInformation noteInformation = notesManager.getAllNotes().get(i);

        // TODO: do not print it, test it!
        assertTrue(noteInformation.getId() != null);
    }

    @Test
    public void addAndRefreshNote() {
        // add note
        int i = notesManager.addNote(NoteType.TEXT);

        // lock the note
        notesManager.lockNote(i);

        // get the note
        List<NoteInformation> allNotes = notesManager.getAllNotes();
        NoteInformation noteInformation = allNotes.get(i);

        // edit the note
        String string = "Dies ist ein neuer Text";
        ((TextNoteInformation) noteInformation).setText(string);

        // save it to notesManager
        notesManager.refreshNote(i, noteInformation);

        // test, if the note is changed
        String text = ((TextNoteInformation) notesManager.getAllNotes().get(i)).getText();
        assertEquals(string, text);
    }

    @Test
    public void asd(){

    }

    private static class MyNotesManagerListener implements NotesManagerListener {
        @Override
        public void onAdd(int indexOfAddedNote) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onChange(int indexOfChangedNote) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onDelete(int indexOfDeletedNote) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onLocked(int indexOfLockedNote) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onUnlocked(int indexOfUnlockedNote) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}