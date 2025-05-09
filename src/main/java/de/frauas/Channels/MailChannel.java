package de.frauas.Channels;

public class MailChannel implements IResponseChannel {
    @Override
    public void send(String message) {
        System.out.println("MailChannel: " + message);
    }
}
