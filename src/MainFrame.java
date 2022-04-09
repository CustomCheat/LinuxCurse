import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.util.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

public class MainFrame extends JFrame implements ActionListener {
    JButton startBtn = new JButton("Download the Modpack");
    JButton chooseBtn = new JButton("Choose the path");
    JLabel label = new JLabel("The install can take a long time be patient");
    static File path;
    String modpackName = "";
    String modpackDownload = "";
    MainFrame(String[] info)  {
        this.modpackName = info[1];
        this.modpackDownload = info[0];
        this.setLocation(SearchFrame.select.getLocation());
        path = new File(System.getProperty("user.home") + "/.linuxcurse/");
        if(!path.exists()){
            boolean success = path.mkdir();
            if(!success){
                System.out.println("Could not create the directory: " + path.getAbsolutePath());
            }
        }

       // System.out.println(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        Border border = BorderFactory.createLineBorder(Color.RED, 3);
        int width = 800, height = 500;
        startBtn.setBounds(0,0, 180, 40);
        startBtn.setLocation((width / 2) - (startBtn.getWidth() / 2),(height/2) - (startBtn.getHeight()/2) + 80);
        startBtn.setFocusable(false);
        startBtn.addActionListener(this);
        chooseBtn.setBounds(0,0, 180, 40);
        chooseBtn.setLocation((width / 2) - (startBtn.getWidth() / 2),(height/2) - (startBtn.getHeight()/2));
        chooseBtn.setFocusable(false);
        chooseBtn.addActionListener(this);
        label.setBounds(0,0,300,40);
        label.setLocation((width / 2) - (startBtn.getWidth() / 2) - 40,(height/2) - (startBtn.getHeight()/2) - 80);
        this.setSize(width,height);
        this.setLayout(null);
        this.setTitle("LinuxCurse");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.add(startBtn);
        this.add(chooseBtn);
        this.add(label);

        this.setVisible(true);
        this.getContentPane().setBackground(Color.GRAY);
    }


    /* Shoutout to this guy: https://stackoverflow.com/a/5368745/14269129 */
    public void copy(File sourceLocation, File targetLocation) throws IOException {
        if (sourceLocation.isDirectory()) {
            copyDirectory(sourceLocation, targetLocation);
        } else {
            copyFile(sourceLocation, targetLocation);
        }
    }

    private void copyDirectory(File source, File target) throws IOException {
        if (!target.exists()) {
            target.mkdir();
        }

        for (String f : source.list()) {
            copy(new File(source, f), new File(target, f));
        }
    }

