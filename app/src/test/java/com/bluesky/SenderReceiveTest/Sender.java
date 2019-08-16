package com.bluesky.SenderReceiveTest;

import android.util.Log;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author BlueSky
 * @date 2019/8/4
 * Description:
 */
public class Sender implements Runnable {
    private Data data;

    // standard constructors

    public Sender(Data data) {
        this.data = data;
    }

    public void run() {
        String packets[] = {
                "First packet",
                "Second packet",
                "Third packet",
                "Fourth packet",
                "End"
        };

        for (String packet : packets) {
            data.send(packet);

            // Thread.sleep() to mimic heavy server-side processing
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5000));
            } catch (InterruptedException e)  {
                Thread.currentThread().interrupt();
                Log.e("Thread interrupted", e.getMessage());
            }
        }
    }
}
