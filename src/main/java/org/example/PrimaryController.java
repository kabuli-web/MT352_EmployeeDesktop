package org.example;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.google.gson.*;
import javafx.scene.text.Text;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

public class PrimaryController {
    Gson gson = new Gson();
    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
    @FXML
    TextField userNameField;
    @FXML
    PasswordField passwordField;
    @FXML
    private Text LoginResult;
    @FXML
    private void login() throws IOException {
        try{
            String response = sendPOST();
            if(response!=null && !response.isEmpty()){
                FileWriter writer = new FileWriter("Token.json");
                gson.toJson(response,writer);
                writer.flush();
                writer.close();
                FileReader fileReader = new FileReader(new File("Token.json"));
                String Token = gson.fromJson(fileReader,String.class);
                fileReader.close();
                System.out.println(Token);
                LoginResult.setText("Login Successful");

                App.setRoot("secondary");
            }else{
                LoginResult.setText("Wrong Credentials");
            }
        }catch (Exception e){
            LoginResult.setText("Failed Api Call"+ e);
        }
    }
    private String LoginUrl;
    private URL url;
    private URLConnection con;

    HttpURLConnection http;
    public PrimaryController() throws IOException {

    }
   private UserLoginResponse PostToLogin() throws IOException {
       url = new URL("http://localhost:8080/352_github/EmployeeAuth");
       con = url.openConnection();
       http = (HttpURLConnection)con;
       http.setRequestMethod("POST"); // PUT is another valid option
       http.setDoOutput(true);
       UserLogin EmployeeLoginData = new UserLogin(userNameField.getText().toString(),passwordField.getText().toString());
       Gson gson = new Gson();
       byte[] out = gson.toJson(EmployeeLoginData).getBytes(StandardCharsets.UTF_8);
       int length = out.length;
       http.setFixedLengthStreamingMode(length);
       http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
       http.connect();
       try(OutputStream os = http.getOutputStream()) {
           System.out.println();
           os.write(out);
       }
       InputStream result = http.getInputStream();
       String resultJson = gson.toJson(result);
       http.disconnect();
       return gson.fromJson(resultJson,UserLoginResponse.class);
   }


    private  void sendGET() throws IOException {
        URL obj = new URL("http://localhost:8080/352_github/EmployeeAuth");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
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

            // print result
            System.out.println(response.toString());
        } else {
            System.out.println("GET request not worked");
        }

    }

    private  String sendPOST() throws IOException {
        URL obj = new URL("http://localhost:8080/352_github/EmployeeAuth");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");

        UserLogin EmployeeLoginData = new UserLogin(userNameField.getText().toString(),passwordField.getText().toString());
        con.setRequestProperty("Content-Type","application/json");
        byte[] out = gson.toJson(EmployeeLoginData).getBytes(StandardCharsets.UTF_8);
        // For POST only - START
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.write(out);
        os.flush();
        os.close();
        // For POST only - END

        int responseCode = con.getResponseCode();
        System.out.println("POST Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            UserLoginResponse loginResponse = gson.fromJson(response.toString(),UserLoginResponse.class);
            System.out.println(response.toString());
            return loginResponse.token;
            // print result

        } else {
            return null;
        }
    }
}
