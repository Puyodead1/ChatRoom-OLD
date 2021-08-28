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

import static optic_fusion1.client.Main.LOGGER;
import optic_fusion1.packets.IPacket;
import optic_fusion1.packets.impl.MessagePacket;

public class PacketListener implements ClientEventListener {

  @Override
  public void onPacketReceive(IPacket packet) {
    if (packet instanceof MessagePacket) {
      MessagePacket p = (MessagePacket) packet;
      LOGGER.info(p.getMessage());
    }
  }

}
