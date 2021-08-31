package optic_fusion1.server;

import jline.console.UserInterruptException;
import net.lenni0451.asmevents.EventManager;
import optic_fusion1.packets.impl.HeartBeatPacket;
import optic_fusion1.packets.impl.MessagePacket;
import optic_fusion1.server.network.SocketServer;
import optic_fusion1.server.network.listeners.ConnectionListener;
import optic_fusion1.server.network.listeners.PacketListener;
import optic_fusion1.server.network.listeners.event.CommandEventListener;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Handler;

public class Server {

    /**
     * Lock to protect the shutdown process from being triggered simultaneously
     * from multiple sources.
     */
    public final ReentrantLock shutdownLock = new ReentrantLock();

    /**
     * Current operation state.
     */
    public volatile boolean isRunning;

    private SocketServer socketServer;

    public Server() throws IOException {
        isRunning = true;
        bind();
        Main.getLogger().info("Server started");
    }

    private void bind() throws IOException {
        socketServer = new SocketServer(this);
        socketServer.getPacketRegister().addPacket("message", MessagePacket.class);
        socketServer.getPacketRegister().addPacket("heartbeat", HeartBeatPacket.class);
        socketServer.addEventListener(new PacketListener(socketServer));
        socketServer.addEventListener(new ConnectionListener(socketServer));
        EventManager.register(new CommandEventListener(socketServer.getCommandHandler()));
        socketServer.bind();
    }

    public void threadStop(final String reason, boolean callSysExit) {
        // TODO: send disconnect packet to clients with reason?
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

    public SocketServer getSocketServer() {
        return socketServer;
    }
}
