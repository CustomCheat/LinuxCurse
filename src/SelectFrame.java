import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class SelectFrame extends JFrame implements ActionListener {
    ArrayList<JButton> buttons = new ArrayList<>();
    int yloc = 0;
    int xLoc = 0;
    MainFrame frame;
    public SelectFrame(ArrayList<String> info){
        this.setLocationRelativeTo(StartingFrame.search);
       // Main.search.dispose();
        this.setLocation(StartingFrame.search.getLocation());
        int width = 800, height = 500;
        info.forEach(i -> {
            String modpackName = i.split("A:W:B]")[0];
            String modpackDownload = i.split("A:W:B]")[1];
            JButton btn = new JButton(modpackName);
            btn.addActionListener(this);
            btn.setFocusable(false);
            btn.setBounds(0,0, 250,20);
            btn.setLocation(xLoc, yloc);
            btn.setToolTipText(modpackDownload);
            yloc += btn.getHeight();
            if(yloc > height - (btn.getHeight() * 3)){
                yloc = 0;
                xLoc += btn.getWidth() + 20;
            }
            this.add(btn);
        });

        this.setSize(width,height);
        this.setLayout(null);
        this.setTitle("Select the modpack from the list");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.getContentPane().setBackground(Color.GRAY);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() instanceof JButton){
            JButton btn = (JButton) e.getSource();
            if(btn.getToolTipText() != null){
                StartingFrame.search.dispose();

                String[] info = {btn.getToolTipText(), btn.getText()};
                System.out.println("Selected: " + btn.getText());
                frame = new MainFrame(info);
                this.dispose();
            }
        }
    }
}
