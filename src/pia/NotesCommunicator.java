package pia;

import java.util.List;

/**Interface to describe the possible actions, which are possible and
 * necessary for a Communcation Tool (i.e. XMPP) to work with ShowNotes. This Interface needs
 * to abstract from the using technology to only the Notes themselves, including
 * session management and EventListeners.

 */
public interface NotesCommunicator {
    
    /**
     * Initializes the Management of this Module. This should be done before 
     * using any other method.
     */
    public void init() throws NotesCommunicatorException;
    
    public void close();
    
    
    // Session Management ////////////////////////////////////////////////
    
    /**
     * Adds a new Session to the System.
     * @param session The session to be added.
     * @return The Id, which has been generated for the new session.
     */
    public Integer addSession(SessionInformation session) throws NotesCommunicatorException;
    
    /**
     * Returns the Ids of all Sessions, which are available.
     * @return List of Integers including all available sessions.
     */
    public List<Integer> getSessionIds() throws NotesCommunicatorException;
    
    /**
     * Gets all Information of the session specified by the id.
     * @param id Id of the requested session
     * @return A <code>SessionInformation</code> Object, which contains further
     * Information.
     */
    public SessionInformation getSessionInformation(Integer id) throws NotesCommunicatorException;
    
    /**
     * Sets the specified Session to be used for Note Management. It is 
     * necessary to specify a Session, before You use Note Managing Methods. 
     * After setting the <code>UsingSession</code>, all Note Management Methods
     * will refer to that specified Session. You SHOULD
     * <code>unsetAvailableNoteListener()</code> before you set another 
     * <code>UsingSession</code>, though the Implementation MAY handle unsetting
     * the EventListener on its own, if you change Session without it.
     * @param id The Id of the Session to be set as currently Using.
     */
    public void setUsingSession(Integer id) throws NotesCommunicatorException;
    
    /**
     * Deletes the Session and all its content (including all Notes).
     * @param id Id of the Session to be deleted.
     */
    public void deleteSession(Integer id) throws NotesCommunicatorException;
    
    
    
    // General Note Management ///////////////////////////////////////////
    
    /**
     * Add a note to the current Session. The note must be specified as an Object
     * which implements the <code>NoteInformation</code> Interface. For this
     * method, you first need to <code>setUsingSession</code>, elsewise there will
     * be an error.
     * @param note The new note to be added to the current Session.
     * @return The id of the new created item.
     */
    public Integer addNote(NoteInformation note) throws NotesCommunicatorException;
    
    /**
     * Get a list of the ids of all available Notes in the current session. The
     * current Session must be specified before using <code>setUsingSession</code>.
     * @return A List of all ids.
     */
    public List<Integer> getNoteIds() throws NotesCommunicatorException;
    
    /**
     * Get all detailed Information about a specified note. 
     * @param id The id of the note, which contents will be loaded.
     * @return A <code>NoteInformation</code> Object which contains all 
     * Information about the note.
     */
    public NoteInformation getNoteInformation(Integer id) throws NotesCommunicatorException;
    
    /**
     * 
     * @param id
     * @param note 
     */
    public void setNote(Integer id, NoteInformation note) throws NotesCommunicatorException;
    public void deleteNote(Integer id) throws NotesCommunicatorException;
    
    public void lockNote(Integer id) throws NotesCommunicatorException;
    public void unlockNote(Integer id) throws NotesCommunicatorException;
    
    
    
    // EventListening Methods ////////////////////////////////////////////
    public void setAvailableSessionListener(NotesCommunicatorListener<SessionInformation> sessionListener);
    public void unsetAvailableSessionListener();
    
    public void setNotesListener(NotesCommunicatorListener<NoteInformation> notesListener) throws NotesCommunicatorException;
    public void unsetNotesListener() throws NotesCommunicatorException;
}
