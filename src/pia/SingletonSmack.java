/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.DiscoverItems;
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
    private Integer usingSessionInteger = null;
    private CollectionNode usingSessionNode;

    public void resetAll() throws XMPPException {
        DiscoverItems discoverNodes;
        for (int i = 0; i < 10; i++) {
            discoverNodes = mgr.discoverNodes(null);
            for (DiscoverItems.Item item : new SmartIterable<DiscoverItems.Item>(discoverNodes.getItems())) {
                mgr.deleteNode(item.getNode());

            }
        }
        discoverNodes = mgr.discoverNodes(null);
        for (DiscoverItems.Item item : new SmartIterable<DiscoverItems.Item>(discoverNodes.getItems())) {
            System.out.println("leftover Nodes after reset: " + item.getNode());

        }
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
    public void close() {
        connection.disconnect();
    }

    // Session Management //////////////////////////////////////////////////////
    @Override
    public Integer addSession(SessionInformation session) throws NotesCommunicatorException {

        // Create the node and add an Item in sessionCollection
        try {
            createSessionId(session);
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
    public SessionInformation getSessionInformation(Integer id) throws NotesCommunicatorException {
        //get the Information Item
        PayloadItem<SimplePayload> infoItem;
        try {
            LeafNode leafNode = mgr.getNode("sessionInfo:" + id);
            List<PayloadItem> items = leafNode.getItems();
            if (items.size() > 1) {
                throw new NotesCommunicatorException("There seems to be more than one sessionInfo.");
            }
            if (items.isEmpty()){
                throw new NotesCommunicatorException("Cannot find node: sessionInfo:" + id);
            }
            infoItem = items.get(0);
        } catch (XMPPException ex) {
            throw new NotesCommunicatorException("cant find node: sessionInfo:" + id, ex);
        }

        //get the XML from the Item
        SimplePayload simplePayload = infoItem.getPayload();
        String xml = simplePayload.toXML();

        //create session from xml and return it
        SessionInformation session = new SessionInformation(xml);
        return session;
    }

    @Override
    public void setUsingSession(Integer id) throws NotesCommunicatorException {
        refreshUsingSession(id);
        usingSessionInteger = id;
    }

    @Override
    public void deleteSession(Integer id) throws NotesCommunicatorException {
        try {
            DiscoverItems discoverNodes = mgr.discoverNodes("session:" + id);
            for (DiscoverItems.Item item : new SmartIterable<DiscoverItems.Item>(discoverNodes.getItems())) {
                mgr.deleteNode(item.getNode());
            }
            mgr.deleteNode("session:" + id);
        } catch (XMPPException ex) {
            throw new NotesCommunicatorException("Error while trying to delete session:" + id, ex);
        }
    }

    // Note Management /////////////////////////////////////////////////////////
    @Override
    public Integer addNote(NoteInformation note) throws NotesCommunicatorException {
        if (usingSessionInteger == null) {
            throw new NotesCommunicatorException("Need to specify a session before using note Management.");
        }
        refreshUsingSession(usingSessionInteger);

        // create the id for the note
        String noteId;
        try {
            noteId = "note:" + usingSessionInteger + ":" + createNoteId(note);
        } catch (XMPPException ex) {
            throw new NotesCommunicatorException("Error while trying to create new id for note: " + note, ex);
        }

        // add item to the Leaf Node of the Session
//        try {
//            usingSessionNode.send(new PayloadItem(noteId, new SimplePayload("note", "", "<note>123</note>")));
//        } catch (XMPPException ex) {
//            throw new NotesCommunicatorException("Error while trying to publish note " + noteId + " as item from " + usingSessionNode.getId(), ex);
//        }

        // add a new LeafNode for the new note to the session CollectionNode
        LeafNode leafNode;
        try {
            leafNode = mgr.createNode(noteId);
            ConfigureForm form = new ConfigureForm(FormType.submit);
            form.setAccessModel(AccessModel.open);
            form.setDeliverPayloads(true);
            form.setPersistentItems(true);
            form.setNotifyRetract(true);
            form.setNotifyConfig(true);
            form.setNotifyDelete(true);
            form.setPublishModel(PublishModel.open);
            form.setCollection("session:" + usingSessionInteger);
            leafNode.sendConfigurationForm(form);
        } catch (XMPPException ex) {
            throw new NotesCommunicatorException("Cant create new LeafNode for " + noteId + ".", ex);
        }

        // add the informations of the note as one item to LeafNode
        SimplePayload payload;
        payload = new SimplePayload("note", "", note.toXml());
        
        PayloadItem<SimplePayload> item;
        item = new PayloadItem<>("data", payload);
        try {
            leafNode.send(item);
        } catch (XMPPException ex) {
            throw new NotesCommunicatorException(
                    "Error while creating payload item with data of " + noteId, ex);
        }

        // finished, return the id which was generated for the new note
        return note.getId();
    }

    @Override
    public List<Integer> getNoteIds() throws NotesCommunicatorException {
        if (usingSessionInteger == null) {
            throw new NotesCommunicatorException("Need to specify a session before using note Management.");
        }
        refreshUsingSession(usingSessionInteger);

        // get all Notes
        DiscoverItems discoverNodes;
        try {
            discoverNodes = mgr.discoverNodes("session:" + usingSessionInteger);
        } catch (XMPPException ex) {
            throw new NotesCommunicatorException("Error while trying to get items from node: " + usingSessionNode, ex);
        }

        // filter the LeafNodes, and extract the id from each LeafNode
        List<Integer> ids = new LinkedList<>();
        for (DiscoverItems.Item item : new SmartIterable<DiscoverItems.Item>(discoverNodes.getItems())) {
            if (item.getNode().startsWith("note:")) {
                ids.add(new Integer(item.getNode().split(":")[2]));
            }
        }

        // return the list
        return ids;
    }

    @Override
    public NoteInformation getNoteInformation(Integer id) throws NotesCommunicatorException {
        if (usingSessionInteger == null) {
            throw new NotesCommunicatorException("Need to specify a session before using note Management.");
        }
        refreshUsingSession(usingSessionInteger);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setNote(Integer id, NoteInformation note) throws NotesCommunicatorException {
        if (usingSessionInteger == null) {
            throw new NotesCommunicatorException("Need to specify a session before using note Management.");
        }
        refreshUsingSession(usingSessionInteger);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteNote(Integer id) throws NotesCommunicatorException {
        if (usingSessionInteger == null) {
            throw new NotesCommunicatorException("Need to specify a session before using note Management.");
        }
        refreshUsingSession(usingSessionInteger);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void lockNote(Integer id) throws NotesCommunicatorException {
        if (usingSessionInteger == null) {
            throw new NotesCommunicatorException("Need to specify a session before using note Management.");
        }
        refreshUsingSession(usingSessionInteger);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void unlockNote(Integer id) throws NotesCommunicatorException {
        if (usingSessionInteger == null) {
            throw new NotesCommunicatorException("Need to specify a session before using note Management.");
        }
        refreshUsingSession(usingSessionInteger);
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
    public void setNotesListener(NotesCommunicatorListener<NoteInformation> notesListener) throws NotesCommunicatorException {
        if (usingSessionInteger == null) {
            throw new NotesCommunicatorException("Need to specify a session before using note Management.");
        }
        refreshUsingSession(usingSessionInteger);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void unsetNotesListener() throws NotesCommunicatorException {
        if (usingSessionInteger == null) {
            throw new NotesCommunicatorException("Need to specify a session before using note Management.");
        }
        refreshUsingSession(usingSessionInteger);
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

            // get the id of the session with the highest id
            for (Integer integer : getSessionIds()) {
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

    private Integer createSessionId(SessionInformation session) throws NotesCommunicatorException {
        try {
            buildUpStructure();
        } catch (XMPPException ex) {
            throw new NotesCommunicatorException("Error while generating new id for session.", ex);
        }
        session.setId(++nextSessionId);
        return session.getId();
    }

    private void addSessionToCollectionNode(
            String collectionNodeString,
            SessionInformation session)
            throws XMPPException {


        // create the Collection Node for the session in the Collection Node of 
        // the session Collection.
        ConfigureForm form = new ConfigureForm(FormType.submit);
        form.setAccessModel(AccessModel.open);
        form.setDeliverPayloads(true);
        form.setNotifyRetract(true);
        form.setPersistentItems(true);
        form.setPublishModel(PublishModel.open);
        form.setNodeType(NodeType.collection);
        form.setCollection(collectionNodeString);
        CollectionNode sessionCollectionNode = (CollectionNode) mgr.createNode("session:" + session.getId(), form);

        // create the LeafNode for the Session Information
        form.setNodeType(NodeType.leaf);
        form.setCollection("session:" + session.getId());
        LeafNode createNode = mgr.createNode("sessionInfo:" + session.getId());
        createNode.sendConfigurationForm(form);
        
        // Prepare the payload
        SimplePayload payload;
        payload = new SimplePayload("session", "", session.toXML());

        // Prepare the item
        PayloadItem item = new PayloadItem("sessionInfo:" + session.getId(), payload);

        
        // set the Properties of the Session
        LeafNode sessionInformationLeafNode;
        sessionInformationLeafNode = mgr.getNode("sessionInfo:" + session.getId());
        sessionInformationLeafNode.send(item);
    }

    private void addItem(String nodeString, String id) throws XMPPException {

        // Get the node
        LeafNode leafnode = mgr.getNode(nodeString);

        // Publish an Item with the specified id
        Item item;
        item = new Item(id);
        leafnode.send(item);
    }

    private Integer createNoteId(NoteInformation note) throws XMPPException {
        // get all ids currently available
        List<Item> items = usingSessionNode.getItems();

        System.out.println("  createNoteId: Anzahl gefundener items ingesamt: " + items.size());

        // find the highest id
        Integer returnId = 0;
        for (Item item : items) {
            System.out.println("  createNoteId: item:" + item.getId() + ": " + item.toXML());
            if (item.getId().startsWith("note:")) {
                Integer itemIdInteger = new Integer(item.getId().split(":")[2]);
                if (itemIdInteger.intValue() > returnId.intValue()) {
                    returnId = itemIdInteger;
                }
            }
        }

        System.out.println("  createNoteId: highestId = " + returnId);

        // +1
        returnId++;

        System.out.println("  createNoteId: returnId = " + returnId);

        // write it into note, return it
        note.setId(returnId);
        return returnId;
    }

    private void refreshUsingSession(Integer id) throws NotesCommunicatorException {
        try {
            usingSessionNode = mgr.getNode("session:" + id);
        } catch (XMPPException ex) {
            throw new NotesCommunicatorException("cant find the session: session:" + id, ex);
        }
    }
}
