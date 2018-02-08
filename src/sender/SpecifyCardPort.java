package sender;

import javafx.scene.control.TextArea;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SpecifyCardPort implements Runnable{

    private String mobNumber;
    private Socket s;
    private TextArea console;
    private String port;
    private String card;


    public SpecifyCardPort(String mobNumber, Socket s, TextArea console, String card, String port) {

        this.mobNumber = mobNumber;
        this.s = s;
        this.console = console;
        this.card = card;
        this.port = port;

    }

    @Override
    public void run() {

        try {
            PrintWriter p = new PrintWriter(s.getOutputStream(), true);
            BufferedReader bufRd = new BufferedReader(new InputStreamReader(s.getInputStream()));

            p.println("{\"number\": \"" + mobNumber + "\",\"msg\":\"" + card + " # " + port +
                        "\",\"unicode\":\"2\",\"send_to_sim\":\"" + card + "#" + port + "\"}");

            String response = bufRd.readLine();
            Object obj = JSONValue.parse(response);
            JSONObject jsonObject = (JSONObject) obj;

            String part1 = (String) jsonObject.get("send_to_sim");
            String part2 = (String) jsonObject.get("reply");

            console.appendText("Send to sim: " + part1 + " Status: " + part2 + "\n");
            } catch (IOException e) {
                System.out.println(e.getMessage());
        }//end try-catch
    }//end run method
}//end class