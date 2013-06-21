package pia;

public interface NotesManagerListener {
    public void onAdd(int indexOfAddedNote);
    public void onChange(int indexOfChangedNote);
    public void onDelete(int indexOfDeletedNote);
    public void onLocked(int indexOfLockedNote);
    public void onUnlocked(int indexOfUnlockedNote);
}
