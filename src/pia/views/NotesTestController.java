package pia.views;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class NotesTestController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Pane notesPad;


    @FXML
    void initialize() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Note.fxml"));
        Parent newNote = (Parent) loader.load();
        if (newNote == null)
            System.out.println("note null");
        notesPad.getChildren().add(newNote);
        newNote.setLayoutX(350);
        newNote.setLayoutY(70);
        if (loader.getController() == null)
            System.out.println("no controller");
    }

}
