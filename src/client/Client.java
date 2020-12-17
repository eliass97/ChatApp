package client;

import common.Signals;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class Client {

    private static final String serverIP = "localhost"; //Test IP
    private static final int serverPort = 10101; //Test port
    private OutputStream outputStream = null;
    private InputStream inputStream = null;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private Socket connection = null;
    private String username, password, type;
    private final CountDownLatch latch = new CountDownLatch(2);

    public static void main(String[] args) {
        new Client().init();
    }

    private void init() {
        initializeConnection();
        getUserCredentials();
        sendUserCredentials();
        closeConnection();
    }

    private void initializeConnection() {
        try {
            connection = new Socket(serverIP, serverPort);
            outputStream = connection.getOutputStream();
            inputStream = connection.getInputStream();
            dataInputStream = new DataInputStream(inputStream);
            dataOutputStream = new DataOutputStream(outputStream);
        } catch (UnknownHostException unk) {
            System.err.println("Host is unreachable!");
        } catch (IOException ioe) {
            System.err.println("Error during the connection with the server!");
        }
    }

    private void getUserCredentials() {
        Scanner scan = new Scanner(System.in);
        System.out.print("Login or Register (L/R): ");
        type = scan.nextLine();
        System.out.print("Enter username: ");
        username = scan.nextLine();
        System.out.print("Enter password: ");
        password = scan.nextLine();
    }

    private void sendUserCredentials() {
        try {
            if (type.equals("l") || type.equals("L")) {
                dataOutputStream.writeUTF(Signals.ConnectionType.LOGIN.name());
            } else {
                dataOutputStream.writeUTF(Signals.ConnectionType.REGISTER.name());
            }
            dataOutputStream.writeUTF(username);
            dataOutputStream.flush();
            dataOutputStream.writeUTF(password);
            dataOutputStream.flush();
            receiveReply();
        } catch (IOException ioe) {
            System.err.println("Error during the connection with the server!");
        }
    }

    private void receiveReply() {
        try {
            Signals.ConnectionValidation reply = Signals.ConnectionValidation.valueOf(dataInputStream.readUTF());
            if (reply == Signals.ConnectionValidation.ACCEPT) {
                connectUser();
            } else {
                System.out.println("Connection rejected!");
            }
        } catch (IOException ioe) {
            System.err.println("Error during the connection with the server!");
        }
    }

    private void connectUser() {
        new InputFromServerThread(dataInputStream, latch).start();
        new OutputFromClientThread(dataOutputStream, latch).start();
        if (type.equals("r") || type.equals("R")) {
            System.out.println("Welcome!");
        } else {
            System.out.println("Welcome back!");
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        try {
            inputStream.close();
            outputStream.close();
            connection.close();
        } catch (NullPointerException npe) {
            System.err.println("Failed to connect to the server!");
        } catch (IOException ioe2) {
            System.err.println("Failed to close the connection properly!");
        }
    }
}