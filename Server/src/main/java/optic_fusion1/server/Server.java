package optic_fusion1.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import optic_fusion1.commandsystem.CommandHandler;
import optic_fusion1.commandsystem.command.Command;
import optic_fusion1.server.client.ClientManager;
import optic_fusion1.server.commands.LoginCommand;
import optic_fusion1.server.commands.RegisterCommand;
import optic_fusion1.server.logging.CustomLogger;
import optic_fusion1.server.network.ServerNetworkHandler;
import optic_fusion1.server.utils.Utils;

public class Server extends Thread {

  public static final CustomLogger LOGGER = new CustomLogger();
  private static final Database DATABASE = new Database();
  private static final CommandHandler COMMAND_HANDLER = new CommandHandler();
  private static final Properties SERVER_PROPERTIES = new Properties();
  private static final ClientManager CLIENT_MANAGER = new ClientManager();
  private ServerNetworkHandler serverNetworkHandler;
  private boolean running;

  public Server() {
    setName("Server/Main");
    try {
      System.setOut(new PrintStream(System.out, true, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new InternalError("VM does not support mandatory encoding UTF-8");
    }
  }

  @Override
  public void run() {
    running = true;
    loadPropertiesFile();
    registerCommands();
    startServerNetwork();
  }

  public void stopServer() {
    running = false;
    try {
      join();
    } catch (InterruptedException ex) {
      Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
    }
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

  private void registerCommands() {
    registerCommand(new LoginCommand(this, "login"));
    registerCommand(new RegisterCommand(this, "register"));
  }

  private void registerCommand(Command command) {
    COMMAND_HANDLER.addCommand(command);
  }

  private void startServerNetwork() {
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

  /*
   private static final ClientManager CLIENT_MANAGER = new ClientManager();
  private ServerNetworkHandler serverNetworkHandler;


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
   */
  public Database getDatabase() {
    return DATABASE;
  }

  public boolean isRunning() {
    return running;
  }

  public ServerNetworkHandler getServerNetworkHandler() {
    return serverNetworkHandler;
  }

  public ClientManager getClientManager() {
    return CLIENT_MANAGER;
  }
  
  public CommandHandler getCommandHandler(){
    return COMMAND_HANDLER;
  }

}
