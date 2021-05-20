package optic_fusion1.packets.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import optic_fusion1.packets.IPacket;

public class MessagePacket implements IPacket {

  private String message;
  private Type type;

  public MessagePacket() {

  }

  public MessagePacket(String message) {
    this(Type.NORMAL, message);
  }

  public MessagePacket(Type type, String message) {
    this.type = type;
    this.message = message;
  }

  public enum Type {
    SYSTEM("System: "),
    NORMAL("");

    private String name;

    Type(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

  @Override
  public void writePacketData(DataOutputStream dataOutputStream) throws IOException {
    dataOutputStream.writeUTF(type.getName() + message);
  }

  @Override
  public void readPacketData(DataInputStream dataInputStream) throws IOException {
    message = dataInputStream.readUTF();
  }

  public String getMessage() {
    return message;
  }

}
