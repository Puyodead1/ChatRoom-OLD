package optic_fusion1.client.command;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import optic_fusion1.client.client.Client;
import optic_fusion1.commandsystem.command.Command;
import optic_fusion1.commandsystem.command.CommandSender;
import optic_fusion1.packet.ChatMessagePacket;

public class ShrugCommand extends Command {

  public ShrugCommand(String name) {
    super(name);
  }

  @Override
  public boolean execute(CommandSender sender, String commandLabel, List<String> args) {
    try {
      ((Client) sender).getNetworkHandler().sendPacket(new ChatMessagePacket("¯\\_(ツ)_/¯"));
    } catch (IOException ex) {
      Logger.getLogger(ShrugCommand.class.getName()).log(Level.SEVERE, null, ex);
    }
    return true;
  }

}
