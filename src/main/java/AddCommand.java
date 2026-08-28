import java.io.IOException;
import java.util.Objects;

/**
 * Adds one parsed task and persists the updated task list.
 */
public class AddCommand extends Command {
    /** Message shown when an addition cannot be safely persisted. */
    private static final String SAVE_ERROR_MESSAGE =
            "I couldn't save the task data, so that change was not kept. "
                    + "Please check the data file and try again.";

    /** Task that should be added when this command executes. */
    private final Task task;

    /**
     * Creates an addition command for an already parsed task.
     *
     * @param task validated task to add
     */
    public AddCommand(Task task) {
        this.task = Objects.requireNonNull(task, "Task to add must not be null");
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws StorageException {
        tasks.add(task);
        try {
            storage.save(tasks.asList());
        } catch (IOException | RuntimeException e) {
            tasks.delete(tasks.size() - 1);
            throw new StorageException(SAVE_ERROR_MESSAGE);
        }
        ui.showTaskAdded(task, tasks.size());
    }
}
