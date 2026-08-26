import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Checks that valid persisted tasks can be reconstructed from disk.
 */
public class StorageTest {
    /**
     * Loads a representative storage file and checks its task types and states.
     *
     * @param args command-line arguments; they are not used
     * @throws Exception if the fixture cannot be written or read
     */
    public static void main(String[] args) throws Exception {
        Path dataFile = Path.of("_temp", "storage-test-data.txt");
        Files.createDirectories(dataFile.getParent());
        Files.write(dataFile, List.of(
                "T | 1 | read book",
                "D | 0 | return book | June 6th",
                "E | 1 | project meeting | Aug 6th 2pm | 4pm"));

        List<Task> tasks = new Storage(dataFile).load();

        require(tasks.size() == 3, "Expected three loaded tasks");
        require(tasks.get(0) instanceof ToDo, "Expected the first task to be a todo");
        require(tasks.get(1) instanceof Deadline, "Expected the second task to be a deadline");
        require(tasks.get(2) instanceof Event, "Expected the third task to be an event");
        require(tasks.get(0).toString().equals("[T][X] read book"), "Todo data was not restored");
        require(tasks.get(1).toString().equals("[D][ ] return book (by: June 6th)"),
                "Deadline data was not restored");
        require(tasks.get(2).toString().equals(
                "[E][X] project meeting (from: Aug 6th 2pm to: 4pm)"),
                "Event data was not restored");

        System.out.println("[PASS] Storage loaded todo, deadline, and event data");
    }

    /**
     * Fails the test with a useful message when a condition is false.
     *
     * @param condition condition that must hold
     * @param message explanation shown when the condition fails
     */
    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
