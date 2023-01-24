package de.b00tload.tools.lastfmtospotifyplaylist.arguments;

import de.b00tload.tools.lastfmtospotifyplaylist.util.FileHelper;
import de.b00tload.tools.lastfmtospotifyplaylist.util.TimeHelper;

import de.umass.lastfm.Period;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.WeekFields;
import java.util.Arrays;
import java.util.Locale;

import static de.b00tload.tools.lastfmtospotifyplaylist.LastFMToSpotify.LINE_SEPERATOR;
import static de.b00tload.tools.lastfmtospotifyplaylist.LastFMToSpotify.configuration;
import static de.b00tload.tools.lastfmtospotifyplaylist.util.Logger.logLn;

import java.util.List;
public class ArgumentHandler {

    /**
     * Selects which argument is handled by which method.
     * @param argument the argument to handle
     * @param value the value supplied with the argument
     */
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
            case SPOTIFY_CACHING -> cache(value);
        }
    }

    /**
     * Handles an argument without a value. Will pass <code>null</code> as value.
     * @param argument the argument to handle without value
     */
    public static void handle(Arguments argument) {
        handle(argument, null);
    }

    /**
     * Checks whether every required argument is used.
     * @param args the <code>string[] args</code> passed to psvm.
     * @return whether every required argument is used
     */
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

    /**
     * Checks whether multiple arguments of an exclusivity group are used.
     * @param args the <code>string[] args</code> passed to psvm.
     * @return Whether only one argument out of every exclusivity group is given
     */
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

    /**
     * Displays help into the console.
     * @param value The argument or alias to display help for.
     */
    private static void help(String value) {
        //evaluates whether an argument or alias is given. If not list of all arguments is displayed.
        if (value == null || value.isEmpty()) {
            logLn("This is a list of all available commands. For more specific help on the argument run --help <argument>.", 1);
            for (Arguments arg : Arguments.values()) {
                String name = arg.getName();
                String description = arg.getDescription();
                logLn("____________________", 1);
                logLn(name, 1);
                logLn("    DESCRIPTION" + LINE_SEPERATOR + description, 1);
            }
            System.exit(200);
        }
        //resolves argument or alias. If no match is found tool will exit
        Arguments arg = Arguments.resolveByNameOrAlias(value);
        if (arg == null) {
            logLn("This argument is unknown. Use --help to get a list of all arguments", 1);
            System.exit(200);
        }
        //Displays information about the selected argument
        String name = arg.getName();
        String description = arg.getDescription();
        String[] aliases = arg.getAliases();
        String usage = arg.getUsage();
        logLn(name ,1);
        logLn("    DESCRIPTION" + LINE_SEPERATOR + description, 1);
        logLn("    USAGE: " + usage, 1);
        StringBuilder aliasString = new StringBuilder();
        for (String alias : aliases) {
            aliasString.append(", -").append(alias);
        }
        logLn("    ALIASES:" + aliasString.substring(1), 1);
        logLn("____________________", 1);
        //tool exits
        System.exit(200);
    }

    /**
     * Sets the log level.
     *   - 0: Quiet      Will run completely quietly
     *   - 1: Default    Will only show progress
     *   - 2: Verbose    Will echo current step being worked on
     *   - 3: Debug      Will give specific information on what excactly the tool is doing
     * @param value an int 0 - 3
     */
    private static void verbose(String value) {

        //evaluates whether value is set
        if (value == null || value.isEmpty()) {
            logLn("--loglevel must be provided with a numeric log level. Check usage: " + Arguments.VERBOSE.getUsage(), 1);
            System.exit(500);
        }
        try {
            int loglevel = Integer.parseInt(value);
            configuration.put("logging.level", String.valueOf(loglevel));
        } catch (NumberFormatException e) {
            logLn("LogLevel must be a numeric value.", 1);
            System.exit(500);
        }

    }

    /**
     * Sets the LastFM api token into the configuration
     * @param value The LastFM api token
     */
    private static void token(String value) {
        //evaluates whether value is set
        if (value == null || value.isEmpty()) {
            logLn("--lastfmtoken must be provided with an api token from LastFM. Check usage: " + Arguments.TOKEN.getUsage(), 1);
            System.exit(500);
        }
        configuration.put("lastfm.apikey", value);
    }

    /**
     * Sets the LastFM username into the configuration
     * @param value The LastFM username
     */
    private static void user(String value) {
        //evaluates whether value is set
        if (value == null || value.isEmpty()) {
            logLn("--lastfmuser must be provided with a LastFM username. Check usage: " + Arguments.USER.getUsage(), 1);
            System.exit(500);
        }
        configuration.put("lastfm.user", value);
    }

    /**
     * Sets the spotify client id into the configuration
     * @param value The Spotify Client ID
     */
    private static void client(String value) {
        //evaluates whether value is set
        if (value == null || value.isEmpty()) {
            logLn("--spotifyclient must be provided with a client id from Spotify. Check usage: " + Arguments.CLIENT.getUsage(), 1);
            System.exit(500);
        }
        configuration.put("spotify.clientid", value);
    }

    /**
     * Sets the spotify client secret into the configuration
     * @param value The Spotify Client Secret
     */
    private static void secret(String value) {
        //evaluates whether value is set
        if (value == null || value.isEmpty()) {
            logLn("--spotifysecret must be provided with a client secret from Spotify. Check usage: " + Arguments.SECRET.getUsage(), 1);
            System.exit(500);
        }
        configuration.put("spotify.secret", value);
    }

    /**
     * Sets the Period to be loaded from LastFM. Possibilities: WEEKLY, MONTHLY, QUARTERLY, BIANNUALLY, YEARLY
     * @param value the LastFM Period to be loaded
     */
    private static void period(Period value) {
        configuration.put("lastfm.period", value.getString());

    }

    /**
     *  Takes a path to a jpeg image and turns it into base64 encoded image data which is subsequently saved into <code>playlist.cover</code> in the configuration.
     * @param value The filepath to the cover image. Must be jpeg.
     */
    private static void cover(String value) {
        //evaluating if value is provided and of content-type image/jpeg
        try {
            if(value == null){
                logLn("--coverart must be provided with a path to a jpeg file. Check usage: " + Arguments.COVER.getUsage(), 1);
                System.exit(500);
            }
            Path path = Path.of(value.replace("\\", "//"));
            if (value.isEmpty() || !Files.exists(path) || !Files.probeContentType(path).equalsIgnoreCase("image/jpeg")) {
                logLn("--coverart must be provided with a path to a jpeg file. Check usage: " + Arguments.COVER.getUsage(), 1);
                System.exit(500);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //encodes image data into base64
        String base64 = FileHelper.encodeFileToBase64(new File(value.replace("\\", "//")));
        //saves to configuration
        configuration.put("playlist.cover", base64);
    }

    /**
     * Sets the access modifier. Can either be <code>"collaborative"</code> or <code>"public"</code>.
     * @param value "collaborative" or "public"
     */
    private static void access(String value) {
        switch (value) {
            case "collaborative" -> configuration.put("playlist.collab", "collab");
            case "public" -> configuration.put("playlist.public", "public");
        }
    }

    /**
     * Sets the <code>playlist.name</code> value in the configuration. Supports templating and date offsetting. See <a href="https://github.com/B00tLoad/LastFMtoSpotifyPlaylist/wiki/Filename-Templating">wiki</a> for further documentation on templating.
     * @param value The playlist name string, including templating
     */
    private static void name(String value) {
        //evaluating if value is provided
        if (value == null || value.isEmpty()) {
            logLn("--playlistname must be provided with a playlist name. Check usage: " + Arguments.NAME.getUsage(), 1);
            System.exit(500);
        }
        //creating current datetime and users locale
        LocalDateTime now = LocalDateTime.now(Clock.systemDefaultZone());
        Locale loc = Locale.forLanguageTag(System.getProperty("user.country"));

        //checking for the date offset flag and applying it if necessary
        if(value.matches("(%\\$-?\\d*\\$).*")){
            int offsetDays = Integer.parseInt(value.substring(2).split("\\$")[0]);
            now = offsetDays < 0 ?  now.minusDays(Math.abs(offsetDays)) : now.plusDays(Math.abs(offsetDays));
        }

        //replacing datetime template
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

    /**
     * Sets the <code>cache.crypto</code> value in the configuration
     * @param value a password encrypting the credential cache
     */
    public static void cache(String value){
        //evaluating if value is provided
        if (value == null || value.isEmpty()) {
            logLn("--spotifycache must be provided with a password. Check usage: " + Arguments.SPOTIFY_CACHING.getUsage(), 1);
            System.exit(500);
        }
        configuration.put("cache.crypto", value);
    }
}
