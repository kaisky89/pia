/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

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
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;

/**
 * FXML Controller class
 *
 * @author kaisky89
 */
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
        try {
            initAllAvailableNodes();
            initNodesListener();
        } catch (XMPPException ex) {
            SingletonViewManager.getInstance().showError(ex);
        }
    }

    @FXML
    private void handleButtonAddNode(ActionEvent e) {
        String askInput = SingletonViewManager.getInstance().askInput(
                "New Node", "Add new Node", "Please enter new node name");
        try {
            SingletonSmack.getInstance().addNode(askInput);
        } catch (XMPPException ex) {
            SingletonViewManager.getInstance().showError(ex);
        }
    }

    @FXML
    private void handleButtonDeleteNode(ActionEvent e) {
        // find out, which Nodes are selected
        ObservableList selectedIndices = listNodes.getSelectionModel().getSelectedIndices();

        // delete all Elements with Smack
        try {
            for (Object object : selectedIndices) {
                int index = ((Integer) object).intValue();
                String nodeName = listNodes.getItems().get(index).toString();
                SingletonSmack.getInstance().deleteNode(nodeName);
            }
        } catch (XMPPException ex) {
            SingletonViewManager.getInstance().showError(ex);
        }

    }

    private void initNodesListener() {
        try {
            SingletonSmack.getInstance().setOnNodesActualization(new ItemEventListener() {
                @Override
                public void handlePublishedItems(ItemPublishEvent ipe) {
                    final List<String> names = new LinkedList<>();

                    // get the names from given Item
                    List<Item> items = ipe.getItems();

                    for (Item item : items) {
                        names.add(item.getId());
                    }

                    // add a new node in the listView
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            listNodes.getItems().addAll(names);
                        }
                    });

                }
            });
        } catch (XMPPException ex) {
            SingletonViewManager.getInstance().showError(ex);
        }
    }

    private void initAllAvailableNodes() throws XMPPException {
        // delete all entries in the listNodes
        listNodes.getItems().clear();

        // get the list from Smack API
        List<String> strings = SingletonSmack.getInstance().getAvailableNodes();

        // insert list into View
        listNodes.getItems().addAll(strings);
    }
}
