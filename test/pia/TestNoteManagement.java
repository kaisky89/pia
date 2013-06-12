/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

import java.util.logging.Level;
import java.util.logging.Logger;
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
public class TestNoteManagement {

    static NotesCommunicator communicator;
    
    static Integer sessionInteger1;
    static Integer sessionInteger2;

    public TestNoteManagement() {
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
    public void expectSetUsingSession() {
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
        communicator.addNote(new TextNoteInformation(new Long(123), "This is a test."));
    }
    
    @Test
    public void asd(){
        
    }
}