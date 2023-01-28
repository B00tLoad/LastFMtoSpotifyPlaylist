package de.b00tload.tools.lastfmtospotifyplaylist.util;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;

import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDateTime;

public class SpotifyCredentials implements Serializable {

    private final AuthorizationCodeCredentials cred;
    private final LocalDateTime validUntil;

    public SpotifyCredentials(AuthorizationCodeCredentials cred){
        this.cred = cred;
        this.validUntil = LocalDateTime.now(Clock.systemDefaultZone()).plusSeconds(cred.getExpiresIn());
    }

    public String getAccessToken(){
        return cred.getAccessToken();
    }

    public String getRefreshToken(){
        return cred.getRefreshToken();
    }

    public LocalDateTime getValidUntil(){
        return validUntil;
    }

    public boolean isValid(){
        return LocalDateTime.now(Clock.systemDefaultZone()).isBefore(validUntil);
    }

}