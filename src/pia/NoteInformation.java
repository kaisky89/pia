/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

import com.sun.org.apache.xpath.internal.operations.Equals;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            throw new NotesCommunicatorException("Error while parsing xml: " + xml, ex);
        }

        NodeList noteTypeNodeList = document.getElementsByTagName("noteType");
        return NoteType.valueOf(noteTypeNodeList.item(0).getTextContent());
    }
    
    private Integer id;
    private Long timePosition;
    private NoteType noteType;
    private String lockedBy;
    
    public NoteInformation(Long timePosition, NoteType noteType){
        setTimePosition(timePosition);
        setNoteType(noteType);
        unlock();
    }
    
    public NoteInformation(String xml) throws NotesCommunicatorException{
        Document document = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            StringReader sr = new StringReader(xml);
            document = builder.parse(new InputSource(sr));
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            throw new NotesCommunicatorException("Error while parsing xml: " + xml, ex);
        }

        NodeList idNodeList = document.getElementsByTagName("id");
        this.id = new Integer(idNodeList.item(0).getTextContent());

        NodeList timePositionNodeList = document.getElementsByTagName("timePosition");
        this.timePosition = new Long(timePositionNodeList.item(0).getTextContent());

        NodeList noteTypeNodeList = document.getElementsByTagName("noteType");
        this.noteType = NoteType.valueOf(noteTypeNodeList.item(0).getTextContent());
        
        NodeList lockedByTypeNodeList = document.getElementsByTagName("lockedBy");
        this.lockedBy = lockedByTypeNodeList.item(0).getTextContent();
        
        NodeList attributeNodeList = document.getElementsByTagName("attribute");
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < attributeNodeList.getLength(); i++) {
            Node item = attributeNodeList.item(i);
            // get the key
            String key = item.getAttributes().getNamedItem("name").getNodeValue();
            
            // get the value
            String value = item.getTextContent();
            
            // add it to map
            //System.out.println("  NoteInformation: adding <" + key + ", " 
            //        + value + "> to Map");
            map.put(key, value);
        }
        setAttributes(map);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.timePosition);
        hash = 67 * hash + (this.noteType != null ? this.noteType.hashCode() : 0);
        hash = 67 * hash + Objects.hashCode(this.lockedBy);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NoteInformation other = (NoteInformation) obj;
        if (!Objects.equals(this.timePosition, other.timePosition)) {
            return false;
        }
        if (this.noteType != other.noteType) {
            return false;
        }
        if (!Objects.equals(this.lockedBy, other.lockedBy)) {
            return false;
        }
        return true;
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
        return (!"free".equals(lockedBy));
    }

    public String getLockedBy() {
        return lockedBy;
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

    public final void setLocked(String jid) {
        this.lockedBy = jid;
    }
    
    public final String toXml(){
        String returnString;
        
        returnString = "<note>"
                + "<id>" + getId() + "</id>"
                + "<timePosition>" + getTimePosition() + "</timePosition>"
                + "<noteType>" + getNoteType() + "</noteType>"
                + "<lockedBy>" + lockedBy + "</lockedBy>";
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

    public void unlock() {
        lockedBy = "free";
    }
}
