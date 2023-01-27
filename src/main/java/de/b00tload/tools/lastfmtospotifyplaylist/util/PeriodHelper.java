package de.b00tload.tools.lastfmtospotifyplaylist.util;

import de.umass.lastfm.Period;

public class PeriodHelper {

    /**
     * Converts a provided String (<code>de.umass.lastfm.Period.getString()</code>) into the corresponding <code>de.umass.lastfm.Period</code>
     * @param string The value to be converted into a <code>de.umass.lastfm.Period</code>
     * @return The converted <code>de.umass.lastfm.Period</code>, defaults to <code>de.umass.lastfm.Period.ONE_MONTH</code>
     */
    public static Period getPeriodByString(String string){
        for(Period p : Period.values()){
            if(p.getString().equalsIgnoreCase(string)) return p;
        }
        return Period.ONE_MONTH;
    }

}
