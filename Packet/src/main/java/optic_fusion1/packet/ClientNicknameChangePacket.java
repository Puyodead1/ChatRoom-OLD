package optic_fusion1.packet;

public class ClientNicknameChangePacket extends Packet{

  private final String nickname;
  
  public ClientNicknameChangePacket(String nickname){
    this.nickname = nickname;
  }
  
  public String getNickName(){
    return nickname;
  }
  
}
