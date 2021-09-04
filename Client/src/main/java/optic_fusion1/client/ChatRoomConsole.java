package optic_fusion1.client;

import net.minecrell.terminalconsole.SimpleTerminalConsole;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

public class ChatRoomConsole extends SimpleTerminalConsole {
    private static final Logger LOGGER = LogManager.getLogger();

//    private final Client client;

    public ChatRoomConsole() {
//        this.client = client;
    }

    @Override
    protected LineReader buildReader(LineReaderBuilder builder) {
        return super.buildReader(builder.appName("Common"));
    }

    @Override
    protected boolean isRunning() {
        return true;
    }

    @Override
    protected void runCommand(String s) {
        LOGGER.info(": " + s);
    }

    @Override
    protected void shutdown() {
       System.exit(0);
    }
}
