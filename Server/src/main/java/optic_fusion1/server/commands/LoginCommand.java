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
import java.util.concurrent.TimeUnit;

import optic_fusion1.packets.OpCode;
import optic_fusion1.packets.serializers.Message;
import optic_fusion1.server.Database;
import optic_fusion1.server.network.ClientConnection;
import optic_fusion1.server.network.SocketServer;
import optic_fusion1.commandsystem.command.Command;
import optic_fusion1.commandsystem.command.CommandSender;
import optic_fusion1.packets.impl.MessagePacket;

import static optic_fusion1.server.Main.LOGGER;

public class LoginCommand extends Command {
  
  private int loginAttempts = 0;
  private SocketServer server;
  private Database database;
  
  public LoginCommand(SocketServer server) {
    super("login");
    this.server = server;
    database = server.getDatabase();
  }
  
  @Override
  public boolean execute(CommandSender sender, String commandLabel, List<String> args) {
    ClientConnection clientConnection = (ClientConnection) sender;
    if (loginAttempts == 3) {
      clientConnection.sendPacket(new MessagePacket(OpCode.MESSAGE, new Message(null, "[Login] You need to wait 10 seconds before trying to login again").serialize(), MessagePacket.MessageChatType.SYSTEM));
      return true;
    }
    if (args.size() != 2) {
      clientConnection.sendPacket(new MessagePacket(OpCode.MESSAGE, new Message(null, "[Login] Usage: /login <username> <password>").serialize(), MessagePacket.MessageChatType.SYSTEM));
      return true;
    }
    if (clientConnection.isLoggedIn()) {
      clientConnection.sendPacket(new MessagePacket(OpCode.MESSAGE, new Message(null, "[Login] You are already logged in").serialize(), MessagePacket.MessageChatType.SYSTEM));
      return true;
    }
    String username = args.get(0);
    String password = args.get(1);
    if (!database.containsUser(username)) {
      clientConnection.sendPacket(new MessagePacket(OpCode.MESSAGE, new Message(null, "[Login] Invalid username or password").serialize(), MessagePacket.MessageChatType.SYSTEM));
      ratelimit(clientConnection);
      return true;
    }
    if (!database.isPasswordCorrect(username, password)) {
      clientConnection.sendPacket(new MessagePacket(OpCode.MESSAGE, new Message(null, "[Login] Invalid username or password").serialize(), MessagePacket.MessageChatType.SYSTEM));
      ratelimit(clientConnection);
      return true;
    }
    clientConnection.login(username);
    return true;
  }
  
  private void ratelimit(ClientConnection client) {
    loginAttempts++;
    if (loginAttempts == 3) {
      server.getExecutorService().schedule(() -> {
        loginAttempts = 0;
        client.sendPacket(new MessagePacket(OpCode.MESSAGE, new Message(null, "You can try to login again").serialize(), MessagePacket.MessageChatType.SYSTEM));
      }, 10, TimeUnit.SECONDS);
    }
  }
  
}
