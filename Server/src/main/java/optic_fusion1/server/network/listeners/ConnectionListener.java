package optic_fusion1.server.network.listeners;

import optic_fusion1.server.network.ClientConnection;
import optic_fusion1.server.network.SocketServer;
import optic_fusion1.packets.impl.MessagePacket;

public class ConnectionListener implements ServerEventListener {

  private SocketServer server;

  public ConnectionListener(SocketServer server) {
    this.server = server;
  }

  @Override
  public void onSocketConnectionEstablished(ClientConnection client) {
    if (server.isLoginRequired() && !client.isLoggedIn()) {
      client.sendMessage("You need to login before you're able to talk here");
    }
  }

  @Override
  public void onSocketDisconnect(ClientConnection client) {
    server.broadcastPacket(new MessagePacket(client.getUsername() + " has disconnected"));
  }

}
