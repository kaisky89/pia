package pia;

/**
 * Created with IntelliJ IDEA.
 * User: kaisky89
 * Date: 19.06.13
 * Time: 17:09
 * To change this template use File | Settings | File Templates.
 */
public interface NotesManagerListener {
    public void onAdd(int indexOfAddedNote);
    public void onChange(int indexOfChangedNote);
    public void onDelete(int indexOfDeletedNote);
    public void onLocked(int indexOfLockedNote);
    public void onUnlocked(int indexOfUnlockedNote);
}
