/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author kaisky89
 */
public class TextNoteInformation extends NoteInformation{
    
    private String text;

    public TextNoteInformation(Long timePosition, String text) {
        super(timePosition, NoteType.TEXT);
        this.text = text;
    }
    
    public TextNoteInformation(String xml) throws NotesCommunicatorException{
        super(xml);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public Map<String, String> getAttributes() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("text", text);
        return hashMap;
    }

    @Override
    public void setAttributes(Map<String, String> attributes) {
        text = attributes.get("text");
    }
    
}
