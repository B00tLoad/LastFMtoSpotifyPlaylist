package de.b00tload.tools.lastfmtospotifyplaylist;


import de.b00tload.tools.lastfmtospotifyplaylist.arguments.ArgumentHandler;
import de.b00tload.tools.lastfmtospotifyplaylist.arguments.Arguments;
import de.umass.lastfm.Caller;
import de.umass.lastfm.Period;
import de.umass.lastfm.User;
import me.tongfei.progressbar.ProgressBar;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

import static de.b00tload.tools.lastfmtospotifyplaylist.util.Logger.logLn;

public class LastFMToSpotify {

    public static final String LINE_SEPERATOR = System.getProperty("line.separator");
    public static HashMap<String, String> configuration;

    public static void main(String[] args) {
        configuration = new HashMap<>();
        configuration.put("requests.useragent", "LastFMToSpotify/1.0-Snapshot (" + System.getProperty("os.name") + "; " + System.getProperty("os.arch") + ") Java/"+System.getProperty("java.version"));
        for(int a = 0; a<args.length; a++){
            Arguments arg;
            if(args[a].startsWith("--")){
                arg = Arguments.getByName(args[a].substring(2));
            } else if(args[a].startsWith("-")){
                arg = Arguments.getByAlias(args[a].substring(1));
            } else {
                break;
            }
            if(arg==null) {
                ArgumentHandler.handle(Arguments.HELP);
                return;
            }
            if(args.length-a==1){
                ArgumentHandler.handle(arg);
            } else if(args[a+1].startsWith("--") || args[a+1].startsWith("-")){
                ArgumentHandler.handle(arg);
            } else {
                ArgumentHandler.handle(arg, args[a+1]);
            }
        }


        // Start Progress Bar
        try (ProgressBar pb = new ProgressBar("LastFM -> Spotify Playlist", 4)) {
            for (int progress = 1; progress<=5; progress++) {
                pb.step(); // step by 1
                switch (progress) {
                    case 1:
                        pb.setExtraMessage("Authenticating with Spotify...");

                        break;
                    case 2:
                        pb.setExtraMessage("Authenticating with LastFM...");
                        Caller.getInstance().setUserAgent(configuration.get("requests.useragent"));
                        logLn(User.getInfo(configuration.get("lastfm.user"), configuration.get("lastfm.apikey")).getName(), 1);
                        break;
                    case 3:
                        pb.setExtraMessage("Reading from LastFM...");
                        User.getTopTracks(configuration.get("lastfm.user"), Period.ONE_MONTH, configuration.get("lastfm.apikey"));
                        break;
                    case 4:
                        pb.setExtraMessage("Creating Playlist...");
                        SpotifyApi.Builder build = SpotifyApi.builder();
                        build.setClientId(configuration.get("spotify.clientid"));
                        build.setClientSecret(configuration.get("spotify.secret"));
                        build.setRedirectUri(URI.create("http://localhost:9876/callback/spotify/"));
                        SpotifyApi api = build.build();
                        api.setAccessToken(configuration.get("spotify.access"));
                        api.createPlaylist(api.getCurrentUsersProfile().build().execute().getId(), configuration.get("playlist.name")).setHeader("User-Agent", configuration.get("requests.useragent"));
                        break;
                    case 5:
                        pb.setExtraMessage("Done.");
                        break;
                }
            }
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            throw new RuntimeException(e);
        }
        //TODO: Implement
    }

}
