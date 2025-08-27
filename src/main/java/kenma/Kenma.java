package kenma;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Kenma {
    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;

    private Kenma(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        TaskList loaded;
        try { loaded = new TaskList(storage.load()); }
        catch (Exception e) { ui.showError(" Warning: failed to load previous data, starting empty."); loaded = new TaskList(); }
        this.tasks = loaded;
    }

    private void run() {
        String logo =
                " _  __ ______ _   _ __  __       \n"
                        + "| |/ /|  ____| \\ | |  \\/  |   /\\ \n"
                        + "| ' / | |__  |  \\| | \\  / |  /  \\ \n"
                        + "|  <  |  __| | . ` | |\\/| | / /\\ \\\n"
                        + "| . \\ | |____| |\\  | |  | |/ ____ \\\n"
                        + "|_|\\_\\|______|_| \\_|_|  |_/_/    \\_\\\n";
        ui.showWelcome(logo);

        while (true) {
            String input = ui.readCommand();
            if (input == null) { ui.showBye(); break; }
            if (input.isEmpty()) continue;
            try {
                Parser.Parsed p = Parser.parse(input);
                switch (p.cmd) {
                    case BYE:
                        ui.showBye(); return;
                    case LIST:
                        ui.showList(tasks.all()); break;
                    case MARK: {
                        int idx = requireValidIndex(p.a, tasks.size());
                        tasks.get(idx).markAsDone();
                        ui.showMarked(tasks.get(idx));
                        trySave(); break;
                    }
                    case UNMARK: {
                        int idx = requireValidIndex(p.a, tasks.size());
                        tasks.get(idx).markAsNotDone();
                        ui.showUnmarked(tasks.get(idx));
                        trySave(); break;
                    }
                    case DELETE: {
                        int idx = requireValidIndex(p.a, tasks.size());
                        Task removed = tasks.remove(idx);
                        ui.showDeleted(removed, tasks.size());
                        trySave(); break;
                    }
                    case TODO: {
                        Task t = new Todo(p.a);
                        tasks.add(t);
                        ui.showAdded(t, tasks.size());
                        trySave(); break;
                    }
                    case DEADLINE: {
                        Task t = new Deadline(p.a, p.b);
                        tasks.add(t);
                        ui.showAdded(t, tasks.size());
                        trySave(); break;
                    }
                    case EVENT: {
                        Task t = new Event(p.a, p.b, p.c);
                        tasks.add(t);
                        ui.showAdded(t, tasks.size());
                        trySave(); break;
                    }
                    case ON: {
                        handleOn(p.a); break;
                    }
                }
            } catch (DukeException e) {
                ui.showError(e.getMessage());
            }
        }
    }

    private void handleOn(String dateStr) {
        System.out.println("____________________________________________________________");
        try {
            LocalDate target = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            boolean found = false;
            System.out.println(" Tasks on " + target + ":");
            for (int i = 0; i < tasks.size(); i++) {
                Task t = tasks.get(i);
                if (t instanceof Deadline) {
                    if (((Deadline) t).occursOn(target)) { System.out.printf(" %d.%s%n", i + 1, t); found = true; }
                } else if (t instanceof Event) {
                    if (((Event) t).occursOn(target)) { System.out.printf(" %d.%s%n", i + 1, t); found = true; }
                }
            }
            if (!found) System.out.println(" No tasks on this date.");
        } catch (DateTimeParseException e) {
            System.out.println(" Please provide a valid date in yyyy-MM-dd format.");
        }
        System.out.println("____________________________________________________________");
    }

    private int requireValidIndex(String raw, int size) throws DukeException {
        int idx;
        try { idx = Integer.parseInt(raw.trim()); }
        catch (NumberFormatException e) { throw new DukeException("Please provide a valid integer index."); }
        if (idx < 1 || idx > size) throw new DukeException("Index out of range. Valid range: 1.." + size + ".");
        return idx;
    }

    private void trySave() {
        try { storage.save(tasks.all()); } catch (Exception ignore) {}
    }

    public static void main(String[] args) {
        new Kenma("../data/kenma.txt").run();
    }
}
