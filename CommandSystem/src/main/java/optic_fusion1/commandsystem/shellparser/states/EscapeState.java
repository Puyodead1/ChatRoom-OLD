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
