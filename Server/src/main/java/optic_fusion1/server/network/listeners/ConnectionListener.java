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

import optic_fusion1.server.network.ClientConnection;
import optic_fusion1.server.network.SocketServer;
import optic_fusion1.packets.impl.MessagePacket;

public class ConnectionListener implements ServerEventListener {

  private SocketServer server;

  public ConnectionListener(SocketServer server) {
    this.server = server;
  }

  @Override
  public void onSocketConnectionEstablished(ClientConnection client) {
    if (server.isLoginRequired() && !client.isLoggedIn()) {
      client.sendMessage("You need to login before you're able to talk here");
    }
  }

  @Override
  public void onSocketPreConnect(ClientConnection client) {
  }

  @Override
  public void onSocketDisconnect(ClientConnection client) {
    server.broadcastPacket(new MessagePacket(client.getUsername() + " has disconnected"));
  }

}
