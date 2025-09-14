package kenma;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Entry point and top-level coordinator of the Kenma/Duke application.
 */
public class Kenma {
    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;

    public Kenma(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        TaskList loaded;
        try {
            loaded = new TaskList(storage.load());
        } catch (Exception e) {
            loaded = new TaskList();
        }
        this.tasks = loaded;
    }

    /** GUI single-turn response. */
    public String getResponse(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        try {
            Parser.Parsed p = Parser.parse(input);
            switch (p.cmd) {
                case BYE:
                    return "Bye. Hope to see you again soon!";

                case LIST:
                    return formatList("Here are the tasks in your list:", tasks.all());

                case MARK: {
                    int idx = requireValidIndex(p.a, tasks.size());
                    tasks.get(idx).markAsDone();
                    trySave();
                    return "Nice! I've marked this task as done:\n" + tasks.get(idx);
                }

                case UNMARK: {
                    int idx = requireValidIndex(p.a, tasks.size());
                    tasks.get(idx).markAsNotDone();
                    trySave();
                    return "OK, I've marked this task as not done yet:\n" + tasks.get(idx);
                }

                case DELETE: {
                    int idx = requireValidIndex(p.a, tasks.size());
                    Task removed = tasks.remove(idx);
                    trySave();
                    return "Noted. I've removed this task:\n" + removed
                            + String.format("%nNow you have %d tasks in the list.", tasks.size());
                }

                case TODO:
                    return addTaskAndRespond(new Todo(p.a));

                case DEADLINE:
                    return addTaskAndRespond(new Deadline(p.a, p.b));

                case EVENT:
                    return addTaskAndRespond(new Event(p.a, p.b, p.c));

                case ON:
                    return tasksOnDateAsText(p.a);

                case FIND: {
                    List<Task> matches = tasks.find(p.a);
                    return formatList(String.format("Here are the matching tasks containing \"%s\":", p.a), matches);
                }

                case SORT: {
                    String mode = (p.a == null) ? "" : p.a;
                    List<Task> sorted = sortTasks(mode);
                    return formatList("Sorted tasks (" + mode + "):", sorted);
                }

                default:
                    return "";
            }
        } catch (DukeException e) {
            return "Error: " + e.getMessage();
        }
    }

    public String getGreeting() {
        String logo = "  _  __  _____ __    __  _          _          _  \n"
                + " | |/ / |  ____||  \\   |  ||  \\       /  |        / \\ \n"
                + " | ' /  |  |___  |    \\ |  ||    \\   /    |      / /\\ \\ \n"
                + " |  <  |  ___|  | |\\  \\|  ||  |\\ \\/ /|  |    / /__\\ \\\n"
                + " | . \\  |  |____ | | \\     ||  | \\   / |  |  /  _____  \\\n"
                + " |_|\\_\\|______|_|   \\__||_|   \\/   |_|/_/          \\_\\\n";
        int n = tasks.size();
        return logo + "\nHello! I'm Kenma.\nYou have " + n
                + (n == 1 ? " task" : " tasks")
                + " in your list.\nTry: todo, deadline, event, list, find. Type 'bye' to exit.";
    }

    private String tasksOnDateAsText(String dateStr) {
        StringBuilder sb = new StringBuilder();
        try {
            LocalDate target = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            boolean found = false;
            sb.append("Tasks on ").append(target).append(":\n");
            for (int i = 0; i < tasks.size(); i++) {
                Task t = tasks.get(i + 1); // TaskList#get is 1-based
                if (t instanceof Deadline && ((Deadline) t).occursOn(target)) {
                    sb.append(String.format(" %d.%s%n", i + 1, t));
                    found = true;
                } else if (t instanceof Event && ((Event) t).occursOn(target)) {
                    sb.append(String.format(" %d.%s%n", i + 1, t));
                    found = true;
                }
            }
            if (!found) {
                sb.append(" No tasks on this date.");
            }
        } catch (DateTimeParseException e) {
            sb.append(" Please provide a valid date in yyyy-MM-dd format.");
        }
        return sb.toString().trim();
    }

    private List<Task> sortTasks(String mode) {
        return tasks.all().stream()
                .sorted((a, b) -> {
                    switch (mode) {
                        case "by name": {
                            return a.getDescription().compareToIgnoreCase(b.getDescription());
                        }
                        case "by status": {
                            return Boolean.compare(a.isDone(), b.isDone());
                        }
                        case "by time": {
                            LocalDateTime ta = extractDateTime(a);
                            LocalDateTime tb = extractDateTime(b);
                            if (ta == null && tb == null) {
                                return 0;
                            }
                            if (ta == null) {
                                return 1;
                            }
                            if (tb == null) {
                                return -1;
                            }
                            return ta.compareTo(tb);
                        }
                        default: {
                            return 0;
                        }
                    }
                })
                .toList();
    }

