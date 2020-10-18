package optic_fusion1.client;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import optic_fusion1.commandsystem.CommandHandler;
import optic_fusion1.commandsystem.command.Command;
import optic_fusion1.commandsystem.command.CommandSender;

public class Client extends Thread implements CommandSender {

  private static final CommandHandler COMMAND_HANDLER = new CommandHandler();
  private boolean running;

  private Client() {
    setName("Client/Client");
    try {
      System.setOut(new PrintStream(System.out, true, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new InternalError("VM does not support mandatory encoding UTF-8");
    }
  }

  public void startClient() {
    running = true;
  }

  private void registerCommands() {
    
  }

  private void registerCommand(Command command) {
    COMMAND_HANDLER.addCommand(command);
  }

  public void stopClient() {
  }

}
