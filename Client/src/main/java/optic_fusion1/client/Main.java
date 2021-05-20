package optic_fusion1.client;

import optic_fusion1.client.network.SocketClient;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import optic_fusion1.client.network.listeners.PacketListener;
import optic_fusion1.packets.impl.MessagePacket;

public class Main {

  public static void main(String[] args) {
    SocketClient client = new SocketClient("localhost", 25565, false);
    client.getPacketRegister().addPacket("message", MessagePacket.class);
    client.addEventListener(new PacketListener());
    try {
      client.connect();
    } catch (IOException ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

}
