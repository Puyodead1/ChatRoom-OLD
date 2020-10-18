package optic_fusion1.server;

import optic_fusion1.server.logging.CustomLogger;
import optic_fusion1.server.server.Server;

public class Main extends Thread {

  public static final CustomLogger LOGGER = new CustomLogger();
  
  @Override
  public void run() {
    setName("Server/Main");
    new Server().startServer();
  }

  public static void main(String[] args) {
    new Main().start();
  }

}
