/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

import java.util.Map;

/**
 *
 * @author kaisky89
 */
public interface NoteInformation {
    
    public Integer getId();
    public void setId(Integer id);
    
    public Integer getTimePosition();
    public void setTimePosition(Integer timePosition);
    
    public Map<String, String> getAdditionalAttributes();
    public void setAdditionalAttributes(Map<String, String> additionalAttributes);
    
    public NoteType getNoteType();
    public void setNoteType(NoteType noteType);
}
