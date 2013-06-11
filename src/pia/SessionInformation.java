/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

import java.io.StringReader;
import java.util.Objects;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author kaisky89
 */
public class SessionInformation {

    private Integer id;
    private String name;
    private String url;
    private String description;

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.name);
        hash = 41 * hash + Objects.hashCode(this.url);
        hash = 41 * hash + Objects.hashCode(this.description);
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
        final SessionInformation other = (SessionInformation) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.url, other.url)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        return true;
    }

    public SessionInformation(String name, String url, String description) {
        this.name = name;
        this.url = url;
        this.description = description;
    }

    public SessionInformation(String xml) throws NotesCommunicatorException {
        Document document = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            StringReader sr = new StringReader(xml);
            document = builder.parse(new InputSource(sr));
        } catch (Exception ex) {
            throw new NotesCommunicatorException("Error while parsing xml: " + xml, ex);
        }
        NodeList nameNodeList = document.getElementsByTagName("name");
        this.name = nameNodeList.item(0).getTextContent();

        NodeList urlNodeList = document.getElementsByTagName("url");
        this.url = urlNodeList.item(0).getTextContent();

        NodeList descriptionNodeList = document.getElementsByTagName("description");
        this.description = descriptionNodeList.item(0).getTextContent();

        NodeList idNodeList = document.getElementsByTagName("id");
        this.id = new Integer(idNodeList.item(0).getTextContent().split(":")[1]);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public String toXML() {
        return "<session>"
                + "<id>session:" + getId() + "</id>"
                + "<name>" + getName() + "</name>"
                + "<url>" + getUrl() + "</url>"
                + "<description>" + getDescription() + "</description>"
                + "</session>";
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
