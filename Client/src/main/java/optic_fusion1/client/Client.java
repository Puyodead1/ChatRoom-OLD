package optic_fusion1.client;

import optic_fusion1.client.network.SocketClient;
import optic_fusion1.client.network.listeners.PacketListener;
import optic_fusion1.packets.impl.HeartBeatPacket;
import optic_fusion1.packets.impl.MessagePacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public class Client {

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Lock to protect the shutdown process from being triggered simultaneously from multiple sources.
     */
    public final ReentrantLock shutdownLock = new ReentrantLock();

    /**
     * Current operation state.
     */
    public volatile boolean isRunning;
    public volatile boolean isConnected;

    private final String ip, username, password;
    private final int port;

    public Client(String ip, int port, String username, String password) {
        isRunning = true;
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public void connect() throws IOException {
        SocketClient socketClient = new SocketClient(this, this.ip, this.port);
        socketClient.getPacketRegister().addPacket("message", MessagePacket.class);
        socketClient.getPacketRegister().addPacket("heartbeat", HeartBeatPacket.class);
        socketClient.addEventListener(new PacketListener());
        socketClient.connect();
    }

    public void threadStop(final String reason, boolean callSysExit) {
        LOGGER.info("thread stop called");
        shutdownLock.lock();
        try {
            // TODO: if connected, send disconnect packet with reason
            if (!isRunning) {
                shutdownLock.unlock();
                return;
            }
            isRunning = false;
            LOGGER.info("Shutting down, goodbye!");
            // Unlock the thread before optionally calling system exit, which might invoke this function again.
            // If that happens, the system will obtain the lock, and then see that isRunning == false and return without doing anything.
        } finally {
            shutdownLock.unlock();
        }

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
