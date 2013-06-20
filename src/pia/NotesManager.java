package pia;

import java.util.ArrayList;
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
        // set the listener, do all the listening stuff
        setListener(notesManagerListener);

        // do some initializing stuff
        init();
    }

    private void init(){
        // initializes the communicator. Not sure, if this happens right here...
        //communicator.init();

    }

    /**
     * Adds a new Note. NoteType needs to be specified by <code>noteType</code>. With the returned
     * id, you can access the new Note via <code>getAllNotes()</code>.
     * @param noteType NoteType of the new Note.
     * @return The id of the new Note.
     */
    public int addNote(NoteType noteType) {
        // generate the new noteInformation
        NoteInformation noteInformation = NoteInformation.produceConcreteNoteInformation(noteType);

        // add it to NotesCommunicator
        try {
            communicator.addNote(noteInformation);
        } catch (NotesCommunicatorException e) {
            // TODO: need error handling here.
            e.printStackTrace();
            return -1;
        }

        // add it to the List
        notes.add(noteInformation);

        // return the index of the new Note
        return notes.size()-1;
    }

    /**
     * Refreshes the Note with the given id to a new Note. Please Note: You must
     * use the <code>NoteInformation</code> Object which you get from
     * <code>getAllNotes()</code>, edit it and use it here. If a new generated
     * <code>NoteInformation</code> Object is used here, things may won't work
     * out that well.
     * @param index index of the Note, which is needed to be updated.
     * @param note The detailed Information of the new state of the Note.
     */
    public void refreshNote(int index, NoteInformation note){
        // get the requested Note from the List
        NoteInformation noteFromList = notes.get(index);

        // check, if id of NoteInformation is the same as provided in the list
        if (!noteFromList.getId().equals(note.getId()))
            throw new IllegalArgumentException("The Note seems to be generated as a new one. " +
                    "Please use the Notes of getAllNotes() to edit notes.");

        // check, if locked status of NoteInformation is the same as provided in the list
        if (!noteFromList.getLockedBy().equals(note.getLockedBy()))
            throw new IllegalArgumentException("The Note has changed locking information. " +
                    "Please use lockNote() and unLockNote() to change locking information.");

        // refresh the Note on NotesCommunicator
        try {
            communicator.setNote(note.getId(), note);
        } catch (NotesCommunicatorException e) {
            // TODO: need error handling here.
            e.printStackTrace();
            return;
        }

        // refresh the Note on List
        notes.set(index, note);
    }

    /**
     * Deletes the specified Note.
     * @param index Index of the Note, which needs to be deleted.
     */
    public void deleteNote(int index) {
        // check, if the note is locked by someone else
        if (isLockedByAnother(index))
            throw new IllegalStateException("Cannot delete note with index "
                + index + ". The note is locked by someone else.");

        // get the note
        NoteInformation note = notes.get(index);

        // delete Note on NotesCommunicator
        try {
            communicator.deleteNote(note.getId());
        } catch (NotesCommunicatorException e) {
            // TODO: need error handling here.
            e.printStackTrace();
            return;
        }

        // delete the Note on the List
        notes.remove(index);
    }

    /**
     * Check if the note with the specified index is locked by someone else than the
     * user who is logged in with this instance of this Program.
     * @param index The index of the note to be checked
     * @return <code>false</code> if nobody locked the note or the user himself has
     * locked it.<br /><code>true</code> if someone else than the user has locked
     * the note.
     */
    public boolean isLockedByAnother(int index) {
        // get the requested note
        NoteInformation note = notes.get(index);

        // find out, if the note is free
        if (note.getLockedBy().equals("free"))
            return false;

        // find out, if the note is locked by the user himself
        if (note.getLockedBy().equals(SingletonDataStore.getInstance().getJID()))
            return false;

        // in all other cases, the note should be locked by someone else, so return true
        return true;
    }

    public void lockNote(int index) {
        // check, if the note is locked by someone else
        if (isLockedByAnother(index))
            throw new IllegalStateException("Cannot lock note with index "
                    + index + ". The note is locked by someone else.");

        // get the note
        NoteInformation note = notes.get(index);

        // edit the note

    }

    /**
     * Returns a List of all Notes. This is just for reading, editing the list won't
     * affect anything in the structure.
     * @return List of all Items.
     */
    public List<NoteInformation> getAllNotes(){
        List<NoteInformation> returnList = new ArrayList<>();
        returnList.addAll(notes);
        return returnList;
    }

    private void setListener(NotesManagerListener listener) {
        this.listener = listener;
    }

}
