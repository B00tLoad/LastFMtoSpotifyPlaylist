package de.b00tload.tools.lastfmtospotifyplaylist.arguments;

import java.util.List;

import static de.b00tload.tools.lastfmtospotifyplaylist.LastFMToSpotify.LINE_SEPERATOR;

public enum Arguments {

    //Defining arguments
    HELP("help", "[Optional, will not execute tool] " + LINE_SEPERATOR
            + "Shows a list of all commands or, if provided, help for a given command.", "--help [argument]", "h", "?"),
    VERBOSE("loglevel", "[Optional] " + LINE_SEPERATOR
                                        + "Sets the loglevel. May flood the console. Use carefully." + LINE_SEPERATOR
                                        + "Possible loglevels:" + LINE_SEPERATOR
                                        + "  - 0: Quiet      Will run completely quietly" + LINE_SEPERATOR
                                        + "  - 1: Default    Will only show progress" + LINE_SEPERATOR
                                        + "  - 2: Verbose    Will echo current step being worked on" + LINE_SEPERATOR
                                        + "  - 3: Debug      Will give specific information on what excactly the tool is doing",
            "--loglevel <level>", "log", "l"),
    SECRET("spotifysecret", "[Required]" + LINE_SEPERATOR
                                        + "Sets the spotify client secret.", "--spotifysecret <secret>", "sS", "sSecret"),
    CLIENT("spotifyclient", "[Required]" + LINE_SEPERATOR
                                        + "Sets the spotify cliend id.", "--spotifyclient <clientid>", "sC", "sClient"),
    TOKEN("lastfmtoken", "[Required]" + LINE_SEPERATOR
                                        + "Sets the LastFM API token.", "--lastfmtoken <apitoken>", "lT", "lToken"),
    USER("lastfmuser", "[Required]" + LINE_SEPERATOR
                                        + "Sets the LastFM API token.", "--lastfmuser <username>", "lU", "lUser"),
    WEEKLY("weekly", "[Optional] [EXCLUSIVE: weekly, monthly, quarterly, biannually, annually]" + LINE_SEPERATOR
                                        + "Creates a playlist from your top tracks from last week.", "--weekly", "W"),
    MONTHLY("monthly", "[Optional, Default] [EXCLUSIVE: weekly, monthly, quarterly, biannually, annually]" + LINE_SEPERATOR
                                        + "Creates a playlist from your top tracks from last month.", "--monthly", "M"),
    QUARTERLY("quarterly", "[Optional] [EXCLUSIVE: weekly, monthly, quarterly, biannually, annually]" + LINE_SEPERATOR
                                        + "Creates a playlist from your top tracks from last quarter.", "--quarterly", "Q"),
    BIANNUALLY("biannually", "[Optional] [EXCLUSIVE: weekly, monthly, quarterly, biannually, annually]" + LINE_SEPERATOR
                                        + "Creates a playlist from your top tracks from last half-year.", "--biannualy", "B"),
    YEARLY("annually", "[Optional] [EXCLUSIVE: weekly, monthly, quarterly, biannually, annually]" + LINE_SEPERATOR
                                        + "Creates a playlist from your top tracks from last year.", "--anually", "A"),
    COVER("coverart", "[Optional]" + LINE_SEPERATOR
                                        + "Will set a cover art for the playlist. Must be jpeg/jpg.", "--coverart <path/to/coverart.jpg>", "ca", "cover"),
    NAME("playlistname", "[Optional]" + LINE_SEPERATOR
                                        + "Sets the playlist name. Supports templating. Refer to https://github.com/B00tLoad/LastFMtoSpotifyPlaylist/wiki/Filename-Templating.", "--playlistname <name>", "pName", "pN"),
    PUBLIC("public", "[Optional] [EXCLUSIVE: public, collaborative]" + LINE_SEPERATOR
                                        + "Makes the playlist public.", "--public", "pP"),
    COLLABORATIVE("collaborative", "[Optional] [EXCLUSIVE: public, collaborative]" + LINE_SEPERATOR
                                        + "Makes the playlist collaborative.", "--collaborative", "pC"),
    SPOTIFY_CACHING("spotifycache", "[Optional]" + LINE_SEPERATOR
                                        + "Saves and (if possible) retrieves auth tokens from a locally saved file.", "--spotifycache <password>", "sTk");

    private final String name;
    private final String description;
    private final String usage;
    private final String[] aliases;

    Arguments(String name, String description, String usage, String... aliases){
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.aliases = aliases;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }

    public String[] getAliases() {
        return aliases;
    }

    /**
     * Resolves an alias-String into an Argument-object
     * @param alias The alias to resolve
     * @return The resolved Argument, null if alias does not exist.
     */
    public static Arguments getByAlias(String alias){
        for(Arguments arg : values()){
            if(List.of(arg.getAliases()).contains(alias)) return arg;
        }
        return null;
    }

    /**
     * Resolves an argument-String into an Argument-object
     * @param name The argument to resolve
     * @return The resolved Argument, null if alias does not exist.
     */
    public static Arguments getByName(String name){
        for(Arguments arg : values()){
            if(arg.getName().equalsIgnoreCase(name)) return arg;
        }
        return null;
    }

    /**
     * Resolves an argument- or alias-String into an Argument-object
     * @param v The alias or argument to resolve
     * @return The resolved Argument, null if alias does not exist.
     */
    public static Arguments resolveByNameOrAlias(String v){
        Arguments ret = getByName(v);
        if(ret != null) return ret;
        ret = getByAlias(v);
        return ret;
    }

    /**
     * Builds a String consisting of the argument name and all aliases listed
     * @return "--$argument ($aliases)"
     */
    @Override
    public String toString() {
        StringBuilder args = new StringBuilder("--").append(this.name).append(" (");
        for(String alias : this.getAliases()){
            args.append("-").append(alias).append(", ");
        }
        args.delete(args.length()-2, args.length());
        args.append(")");
        return args.toString();
    }
}
