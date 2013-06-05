/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import org.jivesoftware.smack.XMPPException;
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
        initNodesListener();
    }

    private void initNodesListener() {
        try {
            SingletonSmack.getInstance().setOnNodesActualization(new ItemEventListener() {
                @Override
                public void handlePublishedItems(ItemPublishEvent ipe) {
                    final String name;

                    // get the name from given Item
                    name = ipe.getNodeId();

                    // add a new node in the listView
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            listNodes.getItems().add(name);
                        }
                    });

                }
            });
        } catch (XMPPException ex) {
            SingletonViewManager.getInstance().showError(ex);
        }
    }
}
