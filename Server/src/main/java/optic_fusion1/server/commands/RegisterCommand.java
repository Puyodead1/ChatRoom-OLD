package optic_fusion1.server.commands;

import java.util.List;
import optic_fusion1.server.network.ClientConnection;
import optic_fusion1.server.network.SocketServer;
import optic_fusion1.commandsystem.command.Command;
import optic_fusion1.commandsystem.command.CommandSender;

public class RegisterCommand extends Command {

  private SocketServer server;

  public RegisterCommand(SocketServer server) {
    super("register", 0x0);
    this.server = server;
  }

  @Override
  public boolean execute(CommandSender sender, String commandLabel, List<String> args) {
    ClientConnection client = (ClientConnection) sender;
    if (args.size() != 2) {
      client.sendMessage("/register <username> <passwod>");
      return true;
    }
    server.createAccount(client, args.get(0), args.get(1));
    return true;
  }

}
