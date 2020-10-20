package optic_fusion1.server.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import optic_fusion1.packet.ChatMessagePacket;

public class ClientManager {

  private static final HashMap<UUID, Client> CLIENTS = new HashMap<>();

  public Collection<Client> getClients() {
    return CLIENTS.values();
  }

  public Client getClient(UUID uniqueId) {
    return CLIENTS.get(uniqueId);
  }

  public void addClient(Client client) {
    CLIENTS.putIfAbsent(client.getUniqueId(), client);
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
