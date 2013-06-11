/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

import java.util.List;
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
public class TestSessionManagement {
    
    private static NotesCommunicator communicator;
    private Integer sessionId;
    
    private String sessionName;
    private String sessionUrl;
    private String sessionDescription;
    
    private static String name = "Test";
    private static String url = "http://example.org";
    private static String description = "Dies ist nur ein Test";
    
    
    public TestSessionManagement() {
    }
    
    @BeforeClass
    public static void setUpClass() throws NotesCommunicatorException {
        SingletonDataStore.getInstance().setServerAdress("localhost");
        SingletonDataStore.getInstance().setUser(new UserData("user1", "123"));
        communicator = SingletonSmack.getInstance();
        communicator.init();
    }
    
    @AfterClass
    public static void tearDownClass() throws NotesCommunicatorException {
        // create a sample session, which represents all session needed to be deleted
        SessionInformation sessionToDelete = new SessionInformation(name, url, description);
        
        // find and delete all sessions, which equal the representing session
        List<Integer> sessionIds = communicator.getSessionIds();
        for (Integer integer : sessionIds) {
            if(communicator.getSessionInformation(integer).equals(sessionToDelete))
                communicator.deleteSession(integer);
        }
        
        // close the connection
        communicator.close();
    }
    
    @Before
    public void setUp() throws NotesCommunicatorException {
        sessionName = name;
        sessionUrl = url;
        sessionDescription = description;
        
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void addSession() throws NotesCommunicatorException{
        SessionInformation session = new SessionInformation(sessionName, sessionUrl, sessionDescription);
        sessionId = communicator.addSession(session);
        
        assertEquals(session.getId(), sessionId);
    }
    
    @Test
    public void getSessionIds() throws NotesCommunicatorException{
        SessionInformation session = new SessionInformation(sessionName, sessionUrl, sessionDescription);
        sessionId = communicator.addSession(session);
        List<Integer> sessionIds = communicator.getSessionIds();
        
        assertTrue(sessionIds.contains(sessionId));
    }
    
    @Test
    public void getSession() throws NotesCommunicatorException{
        List<Integer> sessionIds = communicator.getSessionIds();
        sessionId = sessionIds.get(sessionIds.size()-1);
        SessionInformation session = communicator.getSessionInformation(sessionId);
        
        assertEquals(session.getId(), sessionId);
    }
    
    @Test
    public void deleteSession() throws NotesCommunicatorException{
        // add a Session
        SessionInformation session = new SessionInformation(sessionName, sessionUrl, sessionDescription);
        sessionId = communicator.addSession(session);
        
        // is the new Session there?
        List<Integer> sessionIds = communicator.getSessionIds();
        assertTrue(sessionIds.contains(sessionId));
        
        // delete the new Session
        communicator.deleteSession(sessionId);
        
        // is the Session removed?
        sessionIds = communicator.getSessionIds();
        assertFalse(sessionIds.contains(sessionId));
        
    }
}