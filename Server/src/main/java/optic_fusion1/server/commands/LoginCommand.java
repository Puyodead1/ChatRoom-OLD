package optic_fusion1.server.commands;

import java.util.List;
import java.util.concurrent.TimeUnit;
import optic_fusion1.commandsystem.command.Command;
import optic_fusion1.commandsystem.command.CommandSender;
import optic_fusion1.packet.ChatMessagePacket;
import optic_fusion1.server.Database;
import optic_fusion1.server.Server;
import static optic_fusion1.server.Server.LOGGER;
import optic_fusion1.server.client.Client;

public class LoginCommand extends Command {

  private Server server;
  private int loginAttempts;
  private long timestamp;

  public LoginCommand(Server server, String name) {
    super(name, 0x0);
    this.server = server;
    setArgLength(2, 2);
  }

  @Override
  public boolean execute(CommandSender sender, String commandLabel, List<String> args) {
    if (sender instanceof ConsoleSender) {
      System.out.println("Only clients can run this command");
      return true;
    }
    Client client = (Client) sender;
    if (loginAttempts == 3) {
      client.getClientNetworkHandler().sendPacket(new ChatMessagePacket("You need to wait 10 seconds before trying to login again"));
      return true;
    }
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
      LOGGER.info(client.getClientId() + " tried to login with the username '" + userName + "' but it doesn't exist");
      return true;
    }
    if (database.isPasswordCorrect(userName, password)) {
      client.getClientNetworkHandler().sendPacket(new ChatMessagePacket("You're now logged in"));
      client.login(userName);
      LOGGER.info(client.getClientId() + " logged in with the username " + userName);
      return true;
    }
    client.getClientNetworkHandler().sendPacket(new ChatMessagePacket("Incorrect password"));
    LOGGER.info(client.getClientId() + " tried to login with the username " + userName + " but go the password wrong");
    loginAttempts++;
    if (loginAttempts == 3) {
      timestamp = System.currentTimeMillis();
      server.getExecutorService().schedule(() -> {
        loginAttempts = 0;
        client.getClientNetworkHandler().sendPacket(new ChatMessagePacket("You can try to login again"));
      }, 10, TimeUnit.SECONDS);
    }
    return true;
  }

}
