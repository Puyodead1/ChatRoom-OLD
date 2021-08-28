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

package optic_fusion1.client;

import optic_fusion1.client.network.SocketClient;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import optic_fusion1.client.network.listeners.PacketListener;
import optic_fusion1.packets.impl.HeartBeatPacket;
import optic_fusion1.packets.impl.MessagePacket;

public class Main {

  public static void main(String[] args) {
    SocketClient client = new SocketClient("localhost", 25565, false);
    client.getPacketRegister().addPacket("message", MessagePacket.class);
    client.getPacketRegister().addPacket("heartbeat", HeartBeatPacket.class);
    client.addEventListener(new PacketListener());
    try {
      client.connect();
    } catch (IOException ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

}
