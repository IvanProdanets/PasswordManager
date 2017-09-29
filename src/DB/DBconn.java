package DB;

import DES.CryptoException;
import DES.CryptoUtils;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * <p>Виконує підключення до БД. Singleton клас.</p>
 * <p>Алгоритм:</p>
 * <ol>
 *  <li>Відкриває зашифрований файл БД</li>
 *  <li>Розшифровує його у тимчасовий файл</li>
 *  <li>Переписує тимчасовий файл у зашифрований</li>
 *  <li>Видаляє тимчасовий файл</li>
 * </ol>
 * 
 * @author Admin
 */
public class DBconn {

    
    Connection conn = null;
    public static File encryptedFile;
    public static File decryptedTempFile;
    

    String URL = null;
    private static DBconn instance = null;

    /**
     * <p>Відкриває зашифрований файл PassManager.db</p>
     * <p>Створює тимчасовий temp-file-name.db</p>
     * <p>Встановлює URL для выдкриття пыдключення до БД</p>
     */
    public DBconn() {
        try {
            //
            encryptedFile = new File("PassManager.db");
            encryptedFile.createNewFile();
            Runtime.getRuntime().exec("attrib -H "+encryptedFile.getPath());
            
            //створюю тимчасовий файл з бд
            decryptedTempFile = File.createTempFile("temp-file-name", ".db");
            decryptedTempFile.createNewFile();
            //розшифровую зашифрований PassManager.db в тимчасовий файл
            CryptoUtils.decrypt(encryptedFile, decryptedTempFile);
            
            //встановлюю URL для підключення до тимчасового файлу
            URL =  "jdbc:sqlite:" + decryptedTempFile.getPath();
        } catch (IOException | CryptoException ex) {
            Logger.getLogger(DBconn.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    /**
     * Повератє новий екземпляр об'єкту DBConn, або існуючий
     * @return  екземпляр DBConn
     */
    public static DBconn getInstance() {
        if (instance == null) {
            synchronized (DBconn.class) {
                if (instance == null) {
                    instance = new DBconn();
                }
            }
        }
        return instance;
    }

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, e, "Error!", JOptionPane.ERROR_MESSAGE);
            throw new ExceptionInInitializerError(e);
        }
    }
    /**
     * Повертає підключення до БД
     * @return  Connection підключення до БД  
     * @throws SQLException 
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /**
     * Метод закриває підключення до БД
     * @param rs відкритий ResultSet
     * @param pst відкритий PreparedStatement
     * @param con відкритий Connection
     * @throws IOException може виникнути при видаленні тимчасового файлу 
     */
    public void free(ResultSet rs, PreparedStatement pst, Connection con) throws IOException {
        try {

            if (rs != null) {
                rs.close();
            }
        } catch (SQLException ex) {
        } finally {
            
            try {
                CryptoUtils.encrypt(decryptedTempFile, encryptedFile);
            } catch (CryptoException ex) {
                Logger.getLogger(DBconn.class.getName()).log(Level.SEVERE, null, ex);
            }
            decryptedTempFile.delete();
            Runtime.getRuntime().exec("attrib +H "+encryptedFile.getPath());
            try {
                if (pst != null) {
                    pst.close();
                }
            } catch (SQLException e) {
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                    }
                }
            }
        }
    }
}
