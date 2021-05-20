package optic_fusion1.client.network.listeners;

import optic_fusion1.packets.IPacket;
import optic_fusion1.packets.impl.MessagePacket;

public class PacketListener implements ClientEventListener {

  @Override
  public void onPacketReceive(IPacket packet) {
    if (packet instanceof MessagePacket) {
      MessagePacket p = (MessagePacket) packet;
      System.out.println(p.getMessage());
    }
  }

}
