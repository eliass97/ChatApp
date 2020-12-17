package server;

import server.service.RequestManagementService;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class InputFromClientThread extends Thread {

    private final RequestManagementService requestManagementService = RequestManagementService.getInstance();
    private final DataInputStream dataInputStream;
    private final String username;
    private final UUID id;


    public InputFromClientThread(UUID id, String username, DataInputStream dataInputStream) {
        this.username = username;
        this.id = id;
        this.dataInputStream = dataInputStream;
    }

    public void run() {
        while (true) {
            try {
                String receive = dataInputStream.readUTF();
                System.out.println(username + ": " + receive);
                requestManagementService.addRequest(new Request(id, receive));
                if (receive.equals("/disconnect")) {
                    break;
                }
            } catch (IOException ioe) {
                System.err.println("server.InputFromClientThread -> run -> Connection with " + username + " lost unexpectedly");
                requestManagementService.addRequest(new Request(id, "/disconnect"));
                break;
            }
        }
    }
}