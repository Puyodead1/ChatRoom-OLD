package optic_fusion1.server.network.listeners.event;

import optic_fusion1.server.network.events.CommandEvent;
import net.lenni0451.asmevents.event.EventTarget;
import optic_fusion1.commandsystem.CommandHandler;

public class CommandEventListener {

  private CommandHandler commandHandler;

  public CommandEventListener(CommandHandler commandHandler) {
    this.commandHandler = commandHandler;
  }

  @EventTarget()
  public void onEvent(CommandEvent event) {
    if (event.isCancelled()) {
      return;
    }
    commandHandler.executeCommand(event.getSender(), event.getCommand());
  }
}
