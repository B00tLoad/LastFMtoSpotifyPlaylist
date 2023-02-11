package de.b00tload.tools.lastfmtospotifyplaylist.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class VersionChecker {

    private static final String GITHUB_API_BASE_URL = "https://api.github.com/repos/B00tLoad/LastFMToSpotifyPlaylist";


    public static void checkVerion() {
        String currentVersion = VersionChecker.class.getPackage().getImplementationVersion();
        String latestVersion = fetchLatestReleaseVersion();
        if (currentVersion == null) {
            System.out.println("Error: Failed to retrieve current version");
        } else if (latestVersion == null) {
            System.out.println("Error: Failed to retrieve latest release version");
        } else if (currentVersion.compareTo(latestVersion) < 0) {
            System.out.println("A new version is available: " + latestVersion);
        } else {
            System.out.println("You are running the latest version: " + currentVersion);
        }
    }

    private static String fetchLatestReleaseVersion() {
        try (InputStream inputStream = (new URL(GITHUB_API_BASE_URL + "/releases/latest")).openStream()) {
            String response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            JsonObject release = JsonParser.parseString(response).getAsJsonObject();
            return release.get("tag_name").getAsString().substring(1);
        } catch (NullPointerException | IOException ex){
            ex.printStackTrace();
            return null;
        }
    }

}
