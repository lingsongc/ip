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
        Task[] tasks = new Task[MAX_TASKS];
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
                    System.out.println((i + 1) + "." + tasks[i]);
                }
            } else if (command.equals("mark") || command.startsWith("mark ")) {
                try {
                    int taskIndex = Integer.parseInt(command.substring("mark".length()).trim()) - 1;
                    if (taskIndex < 0 || taskIndex >= taskCount) {
                        System.out.println("Please enter the number of a task in your list.");
                    } else {
                        tasks[taskIndex].markAsDone();
                        System.out.println("Nice! I've marked this task as done:");
                        System.out.println("  " + tasks[taskIndex]);
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
                        tasks[taskIndex].markAsNotDone();
                        System.out.println("OK, I've marked this task as not done yet:");
                        System.out.println("  " + tasks[taskIndex]);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a task number after unmark.");
                }
            } else if (command.startsWith("todo ")) {
                String description = command.substring("todo".length()).trim();
                tasks[taskCount] = new Task(description, "T");
                taskCount++;
                System.out.println("Got it. I've added this task:");
                System.out.println("  " + tasks[taskCount - 1]);
                System.out.println("Now you have " + taskCount + " tasks in the list.");
            } else if (command.startsWith("deadline ") && command.contains(" /by ")) {
                int byIndex = command.indexOf(" /by ");
                String description = command.substring("deadline".length(), byIndex).trim();
                String by = command.substring(byIndex + " /by ".length()).trim();
                String taskDescription = description + " (by: " + by + ")";
                tasks[taskCount] = new Task(taskDescription, "D");
                taskCount++;
                System.out.println("Got it. I've added this task:");
                System.out.println("  " + tasks[taskCount - 1]);
                System.out.println("Now you have " + taskCount + " tasks in the list.");
            } else if (command.startsWith("event ")
                    && command.contains(" /from ") && command.contains(" /to ")) {
                int fromIndex = command.indexOf(" /from ");
                int toIndex = command.indexOf(" /to ", fromIndex + " /from ".length());
                String description = command.substring("event".length(), fromIndex).trim();
                String from = command.substring(fromIndex + " /from ".length(), toIndex).trim();
                String to = command.substring(toIndex + " /to ".length()).trim();
                String taskDescription = description + " (from: " + from + " to: " + to + ")";
                tasks[taskCount] = new Task(taskDescription, "E");
                taskCount++;
                System.out.println("Got it. I've added this task:");
                System.out.println("  " + tasks[taskCount - 1]);
                System.out.println("Now you have " + taskCount + " tasks in the list.");
            } else {
                System.out.println("I don't recognise that command yet.");
            }

            System.out.println(separator);
        }
    }
}
