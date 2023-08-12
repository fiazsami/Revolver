package org.revolver.http;

import org.springframework.stereotype.Component;


import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Client {

    public static void sendFiles(List<String> files, Integer index, String stream) {
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("files", files);
        payload.put("index", index);
        Gson gson = new Gson();
        String json = gson.toJson(payload);
        String response = sendJson("http://localhost:8080/" + stream, json);
        System.out.println(response);
    }

    public static String sendJson(String url, String json) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred";
        }
    }

}


