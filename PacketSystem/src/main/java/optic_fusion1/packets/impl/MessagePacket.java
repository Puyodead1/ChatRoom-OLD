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

package optic_fusion1.packets.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import optic_fusion1.packets.IPacket;

public class MessagePacket implements IPacket {

  private String message;
  private Type type;

  public MessagePacket() {

  }

  public MessagePacket(String message) {
    this(Type.NORMAL, message);
  }

  public MessagePacket(Type type, String message) {
    this.type = type;
    this.message = message;
  }

  public enum Type {
    SYSTEM("System: "),
    NORMAL("");

    private String name;

    Type(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

  @Override
  public void writePacketData(DataOutputStream dataOutputStream) throws IOException {
    dataOutputStream.writeUTF(type.getName() + message);
  }

  @Override
  public void readPacketData(DataInputStream dataInputStream) throws IOException {
    message = dataInputStream.readUTF();
  }

  public String getMessage() {
    return message;
  }

}
