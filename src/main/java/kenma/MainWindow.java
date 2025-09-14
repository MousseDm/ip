package kenma;

import java.util.function.Function;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;

/**
 * Controller for MainWindow.fxml.
 * It delegates user input to a responder function and displays both sides.
 */
public class MainWindow {

    @FXML
    private ListView<DialogBox> dialogContainer; // 改：ListView<String> -> ListView<DialogBox>
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    /** A function that takes user input and returns the bot's reply. */
    private Function<String, String> responder;

    private final Image userImage = loadImageOrNull("/images/DaUser.png"); // or "/images/User.png"
    private final Image botImage = loadImageOrNull("/images/DaDuke.png"); // or "/images/Duke.png"

    private static Image loadImageOrNull(String path) {
        try {
            var is = MainWindow.class.getResourceAsStream(path);
            return is == null ? null : new Image(is);
        } catch (Exception ignore) {
            return null;
        }
    }

    /** Called by Main after FXML load to wire the chatbot core. */
    public void setResponder(Function<String, String> responder) {
        this.responder = responder;
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
                setGraphic(empty || item == null ? null : item);
            }
        });
        dialogContainer.setPlaceholder(new Label(""));

        dialogContainer.getItems().addListener(
                (javafx.collections.ListChangeListener<DialogBox>) c -> dialogContainer
                        .scrollTo(dialogContainer.getItems().size() - 1));
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
            if (looksLikeError(reply)) {
                dialogContainer.getItems().add(DialogBox.getErrorDialog(reply, botImage));
            } else {
                dialogContainer.getItems().add(DialogBox.getDukeDialog(reply, botImage));
            }
        } catch (Exception e) {
            String msg = (e.getMessage() == null || e.getMessage().isBlank())
                    ? "Unknown error."
                    : e.getMessage();
            dialogContainer.getItems().add(DialogBox.getErrorDialog("Error: " + msg, botImage));
        } finally {
            userInput.clear();
        }

        if ("bye".equalsIgnoreCase(input.trim())) {
            sendButton.getScene().getWindow().hide();
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
