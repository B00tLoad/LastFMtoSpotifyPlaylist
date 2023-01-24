package de.b00tload.tools.lastfmtospotifyplaylist.arguments;

import de.b00tload.tools.lastfmtospotifyplaylist.util.FileHelper;
import de.b00tload.tools.lastfmtospotifyplaylist.util.TimeHelper;

import de.umass.lastfm.Period;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Arrays;
import java.util.Locale;

import static de.b00tload.tools.lastfmtospotifyplaylist.LastFMToSpotify.LINE_SEPERATOR;
import static de.b00tload.tools.lastfmtospotifyplaylist.LastFMToSpotify.configuration;
import static de.b00tload.tools.lastfmtospotifyplaylist.util.Logger.logLn;

import java.util.List;

import javax.xml.crypto.Data;

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
            case NAME -> name(value);
            case PUBLIC -> access("public");
            case COLLABORATIVE -> access("collaborative");
        }
    }

    public static void handle(Arguments argument) {
        handle(argument, null);
    }


    public static boolean checkArguments(String[] args) {
        // check if all required arguments are given
        Arguments[] required = {Arguments.SECRET, Arguments.CLIENT, Arguments.TOKEN, Arguments.USER};
        for (Arguments argument : required) {
            boolean found = List.of(args).contains("--" + argument.getName());
            // check all aliases
            for (String alias : argument.getAliases()) {
                // if one alias is found check next argument
                if (List.of(args).contains("-"+alias)) {
                    found = true;
                    break;
                }
            }
            // else return false
            if (!found) {
                logLn("Missing required argument " + argument.getName(), 1);
                return false;
            }
        }
        return true;
    }

    public static boolean checkExclusivity(String[] args){
        Arguments[][] exclusive = {{Arguments.PUBLIC, Arguments.COLLABORATIVE}, {Arguments.WEEKLY, Arguments.MONTHLY, Arguments.QUARTERLY, Arguments.BIANNUALLY, Arguments.YEARLY}};
        for(Arguments[] arguments : exclusive){
            int count = 0;
            for(Arguments argument : arguments){
                if(List.of(args).contains("--"+argument.getName())) count++;
                for(String alias : argument.getAliases()) {
                    if(List.of(args).contains("-"+alias)) count++;
                }
                if(count>1){
                    logLn("You may only use one flag out of every exclusive group." + LINE_SEPERATOR +
                            "This exclusive group contains of: " + Arrays.toString(arguments), 1);
                    return false;
                }
            }
        }
        return true;
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

    private static void access(String value) {
        switch (value) {
            case "collaborative" -> configuration.put("playlist.collab", "collab");
            case "public" -> configuration.put("playlist.public", "public");
        }
    }

    private static void name(String value) {
        if (value == null || value.equalsIgnoreCase("")) {
            System.out.println("--playlistname must be provided with a playlist name. Check usage: " + Arguments.NAME.getUsage());
            System.exit(500);
        }
        LocalDateTime now = LocalDateTime.now(Clock.systemDefaultZone());
        Locale loc = Locale.forLanguageTag(System.getProperty("user.country"));
        if(value.matches("(%\\$-?\\d*\\$).*")){
            int offsetDays = Integer.parseInt(value.substring(2).split("\\$")[0]);
            now = offsetDays < 0 ?  now.minusDays(Math.abs(offsetDays)) : now.plusDays(Math.abs(offsetDays));
        }
        String name = value.replace("%YYYY", String.valueOf(now.getYear())).replace("%YY", String.valueOf(now.getYear()).substring(2))
                .replace("%MMMM", now.getMonth().name().charAt(0) + now.getMonth().name().toLowerCase().substring(1))
                .replace("%MMM", now.getMonth().getDisplayName(TextStyle.FULL, loc))
                .replace("%MM", (String.valueOf(now.getMonth().getValue()).length() == 1 ? "0" + now.getMonth().getValue() : String.valueOf(now.getMonth().getValue())))
                .replace("%M", String.valueOf(now.getMonth().getValue()))
                .replace("%DD", (String.valueOf(now.getDayOfMonth()).length() == 1 ? "0" + now.getDayOfMonth() : String.valueOf(now.getDayOfMonth())))
                .replace("%D", String.valueOf(now.getDayOfMonth()))
                .replace("%DDDD", now.getDayOfWeek().getDisplayName(TextStyle.FULL, loc))
                .replace("%DDD", now.getDayOfWeek().getDisplayName(TextStyle.SHORT, loc))
                .replace("%WW", (String.valueOf(now.get(WeekFields.of(loc).weekOfWeekBasedYear())).length() == 1 ? "0" + now.get(WeekFields.of(loc).weekOfWeekBasedYear()) : String.valueOf(now.get(WeekFields.of(loc).weekOfWeekBasedYear()))))
                .replace("%W", String.valueOf(now.get(WeekFields.of(loc).weekOfWeekBasedYear())))
                .replace("%HH", (String.valueOf(now.get(ChronoField.HOUR_OF_DAY)).length() == 1 ? "0" + now.get(ChronoField.HOUR_OF_DAY) : String.valueOf(now.get(ChronoField.HOUR_OF_DAY))))
                .replace("%H", String.valueOf(now.get(ChronoField.HOUR_OF_DAY)))
                .replace("%hh", (String.valueOf(now.get(ChronoField.HOUR_OF_AMPM)).length() == 1 ? "0" + now.get(ChronoField.HOUR_OF_AMPM) : String.valueOf(now.get(ChronoField.HOUR_OF_AMPM))))
                .replace("%h", String.valueOf(now.get(ChronoField.HOUR_OF_AMPM)))
                .replace("%P", now.get(ChronoField.AMPM_OF_DAY)==0 ? "AM" : "PM")
                .replace("%p", now.get(ChronoField.AMPM_OF_DAY)==0 ? "am" : "pm")
                .replace("%mm", (String.valueOf(now.getMinute()).length() == 1 ? "0" + now.getMinute() : String.valueOf(now.getMinute())))
                .replace("%m", String.valueOf(now.getMinute()))
                .replace("%ss", (String.valueOf(now.getSecond()).length() == 1 ? "0" + now.getSecond() : String.valueOf(now.getSecond())))
                .replace("%s", String.valueOf(now.getSecond()))
                .replace("%o", TimeHelper.getUTCOffset(now))
                .replaceAll("%\\$-?\\d*\\$", "")
                .replace("%%", "%");

        configuration.put("playlist.name", name);
    }
}
