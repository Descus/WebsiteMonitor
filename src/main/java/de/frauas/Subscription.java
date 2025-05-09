package de.frauas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.time.LocalDateTime;


public class Subscription {
    private final URI website;
    LocalDateTime lastUpdate;
    String content = "";

    public Subscription(URI website) {
        this.website = website;
    }

    public URI getWebsite() {
        return website;
    }

    public void CheckUpdate() {
        BufferedReader br;
        String line;
        StringBuilder sb = new StringBuilder();
        try (InputStream is = website.toURL().openStream()) {
            br = new BufferedReader(new InputStreamReader(is));

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            if (content.hashCode() != sb.toString().hashCode()) {
                content = sb.toString();
                lastUpdate = LocalDateTime.now();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
