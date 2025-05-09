package de.frauas;

import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UpdateManager {
    HashMap<String, Subscription> subscriptions = new HashMap<>();
    
    public UpdateManager(){
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::checkUpdates, 0, Settings.MONITOR_INTERVAL, TimeUnit.of(Settings.TIME_UNIT));
    }
    
    public Subscription addOrGetSubscription(URI subscription){
        if (!subscriptions.containsKey(subscription.toString()))
            subscriptions.put(subscription.toString(), new Subscription(subscription));
        return subscriptions.get(subscription.toString());
    }
    
    public void removeSubscription(Subscription subscription){
        subscriptions.remove(subscription.getWebsite().toString());
    }
    
    public void checkUpdates(){
        System.out.println("Checking for updates ...");
        subscriptions.forEach((_, value) -> value.CheckUpdate());
    }
}
