package de.b00tload.tools.lastfmtospotifyplaylist.util;

import java.nio.file.Path;

import static de.b00tload.tools.lastfmtospotifyplaylist.LastFMToSpotify.USER_HOME;
import static de.b00tload.tools.lastfmtospotifyplaylist.LastFMToSpotify.configuration;

public class TokenHelper {

    /**
     * Manages saving a <code>de.b00tload.tools.lastfmtospotifyplaylist.util.SpotifyCredentials</code> into "~/.lfm2s/spotify.lfm2scred" using <code>de.b00tload.tools.lastfmtospotifyplaylist.util.CryptoHelper.serializeEncrypted(...)</code>
     * @param cred The <code>de.b00tload.tools.lastfmtospotifyplaylist.util.SpotifyCredentials</code> to be saved
     */
    public static void saveTokens(SpotifyCredentials cred) {
        CryptoHelper.serializeEncrypted(cred, Path.of(USER_HOME, "/.lfm2s/spotify.lfm2scred"), CryptoHelper.createKeyFromPassword(configuration.get("cache.crypto")));
    }

    /**
     * Manages retrieving a <code>de.b00tload.tools.lastfmtospotifyplaylist.util.SpotifyCredentials</code> from "~/.lfm2s/spotify.lfm2scred" using <code>de.b00tload.tools.lastfmtospotifyplaylist.util.CryptoHelper.deserializeEncrypted(...)</code>
     * @return The retrieved <code>de.b00tload.tools.lastfmtospotifyplaylist.util.SpotifyCredentials</code>
     */
    public static SpotifyCredentials fetchTokens() {
        return (SpotifyCredentials) CryptoHelper.deserializeEncrypted(Path.of(USER_HOME, "/.lfm2s/spotify.lfm2scred"), CryptoHelper.createKeyFromPassword(configuration.get("cache.crypto")));
    }

    /**
     * Checks whether the saved SpotifyCredentials at "~/.lfm2s/spotify.lfm2scred" exist
     * @return true if file exists, false if not
     */
    public static boolean existsTokens(){
        return Path.of(USER_HOME, "/.lfm2s/spotify.lfm2scred").toFile().exists();
    }
}
