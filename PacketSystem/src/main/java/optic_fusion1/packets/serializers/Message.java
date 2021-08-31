package optic_fusion1.packets.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Message {

  private final Client client;
  private final String content;

  /**
   * Creates a new Message data packet
   *
   * @param client sending client
   * @param content content of the message
   */
  public Message(Client client, String content) {
    this.client = client;
    this.content = content;
  }

  public static Message deserialize(String string) {
    JsonParser parser = new JsonParser();
    JsonObject object = (JsonObject) parser.parse(string);

    JsonElement clientElement = object.get("client");

    Client client = clientElement.isJsonNull() ? null : Client.deserialize(clientElement.getAsString());
    String content = object.get("content").getAsString();
    return new Message(client, content);
  }

  public String serialize() {
    JsonObject object = new JsonObject();
    object.addProperty("client", this.getClient() == null ? null : this.getClient().serialize());
    object.addProperty("content", this.getContent());

    return object.toString();
  }

  public Client getClient() {
    return client;
  }

  public String getContent() {
    return content;
  }
}
