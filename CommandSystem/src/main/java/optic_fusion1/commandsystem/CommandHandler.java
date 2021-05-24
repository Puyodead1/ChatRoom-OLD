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

package optic_fusion1.commandsystem;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import optic_fusion1.commandsystem.command.Command;
import optic_fusion1.commandsystem.command.CommandSender;
import optic_fusion1.commandsystem.shellparser.ParseException;
import optic_fusion1.commandsystem.shellparser.ShellParser;

public class CommandHandler {

  private static final HashMap<String, Command> COMMANDS = new HashMap<>();

  public Collection<Command> getCommands() {
    return COMMANDS.values();
  }

  public Command getCommand(String name) {
    return COMMANDS.get(name);
  }

  public void addCommand(Command command) {
    COMMANDS.putIfAbsent(command.getName(), command);
  }

  public boolean executeCommand(CommandSender sender, String message) {
    try {
      List<String> args = ShellParser.parseString(message);
      String commandLabel = args.get(0);
      args.remove(0);
      Command command = getCommand(commandLabel);
      if (command == null) {
        return false;
      }
      if (!command.checkArgLength(args)) {
        sender.sendMessage("You did not enter enough arguments");
        return false;
      }
      return command.execute(sender, commandLabel, args);
    } catch (ParseException ex) {
      return false;
    }
  }
}
