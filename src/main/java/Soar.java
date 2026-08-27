import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Starts the Soar chatbot application.
 */
public class Soar {
    /** Format used when naming the requested date in query results. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);

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
        Ui ui = new Ui();
        ui.showWelcome();
        Storage storage;
        ArrayList<Task> tasks;
        try {
            storage = args.length == 0 ? new Storage() : new Storage(Path.of(args[0]));
            tasks = new ArrayList<>(storage.load());
        } catch (IOException | StorageException | IllegalArgumentException e) {
            ui.showLoadingError(e.getMessage());
            return;
        }

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            try {
                CommandType commandType = CommandType.fromInput(command);

                if (commandType == CommandType.BYE) {
                    ui.showGoodbye();
                    break;
                }

                if (commandType == CommandType.LIST) {
                    ui.showTaskList(tasks);
                } else if (commandType == CommandType.DATE) {
                    showTasksOnDate(command, tasks, ui);
                } else if (commandType == CommandType.MARK) {
                    int taskIndex = parseTaskIndex(command, commandType, tasks.size());
                    Task task = tasks.get(taskIndex);
                    boolean wasDone = task.isDone();
                    task.markAsDone();
                    saveChange(storage, tasks, () -> restoreTaskState(task, wasDone));
                    ui.showTaskMarked(task, true);
                } else if (commandType == CommandType.UNMARK) {
                    int taskIndex = parseTaskIndex(command, commandType, tasks.size());
                    Task task = tasks.get(taskIndex);
                    boolean wasDone = task.isDone();
                    task.markAsNotDone();
                    saveChange(storage, tasks, () -> restoreTaskState(task, wasDone));
                    ui.showTaskMarked(task, false);
                } else if (commandType == CommandType.DELETE) {
                    int taskIndex = parseTaskIndex(command, commandType, tasks.size());
                    Task removedTask = tasks.remove(taskIndex);
                    saveChange(storage, tasks, () -> tasks.add(taskIndex, removedTask));
                    ui.showTaskDeleted(removedTask, tasks.size());
                } else if (commandType == CommandType.TODO) {
                    String description = command.substring(commandType.getCommandWord().length()).trim();
                    requireDescription(description, commandType.getCommandWord());
                    addTask(new ToDo(description), tasks, storage, ui);
                } else if (commandType == CommandType.DEADLINE) {
                    addTask(parseDeadline(command), tasks, storage, ui);
                } else if (commandType == CommandType.EVENT) {
                    addTask(parseEvent(command), tasks, storage, ui);
                }
            } catch (SoarException e) {
                ui.showError(e.getMessage());
            }
        }
    }

    /**
     * Adds and persists a task, rolling back the list if saving fails.
     *
     * @param task task to add
     * @param tasks current task list
     * @param storage persistence service
     * @param ui user interface that presents the confirmation
     * @throws StorageException if the updated list cannot be saved
     */
    private static void addTask(Task task, ArrayList<Task> tasks, Storage storage, Ui ui)
            throws StorageException {
        tasks.add(task);
        saveChange(storage, tasks, () -> tasks.remove(tasks.size() - 1));
        ui.showTaskAdded(task, tasks.size());
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
     * @throws SoarException if the description or due date is missing or invalid
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
        var dateTime = DateTimeParser.parseDateTime(by);
        if (dateTime.isPresent()) {
            return new Deadline(description, dateTime.get());
        }
        var date = DateTimeParser.parseDate(by);
        if (date.isPresent()) {
            return new Deadline(description, date.get());
        }
        throw new InvalidTaskFormatException("I couldn't understand the deadline '" + by
                + "'. Use yyyy-MM-dd, d/M/yyyy, d/M/yyyy HHmm, yyyy-MM-dd HH:mm, "
                + "d MMM yyyy h:mm a, or an ISO date-time!");
    }

    /**
     * Prints deadlines and dated events occurring on a requested calendar date.
     *
     * @param input complete date command
     * @param tasks current task list
     * @param ui user interface that presents the matching tasks
     * @throws InvalidTaskFormatException if the date is missing or unrecognized
     */
    private static void showTasksOnDate(String input, List<Task> tasks, Ui ui)
            throws InvalidTaskFormatException {
        String value = input.substring(CommandType.DATE.getCommandWord().length()).trim();
        if (value.isEmpty()) {
            throw new InvalidTaskFormatException(
                    "Add a date after 'date' so I know which day's flight plan to show!");
        }

        var requestedDate = DateTimeParser.parseCalendarDate(value);
        if (requestedDate.isEmpty()) {
            throw new InvalidTaskFormatException("I couldn't understand the date '" + value
                    + "'. Use one of the supported deadline date or date-time formats!");
        }

        LocalDate date = requestedDate.get();
        String displayedDate = date.format(DISPLAY_DATE_FORMAT);
        ArrayList<String> matches = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            boolean matchesDate = task instanceof Deadline deadline
                    && deadline.getBy().toLocalDate().equals(date);
            matchesDate = matchesDate || task instanceof Event event && event.occursOn(date);
            if (matchesDate) {
                matches.add((i + 1) + "." + task);
            }
        }

        ui.showTasksOnDate(displayedDate, matches);
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

}
