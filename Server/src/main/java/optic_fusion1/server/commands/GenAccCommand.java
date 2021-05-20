package optic_fusion1.server.commands;

import java.util.List;
import optic_fusion1.server.network.ClientConnection;
import optic_fusion1.server.network.SocketServer;
import optic_fusion1.utils.RandomString;
import optic_fusion1.commandsystem.command.Command;
import optic_fusion1.commandsystem.command.CommandSender;

public class GenAccCommand extends Command{

  private SocketServer server;
  
  public GenAccCommand(SocketServer server){
    super("genacc", 0x0);
    this.server = server;
  }
  
  @Override
  public boolean execute(CommandSender sender, String commandLabel, List<String> args) {
    RandomString randomString = new RandomString();
    String username = randomString.nextString();
    String password = randomString.nextString();
    ClientConnection client = (ClientConnection) sender;
    boolean created = server.createAccount(client, username, password);
    if (created) {
      client.sendMessage("Username: " + username + " Password: " + password);
    }
    return true;
  }

}
