package com.bluesky.SenderReceiveTest;

import android.util.Log;

/**
 * @author BlueSky
 * @date 2019/8/4
 * Description:
 */
public class Data {
    private String packet;

    // True if receiver should wait
    // False if sender should wait
    private boolean transfer = true;

    public synchronized void send(String packet) {
        while (!transfer) {
//            try {
//                wait();
//            } catch (InterruptedException e)  {
//                Thread.currentThread().interrupt();
//                Log.e("Thread interrupted", e.getMessage());
//            }
        }
        transfer = false;

        this.packet = packet;
        notifyAll();
    }

    public synchronized String receive() {
        while (transfer) {
//            try {
//                wait();
//            } catch (InterruptedException e)  {
//                Thread.currentThread().interrupt();
//                Log.e("Thread interrupted", e.getMessage());
//            }
        }
        transfer = true;

        notifyAll();
        return packet;
    }
}
