package de.b00tload.tools.lastfmtospotifyplaylist;


import com.neovisionaries.i18n.CountryCode;
import de.b00tload.tools.lastfmtospotifyplaylist.arguments.ArgumentHandler;
import de.b00tload.tools.lastfmtospotifyplaylist.arguments.Arguments;
import de.b00tload.tools.lastfmtospotifyplaylist.util.PeriodHelper;
import de.b00tload.tools.lastfmtospotifyplaylist.util.TokenHelper;
import de.umass.lastfm.Caller;
import de.umass.lastfm.Track;
import de.umass.lastfm.User;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.http.HttpStatus;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.Playlist;

import java.net.URI;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static de.b00tload.tools.lastfmtospotifyplaylist.util.Logger.logLn;

public class LastFMToSpotify {

    public static final String LINE_SEPERATOR = System.getProperty("line.separator");
    public static HashMap<String, String> configuration;

    public static void main(String[] args) {
        // create hash map with user agent
        configuration = new HashMap<>();
        configuration.put("requests.useragent", "LastFMToSpotify/1.0-Snapshot (" + System.getProperty("os.name") + "; " + System.getProperty("os.arch") + ") Java/" + System.getProperty("java.version"));
        configuration.put("playlist.name", "LastFMToSpotify@" + LocalDateTime.now(Clock.systemDefaultZone()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
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

        try {
            logLn("Authenticating with Spotify...", 1);
            SpotifyApi.Builder build = SpotifyApi.builder();
            build.setClientId(configuration.get("spotify.clientid"));
            build.setClientSecret(configuration.get("spotify.secret"));
            build.setRedirectUri(URI.create("http://localhost:9876/callback/spotify/"));
            SpotifyApi api = build.build();
            AtomicBoolean waiting = new AtomicBoolean(true);
            try (Javalin webserver = Javalin.create().start(9876)) {
                Runtime.getRuntime().addShutdownHook(new Thread(webserver::stop));
                webserver.get("/callback/spotify", ctx -> {
                    if(ctx.queryParamMap().containsKey("code")) {
                        AuthorizationCodeCredentials cred = api.authorizationCode(ctx.queryParam("code")).build().execute();
                        configuration.put("spotify.access", cred.getAccessToken());
                        if(configuration.containsKey("spotify.saveaccess")) TokenHelper.saveTokens(cred);
                        ctx.result("success. <script>let win = window.open(null, '_self');win.close();</script>").contentType(ContentType.TEXT_HTML).status(HttpStatus.OK);
                        waiting.set(false);
                    } else {
                        logLn("Error: Spotify authorization failed."+LINE_SEPERATOR+ctx.queryParam("error"), 1);
                        System.exit(500);
                    }
                });
                logLn("Waiting for Spotify authorization.", 1);
                //TODO: Open auth page in Browser
                while (waiting.get());
            }
            logLn("Authenticating with LastFM...", 1);
            Caller.getInstance().setApiRootUrl("https://ws.audioscrobbler.com/2.0/");
            Caller.getInstance().setUserAgent(configuration.get("requests.useragent"));
            logLn(User.getInfo(configuration.get("lastfm.user"), configuration.get("lastfm.apikey")).getName(), 1);
            logLn("Reading from LastFM...", 1);
            Collection<Track> tracks = User.getTopTracks(configuration.get("lastfm.user"), PeriodHelper.getPeriodByString(configuration.get("lastfm.period")), configuration.get("lastfm.apikey"));
            logLn("Creating Playlist...", 1);
            api.setAccessToken(configuration.get("spotify.access"));
            Playlist list = api.createPlaylist(api.getCurrentUsersProfile().build().execute().getId(), configuration.get("playlist.name")).public_(configuration.containsKey("playlist.public")||configuration.containsKey("playlist.collab")).collaborative(configuration.containsKey("playlist.collab")).setHeader("User-Agent", configuration.get("requests.useragent")).build().execute();
            List<String> adders = new LinkedList<>();
            String charsToReplace = "[\"']"; //regex for " and '
            for (Track track : tracks) {
                logLn("Adding " + track.getName() + " by " + track.getArtist(), 3);
                StringBuilder searchQuery = new StringBuilder();
                searchQuery.append("track:").append(track.getName().replaceAll(charsToReplace, ""));
                searchQuery.append(" artist:").append(track.getArtist());
                if(track.getAlbum()!=null&&!track.getAlbum().equalsIgnoreCase("null")&&!track.getAlbum().isEmpty())
                    searchQuery.append(" album:").append(track.getAlbum());
                logLn("Search query: " + searchQuery, 3);
                se.michaelthelin.spotify.model_objects.specification.Track[] add = api.searchTracks(searchQuery.toString()).market(CountryCode.DE).setHeader("User-Agent", configuration.get("requests.useragent")).build().execute().getItems();
                if(add.length!=0) {
                    adders.add(add[0].getUri());
                    logLn("Added " + add[0].getName() + " to " + configuration.get("playlist.name"), 3);
                }
            }
            api.addItemsToPlaylist(list.getId(), adders.toArray(String[]::new)).build().execute();
            if(configuration.containsKey(configuration.get("playlist.cover"))) api.uploadCustomPlaylistCoverImage(list.getId()).image_data(configuration.get("playlist.cover")).build().execute();
            logLn("Done.", 1);
//        } catch (IOException | ParseException | SpotifyWebApiException e) {
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
