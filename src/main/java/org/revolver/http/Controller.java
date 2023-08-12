package org.revolver.http;

import com.google.gson.Gson;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class Controller {

    @PostMapping("/music")
    public String parseJson(@RequestBody String jsonBody) {
        Gson gson = new Gson();
        var payload = gson.fromJson(jsonBody, Map.class);

        List<String> files = Collections.unmodifiableList((List<String>) payload.get("files"));
        int index = ((Double) payload.get("index")).intValue();
        Stream.music(files, index);
        return String.format("[%s]\t%s\n", index, files.get(index));

    }
}