/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

import java.util.LinkedList;
import java.util.List;
import org.jivesoftware.smack.XMPPException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kaisky89
 */
public class TestNoteListener {

    static NotesCommunicator communicator;
    
    static Integer sessionInteger1;
    static Integer sessionInteger2;
    private NoteInformation noteInformation1 = 
            new TextNoteInformation(new Long(123), "This is a test.");
    private NoteInformation noteInformation2 = 
            new TextNoteInformation(new Long(48764), "This is also a test.");

    public TestNoteListener() {
    }

    @BeforeClass
    public static void setUpClass() throws NotesCommunicatorException, XMPPException {
        SingletonDataStore.getInstance().setServerAdress("127.0.0.1");
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
    public void expectExceptionSetUsingSession() {
        boolean exceptionIsThrown = false;
        try {
            communicator.getNoteIds();
        } catch (NotesCommunicatorException ex) {
            exceptionIsThrown = true;
        }
        assertTrue(exceptionIsThrown);
    }
    
    @Test
    public void setUsingSession() throws NotesCommunicatorException{
        communicator.setUsingSession(sessionInteger1);
    }
    
    @Test
    public void multipleSetUsingSession() throws NotesCommunicatorException{
        communicator.setUsingSession(sessionInteger1);
        communicator.setUsingSession(sessionInteger2);
        communicator.setUsingSession(sessionInteger1);
        communicator.setUsingSession(sessionInteger1);
    }
    
    @Test
    public void wrongSetUsingSession() throws NotesCommunicatorException{
        boolean exceptionIsThrown = false;
        try {
            communicator.setUsingSession(1543453643);
        } catch (NotesCommunicatorException ex) {
            exceptionIsThrown = true;
        }
        assertTrue(exceptionIsThrown);
    }
    
    @Test
    public void addNote() throws NotesCommunicatorException{
        communicator.setUsingSession(sessionInteger1);
        communicator.addNote(noteInformation1);
    }
    
    @Test
    public void addMultipleNotes() throws NotesCommunicatorException{
        communicator.setUsingSession(sessionInteger1);
        communicator.addNote(new TextNoteInformation(new Long(123), "This is a test1."));
        communicator.addNote(new TextNoteInformation(new Long(124), "This is a test2."));
        communicator.addNote(new TextNoteInformation(new Long(125), "This is a test3."));
    }
    
    
    
    @Test
    public void getEmptyNoteIds() throws NotesCommunicatorException{
        communicator.setUsingSession(sessionInteger2);
        List<Integer> noteIds = communicator.getNoteIds();
        assertTrue(noteIds.isEmpty());
        
    }
    
    @Test
    public void addNoteRaisesNoteIdsSize() throws NotesCommunicatorException{
        communicator.setUsingSession(sessionInteger2);
        List<Integer> noteIds = communicator.getNoteIds();
        int oldSize = noteIds.size();
        
        communicator.addNote(noteInformation1);
        
        noteIds = communicator.getNoteIds();
        int newSize = noteIds.size();
        
        assertTrue(newSize == (oldSize + 1));
    }
    
    @Test
    public void addMultipleNoteRaisesNoteIdsSize() throws NotesCommunicatorException{
        communicator.setUsingSession(sessionInteger2);
        List<Integer> noteIds = communicator.getNoteIds();
        
        int oldSize = noteIds.size();
        
        communicator.addNote(noteInformation1);
        communicator.addNote(noteInformation1);
        communicator.addNote(noteInformation1);
        communicator.addNote(noteInformation1);
        communicator.addNote(noteInformation1);
        
        noteIds = communicator.getNoteIds();
        int newSize = noteIds.size();
        
        assertTrue(newSize == (oldSize + 5));
    }
    
    @Test
    public void addMultipleNoteCheckIfAllNotesAreAvailableAndEqual() throws NotesCommunicatorException{
        communicator.setUsingSession(sessionInteger2);
        
        List<Integer> tempList = new LinkedList<>();
        
        tempList.add(communicator.addNote(noteInformation1));
        tempList.add(communicator.addNote(noteInformation1));
        tempList.add(communicator.addNote(noteInformation1));
        tempList.add(communicator.addNote(noteInformation1));
        tempList.add(communicator.addNote(noteInformation1));
        tempList.add(communicator.addNote(noteInformation1));
        tempList.add(communicator.addNote(noteInformation1));
        tempList.add(communicator.addNote(noteInformation1));
        
        List<Integer> noteIds = communicator.getNoteIds();
        
        for (Integer integer : noteIds) {
            communicator.getNoteInformation(integer);
        }
        
        for (Integer integer : tempList) {
            assertEquals(communicator.getNoteInformation(integer), noteInformation1);
        }
    }
    
    @Test
    public void addToDifferentSessionsCheckIdsSize() throws NotesCommunicatorException{
        communicator.setUsingSession(sessionInteger1);
        List<Integer> noteIds = communicator.getNoteIds();
        int oldSize1 = noteIds.size();
        
        communicator.setUsingSession(sessionInteger2);
        noteIds = communicator.getNoteIds();
        int oldSize2 = noteIds.size();
        
        communicator.addNote(noteInformation1);
        communicator.addNote(noteInformation1);
        communicator.addNote(noteInformation1);
        communicator.addNote(noteInformation1);
        communicator.addNote(noteInformation1);
        
        communicator.setUsingSession(sessionInteger1);
        noteIds = communicator.getNoteIds();
        int newSize1 = noteIds.size();
        
        assertTrue(newSize1 == oldSize1);
    }
    
    @Test
    public void NoteIdsContainsNewId() throws NotesCommunicatorException{
        communicator.setUsingSession(sessionInteger1);
        Integer addNote = communicator.addNote(noteInformation1);
        List<Integer> noteIds = communicator.getNoteIds();
        assertTrue(noteIds.contains(addNote));
    }
    
    @Test
    public void addAndGetTextNote() throws NotesCommunicatorException{
        communicator.setUsingSession(sessionInteger1);
        Integer newNote = communicator.addNote(noteInformation1);
        NoteInformation noteInformation = communicator.getNoteInformation(newNote);
        assertEquals(noteInformation1.getAttributes(), noteInformation.getAttributes());
        assertEquals(noteInformation1.getNoteType(), noteInformation.getNoteType());
        assertEquals(noteInformation1.getTimePosition(), noteInformation.getTimePosition());
        assertEquals(noteInformation1.isLocked(), noteInformation.isLocked());
    }
    
    @Test
    public void addAndSetNote() throws NotesCommunicatorException{
        communicator.setUsingSession(sessionInteger1);
        Integer addNote = communicator.addNote(noteInformation1);
        communicator.setNote(addNote, noteInformation2);
        NoteInformation noteInformation = communicator.getNoteInformation(addNote);
        assertEquals(noteInformation.getNoteType(), noteInformation2.getNoteType());
        assertEquals(noteInformation.getTimePosition(), noteInformation2.getTimePosition());
        assertEquals(noteInformation.isLocked(), noteInformation2.isLocked());
        assertEquals(noteInformation.getAttributes(), noteInformation2.getAttributes());
        assertEquals(noteInformation.getClass(), noteInformation2.getClass());
    }
    
    @Test
    public void expectExceptionOnSetUnavailableNote() throws NotesCommunicatorException{
        communicator.setUsingSession(sessionInteger1);
        Integer addNote = communicator.addNote(noteInformation1);
        communicator.deleteNote(addNote);
        boolean exceptionIsThrown = false;
        try {
            communicator.setNote(addNote, noteInformation2);
        } catch (NotesCommunicatorException nce) {
            exceptionIsThrown = true;
        }
        assertTrue(exceptionIsThrown);
    }
    
    @Test
    public void asd() throws NotesCommunicatorException{
        
    }
}