package optic_fusion1.server.input.impl;

import java.io.IOException;
import optic_fusion1.server.Server;
import static optic_fusion1.server.Server.LOGGER;
import optic_fusion1.server.input.InputHandler;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.MaskingCallback;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

/*
TODO: Example on this as needed, see the example here https://github.com/jline/jline3/blob/master/builtins/src/test/java/org/jline/example/Example.java
*/
public class JlineInputHandler extends InputHandler {

  private TerminalBuilder builder;
  private Terminal terminal;
  private LineReader reader;

  public JlineInputHandler(Server server) throws IOException {
    super(server);
    builder = TerminalBuilder.builder();
    terminal = builder.build();
    reader = LineReaderBuilder.builder().terminal(terminal).build();
  }

  @Override
  public void run() {
    while (getServer().isRunning()) {
      String input = reader.readLine(null, null, (MaskingCallback) null, null);
      if (input == null || input.isEmpty()) {
        continue;
      }
      if (!getServer().getCommandHandler().executeCommand(getConsoleSender(), input.substring(1))) {
        LOGGER.info("Couldn't run command " + input);
        continue;
      }
    }
  }

}
