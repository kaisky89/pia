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
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.CollectionNode;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.FormType;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.Node;
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
    private LeafNode usingSessionNode;

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
    public SessionInformation getSessionInformation(Integer id) throws NotesCommunicatorException {
        //get the Information Item
        PayloadItem<SimplePayload> infoItem;
        try {
            LeafNode leafNode = mgr.getNode("session:" + id);
            Collection<String> col = new LinkedList<>();
            col.add("sessionInfo:" + id);
            List<PayloadItem> items = leafNode.getItems(col);
            if (items.size() > 1) {
                throw new NotesCommunicatorException("There seems to be more than one sessionInfo.");
            }
            infoItem = items.get(0);
        } catch (XMPPException ex) {
            throw new NotesCommunicatorException("cant find node: session:" + id, ex);
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
        try {
            usingSessionNode = mgr.getNode("session:" + id);
        } catch (XMPPException ex) {
            throw new NotesCommunicatorException("cant find the session: session" + id, ex);
        }
        usingSessionInteger = id;
    }

    @Override
    public void deleteSession(Integer id) throws NotesCommunicatorException {
        try {
            mgr.deleteNode("session:" + id);
        } catch (XMPPException ex) {
            throw new NotesCommunicatorException("Error while trying to delete LeafNode of session:" + id, ex);
        }

        // ToDo: alle LN: note:... Nodes entfernen
        //        try {
        //            DiscoverItems discoverNodes = mgr.discoverNodes(null);
        //            SmartIterable<DiscoverItems.Item> si = new SmartIterable<>(discoverNodes.getItems());
        //            for (DiscoverItems.Item item : si) {
        //                System.out.println(item.toXML());
        //            }
        //            
        //        } catch (XMPPException ex) {
        //        }
        //        }
    }

    // Note Management /////////////////////////////////////////////////////////
    @Override
    public Integer addNote(NoteInformation note) throws NotesCommunicatorException {
        if (usingSessionInteger == null) {
            throw new NotesCommunicatorException("Need to specify a session before using note Management.");
        }

        // create the id for the note
        String noteId;
        try {
            noteId = "note:" + usingSessionInteger + ":" + createNoteId(note);
        } catch (XMPPException ex) {
            throw new NotesCommunicatorException("Error while trying to create new id for note: " + note, ex);
        }

        // add item to the Leaf Node of the Session
        usingSessionNode.publish(new Item(noteId));

        // add a new LeafNode for the new note
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
            leafNode.sendConfigurationForm(form);
        } catch (XMPPException ex) {
            throw new NotesCommunicatorException("Cant create new LeafNode for " + noteId + ".", ex);
        }

        // add the informations of the note as items to LeafNode
        Collection<PayloadItem> items = new LinkedList<>();
        items.add(new PayloadItem("id", new SimplePayload("id", "", "<id>" + note.getId() + "</id>")));
        items.add(new PayloadItem("noteType", new SimplePayload("noteType", "", "<noteType>" + note.getNoteType() + "</noteType>")));
        items.add(new PayloadItem("timePosition", new SimplePayload("timePosition", "", "<timePosition>" + note.getTimePosition() + "</timePosition>")));
        if (note.getAdditionalAttributes() != null) {
            for (Map.Entry<String, String> entry : note.getAdditionalAttributes().entrySet()) {
                items.add(new PayloadItem(entry.getKey(), new SimplePayload(
                        entry.getKey(), "",
                        "<" + entry.getKey() + ">" + entry.getValue() + "</" + entry.getKey() + ">")));
            }
        }

        leafNode.publish(items);

        // finished, return the id which was generated for the new note
        return note.getId();
    }

    @Override
    public List<Integer> getNoteIds() throws NotesCommunicatorException {
        if (usingSessionInteger == null) {
            throw new NotesCommunicatorException("Need to specify a session before using note Management.");
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NoteInformation getNoteInformation(Integer id) throws NotesCommunicatorException {
        if (usingSessionInteger == null) {
            throw new NotesCommunicatorException("Need to specify a session before using note Management.");
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setNote(Integer id, NoteInformation note) throws NotesCommunicatorException {
        if (usingSessionInteger == null) {
            throw new NotesCommunicatorException("Need to specify a session before using note Management.");
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteNote(Integer id) throws NotesCommunicatorException {
        if (usingSessionInteger == null) {
            throw new NotesCommunicatorException("Need to specify a session before using note Management.");
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void lockNote(Integer id) throws NotesCommunicatorException {
        if (usingSessionInteger == null) {
            throw new NotesCommunicatorException("Need to specify a session before using note Management.");
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void unlockNote(Integer id) throws NotesCommunicatorException {
        if (usingSessionInteger == null) {
            throw new NotesCommunicatorException("Need to specify a session before using note Management.");
        }
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void unsetNotesListener() throws NotesCommunicatorException {
        if (usingSessionInteger == null) {
            throw new NotesCommunicatorException("Need to specify a session before using note Management.");
        }
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

    private String createSessionId(SessionInformation session) throws NotesCommunicatorException {
        try {
            buildUpStructure();
        } catch (XMPPException ex) {
            throw new NotesCommunicatorException("Error while generating new id for session.", ex);
        }
        session.setId(++nextSessionId);
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

    private Integer createNoteId(NoteInformation note) throws XMPPException {
        // get all ids currently available
        List<Item> items = usingSessionNode.getItems();

        // find the highest id
        Integer returnId = 0;
        for (Item item : items) {
            if (item.getId().startsWith("note:")) {
                Integer itemIdInteger = new Integer(item.getId().split(":")[2]);
                if (itemIdInteger > returnId) {
                    returnId = itemIdInteger;
                }
            }
        }

        // +1
        returnId++;

        // write it into note, return it
        note.setId(returnId);
        return returnId;
    }
}
