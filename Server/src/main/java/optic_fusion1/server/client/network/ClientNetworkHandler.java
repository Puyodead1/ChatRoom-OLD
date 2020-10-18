package optic_fusion1.server.client.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import optic_fusion1.packet.ChatMessagePacket;
import optic_fusion1.packet.ClientDisconnectPacket;
import optic_fusion1.packet.ClientNicknameChangePacket;
import optic_fusion1.packet.Packet;
import static optic_fusion1.server.Main.LOGGER;
import optic_fusion1.server.client.Client;
import optic_fusion1.server.server.Server;
import optic_fusion1.server.server.network.ServerNetworkHandler;

public class ClientNetworkHandler extends Thread {

  private Socket socket;
  private final ObjectOutputStream output;
  private final ObjectInputStream input;
  private boolean running;
  private final ServerNetworkHandler networkHandler;
  private Client client;
  private Server server;

  public ClientNetworkHandler(Server server, Client client, Socket socket, ServerNetworkHandler networkHandler) throws IOException {
    setName("Server[Client:" + client.getId() + "]/NetworkHandler");
    this.client = client;
    this.socket = socket;
    this.networkHandler = networkHandler;
    output = new ObjectOutputStream(socket.getOutputStream());
    input = new ObjectInputStream(socket.getInputStream());
    running = true;
    this.server = server;
  }

  @Override
  public void run() {
    while (running) {
      try {
        Object object = input.readObject();
        if (object instanceof ChatMessagePacket) {
          ChatMessagePacket message = (ChatMessagePacket) object;
          if (message.getMessage().startsWith("/")) {
            if (!client.isLoggedIn() && !message.getMessage().startsWith("/register") && !message.getMessage().startsWith("/login")) {
              client.getClientNetworkHandler().sendPacket(new ChatMessagePacket("You need to /login before you can run commands"));
              continue;
            }
            if (!server.getCommandHandler().executeCommand(client, message.getMessage().substring(1))) {
              client.getClientNetworkHandler().sendPacket(new ChatMessagePacket("Couldn't run the command " + message.getMessage()));
              continue;
            }
            if (!message.getMessage().startsWith("/register") && !message.getMessage().startsWith("/login")) {
              LOGGER.info(client.getNickname() + " ran the command " + message.getMessage());
            }
            continue;
          }
          server.broadcastMessage(message);
          LOGGER.info(client.getNickname() + " said " + message.getMessage());
        }
        if (object instanceof ClientNicknameChangePacket) {
          if (!client.isLoggedIn()) {
            client.getClientNetworkHandler().sendPacket(new ChatMessagePacket("You need to /login before you can set your nickname"));
            continue;
          }
          String nickname = ((ClientNicknameChangePacket) object).getNickName();
          if (server.isNicknameInUse(nickname)) {
            String message = "The nickname " + nickname + " is already being used";
            sendPacket(new ChatMessagePacket(message));
            LOGGER.info(message);
            continue;
          }
          client.setNickname(nickname);
          sendPacket(new ChatMessagePacket("Your nickname has been set"));
          LOGGER.info("Changed nickname to " + nickname);
        }
        if (object instanceof ClientDisconnectPacket) {
          disconnect();
        }
      } catch (IOException | ClassNotFoundException ex) {
        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        try {
          disconnect();
        } catch (IOException ex1) {
          Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex1);
        }
      }
    }
  }

  public void disconnect() throws IOException {
    running = false;
    server.removeClient(client.getUniqueId());
    output.close();
    input.close();
    socket.close();
    client.setLoggedIn(false);
    socket = null;
    client = null;
    try {
      join();
    } catch (InterruptedException ex) {
      Logger.getLogger(ClientNetworkHandler.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public void sendPacket(Packet packet) {
    try {
      output.writeObject(packet);
      output.reset();
      output.flush();
    } catch (IOException ex) {
      Logger.getLogger(ClientNetworkHandler.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

}
