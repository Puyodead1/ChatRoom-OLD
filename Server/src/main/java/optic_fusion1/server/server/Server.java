package optic_fusion1.server.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import optic_fusion1.commandsystem.CommandHandler;
import optic_fusion1.commandsystem.command.Command;
import static optic_fusion1.server.Main.LOGGER;
import optic_fusion1.server.client.ClientManager;
import optic_fusion1.server.server.commands.LoginCommand;
import optic_fusion1.server.server.commands.RegisterCommand;
import optic_fusion1.server.server.network.ServerNetworkHandler;
import optic_fusion1.server.utils.Utils;

public class Server {

  private static final ClientManager CLIENT_MANAGER = new ClientManager();
  private static final Database DATABASE = new Database();
  private static final CommandHandler COMMAND_HANDLER = new CommandHandler();
  private static final Properties SERVER_PROPERTIES = new Properties();
  private boolean running;
  private ServerNetworkHandler serverNetworkHandler;

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
    String serverIP = SERVER_PROPERTIES.getProperty("server-ip");
    int serverPort = Integer.parseInt(SERVER_PROPERTIES.getProperty("server-port", "25565"));
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
      SERVER_PROPERTIES.load(new FileInputStream(file));
    } catch (FileNotFoundException ex) {
      Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public void stopServer() {
    running = false;
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
  
  public ClientManager getClientManager(){
    return CLIENT_MANAGER;
  }

}
