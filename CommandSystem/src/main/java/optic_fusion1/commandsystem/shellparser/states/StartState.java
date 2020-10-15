package optic_fusion1.commandsystem.shellparser.states;

import java.util.List;
import optic_fusion1.commandsystem.shellparser.ParseException;

public class StartState extends State {

  @Override
  public List<String> parse(String parsing, String accumulator, List<String> parsed, State referrer) throws ParseException {
    if (parsing.length() == 0) {
      if (accumulator.length() > 0) {
        parsed.add(accumulator);
      }
      return parsed;
    }

    char c = (char) parsing.getBytes()[0];
    switch (c) {
      case ' ':
        if (accumulator.length() > 0) {
          parsed.add(accumulator);
        }
        return (new StartState()).parse(parsing.substring(1), "", parsed, this);
      case '\\':
        return (new EscapeState()).parse(parsing.substring(1), accumulator, parsed, this);
      case '"':
      case '\'':
        return (new QuoteState(c)).parse(parsing.substring(1), accumulator, parsed, this);
      default:
        break;
    }

    return (new StartState()).parse(parsing.substring(1), accumulator + c, parsed, this);
  }

}
