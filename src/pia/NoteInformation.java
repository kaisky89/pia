/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

/**
 *
 * @author kaisky89
 */
public interface NoteInformation {
    
    public Integer getId();
    public void setId(Integer id);
    
    public Long getTimePosition();
    public void setTimePosition(Long timePosition);
    
    public NoteType getNoteType();
    public void setNoteType(NoteType noteType);
    
    public String toXml();
}
