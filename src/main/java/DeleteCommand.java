/**
 * Deletes one selected task and persists the updated task list.
 */
public class DeleteCommand extends Command {
    /** Zero-based index of the task to delete. */
    private final int taskIndex;

    /**
     * Creates a deletion command for a validated task index.
     *
     * @param taskIndex zero-based index of the task to delete
     */
    public DeleteCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws StorageException {
        Task removedTask = tasks.delete(taskIndex);
        saveChange(storage, tasks,
                () -> tasks.restoreDeletedTask(taskIndex, removedTask));
        ui.showTaskDeleted(removedTask, tasks.size());
    }
}
