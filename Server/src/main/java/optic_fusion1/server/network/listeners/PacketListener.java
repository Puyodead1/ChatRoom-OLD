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

import net.lenni0451.asmevents.EventManager;
import optic_fusion1.packets.IPacket;
import optic_fusion1.packets.OpCode;
import optic_fusion1.packets.impl.MessagePacket;
import optic_fusion1.packets.serializers.Client;
import optic_fusion1.packets.serializers.Message;
import optic_fusion1.server.Main;
import optic_fusion1.server.network.ClientConnection;
import optic_fusion1.server.network.SocketServer;
import optic_fusion1.server.network.events.CommandEvent;

public class PacketListener implements ServerEventListener {

  private final SocketServer server;

  public PacketListener(SocketServer server) {
    this.server = server;
  }

  @Override
  public void onPacketReceive(ClientConnection clientConnection, IPacket packet) {
    if (packet instanceof MessagePacket) {
      MessagePacket messagePacket = (MessagePacket) packet;
      OpCode opCode = messagePacket.getOpCode();
      Main.getLogger().info("Type: " + opCode + "; Message: " + messagePacket.getMessage());

      switch (opCode) {
        case MESSAGE -> {
          Message message = Message.deserialize(messagePacket.getMessage());
          Main.getLogger().info(message.getContent());
          if (message.getContent().startsWith("/")) {
            EventManager.call(new CommandEvent(clientConnection, message.getContent().substring(1)));
          } else if (!clientConnection.isLoggedIn()) {
            clientConnection.sendPacket(new MessagePacket(OpCode.LOGIN_REQUIRED, "", MessagePacket.MessageChatType.SYSTEM));
          } else {
            server.broadcastPacket(new MessagePacket(OpCode.MESSAGE, new Message(message.getClient(), message.getContent()).serialize(), MessagePacket.MessageChatType.USER));
            Main.getLogger().info(clientConnection.getUsername() + ": " + messagePacket.getMessage());
          }
        }
        case DISCONNECT -> {
          Client client = Client.deserialize(messagePacket.getMessage());
          Main.getLogger().info(String.format("== %s has disconnected", client.getUsername()));
        }
        case CONNECT ->
          Main.getLogger().info("CONNECT");
        case UNKNOWN ->
          Main.getLogger().info("UNKNOWN");
      }
    }
  }

}
