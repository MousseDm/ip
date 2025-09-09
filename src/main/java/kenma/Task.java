package kenma;

/**
 * Represents a generic task with a description and a completion status.
 * All specific task types (e.g., {@link Todo}, {@link Deadline}, {@link Event})
 * extend this base class.
 */
public class Task {
    /** Description of the task. */
    protected String description;
    /** Whether the task is marked as done. */
    protected boolean isDone;
    /** Type of the task (TODO/DEADLINE/EVENT). */
    protected TaskType type;

    /**
     * Creates a new task with the given description and type.
     *
     * @param description description of the task
     * @param type        kind of the task
     */
    public Task(String description, TaskType type) {
        assert description != null && !description.isBlank();
        assert type != null;
        this.description = description;
        this.isDone = false;
        this.type = type;
    }

    /** Returns the visual status icon: "X" if done, or space if not. */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /** Marks this task as done. */
    public void markAsDone() {
        boolean before = this.isDone;
        this.isDone = true;
        assert this.isDone && !before;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        boolean before = this.isDone;
        this.isDone = false;
        assert !this.isDone && before;
    }

    /** Returns the string representation of this task. */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
