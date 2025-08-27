package kenma;

/**
 * Represents a simple todo task without any date/time.
 */
public class Todo extends Task {
    /**
     * Creates a todo task with the given description.
     *
     * @param description description of the todo
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }

    /**
     * Returns the string representation of this todo,
     * including its type symbol.
     *
     * @return string form of this todo
     */
    @Override
    public String toString() {
        return "[" + this.type.getSymbol() + "]" + super.toString();
    }
}
