package de.b00tload.tools.lastfmtospotifyplaylist.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

public class FileHelper {

    /**
     * Reads a file and encodes it into Base64
     * @param file The file to read
     * @return A Base64 encoded String containing the data of the file
     */
    public static String encodeFileToBase64(File file){
        String encodedfile = null;
        try (FileInputStream fileInputStreamReader = new FileInputStream(file)){
            byte[] bytes = new byte[(int)file.length()];
            fileInputStreamReader.read(bytes);
            encodedfile = Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return encodedfile;
    }

}
