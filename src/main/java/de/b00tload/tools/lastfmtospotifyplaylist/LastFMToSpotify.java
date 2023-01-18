package de.b00tload.tools.lastfmtospotifyplaylist;


import de.b00tload.tools.lastfmtospotifyplaylist.arguments.ArgumentHandler;
import de.b00tload.tools.lastfmtospotifyplaylist.arguments.Arguments;
import de.b00tload.tools.lastfmtospotifyplaylist.util.PeriodHelper;
import de.umass.lastfm.Caller;
import de.umass.lastfm.Track;
import de.umass.lastfm.User;

import java.util.Collection;
import java.util.HashMap;

import static de.b00tload.tools.lastfmtospotifyplaylist.util.Logger.logLn;

public class LastFMToSpotify {

    public static final String LINE_SEPERATOR = System.getProperty("line.separator");
    public static HashMap<String, String> configuration;

    public static void main(String[] args) {
        // create hash map with user agent
        configuration = new HashMap<>();
        configuration.put("requests.useragent", "LastFMToSpotify/1.0-Snapshot (" + System.getProperty("os.name") + "; " + System.getProperty("os.arch") + ") Java/" + System.getProperty("java.version"));
        // parse arguments
        for (int a = 0; a < args.length; a++) {
            Arguments arg;
            if (args[a].startsWith("--")) {
                arg = Arguments.getByName(args[a].substring(2));
            } else if (args[a].startsWith("-")) {
                arg = Arguments.getByAlias(args[a].substring(1));
            } else {
                continue;
            }
            if (arg == null) {
                ArgumentHandler.handle(Arguments.HELP);
                return;
            }
            if (args.length - a == 1) {
                ArgumentHandler.handle(arg);
            } else if (args[a + 1].startsWith("--") || args[a + 1].startsWith("-")) {
                ArgumentHandler.handle(arg);
            } else {
                ArgumentHandler.handle(arg, args[a + 1]);
            }
        }


        // Start Progress Bar
        try {
            logLn("Authenticating with Spotify...", 1);
            logLn("Authenticating with LastFM...", 1);
            Caller.getInstance().setUserAgent(configuration.get("requests.useragent"));
            logLn(User.getInfo(configuration.get("lastfm.user"), configuration.get("lastfm.apikey")).getName(), 1);
            logLn("Reading from LastFM...", 1);
            Collection<Track> tracks = User.getTopTracks(configuration.get("lastfm.user"), PeriodHelper.getPeriodByString(configuration.get("lastfm.period")), configuration.get("lastfm.apikey"));
            for (Track track : tracks) {
                logLn(track.getName() + " by " + track.getArtist(), 3);
            }
            logLn("Creating Playlist...", 1);
            //SpotifyApi.Builder build = SpotifyApi.builder();
            //build.setClientId(configuration.get("spotify.clientid"));
            //build.setClientSecret(configuration.get("spotify.secret"));
            //build.setRedirectUri(URI.create("http://localhost:9876/callback/spotify/"));
            //SpotifyApi api = build.build();
            //api.setAccessToken(configuration.get("spotify.access"));
            //api.createPlaylist(api.getCurrentUsersProfile().build().execute().getId(), configuration.get("playlist.name")).setHeader("User-Agent", configuration.get("requests.useragent"));
            logLn("Done.", 1);
//        } catch (IOException | ParseException | SpotifyWebApiException e) {
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //TODO: Implement
    }

}
