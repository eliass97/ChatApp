package server;

import common.Signals;
import server.service.ContactManagementService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class ServerConnectionProcessThread extends Thread {

    private final ContactManagementService contactManagementService = ContactManagementService.getInstance();
    private final Socket connection;

    public ServerConnectionProcessThread(Socket connection) {
        this.connection = connection;
    }

    public void run() {
        try {
            Signals.ConnectionType type = Signals.ConnectionType.valueOf(new DataInputStream(connection.getInputStream()).readUTF());
            String username = new DataInputStream(connection.getInputStream()).readUTF();
            String password = new DataInputStream(connection.getInputStream()).readUTF();
            boolean usernameAlreadyUsed = contactManagementService.contactExists(username);
            if (type == Signals.ConnectionType.REGISTER && !usernameAlreadyUsed) {
                register(username, password);
            } else if (type == Signals.ConnectionType.LOGIN && usernameAlreadyUsed) {
                login(username, password);
            } else {
                reject();
            }
        } catch (IOException ioe) {
            System.err.println("server.ServerConnectionProcessThread -> run -> Error during the validation of connection with a client!");
        }
    }

    private void register(String username, String password) {
        try {
            boolean ok = contactManagementService.addContact(connection, username, password);
            if (ok) {
                UUID id = contactManagementService.findIdByUsername(username);
                new InputFromClientThread(id, username, new DataInputStream(connection.getInputStream())).start();
                new DataOutputStream(connection.getOutputStream()).writeUTF(Signals.ConnectionValidation.ACCEPT.name());
                System.out.println(username + " has been connected to the server!");
                contactManagementService.sendMessageToAllContacts("[Server] " + username + " has been connected", id);
            } else {
                reject();
            }
        } catch (IOException ioe) {
            System.err.println("server.ServerConnectionProcessThread -> register -> Error during connection!");
        }
    }

    private void login(String username, String password) {
        try {
            boolean ok = contactManagementService.reconnectContact(connection, username, password);
            if (ok) {
                UUID id = contactManagementService.findIdByUsername(username);
                new InputFromClientThread(id, username, new DataInputStream(connection.getInputStream())).start();
                new DataOutputStream(connection.getOutputStream()).writeUTF(Signals.ConnectionValidation.ACCEPT.name());
                System.out.println(username + " has been reconnected to the server!");
                contactManagementService.sendMessageToAllContacts("[Server] " + username + " has been reconnected", id);
            } else {
                reject();
            }
        } catch (IOException ioe) {
            System.err.println("server.ServerConnectionProcessThread -> login -> Error during connection!");
        }
    }

    private void reject() {
        try {
            new DataOutputStream(connection.getOutputStream()).writeUTF(Signals.ConnectionValidation.REJECT.name());
        } catch (IOException ioe) {
            System.err.println("server.ServerConnectionProcessThread -> close -> Error while rejecting a contact!");
        }
    }
}