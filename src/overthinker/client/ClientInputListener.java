package overthinker.client;

import com.jme3.input.controls.ActionListener;

/**
 * Created by Peter on 11/12/2014.
 */
public class ClientInputListener implements ActionListener {
    private ClientMain client;

    public ClientInputListener(ClientMain clientGameData)
    {
        this.client = client;
    }
    @Override
    public void onAction(String binding, boolean isPressed, float tpf) {
        if (binding.equals("Left")) {
            client.getLevel().setLeft(isPressed);
        } else if (binding.equals("Right")) {
            client.getLevel().setRight(isPressed);
        } else if (binding.equals("Up")) {
            client.getLevel().setUp(isPressed);
        } else if (binding.equals("Down")) {
            client.getLevel().setDown(isPressed);
        } else if (binding.equals("Jump")) {
            if (isPressed) { client.getPlayer().jump(); }
        }
    }
}
