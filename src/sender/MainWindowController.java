package sender;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable{

    @FXML private TextField ipAddress;
    @FXML private TextField passWord;
    @FXML private TextField card;
    @FXML private TextField card2;
    @FXML private TextField port;
    @FXML private TextField mobNumber;
    @FXML private TextField numCards;
    @FXML private TextField siteName;

    @FXML private TextArea console;

    @FXML private Label connStatus;
    @FXML private Label serverStatus;

    @FXML private ChoiceBox<String> cBox;

    private Socket s;
    String url = "jdbc:sqlite:C://sqlite/sites.db";

    @Override
    public void initialize(URL location, ResourceBundle resources) {

       SiteList list = new SiteList(console, cBox, url);
       list.readList();
    }
    @FXML
    private void selectSite(){

        SelectSite site = new SelectSite(cBox, ipAddress, passWord, console, url, siteName);
        site.selectSite();
    }




    @FXML
    private void openSocket() throws Exception {

        s = new Socket(ipAddress.getText(), 63333);

        (new Thread(new Connector(console, ipAddress.getText(), connStatus))).start();

        (new Thread(new GetStatus(serverStatus, console, passWord.getText(), s, connStatus))).start();
        //s.close();
    }//end
    @FXML
    private void randSend()throws Exception{

        String status = sender.GetRunPause.getRunPause(s);
        if(status.equals("Paused")) {
        serverStatus.setTextFill(Color.RED);
        serverStatus.setText(status);
        console.setText("ERROR:  The server is in pause mode: \nLog onto HMC and set run/scheduled \n");
        }else{
            serverStatus.setTextFill(Color.GREEN);
            serverStatus.setText(status);
            (new Thread(new RandomSend(mobNumber.getText(), s, console))).start();
        }
    }//end randSend
    @FXML
    private void cardPort()throws Exception{

        int cardCheck = Integer.parseInt(card.getText());
            if(cardCheck < 21 || cardCheck >28){
                console.appendText("You must enter a card address from 21 - 28 \n");
                return;
        }
        int portCheck = Integer.parseInt(port.getText());
        if(portCheck < 1 || portCheck >4){
            console.appendText("You must enter a port number from 1 - 4 \n");
            return;
        }
        String status = sender.GetRunPause.getRunPause(s);
        if(status.equals("Paused")) {
            serverStatus.setTextFill(Color.RED);
            serverStatus.setText(status);
            console.setText("ERROR:  The server is in pause mode: \nLog onto HMC and set run/scheduled \n");
        }else{
            serverStatus.setTextFill(Color.GREEN);
            serverStatus.setText(status);
            (new Thread(new SpecifyCardPort(mobNumber.getText(), s, console, card.getText(), port.getText()))).start();
        }//end if - else

    }//end card/port method
    @FXML
    private void allCard()throws Exception{

        int cardCheck = Integer.parseInt(card2.getText());
        if(cardCheck < 21 || cardCheck >28){
            console.appendText("You must enter a card address from 21 - 28 \n");
            return;
        }//end cardCheck
        String status = sender.GetRunPause.getRunPause(s);
        if(status.equals("Paused")) {
            serverStatus.setTextFill(Color.RED);
            serverStatus.setText(status);
            console.setText("ERROR:  The server is in pause mode: \nLog onto HMC and set run/scheduled \n");
        }else {
            serverStatus.setTextFill(Color.GREEN);
            serverStatus.setText(status);
            (new Thread(new AllCard(s, card2.getText(), console, mobNumber.getText()))).start();
        }
    }//end allCard method
    @FXML
    private void allPorts()throws Exception {

        int cardCheck = Integer.parseInt(numCards.getText());
        if(cardCheck < 1 || cardCheck >8){
            console.appendText("You must enter a card number from 1 - 8 \n");
            return;
        }

        int totalCards = Integer.parseInt(numCards.getText());
        totalCards = totalCards + 20;

        String status = sender.GetRunPause.getRunPause(s);
        if(status.equals("Paused")) {
            serverStatus.setTextFill(Color.RED);
            serverStatus.setText(status);
            console.setText("ERROR:  The server is in pause mode: \nLog onto HMC and set run/scheduled \n");
        }else {
            serverStatus.setTextFill(Color.GREEN);
            serverStatus.setText(status);
            (new Thread(new AllCardsPorts(console, s, mobNumber.getText(), totalCards))).start();
        }//end if - else


    }//end allPorts method
    @FXML
    private void clearConsole(){

        console.clear();
    }
    @FXML
    private void pauseServer()throws Exception{

        String response = sender.Senders.pauseServer(s);
        if(response.equals("ok")) {
            serverStatus.setTextFill(Color.RED);
            serverStatus.setText("Paused");
        }

    }//end pause Server
    @FXML
    private void runServer()throws Exception{

        String response = sender.Senders.runServer(s);
        if(response.equals("ok")) {
            serverStatus.setTextFill(Color.GREEN);
            serverStatus.setText("Running");
        }

    }//end pause Server
    @FXML
    private void queryGeneral()throws Exception{

        String response = sender.Senders.queryGenQue(s);
        console.appendText(response + "\n");

    }//end query general queue
    @FXML
    private void queryMaster()throws Exception{


        String response = sender.Senders.queryMasQue(s);
        console.appendText(response + "\n");

    }//end query master queue
    @FXML
    private void flushGeneral()throws Exception{

        String response = sender.Senders.flushGenQue(s);
        console.appendText(response + "\n\n");

        response = sender.Senders.queryGenQue(s);
        console.appendText(response + "\n");

    }
    @FXML
    private void flushMaster()throws Exception{

        String response = sender.Senders.flushMasQue(s);
        console.appendText(response + "\n\n");

        response = sender.Senders.queryMasQue(s);
        console.appendText(response + "\n");

    }

}//end class
