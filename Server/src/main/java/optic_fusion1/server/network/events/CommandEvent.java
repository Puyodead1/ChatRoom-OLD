package optic_fusion1.server.network.events;

import optic_fusion1.server.network.ClientConnection;
import net.lenni0451.asmevents.event.IEvent;
import net.lenni0451.asmevents.event.types.ICancellableEvent;

public class CommandEvent implements IEvent, ICancellableEvent {

  private boolean cancelled = false;
  private String command;
  private ClientConnection sender;

  public CommandEvent(ClientConnection sender, String command) {
    this.sender = sender;
    this.command = command;
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }

  public ClientConnection getSender() {
    return sender;
  }

  public String getCommand() {
    return command;
  }

}
