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
            client.left = isPressed;
        } else if (binding.equals("Right")) {
            client.right = isPressed;
        } else if (binding.equals("Up")) {
            client.up = isPressed;
        } else if (binding.equals("Down")) {
            client.down = isPressed;
        } else if (binding.equals("Jump")) {
            if (isPressed) { client.getPlayer().jump(); }
        }
    }
}
