package optic_fusion1.server.commands;

import java.util.List;
import optic_fusion1.commandsystem.command.Command;
import optic_fusion1.commandsystem.command.CommandSender;
import optic_fusion1.packet.ChatMessagePacket;
import optic_fusion1.server.Server;
import static optic_fusion1.server.Server.LOGGER;
import optic_fusion1.server.client.Client;

public class RegisterCommand extends Command {

  private Server server;

  public RegisterCommand(Server server, String name) {
    super(name, 0x0);
    this.server = server;
    setArgLength(2, 2);
  }

  @Override
  public boolean execute(CommandSender sender, String commandLabel, List<String> args) {
    if (sender instanceof ConsoleSender) {
      LOGGER.info("Only clients can run this command");
      return true;
    }
    Client client = (Client) sender;
    if (args.size() != 2) {
      client.getClientNetworkHandler().sendPacket(new ChatMessagePacket("/register <username> <password>"));
      return true;
    }
    String userName = args.get(0);
    String password = args.get(1);
    server.createAccount(client, userName, password);
    return true;
  }

}
