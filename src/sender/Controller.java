package sender;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import java.net.Socket;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    ObservableList list = FXCollections.observableArrayList();

    @FXML private TextField ipAddress;
    @FXML private TextField passWord;
    @FXML private TextField card;
    @FXML private TextField card2;
    @FXML private TextField port;
    @FXML private TextField mobNumber;
    @FXML private TextField numCards;

    @FXML private TextArea console;

    @FXML private Label connStatus;
    @FXML private Label serverStatus;

    @FXML private ChoiceBox<String> cBox;

    private Socket s;
    Connection conn = null;


    public void openSocket(ActionEvent e) throws Exception {

        s = new Socket(ipAddress.getText(), 63333);

        (new Thread(new Connector(console, ipAddress.getText(), connStatus))).start();

        (new Thread(new GetStatus(serverStatus, console, passWord.getText(), s, connStatus))).start();
        //s.close();
    }//end

    public void randSend(ActionEvent e)throws Exception{

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

    public void cardPort(ActionEvent e)throws Exception{

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

    public void allCard(ActionEvent e)throws Exception{

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

    public void allPorts(ActionEvent e)throws Exception {

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

    public void clearConsole(ActionEvent e){

        console.clear();
    }

    public void pauseServer(ActionEvent e)throws Exception{

        String response = sender.Senders.pauseServer(s);
        if(response.equals("ok")) {
            serverStatus.setTextFill(Color.RED);
            serverStatus.setText("Paused");
        }

    }//end pause Server

    public void runServer(ActionEvent e)throws Exception{

        String response = sender.Senders.runServer(s);
        if(response.equals("ok")) {
            serverStatus.setTextFill(Color.GREEN);
            serverStatus.setText("Running");
        }

    }//end pause Server

    public void queryGeneral(ActionEvent e)throws Exception{

        String response = sender.Senders.queryGenQue(s);
        console.appendText(response + "\n");

    }//end query general queue

    public void queryMaster(ActionEvent e)throws Exception{


        String response = sender.Senders.queryMasQue(s);
        console.appendText(response + "\n");

    }//end query master queue

    public void flushGeneral(ActionEvent e)throws Exception{

        String response = sender.Senders.flushGenQue(s);
        console.appendText(response + "\n\n");

        response = sender.Senders.queryGenQue(s);
        console.appendText(response + "\n");

    }

    public void flushMaster(ActionEvent e)throws Exception{

        String response = sender.Senders.flushMasQue(s);
        console.appendText(response + "\n\n");

        response = sender.Senders.queryMasQue(s);
        console.appendText(response + "\n");

    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadData();
    }

    private void loadData() {

        list.removeAll(list);

        // SQLite connection string
        String url = "jdbc:sqlite:C://sqlite/test.db";

        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException s) {
            System.out.println(s.getMessage());
        }

        String sql = "SELECT * FROM gateways";//construct db query

        try (
                Statement stmt  = conn.createStatement();
                ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {

                String site = rs.getString("site_name");
                list.addAll(site);

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        cBox.getItems().addAll(list);//add site names to list
    }

    @FXML
    private void selectSite(ActionEvent e){

        String site = cBox.getValue();
        if(site == null){
            console.appendText("Please select a gateway from  the dropdown\n");
        }else {
            String sql = ("SELECT * FROM gateways WHERE site_name = " +"\"" + site + "\" ");

            try (
                    Statement stmt  = conn.createStatement();
                    ResultSet rs    = stmt.executeQuery(sql)) {

                //site = rs.getString("site_name");
                String address = rs.getString("ip_address");
                String password = rs.getString("password");

                ipAddress.setText(address);
                passWord.setText(password);

            } catch (SQLException e1) {
                e1.printStackTrace();
            }


        }
    }
}//end class
