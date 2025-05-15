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
    HashMap<String, Subscription> subscriptions = new HashMap<>();
    
    private WebsiteMonitor() {}
    
    public void start(){
        System.out.println("WebsiteMonitor started!");
        //Run Methods in fixed Time intervals
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        executor.scheduleAtFixedRate(this::checkUpdateLoop, 0, Settings.MONITOR_INTERVAL, TimeUnit.of(Settings.TIME_UNIT));
        executor.scheduleAtFixedRate(this::notifyLoop, 1, Settings.NOTIFICATION_INTERVAL, TimeUnit.of(Settings.TIME_UNIT));
    }

    //<editor-fold desc="User Registration">
    public WebsiteMonitor registerUser(String name, int frequency, URI website, IResponseChannel channel){
        Subscription subscription = addOrGetSubscription(website);
        users.put(name, new User(name, frequency, subscription, channel));
        return this;
    }

    public WebsiteMonitor unregisterUser(String name){
        users.remove(name);
        return this;
    }
    //</editor-fold>

    //<editor-fold desc="User Modification">
    public WebsiteMonitor addUserWebsite(String name, URI website){
        if (!users.containsKey(name))
            return this;
        if(website != null)
            users.get(name).addSubscription(addOrGetSubscription(website));
        return this;
    }
    
    public WebsiteMonitor addUserResponseChannel(String name, IResponseChannel channel){
        if (!users.containsKey(name))
            return this;
        if(channel != null)
            users.get(name).addResponseChannel(channel);
        return this;
    }
    //</editor-fold>

    //<editor-fold desc="Loops & Helper">
    private Subscription addOrGetSubscription(URI subscription){
        if (!subscriptions.containsKey(subscription.toString()))
            subscriptions.put(subscription.toString(), new Subscription(subscription));
        return subscriptions.get(subscription.toString());
    }

    private void checkUpdateLoop(){
        System.out.println("Checking for updates ...");
        subscriptions.forEach((_, value) -> value.CheckUpdate());
    }
    
    private void notifyLoop(){
        users.forEach((_, user) -> {
            user.checkUpdate();
        });
    }
    //</editor-fold>
    
    public static void main(String[] args) {
        WebsiteMonitor monitor = new WebsiteMonitor();
        monitor.registerUser("Somebody", 2, URI.create("https://news.ycombinator.com/"), new MailChannel())
                .addUserWebsite("Somebody", URI.create("https://gist.githubusercontent.com/Descus/30d64f7141b03fb6536da4d58f88c0c2/raw/Test"));
        monitor.registerUser("SomebodyElse", 1, URI.create("https://news.ycombinator.com/"), new MailChannel())
                .addUserResponseChannel("SomebodyElse", new SmsChannel());
        monitor.start();
    }
}
