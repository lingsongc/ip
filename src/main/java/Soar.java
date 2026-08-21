import java.util.Scanner;

/**
 * Starts the Soar chatbot application.
 */
public class Soar {
    /** Width of the line used to frame the chatbot's messages. */
    private static final int SEPARATOR_WIDTH = 60;

    /**
     * Greets the user, echoes each command, and exits when the user enters {@code bye}.
     *
     * @param args command-line arguments; they are not used
     */
    public static void main(String[] args) {
        String banner = " ____                    \n"
                + "/ ___|  ___   __ _ _ __  \n"
                + "\\___ \\ / _ \\ / _` | '__| \n"
                + " ___) | (_) | (_| | |    \n"
                + "|____/ \\___/ \\__,_|_|    ";
        String separator = "_".repeat(SEPARATOR_WIDTH);

        System.out.println(separator);
        System.out.println(banner);
        System.out.println("Hey there! I'm Soar, your upbeat little sidekick!");
        System.out.println("What exciting thing can I help you tackle today?");
        System.out.println(separator);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(separator);

            if (command.equals("bye")) {
                System.out.println("Bye! Always soar towards your goals!");
                System.out.println(separator);
                break;
            }

            System.out.println(command);
            System.out.println(separator);
        }
    }
}
