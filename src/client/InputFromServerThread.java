package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class InputFromServerThread extends Thread {

    private final DataInputStream dataInputStream;
    private final CountDownLatch latch;

    public InputFromServerThread(DataInputStream dataInputStream, CountDownLatch latch) {
        this.dataInputStream = dataInputStream;
        this.latch = latch;
    }

    public void run() {
        String receive;
        while (true) {
            try {
                receive = dataInputStream.readUTF();
                if (receive.equals("/disconnect")) {
                    break;
                }
                System.out.println(receive);
            } catch (IOException ioe) {
                System.err.println("Error during server message receiving!");
                break;
            }
        }
        latch.countDown();
    }
}