package optic_fusion1.server.server.commands;

import java.util.List;
import java.util.UUID;
import optic_fusion1.commandsystem.command.Command;
import optic_fusion1.commandsystem.command.CommandSender;
import optic_fusion1.server.server.Database;
import optic_fusion1.server.server.Server;
import optic_fusion1.server.utils.BCrypt;

public class RegisterCommand extends Command {

  private Server server;

  public RegisterCommand(Server server, String name) {
    super(name);
    this.server = server;
  }

  @Override
  public boolean execute(CommandSender sender, String commandLabel, List<String> args) {
    String userName = args.get(0);
    String password = args.get(1);
    Database database = server.getDatabase();
    if (database.containsUser(userName)) {
      System.out.println(userName + " is already set");
      return true;
    }
    database.insertUser(userName, UUID.randomUUID(), BCrypt.hashpw(password, BCrypt.gensalt()));
    return true;
  }

}
