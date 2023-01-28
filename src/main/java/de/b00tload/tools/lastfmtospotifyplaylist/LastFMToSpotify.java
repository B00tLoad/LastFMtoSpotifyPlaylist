package de.b00tload.tools.lastfmtospotifyplaylist;


import com.neovisionaries.i18n.CountryCode;
import de.b00tload.tools.lastfmtospotifyplaylist.arguments.ArgumentHandler;
import de.b00tload.tools.lastfmtospotifyplaylist.arguments.Arguments;
import de.b00tload.tools.lastfmtospotifyplaylist.util.PeriodHelper;

import de.b00tload.tools.lastfmtospotifyplaylist.util.SpotifyCredentials;
import de.b00tload.tools.lastfmtospotifyplaylist.util.TokenHelper;
import de.umass.lastfm.Caller;
import de.umass.lastfm.Track;
import de.umass.lastfm.User;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.http.HttpStatus;
import se.michaelthelin.spotify.SpotifyApi;
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
    public static final String USER_HOME = System.getProperty("user.home");
    public static HashMap<String, String> configuration;

    public static void main(String[] args) {
        // create hash map with user agent and default playlist name
        configuration = new HashMap<>();
        configuration.put("requests.useragent", "LastFMToSpotify/1.0-Snapshot (" + System.getProperty("os.name") + "; " + System.getProperty("os.arch") + ") Java/" + System.getProperty("java.version"));
        configuration.put("playlist.name", "LastFMToSpotify@" + LocalDateTime.now(Clock.systemDefaultZone()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        configuration.put("logging.level", "1");

        //check whether all required arguments are set
        if (!ArgumentHandler.checkArguments(args)) {
            return;
        }

        //check whether multiple arguments from an exclusive set are used
        if(!ArgumentHandler.checkExclusivity(args)){
            return;
        }
        
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
            if (configuration.containsKey("cache.crypto") && TokenHelper.existsTokens()) {
                logLn("Cached credentials have been found.", 2);
                logLn("Fetching credentials from cache.", 2);
                SpotifyCredentials cred = TokenHelper.fetchTokens();
                api.setRefreshToken(cred.getRefreshToken());

                if(!cred.isValid()){
                    logLn("Cached credentials are invalid due to age. Refreshing and saving to cache", 2);
                    cred.refreshCredentials(api.authorizationCodeRefresh().build().execute());
                    TokenHelper.saveTokens(cred);
                }
                configuration.put("spotify.access", cred.getAccessToken());
            } else {
                try (Javalin webserver = Javalin.create().start(9876)) {
                    if (configuration.containsKey("cache.crypto")) logLn("No cached credentials have been found.", 2);
                    logLn("Starting webserver to initiate web based authentication.", 2);
                    Runtime.getRuntime().addShutdownHook(new Thread(webserver::stop));
//                    webserver.exception(Exception.class, (exception, ctx) -> {
//                        ctx.result(exception.getMessage());
//                    });
                    webserver.get("/callback/spotify", ctx -> {
                        if(ctx.queryParamMap().containsKey("code")) {
                            logLn("Received spotify authentication code. Requesting credentials.", 2);
                            SpotifyCredentials cred = new SpotifyCredentials(api.authorizationCode(ctx.queryParam("code")).setHeader("User-Agent", configuration.get("requests.useragent")).build().execute());
                            configuration.put("spotify.access", cred.getAccessToken());
                            if(configuration.containsKey("cache.crypto")) {
                                logLn("Saving credentials to cache.", 2);
                                TokenHelper.saveTokens(cred);
                            }
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
            }
            logLn("Authenticating with LastFM...", 1);
            Caller.getInstance().setApiRootUrl("https://ws.audioscrobbler.com/2.0/");
            Caller.getInstance().setUserAgent(configuration.get("requests.useragent"));
            logLn(User.getInfo(configuration.get("lastfm.user"), configuration.get("lastfm.apikey")).getName(), 1);
            logLn("Reading from LastFM...", 1);
            Collection<Track> tracks = User.getTopTracks(configuration.get("lastfm.user"), PeriodHelper.getPeriodByString(configuration.get("lastfm.period")), configuration.get("lastfm.apikey"));
            logLn("Creating Playlist...", 1);
            logLn("... with custom playlistname (if set) and access modifiers (if set).", 2);
            api.setAccessToken(configuration.get("spotify.access"));
            Playlist list = api.createPlaylist(api.getCurrentUsersProfile().build().execute().getId(), configuration.get("playlist.name")).public_(configuration.containsKey("playlist.public")).collaborative(configuration.containsKey("playlist.collab")).setHeader("User-Agent", configuration.get("requests.useragent")).build().execute();
            List<String> adders = new LinkedList<>();
            String charsToReplace = "[\"']"; //regex removing " and ' because of issues with spotify search
            logLn("Searching tracks from LastFM data on Spotify.", 2);
            for (Track track : tracks) {
                logLn("Searching " + track.getName() + " by " + track.getArtist(), 3);
                StringBuilder searchQuery = new StringBuilder();
                searchQuery.append("track:").append(track.getName().replaceAll(charsToReplace, ""));
                searchQuery.append(" artist:").append(track.getArtist());
                if(track.getAlbum()!=null&&!track.getAlbum().equalsIgnoreCase("null")&&!track.getAlbum().isEmpty())
                    searchQuery.append(" album:").append(track.getAlbum());
                logLn("Search query: " + searchQuery, 3);
                se.michaelthelin.spotify.model_objects.specification.Track[] add = api.searchTracks(searchQuery.toString()).market(CountryCode.DE).setHeader("User-Agent", configuration.get("requests.useragent")).build().execute().getItems();
                for(se.michaelthelin.spotify.model_objects.specification.Track t : add){
                    if(t.getName().equalsIgnoreCase(track.getName())){
                        adders.add(t.getUri());
                        logLn("Added " + add[0].getName() + " to " + configuration.get("playlist.name"), 3);
                        break;
                    }
                }
            }
            logLn("Adding tracks to playlist.", 2);
            api.addItemsToPlaylist(list.getId(), adders.toArray(String[]::new)).setHeader("User-Agent", configuration.get("requests.useragent")).build().execute();
            if(configuration.containsKey("playlist.cover")){
                logLn("Setting playlist cover", 2);
                logLn("Check for \"null\" if setting cover was successful: " + api.uploadCustomPlaylistCoverImage(list.getId()).image_data(configuration.get("playlist.cover")).setHeader("User-Agent", configuration.get("requests.useragent")).build().execute(),3);
            }
            logLn("Done.", 1);
//        } catch (IOException | ParseException | SpotifyWebApiException e) {
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
