package optic_fusion1.packet;

public class ChatMessagePacket extends Packet {

  private final String message;

  public ChatMessagePacket(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

}
