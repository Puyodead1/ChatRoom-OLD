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
import optic_fusion1.server.Server;
import static optic_fusion1.server.Server.LOGGER;
import optic_fusion1.server.client.Client;
import optic_fusion1.server.client.ClientManager;
import optic_fusion1.server.network.ServerNetworkHandler;

public class ClientNetworkHandler extends Thread {

  private Socket socket;
  private ObjectOutputStream output;
  private ObjectInputStream input;
  private ServerNetworkHandler serverNetworkHandler;
  private Client client;
  private Server server;
  private ClientManager clientManager;
  private boolean running;

  public ClientNetworkHandler(Server server, Client client, Socket socket) throws IOException {
    setName("Server[Client:" + client.getUsername() + "]/NetworkHandler");
    this.server = server;
    this.client = client;
    this.socket = socket;
    this.serverNetworkHandler = server.getServerNetworkHandler();
    output = new ObjectOutputStream(socket.getOutputStream());
    input = new ObjectInputStream(socket.getInputStream());
    clientManager = server.getClientManager();
  }

  @Override
  public void run() {
    running = true;
    while (running) {
      try {
        Object object = input.readObject();
        if (object == null) {
          continue;
        }
        if (object instanceof ChatMessagePacket) {
          handleChatMessagePacket((ChatMessagePacket) object);
          continue;
        }
        if (object instanceof ClientNicknameChangePacket) {
          handleClientNicknameChangePacket((ClientNicknameChangePacket) object);
        }
        if (object instanceof ClientDisconnectPacket) {
          disconnect();
        }
      } catch (IOException | ClassNotFoundException ex) {
        System.out.println(client.isLoggedIn() ? client.getUsername() : client.getClientId() + " most likely lost connection");
        try {
          disconnect();
        } catch (IOException ex1) {
          LOGGER.exception(ex);
        }
      }
    }
  }

  private void handleClientNicknameChangePacket(ClientNicknameChangePacket packet) {
    if (!client.isLoggedIn()) {
      sendPacket(new ChatMessagePacket("You need to login before you can set your nickname"));
      return;
    }
    String nickname = packet.getNickName();
    if (clientManager.isNicknameInUse(nickname)) {
      sendPacket(new ChatMessagePacket("The nickname " + nickname + " is already being used"));
      LOGGER.info("Tried to change nickname to " + nickname + " but it's already being used");
      return;
    }
    client.setNickname(nickname);
    sendPacket(new ChatMessagePacket("Your nickname has successfully been changed to " + nickname));
    LOGGER.info("Successfully changed nickname to " + nickname);
  }

  private void handleChatMessagePacket(ChatMessagePacket packet) {
    String message = packet.getMessage();
    if (message.startsWith("/")) {
      handleCommand(message);
      return;
    }
    if (!client.isLoggedIn()) {
      client.sendMessage("You need to be logged in to chat");
      return;
    }
    //Possibly a better way of doing this
    clientManager.broadcastMessage(new ChatMessagePacket(client.getEffectiveName() + ": " + message));
    LOGGER.info(client.getEffectiveName() + " said " + message);
  }

  private void handleCommand(String message) {
    if (!client.isLoggedIn() && !message.startsWith("/register") && !message.startsWith("/login")) {
      sendPacket(new ChatMessagePacket("You need to /login or /register before you can run commands"));
      return;
    }
    if (!server.getCommandHandler().executeCommand(client, message.substring(1))) {
      return;
    }
    if (!message.startsWith("/register") && !message.startsWith("/login")) {
      LOGGER.info(client.getUsername() + " ran the command " + message);
      return;
    }
  }

  public void disconnect() throws IOException {
    running = false;
    output.close();
    input.close();
    socket.close();
    client.logout();
    socket = null;
    client = null;
    try {
      join();
    } catch (InterruptedException ex) {
      LOGGER.exception(ex);
    }
  }

  public void sendPacket(Packet packet) {
    try {
      output.writeObject(packet);
      output.reset();
      output.flush();
    } catch (IOException ex) {
      LOGGER.exception(ex);
    }
  }
}
