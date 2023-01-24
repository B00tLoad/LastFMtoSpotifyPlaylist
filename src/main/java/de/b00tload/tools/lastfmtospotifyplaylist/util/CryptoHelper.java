package de.b00tload.tools.lastfmtospotifyplaylist.util;


import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class CryptoHelper {

    public static SecretKey createKeyFromPassword(String pass){
        try {
            KeySpec spec = new PBEKeySpec(pass.toCharArray(), "abcdefghijklmnop".getBytes(), 65536, 256); // AES-256
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] key = new byte[0];
            key = f.generateSecret(spec).getEncoded();
            return new SecretKeySpec(key, "AES");
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void serializeEncrypted(Serializable obj, Path file, SecretKey key){
        try {
            if(file.toFile().exists()) file.toFile().delete();
            if(!file.getParent().toFile().exists()) file.getParent().toFile().mkdirs();
            file.toFile().createNewFile();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec("abcdefghijklmnop".getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            SealedObject sealedObject = new SealedObject( obj, cipher);
            CipherOutputStream cipherOutputStream = new CipherOutputStream( new BufferedOutputStream( new FileOutputStream( file.toFile() ) ), cipher );
            ObjectOutputStream outputStream = new ObjectOutputStream( cipherOutputStream );
            outputStream.writeObject( sealedObject );
            outputStream.close();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    public static Serializable deserializeEncrypted(Path file, SecretKey key) {
        Serializable ret = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec("abcdefghijklmnop".getBytes());
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            CipherInputStream cipherInputStream = new CipherInputStream( new BufferedInputStream( new FileInputStream( file.toFile() ) ), cipher );
            ObjectInputStream inputStream = new ObjectInputStream( cipherInputStream );
            SealedObject sealedObject = (SealedObject) inputStream.readObject();
            ret = (Serializable) sealedObject.getObject(cipher);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException | IllegalBlockSizeException | ClassNotFoundException | BadPaddingException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

}
