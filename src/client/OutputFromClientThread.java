package client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class OutputFromClientThread extends Thread {

    private final DataOutputStream dataOutputStream;
    private final CountDownLatch latch;

    public OutputFromClientThread(DataOutputStream dataOutputStream, CountDownLatch latch) {
        this.dataOutputStream = dataOutputStream;
        this.latch = latch;
    }

    public void run() {
        Scanner scan = new Scanner(System.in);
        String input = "message";
        try {
            while (!input.equals("/disconnect")) {
                input = scan.nextLine();
                if (!input.equals("")) {
                    dataOutputStream.writeUTF(input);
                    dataOutputStream.flush();
                }
            }
        } catch (IOException ioe) {
            System.err.println("Error during user input!");
        }
        scan.close();
        latch.countDown();
    }
}