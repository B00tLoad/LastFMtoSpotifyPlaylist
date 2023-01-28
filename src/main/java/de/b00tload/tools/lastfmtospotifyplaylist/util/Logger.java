package de.b00tload.tools.lastfmtospotifyplaylist.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

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

    public static class LogStream extends PrintStream{

        public LogStream(@NotNull OutputStream out) {
            super(out);
        }

        @Override
        public void write(int b) {
            if(Integer.parseInt(configuration.get("logging.level"))==3) super.write(b);
        }

        @Override
        public void write(byte[] buf) throws IOException {
            if(Integer.parseInt(configuration.get("logging.level"))==3) super.write(buf);
        }

        @Override
        public void write(@NotNull byte[] buf, int off, int len) {
            if(Integer.parseInt(configuration.get("logging.level"))==3) super.write(buf, off, len);
        }

        @Override
        public void writeBytes(byte[] buf) {
            if(Integer.parseInt(configuration.get("logging.level"))==3) super.writeBytes(buf);
        }
    }

}
