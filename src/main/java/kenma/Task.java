package kenma;

/**
 * Represents a generic task with a description and a completion status.
 * All specific task types (e.g., {@link Todo}, {@link Deadline}, {@link Event})
 * extend this base class.
 */
public class Task {
    private final String description;
    private boolean isDone;
    private final TaskType type;

    public Task(String description, TaskType type) {
        assert description != null && !description.isBlank();
        assert type != null;
        this.description = description;
        this.isDone = false;
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDone() {
        return isDone;
    }

    public TaskType getType() {
        return type;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    public void markAsDone() {
        boolean before = this.isDone;
        this.isDone = true;
        assert this.isDone && !before;
    }

    public void markAsNotDone() {
        boolean before = this.isDone;
        this.isDone = false;
        assert !this.isDone && before;
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
