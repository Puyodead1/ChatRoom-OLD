package optic_fusion1.commandsystem.shellparser;

import java.util.ArrayList;
import java.util.List;
import optic_fusion1.commandsystem.shellparser.states.StartState;

public class ShellParser {

  private ShellParser() {
  }

  public static List<String> parseString(String string) throws ParseException {
    return (new StartState()).parse(string, "", new ArrayList<>(), null);
  }

  public static List<String> safeParseString(String string) {
    try {
      return ShellParser.parseString(string);
    } catch (ParseException e) {
      return null;
    }
  }
}
