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
import optic_fusion1.packets.serializers.Message;
import optic_fusion1.server.network.ClientConnection;
import optic_fusion1.server.network.SocketServer;
import optic_fusion1.server.network.events.CommandEvent;

import static optic_fusion1.server.Main.LOGGER;

public class PacketListener implements ServerEventListener {

    private final SocketServer server;

    public PacketListener(SocketServer server) {
        this.server = server;
    }

    @Override
    public void onPacketReceive(ClientConnection client, IPacket packet) {
        if (packet instanceof MessagePacket) {
            MessagePacket messagePacket = (MessagePacket) packet;
            OpCode opCode = messagePacket.getOpCode();
            LOGGER.info("Type: " + opCode + "; Message: " + messagePacket.getMessage());

            if (opCode.equals(OpCode.MESSAGE)) {
                Message message = Message.deserialize(messagePacket.getMessage());
                if (message.getContent().startsWith("/")) {
                    EventManager.call(new CommandEvent(client, message.getContent().substring(1)));
                } else if (!client.isLoggedIn()) {
                    client.sendPacket(new MessagePacket(OpCode.LOGIN_REQUIRED, "", MessagePacket.MessageChatType.SYSTEM));
                } else {
                    server.broadcastPacket(new MessagePacket(OpCode.MESSAGE, new Message(message.getClient(), message.getContent()).serialize(), MessagePacket.MessageChatType.USER));
                    System.out.println(client.getUsername() + ": " + messagePacket.getMessage());
                }
            }
        }
    }

}
