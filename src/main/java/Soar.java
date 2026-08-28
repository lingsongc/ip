import java.io.IOException;
import java.nio.file.Path;

/**
 * Starts the Soar chatbot application.
 */
public class Soar {
    /**
     * Greets the user, stores tasks, lists or updates their status on request, and
     * exits when the user enters {@code bye}.
     *
     * @param args optional first argument overrides the default task data file
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();
        Storage storage;
        TaskList tasks;
        try {
            storage = args.length == 0 ? new Storage() : new Storage(Path.of(args[0]));
            tasks = new TaskList(storage.load());
        } catch (IOException | StorageException | IllegalArgumentException e) {
            ui.showLoadingError(e.getMessage());
            return;
        }

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            String command = ui.readCommand();
            try {
                Command executableCommand = Parser.parse(command, tasks.size());
                executableCommand.execute(tasks, ui, storage);
                isExit = executableCommand.isExit();
            } catch (SoarException e) {
                ui.showError(e.getMessage());
            }
        }
    }

}
