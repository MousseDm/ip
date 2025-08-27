package kenma;

/**
 * Enumeration of task kinds. Each task type has a symbol
 * used in string serialization (e.g., save file, toString()).
 */
public enum TaskType {
    /** Simple todo task. */
    TODO("T"),
    /** Deadline task with a due date. */
    DEADLINE("D"),
    /** Event task with a start and end. */
    EVENT("E");

    private final String symbol;

    TaskType(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the single-letter symbol of this type.
     *
     * @return symbol string
     */
    public String getSymbol() {
        return symbol;
    }
}
