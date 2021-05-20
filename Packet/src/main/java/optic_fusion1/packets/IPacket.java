package optic_fusion1.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface IPacket {

  void writePacketData(final DataOutputStream dataOutputStream) throws IOException;

  void readPacketData(final DataInputStream dataInputStream) throws IOException;

}
