package UClient;

import com.jme3.audio.AudioNode;
import com.jme3.input.InputManager;

import java.util.ArrayList;

/**
 * Created by Torran on 11/26/14.
 */
public class OverNode extends PlayerNode
{
  private ArrayList<AudioNode> audioList = new ArrayList<AudioNode>();

  public OverNode(String name)
  {
    super(name);
  }

  public void update (float tpf)
  {

  }

  public void setUpPlayer()
  {

  }

  public void onAnalog(String binding, float value, float tpf)
  {

  }

  public void onAction(String binding, boolean isPressed, float tpf)
  {

  }

  public ArrayList getAudio()
  {
    return audioList;
  }

  public ArrayList setUpControls(InputManager inputManager)
  {
    return actionStrings;
  }
}
