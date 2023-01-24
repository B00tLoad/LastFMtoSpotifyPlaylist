package de.b00tload.tools.lastfmtospotifyplaylist.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class TimeHelper {

    public static int getUTCOffsetHours(LocalDateTime now){

        return (int) Math.floor((double) now.atZone(ZoneId.systemDefault()).getOffset().getTotalSeconds()/3600);
    }

    public static int getUTCOffsetMinutes(LocalDateTime now){
        return (now.atZone(ZoneId.systemDefault()).getOffset().getTotalSeconds()/60);
    }

    public static String getUTCOffset(LocalDateTime now){
        int hour = getUTCOffsetHours(now);
        String h = (hour == Math.abs(hour) ? "+" : "-") + (String.valueOf(Math.abs(hour)).length() == 1 ? "0" + Math.abs(hour) : String.valueOf(Math.abs(hour)));
        int min = Math.abs(getUTCOffsetMinutes(now))-(Math.abs(hour)*60);
        String m = (String.valueOf(Math.abs(min)).length() == 1 ? "0" + Math.abs(min) : String.valueOf(Math.abs(min)));
        return h + ":" + m;
    }

}
