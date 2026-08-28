import java.io.IOException;
import java.nio.file.Path;

/**
 * Starts the Soar chatbot application.
 */
public class Soar {
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
        TaskList tasks;
        try {
            storage = args.length == 0 ? new Storage() : new Storage(Path.of(args[0]));
            tasks = new TaskList(storage.load());
        } catch (IOException | StorageException | IllegalArgumentException e) {
            ui.showLoadingError(e.getMessage());
            return;
        }

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            String command = ui.readCommand();
            try {
                CommandType commandType = Parser.parseCommand(command);

                Command executableCommand = switch (commandType) {
                case BYE -> new ExitCommand();
                case LIST -> new ListCommand();
                case DATE -> new DateCommand(Parser.parseDate(command));
                default -> null;
                };
                if (executableCommand != null) {
                    executableCommand.execute(tasks, ui, storage);
                    isExit = executableCommand.isExit();
                    continue;
                }

                if (commandType == CommandType.MARK) {
                    int taskIndex = Parser.parseTaskIndex(command, commandType, tasks.size());
                    Task task = tasks.get(taskIndex);
                    boolean wasDone = task.isDone();
                    tasks.mark(taskIndex);
                    saveChange(storage, tasks, () -> tasks.restoreCompletion(taskIndex, wasDone));
                    ui.showTaskMarked(task, true);
                } else if (commandType == CommandType.UNMARK) {
                    int taskIndex = Parser.parseTaskIndex(command, commandType, tasks.size());
                    Task task = tasks.get(taskIndex);
                    boolean wasDone = task.isDone();
                    tasks.unmark(taskIndex);
                    saveChange(storage, tasks, () -> tasks.restoreCompletion(taskIndex, wasDone));
                    ui.showTaskMarked(task, false);
                } else if (commandType == CommandType.DELETE) {
                    int taskIndex = Parser.parseTaskIndex(command, commandType, tasks.size());
                    Task removedTask = tasks.delete(taskIndex);
                    saveChange(storage, tasks,
                            () -> tasks.restoreDeletedTask(taskIndex, removedTask));
                    ui.showTaskDeleted(removedTask, tasks.size());
                } else if (commandType == CommandType.TODO
                        || commandType == CommandType.DEADLINE
                        || commandType == CommandType.EVENT) {
                    addTask(Parser.parseTask(command, commandType), tasks, storage, ui);
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
    private static void addTask(Task task, TaskList tasks, Storage storage, Ui ui)
            throws StorageException {
        tasks.add(task);
        saveChange(storage, tasks, () -> tasks.delete(tasks.size() - 1));
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
    private static void saveChange(Storage storage, TaskList tasks, Runnable rollback)
            throws StorageException {
        try {
            storage.save(tasks.asList());
        } catch (IOException | RuntimeException e) {
            rollback.run();
            throw new StorageException(SAVE_ERROR_MESSAGE);
        }
    }

}
