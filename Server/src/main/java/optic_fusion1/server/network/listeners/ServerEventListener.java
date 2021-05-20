package optic_fusion1.server.network.listeners;

import optic_fusion1.server.network.ClientConnection;
import optic_fusion1.packets.IPacket;

public interface ServerEventListener {

  default void onSocketPreConnect(final ClientConnection client) {
  }

  default void onSocketConnectionEstablished(final ClientConnection client) {
  }

  default void onSocketDisconnect(final ClientConnection client) {
  }

  default void onRawPacketReceive(final ClientConnection client, final byte[] packet) {
  }

  default void onPacketReceive(final ClientConnection client, final IPacket packet) {
  }

  default void onServerClose() {
  }

}
