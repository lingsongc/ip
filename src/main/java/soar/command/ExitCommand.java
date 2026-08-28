package soar.command;

import soar.storage.Storage;
import soar.task.TaskList;
import soar.ui.Ui;

/**
 * Ends the current Soar session after showing the farewell message.
 */
public class ExitCommand extends Command {
    /**
     * Creates a command that ends the current Soar session.
     */
    public ExitCommand() {
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
