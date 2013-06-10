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
import org.jivesoftware.smackx.pubsub.CollectionNode;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.FormType;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.NodeType;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import pia.tools.SmartIterable;

/**
 *
 * @author kaisky89
 */
public class SingletonSmack implements NotesCommunicator {

    private static SingletonSmack instance = new SingletonSmack();

    private SingletonSmack() {
    }

    public static NotesCommunicator getInstance() {
        return instance;
    }
    private Connection connection;
    private PubSubManager mgr;
    private String sessionCollection = "general:allSessions";
    private Integer nextSessionId = 0;

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

        // Create the node and add an Item in sessionCollection
        ConfigureForm form = new ConfigureForm(FormType.submit);
        form.setAccessModel(AccessModel.open);
        form.setDeliverPayloads(true);
        form.setNotifyRetract(true);
        form.setPersistentItems(true);
        form.setPublishModel(PublishModel.open);
        try {
            String id = createSessionId(session);
            addSessionToCollectionNode(sessionCollection, session);
        } catch (XMPPException ex) {
            throw new NotesCommunicatorException(
                    "cannot create Node for " + session.getName(), ex);
        }

        return session.getId();
    }

    @Override
    public List<Integer> getSessionIds() throws NotesCommunicatorException {
        List<Integer> returnList = new LinkedList<>();
        try {
            CollectionNode node = mgr.getNode(sessionCollection);
            Iterable<String> iterable = new SmartIterable<>(node.getNodeConfiguration().getChildren());
            for (String string : iterable) {
                returnList.add(new Integer(string.split(":")[1]));
            }
        } catch (XMPPException ex) {
            throw new NotesCommunicatorException("cannot find " + sessionCollection, ex);
        }
        return returnList;
    }

    @Override
    public SessionInformation getSessionInformation(Integer id) {
        
        
        return null;
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

    // private help methods ////////////////////////////////////////////////////
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

    private void buildUpStructure() throws XMPPException, NotesCommunicatorException {
        try {
            // get node for the collection of all sessions
            CollectionNode node = mgr.getNode(sessionCollection);
            Iterable<String> children;
            children = new SmartIterable<>(node.getNodeConfiguration().getChildren());
            int i = 0;
            for (String string : children) {
                i++;
            }
            System.out.println("Anzahl der Nodes in sessionCollection: " + i);

            // get the id of the session with the highest id
            for (Integer integer : getSessionIds()) {
                System.out.println("vergleiche: " + integer);
                if (integer.compareTo(nextSessionId) > 0) {
                    nextSessionId = integer;
                }
            }

        } catch (XMPPException ex) {
            // If Node doesn't exist, create the node
            ConfigureForm form = new ConfigureForm(FormType.submit);
            form.setAccessModel(AccessModel.open);
            form.setDeliverPayloads(true);
            form.setNotifyRetract(true);
            form.setPersistentItems(true);
            form.setPublishModel(PublishModel.open);
            form.setNodeType(NodeType.collection);
            mgr.createNode(sessionCollection, form);
            nextSessionId = 0;
        }
    }

    private String createSessionId(SessionInformation session) {
        session.setId(++nextSessionId);
        System.out.println("creating SessionId: " + session.getId());
        return session.getId().toString();
    }

    private void addSessionToCollectionNode(
            String collectionNodeString,
            SessionInformation session)
            throws XMPPException {
        // Get the collection node
        //CollectionNode collectionNode = mgr.getNode(collectionNodeString);

        // Prepare the payload
        SimplePayload payload;
        payload = new SimplePayload("session", "", session.toXML());

        // Prepare the item
        PayloadItem item = new PayloadItem("sessionInfo:" + session.getId(), payload);

        // create the Leaf Node in the Collection Node
        ConfigureForm form = new ConfigureForm(FormType.submit);
        form.setAccessModel(AccessModel.open);
        form.setDeliverPayloads(true);
        form.setNotifyRetract(true);
        form.setPersistentItems(true);
        form.setPublishModel(PublishModel.open);
        form.setCollection(collectionNodeString);
        LeafNode leaf = mgr.createNode("session:" + session.getId());
        leaf.sendConfigurationForm(form);
        
        // set the Properties of the Session
        leaf.send(item);
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
