package kenma;

import java.util.Objects;

/**
 * Represents a generic task with a description and a completion status.
 * Specific task types (e.g., {@link Todo}, {@link Deadline}, {@link Event})
 * extend this base class.
 *
 * <p>
 * <b>Duplication / equality:</b>
 * To help prevent duplicate tasks, this class defines {@link #equals(Object)}
 * and
 * {@link #hashCode()} so that two tasks are considered the same if they are of
 * the
 * same concrete type and have the same normalized description, and (optionally)
 * the same time attributes. Subclasses should override {@link #keyBy()},
 * {@link #keyStart()}, and {@link #keyEnd()} to participate in equality if they
 * carry time fields.
 * </p>
 */
public class Task {
    private final String description;
    private boolean isDone;
    private final TaskType type;

    /**
     * Creates a task.
     * 
     * @throws IllegalArgumentException if description/type are invalid
     */
    public Task(String description, TaskType type) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Task description cannot be empty.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Task type cannot be null.");
        }
        this.description = description;
        this.isDone = false;
        this.type = type;

        assert description != null && !description.isBlank();
        assert type != null;
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

    /**
     * Subclasses can override to contribute a "deadline-by" key for
     * equality/hashCode.
     * Example (in Deadline): return byDateTime.toString();
     */
    protected String keyBy() {
        return null;
    }

    /**
     * Subclasses can override to contribute a "start" key for equality/hashCode.
     * Example (in Event): return startDateTime.toString();
     */
    protected String keyStart() {
        return null;
    }

    /**
     * Subclasses can override to contribute an "end" key for equality/hashCode.
     * Example (in Event): return endDateTime.toString();
     */
    protected String keyEnd() {
        return null;
    }

    /** Normalized description used for equality: trimmed + case-insensitive. */
    private String normalizedDescription() {
        return description.trim().toLowerCase();
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Semantic equality:
     * - same concrete class (Todo vs Deadline vs Event)
     * - same normalized description
     * - same time keys (if provided by subclass)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Task)) {
            return false;
        }
        Task other = (Task) obj;

        // Must be the same concrete type (Todo vs Deadline vs Event)
        if (!this.getClass().equals(other.getClass())) {
            return false;
        }

        // Compare normalized description and any time keys
        return Objects.equals(this.normalizedDescription(), other.normalizedDescription())
                && Objects.equals(this.keyBy(), other.keyBy())
                && Objects.equals(this.keyStart(), other.keyStart())
                && Objects.equals(this.keyEnd(), other.keyEnd());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getClass(), // concrete type
                normalizedDescription(), // description (normalized)
                keyBy(), keyStart(), keyEnd() // optional time keys
        );
    }
}