    private void copyFile(File source, File target) throws IOException {
        try (
                InputStream in = new FileInputStream(source);
                OutputStream out = new FileOutputStream(target)
        ) {
            byte[] buf = new byte[1024];
            int length;
            while ((length = in.read(buf)) > 0) {
                out.write(buf, 0, length);
            }
        }
    }
    static String folderName = "";
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == chooseBtn){
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                path = new File(selectedFile.getAbsolutePath());
                if(!path.exists()){
                    System.out.println("Path: " + path + " doesn't exist");
                }
            }
        }
        if(e.getSource() == startBtn){
            System.out.println("Download link: " + modpackDownload + " Modpack: " + modpackName);
            InputStream inputStream = null;
            try {
                inputStream = new URL(modpackDownload).openStream();
                Files.copy(inputStream, Paths.get(path.getAbsolutePath() + "/modpack.zip"), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                ex.printStackTrace();
            }finally {
                try {
                    // write the modpackName to a file that is located in a specific folder
                    File file = new File(path.getAbsolutePath() + "/modpacks.txt");
                    FileWriter fw = new FileWriter(file, true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(modpackName + "\n");
                    bw.close();

                    folderName = modpackName.replaceAll("[^A-Za-z0-9\",]|,(?!(([^\"]*\"){2})*[^\"]*$)", "").replace("\"", "").replace(",", "").replace(" ", "");
                    new ZipFile(Paths.get(path.getAbsolutePath() + "/modpack.zip").toFile())
                            .extractAll(Paths.get(path.getAbsolutePath() + "/unzip/").toString());
                    new File(path.getAbsolutePath() + "/" + folderName + "/").mkdirs();
                    if(new File(path.getAbsolutePath() + "/unzip/overrides/").exists()){
                        File srcDir = new File(path.getAbsolutePath() + "/unzip/overrides/");
                        File destDir = new File(path.getAbsolutePath() + "/" + folderName + "/" + ".minecraft/");

                        copyDirectory(srcDir,destDir);
                    }
                    new File(path.getAbsolutePath() + "/" + folderName + "/" + ".minecraft/mods").mkdir();
                    new File(path.getAbsolutePath() + "/" + folderName + "/" + ".minecraft/versions").mkdir();
                    if(new File(path.getAbsolutePath() + "/unzip/manifest.json").exists()){
                        File fa = new File(path.getAbsolutePath() + "/unzip/manifest.json");
                        Scanner reader = new Scanner(fa);
                        String text = "";
                        while (reader.hasNextLine()) {
                            text = text + reader.nextLine();
                        }
                        reader.close();
                        JSONObject json = new JSONObject(text);
                        String forgeID = json.getJSONObject("minecraft").getJSONArray("modLoaders").getJSONObject(0).get("id").toString();
                        String strippedID = forgeID.replace("forge-", "");
                        String forgeVersion = json.getJSONObject("minecraft").get("version").toString();
                        String forgelink = "https://maven.minecraftforge.net/net/minecraftforge/forge/" + forgeVersion + "-" + strippedID + "/" + "forge-" + forgeVersion + "-" + strippedID + "-installer.jar";
                        inputStream = new URL(forgelink).openStream();
                        Files.copy(inputStream, Paths.get(path.getAbsolutePath() + "/forge.jar"), StandardCopyOption.REPLACE_EXISTING);
                        int result = JOptionPane.showConfirmDialog(this, "Minecraft launcher will be openend. Sign in then wait until u see the play button then close the window");
                        if(result == 0) {
                           new ExecuteCommand("minecraft-launcher --workDir " + path.getAbsolutePath() + "/" + folderName + "/" + ".minecraft/");
                        }else {
                            System.exit(0);
                        }
                        int r = JOptionPane.showConfirmDialog(this, "Forge installer will be opened just click the button OK");
                        if(r == 0) {
                            new ExecuteCommand("java -Duser.home=" + path.getAbsolutePath() + "/" + folderName + "/ -jar " + path.getAbsolutePath() + "/forge.jar");
                        }else{
                            System.exit(0);
                        }
                        System.out.println("java -Duser.home=" + path.getAbsolutePath() + "/" + folderName + "/ -jar " + path.getAbsolutePath() + "/forge.jar");
                        JSONArray files = json.getJSONArray("files");
                        files.forEach(f -> {
                            if(f instanceof JSONObject){
                                JSONObject jsFile = (JSONObject) f;
                                String projectID = jsFile.get("projectID").toString();
                                String fileID = jsFile.get("fileID").toString();
                                String modlink = "https://addons-ecs.forgesvc.net/api/v2/addon/" + projectID + "/file/" + fileID;
                                HttpClient client = HttpClient.newHttpClient();
                                HttpRequest request = HttpRequest.newBuilder()
                                        .uri(URI.create(modlink)).setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:92.0) Gecko/20100101 Firefox/92.0").build();


                                client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                                        .thenApply(HttpResponse::body)
                                        .thenAccept(MainFrame::downloadMod)
                                        .join();
                            }
                        });
                        int re = JOptionPane.showConfirmDialog(this, "This is the final step. Minecraft launcher will open just click play.");
                        new ExecuteCommand("minecraft-launcher --workDir " + path.getAbsolutePath() + "/" + folderName + "/" + ".minecraft/");
                    }else{
                        throw new FileNotFoundException("Could not find manifest.json");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    public static void downloadMod(String responce){
        try {
            JSONObject jsonObject = new JSONObject(responce);
            String downloadLink = jsonObject.get("downloadUrl").toString();
            String fileName = jsonObject.get("fileName").toString();
            InputStream inputStream = null;
            try {
                System.out.println("Downloading " + fileName);
                inputStream = new URL(downloadLink).openStream();
                Files.copy(inputStream, Paths.get(path.getAbsolutePath() + "/" + folderName + "/" + ".minecraft/mods/" + fileName), StandardCopyOption.REPLACE_EXISTING);
                inputStream.close();
            } catch (IOException ex){
                ex.printStackTrace();
            }
        }catch (JSONException e){
            System.out.println("Err with the json: " + responce + " Retrying...");
            downloadMod(responce);
        }

    }
}
