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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListPacket extends SimplePacket {

  private final List<Object> list;

  public ListPacket() {
    this.list = new ArrayList<>();
  }

  public ListPacket(final Object... objects) {
    this();

    Collections.addAll(this.list, objects);
  }

  public List<Object> getList() {
    return this.list;
  }

  @Override
  public void writePacketData(List<Object> list) {
    list.addAll(this.list);
  }

  @Override
  public void readPacketData(List<Object> list) {
    this.list.addAll(list);
  }

}
