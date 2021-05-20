package optic_fusion1.server.network;

import optic_fusion1.packets.IPacket;
import optic_fusion1.packets.utils.RSACrypter;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.UUID;
import optic_fusion1.commandsystem.command.CommandSender;
import optic_fusion1.packets.impl.MessagePacket;

public class ClientConnection implements CommandSender {

  private final SocketServer server;
  private final Socket socket;
  private final InetAddress address;
  private final DataInputStream dataInputStream;
  private final DataOutputStream dataOutputStream;

  private PrivateKey decryptionKey = null;
  private PublicKey encryptionKey = null;
  private int aesKeyLength;
  private boolean useEncryption;

  private long ping = -1;
  private boolean terminated = false;

  // Optic_Fusion1 start
  private boolean loggedIn;
  private UUID uniqueId;
  private String username;
  // Optic_Fusion1 end

  public ClientConnection(final SocketServer server, final Socket socket) {
    this.server = server;
    this.socket = socket;
    this.address = socket.getInetAddress();
    try {
      this.dataInputStream = new DataInputStream(socket.getInputStream());
      this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
    } catch (Exception e) {
      this.terminateConnection();
      throw new IllegalStateException("Socket is closed or not ready yet", e);
    }
    this.aesKeyLength = 256;
    this.useEncryption = true;
  }

  public Socket getSocket() {
    return this.socket;
  }

  public InetAddress getAddress() {
    return this.address;
  }

  public DataInputStream getInputStream() {
    return this.dataInputStream;
  }

  public DataOutputStream getOutputStream() {
    return this.dataOutputStream;
  }

  public boolean terminateConnection() {
    this.terminated = true;
    try {
      this.dataInputStream.close();
      this.dataOutputStream.close();
      this.socket.close();
      return true;
    } catch (Exception ignored) {
    }
    return false;
  }

  public void updatePing(final long ping) {
    this.ping = ping;
  }

  public long getPing() {
    return this.ping;
  }

  public void setDecryptionKey(final PrivateKey decryptionKey) {
    if (this.encryptionKey != null) {
      throw new IllegalStateException("Decryption key is already set");
    }

    this.decryptionKey = decryptionKey;
  }

  public PrivateKey getDecryptionKey() {
    return this.decryptionKey;
  }

  public void setEncryptionKey(final PublicKey encryptionKey) {
    if (this.encryptionKey != null) {
      throw new IllegalStateException("Encryption key is already set");
    }

    this.encryptionKey = encryptionKey;
  }

  public PublicKey getEncryptionKey() {
    return this.encryptionKey;
  }

  public void setAESKeyLength(final int aesKeyLength) {
    this.aesKeyLength = aesKeyLength;
  }

  public int getAESKeyLength() {
    return this.aesKeyLength;
  }

  public void useNoEncryption() {
    if (this.encryptionKey != null || this.decryptionKey != null) {
      throw new IllegalStateException("Encryption is already initialized");
    }

    this.useEncryption = false;
  }

  public boolean isUsingEncryption() {
    return this.useEncryption;
  }

  public void sendRawPacket(byte[] data) throws IOException {
    if (this.terminated) {
      throw new IllegalStateException("Client connection has been terminated");
    }

    if (this.encryptionKey != null && this.useEncryption) {
      try {
        data = RSACrypter.encrypt(this.encryptionKey, data, aesKeyLength);
      } catch (Exception e) {
        new IOException("Could not encrypt packet data for client " + this.socket.getInetAddress().getHostAddress(), e).printStackTrace();
      }
    }
    if (data.length > this.server.getMaxPacketSize()) {
      throw new RuntimeException("Packet size over maximum: " + data.length + " > " + this.server.getMaxPacketSize());
    }
    this.dataOutputStream.writeInt(data.length);
    this.dataOutputStream.write(data);
  }

  public void sendPacket(final IPacket packet) {
    if (this.terminated) {
      throw new IllegalStateException("Client connection has been terminated");
    }

    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(baos);
      dos.writeUTF(this.server.getPacketRegister().getPacketLabel(packet.getClass()));
      packet.writePacketData(dos);
      this.sendRawPacket(baos.toByteArray());
    } catch (Exception e) {
      new IOException("Could not serialize packet for " + this.address.getHostAddress(), e).printStackTrace();
    }
  }

  // Optic_Fusion1 - start
  @Override
  public void sendMessage(String message) {
    sendPacket(new MessagePacket(message));
  }

  public boolean isLoggedIn() {
    return loggedIn;
  }

  public void login(String username) {
    loggedIn = true;
    this.username = username;
    uniqueId = server.getDatabase().getUUID(username);
    sendPacket(new MessagePacket("You're now logged in"));
  }

  public void logout() {
    username = "";
    uniqueId = null;
    loggedIn = false;
  }

  /*
//  public void setNickname(String nickname) {
//    String oldNickname = this.nickname.isEmpty() ? username : this.nickname;
//    this.nickname = nickname;
//    server.getDatabase().updateNickname(uniqueId, nickname);
//    LOGGER.info(oldNickname + " changed their name to " + nickname);
//  }
   */
  public UUID getUniqueId() {
    return uniqueId;
  }

  public String getUsername() {
    return username;
  }

  // Optic_Fusion1 - end
}
