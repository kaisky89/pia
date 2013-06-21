package pia.views;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.ItemDeleteEvent;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.listener.ItemDeleteListener;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;

public class MainViewController implements Initializable {

    @FXML
    private ListView listNodes;
    @FXML
    private ListView listItems;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        initViewElements();
//        initSmackElements();
    }
//
//    @FXML
//    private void handleButtonRefresh(ActionEvent e) {
//        try {
//            resetAllAvailableNodes();
//        } catch (XMPPException ex) {
//            SingletonViewManager.getInstance().showError(ex);
//        }
//    }
//
//    @FXML
//    private void handleButtonAddNode(ActionEvent e) {
//        String askInput = SingletonViewManager.getInstance().askInput(
//                "New Node", "Add new Node", "Please enter new node name");
//        try {
//            SingletonSmack.getInstance().addNode(askInput);
//        } catch (XMPPException ex) {
//            SingletonViewManager.getInstance().showError(ex);
//        }
//    }
//
//    @FXML
//    private void handleButtonDeleteNode(ActionEvent e) {
//        // find out, which Nodes are selected
//        ObservableList selectedIndices = listNodes.getSelectionModel().getSelectedIndices();
//
//
//        // delete all Elements with Smack
//        try {
//            for (Object object : selectedIndices) {
//                int index = ((Integer) object).intValue();
//                String nodeName = listNodes.getItems().get(index).toString();
//                SingletonSmack.getInstance().deleteNode(nodeName);
//            }
//        } catch (XMPPException ex) {
//            SingletonViewManager.getInstance().showError(ex);
//        }
//
//    }
//    
//    @FXML
//    private void handleNodeListSelection(ActionEvent e){
//        // clean the old stuff
//        
//        
//        // find out, which Node is selected
//        
//        // initialise 
//    }
//
//    private void initNodesListener() {
//        initListenerForList("availableNodes", listNodes);
//    }
//
//    private void resetAllAvailableNodes() throws XMPPException {
//        resetAllItems(listNodes);
//    }
//
//    private void initViewElements() {
//        listNodes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
//    }
//
//    private void initSmackElements() {
//        try {
//            resetAllAvailableNodes();
//            initNodesListener();
//        } catch (XMPPException ex) {
//            SingletonViewManager.getInstance().showError(ex);
//        }
//    }
//
//    private void initListenerForList(String nodeName, final ListView listView) {
//        try {
//            SingletonSmack.getInstance().setOnActualization(nodeName, new ItemEventListener() {
//                @Override
//                public void handlePublishedItems(ItemPublishEvent ipe) {
//                    final List<String> names = new LinkedList<>();
//
//                    // get the names from given Item
//                    List<Item> items = ipe.getItems();
//
//                    for (Item item : items) {
//                        names.add(item.getId());
//                    }
//
//                    // add a new node in the listView
//                    Platform.runLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            listView.getItems().addAll(names);
//                        }
//                    });
//
//                }
//            }, new ItemDeleteListener() {
//                @Override
//                public void handleDeletedItems(ItemDeleteEvent ide) {
//                    Platform.runLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                resetAllAvailableNodes();
//                            } catch (XMPPException ex) {
//                                SingletonViewManager.getInstance().showError(ex);
//                            }
//                        }
//                    });
//
//                    // get the names of the Items, which are still available
//                    //List<String> names = ide.getItemIds();
//
//                    // delete the Items in the listView
//                    //for (Object object : listNodes.getItems()) {
//                    //}
//                    //}
//                }
//
//                @Override
//                public void handlePurge() {
//                    Platform.runLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                resetAllAvailableNodes();
//                            } catch (XMPPException ex) {
//                                SingletonViewManager.getInstance().showError(ex);
//                            }
//                        }
//                    });
//                }
//            });
//        } catch (XMPPException ex) {
//            SingletonViewManager.getInstance().showError(ex);
//        }
//    }
//
//    private void resetAllItems(ListView listView) throws XMPPException {
//        // delete all entries in the listNodes
//        listView.getItems().clear();
//
//        // get the list from Smack API
//        List<String> strings = SingletonSmack.getInstance().getAvailableNodes();
//
//        // insert list into View
//        listView.getItems().addAll(strings);
//    }
}
