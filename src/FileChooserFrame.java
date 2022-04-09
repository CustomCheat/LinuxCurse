import javax.swing.*;
import java.awt.*;
import java.net.URISyntaxException;

public class FileChooserFrame extends JFrame {
    public FileChooserFrame() throws URISyntaxException {
        this.setSize(200,200);
        JFileChooser fileChooser = new JFileChooser(String.valueOf(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()));
        this.add(fileChooser);

        this.setVisible(true);
        this.getContentPane().setBackground(Color.GRAY);

    }
    public FileChooserFrame(String path) throws URISyntaxException {
        this.setSize(200,200);
        JFileChooser fileChooser = new JFileChooser(path);
        this.add(fileChooser);

        this.setVisible(true);
        this.getContentPane().setBackground(Color.GRAY);

    }

}
