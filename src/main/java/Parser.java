import java.time.LocalDate;

/**
 * Interprets user input and converts command arguments into application values.
 */
public class Parser {
    /** Prevents creation of a stateless parser utility. */
    private Parser() {
    }

    /**
     * Identifies the command represented by a complete input line.
     *
     * @param input complete line entered by the user
     * @return matching command type
     * @throws UnknownCommandException if the input does not start with a supported command
     */
    public static CommandType parseCommand(String input) throws UnknownCommandException {
        for (CommandType commandType : CommandType.values()) {
            if (commandType.matches(input)) {
                return commandType;
            }
        }
        throw new UnknownCommandException();
    }

    /**
     * Builds the task described by a todo, deadline, or event command.
     *
     * @param input complete task-creation command
     * @param commandType type of task command being parsed
     * @return validated task represented by the command
     * @throws SoarException if a required task detail is missing or invalid
     */
    public static Task parseTask(String input, CommandType commandType) throws SoarException {
        return switch (commandType) {
        case TODO -> parseToDo(input);
        case DEADLINE -> parseDeadline(input);
        case EVENT -> parseEvent(input);
        default -> throw new IllegalArgumentException(
                "Only todo, deadline, and event commands describe new tasks");
        };
    }

    /**
     * Parses and validates the task number in a mark, unmark, or delete command.
     *
     * @param input complete line entered by the user
     * @param commandType mark, unmark, or delete
     * @param taskCount number of tasks currently stored
     * @return zero-based index of the selected task
     * @throws InvalidTaskNumberException if the number is missing, not an integer,
     *         or outside the task list
     */
    public static int parseTaskIndex(String input, CommandType commandType, int taskCount)
            throws InvalidTaskNumberException {
        String commandWord = commandType.getCommandWord();
        String taskNumberText = argumentsAfter(input, commandType);
        if (taskNumberText.isEmpty()) {
            throw new InvalidTaskNumberException("Add a task number after '" + commandWord
                    + "' so I know which task "
                    + commandType.getMissingTaskNumberAction().getDescription() + "!");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new InvalidTaskNumberException("'" + taskNumberText
                    + "' is not a whole task number. Choose a number from your list to keep flying high!");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            if (taskCount == 0) {
                throw new InvalidTaskNumberException(
                        "Your task list is an open sky right now. Add a task before using '"
                                + commandWord + "'!");
            }
            throw new InvalidTaskNumberException("Task " + taskNumber
                    + " is outside your list. Choose a number from 1 to " + taskCount
                    + " and we'll stay on course!");
        }
        return taskNumber - 1;
    }

    /**
     * Parses the calendar date supplied to a date query.
     *
     * @param input complete date command
     * @return requested calendar date
     * @throws InvalidTaskFormatException if the date is missing or unrecognized
     */
    public static LocalDate parseDate(String input) throws InvalidTaskFormatException {
        String value = argumentsAfter(input, CommandType.DATE);
        if (value.isEmpty()) {
            throw new InvalidTaskFormatException(
                    "Add a date after 'date' so I know which day's flight plan to show!");
        }

        var requestedDate = DateTimeParser.parseCalendarDate(value);
        if (requestedDate.isEmpty()) {
            throw new InvalidTaskFormatException("I couldn't understand the date '" + value
                    + "'. Use one of the supported deadline date or date-time formats!");
        }
        return requestedDate.get();
    }

    /** Builds a todo after checking its description. */
    private static ToDo parseToDo(String input) throws EmptyDescriptionException {
        String description = argumentsAfter(input, CommandType.TODO);
        requireDescription(description, CommandType.TODO.getCommandWord());
        return new ToDo(description);
    }

    /** Builds a deadline after checking its description and {@code /by} value. */
    private static Deadline parseDeadline(String input) throws SoarException {
        String details = argumentsAfter(input, CommandType.DEADLINE);
        int byIndex = details.indexOf("/by");
        if (byIndex < 0) {
            throw new InvalidTaskFormatException(
                    "This deadline has no '/by' date. Add one so it has a clear path through the sky!");
        }

        String description = details.substring(0, byIndex).trim();
        String by = details.substring(byIndex + "/by".length()).trim();
        requireDescription(description, CommandType.DEADLINE.getCommandWord());
        if (by.isEmpty()) {
            throw new InvalidTaskFormatException(
                    "The deadline's '/by' date is empty. Add a date or time so it can fly on schedule!");
        }
        var dateTime = DateTimeParser.parseDateTime(by);
        if (dateTime.isPresent()) {
            return new Deadline(description, dateTime.get());
        }
        var date = DateTimeParser.parseDate(by);
        if (date.isPresent()) {
            return new Deadline(description, date.get());
        }
        throw new InvalidTaskFormatException("I couldn't understand the deadline '" + by
                + "'. Use yyyy-MM-dd, d/M/yyyy, d/M/yyyy HHmm, yyyy-MM-dd HH:mm, "
                + "d MMM yyyy h:mm a, or an ISO date-time!");
    }

    /** Builds an event after checking its description and time range. */
    private static Event parseEvent(String input) throws SoarException {
        String details = argumentsAfter(input, CommandType.EVENT);
        int fromIndex = details.indexOf("/from");
        int toIndex = fromIndex < 0 ? -1 : details.indexOf("/to", fromIndex + "/from".length());
        if (fromIndex < 0 || toIndex < 0) {
            throw new InvalidTaskFormatException(
                    "This event needs both '/from' and '/to' times to map its flight across the sky!");
        }

        String description = details.substring(0, fromIndex).trim();
        String from = details.substring(fromIndex + "/from".length(), toIndex).trim();
        String to = details.substring(toIndex + "/to".length()).trim();
        requireDescription(description, CommandType.EVENT.getCommandWord());
        if (from.isEmpty() || to.isEmpty()) {
            throw new InvalidTaskFormatException(
                    "The event's flight times are incomplete. Fill in both '/from' and '/to' values!");
        }
        return new Event(description, from, to);
    }

    /** Returns the trimmed argument text following a recognized command word. */
    private static String argumentsAfter(String input, CommandType commandType) {
        return input.substring(commandType.getCommandWord().length()).trim();
    }

    /** Rejects an empty task description with a task-specific explanation. */
    private static void requireDescription(String description, String taskType)
            throws EmptyDescriptionException {
        if (description.isEmpty()) {
            throw new EmptyDescriptionException(taskType);
        }
    }
}
