package optic_fusion1.client.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import static optic_fusion1.client.Main.COMMAND_HANDLER;
import optic_fusion1.client.client.Client;
import optic_fusion1.packet.ChatMessagePacket;
import optic_fusion1.packet.ClientDisconnectPacket;
import optic_fusion1.packet.Packet;

public class NetworkHandler extends Thread {

  private static final Scanner SCANNER = new Scanner(System.in);
  private Socket serverSocket;
  private ObjectOutputStream serverOutput;
  private ObjectInputStream serverInput;
  private boolean running;
  private Thread inputThread;
  private Thread outputThread;
  private Client client;

  public NetworkHandler(Client client, String ip, int port) throws IOException {
    setName("Client/NetworkHandler");
    this.client = client;
    try {
      serverSocket = new Socket(ip, port);
    } catch (IOException ex) {
      System.out.println("Couldn't connect to server, is it running?");
      return;
    }
    serverOutput = new ObjectOutputStream(serverSocket.getOutputStream());
    serverInput = new ObjectInputStream(serverSocket.getInputStream());
    running = true;
  }

  @Override
  public void run() {
    handleOutput();
    handleInput();
  }

  private void handleInput() {
    inputThread = new Thread() {
      @Override
      public void run() {
        while (running) {
          Object object = null;
          try {
            object = serverInput.readObject();
          } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(NetworkHandler.class.getName()).log(Level.SEVERE, null, ex);
            continue;
          }
          if (object == null) {
            continue;
          }
          if (object instanceof ChatMessagePacket) {
            System.out.println(((ChatMessagePacket) object).getMessage());
          }
        }
      }
    };
    inputThread.start();
  }

  private void handleOutput() {
    outputThread = new Thread() {
      @Override
      public void run() {
        while (running) {
          String nextLine = SCANNER.nextLine();
          if (nextLine == null || nextLine.isEmpty()) {
            continue;
          }
          if (nextLine.startsWith("/")) {
            nextLine = nextLine.substring(1);
            if (!COMMAND_HANDLER.executeCommand(client, nextLine)) {
              try {
                sendPacket(new ChatMessagePacket("/" + nextLine));
              } catch (IOException ex) {
                disconnect();
              }
              continue;
            }
            continue;
          }
          try {
            sendPacket(new ChatMessagePacket(nextLine));
          } catch (IOException ex) {
            disconnect();
          }
        }
      }
    };
    outputThread.start();
  }

  public void sendPacket(Packet packet) throws IOException {
    serverOutput.writeObject(packet);
    serverOutput.reset();
    serverOutput.flush();
  }

  public void disconnect() {
    try {
      sendPacket(new ClientDisconnectPacket());
      serverOutput.close();
      serverInput.close();
      serverSocket.close();
      running = false;
    } catch (IOException ex) {
      Logger.getLogger(NetworkHandler.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

}
