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
package optic_fusion1.server;

import jline.console.UserInterruptException;
import optic_fusion1.common.logging.ChatRoomLogger;
import optic_fusion1.packets.OpCode;
import optic_fusion1.packets.impl.MessagePacket;
import optic_fusion1.packets.serializers.Message;

import java.io.IOException;

public class Main {

  private static ChatRoomLogger logger;
  private static Server server;

  public static void main(String[] args) {
    try {
      try {
        logger = new ChatRoomLogger("ChatRoom Server", "server.log");
        server = new Server();

        String line;
        while (server.isRunning && (line = Main.getLogger().getConsoleReader().readLine("> ")) != null) {
          if (line.isEmpty() || line.isBlank()) {
            continue;
          }

          server.getSocketServer().broadcastPacket(new MessagePacket(OpCode.MESSAGE, new Message(null, line).serialize(), MessagePacket.MessageChatType.SERVER));
          logger.info(String.format("* You: %s", line));
        }
      } catch (UserInterruptException e) {
        server.threadStop("term", true);
      }
    } catch (IOException e) {
      System.err.println("Failed to create logger!");
      e.printStackTrace();
      System.exit(1);
    }
  }

  public static ChatRoomLogger getLogger() {
    return logger;
  }

  public static Server getServer() {
    return server;
  }
}
