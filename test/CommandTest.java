import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Checks command behavior that is not practical to trigger in normal UI cases.
 */
public class CommandTest {
    /**
     * Verifies that an addition is rolled back when its save fails.
     *
     * @param args command-line arguments; they are not used
     * @throws Exception if the test fixture cannot be prepared or cleaned up
     */
    public static void main(String[] args) throws Exception {
        Path storageTarget = Path.of("_temp", "add-command-save-target");
        Files.deleteIfExists(storageTarget);
        Files.createDirectories(storageTarget);
        TaskList tasks = new TaskList();

        try {
            new AddCommand(new ToDo("must be rolled back"))
                    .execute(tasks, new Ui(), new Storage(storageTarget));
            throw new AssertionError("Adding should fail when the data-file path is a directory");
        } catch (StorageException expected) {
            require(tasks.size() == 0, "A failed addition remained in the task list");
            require(expected.getMessage().contains("change was not kept"),
                    "The save failure did not explain that the addition was rolled back");
        } finally {
            Files.deleteIfExists(storageTarget);
        }

        System.out.println("[PASS] AddCommand rolled back an addition after a save failure");
    }

    /** Fails with a useful message when a condition is false. */
    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
