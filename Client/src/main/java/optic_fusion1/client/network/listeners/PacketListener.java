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

package optic_fusion1.client.network.listeners;

import optic_fusion1.client.network.SocketClient;
import optic_fusion1.packets.serializers.Client;
import optic_fusion1.packets.IPacket;
import optic_fusion1.packets.OpCode;
import optic_fusion1.packets.impl.MessagePacket;
import optic_fusion1.packets.serializers.Message;

import static optic_fusion1.client.Main.LOGGER;

public class PacketListener implements ClientEventListener {

    @Override
    public void onPacketReceive(SocketClient socketClient, IPacket packet) {
        if (packet instanceof MessagePacket) {
            MessagePacket messagePacket = (MessagePacket) packet;
            OpCode opCode = messagePacket.getOpCode();
//            LOGGER.info("Type: " + opCode + "; Message: " + messagePacket.getMessage());

            switch (opCode) {
                case LOGIN_REQUIRED -> LOGGER.info("This server requires you to login before you can chat.");
                case LOGIN -> {
                    Client client = Client.deserialize(messagePacket.getMessage());
                    // TODO: we should probably track the known clients in a hashmap somewhere
                    LOGGER.info(String.format("== %s has joined ==", client.getUsername()));
                }
                case LOGGED_IN -> {
                    Client client = Client.deserialize(messagePacket.getMessage());
                    socketClient.setClient(client);
                    LOGGER.info(String.format("== Logged in as %s ==", client.getUsername()));
                }
                case DISCONNECT -> {
                    Client client = Client.deserialize(messagePacket.getMessage());
                    LOGGER.info(String.format("== %s has disconnected ==", client.getUsername()));
                }
                case MESSAGE -> {
                    Message message = Message.deserialize(messagePacket.getMessage());
                    switch(messagePacket.getChatType()) {
                        case USER -> {
                            if(message.getClient().getUuid().equals(socketClient.getClient().getUuid())) {
                                // the client receiving the message is also the client that sent the message
                                LOGGER.info(String.format("* You: %s", message.getContent()));
                            } else {
                                LOGGER.info(String.format("%s: %s", message.getClient().getUsername(), message.getContent()));
                            }
                        }
                        case SYSTEM -> {
                            LOGGER.info(String.format("[System]: %s", message.getContent()));
                        }
                    }
                }
                case CONNECT -> LOGGER.info("CONNECT");
                case UNKNOWN -> LOGGER.info("UNKNOWN");
            }
        }
    }

    @Override
    public void onConnectionEstablished() {
        System.out.println("=== Connected to server ===");
    }

    @Override
    public void onDisconnect() {
        System.out.println("=== Disconnected ===");
    }
}
