package com.bluesky.SenderReceiveTest;

/**
 * @author BlueSky
 * @date 2019/8/4
 * Description:
 */
public class SenderReceiveTest {

    public static void main(String[] args) {
        Data data = new Data();
        Thread sender = new Thread(new Sender(data));
        Thread receiver = new Thread(new Receiver(data));

        sender.start();
        receiver.start();
    }
}
