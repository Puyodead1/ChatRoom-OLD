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

package optic_fusion1.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import optic_fusion1.packets.utils.ObjectConverter;

public abstract class SimplePacket implements IPacket {

  @Override
  public void writePacketData(DataOutputStream dataOutputStream) throws IOException {
    List<Object> list = new ArrayList<>();
    this.writePacketData(list);
    byte[] rawData = ObjectConverter.objectToByteArray(list);
    dataOutputStream.writeInt(rawData.length);
    dataOutputStream.write(rawData);
  }

  @Override
  public void readPacketData(DataInputStream dataInputStream) throws IOException {
    byte[] rawData = new byte[dataInputStream.readInt()];
    dataInputStream.read(rawData);
    List<Object> list = ObjectConverter.byteArrayToObject(rawData);
    this.readPacketData(list);
  }

  public abstract void writePacketData(final List<Object> list);

  public abstract void readPacketData(final List<Object> list);

}
