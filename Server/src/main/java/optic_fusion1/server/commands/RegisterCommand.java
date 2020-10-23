package optic_fusion1.server.commands;

import java.util.List;
import java.util.UUID;
import optic_fusion1.commandsystem.command.Command;
import optic_fusion1.commandsystem.command.CommandSender;
import optic_fusion1.packet.ChatMessagePacket;
import optic_fusion1.server.Database;
import optic_fusion1.server.Server;
import optic_fusion1.server.client.Client;
import optic_fusion1.server.utils.BCrypt;

public class RegisterCommand extends Command {

  private Server server;

  public RegisterCommand(Server server, String name) {
    super(name);
    this.server = server;
  }

  @Override
  public boolean execute(CommandSender sender, String commandLabel, List<String> args) {
    if (sender instanceof ConsoleSender) {
      System.out.println("Only clients can run this command");
      return true;
    }
    Client client = (Client) sender;
    if (args.size() != 2) {
      client.getClientNetworkHandler().sendPacket(new ChatMessagePacket("/register <username> <password>"));
      return true;
    }
    String userName = args.get(0);
    String password = args.get(1);
    Database database = server.getDatabase();
    if (database.containsUser(userName)) {
      client.getClientNetworkHandler().sendPacket(new ChatMessagePacket("The username '" + userName + "' is already taken"));
      System.out.println(userName + " is already set");
      return true;
    }
    database.insertUser(userName, UUID.randomUUID(), BCrypt.hashpw(password, BCrypt.gensalt()));
    client.getClientNetworkHandler().sendPacket(new ChatMessagePacket("Registed the username " + userName));
    System.out.println("Registered username " + userName);
    return true;
  }

}
