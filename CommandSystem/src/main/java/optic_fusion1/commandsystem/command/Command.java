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

package optic_fusion1.commandsystem.command;

import java.util.List;

public abstract class Command {

  private String name;
  private int minimumArgLength = -1;
  private int maximumArgLength = -1;
  private int perm;

  public Command(String name, int perm) {
    this.name = name;
    this.perm = perm;
  }

  public abstract boolean execute(CommandSender sender, String commandLabel, List<String> args);

  public int getPerm() {
    return perm;
  }

  public int getMinimumArgLength() {
    return minimumArgLength;
  }

  public void setArgLength(int minimumArgLength, int maximumArgLength) {
    this.minimumArgLength = minimumArgLength;
    this.maximumArgLength = maximumArgLength;
  }

  public void setMinimumArgLength(int minimumArgLength) {
    this.minimumArgLength = minimumArgLength;
  }

  public int getMaximumArgLength() {
    return maximumArgLength;
  }

  public void setMaximumArgLength(int maximumArgLength) {
    this.maximumArgLength = maximumArgLength;
  }

  public String getName() {
    return name;
  }

  public boolean checkArgLength(final List<String> args) {
    return (this.minimumArgLength == -1 || this.minimumArgLength <= args.size()) && (args.size() <= this.maximumArgLength || this.maximumArgLength == -1);
  }

}
