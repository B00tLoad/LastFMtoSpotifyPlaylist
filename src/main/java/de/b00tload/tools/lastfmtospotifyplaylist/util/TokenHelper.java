package de.b00tload.tools.lastfmtospotifyplaylist.util;

import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;

import java.nio.file.Path;

import static de.b00tload.tools.lastfmtospotifyplaylist.LastFMToSpotify.USER_HOME;
import static de.b00tload.tools.lastfmtospotifyplaylist.LastFMToSpotify.configuration;

public class TokenHelper {
    public static void saveTokens(AuthorizationCodeCredentials cred) {
        CryptoHelper.serializeEncrypted(cred, Path.of(USER_HOME, "/.lfm2s/spotify.lfm2scred"), CryptoHelper.createKeyFromPassword(configuration.get("cache.crypto")));
    }

    public static AuthorizationCodeCredentials fetchTokens() {
        return (AuthorizationCodeCredentials) CryptoHelper.deserializeEncrypted(Path.of(USER_HOME, "/.lfm2s/spotify.lfm2scred"), CryptoHelper.createKeyFromPassword(configuration.get("cache.crypto")));
    }

    public static boolean existsTokens(){
        return Path.of(USER_HOME, "/.lfm2s/spotify.lfm2scred").toFile().exists();
    }
}
