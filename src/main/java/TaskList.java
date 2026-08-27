import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Owns the ordered collection of tasks and its list-level operations.
 */
public class TaskList {
    /** Tasks in the same order in which they are shown to the user. */
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this(List.of());
    }

    /**
     * Creates a task list containing a copy of previously loaded tasks.
     *
     * @param initialTasks tasks to store in their current order
     */
    public TaskList(List<Task> initialTasks) {
        Objects.requireNonNull(initialTasks, "Initial tasks must not be null");
        tasks = new ArrayList<>(initialTasks);
    }

    /**
     * Returns the number of stored tasks.
     *
     * @return current task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index zero-based task index
     * @return selected task
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns a snapshot suitable for display or persistence.
     *
     * <p>The returned list cannot be structurally modified, so callers must use
     * this class's operations to change the task collection.</p>
     *
     * @return immutable snapshot of the tasks in list order
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        tasks.add(Objects.requireNonNull(task, "Task must not be null"));
    }

    /**
     * Deletes and returns the task at a zero-based index.
     *
     * @param index zero-based task index
     * @return removed task
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Restores a task at its previous position after a failed save.
     *
     * @param index zero-based position at which the task belonged
     * @param task task to restore
     */
    public void restoreDeletedTask(int index, Task task) {
        tasks.add(index, Objects.requireNonNull(task, "Task must not be null"));
    }

    /** Marks and returns the task at a zero-based index. */
    public Task mark(int index) {
        Task task = tasks.get(index);
        task.markAsDone();
        return task;
    }

    /** Unmarks and returns the task at a zero-based index. */
    public Task unmark(int index) {
        Task task = tasks.get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Restores a task's completion state after a failed save.
     *
     * @param index zero-based task index
     * @param wasDone completion state to restore
     */
    public void restoreCompletion(int index, boolean wasDone) {
        if (wasDone) {
            mark(index);
        } else {
            unmark(index);
        }
    }

    /**
     * Finds the original zero-based indices of dated tasks occurring on a date.
     *
     * @param date calendar date to match
     * @return matching indices in task-list order
     */
    public List<Integer> findIndicesOn(LocalDate date) {
        ArrayList<Integer> matches = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            boolean matchesDate = task instanceof Deadline deadline
                    && deadline.getBy().toLocalDate().equals(date);
            matchesDate = matchesDate || task instanceof Event event && event.occursOn(date);
            if (matchesDate) {
                matches.add(i);
            }
        }
        return List.copyOf(matches);
    }
}
