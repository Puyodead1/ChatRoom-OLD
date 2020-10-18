package optic_fusion1.server.client;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import optic_fusion1.commandsystem.command.CommandSender;
import static optic_fusion1.server.Main.LOGGER;
import optic_fusion1.server.client.network.ClientNetworkHandler;
import optic_fusion1.server.server.Server;
import optic_fusion1.server.server.network.ServerNetworkHandler;

public class Client implements CommandSender {

  private static UUID uniqueId;
  private String nickname = "Client";
  private final ClientNetworkHandler clientNetworkHandler;
  private final int id;
  private boolean loggedIn;
  private Server server;

  public Client(Server server, Socket socket, ServerNetworkHandler networkHandler, int id) throws IOException {
    this.id = id;
    this.server = server;
    (clientNetworkHandler = new ClientNetworkHandler(server, this, socket, networkHandler)).start();
    nickname = "Client#" + id;
  }

  public String getNickname() {
    return nickname;
  }
  
  public void setUniqueId(String username){
    uniqueId = server.getDatabase().getUUID(username);
  }

  public void setNickname(String nickname) {
    String oldNick = nickname;
    this.nickname = nickname;
    server.getDatabase().updateNickname(uniqueId, nickname);
    LOGGER.info(oldNick + "changed their name to " + nickname);
  }

  public UUID getUniqueId() {
    return uniqueId;
  }

  public ClientNetworkHandler getClientNetworkHandler() {
    return clientNetworkHandler;
  }

  public int getId() {
    return id;
  }

  public boolean isLoggedIn() {
    return loggedIn;
  }

  public void setLoggedIn(boolean loggedIn) {
    this.loggedIn = loggedIn;
    if(!loggedIn){
      uniqueId = null;
    }
  }

}
