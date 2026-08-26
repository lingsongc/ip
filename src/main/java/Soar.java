import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Starts the Soar chatbot application.
 */
public class Soar {
    /** Width of the line used to frame the chatbot's messages. */
    private static final int SEPARATOR_WIDTH = 60;

    /** Message shown when a task-list change cannot be safely persisted. */
    private static final String SAVE_ERROR_MESSAGE =
            "I couldn't save the task data, so that change was not kept. Please check the data file and try again.";

    /**
     * Greets the user, stores tasks, lists or updates their status on request, and
     * exits when the user enters {@code bye}.
     *
     * @param args optional first argument overrides the default task data file
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
        Storage storage;
        ArrayList<Task> tasks;
        try {
            storage = args.length == 0 ? new Storage() : new Storage(Path.of(args[0]));
            tasks = new ArrayList<>(storage.load());
        } catch (IOException | StorageException | IllegalArgumentException e) {
            System.out.println("I couldn't load the task data safely: " + e.getMessage());
            System.out.println("Please repair or move the data file, then restart Soar.");
            System.out.println(separator);
            return;
        }

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(separator);

            try {
                CommandType commandType = CommandType.fromInput(command);

                if (commandType == CommandType.BYE) {
                    System.out.println("Bye! Always soar towards your goals!");
                    System.out.println(separator);
                    break;
                }

                if (commandType == CommandType.LIST) {
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println((i + 1) + "." + tasks.get(i));
                    }
                } else if (commandType == CommandType.MARK) {
                    int taskIndex = parseTaskIndex(command, commandType, tasks.size());
                    Task task = tasks.get(taskIndex);
                    boolean wasDone = task.isDone();
                    task.markAsDone();
                    saveChange(storage, tasks, () -> restoreTaskState(task, wasDone));
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + task);
                } else if (commandType == CommandType.UNMARK) {
                    int taskIndex = parseTaskIndex(command, commandType, tasks.size());
                    Task task = tasks.get(taskIndex);
                    boolean wasDone = task.isDone();
                    task.markAsNotDone();
                    saveChange(storage, tasks, () -> restoreTaskState(task, wasDone));
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + task);
                } else if (commandType == CommandType.DELETE) {
                    int taskIndex = parseTaskIndex(command, commandType, tasks.size());
                    Task removedTask = tasks.remove(taskIndex);
                    saveChange(storage, tasks, () -> tasks.add(taskIndex, removedTask));
                    System.out.println("Noted. I've removed this task:");
                    System.out.println("  " + removedTask);
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                } else if (commandType == CommandType.TODO) {
                    String description = command.substring(commandType.getCommandWord().length()).trim();
                    requireDescription(description, commandType.getCommandWord());
                    addTask(new ToDo(description), tasks, storage);
                } else if (commandType == CommandType.DEADLINE) {
                    addTask(parseDeadline(command), tasks, storage);
                } else if (commandType == CommandType.EVENT) {
                    addTask(parseEvent(command), tasks, storage);
                }
            } catch (SoarException e) {
                System.out.println(e.getMessage());
            }

            System.out.println(separator);
        }
    }

    /**
     * Adds and persists a task, rolling back the list if saving fails.
     *
     * @param task task to add
     * @param tasks current task list
     * @param storage persistence service
     * @throws StorageException if the updated list cannot be saved
     */
    private static void addTask(Task task, ArrayList<Task> tasks, Storage storage)
            throws StorageException {
        tasks.add(task);
        saveChange(storage, tasks, () -> tasks.remove(tasks.size() - 1));
        printTaskAdded(task, tasks.size());
    }

    /**
     * Saves a changed list and reverses the in-memory change on failure.
     *
     * @param storage persistence service
     * @param tasks changed task list
     * @param rollback action that restores the list's previous state
     * @throws StorageException if the changed list cannot be saved
     */
    private static void saveChange(Storage storage, List<Task> tasks, Runnable rollback)
            throws StorageException {
        try {
            storage.save(tasks);
        } catch (IOException | RuntimeException e) {
            rollback.run();
            throw new StorageException(SAVE_ERROR_MESSAGE);
        }
    }

