package pia;

public interface NotesCommunicatorListener<T> {
    public void onPublish(T publishedItem);
    public void onDelete(Integer deletedItemId);
    public void onPurge();
}
