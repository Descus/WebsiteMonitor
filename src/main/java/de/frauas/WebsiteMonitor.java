package de.frauas;

import de.frauas.Channels.IResponseChannel;
import de.frauas.Channels.MailChannel;
import de.frauas.Channels.SmsChannel;

import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class WebsiteMonitor {

    HashMap<String, User> users = new HashMap<>();
    UpdateManager updateManager = new UpdateManager();
    
    private WebsiteMonitor() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::notifyLoop, 0, Settings.NOTIFICATION_INTERVAL, TimeUnit.of(Settings.TIME_UNIT));
    }
    private WebsiteMonitor registerUser(String name, int frequency, URI website, IResponseChannel channel){
        Subscription subscription = updateManager.addOrGetSubscription(website);
        users.put(name, new User(name, frequency, subscription, channel));
        return this;
    }
    
    private WebsiteMonitor addUserWebsite(String name, URI website){
        if (!users.containsKey(name))
            return this;
        if(website != null)
            users.get(name).addSubscription(updateManager.addOrGetSubscription(website));
        return this;
    }
    
    private WebsiteMonitor addUserResponseChannel(String name, IResponseChannel channel){
        if (!users.containsKey(name))
            return this;
        if(channel != null)
            users.get(name).addResponseChannel(channel);
        return this;
    }
    
    private WebsiteMonitor unregisterUser(String name){
        users.remove(name);
        return this;
    }
    
    public void notifyLoop(){
        users.forEach((s, user) -> {
            user.checkUpdate();
        });
    }
    
    public static void main(String[] args) {
        WebsiteMonitor monitor = new WebsiteMonitor();
        monitor.registerUser("Somebody", 2, URI.create("https://news.ycombinator.com/"), new MailChannel())
                .addUserWebsite("Somebody", URI.create("https://gist.githubusercontent.com/Descus/30d64f7141b03fb6536da4d58f88c0c2/raw/Test"));
        monitor.registerUser("SomebodyElse", 1, URI.create("https://news.ycombinator.com/"), new MailChannel())
                .addUserResponseChannel("SomebodyElse", new SmsChannel());
    }
}
