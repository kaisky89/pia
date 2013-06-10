/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

/**
 *
 * @author kaisky89
 */
public class SessionInformation {
    
    private Integer id;
    private String name;
    private String url;
    private String description;
    
    public SessionInformation(String name, String url, String description){
        this.name = name;
        this.url = url;
        this.description = description;
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
                + "<id>" + getId() + "</id>"
                + "<name>" + getName() + "</name>"
                + "<description>" + getDescription() + "</description>"
                + "</session>";
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    
}
