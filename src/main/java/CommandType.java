/**
 * Identifies a command that Soar accepts from the user.
 */
public enum CommandType {
    BYE("bye", false),
    LIST("list", false),
    MARK("mark", TaskAction.COMPLETE_FLIGHT),
    UNMARK("unmark", TaskAction.RETURN_TO_FLIGHT_PATH),
    DELETE("delete", TaskAction.SHOT_DOWN),
    TODO("todo", true),
    DEADLINE("deadline", true),
    EVENT("event", true);

    /** Word that identifies this command at the start of an input line. */
    private final String commandWord;

    /** Whether text may follow the command word. */
    private final boolean allowsArguments;

    /** Action used when a task-number command is missing its number. */
    private final TaskAction missingTaskNumberAction;

    /**
     * Creates a command that does not operate on a numbered task.
     *
     * @param commandWord word entered by the user for this command
     * @param allowsArguments whether text may follow the command word
     */
    CommandType(String commandWord, boolean allowsArguments) {
        this(commandWord, allowsArguments, null);
    }

    /**
     * Creates a command that operates on a numbered task.
     *
     * @param commandWord word entered by the user for this command
     * @param missingTaskNumberAction action described when its task number is missing
     */
    CommandType(String commandWord, TaskAction missingTaskNumberAction) {
        this(commandWord, true, missingTaskNumberAction);
    }

    /**
     * Creates a command with its complete parsing and error-message data.
     *
     * @param commandWord word entered by the user for this command
     * @param allowsArguments whether text may follow the command word
     * @param missingTaskNumberAction action described when its task number is missing
     */
    CommandType(String commandWord, boolean allowsArguments, TaskAction missingTaskNumberAction) {
        this.commandWord = commandWord;
        this.allowsArguments = allowsArguments;
        this.missingTaskNumberAction = missingTaskNumberAction;
    }

    /**
     * Finds the command whose word appears at the start of an input line.
     *
     * @param input complete input line entered by the user
     * @return matching command type
     * @throws UnknownCommandException if the first word is not a supported command
     */
    public static CommandType fromInput(String input) throws UnknownCommandException {
        for (CommandType commandType : values()) {
            if (input.equals(commandType.commandWord)
                    || (commandType.allowsArguments
                            && input.startsWith(commandType.commandWord + " "))) {
                return commandType;
            }
        }
        throw new UnknownCommandException();
    }

    /**
     * Returns the word entered by the user for this command.
     *
     * @return command word
     */
    public String getCommandWord() {
        return commandWord;
    }

    /**
     * Returns the action phrase for a missing task number.
     *
     * @return missing-number action, or {@code null} for other commands
     */
    public TaskAction getMissingTaskNumberAction() {
        return missingTaskNumberAction;
    }
}
