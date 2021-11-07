package org.example;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class SecondaryController {
    Gson gson = new Gson();
    ArrayList<Complaint> complaints ;
    @FXML
    private VBox complaintsList;
    @FXML
    protected void initialize() {
        try{
            complaints = sendGET();
            for (Complaint complaint: complaints){
                HBox row = new HBox();
                row.getChildren().add(new Text(complaint.title));
                Button EditButton = new Button(complaint.title);
                EditButton.setText("Edit");
                EditButton.setOnAction(new EventHandler<ActionEvent>() {
                     @Override
                     public void handle(ActionEvent actionEvent) {
                         try {
                             EditComplain(complaint.Id);
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                     }
                 });
                row.getChildren().add(EditButton);
                Text Status = new Text("😟");
                if(complaint.resolvedStatus){
                    Status.setText("😊");
                }
                row.setSpacing(20.0);
                row.getChildren().add(Status);
                complaintsList.getChildren().add(row);
            }

        }catch (Exception e){
            Error.setText("Error Getting Caomplaints from api"+ e);
        }
    }
    @FXML
    private Text Error;
    public SecondaryController(){

    }
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }

    private ArrayList<Complaint> sendGET() throws IOException {
        URL obj = new URL("http://localhost:8080/352_github/Employee/Complaints");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        FileReader fileReader = new FileReader(new File("Token.json"));
        String Token = gson.fromJson(fileReader,String.class);
        System.out.println(Token);
        fileReader.close();
        if(Token==null || Token.isEmpty()){
            App.setRoot("primary");
        }
        con.addRequestProperty("token",Token);
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.println(response.toString());
            ArrayList<Complaint> complaints = gson.fromJson(response.toString(),new TypeToken<ArrayList<Complaint>>(){}.getType());
            // print result

            return complaints;
        } else {
            System.out.println("GET request not worked");
            return null;
        }

    }
    private void EditComplain(String Id) throws IOException {
        App.setComplainDetailId(Id);
        App.setRoot("Complaint");
    }
}