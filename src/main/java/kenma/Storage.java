package kenma;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Handles persistence of tasks to and from a plain-text file. */
public class Storage {
    private final Path file;

    public Storage(String filePath) {
        assert filePath != null && !filePath.isBlank();
        this.file = Paths.get(filePath);
    }

    public List<Task> load() throws IOException {
        assert file != null;
        if (!Files.exists(file)) {
            return new ArrayList<>();
        }
        List<String> lines = Files.readAllLines(file);
        List<Task> tasks = new ArrayList<>();
        for (String line : lines) {
            Task t = decode(line);
            if (t != null) {
                tasks.add(t);
            }
        }
        assert tasks != null;
        return tasks;
    }

    public void save(List<Task> tasks) throws IOException {
        assert tasks != null;
        Files.createDirectories(file.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(file)) {
            for (Task t : tasks) {
                String s = encode(t);
                assert s != null;
                bw.write(s);
                bw.newLine();
            }
        }
        assert Files.exists(file);
    }

    private Task decode(String line) {
        String[] p = Arrays.stream(line.split("\\|"))
                .map(String::trim)
                .toArray(String[]::new);
        if (p.length < 3) {
            return null;
        }
        String type = p[0];
        boolean isDone = "1".equals(p[1]);
        String desc = p[2];
        try {
            switch (type) {
                case "T": {
                    Todo t = new Todo(desc);
                    if (isDone)
                        t.markAsDone();
                    return t;
                }
                case "D": {
                    if (p.length < 4)
                        return null;
                    Deadline d = new Deadline(desc, p[3]);
                    if (isDone)
                        d.markAsDone();
                    return d;
                }
                case "E": {
                    if (p.length == 4) {
                        Event e = new Event(desc, p[3], "");
                        if (isDone)
                            e.markAsDone();
                        return e;
                    } else if (p.length >= 5) {
                        Event e = new Event(desc, p[3], p[4]);
                        if (isDone)
                            e.markAsDone();
                        return e;
                    } else {
                        return null;
                    }
                }
                default:
                    return null;
            }
        } catch (Exception ex) {
            return null;
        }
    }

    private String encode(Task t) {
        String flag = t.isDone() ? "1" : "0";
        if (t instanceof Todo) {
            return "T | " + flag + " | " + t.getDescription();
        } else if (t instanceof Deadline) {
            Deadline d = (Deadline) t;
            return "D | " + flag + " | " + d.getDescription() + " | " + d.getBy();
        } else if (t instanceof Event) {
            Event e = (Event) t;
            if (e.getTo() == null || e.getTo().isEmpty()) {
                return "E | " + flag + " | " + e.getDescription() + " | " + e.getFrom();
            }
            return "E | " + flag + " | " + e.getDescription() + " | " + e.getFrom() + " | " + e.getTo();
        } else {
            return null;
        }
    }
}
