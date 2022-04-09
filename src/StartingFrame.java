import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartingFrame extends JFrame implements ActionListener {
    public static SearchFrame search;
    JButton newModpack = new JButton("Install new modpack");
    public static SearchFrame searchFrame;
    JButton launchModpack = new JButton("Launch a modpack");
    public StartingFrame() {
        Border border = BorderFactory.createLineBorder(Color.RED, 3);
        int width = 800, height = 500;
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

        double screenHeight = dimension.getHeight();
        double screenWidth = dimension.getWidth();
        this.setLocation((int)(screenWidth / 2) - (width / 2), (int)(screenHeight / 2) - (height / 2));
        newModpack.setBounds(0,0, 180, 40);
        newModpack.setLocation((width / 2) - (newModpack.getWidth() / 2),(height/2) - (newModpack.getHeight()/2) - 40);
        newModpack.setFocusable(false);
        newModpack.addActionListener(this);
        launchModpack.setBounds(0,0, 180, 40);
        launchModpack.setLocation((width / 2) - (launchModpack.getWidth() / 2),(height/2) - (launchModpack.getHeight()/2) + 40);
        launchModpack.setFocusable(false);
        launchModpack.addActionListener(this);
        this.setSize(width,height);
        this.setLayout(null);
        this.setTitle("LinuxCurse");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.add(newModpack);
        this.add(launchModpack);

        this.setVisible(true);
        this.getContentPane().setBackground(Color.GRAY);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == newModpack) {
            this.dispose();
            new SearchFrame();
        }
        if(e.getSource() == launchModpack) {
            this.dispose();
            new LaunchFrame();
        }
    }
}
