package optic_fusion1.server.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import static optic_fusion1.server.Main.LOGGER;
import optic_fusion1.server.client.Client;
import optic_fusion1.server.server.network.ServerNetworkHandler;
import optic_fusion1.server.utils.Utils;

public class Server {

  private static final HashMap<UUID, Client> CLIENTS = new HashMap<>();
  private boolean running;
  private ServerNetworkHandler serverNetworkHandler;
  private Properties serverProperties = new Properties();

  public Server() {

  }

  public void startServer() {
    loadPropertiesFile();
    String serverIP = serverProperties.getProperty("server-ip");
    int serverPort = Integer.parseInt(serverProperties.getProperty("server-port", "25565"));
    InetAddress serverAddress = null;
    if (serverIP.length() > 0) {
      try {
        serverAddress = InetAddress.getByName(serverIP);
      } catch (UnknownHostException ex) {
        LOGGER.exception(ex);
        serverAddress = null;
      }
    }
    serverNetworkHandler = new ServerNetworkHandler(this, serverAddress, 0);
    LOGGER.info("Starting Server on " + ((serverIP.length() == 0) ? "*" : serverIP) + ":" + serverPort);
    serverNetworkHandler.start();
    running = true;
  }

  private void loadPropertiesFile() {
    File file = new File("server.properties");
    if (!file.exists()) {
      Utils.saveResource(file, "server.properties", false);
    }
    try {
      serverProperties.load(new FileInputStream(file));
    } catch (FileNotFoundException ex) {
      Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public void stopServer() {
    running = false;
  }

  public Collection<Client> getClients() {
    return CLIENTS.values();
  }

  public Client getClient(UUID uniqueId) {
    return CLIENTS.get(uniqueId);
  }

  public void addClient(Client client) {
    CLIENTS.putIfAbsent(client.getUniqueId(), client);
  }

  public void removeClient(UUID uniqueId) {
    CLIENTS.remove(uniqueId);
  }

  public boolean isNicknameInUse(String nickName) {
    return CLIENTS.values().stream().anyMatch(client -> (client.getNickname().equals(nickName)));
  }

  public boolean isRunning() {
    return running;
  }

}
