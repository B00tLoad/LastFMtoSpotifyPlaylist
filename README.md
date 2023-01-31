# LastFM To Spotify-Playlist
This command-line-tool creates a playlist on Spotify which contains the 50 songs you listened to the most. This data is pulled from your LastFM profile.

The period for which this data is pulled is limited to:
 - One week
 - One month
 - Three months 
 - Six months
 - One year
 - All time

This limitation is set by LastFM, as those are the period selectors in their API.

The tool supports setting a custom name for the playlist, setting a cover art as well as making the created playlist public (Added to profile) or collaborative. public and collaborative are mutually exclusive.

---

## Prerequisites
 - A Spotify application
 - A LastFM api token
 - Java 18 (as this tool is not shipped with a JRE you'll need to install the JDK)
 - Usage on Linux requires having xdg-open installed
 - Active Last.fm scrobbling

## Setup
### Spotify
1. Visit [the Spotify Developer Dashboard](https://developer.spotify.com/dashboard/)
2. Create an app
3. In the "Users and Access" menu, add your Email-Address and Name
4. In the "Edit Settings" add "http://localhost:9876/callback/spotify/" as a Redirect URI
5. Note your Client ID and Client Secret

### LastFM
1. [Create a LastFM API account](https://www.last.fm/api/account/create)
2. Note the API key

## Usage
In your command line run ```java -jar lfm2s-%version%.jar [arguments]```.

For argument usages check [the wiki](https://github.com/B00tLoad/LastFMtoSpotifyPlaylist/wiki/Arguments)
