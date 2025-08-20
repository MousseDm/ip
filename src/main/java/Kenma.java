import java.util.Scanner;

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

        Task[] tasks = new Task[100];
        int size = 0;

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
                    if (size == 0) {
                        System.out.println(" No tasks in your list.");
                    } else {
                        System.out.println(" Here are the tasks in your list:");
                        for (int i = 0; i < size; i++) {
                            System.out.printf(" %d.%s%n", i + 1, tasks[i]);
                        }
                    }
                    System.out.println(LINE);
                } else if (input.startsWith("mark ")) {
                    int idx = parseIndex(input.substring(5));
                    if (!valid(idx, size)) throw new DukeException("Invalid task number to mark.");
                    tasks[idx - 1].markAsDone();
                    System.out.println(LINE);
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   " + tasks[idx - 1]);
                    System.out.println(LINE);
                } else if (input.startsWith("unmark ")) {
                    int idx = parseIndex(input.substring(7));
                    if (!valid(idx, size)) throw new DukeException("Invalid task number to unmark.");
                    tasks[idx - 1].markAsNotDone();
                    System.out.println(LINE);
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + tasks[idx - 1]);
                    System.out.println(LINE);
                } else if (input.startsWith("todo")) {
                    String desc = input.length() > 4 ? input.substring(5).trim() : "";
                    if (desc.isEmpty()) throw new DukeException("The description of a todo cannot be empty.");
                    tasks[size++] = new Todo(desc);
                    added(tasks[size - 1], size);
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
                    tasks[size++] = new Deadline(desc, by);
                    added(tasks[size - 1], size);
                } else if (input.startsWith("event")) {
                    String body = input.length() > 5 ? input.substring(6).trim() : "";
                    String[] parts = body.split("/from|/to");
                    if (parts.length < 3) throw new DukeException("Event must have description, /from and /to.");
                    String desc = parts[0].trim();
                    String from = parts[1].trim();
                    String to = parts[2].trim();
                    if (desc.isEmpty() || from.isEmpty() || to.isEmpty()) throw new DukeException("Event requires description, from and to.");
                    tasks[size++] = new Event(desc, from, to);
                    added(tasks[size - 1], size);
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

    private static boolean valid(int idx, int size) {
        return idx >= 1 && idx <= size;
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
