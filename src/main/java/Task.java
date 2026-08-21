/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    /** Description of what needs to be done. */
    protected String description;

    /** Whether this task has been completed. */
    protected boolean isDone;

    /** Label that identifies this as a todo, deadline, or event. */
    protected String label;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description of what needs to be done
     * @param label label for the task: {@code T}, {@code D}, or {@code E}
     */
    public Task(String description, String label) {
        this.description = description;
        this.label = label;
        this.isDone = false;
    }

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
     * @return the task label, status icon, and task description
     */
    @Override
    public String toString() {
        String labelIcon;
        if (label.equals("D")) {
            labelIcon = "[D]";
        } else if (label.equals("E")) {
            labelIcon = "[E]";
        } else {
            labelIcon = "[T]";
        }
        return labelIcon + "[" + getStatusIcon() + "] " + description;
    }
}
