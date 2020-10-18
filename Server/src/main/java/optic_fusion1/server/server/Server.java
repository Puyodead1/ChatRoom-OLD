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
import optic_fusion1.commandsystem.CommandHandler;
import optic_fusion1.commandsystem.command.Command;
import optic_fusion1.packet.ChatMessagePacket;
import static optic_fusion1.server.Main.LOGGER;
import optic_fusion1.server.client.Client;
import optic_fusion1.server.server.commands.LoginCommand;
import optic_fusion1.server.server.commands.RegisterCommand;
import optic_fusion1.server.server.network.ServerNetworkHandler;
import optic_fusion1.server.utils.Utils;

public class Server {

  private static final Database DATABASE = new Database();
  private static final CommandHandler COMMAND_HANDLER = new CommandHandler();
  private static final HashMap<UUID, Client> CLIENTS = new HashMap<>();
  private boolean running;
  private ServerNetworkHandler serverNetworkHandler;
  private Properties serverProperties = new Properties();

  public Server() {

  }

  public void startServer() {
    running = true;
    loadPropertiesFile();
    startServerNetworkHandler();
    registerCommands();
  }

  private void registerCommands() {
    registerCommand(new RegisterCommand(this, "register"));
    registerCommand(new LoginCommand(this, "login"));
  }

  private void registerCommand(Command command) {
    COMMAND_HANDLER.addCommand(command);
  }

  private void startServerNetworkHandler() {
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
    serverNetworkHandler = new ServerNetworkHandler(this, serverAddress, serverPort);
    LOGGER.info("Starting Server on " + ((serverIP.length() == 0) ? "*" : serverIP) + ":" + serverPort);
    serverNetworkHandler.start();
  }

  private void loadPropertiesFile() {
    File file = new File("server", "server.properties");
    if (!file.exists()) {
      Utils.saveResource(new File("server"), "server.properties", false);
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

  public void broadcastMessage(ChatMessagePacket message) {
    CLIENTS.values().forEach(client -> {
      client.getClientNetworkHandler().sendPacket(message);
    });
  }

  public boolean isRunning() {
    return running;
  }

  public CommandHandler getCommandHandler() {
    return COMMAND_HANDLER;
  }

  public Database getDatabase() {
    return DATABASE;
  }

}
