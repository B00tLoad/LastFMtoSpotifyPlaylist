package de.b00tload.tools.lastfmtospotifyplaylist.util;

public class BrowserHelper {
    
    /**
     * Opens a <code>url</code> in the systems default browser
     * @param url
     */
    public static void openInBrowser(String url) {

        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder builder;

        if (os.indexOf("win") >= 0) {
            // Windows
            builder = new ProcessBuilder("rundll32.exe","url.dll,FileProtocolHandler", url);
        } else if (os.indexOf("mac") >= 0) {
            // Mac
            builder = new ProcessBuilder("open", url);
        } else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
            // Linux
            os = "linux";
            builder = new ProcessBuilder("xdg-open", url);
        } else {
            Logger.logLn("Please open the following link:\n"+url, 1);
            builder = null;
        }
        
        try {
            if (builder != null) {
                Process exec = builder.start();
                if (os.equals("linux") && exec.exitValue() == 3) {
                    // on Linux in case of missing browser
                    Logger.logLn("Please open the following link:\n"+url, 1);
                }
            }
        } catch (Exception e) {
            Logger.logLn(e.getMessage(), 3);
        }

    }
}
