/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

/**
 *
 * @author kaisky89
 */
public class NotesCommunicatorException extends Exception {

    /**
     * Creates a new instance of
     * <code>NotesCommunicatorException</code> without detail message.
     */
    public NotesCommunicatorException() {
    }

    /**
     * Constructs an instance of
     * <code>NotesCommunicatorException</code> with the specified detail
     * message.
     *
     * @param msg the detail message.
     */
    public NotesCommunicatorException(String msg) {
        super(msg);
    }
    
    /**
     * Constructs an instance of
     * <code>NotesCommunicatorException</code> with the specified detail
     * message and another exception as Suppressed.
     * @param msg the detail message.
     * @param ex the exception to add as Suppressed.
     */
    public NotesCommunicatorException(String msg, Exception ex) {
        super(msg);
        this.addSuppressed(ex);
    }
}
