import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ExecuteCommand{
    public ExecuteCommand(String cmd){
        String s;
        Process p;
        try {
            String[] arr = cmd.split(" ");
            p = Runtime.getRuntime().exec(arr);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null)
                System.out.println("line: " + s);
            p.waitFor();
            System.out.println ("exit: " + p.exitValue());
            p.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
