package optic_fusion1.server.client;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;
import optic_fusion1.packet.ChatMessagePacket;

public class ClientManager {

  private static final HashMap<UUID, Client> CLIENTS = new HashMap<>();

  public Collection<Client> getClients() {
    return Collections.unmodifiableCollection(CLIENTS.values());
  }

  public Client getClientWithUUID(UUID uuid) {
    return CLIENTS.get(uuid);
  }

  public Client getClientWithUsername(String username) {
    for (Client client : CLIENTS.values()) {
      if (client.getUsername().equals(username)) {
        return client;
      }
    }
    return null;
  }

  public void addClient(Client client) {
    CLIENTS.put(client.getUniqueId(), client);
  }

  public void removeClient(UUID uniqueId) {
    CLIENTS.remove(uniqueId);
  }

  public boolean isNicknameInUse(String nickName) {
    return CLIENTS.values().stream().anyMatch(client -> (client.getNickname().equals(nickName)));
  }

  public void broadcastMessage(ChatMessagePacket message) {
    CLIENTS.values().forEach(client -> {
      client.getClientNetworkHandler().sendPacket(message);
    });
  }

}
