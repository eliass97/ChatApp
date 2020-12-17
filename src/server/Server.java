package server;

import server.service.ContactManagementService;

import java.util.Scanner;

public class Server {

    private final ContactManagementService contactManagementService = ContactManagementService.getInstance();

    public static void main(String[] args) {
        new Server().init();
    }

    private void init() {
        System.out.println("Connecting to the database...");
        if (contactManagementService.connectDatabase("jdbc:mysql://192.168.2.56:3306/Test?serverTimezone=UTC", "administrator", "123456!a")) {
            System.out.println("Initiating the threads...");
            new ServerSocketConnectionThread().start();
            new ServerRequestProcessThread().start();
            console();
        }
    }

    private void console() {
        System.out.println("Server is online!");
        Scanner scan = new Scanner(System.in);
        while (true) {
            String keyboardInput;
            keyboardInput = scan.nextLine();
            if (keyboardInput.equals("/showcontacts")) {
                contactManagementService.printAllContacts();
            }
            if (keyboardInput.equals("/disconnect")) {
                if (contactManagementService.disconnectDatabase()) {
                    break;
                }
            }
        }
    }
}