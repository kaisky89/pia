/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

import org.jivesoftware.smack.XMPPException;
import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author kaisky89
 */
public class TestSmackNoteListener {

    static NotesCommunicator communicator;
    
    static Integer sessionInteger1;
    static Integer sessionInteger2;
    private NoteInformation noteInformation1 = 
            new TextNoteInformation(new Long(123), "This is a test.");
    private NoteInformation noteInformation2 = 
            new TextNoteInformation(new Long(48764), "This is also a test.");
    
    private static NoteInformation lastPublishedItem;
    private static Integer lastDeletedItemId;
    private static boolean purge = false;
    private static int publishedItems;
    private NotesCommunicatorListenerImpl itemListener = new NotesCommunicatorListenerImpl();

    public TestSmackNoteListener() {
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
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    
    @Test
    public void setAndUnsetNotesListener() throws NotesCommunicatorException{
        communicator.setUsingSession(sessionInteger1);
        communicator.setNotesListener(itemListener);
        communicator.unsetNotesListener();
    }
    
    @Test
    public void listenToAddOneItem() throws NotesCommunicatorException, InterruptedException{
        communicator.setUsingSession(sessionInteger1);
        communicator.setNotesListener(itemListener);
        communicator.addNote(noteInformation1);
        Thread.sleep(100);
        communicator.unsetNotesListener();
        assertEquals(lastPublishedItem, noteInformation1);
    }
    
    @Test
    public void expectUnsetItemListenerToReallyUnset() 
            throws NotesCommunicatorException, InterruptedException{
        communicator.setUsingSession(sessionInteger1);
        communicator.setNotesListener(itemListener);
        communicator.addNote(noteInformation1);
        Thread.sleep(100);
        communicator.unsetNotesListener();
        communicator.addNote(noteInformation2);
        Thread.sleep(100);
        assertEquals(noteInformation1, lastPublishedItem);
    }
    
    @Test
    public void sameNumberOfPublishedItemsAsDone() throws NotesCommunicatorException, InterruptedException {
        int oldNumber = publishedItems;
        
        communicator.setNotesListener(itemListener);
        
        communicator.addNote(noteInformation1);
        communicator.addNote(noteInformation2);
        communicator.addNote(noteInformation1);
        communicator.addNote(noteInformation1);
        communicator.addNote(noteInformation2);
        communicator.addNote(noteInformation2);
        
        communicator.unsetNotesListener();

        Thread.sleep(200);
        assertTrue(publishedItems == oldNumber + 6);
    }
    
    @Test
    public void asd() throws NotesCommunicatorException{
        
    }

    private static class NotesCommunicatorListenerImpl implements NotesCommunicatorListener<NoteInformation> {

        public NotesCommunicatorListenerImpl() {
        }

        @Override
        public void onPublish(NoteInformation publishedItem) {
            lastPublishedItem = publishedItem;
            publishedItems++;
        }

        @Override
        public void onDelete(Integer deletedItemId) {
            lastDeletedItemId = deletedItemId;
        }

        @Override
        public void onPurge() {
            purge = true;
        }
    }
}