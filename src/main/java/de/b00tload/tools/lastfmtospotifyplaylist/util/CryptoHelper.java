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

    /**
     * Creates a <code>javax.crypto.SecretKey</code> from a provided password
     * @param pass The password
     * @return the generated secret key
     */
    public static SecretKey createKeyFromPassword(String pass){
        try {
            KeySpec spec = new PBEKeySpec(pass.toCharArray(), "abcdefghijklmnop".getBytes(), 65536, 256); // AES-256
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] key = f.generateSecret(spec).getEncoded();
            return new SecretKeySpec(key, "AES");
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves a <code>java.io.Serializable</code> Object into an encrypted file
     * @param obj The object to save
     * @param file The Path where to save the file
     * @param key The SecretKey (AES) to encrypt the file with
     */
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

    /**
     * Reads an encrypted file into a <code>java.io.Serializable</code> object.
     * @param file The <code>java.nio.Path</code> where the encrypted file is stored.
     * @param key The SecretKey (AES) to decrypt the file with
     * @return The <code>java.io.Serializable</code> object read from the file.
     */
    public static Serializable deserializeEncrypted(Path file, SecretKey key) {
        Serializable ret;
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
