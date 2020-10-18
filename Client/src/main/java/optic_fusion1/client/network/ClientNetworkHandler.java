package optic_fusion1.client.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import optic_fusion1.client.Client;
import optic_fusion1.packet.ChatMessagePacket;
import optic_fusion1.packet.ClientDisconnectPacket;
import optic_fusion1.packet.Packet;

public class ClientNetworkHandler extends Thread {

  private static final ExecutorService executors = Executors.newCachedThreadPool();
  private String serverIp;
  private int port;
  private int retryCount = 0;
  private Socket socket;
  private Client client;
  private boolean running;
  private ObjectOutputStream serverOutput;
  private ObjectInputStream serverInput;
  private Scanner scanner;

  public ClientNetworkHandler(Client client, String serverIp, int port) {
    this.client = client;
    this.serverIp = serverIp;
    this.port = port;
    reconnect();
    running = true;
    scanner = client.getScanner();
  }

  public void reconnect() {
    try {
      socket = new Socket(serverIp, port);
      serverOutput = new ObjectOutputStream(socket.getOutputStream());
      serverInput = new ObjectInputStream(socket.getInputStream());
      retryCount = 0;
      System.out.println("Connected to the server");
    } catch (IOException ex) {
      System.out.println("Couldn't connect to the server, is it running?");
    }
  }

  @Override
  public void run() {
    handleOutput();
    handleInput();
  }

  private void handleInput() {
    executors.submit(() -> {
      while (running) {
        try {
          Object object = serverInput.readObject();
          if (object == null) {
            continue;
          }
          if (object instanceof ChatMessagePacket) {
            System.out.println(((ChatMessagePacket) object).getMessage());
          }
        } catch (IOException | ClassNotFoundException ex) {
          Logger.getLogger(ClientNetworkHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    });
  }

  private void handleOutput() {
    executors.submit(() -> {
      while (running) {
        String input = scanner.nextLine();
        if (input == null || input.isEmpty()) {
          continue;
        }
        if (input.startsWith("/")) {
          String command = input.substring(1);
          if (!client.getCommandHandler().executeCommand(client, command)) {
            try {
              sendPacket(new ChatMessagePacket(command));
            } catch (IOException ex) {
              reconnect();
            }
          }
          continue;
        }
        try {
          sendPacket(new ChatMessagePacket(input));
        } catch (IOException ex) {
          reconnect();
        }
      }
    });
  }

  public void sendPacket(Packet packet) throws IOException {
    serverOutput.writeObject(packet);
    serverOutput.reset();
    serverOutput.flush();
  }

  public void disconnect() {
    try {
      if (retryCount == 0) {
        sendPacket(new ClientDisconnectPacket());
        serverOutput.close();
      }
      running = false;
      serverInput.close();
      socket.close();
      executors.shutdown();
    } catch (IOException ex) {
      Logger.getLogger(NetworkHandler.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

}
