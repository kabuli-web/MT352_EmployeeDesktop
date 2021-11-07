package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ComplaintDetailsController {
    String ComplaintId;
    Gson gson = new Gson();
    Text ErrrorMessage = new Text("complaint not found");
    Button button = new Button("Go Back");

    @FXML
    private VBox ErrorList;
    @FXML
    public void initialize() {
        System.out.println(App.getComplainDetailId());
        button.setOnAction(actionEvent -> {
            try {
                App.setRoot("secondary");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        // update text area if text in model changes:
        ComplaintId = App.getComplainDetailId();
        ErrorList.getChildren().add(ErrrorMessage);
        ErrorList.getChildren().add(button);
        ErrrorMessage.setVisible(false);
        try {

            Complaint complaint = sendGET();
            if(complaint==null){
                ErrrorMessage.setVisible(true);
            }else{
                message.setText(complaint.message);
                title.setText(complaint.title);
                resolveStatus.setSelected(complaint.resolvedStatus);
            }

        }catch (Exception e){
            ErrrorMessage.setVisible(true);
        }
    }

    @FXML
    TextField title;

    @FXML
    TextField message;

    @FXML
    CheckBox resolveStatus;

    @FXML
    public void UpdateComplaint(ActionEvent actionEvent) {

        try {
            Complaint complaint = sendPOST();
            if(complaint==null){
                ErrrorMessage.setVisible(true);
                ErrrorMessage.setText("Couldn't Update complaint");
            }
            ErrrorMessage.setVisible(true);
            ErrrorMessage.setText("Complaint Updated successfully");
            message.setText(complaint.message);
            title.setText(complaint.title);
            resolveStatus.setSelected(complaint.resolvedStatus);
        }catch (Exception e){
            ErrrorMessage.setVisible(true);
            ErrrorMessage.setText("Couldn't Update complaint"+ e);
        }
    }

    private Complaint sendGET() throws IOException {
        URL obj = new URL("http://localhost:8080/352_github/Employee/Complaints?complaintId=" + ComplaintId);
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
            Complaint complaint = gson.fromJson(response.toString(),Complaint.class);
            // print result
            return complaint;
        } else {
            System.out.println("GET request not worked");
            return null;
        }

    }

    private  Complaint sendPOST() throws IOException {
        URL obj = new URL("http://localhost:8080/352_github/Employee/Complaints?complaintId=" + ComplaintId);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        FileReader fileReader = new FileReader(new File("Token.json"));
        String Token = gson.fromJson(fileReader,String.class);

        fileReader.close();
        if(Token==null || Token.isEmpty()){
            App.setRoot("primary");
        }
        con.setRequestProperty("Content-Type","application/json");
        con.addRequestProperty("token",Token);
        Complaint complaint = new Complaint();
        complaint.Id = ComplaintId;
        complaint.title = title.getText();
        complaint.message = message.getText();
        complaint.resolvedStatus = resolveStatus.isSelected();

        String complaintJson = gson.toJson(complaint);
        System.out.println(complaintJson);
        byte[] out = complaintJson.getBytes(StandardCharsets.UTF_8);
        // For POST only - START
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.write(out);
        os.flush();
        os.close();
        // For POST only - END

        int responseCode = con.getResponseCode();
        System.out.println("POST Response Code :: " + responseCode);
        System.out.println("POST Response Code :: " + con.getContent().toString());

        System.out.println(complaint.Id);
        System.out.println(complaint.title);
        System.out.println(complaint.message);
        System.out.println(complaint.resolvedStatus);

        if (responseCode == HttpURLConnection.HTTP_CREATED) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Complaint updatedComplaint = gson.fromJson(response.toString(),Complaint.class);
            System.out.println(response.toString());
            return updatedComplaint;
            // print result

        } else {

            return null;
        }
    }
}
