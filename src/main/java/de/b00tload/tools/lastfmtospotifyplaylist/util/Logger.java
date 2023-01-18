package de.b00tload.tools.lastfmtospotifyplaylist.util;

import static de.b00tload.tools.lastfmtospotifyplaylist.LastFMToSpotify.configuration;

public class Logger {

    public static void logLn(String string, int priority){
        if(Integer.parseInt(configuration.get("verbose.level")))
    }

}
