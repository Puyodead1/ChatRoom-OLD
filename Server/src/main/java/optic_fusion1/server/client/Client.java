package optic_fusion1.server.client;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import optic_fusion1.commandsystem.command.CommandSender;
import optic_fusion1.packet.ChatMessagePacket;
import optic_fusion1.server.Server;
import static optic_fusion1.server.Server.LOGGER;
import optic_fusion1.server.client.network.ClientNetworkHandler;

public class Client implements CommandSender {

  private static UUID uniqueId;
  private int clientId;
  private Server server;
  private Socket socket;
  private boolean loggedIn;
  private String nickname = "";
  private String username = "";
  private ClientNetworkHandler clientNetworkHandler;

  public Client(Server server, Socket socket, int clientId) {
    this.server = server;
    this.socket = socket;
    this.clientId = clientId;
    try {
      (clientNetworkHandler = new ClientNetworkHandler(server, this, socket)).start();
    } catch (IOException ex) {
      Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public void login(String username) {
    this.username = username;
    uniqueId = server.getDatabase().getUUID(username);
    loggedIn = true;
    server.getClientManager().addClient(this);
  }

  public void logout() {
    username = "";
    uniqueId = null;
    loggedIn = false;
    server.getClientManager().removeClient(uniqueId);
  }

  public void setNickname(String nickname) {
    String oldNickname = this.nickname.isEmpty() ? username : this.nickname;
    this.nickname = nickname;
    server.getDatabase().updateNickname(uniqueId, nickname);
    LOGGER.info(oldNickname + " changed their name to " + nickname);
  }

  @Override
  public void sendMessage(String message) {
    clientNetworkHandler.sendPacket(new ChatMessagePacket(message));
  }

  public void sendMessage(Client target, String message) {
    target.getClientNetworkHandler().sendPacket(new ChatMessagePacket(getEffectiveName() + ": " + message));
  }

  public int getClientId() {
    return clientId;
  }

  public Server getServer() {
    return server;
  }

  public Socket getSocket() {
    return socket;
  }

  public boolean isLoggedIn() {
    return loggedIn;
  }

  public String getNickname() {
    return nickname;
  }

  public String getUsername() {
    return username;
  }

  public ClientNetworkHandler getClientNetworkHandler() {
    return clientNetworkHandler;
  }

  public UUID getUniqueId() {
    return uniqueId;
  }

  public String getEffectiveName() {
    return nickname.isEmpty() ? username : nickname;
  }

}
