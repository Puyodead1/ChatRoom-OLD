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
