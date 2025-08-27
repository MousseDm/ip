import java.util.ArrayList;
import java.util.List;

public class TaskList {
    private final ArrayList<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public TaskList(List<Task> init) {
        this.tasks = new ArrayList<>(init);
    }

    public ArrayList<Task> all() {
        return tasks;
    }

    public int size() {
        return tasks.size();
    }

    public Task get(int idx1Based) {
        return tasks.get(idx1Based - 1);
    }

    public void add(Task t) {
        tasks.add(t);
    }

    public Task remove(int idx1Based) {
        return tasks.remove(idx1Based - 1);
    }
}
