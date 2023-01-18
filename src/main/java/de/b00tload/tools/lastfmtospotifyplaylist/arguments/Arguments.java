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
    SECRET("spotifysecret", "[]", "--spotifysecret <secret>", "sS", "sSecret");

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
