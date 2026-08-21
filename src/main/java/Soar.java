import java.util.Scanner;

/**
 * Starts the Soar chatbot application.
 */
public class Soar {
    /** Width of the line used to frame the chatbot's messages. */
    private static final int SEPARATOR_WIDTH = 60;

    /** Maximum number of tasks that can be kept during one run. */
    private static final int MAX_TASKS = 100;

    /**
     * Greets the user, stores tasks, lists or updates their status on request, and
     * exits when the user enters {@code bye}.
     *
     * @param args command-line arguments; they are not used
     */
    public static void main(String[] args) {
        String banner = " ____                    \n"
                + "/ ___|  ___   __ _ _ __  \n"
                + "\\___ \\ / _ \\ / _` | '__| \n"
                + " ___) | (_) | (_| | |    \n"
                + "|____/ \\___/ \\__,_|_|    ";
        String separator = "_".repeat(SEPARATOR_WIDTH);

        System.out.println(separator);
        System.out.println(banner);
        System.out.println("Hey there! I'm Soar, your upbeat little sidekick!");
        System.out.println("What exciting thing can I help you tackle today?");
        System.out.println(separator);

        Scanner scanner = new Scanner(System.in);
        String[] tasks = new String[MAX_TASKS];
        // Each completion value belongs to the task at the same array index.
        boolean[] isDone = new boolean[MAX_TASKS];
        int taskCount = 0;

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(separator);

            if (command.equals("bye")) {
                System.out.println("Bye! Always soar towards your goals!");
                System.out.println(separator);
                break;
            }

            if (command.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    String statusIcon = isDone[i] ? "X" : " ";
                    System.out.println((i + 1) + ".[" + statusIcon + "] " + tasks[i]);
                }
            } else if (command.equals("mark") || command.startsWith("mark ")) {
                try {
                    int taskIndex = Integer.parseInt(command.substring("mark".length()).trim()) - 1;
                    if (taskIndex < 0 || taskIndex >= taskCount) {
                        System.out.println("Please enter the number of a task in your list.");
                    } else {
                        isDone[taskIndex] = true;
                        System.out.println("Nice! I've marked this task as done:");
                        System.out.println("  [X] " + tasks[taskIndex]);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a task number after mark.");
                }
            } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                try {
                    int taskIndex = Integer.parseInt(command.substring("unmark".length()).trim()) - 1;
                    if (taskIndex < 0 || taskIndex >= taskCount) {
                        System.out.println("Please enter the number of a task in your list.");
                    } else {
                        isDone[taskIndex] = false;
                        System.out.println("OK, I've marked this task as not done yet:");
                        System.out.println("  [ ] " + tasks[taskIndex]);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a task number after unmark.");
                }
            } else {
                tasks[taskCount] = command;
                taskCount++;
                System.out.println("added: " + command);
            }

            System.out.println(separator);
        }
    }
}
