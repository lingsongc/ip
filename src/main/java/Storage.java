import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
        this.dataFile = dataFile;
    }

    /**
     * Loads tasks from the data file in their saved order.
     *
     * @return tasks stored in the data file, or an empty list if it does not exist
     * @throws IOException if the data file cannot be read
     */
    public List<Task> load() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(dataFile)) {
            return tasks;
        }

        for (String line : Files.readAllLines(dataFile)) {
            tasks.add(parseTask(line));
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
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.write(dataFile, tasks.stream().map(Task::toDataString).toList());
    }

    /**
     * Converts one valid storage line back into its concrete task type.
     *
     * @param line serialized task from the data file
     * @return reconstructed task
     */
    private Task parseTask(String line) {
        String[] fields = line.split(" \\| ", -1);
        Task task;
        if (fields[0].equals("T")) {
            task = new ToDo(fields[2]);
        } else if (fields[0].equals("D")) {
            task = new Deadline(fields[2], fields[3]);
        } else {
            task = new Event(fields[2], fields[3], fields[4]);
        }

        if (fields[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }
}
