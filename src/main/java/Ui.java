import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Handles console input and presents Soar's messages to the user.
 */
public class Ui {
    /** Width of the line used to frame the chatbot's messages. */
    private static final int SEPARATOR_WIDTH = 60;

    /** Console input used to read commands. */
    private final Scanner scanner;

    /** Creates a user interface connected to standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Shows the application banner and greeting. */
    public void showWelcome() {
        String banner = " ____                    \n"
                + "/ ___|  ___   __ _ _ __  \n"
                + "\\___ \\ / _ \\ / _` | '__| \n"
                + " ___) | (_) | (_| | |    \n"
                + "|____/ \\___/ \\__,_|_|    ";
        System.out.println(separator());
        System.out.println(banner);
        System.out.println("Hey there! I'm Soar, your upbeat little sidekick!");
        System.out.println("What exciting thing can I help you tackle today?");
        System.out.println(separator());
    }

    /**
     * Reports whether another complete command is available from the user.
     *
     * @return {@code true} when another input line can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the user's next command.
     *
     * @return complete command line entered by the user
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Shows the farewell response. */
    public void showGoodbye() {
        showFramed("Bye! Always soar towards your goals!");
    }

    /** Shows an error raised while handling a command. */
    public void showError(String message) {
        showFramed(message);
    }

    /**
     * Explains why startup cannot continue after loading task data.
     *
     * @param details cause reported by storage or path validation
     */
    public void showLoadingError(String details) {
        System.out.println("I couldn't load the task data safely: " + details);
        System.out.println("Please repair or move the data file, then restart Soar.");
        System.out.println(separator());
    }

    /** Shows every task with its one-based list number. */
    public void showTaskList(List<Task> tasks) {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            lines.add((i + 1) + "." + tasks.get(i));
        }
        showFramed(lines);
    }

    /** Shows the confirmation for a newly added task. */
    public void showTaskAdded(Task task, int taskCount) {
        showFramed(List.of(
                "Got it. I've added this task:",
                "  " + task,
                "Now you have " + taskCount + " tasks in the list."));
    }

    /** Shows the confirmation for a changed task completion state. */
    public void showTaskMarked(Task task, boolean isDone) {
        String message = isDone
                ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:";
        showFramed(List.of(message, "  " + task));
    }

    /** Shows the confirmation for a removed task. */
    public void showTaskDeleted(Task task, int taskCount) {
        showFramed(List.of(
                "Noted. I've removed this task:",
                "  " + task,
                "Now you have " + taskCount + " tasks in the list."));
    }

    /** Shows tasks matching a requested date, or explains that there are none. */
    public void showTasksOnDate(String displayedDate, List<String> matches) {
        if (matches.isEmpty()) {
            showFramed("There are no deadlines or events on " + displayedDate + ".");
            return;
        }
        ArrayList<String> lines = new ArrayList<>();
        lines.add("Here are the deadlines and events on " + displayedDate + ":");
        lines.addAll(matches);
        showFramed(lines);
    }

    /** Shows one response surrounded by the standard separator. */
    private void showFramed(String line) {
        showFramed(List.of(line));
    }

    /** Shows response lines surrounded by the standard separator. */
    private void showFramed(List<String> lines) {
        System.out.println(separator());
        lines.forEach(System.out::println);
        System.out.println(separator());
    }

    /** Returns the line used to frame output without storing duplicate text. */
    private String separator() {
        return "_".repeat(SEPARATOR_WIDTH);
    }
}
