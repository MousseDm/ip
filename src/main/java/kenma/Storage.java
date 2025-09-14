package kenma;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Handles persistence of tasks to and from a plain-text file. */
public class Storage {
    private final Path file;

    public Storage(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("File path cannot be empty.");
        }
        this.file = Paths.get(filePath);
    }

    public List<Task> load() {
        try {
            if (!Files.exists(file)) {
                Path parent = file.getParent();
                if (parent != null) {
                    Files.createDirectories(parent);
                }
                Files.createFile(file);
                return new ArrayList<>();
            }
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            List<Task> tasks = new ArrayList<>();
            int lineNo = 0;
            for (String line : lines) {
                lineNo++;
                if (line == null || line.isBlank()) {
                    continue;
                }
                try {
                    Task t = decode(line);
                    if (t != null) {
                        tasks.add(t);
                    }
                } catch (Exception ex) {
                    System.err.println("[WARN] Ignore corrupt line " + lineNo + ": " + ex.getMessage());
                }
            }
            return tasks;
        } catch (AccessDeniedException ade) {
            throw new DukeException("Access denied to data file: " + file);
        } catch (Exception e) {
            throw new DukeException("Failed to load data from: " + file + " (" + e.getMessage() + ")");
        }
    }

    public void save(List<Task> tasks) {
        if (tasks == null) {
            throw new IllegalArgumentException("Tasks cannot be null.");
        }
        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
            try (BufferedWriter bw = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
                for (Task t : tasks) {
                    String s = encode(t);
                    if (s != null) {
                        bw.write(s);
                        bw.newLine();
                    }
                }
            }
            try {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ignore) {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (AccessDeniedException ade) {
            throw new DukeException("Access denied when saving to: " + file);
        } catch (Exception e) {
            throw new DukeException("Failed to save to: " + file + " (" + e.getMessage() + ")");
        }
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
                    if (p.length >= 5) {
                        Event e = new Event(desc, p[3], p[4]);
                        if (isDone) {
                            e.markAsDone();
                        }
                        return e;
                    } else if (p.length == 4) {
                        return null;
                    } else {
                        return null;
                    }
                }
                default: {
                    return null;
                }
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
            return "E | " + flag + " | " + e.getDescription() + " | " + e.getFrom() + " | " + e.getTo();
        } else {
            return null;
        }
    }
}
