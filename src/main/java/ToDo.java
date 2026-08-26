/**
 * Represents a task without an associated date or time.
 */
public class ToDo extends Task {
    /**
     * Creates an incomplete todo with the given description.
     *
     * @param description description of what needs to be done
     */
    public ToDo(String description) {
        super(description);
    }

    @Override
    public String getTypeIcon() {
        return "[T]";
    }

    @Override
    public String toDataString() {
        return "T | " + getDataStatus() + " | " + description;
    }
}
