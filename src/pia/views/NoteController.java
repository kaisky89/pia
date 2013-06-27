package pia.views;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;


public class NoteController {

    @FXML
    private TextArea noteTextArea;

    @FXML
    private AnchorPane root;

    @FXML
    private void handleNoteClicked(MouseEvent event) {
        if (!noteTextArea.isEditable()) {
            startEditing();
            event.consume();
        }
    }

    @FXML
    private void handleKeyPressed(KeyEvent event) {
        System.out.println("key"+event.getCharacter());
        if (event.getCode() == KeyCode.ENTER) {
            endEditing();
            event.consume();
        }
    }

    private void startEditing() {
        System.out.println("start edit");
        noteTextArea.setEditable(true);
        root.getStyleClass().add("editing");
    }

    private void endEditing() {
        System.out.println("end edit");
        noteTextArea.setEditable(false);
        root.getStyleClass().removeAll("editing");
    }

    @FXML
    void initialize() {
        //System.out.println("init note");
        noteTextArea.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue,
                                Boolean oldVal, Boolean newVal) {
                if (newVal)
                    startEditing();
                else
                    endEditing();
            }
        });
    }


}