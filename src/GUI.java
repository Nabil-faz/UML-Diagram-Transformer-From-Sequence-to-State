import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class GUI {
    static File projectDir = new File(System.getProperty("user.dir"));
    public static void createGUI() throws IOException {
        JFrame frame = new JFrame("SeqToState Generator 1.0");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setLayout(new GridLayout(2,1));
        JPanel titel = new JPanel();
        JPanel intro = new JPanel();
        titel.add(new JLabel("<html><h1><strong><i>PUML State Machine Generator</i></strong></h1><hr></html>"));
        intro.add(new JLabel("Choose the  PUML Sequence Diagramm to be transformed"));
        JButton select = new JButton("select File");
        intro.add(select);
        frame.add(titel);
        frame.add(intro);

        select.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(projectDir);
                    FileFilter pumlOnly = new FileNameExtensionFilter("PUML File","puml");
                    fileChooser.setFileFilter(pumlOnly);
                    fileChooser.addChoosableFileFilter(pumlOnly);
                    fileChooser.showDialog(null, "generate");
                    String uploadFromTF = fileChooser.getSelectedFile().getName();
                    String Path_uploadFromTF = fileChooser.getSelectedFile().getPath();

                    PUMLConverter.seqToState(Path_uploadFromTF,uploadFromTF);


                    JOptionPane.showMessageDialog(null, uploadFromTF + "Transformed successfully");


                } catch (NullPointerException nullPointerException) {
                    JOptionPane.showMessageDialog(null, "No File selected");

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }


            }
        });
    }

}
