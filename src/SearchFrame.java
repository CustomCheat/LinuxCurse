import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class SearchFrame extends JFrame implements ActionListener {
    static SelectFrame select;
    JButton searchBtn = new JButton("Search");
    int c = 0;
    JTextField searchTerm = new JTextField();
    SearchFrame() {
        Border border = BorderFactory.createLineBorder(Color.RED, 3);
        int width = 800, height = 500;
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

        double screenHeight = dimension.getHeight();
        double screenWidth = dimension.getWidth();
        this.setLocation((int)(screenWidth / 2) - (width / 2), (int)(screenHeight / 2) - (height / 2));
        searchBtn.setBounds(0,0, 180, 40);
        searchBtn.setLocation((width / 2) - (searchBtn.getWidth() / 2),(height/2) - (searchBtn.getHeight()/2) + 80);
        searchBtn.setFocusable(false);
        searchBtn.addActionListener(this);
        searchTerm.setBounds(0,0,200,40);
        searchTerm.setLocation((width / 2) - (searchBtn.getWidth() / 2) - 10,(height/2) - (searchBtn.getHeight()/2));
        this.setSize(width,height);
        this.setLayout(null);
        this.setTitle("LinuxCurse");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.add(searchBtn);
        this.add(searchTerm);

        this.setVisible(true);
        this.getContentPane().setBackground(Color.GRAY);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == searchBtn){
                search(searchTerm.getText());

        }
    }

    public void search(String searchTerm){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://addons-ecs.forgesvc.net/api/v2/addon/search?categoryId=0&gameId=432&index=1&pageSize=0&searchFilter=" + searchTerm.replace(" ", "%20"))).setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:92.0) Gecko/20100101 Firefox/92.0").build();


        System.out.println("Searching...");
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(SearchFrame::processResponce)
                .join();
    }
    public static void processResponce(String responce) {
        JSONArray obj = new JSONArray(responce);
        System.out.println(obj.get(0));
        ArrayList<String> modpackNames = new ArrayList<>();
        for (Object object : obj){
            if(object instanceof JSONObject modpack){
                String modpackName = modpack.get("name").toString();/*.replaceAll("[^A-Za-z0-9\", ]|,(?!(([^\"]*\"){2})*[^\"]*$)", "").replace("\"", "").replace(",", "");*/
                String downloadLink = modpack.getJSONArray("latestFiles").getJSONObject(modpack.getJSONArray("latestFiles").length() - 1).get("downloadUrl").toString().replace(" ", "%20");
                modpackNames.add(modpackName + "A:W:B]" + downloadLink);
                System.out.println(modpackName);
            }
        }
        if(!modpackNames.isEmpty()){
            StartingFrame.search.setVisible(false);
            select = new SelectFrame(modpackNames);

        }else{
            JOptionPane.showMessageDialog(StartingFrame.search, "No modpacks found.");
        }

    }
}
