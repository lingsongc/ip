package soar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import soar.storage.Storage;

/**
 * Tests the command-response boundary shared by the JavaFX and console interfaces.
 */
public class SoarTest {
    private Path testDirectory;

    /** Creates an isolated relative storage directory. */
    @BeforeEach
    public void setUp() throws IOException {
        Path testRoot = Path.of("build", "junit-soar");
        Files.createDirectories(testRoot);
        testDirectory = Files.createTempDirectory(testRoot, "case-");
    }

    /** Removes generated task data. */
    @AfterEach
    public void tearDown() throws IOException {
        if (testDirectory == null || !Files.exists(testDirectory)) {
            return;
        }
        try (var paths = Files.walk(testDirectory)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        }
    }

    /** Verifies GUI responses use command behavior without console separators. */
    @Test
    public void getResponse_commandsAndErrors_returnsUnframedMessages() {
        Soar soar = createSoar();

        assertEquals("Got it. I've added this task:\n"
                + "  [T][ ] read book\n"
                + "Now you have 1 tasks in the list.", soar.getResponse("todo read book"));
        assertEquals("Here are the tasks in your list:\n"
                + "1.[T][ ] read book", soar.getResponse("list"));
        assertEquals("That command is on an unfamiliar flight path. Try list, find, date, todo, deadline, "
                + "event, mark, unmark, delete, or bye to keep flying high!", soar.getResponse("unknown"));
    }

    /** Verifies GUI commands persist state for a later Soar session. */
    @Test
    public void getResponse_mutatingCommands_persistsForNextSession() {
        Soar firstSession = createSoar();
        firstSession.getResponse("todo persistent task");
        firstSession.getResponse("mark 1");

        Soar nextSession = createSoar();

        assertEquals("Here are the tasks in your list:\n"
                + "1.[T][X] persistent task", nextSession.getResponse("list"));
    }

    /** Creates a Soar session using this test's isolated data file. */
    private Soar createSoar() {
        return new Soar(new Storage(testDirectory.resolve("tasks.txt")));
    }
}
