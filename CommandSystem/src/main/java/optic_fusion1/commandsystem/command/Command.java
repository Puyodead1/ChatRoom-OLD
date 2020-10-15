package optic_fusion1.commandsystem.command;

import java.util.List;

public abstract class Command {

  private String name;
  
  public Command(String name){
    this.name = name;
  }
  
  public String getName(){
    return name;
  }
  
  public abstract boolean execute(CommandSender sender, String commandLabel, List<String> args);
  
}
