package optic_fusion1.server.server.commands;

import java.util.List;
import optic_fusion1.commandsystem.command.Command;
import optic_fusion1.commandsystem.command.CommandSender;
import optic_fusion1.packet.ChatMessagePacket;
import optic_fusion1.server.client.Client;
import optic_fusion1.server.server.Database;
import optic_fusion1.server.server.Server;

public class LoginCommand extends Command {

  private Server server;

  public LoginCommand(Server server, String name) {
    super(name);
    this.server = server;
  }

  @Override
  public boolean execute(CommandSender sender, String commandLabel, List<String> args) {
    Client client = (Client) sender;
    if (args.size() != 2) {
      client.getClientNetworkHandler().sendPacket(new ChatMessagePacket("/login <username> <password>"));
      return true;
    }
    if (client.isLoggedIn()) {
      client.getClientNetworkHandler().sendPacket(new ChatMessagePacket("You're already logged in"));
      return true;
    }
    String userName = args.get(0);
    String password = args.get(1);
    Database database = server.getDatabase();
    if (!database.containsUser(userName)) {
      client.getClientNetworkHandler().sendPacket(new ChatMessagePacket("The username " + userName + " doesn't exist"));
      return true;
    }
    if (database.isPasswordCorrect(userName, password)) {
      client.getClientNetworkHandler().sendPacket(new ChatMessagePacket("You're now logged in"));
      client.setLoggedIn(true);
      return true;
    }
    client.getClientNetworkHandler().sendPacket(new ChatMessagePacket("Incorrect password"));
    return true;
  }

}
