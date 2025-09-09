package kenma;

/** Represents a simple todo task without any date/time. */
public class Todo extends Task {
    public Todo(String description) {
        super(description, TaskType.TODO);
        assert description != null && !description.isBlank();
    }

    @Override
    public String toString() {
        return "[" + getType().getSymbol() + "]" + super.toString();
    }
}
