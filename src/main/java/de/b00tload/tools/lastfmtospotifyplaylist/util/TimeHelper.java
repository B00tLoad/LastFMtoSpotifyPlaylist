package de.b00tload.tools.lastfmtospotifyplaylist.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeHelper {

    /**
     * Gets the hours of the UTC offset from a LocalDateTime, which is asserted to be at the system default Timezone (<code>ZoneId.systemDefault()</code>)
     * @param now The <code>java.time.LocalDateTime</code> for which the offset should be calculated
     * @return The hours of offset from UTC
     */
    public static int getUTCOffsetHours(LocalDateTime now){
        return (int) Math.floor((double) now.atZone(ZoneId.systemDefault()).getOffset().getTotalSeconds()/3600);
    }

    /**
     * Gets the minutes of the UTC offset from a LocalDateTime, which is asserted to be at the system default Timezone (<code>ZoneId.systemDefault()</code>)
     * @param now The <code>java.time.LocalDateTime</code> for which the offset should be calculated
     * @return The minutes of offset from UTC
     */
    public static int getUTCOffsetMinutes(LocalDateTime now){
        return (now.atZone(ZoneId.systemDefault()).getOffset().getTotalSeconds()/60);
    }

    /**
     * Generates an ISO 8601 complying String containing the UTC offset from a LocalDateTime, which is asserted to be at the system default Timezone (<code>ZoneId.systemDefault()</code>)
     * @param now The <code>java.time.LocalDateTime</code> for which the offset should be calculated
     * @return an ISO 8601 complying String containing the UTC offset (e.g. "+01:00")
     */
    public static String getUTCOffset(LocalDateTime now){
        int hour = getUTCOffsetHours(now);
        String h = (hour == Math.abs(hour) ? "+" : "-") + (String.valueOf(Math.abs(hour)).length() == 1 ? "0" + Math.abs(hour) : String.valueOf(Math.abs(hour)));
        int min = Math.abs(getUTCOffsetMinutes(now))%(Math.abs(hour));
        String m = (String.valueOf(Math.abs(min)).length() == 1 ? "0" + Math.abs(min) : String.valueOf(Math.abs(min)));
        return h + ":" + m;
    }

}
