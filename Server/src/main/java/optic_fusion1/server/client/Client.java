package optic_fusion1.server.client;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import static optic_fusion1.server.Main.LOGGER;
import optic_fusion1.server.client.network.ClientNetworkHandler;
import optic_fusion1.server.server.Server;
import optic_fusion1.server.server.network.ServerNetworkHandler;

public class Client {

  private static final UUID uniqueId = UUID.randomUUID();
  private String nickname = "Client";
  private final ClientNetworkHandler clientNetworkHandler;
  private final int id;

  public Client(Server server, Socket socket, ServerNetworkHandler networkHandler, int id) throws IOException {
    this.id = id;
    (clientNetworkHandler = new ClientNetworkHandler(server, this, socket, networkHandler)).start();
    nickname = "Client#" + id;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    LOGGER.info(this.nickname + "changed their name to " + nickname);
    this.nickname = nickname;
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

}
