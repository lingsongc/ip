/**
 * Represents a task that must be completed by a particular date or time.
 */
public class Deadline extends Task {
    /** Date or time by which the task should be completed. */
    protected String by;

    /**
     * Creates an incomplete deadline with the given description and due date.
     *
     * @param description description of what needs to be done
     * @param by date or time by which the task should be completed
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    public String getTypeIcon() {
        return "[D]";
    }

    @Override
    public String toDataString() {
        return "D | " + getDataStatus() + " | " + Storage.escapeField(description)
                + " | " + Storage.escapeField(by);
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by + ")";
    }
}
