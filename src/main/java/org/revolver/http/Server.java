package org.revolver.http;

import java.util.List;

import org.revolver.stream.Streamer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Server {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(Server.class);
        List<String> files = Streamer.getFiles(args[0]);
        Thread.sleep(1000);
        Client.sendFiles(0, files);
    }

}