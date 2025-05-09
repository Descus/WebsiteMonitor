package de.frauas.Channels;

public class WebhookChannel implements IResponseChannel{

    @Override
    public void send(String message) {
        System.out.println("WebhookChannel: " + message);
    }
}
