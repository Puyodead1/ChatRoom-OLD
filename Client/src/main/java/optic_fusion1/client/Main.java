package optic_fusion1.client;

public class Main extends Thread {

  public Main() {
    setName("Client/Main");
  }

  @Override
  public void run() {
    new Client().startClient();
  }

  public static void main(String[] args) {
    new Main().start();
  }

}
