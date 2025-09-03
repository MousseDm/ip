package kenma;

import java.util.function.Function;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * Controller for MainWindow.fxml.
 * It delegates user input to a responder function and displays both sides.
 */
public class MainWindow {

    @FXML
    private ListView<String> dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    /** A function that takes user input and returns the bot's reply. */
    private Function<String, String> responder;

    /** Called by Main after FXML load to wire the chatbot core. */
    public void setResponder(Function<String, String> responder) {
        this.responder = responder;
    }

    public void showGreeting(String text) {
        dialogContainer.getItems().add("Kenma:\n" + text);
        userInput.requestFocus();
    }

    @FXML
    private void initialize() {
        // Pressing Enter triggers send
        userInput.setOnAction(e -> handleUserInput());

        // Auto-scroll to bottom on new items
        dialogContainer.getItems().addListener((javafx.collections.ListChangeListener<String>) c -> {
            dialogContainer.scrollTo(dialogContainer.getItems().size() - 1);
        });
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input == null || input.isBlank()) {
            return;
        }

        // Show user input
        dialogContainer.getItems().add("You: " + input);

        // Get bot reply
        String reply = (responder == null) ? "(engine not wired)" : responder.apply(input);
        dialogContainer.getItems().add("Kenma: " + reply);

        userInput.clear();

        // Keep 'bye' command: close window on "bye"
        if ("bye".equalsIgnoreCase(input.trim())) {
            sendButton.getScene().getWindow().hide();
        }
    }
}
