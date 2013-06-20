/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author kaisky89
 */
public abstract class NoteInformation {

    static NoteType getType(String xml) throws NotesCommunicatorException {
        Document document;
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

    static NoteInformation produceConcreteNoteInformation(NoteType noteType){
        switch (noteType) {
            case TEXT:
                return new TextNoteInformation((long) 0, "");
            default:
                throw new IllegalArgumentException(
                        "Cannot handle noteType: " + noteType);
        }
    }

    private Integer id;
    private Long timePosition;
    private NoteType noteType;
    private String lockedBy;
    private Date lastChange;

    public NoteInformation(Long timePosition, NoteType noteType){
        setTimePosition(timePosition);
        setNoteType(noteType);
        unlock();
        setLastChange();
    }

    public void setLastChange() {
        this.lastChange = new Date();
    }

    public NoteInformation(String xml) throws NotesCommunicatorException{
        initFromXml(xml);
    }

    public void initFromXml(String xml) throws NotesCommunicatorException {
        Document document;
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

        NodeList lockedByNodeList = document.getElementsByTagName("lockedBy");
        this.lockedBy = lockedByNodeList.item(0).getTextContent();

        NodeList lastChangeNodeList = document.getElementsByTagName("lastChange");
        this.lastChange = new Date(new Long(lastChangeNodeList.item(0).getTextContent()));

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NoteInformation)) return false;

        NoteInformation that = (NoteInformation) o;

        return this.toXml().equals(that.toXml());
    }

    public boolean equalsIgnoreId(Object o) {
        if (this == o) return true;
        if (!(o instanceof NoteInformation)) return false;

        NoteInformation that = (NoteInformation) o;

        if (!lockedBy.equals(that.lockedBy)) return false;
        if (noteType != that.noteType) return false;
        if (!timePosition.equals(that.timePosition)) return false;
        if (!this.getAttributes().equals(that.getAttributes())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + timePosition.hashCode();
        result = 31 * result + noteType.hashCode();
        result = 31 * result + lockedBy.hashCode();
        return result;
    }

    @Override
    public String toString() {
        String returnString = "NoteInformation{\n" +
                "   id = " + id + ",\n" +
                "   timePosition = " + timePosition + ",\n" +
                "   noteType = " + noteType + ",\n" +
                "   lockedBy = " + lockedBy + ",\n" +
                "   lastChange = " + lastChange + ",\n" +
                "   Attributes{\n";
        for (String key : getAttributes().keySet())
            returnString +=
                    "      " + key + " = " + getAttributes().get(key) + ",\n";

        returnString += "   }\n" + "}";

        return returnString;
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
        setLastChange();
    }

    public final void setNoteType(NoteType noteType) {
        this.noteType = noteType;
        setLastChange();
    }

    public final void setTimePosition(Long timePosition) {
        this.timePosition = timePosition;
        setLastChange();
    }

    public final void setLocked(String jid) {
        this.lockedBy = jid;
        setLastChange();
    }

    public final String toXml(){
        String returnString;

        returnString = "<note>"
                + "<id>" + getId() + "</id>"
                + "<timePosition>" + getTimePosition() + "</timePosition>"
                + "<noteType>" + getNoteType() + "</noteType>"
                + "<lockedBy>" + lockedBy + "</lockedBy>"
                + "<lastChange>" + lastChange.getTime() + "</lastChange>";
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

    public Date getLastChange() {
        return lastChange;
    }
}
