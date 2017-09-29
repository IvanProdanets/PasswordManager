
package DES;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
 
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
 
/**
 * Клас шифрує та розшифровує файли
 * @author Admin
 *
 */
public class CryptoUtils {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    private static final String KEY = "Q;D156sddLKs dee";
 
    /**
     * Виконує шифрвування файла
     * @param inputFile вхідний незашифрований файл
     * @param outputFile вихідний зашифрований файл
     * @throws CryptoException 
     */
    public static void encrypt(File inputFile, File outputFile)
            throws CryptoException {
        doCrypto(Cipher.ENCRYPT_MODE, KEY, inputFile, outputFile);
    }
 
    /**
     * Виконує розшифрування файла
     * @param inputFile вхідний зашифрований файл
     * @param outputFile вихідний розшифрований файл
     * @throws CryptoException 
     */
    public static void decrypt(File inputFile, File outputFile)
            throws CryptoException {
        doCrypto(Cipher.DECRYPT_MODE, KEY, inputFile, outputFile);
    }
 
    /**
     * Загальний метод для шифрування/розшифрування
     * @param cipherMode шифрування/розшифрування
     * @param key ключ шифру
     * @param inputFile вхідний файл
     * @param outputFile вихідний файл
     * @throws CryptoException 
     */
    private static void doCrypto(int cipherMode, String key, File inputFile,
            File outputFile) throws CryptoException {
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);
             
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
             
            byte[] outputBytes = cipher.doFinal(inputBytes);
             
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);
             
            inputStream.close();
            outputStream.close();
             
        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
    }
}