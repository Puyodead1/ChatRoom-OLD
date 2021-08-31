/*
 * Copyright (C) 2021 Optic_Fusion1
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package optic_fusion1.client.network;

import optic_fusion1.client.Main;
import optic_fusion1.client.network.listeners.ClientEventListener;
import optic_fusion1.commandsystem.command.CommandSender;
import optic_fusion1.packets.IPacket;
import optic_fusion1.packets.OpCode;
import optic_fusion1.packets.PacketRegister;
import optic_fusion1.packets.impl.MessagePacket;
import optic_fusion1.packets.impl.PingPacket;
import optic_fusion1.packets.serializers.Client;
import optic_fusion1.packets.serializers.Message;
import optic_fusion1.packets.utils.RSACrypter;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SocketClient implements CommandSender {

    private final String ip;
    private final int port;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private final int maxPacketSize = 32767;
    private final boolean useEncryption = true;
    private optic_fusion1.packets.serializers.Client clientUser;
    private final optic_fusion1.client.Client client;

    private Thread packetListener;
    private final List<ClientEventListener> eventListener;

    private PublicKey encryptionKey;
    private PrivateKey decryptionKey;

    private final PacketRegister packetRegister;

    public SocketClient(final optic_fusion1.client.Client client, final String ip, final int port) {
        this.ip = ip;
        this.port = port;
        this.client = client;

        this.eventListener = new CopyOnWriteArrayList<>();
        this.packetRegister = new PacketRegister();
    }

    public void connect() throws IOException {
        if (this.isConnected()) {
            throw new IllegalStateException("Client socket is already connected to address " + this.ip);
        }

        this.socket = new Socket();
        this.socket.setTcpNoDelay(true);
        this.socket.connect(new InetSocketAddress(this.ip, this.port));
        this.dataInputStream = new DataInputStream(this.socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
        this.packetListener = new Thread(() -> {
            while (!this.packetListener.isInterrupted() && this.socket.isConnected()) {
                try {
                    int packetLength = this.dataInputStream.readInt();
                    if (packetLength > this.maxPacketSize) {
                        Main.getLogger().warning("Server packet is over max size of " + maxPacketSize);
                        try {
                            dataInputStream.skipBytes(packetLength);
                        } catch (Exception e) {
                            new IOException("Could not skip bytes for too large packet", e).printStackTrace();
                            break;
                        }
                        continue;
                    }
                    if (packetLength < 0) {
                        throw new EOFException();
                    }
                    byte[] packet = new byte[packetLength];
                    dataInputStream.read(packet);

                    this.onPacketReceive(packet);
                } catch (EOFException | SocketException | SocketTimeoutException e) {
                    break;
                } catch (Throwable e) {
                    new IOException("Could not receive packet", e).printStackTrace();
                    break;
                }
            }
            this.onDisconnect();
        });
        this.packetListener.start();

        { //Encryption
            if (!this.useEncryption) {
                this.sendRawPacket(new byte[]{0});
            } else {
                int rsaKeyLength = RSACrypter.getRSAKeyLength();

                try {
                    KeyPair keyPair = RSACrypter.generateKeyPair(rsaKeyLength);
                    this.decryptionKey = keyPair.getPrivate();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    DataOutputStream dos = new DataOutputStream(baos);
                    dos.writeInt(rsaKeyLength);
                    dos.writeInt(RSACrypter.getAESKeyLength());
                    dos.writeInt(keyPair.getPublic().getEncoded().length);
                    dos.write(keyPair.getPublic().getEncoded());
                    this.sendRawPacket(baos.toByteArray());
                } catch (Exception e) {
                    new IOException("Could not create encryption key for server", e).printStackTrace();
                    this.disconnect();
                }
            }
        }

        { //Call event
            for (ClientEventListener clientEventListener : this.eventListener.toArray(new ClientEventListener[0])) {
                try {
                    clientEventListener.onPreConnect(this);
                    if (!this.useEncryption) {
                        clientEventListener.onConnectionEstablished(this);
                    }
                } catch (Throwable t) {
                    new Exception("Unhandled exception in client event listener", t).printStackTrace();
                }
            }
        }
        handleInput();
    }

    public void disconnect() {
        try {
            this.socket.shutdownInput();
            this.socket.close();
        } catch (Exception ignored) {
        }
    }

    public boolean isConnected() {
        return this.socket != null && this.socket.isConnected() && this.packetListener.isAlive() && !this.packetListener.isInterrupted();
    }

    public void addEventListener(final ClientEventListener clientEventListener) {
        this.eventListener.add(clientEventListener);
    }

    private void onDisconnect() {
        try {
            this.socket.close();
        } catch (Exception ignored) {
        }
        this.packetListener.interrupt();

        this.encryptionKey = null;
        this.decryptionKey = null;

        { //Call event
            for (ClientEventListener clientEventListener : this.eventListener.toArray(new ClientEventListener[0])) {
                try {
                    clientEventListener.onDisconnect(this);
                } catch (Throwable t) {
                    new Exception("Unhandled exception in client event listener", t).printStackTrace();
                }
            }
        }
    }

    private void onPacketReceive(byte[] packet) {
        if (this.encryptionKey == null && this.useEncryption) {
            try {
                this.encryptionKey = RSACrypter.initPublicKey(packet);

                { //Call event
                    for (ClientEventListener clientEventListener : this.eventListener.toArray(new ClientEventListener[0])) {
                        try {
                            clientEventListener.onConnectionEstablished(this);
                        } catch (Throwable t) {
                            new Exception("Unhandled exception in client event listener", t).printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                new IOException("Could not create encryption key", e).printStackTrace();
                this.disconnect();
            }
            return;
        }

        if (this.decryptionKey != null) {
            try {
                packet = RSACrypter.decrypt(this.decryptionKey, packet);
            } catch (Exception e) {
                new IOException("Could not decrypt packet data", e).printStackTrace();
            }
        }

        try {
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(packet));
            String packetLabel = dis.readUTF();
            Class<? extends IPacket> packetClass = this.packetRegister.getPacketClass(packetLabel);
            IPacket packetObject = packetClass.newInstance();
            packetObject.readPacketData(dis);

            if (packetObject instanceof PingPacket) {
                this.sendPacket(packetObject);
                return;
            }

            { //Call event
                for (ClientEventListener clientEventListener : this.eventListener.toArray(new ClientEventListener[0])) {
                    try {
                        clientEventListener.onPacketReceive(this, packetObject);
                    } catch (Throwable t) {
                        new Exception("Unhandled exception in client event listener", t).printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            { //Call event
                for (ClientEventListener clientEventListener : this.eventListener.toArray(new ClientEventListener[0])) {
                    try {
                        clientEventListener.onRawPacketReceive(packet);
                    } catch (Throwable t) {
                        new Exception("Unhandled exception in client event listener", t).printStackTrace();
                    }
                }
            }
        }
    }

    public void sendRawPacket(byte[] data) throws IOException {
        if (!this.isConnected()) {
            throw new IllegalStateException("Client is not connected to a server");
        }

        if (this.encryptionKey != null && this.useEncryption) {
            try {
                data = RSACrypter.encrypt(this.encryptionKey, data);
            } catch (Exception e) {
                new IOException("Could not decrypt packet data", e).printStackTrace();
            }
        }
        if (data.length > this.maxPacketSize) {
            throw new RuntimeException("Packet size over maximum: " + data.length + " > " + this.maxPacketSize);
        }
        this.dataOutputStream.writeInt(data.length);
        this.dataOutputStream.write(data);
    }

    public void sendPacket(final IPacket packet) {
        if (!this.isConnected()) {
            throw new IllegalStateException("Client is not connected to a server");
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeUTF(this.packetRegister.getPacketLabel(packet.getClass()));
            packet.writePacketData(dos);
            this.sendRawPacket(baos.toByteArray());
        } catch (Exception e) {
            new IOException("Could not serialize packet", e).printStackTrace();
            disconnect();
        }
    }

    @Override
    public void sendMessage(String message) {
        Main.getLogger().info(message);
    }

    public void handleInput() throws IOException {
        String line;
        while (client.isRunning() && (line = Main.getLogger().getConsoleReader().readLine("> ")) != null) {
            if(line.isEmpty() || line.isBlank()) continue;

            sendPacket(new MessagePacket(OpCode.MESSAGE, new Message(getClientUser(), line).serialize(), MessagePacket.MessageChatType.USER));
        }
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public Socket getSocket() {
        return socket;
    }

    public DataInputStream getDataInputStream() {
        return dataInputStream;
    }

    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    public int getMaxPacketSize() {
        return maxPacketSize;
    }

    public boolean isUseEncryption() {
        return useEncryption;
    }

    public Client getClientUser() {
        return clientUser;
    }

    public optic_fusion1.client.Client getClient() {
        return client;
    }

    public Thread getPacketListener() {
        return packetListener;
    }

    public List<ClientEventListener> getEventListener() {
        return eventListener;
    }

    public PublicKey getEncryptionKey() {
        return encryptionKey;
    }

    public PrivateKey getDecryptionKey() {
        return decryptionKey;
    }

    public PacketRegister getPacketRegister() {
        return packetRegister;
    }

    public void setClientUser(Client clientUser) {
        this.clientUser = clientUser;
    }
}
