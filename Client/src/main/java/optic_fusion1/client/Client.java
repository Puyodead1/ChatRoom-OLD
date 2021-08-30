package optic_fusion1.client;

import jline.console.ConsoleReader;
import jline.console.UserInterruptException;
import lombok.Getter;
import lombok.Setter;
import optic_fusion1.client.logging.CustomLogger;
import optic_fusion1.client.logging.JDK14LoggerFactory;
import optic_fusion1.client.logging.LoggingOutputStream;
import optic_fusion1.client.network.SocketClient;
import optic_fusion1.client.network.listeners.PacketListener;
import optic_fusion1.packets.impl.HeartBeatPacket;
import optic_fusion1.packets.impl.MessagePacket;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ConnectException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    public static Logger logger;
    public ConsoleReader consoleReader;

    /**
     * Lock to protect the shutdown process from being triggered simultaneously
     * from multiple sources.
     */
    public final ReentrantLock shutdownLock = new ReentrantLock();

    /**
     * Current operation state.
     */
    public volatile boolean isRunning;

    public volatile boolean isConnected;

    public Client() {
        try {
            // This is a workaround for quite possibly the weirdest bug I have ever encountered in my life!
            // When jansi attempts to extract its natives, by default it tries to extract a specific version,
            // using the loading class's implementation version. Normally this works completely fine,
            // however when on Windows certain characters such as - and : can trigger special behaviour.
            // Furthermore this behaviour only occurs in specific combinations due to the parsing done by jansi.
            // For example test-test works fine, but test-test-test does not! In order to avoid this all together but
            // still keep our versions the same as they were, we set the override property to the essentially garbage version
            // BungeeCord. This version is only used when extracting the libraries to their temp folder.
            System.setProperty("library.jansi.version", "ChatRoom");

            AnsiConsole.systemInstall();
            consoleReader = new ConsoleReader();
            consoleReader.setExpandEvents(false);
            consoleReader.setHandleUserInterrupt(true);

            logger = new CustomLogger("ChatRoom", "client.log", consoleReader);
            JDK14LoggerFactory.LOGGER = logger;
            System.setErr(new PrintStream(new LoggingOutputStream(logger, Level.SEVERE), true));
            System.setOut(new PrintStream(new LoggingOutputStream(logger, Level.INFO), true));
            isRunning = true;

            while (!isConnected) {
                try {
                    String line = getConsoleReader().readLine("Input an ip and port (e.g. localhost:25565): ");
                    String[] split = line.split(":");
                    connect(split[0], Integer.parseInt(split[1]));
                } catch (ArrayIndexOutOfBoundsException e) {
                    logger.warning("Please enter a valid server address!");
                } catch (ConnectException e) {
                    logger.severe("Failed to connect to the server, check the address and try again!");
                }
            }
        } catch (UserInterruptException e) {
            threadStop("term", true);
        } catch (IOException e) {
            e.printStackTrace();
            threadStop("error", true);
        }
    }

    public void connect(String ip, int port) throws IOException {
        SocketClient client = new SocketClient(this, ip, port);
        client.getPacketRegister().addPacket("message", MessagePacket.class);
        client.getPacketRegister().addPacket("heartbeat", HeartBeatPacket.class);
        client.addEventListener(new PacketListener());
        client.connect();
    }


    public void threadStop(final String reason, boolean callSysExit) {
        // TODO: if connected, send disconnect packet with reason
        shutdownLock.lock();

        if (!isRunning) {
            shutdownLock.unlock();
            return;
        }
        isRunning = false;

        getLogger().info("Shutting down, goodbye!");

        for (Handler handler : getLogger().getHandlers()) {
            handler.close();
        }

        // Unlock the thread before optionally calling system exit, which might invoke this function again.
        // If that happens, the system will obtain the lock, and then see that isRunning == false and return without doing anything.
        shutdownLock.unlock();

        if (callSysExit) {
            System.exit(0);
        }
    }

    public static Logger getLogger() {
        return logger;
    }

    public ConsoleReader getConsoleReader() {
        return consoleReader;
    }

    public ReentrantLock getShutdownLock() {
        return shutdownLock;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}
