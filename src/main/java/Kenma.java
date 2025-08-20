import java.util.Scanner;
import java.util.ArrayList;


public class Kenma {
    private static final String LINE =
            "____________________________________________________________";

    public static void main(String[] args) {
        String logo =
                " _  __ ______ _   _ __  __       \n"
                        + "| |/ /|  ____| \\ | |  \\/  |   /\\ \n"
                        + "| ' / | |__  |  \\| | \\  / |  /  \\ \n"
                        + "|  <  |  __| | . ` | |\\/| | / /\\ \\\n"
                        + "| . \\ | |____| |\\  | |  | |/ ____ \\\n"
                        + "|_|\\_\\|______|_| \\_|_|  |_/_/    \\_\\\n";

        System.out.println(logo);
        System.out.println(LINE);
        System.out.println(" Hello! I'm Kenma");
        System.out.println(" What can I do for you?");
        System.out.println(LINE);

        ArrayList<Task> tasks = new ArrayList<>();

        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                String input = sc.nextLine().trim();
                if (input.equalsIgnoreCase("bye")) {
                    System.out.println(LINE);
                    System.out.println(" Bye. Hope to see you again soon!");
                    System.out.println(LINE);
                    break;
                } else if (input.equalsIgnoreCase("list")) {
                    System.out.println(LINE);
                    if (tasks.isEmpty()) {
                        System.out.println(" No tasks in your list.");
                    } else {
                        System.out.println(" Here are the tasks in your list:");
                        for (int i = 0; i < tasks.size(); i++) {
                            System.out.printf(" %d.%s%n", i + 1, tasks.get(i));
                        }
                    }
                    System.out.println(LINE);
                } else if (input.startsWith("mark ")) {
                    int idx = requireValidIndex(input.substring(5), tasks.size());
                    tasks.get(idx - 1).markAsDone();
                    System.out.println(LINE);
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   " + tasks.get(idx - 1));
                    System.out.println(LINE);
                } else if (input.startsWith("unmark ")) {
                    int idx = requireValidIndex(input.substring(7), tasks.size());
                    tasks.get(idx - 1).markAsNotDone();
                    System.out.println(LINE);
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + tasks.get(idx - 1));
                    System.out.println(LINE);
                } else if (input.startsWith("delete ")) {
                    int idx = requireValidIndex(input.substring(7), tasks.size());
                    Task removed = tasks.remove(idx - 1);
                    System.out.println(LINE);
                    System.out.println(" Noted. I've removed this task:");
                    System.out.println("   " + removed);
                    System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                    System.out.println(LINE);
                } else if (input.startsWith("todo")) {
                    String desc = input.length() > 4 ? input.substring(5).trim() : "";
                    if (desc.isEmpty()) throw new DukeException("The description of a todo cannot be empty.");
                    tasks.add(new Todo(desc));
                    added(tasks.get(tasks.size() - 1), tasks.size());
                } else if (input.startsWith("deadline")) {
                    String body = input.substring(9).trim();
                    int idx = body.indexOf("/by");
                    if (idx == -1) {
                        throw new DukeException("Missing '/by'. Usage: deadline <desc> /by <when>");
                    }
                    String desc = body.substring(0, idx).trim();
                    String by = body.substring(idx + 3).trim();
                    if (desc.isEmpty() || by.isEmpty()) {
                        throw new DukeException("Both description and '/by <when>' are required.");
                    }
                    tasks.add(new Deadline(desc, by));
                    added(tasks.get(tasks.size() - 1), tasks.size());
                } else if (input.startsWith("event")) {
                    String body = input.length() > 5 ? input.substring(6).trim() : "";
                    String[] parts = body.split("/from|/to");
                    if (parts.length < 3) throw new DukeException("Event must have description, /from and /to.");
                    String desc = parts[0].trim();
                    String from = parts[1].trim();
                    String to = parts[2].trim();
                    if (desc.isEmpty() || from.isEmpty() || to.isEmpty()) throw new DukeException("Event requires description, from and to.");
                    tasks.add(new Event(desc, from, to));
                    added(tasks.get(tasks.size() - 1), tasks.size());
                } else {
                    throw new DukeException("I'm sorry, but I don't know what that means :-(");
                }
            } catch (DukeException e) {
                System.out.println(LINE);
                System.out.println(" " + e.getMessage());
                System.out.println(LINE);
            }
        }
    }

    private static int requireValidIndex(String raw, int size) throws DukeException {
        int idx;
        try { idx = Integer.parseInt(raw.trim()); }
        catch (NumberFormatException e) { throw new DukeException("Please provide a valid integer index."); }
        if (idx < 1 || idx > size) throw new DukeException("Index out of range. Valid range: 1.." + size + ".");
        return idx;
    }

    private static int parseIndex(String s) {
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return -1; }
    }

    private static void added(Task t, int count) {
        System.out.println(LINE);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + t);
        System.out.println(" Now you have " + count + " tasks in the list.");
        System.out.println(LINE);
    }
}
