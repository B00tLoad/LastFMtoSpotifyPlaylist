package de.b00tload.tools.lastfmtospotifyplaylist.util;

import de.umass.lastfm.Period;

public class PeriodHelper {

    public static Period getPeriodByString(String string){
        for(Period p : Period.values()){
            if(p.getString().equalsIgnoreCase(string)) return p;
        }
        return Period.ONE_MONTH;
    }

}
