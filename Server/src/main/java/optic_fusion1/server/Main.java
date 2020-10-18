package optic_fusion1.server;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import optic_fusion1.server.logging.CustomLogger;
import optic_fusion1.server.server.Server;

public class Main extends Thread {

  public static final CustomLogger LOGGER = new CustomLogger();

  @Override
  public void run() {
    try {
      System.setOut(new PrintStream(System.out, true, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new InternalError("VM does not support mandatory encoding UTF-8");
    }

    setName("Server/Main");
    new Server().startServer();
  }

  public static void main(String[] args) {
    new Main().start();
  }

}
