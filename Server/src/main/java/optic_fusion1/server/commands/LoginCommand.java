package optic_fusion1.server.commands;

import java.util.List;
import java.util.concurrent.TimeUnit;
import optic_fusion1.server.Database;
import optic_fusion1.server.network.ClientConnection;
import optic_fusion1.server.network.SocketServer;
import optic_fusion1.commandsystem.command.Command;
import optic_fusion1.commandsystem.command.CommandSender;
import optic_fusion1.packets.impl.MessagePacket;

public class LoginCommand extends Command {
  
  private int loginAttempts = 0;
  private SocketServer server;
  private Database database;
  
  public LoginCommand(SocketServer server) {
    super("login", 0x0);
    this.server = server;
    database = server.getDatabase();
  }
  
  @Override
  public boolean execute(CommandSender sender, String commandLabel, List<String> args) {
    ClientConnection client = (ClientConnection) sender;
    if (loginAttempts == 3) {
      client.sendMessage("You need to wait 10 seconds before trying to login again");
      return true;
    }
    if (args.size() != 2) {
      client.sendMessage("/login <username> <password>");
      return true;
    }
    if (client.isLoggedIn()) {
      client.sendMessage("You're already logged in");
      return true;
    }
    String username = args.get(0);
    String password = args.get(1);
    if (!database.containsUser(username)) {
      client.sendMessage("The username " + username + " doesn't exist");
      ratelimit(client);
      return true;
    }
    if (!database.isPasswordCorrect(username, password)) {
      client.sendMessage("Incorrect username or password");
      ratelimit(client);
      return true;
    }
    client.login(username);
    server.broadcastPacket(new MessagePacket(MessagePacket.Type.SYSTEM, username + " has logged in"));
    return true;
  }
  
  private void ratelimit(ClientConnection client) {
    loginAttempts++;
    if (loginAttempts == 3) {
      server.getExecutorService().schedule(() -> {
        loginAttempts = 0;
        client.sendMessage("You can try to login again");
      }, 10, TimeUnit.SECONDS);
    }
  }
  
}
