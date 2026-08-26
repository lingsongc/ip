import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads and saves the task list using a local data file.
 */
public class Storage {
    /** Default data-file location, relative to the project root. */
    private static final Path DEFAULT_DATA_FILE = Path.of("data", "soar.txt");

    /** Data file used by this storage instance. */
    private final Path dataFile;

    /** Creates storage that uses the application's default data file. */
    public Storage() {
        this(DEFAULT_DATA_FILE);
    }

    /**
     * Creates storage that uses a specific data file.
     *
     * @param dataFile file from which tasks are loaded and to which they are saved
     */
    public Storage(Path dataFile) {
        if (dataFile == null || dataFile.getFileName() == null) {
            throw new IllegalArgumentException("The data file path must name a file");
        }
        this.dataFile = dataFile;
    }

    /**
     * Loads tasks from the data file in their saved order.
     *
     * @return tasks stored in the data file, or an empty list if it does not exist
     * @throws IOException if the data file cannot be read
     * @throws StorageException if a record in the data file is invalid
     */
    public List<Task> load() throws IOException, StorageException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(dataFile)) {
            return tasks;
        }

        List<String> lines = Files.readAllLines(dataFile);
        for (int i = 0; i < lines.size(); i++) {
            try {
                tasks.add(parseTask(lines.get(i)));
            } catch (StorageException e) {
                throw new StorageException("Invalid task data on line " + (i + 1) + ": " + e.getMessage());
            }
        }
        return tasks;
    }

    /**
     * Replaces the data file with a snapshot of the current task list.
     *
     * @param tasks tasks to save in their current order
     * @throws IOException if the data directory or file cannot be written
     */
    public void save(List<Task> tasks) throws IOException {
        Path parent = dataFile.getParent();
        Path saveDirectory = parent == null ? Path.of(".") : parent;
        Files.createDirectories(saveDirectory);

        String temporaryPrefix = dataFile.getFileName().toString();
        if (temporaryPrefix.length() < 3) {
            temporaryPrefix = (temporaryPrefix + "___").substring(0, 3);
        }
        Path temporaryFile = Files.createTempFile(saveDirectory, temporaryPrefix, ".tmp");
        try {
            Files.write(temporaryFile, tasks.stream().map(Task::toDataString).toList());
            try {
                Files.move(temporaryFile, dataFile, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, dataFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /**
     * Escapes characters that have structural meaning in the line-based format.
     *
     * @param value task text to encode
     * @return text safe to store as one field on one line
     */
    public static String escapeField(String value) {
        return value.replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("|", "\\|");
    }

    /**
     * Converts one valid storage line back into its concrete task type.
     *
     * @param line serialized task from the data file
     * @return reconstructed task
     * @throws StorageException if the line has an invalid type, status, or fields
     */
    private Task parseTask(String line) throws StorageException {
        if (line.isBlank()) {
            throw new StorageException("blank records are not allowed");
        }

        List<String> fields = splitFields(line);
        if (fields.size() < 2 || (!fields.get(1).equals("0") && !fields.get(1).equals("1"))) {
            throw new StorageException("completion status must be 0 or 1");
        }

        String type = fields.get(0);
        int expectedFields;
        if (type.equals("T")) {
            expectedFields = 3;
        } else if (type.equals("D")) {
            expectedFields = 4;
        } else if (type.equals("E")) {
            expectedFields = 5;
        } else {
            throw new StorageException("unknown task type '" + type + "'");
        }
        if (fields.size() != expectedFields) {
            throw new StorageException("task type " + type + " requires " + expectedFields
                    + " fields but found " + fields.size());
        }
        for (int i = 2; i < fields.size(); i++) {
            if (fields.get(i).isBlank()) {
                throw new StorageException("task text fields must not be empty");
            }
        }

        Task task;
        if (type.equals("T")) {
            task = new ToDo(fields.get(2));
        } else if (type.equals("D")) {
            task = new Deadline(fields.get(2), fields.get(3));
        } else {
            task = new Event(fields.get(2), fields.get(3), fields.get(4));
        }

        if (fields.get(1).equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Splits a record at unescaped {@code |} delimiters and restores escaped text.
     *
     * @param line serialized task record
     * @return decoded fields
     * @throws StorageException if an escape sequence is incomplete or unsupported
     */
    private List<String> splitFields(String line) throws StorageException {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);
            if (current == '\\') {
                if (i + 1 >= line.length()) {
                    throw new StorageException("incomplete escape sequence");
                }
                char escaped = line.charAt(++i);
                if (escaped == 'n') {
                    field.append('\n');
                } else if (escaped == 'r') {
                    field.append('\r');
                } else if (escaped == '\\' || escaped == '|') {
                    field.append(escaped);
                } else {
                    throw new StorageException("unsupported escape sequence \\" + escaped + "'");
                }
            } else if (current == '|' && i > 0 && i + 1 < line.length()
                    && line.charAt(i - 1) == ' ' && line.charAt(i + 1) == ' ') {
                field.setLength(Math.max(0, field.length() - 1));
                fields.add(field.toString());
                field.setLength(0);
                i++;
            } else {
                field.append(current);
            }
        }
        fields.add(field.toString());
        return fields;
    }
}
