package kenma;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Handles persistence of tasks to and from a plain-text file.
 *
 * <p>File format (pipe-delimited):
 * <ul>
 *   <li>{@code T | doneFlag | description}</li>
 *   <li>{@code D | doneFlag | description | by}</li>
 *   <li>{@code E | doneFlag | description | from | to}</li>
 * </ul>
 * where {@code doneFlag} is {@code "1"} if done, otherwise {@code "0"}.
 */
public class Storage {
    private final Path file;

    /**
     * Creates a storage backed by the given file path.
     *
     * @param filePath path to the save file
     */
    public Storage(String filePath) {
        this.file = Paths.get(filePath);
    }

    /**
     * Loads tasks from disk.
     *
     * @return list of tasks (empty if file does not exist or on decoding errors)
     * @throws IOException if an I/O error occurs while reading the file
     */
    public List<Task> load() throws IOException {
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
        return tasks;
    }

    /**
     * Saves the given tasks to disk, creating parent directories if necessary.
     *
     * @param tasks tasks to persist
     * @throws IOException if an I/O error occurs while writing the file
     */
    public void save(List<Task> tasks) throws IOException {
        Files.createDirectories(file.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(file)) {
            for (Task t : tasks) {
                String s = encode(t);
                if (s != null) {
                    bw.write(s);
                    bw.newLine();
                }
            }
        }
    }

    /**
     * Decodes one line from the save file into a {@link Task}.
     * Lines that cannot be decoded safely return {@code null}.
     *
     * @param line raw line from the file
     * @return decoded task or {@code null} if invalid
     */
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
                if (isDone) {
                    t.markAsDone();
                }
                return t;
            }
            case "D": {
                if (p.length < 4) {
                    return null;
                }
                Deadline d = new Deadline(desc, p[3]);
                if (isDone) {
                    d.markAsDone();
                }
                return d;
            }
            case "E": {
                if (p.length == 4) {
                    Event e = new Event(desc, p[3], "");
                    if (isDone) {
                        e.markAsDone();
                    }
                    return e;
                } else if (p.length >= 5) {
                    Event e = new Event(desc, p[3], p[4]);
                    if (isDone) {
                        e.markAsDone();
                    }
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

    /**
     * Encodes a {@link Task} into one save-file line.
     * Returns {@code null} if the task type is not recognized.
     *
     * @param t task to encode
     * @return encoded line or {@code null} if unsupported type
     */
    private String encode(Task t) {
        String flag = t.isDone ? "1" : "0";
        if (t instanceof Todo) {
            return "T | " + flag + " | " + t.description;
        } else if (t instanceof Deadline) {
            return "D | " + flag + " | " + t.description + " | " + ((Deadline) t).by;
        } else if (t instanceof Event) {
            Event e = (Event) t;
            if (e.to == null || e.to.isEmpty()) {
                return "E | " + flag + " | " + e.description + " | " + e.from;
            }
            return "E | " + flag + " | " + e.description + " | " + e.from + " | " + e.to;
        } else {
            return null;
        }
    }
}
