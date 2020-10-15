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
  private Socket clientSocket;
  private ObjectOutputStream output;
  private ObjectInputStream input;
  private boolean running;
  private Thread inputThread;
  private Client client;

  public NetworkHandler(Client client, String ip, int port) throws IOException {
    setName("Client/NetworkHandler");
    this.client = client;
    try {
      clientSocket = new Socket(ip, port);
    } catch (IOException ex) {
      System.out.println("Couldn't connect to server, is it running?");
      return;
    }
    output = new ObjectOutputStream(clientSocket.getOutputStream());
    input = new ObjectInputStream(clientSocket.getInputStream());
    running = true;
  }

  @Override
  public void run() {
    handleOutput();
    //TODO: Figure out why handleInput is broken
    handleInput();
  }

  private void handleInput() {
    inputThread = new Thread() {
      @Override
      public void run() {
        while (running) {
          Object object = null;
          try {
            object = input.readObject();
          } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(NetworkHandler.class.getName()).log(Level.SEVERE, null, ex);
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
    while (running) {
      String nextLine = SCANNER.nextLine();
      if (nextLine == null || nextLine.isEmpty()) {
        continue;
      }
      if (nextLine.startsWith("/")) {
        nextLine = nextLine.substring(1);
        if (!COMMAND_HANDLER.executeCommand(client, nextLine)) {
          System.out.println("Couldn't run the command " + nextLine);
        }
        return;
      }
      try {
        sendPacket(new ChatMessagePacket(nextLine));
      } catch (IOException ex) {
        Logger.getLogger(NetworkHandler.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  public void sendPacket(Packet packet) throws IOException {
    output.writeObject(packet);
    output.reset();
    output.flush();
  }

  public void disconnect() {
    try {
      sendPacket(new ClientDisconnectPacket());
      output.close();
      input.close();
      clientSocket.close();
      running = false;
    } catch (IOException ex) {
      Logger.getLogger(NetworkHandler.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

}
