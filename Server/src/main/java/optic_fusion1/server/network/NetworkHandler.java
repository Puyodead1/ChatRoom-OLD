package optic_fusion1.server.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import static optic_fusion1.server.Main.LOGGER;
import optic_fusion1.server.client.Client;
import optic_fusion1.server.client.ClientList;

public class NetworkHandler extends Thread {

  private static final ClientList CLIENT_LIST = new ClientList();
  private final ServerSocket serverSocket;
  private int currentId = 0;

  public NetworkHandler(int port) throws IOException {
    setName("Server/NetworkHandler");
    (serverSocket = new ServerSocket(25565)).setPerformancePreferences(0, 2, 1);
    LOGGER.info("Started a server on port " + port);
  }

  @Override
  public void run() {
    while (true) {
      try {
        Socket socket = serverSocket.accept();
        if (socket == null) {
          continue;
        }
        Client client = new Client(socket, this, currentId);
        currentId++;
        CLIENT_LIST.addClient(client);
        LOGGER.info("Client " + client.getId() + " connected");
      } catch (IOException ex) {
        Logger.getLogger(NetworkHandler.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }
  
  public ClientList getClientList(){
    return CLIENT_LIST;
  }

}
