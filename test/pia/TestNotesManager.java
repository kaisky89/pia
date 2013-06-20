/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

import org.jivesoftware.smack.XMPPException;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

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
        notesManager = new NotesManager(null);
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
        List<Integer> integers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            integers.add(notesManager.addNote(NoteType.TEXT));
        }

        // TODO: check incremental integers

    }

    @Test
    public void addNoteAndCheckIfIdExists() {
        int i = notesManager.addNote(NoteType.TEXT);
        NoteInformation noteInformation = notesManager.getAllNotes().get(i);

        // TODO: do not print it, test it!
        System.out.println(noteInformation.getId());
    }

    @Test
    public void addAndRefreshNote() {
        int i = notesManager.addNote(NoteType.TEXT);
        List<NoteInformation> allNotes = notesManager.getAllNotes();
        NoteInformation noteInformation = allNotes.get(i);
        ((TextNoteInformation) noteInformation).setText("Dies ist ein neuer Text");
        notesManager.refreshNote(i, noteInformation);
    }

    @Test
    public void asd(){

    }
}