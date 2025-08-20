import java.util.Scanner;

public class Kenma {
    public static void main(String[] args) {
        String logo =
                " _  __ ______ _   _ __  __       \n"
                        + "| |/ /|  ____| \\ | |  \\/  |   /\\ \n"
                        + "| ' / | |__  |  \\| | \\  / |  /  \\ \n"
                        + "|  <  |  __| | . ` | |\\/| | / /\\ \\\n"
                        + "| . \\ | |____| |\\  | |  | |/ ____ \\\n"
                        + "|_|\\_\\|______|_| \\_|_|  |_/_/    \\_\\\n";

        String LINE = "____________________________________________________________";
        System.out.println(logo);
        System.out.println(LINE);
        System.out.println(" Hello! I'm Kenma");
        System.out.println(" What can I do for you?");

        System.out.println(LINE);

        String[] tasks = new String[100];
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
                for (int i = 0; i < size; i++) {
                    System.out.printf(" %d. %s%n", i + 1, tasks[i]);
                }
                if (size == 0) {
                    System.out.println("Currently no tasks");
                }
                System.out.println(LINE);
            } else if (!input.isEmpty()) {
                tasks[size++] = input;
                System.out.println(LINE);
                System.out.println(" added: " + input);
                System.out.println(LINE);
            }
        }
    }
}
