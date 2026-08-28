package soar.command;

import soar.storage.Storage;
import soar.task.TaskList;
import soar.ui.Ui;

/**
 * Ends the current Soar session after showing the farewell message.
 */
public class ExitCommand extends Command {
    /**
     * Displays the farewell message for the current session.
     *
     * @param tasks unused task-list dependency supplied by the command interface
     * @param ui interface used to display the farewell message
     * @param storage unused storage dependency supplied by the command interface
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Identifies this command as the command that ends the input loop.
     *
     * @return {@code true} because this is an exit command
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
