/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author kaisky89
 */
public abstract class NoteInformation {

    static NoteType getType(String xml) throws NotesCommunicatorException {
        Document document = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            StringReader sr = new StringReader(xml);
            document = builder.parse(new InputSource(sr));
        } catch (Exception ex) {
            throw new NotesCommunicatorException("Error while parsing xml: " + xml, ex);
        }

        NodeList noteTypeNodeList = document.getElementsByTagName("noteType");
        return NoteType.valueOf(noteTypeNodeList.item(0).getTextContent());
    }
    
    private Integer id;
    private Long timePosition;
    private NoteType noteType;
    private boolean locked;
    
    public NoteInformation(Long timePosition, NoteType noteType){
        setTimePosition(timePosition);
        setNoteType(noteType);
        setLocked(false);
    }
    
    public NoteInformation(String xml) throws NotesCommunicatorException{
        Document document = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            StringReader sr = new StringReader(xml);
            document = builder.parse(new InputSource(sr));
        } catch (Exception ex) {
            throw new NotesCommunicatorException("Error while parsing xml: " + xml, ex);
        }

        NodeList idNodeList = document.getElementsByTagName("id");
        this.id = new Integer(idNodeList.item(0).getTextContent());

        NodeList timePositionNodeList = document.getElementsByTagName("timePosition");
        this.timePosition = new Long(timePositionNodeList.item(0).getTextContent());

        NodeList noteTypeNodeList = document.getElementsByTagName("noteType");
        this.noteType = NoteType.valueOf(noteTypeNodeList.item(0).getTextContent());
        
        NodeList attributeNodeList = document.getElementsByTagName("attribute");
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < attributeNodeList.getLength(); i++) {
            Node item = attributeNodeList.item(i);
            // get the key
            String key = item.getAttributes().getNamedItem("name").getNodeValue();
            
            // get the value
            String value = item.getTextContent();
            
            // add it to map
            System.out.println("  NoteInformation: adding <" + key + ", " 
                    + value + "> to Map");
            map.put(key, value);
        }
        setAttributes(map);
    }

    public final Integer getId() {
        return id;
    }

    public final NoteType getNoteType() {
        return noteType;
    }

    public final Long getTimePosition() {
        return timePosition;
    }

    public boolean isLocked() {
        return locked;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public final void setNoteType(NoteType noteType) {
        this.noteType = noteType;
    }

    public final void setTimePosition(Long timePosition) {
        this.timePosition = timePosition;
    }

    public final void setLocked(boolean locked) {
        this.locked = locked;
    }
    
    public final String toXml(){
        String returnString;
        
        returnString = "<note>"
                + "<id>" + getId() + "</id>"
                + "<timePosition>" + getTimePosition() + "</timePosition>"
                + "<noteType>" + getNoteType() + "</noteType>";
        for (Map.Entry<String, String> entry : getAttributes().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            returnString += "<attribute name=\"" + key + "\">" + value + "</attribute>";
        }
        returnString += "</note>";
        
        return returnString;
    }
    
    public abstract Map<String, String> getAttributes();
    public abstract void setAttributes(Map<String, String> attributes);
}
