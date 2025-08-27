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
    }

    /**
     * Creates a task list initialized with the given tasks.
     *
     * @param init initial tasks
     */
    public TaskList(List<Task> init) {
        this.tasks = new ArrayList<>(init);
    }

    /**
     * Returns the backing list of tasks.
     *
     * @return list of all tasks
     */
    public ArrayList<Task> all() {
        return tasks;
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at the given 1-based index.
     *
     * @param idx1Based index starting from 1
     * @return the task at that position
     */
    public Task get(int idx1Based) {
        return tasks.get(idx1Based - 1);
    }

    /**
     * Adds a new task to the end of the list.
     *
     * @param t task to add
     */
    public void add(Task t) {
        tasks.add(t);
    }

    /**
     * Removes and returns the task at the given 1-based index.
     *
     * @param idx1Based index starting from 1
     * @return removed task
     */
    public Task remove(int idx1Based) {
        return tasks.remove(idx1Based - 1);
    }

    /**
     * Returns tasks whose description contains the given keyword
     * (case-insensitive).
     *
     * @param keyword keyword to search
     * @return matching tasks in encounter order
     */
    public ArrayList<Task> find(String keyword) {
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
