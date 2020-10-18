package optic_fusion1.client;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import optic_fusion1.client.client.Client;
import optic_fusion1.client.command.DisconnectCommand;
import optic_fusion1.client.command.SetNickname;
import optic_fusion1.client.command.ShrugCommand;
import optic_fusion1.commandsystem.CommandHandler;
import optic_fusion1.commandsystem.command.Command;

public class Main extends Thread {

  public static final CommandHandler COMMAND_HANDLER = new CommandHandler();

  public Main() {
    setName("Client/Main");
  }

  @Override
  public void run() {
    try {
      System.setOut(new PrintStream(System.out, true, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new InternalError("VM does not support mandatory encoding UTF-8");
    }

    registerCommands();

    Scanner scanner = new Scanner(System.in);

    System.out.println("Enter an IP:");
    String ip = scanner.nextLine();
    System.out.println("Enter a Port:");
    int port = 25565;

    try {
      port = scanner.nextInt();
    } catch (Exception e) {

    }

    new Client(ip, port);
  }

  private void registerCommands() {
    registerCommand(new DisconnectCommand("disconnect"));
    registerCommand(new SetNickname("setnickname"));
    registerCommand(new ShrugCommand("shrug"));
  }

  private void registerCommand(Command command) {
    COMMAND_HANDLER.addCommand(command);
  }

  public static void main(String[] args) {
    new Main().start();
  }

}
