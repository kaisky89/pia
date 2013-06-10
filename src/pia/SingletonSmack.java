/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

import java.util.LinkedList;
import java.util.List;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.FormType;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.pubsub.listener.ItemDeleteListener;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;

/**
 *
 * @author kaisky89
 */
public class SingletonSmack implements NotesCommunicator{

    private static NotesCommunicator instance = new SingletonSmack();

    private SingletonSmack() {
    }

    public static NotesCommunicator getInstance() {
        return instance;
    }
    private Connection connection;
    private PubSubManager mgr;
    private String sessionCollection;
    private Integer nextSessionId;

    private void connect() throws XMPPException {
        connection = new XMPPConnection(
                SingletonDataStore.getInstance().getServerAdress());
        connection.connect();
    }

    private void login() throws XMPPException {
        UserData user = SingletonDataStore.getInstance().getUser();
        connection.login(user.getUsername(), user.getPassword());
        mgr = new PubSubManager(connection);
    }

    public List<String> getAvailableNodes() throws XMPPException {
        return getAvailableItems("availableNodes");
    }

    public void setOnNodesActualization(
            ItemEventListener<Item> onAdd,
            ItemDeleteListener onDelete)
            throws XMPPException {
        setOnActualization("availableNodes", onAdd, onDelete);
    }

    private void cleanSubscriptions(LeafNode node) throws XMPPException {
        node.unsubscribe(SingletonDataStore.getInstance().getJID());
    }

    public void addNode(String name) throws XMPPException {

        // Create the node
        ConfigureForm form = new ConfigureForm(FormType.submit);
        form.setAccessModel(AccessModel.open);
        form.setDeliverPayloads(true);
        form.setNotifyRetract(true);
        form.setPersistentItems(true);
        form.setPublishModel(PublishModel.open);
        mgr.createNode(name, form);

        addItem("availableNodes", name);
    }

    void deleteNode(String nodeName) throws XMPPException {
        // delete the node
        mgr.deleteNode(nodeName);

        // delete the entry in "availableNodes"
        LeafNode node = mgr.getNode("availableNodes");
        node.deleteItem(nodeName);

    }

    public void setOnActualization(String nodeName, ItemEventListener<Item> onAdd, ItemDeleteListener onDelete) throws XMPPException {
        // Get the node
        LeafNode node = mgr.getNode(nodeName);

        node.addItemEventListener(onAdd);
        node.addItemDeleteListener(onDelete);
        cleanSubscriptions(node);
        node.subscribe(SingletonDataStore.getInstance().getJID());
    }

    private List<String> getAvailableItems(String nodeName) throws XMPPException {
        try {
            LeafNode node = mgr.getNode(nodeName);
        } catch (XMPPException ex) {
            // Create the node
            LeafNode leaf = mgr.createNode(nodeName);
            ConfigureForm form = new ConfigureForm(FormType.submit);
            form.setAccessModel(AccessModel.open);
            form.setDeliverPayloads(false);
            form.setNotifyRetract(true);
            form.setPersistentItems(true);
            form.setPublishModel(PublishModel.open);
            leaf.sendConfigurationForm(form);
        }

        LeafNode node = mgr.getNode(nodeName);
        List list = node.getItems();
        List<String> returnList = new LinkedList<>();
        for (Object object : list) {
            returnList.add(((Item) object).getId());
        }
        return returnList;
    }
    
    // NotesCommunicator Interface Implementation //////////////////////////////

    @Override
    public void init() throws NotesCommunicatorException {
        try {
            connect();
        } catch (XMPPException ex) {
            throw new NotesCommunicatorException("Error while building up connection.", ex);
        }
        try {
            login();
        } catch (XMPPException ex) {
            throw new NotesCommunicatorException("Error while login.", ex);
        }
        try {
            buildUpStructure();
        } catch (XMPPException ex) {
            throw new NotesCommunicatorException("Error while building up structure.", ex);
        }
    }

    @Override
    public Integer addSession(SessionInformation session) throws NotesCommunicatorException {
        
        // Create the node
        ConfigureForm form = new ConfigureForm(FormType.submit);
        form.setAccessModel(AccessModel.open);
        form.setDeliverPayloads(true);
        form.setNotifyRetract(true);
        form.setPersistentItems(true);
        form.setPublishModel(PublishModel.open);
        try {
            mgr.createNode(createSessionId(session), form);
            addItem(sessionCollection, session);
        } catch (XMPPException ex) {
            throw new NotesCommunicatorException(
                    "cannot create Node for " + session.getName(), ex);
        }
        
        return session.getId();
    }

    @Override
    public List<Integer> getSessionIds() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SessionInformation getSessionInformation(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setUsingSession(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteSession(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer addNote(NoteInformation note) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Integer> getNoteIds() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NoteInformation getNoteInformation(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setNote(Integer id, NoteInformation note) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteNote(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void lockNote(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void unlockNote(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAvailableSessionListener(NotesCommunicatorListener<SessionInformation> availableSessionListener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void unsetAvailableSessionListener() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setNotesListener(NotesCommunicatorListener<NoteInformation> notesListener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void unsetNotesListener() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void buildUpStructure() throws XMPPException {
        sessionCollection = "general:allSessions";
        try {
            // get node for the collection of all sessions
            LeafNode node = mgr.getNode(sessionCollection);
            
            // get the id of the session with the highest id
            for (Item item : node.getItems()) {
                Integer integer = new Integer(item.getId().split(":")[1]);
                if(integer.compareTo(nextSessionId) > 0)
                    nextSessionId = integer;
            }

        } catch (XMPPException ex) {
            // If Node doesn't exist, create the node
            LeafNode leaf = mgr.createNode(sessionCollection);
            ConfigureForm form = new ConfigureForm(FormType.submit);
            form.setAccessModel(AccessModel.open);
            form.setDeliverPayloads(true);
            form.setNotifyRetract(true);
            form.setPersistentItems(true);
            form.setPublishModel(PublishModel.open);
            leaf.sendConfigurationForm(form);
            nextSessionId = 0;
        }
    }

    private String createSessionId(SessionInformation session) {
        session.setId(++nextSessionId);
        return session.getId().toString();
    }

    private void addItem(String nodeString, SessionInformation session) throws XMPPException {
        // Get the node
        LeafNode leafnode = mgr.getNode(nodeString);
        
        // Prepare the payload
        SimplePayload payload;
        payload = new SimplePayload("session", "", session.toXML());
        
        // Prepare the item
        PayloadItem item = new PayloadItem("session:" + session.getId(), payload);
        
        // send item to the node
        leafnode.send(item);
    }
    
    private void addItem(String nodeString, String id) throws XMPPException {

        // Get the node
        LeafNode leafnode = mgr.getNode(nodeString);

        // Publish an Item with the specified id
        Item item;
        item = new Item(id);
        leafnode.send(item);
    }
}
