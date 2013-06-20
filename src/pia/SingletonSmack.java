/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.pubsub.*;
import org.jivesoftware.smackx.pubsub.listener.ItemDeleteListener;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import pia.tools.SmartIterable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author kaisky89
 */
final public class SingletonSmack implements NotesCommunicator {

    final private static SingletonSmack instance = new SingletonSmack();
    private ItemEventListener itemListener;
    private ItemDeleteListener deleteListener;

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

    public void resetAll() throws XMPPException {
        DiscoverItems discoverNodes;

        for (discoverNodes = mgr.discoverNodes(null);
                discoverNodes.getItems().hasNext();
                discoverNodes = mgr.discoverNodes(null)) {
            for (DiscoverItems.Item item
                    : new SmartIterable<DiscoverItems.Item>(discoverNodes.getItems())) {
                mgr.deleteNode(item.getNode());

            }
        }
        discoverNodes = mgr.discoverNodes(null);
        for (DiscoverItems.Item item : new SmartIterable<DiscoverItems.Item>(discoverNodes.getItems())) {
            System.out.println("leftover Nodes after reset: " + item.getNode());

        }

        usingSessionInteger = null;
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
        try {
            this.unsetNotesListener();
            this.unsetAvailableSessionListener();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

        // Create leafNode for notes
        LeafNode leafNode;
        try {
            leafNode = mgr.createNode(getNotesLeafNodeString(session.getId()));
            ConfigureForm form = new ConfigureForm(FormType.submit);
            form.setAccessModel(AccessModel.open);
            form.setDeliverPayloads(true);
            form.setPersistentItems(true);
            form.setNotifyRetract(true);
            form.setNotifyConfig(true);
            form.setNotifyDelete(true);
            form.setPublishModel(PublishModel.open);
            form.setCollection("session:" + session.getId());
            leafNode.sendConfigurationForm(form);
        } catch (XMPPException ex) {
            throw new NotesCommunicatorException(
                    "Cant create new LeafNode for " + getNotesLeafNodeString(session.getId()) + ".", ex);
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
            if (items.isEmpty()) {
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
        // before trying to switch, check if the requested Note is available
        // if it's not available, a NotesCommunicatorException will be thrown.
        getItemsLeafNode(id);


        this.unsetNotesListener();
        usingSessionInteger = id;
        this.unsetNotesListener();
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

        // create the id for the note
        String noteId;
        try {
            noteId = "note:" + usingSessionInteger + ":" + createNoteId(note);
        } catch (XMPPException ex) {
            throw new NotesCommunicatorException("Error while trying to create new id for note: " + note, ex);
        }
        // get the LeafNode for notes in the session CollectionNode
        LeafNode leafNode = getItemsLeafNode();

        // add the informations of the note as an item to LeafNode
        addDataItem(note, leafNode);

        // finished, return the id which was generated for the new note
        return note.getId();
    }

    @Override
    public List<Integer> getNoteIds() throws NotesCommunicatorException {
        if (usingSessionInteger == null) {
            throw new NotesCommunicatorException("Need to specify a session before using note Management.");
        }

        // get all items as discover items
        List itemsObject;
        DiscoverItems discoverItems;
        try {
            discoverItems = getItemsLeafNode().discoverItems();
        } catch (XMPPException ex) {
            throw new NotesCommunicatorException(
                    "Error while trying to receive all items from "
                    + getNotesLeafNodeString() + ".", ex);
        }

        // some converting stuff for iteration
        Iterator<DiscoverItems.Item> iterator = discoverItems.getItems();
        SmartIterable<DiscoverItems.Item> smartIterable = new SmartIterable<>(iterator);

        // convert into a integer List
        List<Integer> ids = new LinkedList<>();
        for (DiscoverItems.Item item : smartIterable) {
            ids.add(new Integer(item.getName().split(":")[2]));
        }

        // return the list
        return ids;
    }

    @Override
    public NoteInformation getNoteInformation(Integer id) throws NotesCommunicatorException {
        if (usingSessionInteger == null) {
            throw new NotesCommunicatorException(
                    "Need to specify a session before using note Management.");
        }
        

        // get the leafNode of the notes
        LeafNode leafNode = getItemsLeafNode();

        // define the item, we are looking for
        List<String> itemsToFind = new LinkedList<>();
        itemsToFind.add(getNoteIdString(id));

        // get the dataItem
        List<Item> items;
        try {
            items = leafNode.getItems(itemsToFind);
        } catch (XMPPException ex) {
            throw new NotesCommunicatorException(
                    "Error while trying to receive item: " + getNoteIdString(id), ex);
        }

        // check the list
        String leafNodeId = getNoteIdString(id);
        if (items.isEmpty()) {
            throw new NotesCommunicatorException(
                    "Cannot find the data item in " + leafNodeId);
        }
        if (items.size() > 1) {
            throw new NotesCommunicatorException(
                    "Found more than one data item in " + leafNodeId);
        }

        Item dataItem = items.get(0);
        return getNoteInformationFromItem(dataItem);
    }

    @Override
    public void setNote(Integer id, NoteInformation note) throws NotesCommunicatorException {
        if (usingSessionInteger == null) {
            throw new NotesCommunicatorException("Need to specify a session before using note Management.");
        }

        // check, if the node is locked
        checkIfLocked(id);

        // set the id of the note
        note.setId(id);

        // get the leafNode of the notes
        LeafNode leafNode = getItemsLeafNode();

        // publish to the leafNode
        addDataItem(note, leafNode);
    }

    @Override
    public void deleteNote(Integer id) throws NotesCommunicatorException {
        if (usingSessionInteger == null) {
            throw new NotesCommunicatorException(
                    "Need to specify a session before using note Management.");
        }
        

        // check, if the Note is already locked
        checkIfLocked(id);

        // delete the item of the Note
        try {
            getItemsLeafNode().deleteItem(getNoteIdString(id));
        } catch (XMPPException ex) {
            throw new NotesCommunicatorException(
                    "Cannot find delete note " + getNoteIdString(id), ex);
        }
    }

    @Override
    public void lockNote(Integer id) throws NotesCommunicatorException {
        if (usingSessionInteger == null) {
            throw new NotesCommunicatorException("Need to specify a session before using note Management.");
        }

        // check, if the Note is already locked
        checkIfLocked(id);

        // get the Note
        NoteInformation note = getNoteInformation(id);

        // lock the Note
        note.setLocked(SingletonDataStore.getInstance().getJID());

        // send the Note
        setNote(id, note);
    }

    @Override
    public void unlockNote(Integer id) throws NotesCommunicatorException {
        if (usingSessionInteger == null) {
            throw new NotesCommunicatorException("Need to specify a session before using note Management.");
        }

        // check, if the Note is already locked
        checkIfLocked(id);

        // get the Note
        NoteInformation note = getNoteInformation(id);

        // unlock the Note
        note.unlock();

        // send the Note
        setNote(id, note);
    }

    // Listening Management ////////////////////////////////////////////////////
    
    @Override
    public void setAvailableSessionListener(NotesCommunicatorListener<SessionInformation> availableSessionListener) {
        // TODO: implement this feature!
    }

    @Override
    public void unsetAvailableSessionListener() {
        // TODO: implement this feature!
    }

    @Override
    public void setNotesListener(final NotesCommunicatorListener<NoteInformation> notesListener) throws NotesCommunicatorException {
        if (usingSessionInteger == null) {
            throw new NotesCommunicatorException("Need to specify a session before using note Management.");
        }

        // first, if there is any other subscription yet open, unsubscribe
        unsetNotesListener();
        
        handlePublishedItem(notesListener);
        
        handleDeleteItem(notesListener);
        
        subscribe();
    }

    @Override
    public void unsetNotesListener() throws NotesCommunicatorException {
        if (usingSessionInteger == null) {
            return;
        }

        // find all subscriptions from the user to this node
        List<Subscription> subscriptions;
        try {
            subscriptions = getItemsLeafNode().getSubscriptions();
        } catch (XMPPException ex) {
            throw new NotesCommunicatorException(
                    "Error while trying to get List of subscriptions of "
                    + getNotesLeafNodeString(), ex);
        }
        for (Subscription subscription : subscriptions) {
            try {
                getItemsLeafNode().unsubscribe(
                        SingletonDataStore.getInstance().getJID(),
                        subscription.getId());
            } catch (XMPPException ex) {
                throw new NotesCommunicatorException(
                        "Error while trying to unsubscribe:\n"
                        + "node: " + getNotesLeafNodeString() + "\n"
                        + "subscriptionId: " + subscription.getId() + "\n"
                        + "jid: " + subscription.getJid() + "\n"
                        + "state: " + subscription.getState(), ex);
            }
        }
        
        if(deleteListener == null || itemListener == null)
            return;
        
        getItemsLeafNode().removeItemDeleteListener(deleteListener);
        deleteListener = null;
        
        getItemsLeafNode().removeItemEventListener(itemListener);
        itemListener = null;
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

    private Integer createNoteId(NoteInformation note) throws XMPPException, NotesCommunicatorException {
        // get all ids currently available
        List<Integer> noteIds = getNoteIds();

        // find the highest id
        Integer returnId = 0;
        for (Integer integer : noteIds) {
            if (integer.intValue() > returnId.intValue()) {
                returnId = integer;
            }

        }

        // +1
        returnId++;

        // write it into note, return it
        note.setId(returnId);
        return returnId;
    }

    private void addDataItem(NoteInformation note, LeafNode leafNode) throws NotesCommunicatorException {
        // build noteIdString
        String noteIdString;
        noteIdString = "note:" + usingSessionInteger + ":" + note.getId();

        // add the informations of the note as one item to LeafNode
        SimplePayload payload;
        payload = new SimplePayload("note", "", note.toXml());

        PayloadItem<SimplePayload> item;
        item = new PayloadItem<>(noteIdString, payload);
        try {
            leafNode.send(item);
        } catch (XMPPException ex) {
            throw new NotesCommunicatorException(
                    "Error while sending payload item with data of " + noteIdString, ex);
        }
    }

    private String getNoteIdString(Integer noteId) {
        String nodeIdString = "note:" + usingSessionInteger + ":" + noteId;
        return nodeIdString;
    }

    private void checkIfLocked(Integer id) throws NotesCommunicatorException {

        // get the Note
        NoteInformation note = getNoteInformation(id);


        // if the Note isn't locked at all, everything is fine
        if (!note.isLocked()) {
            return;
        }

        // if the Note is locked, it's ok, if it is me, who has locked it.
        if (note.getLockedBy().equals(SingletonDataStore.getInstance().getJID())) {
            return;
        }

        // otherwise, another person seems to lock it. So, no write access. That's sad.
        throw new NotesCommunicatorException(
                "Cannot write into " + getNoteIdString(id) + ". Note is locked by "
                + note.getLockedBy());
    }

    private NoteInformation getNoteInformationFromItem(Item dataItem) throws NotesCommunicatorException {
        // get the Type of the Item
        String xml = dataItem.toXML();
        NoteType noteType = NoteInformation.getType(xml);

        // Produce the specific NoteInformation, instantiate it with the given xml
        NoteInformation noteInformation = NoteInformation.produceConcreteNoteInformation(noteType);
        noteInformation.initFromXml(xml);

        // finally, return the finished noteInformation
        return noteInformation;
    }

    private void handleDeleteItem(final NotesCommunicatorListener<NoteInformation> notesListener) throws NotesCommunicatorException {
        // handle deleting Items
        getItemsLeafNode().addItemDeleteListener(deleteListener = new ItemDeleteListener() {
            @Override
            public void handleDeletedItems(ItemDeleteEvent items) {
                List<String> itemIds = items.getItemIds();
                for (String string : itemIds) {
                    notesListener.onDelete(new Integer(string.split(":")[2]));
                }
            }

            @Override
            public void handlePurge() {
                notesListener.onPurge();
            }
        });
    }

    private void handlePublishedItem(final NotesCommunicatorListener<NoteInformation> notesListener) throws NotesCommunicatorException {
        // handle incoming Items
        getItemsLeafNode().addItemEventListener(itemListener = new ItemEventListener() {
            @Override
            public void handlePublishedItems(ItemPublishEvent items) {

                // for each element of the list
                List<PayloadItem<SimplePayload>> itemsList;
                itemsList = items.getItems();

                for (PayloadItem<SimplePayload> payloadItem : itemsList) {

                    // convert the item to NoteInformation
                    NoteInformation note = null;
                    try {
                        note = getNoteInformationFromItem(payloadItem);
                    } catch (NotesCommunicatorException ex) {
                        ex.printStackTrace();
                    }

                    notesListener.onPublish(note);
                }

            }
        });
    }

    private void subscribe() throws NotesCommunicatorException {
        try {
            // subscribe to the Node
            SubscribeForm subscribeForm = new SubscribeForm(FormType.submit);
            subscribeForm.setDeliverOn(true);
            subscribeForm.setIncludeBody(true);
            getItemsLeafNode().subscribe(SingletonDataStore.getInstance().getJID(), subscribeForm);
        } catch (XMPPException ex) {
            ex.printStackTrace();
        }
    }

    private String getNotesLeafNodeString(Integer id) {
        return "notes:" + id;
    }

    private String getNotesLeafNodeString() {
        return getNotesLeafNodeString(usingSessionInteger);
    }

    private LeafNode getItemsLeafNode() throws NotesCommunicatorException {
        LeafNode leafNode;
        try {
            leafNode = mgr.getNode(getNotesLeafNodeString());
        } catch (XMPPException ex) {
            throw new NotesCommunicatorException(
                    "Error while trying to get leafNode: " + getNotesLeafNodeString(), ex);
        }
        return leafNode;
    }

    private LeafNode getItemsLeafNode(Integer sessionInteger) throws NotesCommunicatorException {
        LeafNode leafNode;
        try {
            leafNode = mgr.getNode(getNotesLeafNodeString(sessionInteger));
        } catch (XMPPException ex) {
            throw new NotesCommunicatorException(
                    "Error while trying to get leafNode: " + getNotesLeafNodeString(sessionInteger), ex);
        }
        return leafNode;
    }
}
