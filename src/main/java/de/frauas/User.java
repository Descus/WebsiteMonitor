package de.frauas;

import de.frauas.Channels.IResponseChannel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User {
    private final String name;
    private final int frequency;
    private LocalDateTime lastNotification;
    private final List<IResponseChannel> responseChannels = new ArrayList<>();

    private final HashMap<Subscription, LocalDateTime> subscriptions = new HashMap<>();

    public User(String name, int frequency, Subscription subscription, IResponseChannel responseChannels) {
        this.name = name;
        this.frequency = frequency;
        addSubscription(subscription);
        addResponseChannel(responseChannels);
    }

    public void addSubscription(Subscription subscription) {
        subscriptions.put(subscription, null);
    }
    
    public void addResponseChannel(IResponseChannel responseChannel) {
        this.responseChannels.add(responseChannel);
    }
    
    public void removeResponseChannel(IResponseChannel responseChannel) {
        this.responseChannels.remove(responseChannel);
    }

    public void cancelSubscription(Subscription subscription){
        subscriptions.forEach((sub, _) -> {
            if (sub.equals(subscription)) {
                subscriptions.remove(subscription);
            }
        });
    }

    private void notify(String message) {
        if (lastNotification == null || lastNotification.plus((long) frequency * Settings.NOTIFICATION_INTERVAL, Settings.TIME_UNIT).isBefore(LocalDateTime.now())){
            responseChannels.forEach(channel -> channel.send(message));
            lastNotification = LocalDateTime.now();
        }
    }
    
    public void checkUpdate(){
        subscriptions.forEach((sub, lastUpdate) -> {
            if(sub.lastUpdate != null && ( lastUpdate == null || sub.lastUpdate.isAfter(lastUpdate))) {
                notify( LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) + " - " + name + " - " + sub.getWebsite().toString() + " has changed!");
                subscriptions.put(sub, sub.lastUpdate);
            }
        });
    }

    public String getName() {
        return name;
    }
}
