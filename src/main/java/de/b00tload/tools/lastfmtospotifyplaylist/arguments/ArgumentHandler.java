package de.b00tload.tools.lastfmtospotifyplaylist.arguments;

import de.b00tload.tools.lastfmtospotifyplaylist.util.FileHelper;
import de.umass.lastfm.Period;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static de.b00tload.tools.lastfmtospotifyplaylist.LastFMToSpotify.LINE_SEPERATOR;
import static de.b00tload.tools.lastfmtospotifyplaylist.LastFMToSpotify.configuration;
import static de.b00tload.tools.lastfmtospotifyplaylist.util.Logger.logLn;

public class ArgumentHandler {

    public static void handle(Arguments argument, @Nullable String value) {
        switch (argument) {
            case HELP -> help(value);
            case VERBOSE -> verbose(value);
            case SECRET -> secret(value);
            case CLIENT -> client(value);
            case TOKEN -> token(value);
            case USER -> user(value);
            case WEEKLY -> period(Period.WEEK);
            case MONTHLY -> period(Period.ONE_MONTH);
            case QUARTERLY -> period(Period.THREE_MONTHS);
            case BIANNUALLY -> period(Period.SIX_MONTHS);
            case YEARLY -> period(Period.TWELVE_MONTHS);
            case COVER -> cover(value);
        }
    }

    public static void handle(Arguments argument) {
        handle(argument, null);
    }

    private static void help(String value) {
        if (value == null) {
            System.out.println("This is a list of all available commands. For more specific help on the argument run --help <argument>.");
            for (Arguments arg : Arguments.values()) {
                String name = arg.getName();
                String description = arg.getDescription();
                System.out.println("____________________");
                System.out.println(name);
                System.out.println("    DESCRIPTION" + LINE_SEPERATOR + description);
            }
            System.exit(200);
        }
        Arguments arg = Arguments.resolveByNameOrAlias(value);
        if (arg == null) {
            System.out.println("This argument is unknown. Use --help to get a list of all arguments");
            System.exit(200);
        }
        String name = arg.getName();
        String description = arg.getDescription();
        String[] aliases = arg.getAliases();
        String usage = arg.getUsage();
        System.out.println(name);
        System.out.println("    DESCRIPTION" + LINE_SEPERATOR + description);
        System.out.println("    USAGE: " + usage);
        StringBuilder aliasString = new StringBuilder();
        for (String alias : aliases) {
            aliasString.append(", -").append(alias);
        }
        System.out.println("    ALIASES:" + aliasString.substring(1));
        System.out.println("____________________");
        System.exit(200);
    }

    private static void verbose(String value) {
        if (value == null) {
            System.out.println("--loglevel must be provided with a numeric log level. Check usage: " + Arguments.VERBOSE.getUsage());
            System.exit(500);
        }
        try {
            int loglevel = Integer.parseInt(value);
            configuration.put("logging.level", String.valueOf(loglevel));
        } catch (NumberFormatException e) {
            System.out.println("LogLevel must be a numeric value.");
            System.exit(500);
        }

    }

    private static void token(String value) {
        if (value == null || value.equalsIgnoreCase("")) {
            System.out.println("--lastfmtoken must be provided with an api token from LastFM. Check usage: " + Arguments.TOKEN.getUsage());
            System.exit(500);
        }
        configuration.put("lastfm.apikey", value);
    }

    private static void user(String value) {
        if (value == null || value.equalsIgnoreCase("")) {
            System.out.println("--lastfmuser must be provided with a LastFM username. Check usage: " + Arguments.USER.getUsage());
            System.exit(500);
        }
        configuration.put("lastfm.user", value);
    }

    private static void client(String value) {
        if (value == null || value.equalsIgnoreCase("")) {
            System.out.println("--spotifyclient must be provided with a client id from Spotify. Check usage: " + Arguments.CLIENT.getUsage());
            System.exit(500);
        }
        configuration.put("spotify.clientid", value);
    }

    private static void secret(String value) {
        if (value == null || value.equalsIgnoreCase("")) {
            System.out.println("--spotifysecret must be provided with a client secret from Spotify. Check usage: " + Arguments.SECRET.getUsage());
            System.exit(500);
        }
        configuration.put("spotify.secret", value);
    }

    private static void period(Period value) {
        configuration.put("lastfm.period", value.getString());
    }

    private static void cover(String value) {
        if (value == null || value.equalsIgnoreCase("") || !Files.exists(Path.of(value.replace("\\", "//")))) {
            System.out.println("--coverart must be provided with a path to a png file. Check usage: " + Arguments.COVER.getUsage());
            System.exit(500);
        }
        String base64 = FileHelper.encodeFileToBase64(new File(value.replace("\\", "//")));
        configuration.put("playlist.cover", base64);
    }
}
