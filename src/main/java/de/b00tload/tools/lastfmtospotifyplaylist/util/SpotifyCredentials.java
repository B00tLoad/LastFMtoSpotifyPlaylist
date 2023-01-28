package de.b00tload.tools.lastfmtospotifyplaylist.util;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;

import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * A wrapper class for <code>se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials</code>. Implements checking validity of access token.
 */
public class SpotifyCredentials implements Serializable {

    private final AuthorizationCodeCredentials cred;
    private final LocalDateTime validUntil;

    /**
     * Initializes the class
     * @param cred The <code>AuthorizationCodeCredentials</code> to be saved. Recommended for use with recently (last few seconds) generated Credentials
     */
    public SpotifyCredentials(AuthorizationCodeCredentials cred){
        this.cred = cred;
        this.validUntil = LocalDateTime.now(Clock.systemDefaultZone()).plusSeconds(cred.getExpiresIn());
    }

    /**
     * Get the access token. It becomes invalid after a certain period of time. Check validity with <code>isValid()</code>.
     * @return An access token that can be provided in subsequent calls, for example to Spotify Web API services.
     */
    public String getAccessToken(){
        return cred.getAccessToken();
    }

    /**
     * Get the refresh token. This token can be sent to the Spotify Accounts service in place of an authorization code to retrieve a new access token.
     * @return A token that can be sent to the Spotify Accounts service in place of an access token.
     */
    public String getRefreshToken(){
        return cred.getRefreshToken();
    }

    /**
     * Returns a <code>LocalDateTime</code> which represents the latest point in time when the access token is still valid.
     * @return A <code>LocalDateTime</code> representing the latest point in time of access token validity
     */
    public LocalDateTime getValidUntil(){
        return validUntil;
    }

    /**
     * Checks whether the saved access token is still valid for use in calls, for example to the Spotify Web API services.
     * @return true if the access token is still valid for use, false if not.
     */
    public boolean isValid(){
        return LocalDateTime.now(Clock.systemDefaultZone()).isBefore(getValidUntil());
    }

}