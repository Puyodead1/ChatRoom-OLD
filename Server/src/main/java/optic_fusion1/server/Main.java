package optic_fusion1.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import optic_fusion1.server.logging.CustomLogger;
import optic_fusion1.server.network.NetworkHandler;

public class Main extends Thread {

  public static final CustomLogger LOGGER = new CustomLogger();
  
  @Override
  public void run() {
    setName("Server/Main");
    try {
      new NetworkHandler(25565).start();
    } catch (IOException ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static void main(String[] args) {
    new Main().start();
  }

}
