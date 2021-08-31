package optic_fusion1.packets.serializers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.UUID;

public class Client {

  private static final HashMap<UUID, Client> clients = new HashMap<>();

  private final String username;
  private final UUID uuid;

  public Client(UUID uuid, String username) {
    this.uuid = uuid;
    this.username = username;

    Client.getClients().put(uuid, this);
  }

  /**
   * Serializes Client to JSON string
   *
   * @return JSON string
   */
  public String serialize() {
    JsonObject object = new JsonObject();
    object.addProperty("id", this.getUuid().toString());
    object.addProperty("username", this.getUsername());

    return object.toString();
  }

  /**
   * Deserializes a Client JSON string to a Client object
   *
   * @param string raw Client JSON string
   * @return new Client instance
   */
  public static Client deserialize(String string) {
    JsonParser parser = new JsonParser();
    JsonObject object = (JsonObject) parser.parse(string);

    UUID uuid = UUID.fromString(object.get("id").getAsString());
    String username = object.get("username").getAsString();

    // Tries to get existing client instance or returns a new one
    return Client.getClients().getOrDefault(uuid, new Client(uuid, username));
  }

  public String getUsername() {
    return username;
  }

  public UUID getUuid() {
    return uuid;
  }

  public static HashMap<UUID, Client> getClients() {
    return clients;
  }
}
