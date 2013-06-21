package pia.views;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import pia.StreamPlayer;


public class MainWindowController {

    private StreamPlayer player;

    @FXML
    public AnchorPane root;

    @FXML
    private AnchorPane playerControls;

    @FXML
    private AnchorPane notesPane;

    public void setPlayer(StreamPlayer player) {
        this.player = player;
        playerControls.getProperties().put("player", player);
    }

    @FXML
    void initialize() {

    }
}
