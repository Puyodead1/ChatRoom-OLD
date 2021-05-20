package optic_fusion1.server.network.listeners;

import optic_fusion1.packets.IPacket;
import optic_fusion1.packets.impl.MessagePacket;
import optic_fusion1.server.network.ClientConnection;
import optic_fusion1.server.network.SocketServer;
import optic_fusion1.server.network.events.CommandEvent;
import net.lenni0451.asmevents.EventManager;

public class PacketListener implements ServerEventListener {

  private SocketServer server;
  
  public PacketListener(SocketServer server){
    this.server = server;
  }
  
  @Override
  public void onPacketReceive(ClientConnection client, IPacket packet) {
    if (packet instanceof MessagePacket) {
      String message = ((MessagePacket) packet).getMessage();
      if (message.startsWith("/")) {
        EventManager.call(new CommandEvent(client, message.substring(1)));
        return;
      }
      if (!client.isLoggedIn()) {
        client.sendMessage("You must be logged in to chat");
        return;
      }
      server.broadcastPacket(new MessagePacket(client.getUsername() + ": " + message));
      System.out.println(client.getUsername() + ": " + message);
    }
  }

}
