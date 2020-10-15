package optic_fusion1.client.command;

import java.io.IOException;
import java.util.List;
import optic_fusion1.client.client.Client;
import optic_fusion1.commandsystem.command.Command;
import optic_fusion1.commandsystem.command.CommandSender;
import optic_fusion1.packet.ClientNicknameChangePacket;

public class SetNickname extends Command {

  public SetNickname(String name) {
    super(name);
  }

  @Override
  public boolean execute(CommandSender sender, String commandLabel, List<String> args) {
    try {
      ((Client) sender).getNetworkHandler().sendPacket(new ClientNicknameChangePacket(args.get(0)));
    } catch (IOException ex) {
      return false;
    }
    return true;
  }

}
