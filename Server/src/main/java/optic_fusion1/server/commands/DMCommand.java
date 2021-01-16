package optic_fusion1.server.commands;

import java.util.List;
import optic_fusion1.commandsystem.command.Command;
import optic_fusion1.commandsystem.command.CommandSender;
import optic_fusion1.server.client.Client;
import org.apache.commons.lang3.StringUtils;

public class DMCommand extends Command {

  public DMCommand(String name) {
    super(name, 0x0);
  }

  @Override
  public boolean execute(CommandSender sender, String commandLabel, List<String> args) {
    String targetUsername = args.get(0);
    Client client = (Client) sender;
    Client target = client.getServer().getClientManager().getClientWithUsername(targetUsername);
    if (target == null) {
      client.sendMessage("The client " + targetUsername + " doesn't exist");
      return true;
    }
    client.sendMessage(target, StringUtils.join(args, " ", 1, args.size()));
    return true;
  }

}
