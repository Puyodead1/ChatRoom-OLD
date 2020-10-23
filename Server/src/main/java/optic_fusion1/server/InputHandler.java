package optic_fusion1.server;

import java.util.Scanner;
import static optic_fusion1.server.Server.LOGGER;
import optic_fusion1.server.commands.ConsoleSender;

public class InputHandler extends Thread {

  private static final ConsoleSender CONSOLE_SENDER = new ConsoleSender();
  private Server server;
  private Scanner scanner;

  public InputHandler(Server server) {
    this.server = server;
    scanner = new Scanner(System.in);
  }

  @Override
  public void run() {
    while (server.isRunning()) {
      String input = scanner.nextLine();
      if (input == null || input.isEmpty()) {
        continue;
      }
      if (!server.getCommandHandler().executeCommand(CONSOLE_SENDER, input.substring(1))) {
        LOGGER.info("Couldn't run command " + input);
        return;
      }
    }
  }

}
