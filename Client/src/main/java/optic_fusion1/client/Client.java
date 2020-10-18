package optic_fusion1.client;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import optic_fusion1.client.command.DisconnectCommand;
import optic_fusion1.client.command.SetNickname;
import optic_fusion1.client.command.ShrugCommand;
import optic_fusion1.client.network.ClientNetworkHandler;
import optic_fusion1.commandsystem.CommandHandler;
import optic_fusion1.commandsystem.command.Command;
import optic_fusion1.commandsystem.command.CommandSender;

public class Client extends Thread implements CommandSender {

  private static final CommandHandler COMMAND_HANDLER = new CommandHandler();
  private static final Scanner SCANNER = new Scanner(System.in);
  private ClientNetworkHandler clientNetworkHandler;

  public Client() {
    setName("Client/Client");
    try {
      System.setOut(new PrintStream(System.out, true, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new InternalError("VM does not support mandatory encoding UTF-8");
    }
  }

  public void startClient() {
    registerCommands();
    requestServerCredentials();
  }

  private void requestServerCredentials() {
    System.out.println("Enter a server ip");
    String serverIp = SCANNER.nextLine();
    System.out.println("Enter a server port");
    int port = 25565;
    try {
      port = SCANNER.nextInt();
    } catch (Exception e) {

    }
    (clientNetworkHandler = new ClientNetworkHandler(this, serverIp, port)).start();
  }

  private void registerCommands() {
    registerCommand(new DisconnectCommand("disconnect"));
    registerCommand(new SetNickname("setnickname"));
    registerCommand(new ShrugCommand("shrug"));
  }

  private void registerCommand(Command command) {
    COMMAND_HANDLER.addCommand(command);
  }

  public CommandHandler getCommandHandler() {
    return COMMAND_HANDLER;
  }

  public Scanner getScanner() {
    return SCANNER;
  }

  public ClientNetworkHandler getNetworkHandler() {
    return clientNetworkHandler;
  }

}
