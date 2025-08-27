import java.util.List;
import java.util.Scanner;

public class Ui {
    private static final String LINE = "____________________________________________________________";
    private final Scanner sc = new Scanner(System.in);

    public void showWelcome(String logo) {
        System.out.println(logo);
        System.out.println(LINE);
        System.out.println(" Hello! I'm Kenma");
        System.out.println(" What can I do for you?");
        System.out.println(LINE);
    }

    public String readCommand() {
        if (!sc.hasNextLine()) return null;
        String s = sc.nextLine();
        return s == null ? "" : s.trim();
    }

    public void showBye() {
        System.out.println(LINE);
        System.out.println(" Bye. Hope to see you again soon!");
        System.out.println(LINE);
    }

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

    public void showAdded(Task t, int count) {
        System.out.println(LINE);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + t);
        System.out.println(" Now you have " + count + " tasks in the list.");
        System.out.println(LINE);
    }

    public void showMarked(Task t) {
        System.out.println(LINE);
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + t);
        System.out.println(LINE);
    }

    public void showUnmarked(Task t) {
        System.out.println(LINE);
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + t);
        System.out.println(LINE);
    }

    public void showDeleted(Task t, int count) {
        System.out.println(LINE);
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + t);
        System.out.println(" Now you have " + count + " tasks in the list.");
        System.out.println(LINE);
    }

    public void showError(String msg) {
        System.out.println(LINE);
        System.out.println(" " + msg);
        System.out.println(LINE);
    }
}
