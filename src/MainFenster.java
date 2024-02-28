import javax.swing.*;
import java.io.IOException;

public class MainFenster {
    public static void main(String[] args) {
        GUI GUI = new GUI();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    GUI.createGUI();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}

