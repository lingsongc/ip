import java.util.Objects;

/**
 * Adds one parsed task and persists the updated task list.
 */
public class AddCommand extends Command {
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
        saveChange(storage, tasks, () -> tasks.delete(tasks.size() - 1));
        ui.showTaskAdded(task, tasks.size());
    }
}
