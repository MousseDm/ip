package kenma;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

/**
 * Entry point and top-level coordinator of the Kenma/Duke application.
 *
 * Responsibilities:
 * - Initialize UI, storage, and task list
 * - Run the REPL loop to read/parse/execute commands (CLI)
 * - Provide a single-turn response method for GUI
 * - Persist changes to disk
 */
public class Kenma {
    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;

    /**
     * Creates an application instance that uses the given save file path.
     *
     * NOTE: must be public so the JavaFX app can create and reuse a single engine.
     */
    public Kenma(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        TaskList loaded;
        try {
            loaded = new TaskList(storage.load());
        } catch (Exception e) {
            // For GUI mode we avoid printing; UI can show warnings in CLI mode
            loaded = new TaskList();
        }
        this.tasks = loaded;
    }

    /* ===================== GUI ENTRY (SINGLE-TURN) ====================== */

    /**
     * Returns a single response string for a single user input.
     * Used by the JavaFX GUI (no printing; everything returned as text).
     */
    public String getResponse(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        try {
            Parser.Parsed p = Parser.parse(input);
            StringBuilder out = new StringBuilder();
            switch (p.cmd) {
                case BYE:
                    out.append("Bye. Hope to see you again soon!");
                    break;

                case LIST: {
                    ArrayList<Task> all = tasks.all();
                    out.append("Here are the tasks in your list:\n");
                    for (int i = 0; i < all.size(); i++) {
                        out.append(String.format("%d.%s%n", i + 1, all.get(i)));
                    }
                    break;
                }

                case MARK: {
                    int idx = requireValidIndex(p.a, tasks.size());
                    tasks.get(idx).markAsDone();
                    out.append("Nice! I've marked this task as done:\n")
                            .append(tasks.get(idx));
                    trySave();
                    break;
                }

                case UNMARK: {
                    int idx = requireValidIndex(p.a, tasks.size());
                    tasks.get(idx).markAsNotDone();
                    out.append("OK, I've marked this task as not done yet:\n")
                            .append(tasks.get(idx));
                    trySave();
                    break;
                }

                case DELETE: {
                    int idx = requireValidIndex(p.a, tasks.size());
                    Task removed = tasks.remove(idx);
                    out.append("Noted. I've removed this task:\n")
                            .append(removed)
                            .append(String.format("%nNow you have %d tasks in the list.", tasks.size()));
                    trySave();
                    break;
                }

                case TODO: {
                    Task t = new Todo(p.a);
                    tasks.add(t);
                    out.append("Got it. I've added this task:\n")
                            .append(t)
                            .append(String.format("%nNow you have %d tasks in the list.", tasks.size()));
                    trySave();
                    break;
                }

                case DEADLINE: {
                    Task t = new Deadline(p.a, p.b);
                    tasks.add(t);
                    out.append("Got it. I've added this task:\n")
                            .append(t)
                            .append(String.format("%nNow you have %d tasks in the list.", tasks.size()));
                    trySave();
                    break;
                }

                case EVENT: {
                    Task t = new Event(p.a, p.b, p.c);
                    tasks.add(t);
                    out.append("Got it. I've added this task:\n")
                            .append(t)
                            .append(String.format("%nNow you have %d tasks in the list.", tasks.size()));
                    trySave();
                    break;
                }

                case ON: {
                    out.append(tasksOnDateAsText(p.a));
                    break;
                }

                case FIND: {
                    ArrayList<Task> matches = tasks.find(p.a);
                    out.append(String.format("Here are the matching tasks containing \"%s\":%n", p.a));
                    for (int i = 0; i < matches.size(); i++) {
                        out.append(String.format("%d.%s%n", i + 1, matches.get(i)));
                    }
                    break;
                }

                default:
                    // unreachable
            }
            return out.toString().trim();

        } catch (DukeException e) {
            return e.getMessage();
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
                Task t = tasks.get(i);
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

    private int requireValidIndex(String raw, int size) throws DukeException {
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
            // Ignore I/O errors to keep the app responsive
        }
    }

    /* ===================== CLI ENTRY (OPTIONAL) ====================== */

    /**
     * Classic CLI run loop (kept for compatibility). Not used by JavaFX.
     */
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
            if (input == null) { // EOF
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
                        ArrayList<Task> matches = tasks.find(p.a);
                        ui.showFound(matches, p.a);
                        break;
                    }
                    default:
                        // unreachable
                }
            } catch (DukeException e) {
                ui.showError(e.getMessage());
            }
        }
    }

    /**
     * CLI main – optional. JavaFX uses Launcher to Main.
     */
    public static void main(String[] args) {
        String path = (args.length > 0) ? args[0] : "data/kenma.txt";
        new Kenma(path).run();
    }
}
