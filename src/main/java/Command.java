import java.io.IOException;

/**
 * Represents one user command that can act on the application's components.
 */
public abstract class Command {
    /** Message shown when a task-list change cannot be safely persisted. */
    private static final String SAVE_ERROR_MESSAGE =
            "I couldn't save the task data, so that change was not kept. "
                    + "Please check the data file and try again.";

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
     * Persists a changed task list and reverses the change if saving fails.
     *
     * @param storage persistence service
     * @param tasks changed task list
     * @param rollback action that restores the previous in-memory state
     * @throws StorageException if the changed list cannot be saved
     */
    protected final void saveChange(Storage storage, TaskList tasks, Runnable rollback)
            throws StorageException {
        try {
            storage.save(tasks.asList());
        } catch (IOException | RuntimeException e) {
            rollback.run();
            throw new StorageException(SAVE_ERROR_MESSAGE);
        }
    }

    /**
     * Reports whether executing this command should end the application loop.
     *
     * @return {@code false} for commands that keep Soar running
     */
    public boolean isExit() {
        return false;
    }
}
