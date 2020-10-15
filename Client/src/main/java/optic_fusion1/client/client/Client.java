package optic_fusion1.client.client;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import optic_fusion1.client.network.NetworkHandler;
import optic_fusion1.commandsystem.command.CommandSender;

public class Client implements CommandSender {

  private NetworkHandler networkHandler;

  public Client() {
    try {
      (networkHandler = new NetworkHandler(this, "127.0.0.1", 25565)).start();
    } catch (IOException ex) {
      Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  public NetworkHandler getNetworkHandler(){
    return networkHandler;
  }

}
