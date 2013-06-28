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

    private NotesPersistenceManager notesPersistenceManager;

    private static int onAddIndex = -1;
    private static int onChangeIndex = -1;
    private static int onDeleteIndex = -1;
    private static int onLockedIndex = -1;
    private static int onUnlockedIndex = -1;


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
        //((SingletonSmack) communicator).resetAll();
        communicator.close();
    }

    @Before
    public void setUp() throws NotesCommunicatorException {
        communicator.setUsingSession(sessionInteger1);
        notesPersistenceManager = new NotesPersistenceManager(new MyNotesPersistenceManagerListener());
        onAddIndex = -1;
        onChangeIndex = -1;
        onDeleteIndex = -1;
        onLockedIndex = -1;
        onUnlockedIndex = -1;
    }

    @After
    public void tearDown() {
    }
    
    @Test
    public void testAddNote(){
        notesPersistenceManager.addNote(NoteType.TEXT);
    }

    @Test
    public void testAddNoteRaisesInt() {
        // add some notes, save the indexes in a list
        List<Integer> integers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            integers.add(notesPersistenceManager.addNote(NoteType.TEXT));
        }

        // check, if the indexes increments by one
        int difference = integers.get(0);

        for (int i = 0; i < integers.size(); i++) {
            assertTrue("i:" + i + " + difference:" + difference +
                    " == integers.get(i):" + integers.get(i), i
                    + difference == integers.get(i));
        }
    }

    @Test
    public void addNoteAndCheckIfIdExists() throws InstantiationException {
        int i = notesPersistenceManager.addNote(NoteType.TEXT);
        NoteInformation noteInformation = notesPersistenceManager.getAllNotes().get(i);

        assertTrue(noteInformation.getId() != null);
    }

    @Test
    public void addAndRefreshNote() throws InstantiationException {
        // add note
        int i = notesPersistenceManager.addNote(NoteType.TEXT);

        // lock the note
        notesPersistenceManager.lockNote(i);

        // get the note
        List<NoteInformation> allNotes = notesPersistenceManager.getAllNotes();
        NoteInformation noteInformation = allNotes.get(i);

        // edit the note
        String string = "Dies ist ein neuer Text";
        ((TextNoteInformation) noteInformation).setText(string);

        // save it to notesPersistenceManager
        notesPersistenceManager.refreshNote(i, noteInformation);

        // unlock the note
        notesPersistenceManager.unlockNote(i);

        // test, if the note is changed
        String text = ((TextNoteInformation) notesPersistenceManager.getAllNotes().get(i)).getText();
        assertEquals(string, text);
    }

    @Test
    public void testListenOnAdd() throws NotesCommunicatorException, InterruptedException, InstantiationException {
        // create a textNote
        TextNoteInformation textNote = new TextNoteInformation((long) 1873, "FooBar");

        // onAdd shouldn't be called when adding through NotesPersistenceManager
        int index = notesPersistenceManager.addNote(NoteType.TEXT);
        Thread.sleep(200);
        assertTrue(onAddIndex == -1);

        // onAdd shouldn't be called when changing a Note through NotesPersistenceManager
        notesPersistenceManager.lockNote(index);
        TextNoteInformation noteAdded = (TextNoteInformation) notesPersistenceManager.getAllNotes().get(index);
        noteAdded.setText("BarFoo");
        notesPersistenceManager.refreshNote(index, noteAdded);
        notesPersistenceManager.unlockNote(index);
        Thread.sleep(200);
        assertTrue(onAddIndex == -1);

        // onAdd shouldn't be called when changing through NotesCommunicator
        communicator.setNote(noteAdded.getId(), textNote);
        Thread.sleep(200);
        assertTrue(onAddIndex == -1);

        // onAdd should be called when adding through NotesCommunicator
        Integer id = communicator.addNote(textNote);
        Thread.sleep(200);
        assertTrue(onAddIndex != -1);

        // expect the same Note Values as it was published
        TextNoteInformation newTextNote = (TextNoteInformation) notesPersistenceManager.getAllNotes().get(onAddIndex);
        assertTrue(newTextNote.equalsIgnoreId(textNote));
    }

    @Test
    public void testOnChange() throws Exception {
        // create a textNote
        TextNoteInformation textNote = new TextNoteInformation((long) 1873, "FooBar");

        // onChange shouldn't be called when adding through NotesPersistenceManager
        int index = notesPersistenceManager.addNote(NoteType.TEXT);
        Thread.sleep(200);
        assertTrue(onChangeIndex == -1);

        // onChange shouldn't be called when changing a Note through NotesPersistenceManager
        notesPersistenceManager.lockNote(index);
        TextNoteInformation noteAdded = (TextNoteInformation) notesPersistenceManager.getAllNotes().get(index);
        noteAdded.setText("BarFoo");
        notesPersistenceManager.refreshNote(index, noteAdded);
        notesPersistenceManager.unlockNote(index);
        Thread.sleep(200);
        assertTrue(onChangeIndex == -1);

        // onChange shouldn't be called when adding through NotesCommunicator
        Integer id = communicator.addNote(textNote);
        Thread.sleep(200);
        assertTrue(onChangeIndex == -1);

        // onChange should be called when changing through NotesCommunicator
        communicator.setNote(noteAdded.getId(), textNote);
        Thread.sleep(200);
        assertTrue(onAddIndex != -1);

        // expect the same Note Values as it was published
        TextNoteInformation newTextNote = (TextNoteInformation) notesPersistenceManager.getAllNotes().get(onAddIndex);
        assertTrue(newTextNote.equalsIgnoreId(textNote));
    }

    @Test
    public void testDifferentUsers() throws Exception {
        // add a new item, leave it locked
        int index = notesPersistenceManager.addNote(NoteType.TEXT);
        notesPersistenceManager.lockNote(index);
        TextNoteInformation textNote = (TextNoteInformation) notesPersistenceManager.getAllNotes().get(index);
        textNote.setText("This note is going to be locked.");
        notesPersistenceManager.refreshNote(index, textNote);

        // remember the id of the note, remember the sessionId
        Integer noteId = notesPersistenceManager.getAllNotes().get(index).getId();
        Integer sessionId = ((SingletonSmack)communicator).getUsingSession();

        // remember the size of allNotes
        int sizeOfAllNotes = notesPersistenceManager.getAllNotes().size();

        // logout
        notesPersistenceManager.close();
        communicator.close();
        communicator = SingletonSmack.getInstance();

        // set another user
        SingletonDataStore.getInstance().setUser(new UserData("user2", "123"));

        // reset fields for listeners
        onAddIndex = -1;
        onChangeIndex = -1;
        onDeleteIndex = -1;
        onLockedIndex = -1;
        onUnlockedIndex = -1;

        // login, prepare NotesPersistenceManager
        communicator.init();
        communicator.setUsingSession(sessionId);
        notesPersistenceManager = new NotesPersistenceManager(new MyNotesPersistenceManagerListener());

        // check, if the size is the same
        assertEquals(sizeOfAllNotes, notesPersistenceManager.getAllNotes().size());

        // get the index of the locked note
        int newIndex = -1;
        for (int i = 0; i < notesPersistenceManager.getAllNotes().size(); i++) {
            if (notesPersistenceManager.getAllNotes().get(i).getId().equals(noteId)){
                newIndex = i;
                break;
            }
        }

        if (newIndex == -1) {
            throw new IndexOutOfBoundsException("Cannot find item with id " + noteId + ".");
        }

        // check, if the locking is ok.
        Assert.assertTrue(notesPersistenceManager.isLockedByAnother(newIndex));

        boolean exceptionIsThrown = false;
        try {
            notesPersistenceManager.lockNote(newIndex);
        } catch (Exception e) {
            exceptionIsThrown = true;
        }
        assertTrue(exceptionIsThrown);

        // add a note as new user
        int index2 = notesPersistenceManager.addNote(NoteType.TEXT);
        notesPersistenceManager.lockNote(index2);
        TextNoteInformation note2 = (TextNoteInformation) notesPersistenceManager.getAllNotes().get(index2);
        note2.setText("This is a note from user2. It will be unlocked.");
        notesPersistenceManager.refreshNote(index2, note2);
        notesPersistenceManager.unlockNote(index2);
        note2 = (TextNoteInformation) notesPersistenceManager.getAllNotes().get(index2);
        NoteInformation savedNote2 = NoteInformation.produceNoteInformation(note2.toXml());

        // there should be no event handler called
        assertEquals(-1, onAddIndex);
        assertEquals(-1, onChangeIndex);
        assertEquals(-1, onDeleteIndex);
        assertEquals(-1, onLockedIndex);
        assertEquals(-1, onUnlockedIndex);

        // remember the new size
        sizeOfAllNotes = notesPersistenceManager.getAllNotes().size();

        // logout
        notesPersistenceManager.close();
        communicator.close();
        communicator = SingletonSmack.getInstance();

        // set another user
        SingletonDataStore.getInstance().setUser(new UserData("user1", "123"));

        // reset fields for listeners
        onAddIndex = -1;
        onChangeIndex = -1;
        onDeleteIndex = -1;
        onLockedIndex = -1;
        onUnlockedIndex = -1;

        // login, prepare NotesPersistenceManager
        communicator.init();
        communicator.setUsingSession(sessionId);
        notesPersistenceManager = new NotesPersistenceManager(new MyNotesPersistenceManagerListener());

        // check, if size is still the same
        assertEquals(sizeOfAllNotes, notesPersistenceManager.getAllNotes().size());

        // get the note from user2 and check it.
        for (NoteInformation note : notesPersistenceManager.getAllNotes()) {
            TextNoteInformation textNoteInformation = (TextNoteInformation) note;
            if (textNoteInformation.getId().equals(savedNote2.getId())) {
                assertTrue(textNoteInformation.equalsIgnoreId(savedNote2));
            }
        }
    }

    @Test
    public void testOnDelete() throws Exception {
        // Add and delete a note
        notesPersistenceManager.deleteNote(notesPersistenceManager.addNote(NoteType.TEXT));

        // assert that the onDeleteIndex didn't change
        Thread.sleep(200);
        assertEquals(-1, onDeleteIndex);

        // Add a note. Get the id
        final int i = notesPersistenceManager.addNote(NoteType.TEXT);
        final Integer id = notesPersistenceManager.getAllNotes().get(i).getId();

        // use the communicator to delete it
        communicator.deleteNote(id);

        // now, the onDeleteIndex should have been changed
        Thread.sleep(200);
        assertTrue(-1 != onDeleteIndex);
    }

    @Test
    public void testOnLock() throws Exception {

        // Add a note. Get the id
        final int index = notesPersistenceManager.addNote(NoteType.TEXT);
        final Integer id = notesPersistenceManager.getAllNotes().get(index).getId();

        // lock it, unlock it through NotesPersistenceManager
        notesPersistenceManager.lockNote(index);
        notesPersistenceManager.unlockNote(index);

        // assert that onLock isn't been called
        Thread.sleep(200);
        assertEquals(-1, onLockedIndex);

        // use the communicator to lock
        communicator.lockNote(id);

        // now, the onLock should have been changed
        Thread.sleep(200);
        assertTrue(-1 != onLockedIndex);

        // unlock the note
        notesPersistenceManager.unlockNote(index);
    }

    @Test
    public void testOnUnlock() throws Exception {
        // Add a note. Get the id
        final int index = notesPersistenceManager.addNote(NoteType.TEXT);
        final Integer id = notesPersistenceManager.getAllNotes().get(index).getId();

        // lock it, unlock it through NotesPersistenceManager
        notesPersistenceManager.lockNote(index);
        notesPersistenceManager.unlockNote(index);

        // assert that onUnLock isn't been called
        Thread.sleep(200);
        assertEquals(-1, onUnlockedIndex);

        // use the communicator to lock and to unlock
        communicator.lockNote(id);
        communicator.unlockNote(id);

        // now, the onUnLock should have been changed
        Thread.sleep(200);
        assertTrue(-1 != onUnlockedIndex);
    }

    private static class MyNotesPersistenceManagerListener implements NotesPersistenceManagerListener {
        @Override
        public void onAdd(int indexOfAddedNote) {
            onAddIndex = indexOfAddedNote;
        }

        @Override
        public void onChange(int indexOfChangedNote) {
            onChangeIndex = indexOfChangedNote;
        }

        @Override
        public void onDelete(int indexOfDeletedNote) {
            onDeleteIndex = indexOfDeletedNote;
        }

        @Override
        public void onLocked(int indexOfLockedNote) {
            onLockedIndex = indexOfLockedNote;
        }

        @Override
        public void onUnlocked(int indexOfUnlockedNote) {
            onUnlockedIndex = indexOfUnlockedNote;
        }
    }
}