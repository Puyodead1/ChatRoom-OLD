package optic_fusion1.client.command;

import java.util.List;
import optic_fusion1.client.client.Client;
import optic_fusion1.commandsystem.command.Command;
import optic_fusion1.commandsystem.command.CommandSender;

public class QuitCommand extends Command {

  public QuitCommand(String name) {
    super(name);
  }

  @Override
  public boolean execute(CommandSender sender, String commandLabel, List<String> args) {
    ((Client) sender).getNetworkHandler().disconnect();
    System.out.println("You have been disconnected from the server");
    return true;
  }

}
