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
package optic_fusion1.server.network.listeners;

import optic_fusion1.packets.OpCode;
import optic_fusion1.packets.impl.MessagePacket;
import optic_fusion1.server.Main;
import optic_fusion1.server.network.ClientConnection;
import optic_fusion1.server.network.SocketServer;

public class ConnectionListener implements ServerEventListener {

  private final SocketServer server;

  public ConnectionListener(SocketServer server) {
    this.server = server;
  }

  @Override
  public void onSocketConnectionEstablished(ClientConnection client) {
    Main.getLogger().info(String.format("New connection from %s", client.getAddress()));
    if (server.isLoginRequired() && !client.isLoggedIn()) {
      client.sendPacket(new MessagePacket(OpCode.LOGIN_REQUIRED, "", MessagePacket.MessageChatType.SYSTEM));
    }
  }

  @Override
  public void onSocketPreConnect(ClientConnection client) {
  }

  @Override
  public void onSocketDisconnect(ClientConnection clientConnection) {
    if (clientConnection.isLoggedIn()) {
      Main.getLogger().info(String.format("%s has disconnected from %s", clientConnection.getUsername(), clientConnection.getAddress()));
      server.broadcastPacket(new MessagePacket(OpCode.DISCONNECT, clientConnection.getClient().serialize(), MessagePacket.MessageChatType.SYSTEM));
    } else {
      Main.getLogger().info(String.format("A User has disconnected from %s", clientConnection.getAddress()));
    }
  }

}
