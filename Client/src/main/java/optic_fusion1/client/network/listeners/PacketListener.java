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

import optic_fusion1.client.Main;
import optic_fusion1.client.Utils;
import optic_fusion1.client.network.SocketClient;
import optic_fusion1.packets.IPacket;
import optic_fusion1.packets.OpCode;
import optic_fusion1.packets.impl.MessagePacket;
import optic_fusion1.packets.serializers.Client;
import optic_fusion1.packets.serializers.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URISyntaxException;

public class PacketListener implements ClientEventListener {

  private static final Logger LOGGER = LogManager.getLogger();

  @Override
  public void onPacketReceive(SocketClient socketClient, IPacket packet) {
    if (packet instanceof MessagePacket) {
      MessagePacket messagePacket = (MessagePacket) packet;
      OpCode opCode = messagePacket.getOpCode();
      switch (opCode) {
        case LOGIN_REQUIRED ->
         LOGGER.info("This server requires you to login before you can chat.");
        case LOGIN -> {
          Client client = Client.deserialize(messagePacket.getMessage());
          // TODO: we should probably track the known clients in a hashmap somewhere
          LOGGER.info(String.format("== %s has joined ==", client.getUsername()));
        }
        case LOGGED_IN -> {
          Client client = Client.deserialize(messagePacket.getMessage());
          socketClient.setClientUser(client);
          LOGGER.info(String.format("== Logged in as %s ==", client.getUsername()));
        }
        case DISCONNECT -> {
          Client client = Client.deserialize(messagePacket.getMessage());
          LOGGER.info(String.format("== %s has disconnected ==", client.getUsername()));
        }
        case MESSAGE -> {
          Message message = Message.deserialize(messagePacket.getMessage());
          switch (messagePacket.getChatType()) {
            case USER -> {
              if (message.getClient().getUuid().equals(socketClient.getClientUser().getUuid())) {
                // the client receiving the message is also the client that sent the message
                LOGGER.info(String.format("* You: %s", message.getContent()));
              } else {
                LOGGER.info(String.format("%s: %s", message.getClient().getUsername(), message.getContent()));
                // play notification sound
                try {
                  Utils.playSound("ping");
                } catch (UnsupportedAudioFileException | LineUnavailableException | IOException | InterruptedException | URISyntaxException e) {
                  e.printStackTrace();
                }
              }
            }
            case SYSTEM ->
                    LOGGER.info(String.format("[System]: %s", message.getContent()));

            case SERVER ->
                    LOGGER.info(String.format("Server: %s", message.getContent()));
          }
        }
        case CONNECT ->
                LOGGER.info("CONNECT");
        case UNKNOWN ->
                LOGGER.info("UNKNOWN");
      }
    }
  }

  @Override
  public void onConnectionEstablished(SocketClient socketClient) {
    socketClient.getClient().setConnected(true);
    LOGGER.info("=== Connected to server ===");
  }

  @Override
  public void onDisconnect(SocketClient socketClient) {
    socketClient.getClient().setConnected(false);
    LOGGER.info("=== Disconnected ===");
  }
}
