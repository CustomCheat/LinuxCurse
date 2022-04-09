import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class LaunchFrame extends JFrame implements ActionListener {
    ArrayList<JButton> buttons = new ArrayList<>();
    int startingX = 0;
    int startingY = 0;
    File path;
    public LaunchFrame() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 800, height = 500;
        double screenHeight = dimension.getHeight();
        double screenWidth = dimension.getWidth();
        this.setLocation((int)(screenWidth / 2) - (width / 2), (int)(screenHeight / 2) - (height / 2));
        // read from a txt file and save to a string then for every line in the string, create a button with the line as the text and add it to the arraylist of buttons and add the actionlistener to the button and add it to the frame
        try {
            path = new File(System.getProperty("user.home") + "/.linuxcurse/");
            File myObj = new File(path.getAbsolutePath() + "/modpacks.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                JButton button = new JButton(data);
                button.setBounds(0, 0, width / 3, 40);
                button.setLocation(startingX, startingY);
                button.addActionListener(this);
                buttons.add(button);

                add(button);
                startingX += button.getWidth();
                if(startingX + button.getWidth() > width) {
                    startingX = 0;
                    startingY += button.getHeight();
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        this.setSize(width,height);
        this.setLayout(null);
        this.setTitle("Select the modpack from the list");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.getContentPane().setBackground(Color.GRAY);


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for(JButton button : buttons) {
            if(e.getSource() == button) {
                String modpackName = button.getText();
                String folderName = modpackName.replaceAll("[^A-Za-z0-9\",]|,(?!(([^\"]*\"){2})*[^\"]*$)", "").replace("\"", "").replace(",", "").replace(" ", "");
                new ExecuteCommand("minecraft-launcher --workDir " + path.getAbsolutePath() + "/" + folderName + "/" + ".minecraft/");
                System.exit(0);
            }
        }
    }
}
