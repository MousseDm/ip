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
        assert this.tasks != null;
    }

    public TaskList(List<Task> init) {
        assert init != null;
        this.tasks = new ArrayList<>(init);
        assert this.tasks != null;
    }

    /** Read-only view to prevent representation exposure. */
    public List<Task> all() {
        return Collections.unmodifiableList(tasks);
    }

    public int size() {
        return tasks.size();
    }

    public Task get(int idx1Based) {
        assert idx1Based > 0 && idx1Based <= tasks.size();
        return tasks.get(idx1Based - 1);
    }

    public void add(Task t) {
        assert t != null;
        tasks.add(t);
    }

    public Task remove(int idx1Based) {
        assert idx1Based > 0 && idx1Based <= tasks.size();
        return tasks.remove(idx1Based - 1);
    }

    public List<Task> find(String keyword) {
        assert keyword != null && !keyword.isBlank();
        String needle = keyword.toLowerCase();
        ArrayList<Task> out = new ArrayList<>();
        for (Task t : tasks) {
            String d = t.getDescription();
            if (d != null && d.toLowerCase().contains(needle)) {
                out.add(t);
            }
        }
        return out;
    }
}
