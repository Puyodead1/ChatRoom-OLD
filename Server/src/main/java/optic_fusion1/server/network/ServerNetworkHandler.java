package optic_fusion1.server.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import optic_fusion1.server.Server;
import static optic_fusion1.server.Server.LOGGER;
import optic_fusion1.server.client.Client;
import optic_fusion1.server.client.ClientManager;

public class ServerNetworkHandler extends Thread {

  private Server server;
  private ServerSocket serverSocket;
  private int currentClientID = 0;
  private ClientManager clientManager;

  public ServerNetworkHandler(Server server, InetAddress address, int port) {
    setName("Server/ServerNetworkHandler");
    this.server = server;
    this.clientManager = server.getClientManager();
    try {
      serverSocket = new ServerSocket(port, 0, address);
    } catch (IOException ex) {
      Logger.getLogger(ServerNetworkHandler.class.getName()).log(Level.SEVERE, null, ex);
    }
    LOGGER.info("Started a server on port " + port);
  }

  @Override
  public void run() {
    while (server.isRunning()) {
      try {
        Socket clientSocket = serverSocket.accept();
        if (clientSocket == null) {
          continue;
        }
        Client client = new Client(server, clientSocket, currentClientID);
        currentClientID++;
        LOGGER.info("Client " + client.getClientId() + " connected IP:" + client.getSocket().getInetAddress().getHostAddress());
      } catch (IOException ex) {
        Logger.getLogger(ServerNetworkHandler.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  public ClientManager getClientManager() {
    return clientManager;
  }

}
