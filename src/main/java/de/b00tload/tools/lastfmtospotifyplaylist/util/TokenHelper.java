package de.b00tload.tools.lastfmtospotifyplaylist.util;

import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;

import java.nio.file.Path;

import static de.b00tload.tools.lastfmtospotifyplaylist.LastFMToSpotify.USER_HOME;
import static de.b00tload.tools.lastfmtospotifyplaylist.LastFMToSpotify.configuration;

public class TokenHelper {

    /**
     * Manages saving a <code>se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials</code> into "~/.lfm2s/spotify.lfm2scred" using <code>de.b00tload.tools.lastfmtospotifyplaylist.util.CryptoHelper.serializeEncrypted(...)</code>
     * @param cred The <code>se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials</code> to be saved
     */
    public static void saveTokens(AuthorizationCodeCredentials cred) {
        CryptoHelper.serializeEncrypted(cred, Path.of(USER_HOME, "/.lfm2s/spotify.lfm2scred"), CryptoHelper.createKeyFromPassword(configuration.get("cache.crypto")));
    }

    /**
     * Manages retrieving a <code>se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials</code> from "~/.lfm2s/spotify.lfm2scred" using <code>de.b00tload.tools.lastfmtospotifyplaylist.util.CryptoHelper.deserializeEncrypted(...)</code>
     * @return The retrieved <code>se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials</code>
     */
    public static AuthorizationCodeCredentials fetchTokens() {
        return (AuthorizationCodeCredentials) CryptoHelper.deserializeEncrypted(Path.of(USER_HOME, "/.lfm2s/spotify.lfm2scred"), CryptoHelper.createKeyFromPassword(configuration.get("cache.crypto")));
    }

    /**
     * Checks whether the saved spotify AuthorizationCodeCredentials at "~/.lfm2s/spotify.lfm2scred" exist
     * @return true if file exists, false if not
     */
    public static boolean existsTokens(){
        return Path.of(USER_HOME, "/.lfm2s/spotify.lfm2scred").toFile().exists();
    }
}
