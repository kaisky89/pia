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
    public void onAdd(T addedItem);
    public void onChange(T ChangedItem);
    public void onDelete(T deletedItem);
    public void onPurge();
}
