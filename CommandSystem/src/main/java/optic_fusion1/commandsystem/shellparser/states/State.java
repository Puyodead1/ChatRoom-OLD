package optic_fusion1.commandsystem.shellparser.states;

import java.util.List;
import optic_fusion1.commandsystem.shellparser.ParseException;

public abstract class State {

  public abstract List<String> parse(String parsing, String accumulator, List<String> parsed, State referrer) throws ParseException;
}
