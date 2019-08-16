package com.bluesky.SenderReceiveTest;

import android.util.Log;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author BlueSky
 * @date 2019/8/4
 * Description:
 */
public class Receiver implements Runnable {
    private Data load;

    // standard constructors

    public Receiver(Data load) {
        this.load = load;
    }

    public void run() {
        for(String receivedMessage = load.receive();  !"End".equals(receivedMessage); receivedMessage = load.receive()) {

            System.out.println(receivedMessage);

            // ...
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Log.e("Thread interrupted", e.getMessage());
            }
        }
    }
}
