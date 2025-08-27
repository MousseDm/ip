package kenma;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

/**
 * Entry point and top-level coordinator of the Kenma/Duke application.
 *
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Initialize UI, storage, and task list</li>
 * <li>Run the REPL loop to read/parse/execute commands</li>
 * <li>Persist changes to disk</li>
 * </ul>
 */
public class Kenma {
    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;

    /**
     * Creates an application instance that uses the given save file path.
     *
     * @param filePath file path used for persistence
     */
    private Kenma(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        TaskList loaded;
        try {
            loaded = new TaskList(storage.load());
        } catch (Exception e) {
            ui.showError(" Warning: failed to load previous data, starting empty.");
            loaded = new TaskList();
        }
        this.tasks = loaded;
    }

    /**
     * Runs the main REPL loop: reads commands, executes them, and persists changes.
     * Terminates when a {@code bye} command is received or on EOF.
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
                        handleOn(p.a);
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
     * Handles the {@code on yyyy-MM-dd} command: prints tasks that occur on the
     * given date.
     *
     * @param dateStr date string in {@code yyyy-MM-dd} format
     */
    private void handleOn(String dateStr) {
        System.out.println("____________________________________________________________");
        try {
            LocalDate target = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            boolean found = false;
            System.out.println(" Tasks on " + target + ":");
            for (int i = 0; i < tasks.size(); i++) {
                Task t = tasks.get(i);
                if (t instanceof Deadline) {
                    if (((Deadline) t).occursOn(target)) {
                        System.out.printf(" %d.%s%n", i + 1, t);
                        found = true;
                    }
                } else if (t instanceof Event) {
                    if (((Event) t).occursOn(target)) {
                        System.out.printf(" %d.%s%n", i + 1, t);
                        found = true;
                    }
                }
            }
            if (!found) {
                System.out.println(" No tasks on this date.");
            }
        } catch (DateTimeParseException e) {
            System.out.println(" Please provide a valid date in yyyy-MM-dd format.");
        }
        System.out.println("____________________________________________________________");
    }

    /**
     * Parses and validates a 1-based index argument against the current task list
     * size.
     *
     * @param raw  raw user input for the index
     * @param size current task list size
     * @return zero-based index
     * @throws DukeException if the input is not an integer or out of range
     */
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

    /**
     * Attempts to persist the current task list to disk.
     * Any I/O errors are intentionally ignored but the run loop continues.
     */
    private void trySave() {
        try {
            storage.save(tasks.all());
        } catch (Exception ignore) {
            // Intentionally ignored to avoid interrupting the run loop.
        }
    }

    /**
     * Program entry point. Accepts an optional file path argument.
     *
     * @param args {@code [0]} may specify the save file path; otherwise
     *             {@code data/kenma.txt} is used
     */
    public static void main(String[] args) {
        String path = (args.length > 0) ? args[0] : "data/kenma.txt";
        new Kenma(path).run();
    }
}
