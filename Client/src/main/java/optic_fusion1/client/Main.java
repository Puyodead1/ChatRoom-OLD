package optic_fusion1.client;

import optic_fusion1.client.client.Client;
import optic_fusion1.client.command.DisconnectCommand;
import optic_fusion1.client.command.SetNickname;
import optic_fusion1.commandsystem.CommandHandler;
import optic_fusion1.commandsystem.command.Command;

public class Main extends Thread {

  public static final CommandHandler COMMAND_HANDLER = new CommandHandler();

  @Override
  public void run() {
    registerCommands();
    new Client();
  }

  private void registerCommands() {
    registerCommand(new DisconnectCommand("quit"));
    registerCommand(new SetNickname("setnickname"));
  }
  
  private void registerCommand(Command command){
    COMMAND_HANDLER.addCommand(command);
  }

  public static void main(String[] args) {
    new Main().start();
  }

}
