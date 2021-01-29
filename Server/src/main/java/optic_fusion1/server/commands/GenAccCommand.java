package optic_fusion1.server.commands;

import java.util.List;
import optic_fusion1.commandsystem.command.Command;
import optic_fusion1.commandsystem.command.CommandSender;
import optic_fusion1.server.Server;
import optic_fusion1.server.client.Client;
import optic_fusion1.server.utils.RandomString;

public class GenAccCommand extends Command {

  private Server server;

  public GenAccCommand(Server server, String name) {
    super(name, 0x0);
    this.server = server;
  }

  @Override
  public boolean execute(CommandSender sender, String commandLabel, List<String> args) {
    RandomString randomString = new RandomString();
    String username = randomString.nextString();
    String password = randomString.nextString();
    Client client = (Client) sender;
    boolean created = server.createAccount(client, username, password);
    if (created) {
      client.sendMessage("Username: " + username + " Password: " + password);
    }
    return true;
  }

}
