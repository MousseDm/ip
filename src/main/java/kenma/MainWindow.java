package kenma;

import java.util.function.Function;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

public class MainWindow {

    @FXML
    private Label titleLabel;

    @FXML
    private ListView<DialogBox> dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private Function<String, String> responder;

    private final Image userImage = loadImageOrNull("/images/DaUser.png");
    private final Image botImage = loadImageOrNull("/images/DaDuke.png");

    private static Image loadImageOrNull(String path) {
        try {
            var is = MainWindow.class.getResourceAsStream(path);
            return is == null ? null : new Image(is);
        } catch (Exception e) {
            return null;
        }
    }

    public void setResponder(Function<String, String> responder) {
        this.responder = responder;
    }

    public void setTitle(String title) {
        if (titleLabel != null) {
            titleLabel.setText(title);
        }
    }

    public void addThemeStylesheet(String cssClasspath) {
        if (cssClasspath == null || cssClasspath.isBlank()) {
            return;
        }
        sendButton.sceneProperty().addListener((obs, oldScene, scene) -> {
            if (scene != null) {
                scene.getStylesheets().add(cssClasspath);
            }
        });
    }

    public void showGreeting(String text) {
        dialogContainer.getItems().add(DialogBox.getDukeDialog(text, botImage));
        userInput.requestFocus();
    }

    @FXML
    private void initialize() {
        userInput.setOnAction(e -> handleUserInput());

        dialogContainer.setCellFactory(lv -> new ListCell<DialogBox>() {
            @Override
            protected void updateItem(DialogBox item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                setGraphic((empty || item == null) ? null : item);
            }
        });
        dialogContainer.setPlaceholder(new Label(""));

        dialogContainer.getItems().addListener((ListChangeListener<DialogBox>) c -> {
            dialogContainer.scrollTo(dialogContainer.getItems().size() - 1);
        });
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input == null || input.isBlank()) {
            return;
        }

        dialogContainer.getItems().add(DialogBox.getUserDialog(input, userImage));

        try {
            String reply = (responder == null) ? "(engine not wired)" : responder.apply(input);
            // Engine now prefixes "Error: " for user-friendly errors; still guard here.
            if (looksLikeError(reply)) {
                dialogContainer.getItems().add(DialogBox.getErrorDialog(reply, botImage));
            } else {
                dialogContainer.getItems().add(DialogBox.getDukeDialog(reply, botImage));
            }
        } catch (DukeException de) {
            dialogContainer.getItems().add(DialogBox.getErrorDialog("Error: " + de.getMessage(), botImage));
        } catch (Exception e) {
            String msg = (e.getMessage() == null || e.getMessage().isBlank())
                    ? "Unknown error."
                    : e.getMessage();
            dialogContainer.getItems().add(DialogBox.getErrorDialog("Error: " + msg, botImage));
        } finally {
            userInput.clear();
        }

        if ("bye".equalsIgnoreCase(input.trim())) {
            var scene = sendButton.getScene();
            if (scene != null && scene.getWindow() != null) {
                scene.getWindow().hide();
            }
        }
    }

    private boolean looksLikeError(String s) {
        if (s == null) {
            return false;
        }
        String t = s.trim();
        return t.startsWith("Error:")
                || t.startsWith("ERROR")
                || t.startsWith("[ERROR]");
    }
}
