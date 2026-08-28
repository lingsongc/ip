/**
 * Marks one selected task as incomplete and persists the change.
 */
public class UnmarkCommand extends Command {
    /** Zero-based index of the task to unmark. */
    private final int taskIndex;

    /**
     * Creates a command for a validated task index.
     *
     * @param taskIndex zero-based index of the task to unmark
     */
    public UnmarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws StorageException {
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        tasks.unmark(taskIndex);
        saveChange(storage, tasks, () -> tasks.restoreCompletion(taskIndex, wasDone));
        ui.showTaskMarked(task, false);
    }
}
