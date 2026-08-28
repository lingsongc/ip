package soar.command;

import soar.storage.Storage;
import soar.task.TaskList;
import soar.ui.Ui;

/**
 * Shows all tasks in their current list order.
 */
public class ListCommand extends Command {
    /**
     * Creates a command that shows all stored tasks.
     */
    public ListCommand() {
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks.asList());
    }
}
