package kenma;

/** Represents a simple todo task without any date/time. */
public class Todo extends Task {
    /** Creates a todo task with the given description. */
    public Todo(String description) {
        super(description, TaskType.TODO);
        assert description != null && !description.isBlank();
    }

    /** Returns the string representation of this todo. */
    @Override
    public String toString() {
        return "[" + this.type.getSymbol() + "]" + super.toString();
    }
}
