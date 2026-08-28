package soar.command;

import soar.exception.SoarException;
import soar.parser.CommandType;
import soar.storage.Storage;
import soar.task.Task;
import soar.task.TaskList;
import soar.ui.Ui;

/**
 * Marks one selected task as incomplete and persists the change.
 */
public class UnmarkCommand extends Command {
    /** One-based task number entered by the user. */
    private final int taskNumber;

    /**
     * Creates a command for a parsed task number.
     *
     * @param taskNumber One-based number of the task to unmark.
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws SoarException {
        int taskIndex = requireTaskIndex(taskNumber, tasks, CommandType.UNMARK);
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        tasks.unmark(taskIndex);
        saveChange(storage, tasks, () -> tasks.restoreCompletion(taskIndex, wasDone));
        ui.showTaskMarked(task, false);
    }
}
