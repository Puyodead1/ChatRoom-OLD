/*
* Copyright (C) 2021 Optic_Fusion1
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package optic_fusion1.server.commands;

import java.util.List;
import optic_fusion1.server.network.ClientConnection;
import optic_fusion1.server.network.SocketServer;
import optic_fusion1.server.utils.RandomString;
import optic_fusion1.commandsystem.command.Command;
import optic_fusion1.commandsystem.command.CommandSender;

public class GenAccCommand extends Command{

  private SocketServer server;
  
  public GenAccCommand(SocketServer server){
    super("genacc");
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
