package de.frauas.Channels;

public class SmsChannel implements IResponseChannel {
    @Override
    public void send(String message) {
        System.out.println("SmsChannel: " + message);
    }
}
