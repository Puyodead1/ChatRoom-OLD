package optic_fusion1.server.input.impl;

import java.util.Scanner;
import optic_fusion1.server.Server;
import static optic_fusion1.server.Server.LOGGER;
import optic_fusion1.server.input.InputHandler;

public class SimpleInputHandler extends InputHandler {

  private Scanner scanner;

  public SimpleInputHandler(Server server) {
    super(server);
    scanner = new Scanner(System.in);
  }

  @Override
  public void run() {
    while (getServer().isRunning()) {
      String input = scanner.nextLine();
      if (input == null || input.isEmpty()) {
        continue;
      }
      if (!getServer().getCommandHandler().executeCommand(getConsoleSender(), input.substring(1))) {
        LOGGER.info("Couldn't run command " + input);
        return;
      }
    }
  }

}
