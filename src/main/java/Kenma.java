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
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("bye")) {
                System.out.println(LINE);
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(LINE);
                break;
            } else if (input.equalsIgnoreCase("list")) {
                System.out.println(LINE);
                System.out.println(" Here are the tasks in your list:");
                for (int i = 0; i < size; i++) {
                    System.out.printf(" %d.%s%n", i + 1, tasks[i]);
                }
                System.out.println(LINE);
            } else if (input.startsWith("mark ")) {
                int idx = parseIndex(input.substring(5));
                if (valid(idx, size)) {
                    tasks[idx - 1].markAsDone();
                    System.out.println(LINE);
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   " + tasks[idx - 1]);
                    System.out.println(LINE);
                }
            } else if (input.startsWith("unmark ")) {
                int idx = parseIndex(input.substring(7));
                if (valid(idx, size)) {
                    tasks[idx - 1].markAsNotDone();
                    System.out.println(LINE);
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + tasks[idx - 1]);
                    System.out.println(LINE);
                }
            } else if (input.startsWith("todo ")) {
                String desc = input.substring(5).trim();
                if (desc.isEmpty()) { continue; }
                if (size < tasks.length) {
                    tasks[size++] = new Todo(desc);
                    added(tasks[size - 1], size);
                }
            } else if (input.startsWith("deadline ")) {
                String body = input.substring(9).trim();
                int idx = body.indexOf("/by");
                if (idx == -1) { continue; }
                String desc = body.substring(0, idx).trim();
                String by   = body.substring(idx + 3).trim(); // 跳过 "/by"
                if (desc.isEmpty() || by.isEmpty()) { continue; }
                if (size < tasks.length) {
                    tasks[size++] = new Deadline(desc, by);
                    added(tasks[size - 1], size);
                }
            } else if (input.startsWith("event ")) {
                String body = input.substring(6).trim(); // 去掉 "event "
                String[] parts = body.split("/from|/to"); // 按关键字分割

                if (parts.length >= 3) {
                    String desc = parts[0].trim();
                    String from = parts[1].trim();
                    String to   = parts[2].trim();

                    if (!desc.isEmpty() && !from.isEmpty() && !to.isEmpty() && size < tasks.length) {
                        tasks[size++] = new Event(desc, from, to);
                        added(tasks[size - 1], size);
                    }
                }
            } else if (!input.isEmpty()) {
                if (size < tasks.length) {
                    tasks[size++] = new Todo(input);
                    added(tasks[size - 1], size);
                }
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
