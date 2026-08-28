/**
 * Marks one selected task as completed and persists the change.
 */
public class MarkCommand extends Command {
    /** Zero-based index of the task to mark. */
    private final int taskIndex;

    /**
     * Creates a command for a validated task index.
     *
     * @param taskIndex zero-based index of the task to mark
     */
    public MarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws StorageException {
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        tasks.mark(taskIndex);
        saveChange(storage, tasks, () -> tasks.restoreCompletion(taskIndex, wasDone));
        ui.showTaskMarked(task, true);
    }
}
