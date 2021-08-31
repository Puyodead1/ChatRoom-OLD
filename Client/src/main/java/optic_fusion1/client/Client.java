package optic_fusion1.client;

import jline.console.UserInterruptException;
import optic_fusion1.client.network.SocketClient;
import optic_fusion1.client.network.listeners.PacketListener;
import optic_fusion1.packets.impl.HeartBeatPacket;
import optic_fusion1.packets.impl.MessagePacket;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Handler;

public class Client {

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
            isRunning = true;

            while (!isConnected) {
                try {
                    String line = Main.getLogger().getConsoleReader().readLine("Input an ip and port (e.g. localhost:25565): ");
                    String[] split = line.split(":");
                    connect(split[0], Integer.parseInt(split[1]));
                } catch (ArrayIndexOutOfBoundsException e) {
                    Main.getLogger().warning("Please enter a valid server address!");
                } catch (IOException e) {
                    Main.getLogger().severe("Failed to connect to the server, check the address and try again!");
                }
            }
        } catch (UserInterruptException e) {
            threadStop("term", true);
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

        Main.getLogger().info("Shutting down, goodbye!");

        for (Handler handler : Main.getLogger().getHandlers()) {
            handler.close();
        }

        // Unlock the thread before optionally calling system exit, which might invoke this function again.
        // If that happens, the system will obtain the lock, and then see that isRunning == false and return without doing anything.
        shutdownLock.unlock();

        if (callSysExit) {
            System.exit(0);
        }
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
