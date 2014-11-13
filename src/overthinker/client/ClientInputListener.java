package overthinker.client;

import com.jme3.input.controls.ActionListener;

/**
 * Created by Peter on 11/12/2014.
 */
public class ClientInputListener implements ActionListener {
    private GameData gameData;

    public ClientInputListener(GameData gameData)
    {
        this.gameData = gameData;
    }
    @Override
    public void onAction(String binding, boolean isPressed, float tpf) {
        if (binding.equals("Left")) {
            gameData.setLeft(isPressed);
        } else if (binding.equals("Right")) {
            gameData.setRight(isPressed);
        } else if (binding.equals("Up")) {
            gameData.setUp(isPressed);
        } else if (binding.equals("Down")) {
            gameData.setDown(isPressed);
        } else if (binding.equals("Jump")) {
            if (isPressed) { gameData.getPlayer().jump(); }
        }
    }
}