    /** Restores a task's completion state after a failed save. */
    private static void restoreTaskState(Task task, boolean wasDone) {
        if (wasDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
    }

    /**
     * Parses and validates the task number in a mark, unmark, or delete command.
     *
     * @param input complete line entered by the user
     * @param commandType mark, unmark, or delete
     * @param taskCount number of tasks currently stored
     * @return zero-based index of the selected task
     * @throws InvalidTaskNumberException if the number is missing, not an integer,
     *         or outside the task list
     */
    private static int parseTaskIndex(String input, CommandType commandType, int taskCount)
            throws InvalidTaskNumberException {
        String commandWord = commandType.getCommandWord();
        String taskNumberText = input.substring(commandWord.length()).trim();
        if (taskNumberText.isEmpty()) {
            throw new InvalidTaskNumberException("Add a task number after '" + commandWord
                    + "' so I know which task "
                    + commandType.getMissingTaskNumberAction().getDescription() + "!");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new InvalidTaskNumberException("'" + taskNumberText
                    + "' is not a whole task number. Choose a number from your list to keep flying high!");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            if (taskCount == 0) {
                throw new InvalidTaskNumberException(
                        "Your task list is an open sky right now. Add a task before using '"
                                + commandWord + "'!");
            }
            throw new InvalidTaskNumberException("Task " + taskNumber
                    + " is outside your list. Choose a number from 1 to " + taskCount
                    + " and we'll stay on course!");
        }
        return taskNumber - 1;
    }

    /**
     * Builds a deadline after checking its description and {@code /by} value.
     *
     * @param input complete deadline command
     * @return validated deadline
     * @throws SoarException if the description or due date is missing
     */
    private static Deadline parseDeadline(String input) throws SoarException {
        String details = input.substring(CommandType.DEADLINE.getCommandWord().length()).trim();
        int byIndex = details.indexOf("/by");
        if (byIndex < 0) {
            throw new InvalidTaskFormatException(
                    "This deadline has no '/by' date. Add one so it has a clear path through the sky!");
        }

        String description = details.substring(0, byIndex).trim();
        String by = details.substring(byIndex + "/by".length()).trim();
        requireDescription(description, CommandType.DEADLINE.getCommandWord());
        if (by.isEmpty()) {
            throw new InvalidTaskFormatException(
                    "The deadline's '/by' date is empty. Add a date or time so it can fly on schedule!");
        }
        return new Deadline(description, by);
    }

    /**
     * Builds an event after checking its description and time range.
     *
     * @param input complete event command
     * @return validated event
     * @throws SoarException if the description, start, or end value is missing
     */
    private static Event parseEvent(String input) throws SoarException {
        String details = input.substring(CommandType.EVENT.getCommandWord().length()).trim();
        int fromIndex = details.indexOf("/from");
        int toIndex = fromIndex < 0 ? -1 : details.indexOf("/to", fromIndex + "/from".length());
        if (fromIndex < 0 || toIndex < 0) {
            throw new InvalidTaskFormatException(
                    "This event needs both '/from' and '/to' times to map its flight across the sky!");
        }

        String description = details.substring(0, fromIndex).trim();
        String from = details.substring(fromIndex + "/from".length(), toIndex).trim();
        String to = details.substring(toIndex + "/to".length()).trim();
        requireDescription(description, CommandType.EVENT.getCommandWord());
        if (from.isEmpty() || to.isEmpty()) {
            throw new InvalidTaskFormatException(
                    "The event's flight times are incomplete. Fill in both '/from' and '/to' values!");
        }
        return new Event(description, from, to);
    }

    /**
     * Rejects an empty task description with a task-specific explanation.
     *
     * @param description description to validate
     * @param taskType type of task being created
     * @throws EmptyDescriptionException if the description is empty
     */
    private static void requireDescription(String description, String taskType)
            throws EmptyDescriptionException {
        if (description.isEmpty()) {
            throw new EmptyDescriptionException(taskType);
        }
    }

    /**
     * Prints the shared confirmation shown after adding any task type.
     *
     * @param task task that was added
     * @param taskCount updated number of stored tasks
     */
    private static void printTaskAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }
}
