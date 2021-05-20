package optic_fusion1.server;

import optic_fusion1.server.network.SocketServer;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import optic_fusion1.server.network.listeners.ConnectionListener;
import optic_fusion1.packets.impl.MessagePacket;
import optic_fusion1.server.network.listeners.PacketListener;
import optic_fusion1.server.network.listeners.event.CommandEventListener;
import net.lenni0451.asmevents.EventManager;

public class Main {

  public static void main(String[] args) {
    SocketServer server = new SocketServer();
    server.getPacketRegister().addPacket("message", MessagePacket.class);
    server.addEventListener(new PacketListener(server));
    server.addEventListener(new ConnectionListener(server));
    EventManager.register(new CommandEventListener(server.getCommandHandler()));
    try {
      server.bind();
    } catch (IOException ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
}
