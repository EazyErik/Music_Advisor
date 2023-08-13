package advisor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Client {

     public void open(String code, String access, String clientId) throws IOException, InterruptedException {
         //todo: test failed : Wrong answer in test #1 Not found correct access token in the result. Make sure, that you use the server from the command line arguments to access the token....
         System.out.println(access + "/api/token");
String authString = clientId + ":" + "2848e7c861244f26b1447aba0c017797";
        String base64Auth = Base64.getEncoder().encodeToString(authString.getBytes());
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Basic " + base64Auth)
                .uri(URI.create(access + "/api/token"))
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=authorization_code&code=" + code + "&redirect_uri=http://localhost:8080"))
                .build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
         System.out.println("response:");
         // Process the response containing the access token and other information
         System.out.println(response.body());
         System.out.println("---SUCCESS---");


    }

    public void open1(String code,String access) throws IOException {
        String authOptionsUrl = access + "/api/token";
        System.out.println(access);
        String authString = "64813a69782747bab8d53a9652d7c29e" + ":" + "2848e7c861244f26b1447aba0c017797";
        String base64Auth = Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8));

        // Set up the HTTP connection
        URL url = new URL(authOptionsUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Basic " + base64Auth);
        conn.setDoOutput(true);

        // Prepare the request body
        String requestBody = "code=" + code + "&redirect_uri=http://localhost:8080&grant_type=authorization_code";
        byte[] postData = requestBody.getBytes(StandardCharsets.UTF_8);
        conn.getOutputStream().write(postData);
        // Get the response
        int responseCode = conn.getResponseCode();
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("response:");
            // Process the response containing the access token and other information
            System.out.println(response.toString());
            System.out.println("---SUCCESS---");
            // Parse the JSON response and extract the access token and other relevant data
        } else {
            // Handle error response
            System.out.println("Error: " + responseCode);
            System.out.println(response.toString());
        }

        // Close the connection
        conn.disconnect();
    }
}



