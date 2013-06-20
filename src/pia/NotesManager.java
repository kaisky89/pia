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
        // set the listener, do all the listening stuff
        setListener(notesManagerListener);

        // do some initializing stuff
        init();
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
     * Refreshes the Note with the given id to a new Note.<br /><br />Important:
     * You must lock the note using <code>lockNote()</code>before you can edit anything.
     * <br /><br />Please Note: You must use the <code>NoteInformation</code> Object which
     * you get from <code>getAllNotes()</code>, edit it and use it here. If a new generated
     * <code>NoteInformation</code> Object is used here, things may won't work
     * out that well.
     * @param index index of the Note, which is needed to be updated.
     * @param note The detailed Information of the new state of the Note.
     */
    public void refreshNote(int index, NoteInformation note){
        // check, if the note is locked by someone else
        if (isLockedByAnother(index))
            throw new IllegalStateException("Cannot refresh note with index "
                    + index + ". The note is locked by someone else.");

        // get the requested Note from the List
        NoteInformation noteFromList = notes.get(index);

        // check, if id of NoteInformation is the same as provided in the list
        if (!noteFromList.getId().equals(note.getId()))
            throw new IllegalArgumentException("The Note seems to be generated as a new one. " +
                    "Please use the Notes of getAllNotes() to edit notes.");

        // check, if locked status of NoteInformation is the same as provided in the list
        if (!noteFromList.getLockedBy().equals(note.getLockedBy()))
            throw new IllegalArgumentException("The Note has changed locking information. " +
                    "Please use lockNote() and unlockNote() to change locking information.");

        // check, if the note is locked by the user (this should be necessary!)
        if (!noteFromList.getLockedBy().equals(SingletonDataStore.getInstance().getJID()))
            throw new IllegalStateException("The note with index " + index + " needs to be " +
                    "locked via lockNote() before it can be edited.");

        // refresh the Note on List, keep the old one in case communicating won't work
        NoteInformation oldNote = notes.set(index, note);

        // refresh the Note on NotesCommunicator
        try {
            communicator.setNote(note.getId(), note);
        } catch (NotesCommunicatorException e) {
            // TODO: need error handling here.
            e.printStackTrace();
            notes.set(index, oldNote);
            return;
        }
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

    /**
     * Locks the Note. This prevents others editing this note. Locking the
     * note is necessary to edit a note via <code>refreshNote()</code>.
     * @param index The index of the note, which shall be locked.
     */
    public void lockNote(int index) {
        // check, if the note is locked by someone else
        if (isLockedByAnother(index))
            throw new IllegalStateException("Cannot lock note with index "
                    + index + ". The note is locked by someone else.");

        // get the note
        NoteInformation note = notes.get(index);

        // try to lock it via NotesCommunicator
        try {
            communicator.lockNote(note.getId());
        } catch (NotesCommunicatorException e) {
            // TODO: need error handling here.
            e.printStackTrace();
            return;
        }

        // lock the note in the list
        // TODO: not sure if that works well. Needs some tests.
        note.setLocked(SingletonDataStore.getInstance().getJID());
        notes.set(index, note);
    }

    /**
     * Unlocks the note, so others users are able to edit it. Unlocking is only possible,
     * if the user has locked the note before.
     * @param index The index of the note, which shall be unlocked.
     */
    public void unlockNote(int index) {
        // check, if the note is locked by someone else
        if (isLockedByAnother(index))
            throw new IllegalStateException("Cannot unlock note with index "
                    + index + ". The note is locked by someone else.");

        // get the note
        NoteInformation note = notes.get(index);

        // try to unlock it via NotesCommunicator
        try {
            communicator.unlockNote(note.getId());
        } catch (NotesCommunicatorException e) {
            // TODO: need error handling here.
            e.printStackTrace();
            return;
        }

        // unlock the note in the list
        // TODO: not sure if that works well. Needs some tests.
        note.unlock();
        notes.set(index, note);
    }

    /**
     * Returns a List of all Notes. This is just for reading, editing the list won't
     * affect anything in the structure, but may loose consistence, so you don't do it.
     * @return List of all Items.
     */
    public List<NoteInformation> getAllNotes(){
        List<NoteInformation> returnList = new ArrayList<>();
        returnList.addAll(notes);
        return returnList;
    }

    private void setListener(NotesManagerListener listener) {
        this.listener = listener;
        try {
            communicator.setNotesListener(new MyNotesCommunicatorListener());
        } catch (NotesCommunicatorException e) {
            // TODO: need error handling here.
            e.printStackTrace();
            return;
        }
    }

    private void init(){
        // initializes the communicator. Not sure, if this happens right here...
        //communicator.init();
    }

    private class MyNotesCommunicatorListener implements NotesCommunicatorListener<NoteInformation> {
        @Override
        public void onPublish(NoteInformation publishedItem) {

          //// onAdd(): is the item new? //////

            // get the id of the publishedItem
            Integer newId = publishedItem.getId();

            // find a note in the list, where the id is the same
            NoteInformation noteFromList = null;
            for (NoteInformation note : notes) {
                if (note.getId().equals(newId)) {
                    noteFromList = note;
                    break;
                }
            }

            // if no item was found, the publishedItem is new
            if (noteFromList == null) {
                // add it to list
                notes.add(publishedItem);

                // notify the gui
                listener.onAdd(notes.size()-1);

                // and finish method
                return;
            }

          //// onLocked(): is the item got locked? //////

            // get the index of the noteFromList
            int index = notes.lastIndexOf(noteFromList);

            // find out, if the noteFromList isn't locked, but the publishedItem is
            if ((!noteFromList.isLocked()) && (publishedItem.isLocked())) {
                // if so, edit it in list
                notes.set(index, publishedItem);

                // notify the gui
                listener.onLocked(index);

                // and finish method
                return;
            }

          //// onUnlocked(): is the item got unlocked? //////

            // find out, if the noteFromList is locked, but the publishedItem isn't
            if (noteFromList.isLocked() && !publishedItem.isLocked()) {
                // if so, edit it in list
                notes.set(index, publishedItem);

                // notify the gui
                listener.onUnlocked(index);

                // and finish method
                return;
            }

          //// onChange(): is the item changed? //////

            // in every other case, there should be simple Changes in the Note
            if (!publishedItem.equals(noteFromList)){
                // if so, edit it in list
                notes.set(index, publishedItem);

                // notify the gui
                listener.onChange(index);
            }
        }

        @Override
        public void onDelete(Integer deletedItemId) {

            // find note which needed to be deleted
            NoteInformation noteToDelete = null;
            for (NoteInformation note : notes) {
                if (note.getId().equals(deletedItemId)) {
                    noteToDelete = note;
                }
            }

            // get the index of the note
            int index = notes.lastIndexOf(noteToDelete);

            // delete the note from list
            notes.remove(index);

            // notify the gui
            listener.onDelete(index);
        }

        @Override
        public void onPurge() {
            // get all ids, which are still in the list
            List<Integer> ids = new LinkedList<>();
            for (NoteInformation note : notes) {
                ids.add(note.getId());
            }

            // use onDelete to remove all of them and notify the gui
            for (Integer integer : ids) {
                onDelete(integer);
            }
        }
    }
}
