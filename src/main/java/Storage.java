import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Saves the current task list to the local data file.
 */
public class Storage {
    /** Location of the task data file, relative to the project root. */
    private static final Path DATA_FILE = Path.of("data", "soar.txt");

    /**
     * Replaces the data file with a snapshot of the current task list.
     *
     * @param tasks tasks to save in their current order
     * @throws IOException if the data directory or file cannot be written
     */
    public void save(List<Task> tasks) throws IOException {
        Files.createDirectories(DATA_FILE.getParent());
        Files.write(DATA_FILE, tasks.stream().map(Task::toDataString).toList());
    }
}
