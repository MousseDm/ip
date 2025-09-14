package kenma;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mutable list of tasks backed by an ArrayList.
 * Exposes read-only view to callers to prevent external mutation.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public TaskList(List<Task> init) {
        if (init == null) {
            throw new IllegalArgumentException("Initial list cannot be null.");
        }
        this.tasks = new ArrayList<>(init);
    }

    /** Read-only view to prevent representation exposure. */
    public List<Task> all() {
        return Collections.unmodifiableList(tasks);
    }

    public int size() {
        return tasks.size();
    }

    /** 1-based index access with error handling. */
    public Task get(int idx1Based) {
        ensureIndex(idx1Based);
        return tasks.get(idx1Based - 1);
    }

    /** Prevent duplicate tasks (semantic equality). */
    public void add(Task t) {
        if (t == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }
        if (tasks.contains(t)) {
            throw new DukeException("Duplicate task: " + t);
        }
        tasks.add(t);
    }

    public Task remove(int idx1Based) {
        ensureIndex(idx1Based);
        return tasks.remove(idx1Based - 1);
    }

    public List<Task> find(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new DukeException("Keyword for find cannot be empty.");
        }
        String needle = keyword.toLowerCase();
        return tasks.stream()
                .filter(t -> {
                    String d = t.getDescription();
                    return d != null && d.toLowerCase().contains(needle);
                })
                .toList();
    }

    private void ensureIndex(int idx1Based) {
        if (idx1Based <= 0 || idx1Based > tasks.size()) {
            throw new DukeException("Index out of range. Valid range: 1.." + tasks.size() + ".");
        }
    }
}
