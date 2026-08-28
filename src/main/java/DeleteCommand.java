/**
 * Deletes one selected task and persists the updated task list.
 */
public class DeleteCommand extends Command {
    /** One-based task number entered by the user. */
    private final int taskNumber;

    /**
     * Creates a deletion command for a parsed task number.
     *
     * @param taskNumber one-based number of the task to delete
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws SoarException {
        int taskIndex = requireTaskIndex(taskNumber, tasks, CommandType.DELETE);
        Task removedTask = tasks.delete(taskIndex);
        saveChange(storage, tasks,
                () -> tasks.restoreDeletedTask(taskIndex, removedTask));
        ui.showTaskDeleted(removedTask, tasks.size());
    }
}
