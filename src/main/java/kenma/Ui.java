package kenma;

import java.util.List;
import java.util.Scanner;

/**
 * Handles user interaction by reading commands and printing messages to the
 * console.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private final Scanner sc = new Scanner(System.in);

    /**
     * Displays the welcome banner and logo.
     *
     * @param logo ASCII logo to print
     */
    public void showWelcome(String logo) {
        System.out.println(logo);
        System.out.println(LINE);
        System.out.println(" Hello! I'm Kenma");
        System.out.println(" What can I do for you?");
        System.out.println(LINE);
    }

    /**
     * Reads the next line of user input.
     *
     * @return trimmed input string, empty string if null, or {@code null} if EOF
     */
    public String readCommand() {
        if (!sc.hasNextLine()) {
            return null;
        }
        String s = sc.nextLine();
        return s == null ? "" : s.trim();
    }

    /** Displays the goodbye message. */
    public void showBye() {
        System.out.println(LINE);
        System.out.println(" Bye. Hope to see you again soon!");
        System.out.println(LINE);
    }

    /**
     * Displays the current task list.
     *
     * @param tasks tasks to display
     */
    public void showList(List<Task> tasks) {
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
    }

    /**
     * Displays a message after adding a task.
     *
     * @param t     the task added
     * @param count total number of tasks after addition
     */
    public void showAdded(Task t, int count) {
        System.out.println(LINE);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + t);
        System.out.println(" Now you have " + count + " tasks in the list.");
        System.out.println(LINE);
    }

    /**
     * Displays a message after marking a task as done.
     *
     * @param t task that was marked
     */
    public void showMarked(Task t) {
        System.out.println(LINE);
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + t);
        System.out.println(LINE);
    }

    /**
     * Displays a message after marking a task as not done.
     *
     * @param t task that was unmarked
     */
    public void showUnmarked(Task t) {
        System.out.println(LINE);
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + t);
        System.out.println(LINE);
    }

    /**
     * Displays a message after deleting a task.
     *
     * @param t     task that was deleted
     * @param count total number of tasks after deletion
     */
    public void showDeleted(Task t, int count) {
        System.out.println(LINE);
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + t);
        System.out.println(" Now you have " + count + " tasks in the list.");
        System.out.println(LINE);
    }

    /**
     * Displays an error message.
     *
     * @param msg error message text
     */
    public void showError(String msg) {
        System.out.println(LINE);
        System.out.println(" " + msg);
        System.out.println(LINE);
    }

    /**
     * Displays tasks that match a keyword search.
     *
     * @param matches matching tasks
     * @param keyword original keyword
     */
    public void showFound(List<Task> matches, String keyword) {
        System.out.println(LINE);
        if (matches.isEmpty()) {
            System.out.println(" No matching tasks for: \"" + keyword + "\"");
        } else {
            System.out.println(" Here are the matching tasks in your list:");
            for (int i = 0; i < matches.size(); i++) {
                System.out.printf(" %d.%s%n", i + 1, matches.get(i));
            }
        }
        System.out.println(LINE);
    }
}
