package de.b00tload.tools.lastfmtospotifyplaylist.util;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;

import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A wrapper class for <code>se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials</code>. Implements checking validity of access token.
 */
public class SpotifyCredentials implements Serializable {

    private String accessToken;
    private String refreshToken;
    private LocalDateTime validUntil;

    /**
     * Initializes the class
     * @param cred The <code>AuthorizationCodeCredentials</code> to be saved. Recommended for use with recently (last few seconds) generated Credentials
     */
    public SpotifyCredentials(AuthorizationCodeCredentials cred){
        this.accessToken = cred.getAccessToken();
        this.refreshToken = cred.getRefreshToken();
        this.validUntil = LocalDateTime.now(Clock.systemDefaultZone()).plusSeconds(cred.getExpiresIn());
    }

    /**
     * Get the access token. It becomes invalid after a certain period of time. Check validity with <code>isValid()</code>.
     * @return An access token that can be provided in subsequent calls, for example to Spotify Web API services.
     */
    public String getAccessToken(){
        return accessToken;
    }

    /**
     * Get the refresh token. This token can be sent to the Spotify Accounts service in place of an authorization code to retrieve a new access token.
     * @return A token that can be sent to the Spotify Accounts service in place of an access token.
     */
    public String getRefreshToken(){
        return refreshToken;
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

    /**
     * Refreshes the access token. If a new refresh token is provided it will be saved as well.
     * @param cred The <code>AuthorizationCodeCredentials</code> to be saved. Recommended for use with recently (last few seconds) generated Credentials
     */
    public void refreshCredentials(AuthorizationCodeCredentials cred){
        this.accessToken = cred.getAccessToken();
        if(Objects.nonNull(cred.getRefreshToken())) this.refreshToken = cred.getRefreshToken();
        this.validUntil = LocalDateTime.now(Clock.systemDefaultZone()).plusSeconds(cred.getExpiresIn());
    }

}