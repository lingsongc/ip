import java.time.LocalDate;
import java.util.Optional;

/**
 * Represents a task that takes place between a start and end date or time.
 */
public class Event extends Task {
    /** Date or time at which the event starts. */
    protected String from;

    /** Date or time at which the event ends. */
    protected String to;

    /**
     * Creates an incomplete event with the given description and time range.
     *
     * @param description description of the event
     * @param from date or time at which the event starts
     * @param to date or time at which the event ends
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns whether this event occurs on a calendar date.
     *
     * <p>A fully dated start and end form an inclusive range. If only one end
     * contains a supported calendar date, that date alone is matched. This keeps
     * older free-form event times valid while making dated events searchable.</p>
     *
     * @param date calendar date to check
     * @return {@code true} if the event occurs on that date
     */
    public boolean occursOn(LocalDate date) {
        Optional<LocalDate> startDate = DateTimeParser.parseCalendarDate(from);
        Optional<LocalDate> endDate = DateTimeParser.parseCalendarDate(to);
        if (startDate.isPresent() && endDate.isPresent()) {
            return !date.isBefore(startDate.get()) && !date.isAfter(endDate.get());
        }
        return startDate.map(date::equals).orElse(false)
                || endDate.map(date::equals).orElse(false);
    }

    @Override
    public String getTypeIcon() {
        return "[E]";
    }

    @Override
    public String toDataString() {
        return "E | " + getDataStatus() + " | " + Storage.escapeField(description)
                + " | " + Storage.escapeField(from) + " | " + Storage.escapeField(to);
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
