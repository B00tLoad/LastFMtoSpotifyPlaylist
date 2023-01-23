package de.b00tload.tools.lastfmtospotifyplaylist.arguments;

import java.util.List;

import static de.b00tload.tools.lastfmtospotifyplaylist.LastFMToSpotify.LINE_SEPERATOR;

public enum Arguments {

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
    WEEKLY("weekly", "[Optional]" + LINE_SEPERATOR
                                        + "Creates a playlist from your top tracks from last week.", "--weekly", "W"),
    MONTHLY("monthly", "[Optional, Default]" + LINE_SEPERATOR
                                        + "Creates a playlist from your top tracks from last month.", "--monthly", "M"),
    QUARTERLY("quarterly", "[Optional]" + LINE_SEPERATOR
                                        + "Creates a playlist from your top tracks from last quarter.", "--quarterly", "Q"),
    BIANNUALLY("biannually", "[Optional]" + LINE_SEPERATOR
                                        + "Creates a playlist from your top tracks from last half-year.", "--biannualy", "B"),
    YEARLY("yearly", "[Optional]" + LINE_SEPERATOR
                                        + "Creates a playlist from your top tracks from last year.", "--anually", "A"),
    COVER("coverart", "[Optional]" + LINE_SEPERATOR
                                        + "Will set a cover art for the playlist. Must be jpeg/jpg.", "--coverart <path/to/coverart.jpg>", "ca", "cover");

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

    public static Arguments getByAlias(String alias){
        for(Arguments arg : values()){
            if(List.of(arg.getAliases()).contains(alias)) return arg;
        }
        return null;
    }

    public static Arguments getByName(String name){
        for(Arguments arg : values()){
            if(arg.getName().equalsIgnoreCase(name)) return arg;
        }
        return null;
    }

    public static Arguments resolveByNameOrAlias(String v){
        Arguments ret = getByName(v);
        if(ret != null) return ret;
        ret = getByAlias(v);
        return ret;
    }
}
