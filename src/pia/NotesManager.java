package pia;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This Class acts as a persistent module to handle requests and events between
 * the View and the NotesCommunicator.
 * User: kaisky89
 * Date: 19.06.13
 * Time: 15:37
 */
public class NotesManager {

    private NotesCommunicator communicator = SingletonSmack.getInstance();
    private List<NoteInformation> notes = new ArrayList<>();
    private NotesManagerListener listener;

    /**
     * Creates a new instance of NotesManager. Expects a <code>NotesManagerListener</code> as Parameter.
     * @param notesManagerListener A <code>NotesManagerListener</code> which handles all events called
     *                             by this Manager.
     */
    public NotesManager(NotesManagerListener notesManagerListener){
        setListener(notesManagerListener);

        init();
    }

    /**
     * Initializes the Manager. Needs to be executed before using all other methods of this class.
     */
    private void init(){
        // initializes the communicator. Not sure, if this happens right here...
        //communicator.init();

    }

    public NoteInformation addNote(NoteType noteType){

    }

    public void refreshNote(int index, NoteInformation note){

    }

    public void deleteNote(int index){

    }

    private void setListener(NotesManagerListener listener) {
        this.listener = listener;
    }

}
