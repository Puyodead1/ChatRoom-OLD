package optic_fusion1.server.input;

import optic_fusion1.server.Server;
import optic_fusion1.server.commands.ConsoleSender;

public abstract class InputHandler extends Thread {

  private static final ConsoleSender CONSOLE_SENDER = new ConsoleSender();
  private Server server;

  public InputHandler(Server server) {
    this.server = server;
  }

  public Server getServer() {
    return server;
  }

  public ConsoleSender getConsoleSender() {
    return CONSOLE_SENDER;
  }

}
