package optic_fusion1.client.network;

import java.io.IOException;
import java.net.Socket;

public class ClientNetworkHandler extends Thread {

  private String serverIp;
  private int port;
  private Socket socket;

  public ClientNetworkHandler(String serverIp, int port) {
    this.serverIp = serverIp;
    this.port = port;
    try {
      socket = new Socket(serverIp, port);
    } catch (IOException ex) {
      System.out.println("Couldn't connect to the server, is it running?");
    }
  }

}
