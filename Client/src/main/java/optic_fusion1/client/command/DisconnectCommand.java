package optic_fusion1.client.command;

import java.util.List;
import optic_fusion1.client.Client;
import optic_fusion1.commandsystem.command.Command;
import optic_fusion1.commandsystem.command.CommandSender;

public class DisconnectCommand extends Command {

  public DisconnectCommand(String name) {
    super(name, 0x0);
  }

  @Override
  public boolean execute(CommandSender sender, String commandLabel, List<String> args) {
    ((Client) sender).getNetworkHandler().disconnect();
    System.out.println("You have been disconnected from the server");
    return true;
  }

}
