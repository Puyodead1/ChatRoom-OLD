package optic_fusion1.server.commands;

import optic_fusion1.commandsystem.command.CommandSender;

public class ConsoleSender implements CommandSender {

  @Override
  public void sendMessage(String message) {
    System.out.println(message);
  }

}
