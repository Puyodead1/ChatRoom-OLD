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
package optic_fusion1.commandsystem.shellparser.states;

import java.util.List;
import optic_fusion1.commandsystem.shellparser.ParseException;

public class EscapeState extends State {

  @Override
  public List<String> parse(String parsing, String accumulator, List<String> parsed, State referrer) throws ParseException {
    if (parsing.length() == 0) {
      throw new ParseException("Unexpected end of string after escape character");
    }

    return referrer.parse(parsing.substring(1), accumulator + (char) (parsing.getBytes()[0]), parsed, this);
  }

}
