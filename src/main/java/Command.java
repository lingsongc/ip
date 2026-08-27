/**
 * Represents one user command that can act on the application's components.
 */
public abstract class Command {
    /**
     * Performs this command's behavior.
     *
     * @param tasks task collection on which the command can operate
     * @param ui user interface used to present the result
     * @param storage persistence service for commands that change tasks
     * @throws SoarException if the command cannot be completed safely
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws SoarException;

    /**
     * Reports whether executing this command should end the application loop.
     *
     * @return {@code false} for commands that keep Soar running
     */
    public boolean isExit() {
        return false;
    }
}