    private LocalDateTime extractDateTime(Task t) {
        if (t instanceof Deadline) {
            Deadline d = (Deadline) t;
            if (d.getDueDateTime() != null) {
                return d.getDueDateTime();
            }
            if (d.getDueDate() != null) {
                return d.getDueDate().atStartOfDay();
            }
        }
        if (t instanceof Event) {
            Event e = (Event) t;
            if (e.getFromDateTime() != null) {
                return e.getFromDateTime();
            }
            if (e.getFromDate() != null) {
                return e.getFromDate().atStartOfDay();
            }
        }
        return null;
    }

    /** Validate 1-based index string and return it as int. */
    private int requireValidIndex(String raw, int size) throws DukeException {
        if (raw == null || raw.isBlank()) {
            throw new DukeException("Please provide an index.");
        }
        int idx;
        try {
            idx = Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            throw new DukeException("Please provide a valid integer index.");
        }
        if (idx < 1 || idx > size) {
            throw new DukeException("Index out of range. Valid range: 1.." + size + ".");
        }
        return idx;
    }

    private void trySave() {
        try {
            storage.save(tasks.all());
        } catch (Exception ignore) {
        }
    }

    private String addTaskAndRespond(Task t) {
        tasks.add(t);
        trySave();
        return "Got it. I've added this task:\n" + t
                + String.format("%nNow you have %d tasks in the list.", tasks.size());
    }

    private String formatList(String header, List<Task> list) {
        if (list.isEmpty()) {
            return header + System.lineSeparator() + "(no tasks)";
        }
        String body = IntStream.range(0, list.size())
                .mapToObj(i -> String.format("%d.%s", i + 1, list.get(i)))
                .collect(Collectors.joining(System.lineSeparator()));
        return header + System.lineSeparator() + body;
    }

    /** CLI main – optional. */
    public static void main(String[] args) {
        String path = (args.length > 0) ? args[0] : "data/kenma.txt";
        new Kenma(path).run();
    }

    /** Classic CLI run loop. */
    private void run() {
        String logo = " _  __ ______ _   _ __  __       \n"
                + "| |/ /|  ____| \\ | |  \\/  |   /\\ \n"
                + "| ' / | |__  |  \\| | \\  / |  /  \\ \n"
                + "|  <  |  __| | . ` | |\\/| | / /\\ \\\n"
                + "| . \\ | |____| |\\  | |  | |/ ____ \\\n"
                + "|_|\\_\\|______|_| \\_|_|  |_/_/    \\_\\\n";
        ui.showWelcome(logo);

        while (true) {
            String input = ui.readCommand();
            if (input == null) {
                ui.showBye();
                break;
            }
            if (input.isEmpty()) {
                continue;
            }
            try {
                Parser.Parsed p = Parser.parse(input);
                switch (p.cmd) {
                    case BYE:
                        ui.showBye();
                        return;
                    case LIST:
                        ui.showList(tasks.all());
                        break;
                    case MARK: {
                        int idx = requireValidIndex(p.a, tasks.size());
                        tasks.get(idx).markAsDone();
                        ui.showMarked(tasks.get(idx));
                        trySave();
                        break;
                    }
                    case UNMARK: {
                        int idx = requireValidIndex(p.a, tasks.size());
                        tasks.get(idx).markAsNotDone();
                        ui.showUnmarked(tasks.get(idx));
                        trySave();
                        break;
                    }
                    case DELETE: {
                        int idx = requireValidIndex(p.a, tasks.size());
                        Task removed = tasks.remove(idx);
                        ui.showDeleted(removed, tasks.size());
                        trySave();
                        break;
                    }
                    case TODO: {
                        Task t = new Todo(p.a);
                        tasks.add(t);
                        ui.showAdded(t, tasks.size());
                        trySave();
                        break;
                    }
                    case DEADLINE: {
                        Task t = new Deadline(p.a, p.b);
                        tasks.add(t);
                        ui.showAdded(t, tasks.size());
                        trySave();
                        break;
                    }
                    case EVENT: {
                        Task t = new Event(p.a, p.b, p.c);
                        tasks.add(t);
                        ui.showAdded(t, tasks.size());
                        trySave();
                        break;
                    }
                    case ON: {
                        System.out.println(tasksOnDateAsText(p.a));
                        break;
                    }
                    case FIND: {
                        ui.showFound(tasks.find(p.a), p.a);
                        break;
                    }
                    case SORT: {
                        String mode = (p.a == null) ? "" : p.a;
                        ui.showList(sortTasks(mode));
                        break;
                    }
                    default:
                }
            } catch (DukeException e) {
                ui.showError(e.getMessage());
            }
        }
    }
}
