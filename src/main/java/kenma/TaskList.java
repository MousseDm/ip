package kenma;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a mutable list of tasks, backed by an {@link ArrayList}.
 * Provides operations to add, remove, access, and query tasks.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
        assert this.tasks != null;
    }

    /**
     * Creates a task list initialized with the given tasks.
     *
     * @param init initial tasks
     */
    public TaskList(List<Task> init) {
        assert init != null;
        this.tasks = new ArrayList<>(init);
        assert this.tasks != null;
    }

    /** Returns the backing list of tasks. */
    public ArrayList<Task> all() {
        assert tasks != null;
        return tasks;
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        assert tasks != null;
        assert tasks.size() >= 0;
        return tasks.size();
    }

    /** Returns the task at the given 1-based index. */
    public Task get(int idx1Based) {
        assert idx1Based > 0 && idx1Based <= tasks.size();
        return tasks.get(idx1Based - 1);
    }

    /** Adds a new task to the end of the list. */
    public void add(Task t) {
        assert t != null;
        int before = tasks.size();
        tasks.add(t);
        assert tasks.size() == before + 1;
    }

    /** Removes and returns the task at the given 1-based index. */
    public Task remove(int idx1Based) {
        assert idx1Based > 0 && idx1Based <= tasks.size();
        int before = tasks.size();
        Task removed = tasks.remove(idx1Based - 1);
        assert tasks.size() == before - 1;
        return removed;
    }

    /**
     * Returns tasks whose description contains the given keyword
     * (case-insensitive).
     */
    public ArrayList<Task> find(String keyword) {
        assert keyword != null && !keyword.isBlank();
        String needle = keyword.toLowerCase();
        ArrayList<Task> out = new ArrayList<>();
        for (Task t : tasks) {
            if (t.description != null && t.description.toLowerCase().contains(needle)) {
                out.add(t);
            }
        }
        return out;
    }
}
