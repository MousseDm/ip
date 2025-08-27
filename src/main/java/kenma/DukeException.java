package kenma;

/**
 * Application-specific exception used to signal user, I/O, or parsing errors
 * within the Kenma application.
 */
public class DukeException extends Exception {
    /**
     * Creates a {@code DukeException} with the given message.
     *
     * @param message human-readable error message
     */
    public DukeException(String message) {
        super(message);
    }
}
