package overthinker.server.gui;

import overthinker.GameState;

import javax.swing.*;
import java.io.IOException;

/**
 * Created by sidholman on 11/15/14.
 */
public class ServerSetupGUI extends JFrame {
    private JComboBox mapNumber;
    private JComboBox difficultyNumber;
    private JTextField portNumber;
    private JComboBox modeNumber;
    private JPanel contentPanel;
    private JButton launchServerButton;
    private static ServerMain serverWrapper = null;
    private static GameState state = null;
    private static int port = 0;

    public ServerSetupGUI()
    {
        super("Server Setup");
        state = new GameState();
        setContentPane(contentPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        launchServerButton.addActionListener( e -> launchServer());
        this.pack();
    }

    private void launchServer() {
        port = Integer.parseInt(portNumber.getText());
        if (port > 1024 && port < 65535) {
            state.setGameMap(mapNumber.getSelectedIndex());
            state.setGameMode(modeNumber.getSelectedIndex());
            serverWrapper = new ServerMain(port);
        }
        else
            JOptionPane.showMessageDialog(null,
                    "Port number is out of range. 1024 < port < 65535",
                    "Port Warning",
                    JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

                ServerSetupGUI gui = null;
                gui = new ServerSetupGUI();
                gui.setVisible(true);
        });
    }

}
