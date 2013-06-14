/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

/**
 *
 * @author kaisky89
 */
public interface NotesCommunicatorListener<T> {
    public void onPublish(T publishedItem);
    public void onDelete(Integer deletedItemId);
    public void onPurge();
}
