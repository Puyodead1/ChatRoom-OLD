package optic_fusion1.client.command;

import java.util.List;
import java.util.Scanner;
import optic_fusion1.client.Client;
import optic_fusion1.client.network.ClientNetworkHandler;
import optic_fusion1.commandsystem.command.Command;
import optic_fusion1.commandsystem.command.CommandSender;

public class ConnectCommand extends Command {

  private Scanner scanner;

  public ConnectCommand(String name) {
    super("Connect", 0x0);

  }

  @Override
  public boolean execute(CommandSender sender, String commandLabel, List<String> args) {
    Client client = (Client) sender;
    scanner = client.getScanner();
    enterDetails(client);
    return true;
  }

  private void enterDetails(Client client) {
    System.out.println("Enter a server ip");
    String serverIp = scanner.nextLine();
    System.out.println("Enter a server port");
    int port = 25565;
    try {
      port = scanner.nextInt();
    } catch (Exception e) {
      System.out.println("You didn't enter a valid port (it needs to be a number)");
      return;
    }
    ClientNetworkHandler networkHandler = client.getNetworkHandler();
    networkHandler.setServerIp(serverIp);
    networkHandler.setPort(port);
    networkHandler.connect();
  }

}
