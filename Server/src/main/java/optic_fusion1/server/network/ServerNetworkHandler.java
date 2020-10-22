package optic_fusion1.server.network;

import java.net.InetAddress;
import optic_fusion1.server.Server;

public class ServerNetworkHandler extends Thread {

  private Server server;

  public ServerNetworkHandler(Server server, InetAddress address, int port) {
    setName("Server/ServerNetworkHandler");
    this.server = server;
  }

  /*
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
        Client client = new Client(server, clientSocket, this, currentClientID);
        currentClientID++;
        clientManager.addClient(client);
        LOGGER.info("Client " + client.getId() + " connected");
      } catch (IOException ex) {
        Logger.getLogger(ServerNetworkHandler.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }
   */
}
