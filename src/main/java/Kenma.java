import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedWriter;
import java.io.IOException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Kenma {
    private static final String LINE =
            "____________________________________________________________";
    private static final Path DATA_FILE = Paths.get("data", "kenma.txt");

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
        try {
            tasks.addAll(loadFromDisk());
        } catch (IOException e) {
            System.out.println(" Warning: failed to load previous data, starting empty.");
        }

        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                if (!sc.hasNextLine()) {
                    sayBye();
                    break;
                }
                String input = sc.nextLine().trim();
                if (input.isEmpty()) continue;

                if (input.equalsIgnoreCase("bye")) {
                    sayBye();
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
                    trySave(tasks);

                } else if (input.startsWith("unmark ")) {
                    int idx = requireValidIndex(input.substring(7), tasks.size());
                    tasks.get(idx - 1).markAsNotDone();
                    System.out.println(LINE);
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + tasks.get(idx - 1));
                    System.out.println(LINE);
                    trySave(tasks);

                } else if (input.startsWith("delete ")) {
                    int idx = requireValidIndex(input.substring(7), tasks.size());
                    Task removed = tasks.remove(idx - 1);
                    System.out.println(LINE);
                    System.out.println(" Noted. I've removed this task:");
                    System.out.println("   " + removed);
                    System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                    System.out.println(LINE);
                    trySave(tasks);

                } else if (input.startsWith("todo")) {
                    String desc = input.length() > 4 ? input.substring(5).trim() : "";
                    if (desc.isEmpty()) throw new DukeException("The description of a todo cannot be empty.");
                    tasks.add(new Todo(desc));
                    added(tasks.get(tasks.size() - 1), tasks.size());
                    trySave(tasks);

                } else if (input.startsWith("deadline")) {
                    String body = input.substring(9).trim();
                    int idx = body.toLowerCase().indexOf("/by");
                    if (idx == -1) throw new DukeException("Missing '/by'. Usage: deadline <desc> /by <when>");
                    String desc = body.substring(0, idx).trim();
                    String by = body.substring(idx + 3).trim();
                    if (desc.isEmpty() || by.isEmpty()) throw new DukeException("Both description and '/by <when>' are required.");
                    tasks.add(new Deadline(desc, by));
                    added(tasks.get(tasks.size() - 1), tasks.size());
                    trySave(tasks);

                } else if (input.startsWith("event")) {
                    String body = input.length() > 5 ? input.substring(6).trim() : "";
                    String low = body.toLowerCase();
                    int fromPos = low.indexOf("/from");
                    int toPos = low.indexOf("/to");
                    if (fromPos < 0 || toPos < 0 || toPos <= fromPos)
                        throw new DukeException("Event must be 'event <desc> /from <start> /to <end>'.");
                    String desc = body.substring(0, fromPos).trim();
                    String from = body.substring(fromPos + 5, toPos).trim();
                    String to = body.substring(toPos + 3).trim();
                    if (desc.isEmpty() || from.isEmpty() || to.isEmpty())
                        throw new DukeException("Event requires description, from and to.");
                    tasks.add(new Event(desc, from, to));
                    added(tasks.get(tasks.size() - 1), tasks.size());
                    trySave(tasks);

                } else if (input.startsWith("on ")) {
                    String dateStr = input.substring(3).trim();
                    handleOnCommand(tasks, dateStr);
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

    private static List<Task> loadFromDisk() throws IOException {
        if (!Files.exists(DATA_FILE)) return new ArrayList<>();
        List<String> lines = Files.readAllLines(DATA_FILE);
        ArrayList<Task> tasks = new ArrayList<>();
        for (String line : lines) {
            Task t = decode(line);
            if (t != null) tasks.add(t);
        }
        return tasks;
    }

    private static void trySave(List<Task> tasks) {
        try {
            Files.createDirectories(DATA_FILE.getParent());
            try (BufferedWriter bw = Files.newBufferedWriter(DATA_FILE)) {
                for (Task t : tasks) {
                    bw.write(encode(t));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println(" Warning: failed to save.");
        }
    }

    private static Task decode(String line) {
        String[] p = Arrays.stream(line.split("\\|")).map(String::trim).toArray(String[]::new);
        if (p.length < 3) return null;
        String type = p[0];
        boolean isDone = "1".equals(p[1]);
        String desc = p[2];
        try {
            switch (type) {
                case "T":
                    Todo t = new Todo(desc);
                    if (isDone) t.markAsDone();
                    return t;
                case "D":
                    if (p.length < 4) return null;
                    Deadline d = new Deadline(desc, p[3]);
                    if (isDone) d.markAsDone();
                    return d;
                case "E":
                    if (p.length == 4) {
                        Event e1 = new Event(desc, p[3], "");
                        if (isDone) e1.markAsDone();
                        return e1;
                    } else if (p.length >= 5) {
                        Event e2 = new Event(desc, p[3], p[4]);
                        if (isDone) e2.markAsDone();
                        return e2;
                    } else {
                        return null;
                    }
                default:
                    return null;
            }
        } catch (Exception ex) {
            return null;
        }
    }


    private static String encode(Task t) {
        String flag = t.isDone ? "1" : "0";
        if (t instanceof Todo) {
            return "T | " + flag + " | " + t.description;
        } else if (t instanceof Deadline) {
            return "D | " + flag + " | " + t.description + " | " + ((Deadline) t).by;
        } else if (t instanceof Event) {
            Event e = (Event) t;
            if (e.to == null || e.to.isEmpty()) {
                return "E | " + flag + " | " + e.description + " | " + e.from;
            } else {
                return "E | " + flag + " | " + e.description + " | " + e.from + " | " + e.to;
            }
        } else {
            return null;
        }
    }

    private static void sayBye() {
        System.out.println(LINE);
        System.out.println(" Bye. Hope to see you again soon!");
        System.out.println(LINE);
    }

    private static int requireValidIndex(String raw, int size) throws DukeException {
        int idx;
        try { idx = Integer.parseInt(raw.trim()); }
        catch (NumberFormatException e) { throw new DukeException("Please provide a valid integer index."); }
        if (idx < 1 || idx > size) throw new DukeException("Index out of range. Valid range: 1.." + size + ".");
        return idx;
    }

    private static void added(Task t, int count) {
        System.out.println(LINE);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + t);
        System.out.println(" Now you have " + count + " tasks in the list.");
        System.out.println(LINE);
    }

    private static void handleOnCommand(ArrayList<Task> tasks, String dateStr) {
        System.out.println(LINE);
        try {
            java.time.LocalDate target = java.time.LocalDate.parse(
                    dateStr, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            boolean found = false;
            System.out.println(" Tasks on " + target + ":");
            for (int i = 0; i < tasks.size(); i++) {
                Task t = tasks.get(i);
                if (t instanceof Deadline) {
                    Deadline d = (Deadline) t;
                    if (d.occursOn(target)) {
                        System.out.printf(" %d.%s%n", i + 1, d);
                        found = true;
                    }
                } else if (t instanceof Event) {
                    Event e = (Event) t;
                    if (e.occursOn(target)) {
                        System.out.printf(" %d.%s%n", i + 1, e);
                        found = true;
                    }
                }
            }
            if (!found) {
                System.out.println(" No tasks on this date.");
            }
        } catch (java.time.format.DateTimeParseException e) {
            System.out.println(" Please provide a valid date in yyyy-MM-dd format.");
        }
        System.out.println(LINE);
    }
}
