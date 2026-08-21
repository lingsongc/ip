/**
 * Represents the shared state and behavior of a task.
 */
public abstract class Task {
    /** Description of what needs to be done. */
    protected String description;

    /** Whether this task has been completed. */
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description of what needs to be done
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the label that identifies the concrete task type.
     *
     * @return the task type label, such as {@code [T]}
     */
    public abstract String getTypeIcon();

    /**
     * Returns the icon used to show this task's completion status.
     *
     * @return {@code X} if the task is done, or a space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not completed. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the task in the format used by the chatbot.
     *
     * @return the task type, status icon, and description
     */
    @Override
    public String toString() {
        return getTypeIcon() + "[" + getStatusIcon() + "] " + description;
    }
}
