package de.b00tload.tools.lastfmtospotifyplaylist.util;

import static de.b00tload.tools.lastfmtospotifyplaylist.LastFMToSpotify.configuration;

public class Logger {

    /**
     * Logs the provided String to console if the option <code>logging.level</code> in the configuration is higher or equal to the provided priority
     * @param string The String to log
     * @param priority The minimum logging level with which the provided string should be logged
     */
    public static void logLn(String string, int priority){
        if(Integer.parseInt(configuration.get("logging.level"))>=priority){
            System.out.println(string);
        }
    }

}
