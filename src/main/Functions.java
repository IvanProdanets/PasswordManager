
package main;

import DB.DBconn;
import DES.CryptoException;
import DES.CryptoUtils;
import DES.Encryptor;
import Models.Password;
import Views.EditMasterView;
import Views.LoginView;
import Views.MainView;
import Views.PasswordInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.Timer;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Клас описує основні функції для фреймів
 * @author Admin
 */
public class Functions {

    SQLQuery query;

    public Functions() {
        query = new SQLQuery();
    }

    private String sql = null;

    /**
     * Викликає вікно вибору файла чи папки
     * @param frame Дочірне вікно
     * @param title Назва вікна вибору фала чи папки
     * @param ButtonText назва кнопки 
     * @param withFilter булеве значення, якщо true, то будуть видимі тільки файли з розширенням *.pass 
     * @param selectionMode  DIRECTORIES_ONLY = 1 або FILES_AND_DIRECTORIES = 2
     * @return 
     */
    public File ChoseDBFile(JFrame frame, String title, String ButtonText, boolean withFilter, int selectionMode) {
        File filePath;
        JFileChooser chooseFile = new JFileChooser();
        if (withFilter) {
            FileFilter filter = new FileNameExtensionFilter("Import file like *.pass", "pass");
            chooseFile.setFileFilter(filter);
            chooseFile.addChoosableFileFilter(filter);
        }
        //Selection mode DIRECTORIES_ONLY = 1;
        //Selection mode FILES_AND_DIRECTORIES = 2;
        chooseFile.setFileSelectionMode(selectionMode);
        chooseFile.setApproveButtonText(ButtonText);
        chooseFile.setDialogTitle(title);// выбрать название
        chooseFile.setDialogType(JFileChooser.OPEN_DIALOG);// выбрать тип диалога Open или Save
        chooseFile.setMultiSelectionEnabled(false); // Разрегить выбор нескольки файлов
        chooseFile.showOpenDialog(frame);
        File file = chooseFile.getSelectedFile();
        return file;
    }

    /**
     * Відкриває зашифрований фалй та вставляє дані з файлу в БД
     * @param file файл з даними
     * @throws IOException 
     * @throws CryptoException 
     */
    public void importDataFromFile(File file) throws IOException, CryptoException {
        File decryptedTempFile = File.createTempFile("temp-file-name", ".pass");
        decryptedTempFile.createNewFile();
        file.createNewFile();
        CryptoUtils.decrypt(file, decryptedTempFile);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(decryptedTempFile)));
            String line = null;

            while ((line = br.readLine()) != null) {
                String str[] = line.split(",");
                if (str[4].equals("____")) {
                    str[4] = "";
                }
                if (str[5].equals("____")) {
                    str[5] = "";
                }
                sql = "INSERT INTO [password] ([id], [title], [type_id], [password], [userName], [webSite], [notes]) VALUES( "
                        + "null, '" + str[0] + "',  '" + str[1] + "', '" + str[3] + "', '" + str[2] + "', '" + str[4] + "', '" + str[5] + "')";
                query.execute(sql);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) MainView.getInstanse().getjTree1().getLastSelectedPathComponent();
                String type;
                if (node == null || node.isRoot()) {
                    type = "Загальні";
                }
                type = node.toString();
                updateTablePassword(MainView.getInstanse().getjTable1(), type);
            }
            JOptionPane.showMessageDialog(MainView.getInstanse(), "Дані імпортовані!");
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(MainView.getInstanse(), "Файл не знайдений!", "Error:", 0);
        } catch (IOException ex) {
        } finally {
            try {
                decryptedTempFile.delete();
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Витягує дані з БД та записує у зашифрований файл
     * @throws FileNotFoundException 
     * @throws SQLException
     * @throws IOException
     * @throws CryptoException 
     */
    public void exportDataToFile() throws FileNotFoundException, SQLException, IOException, CryptoException {
        File file = new File(
                ChoseDBFile(MainView.getInstanse(),
                        "Виберіть місце для збереження",
                        "Зберегти",
                        false,
                        1)
                .getPath() + ".pass");
        FileWriter fw = new FileWriter(file);
        sql = "SELECT "
                + "       p.title AS 'Назва', "
                + "      p.type_id AS 'type_id', "
                + "       p.userName AS 'Користувач', "
                + "       p.password AS 'Пароль', "
                + "       p.webSite AS 'Web-сайт', "
                + "       p.notes AS 'Нотатки' "
                + "FROM password AS p ";
        Statement stmt = DBconn.getInstance().getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            fw.append(rs.getString(1));
            fw.append(',');
            fw.append(rs.getString(2));
            fw.append(',');
            fw.append(rs.getString(3));
            fw.append(',');
            fw.append(rs.getString(4));
            fw.append(',');
            if (rs.getString(5).equals("") || rs.getString(5) == null) {
                fw.append("____");
            } else {
                fw.append(rs.getString(5));
            }
            fw.append(',');
            if (rs.getString(6).equals("") || rs.getString(6) == null) {
                fw.append("____");
            } else {
                fw.append(rs.getString(6));
            }
            fw.append('\n');
        }
        fw.flush();
        fw.close();
        CryptoUtils.encrypt(file, file);
    }

    /**
     * Екранує символи ' та " переданого значення
     * @param value
     * @return  екрановане знаяення
     */
    public String escaping(String value) {
        return value.replaceAll("\'", "\\\\\'").replaceAll("\"", "\\\\\"");
    }

    /**
     * Створує схему БД
     */
    public void createDBSchema() {
        sql = "CREATE TABLE if not exists [master] ("
                + "    [id] INTEGER NOT NULL UNIQUE, "
                + "    [password] VARCHAR2(100) NOT NULL)";
        query.execute(sql);
        sql = "CREATE TABLE if not exists [password]("
                + "    [id] INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, "
                + "    [title] VARCHAR2(255) NOT NULL, "
                + "    [type_id] INTEGER NOT NULL CONSTRAINT [FK_TYPE] REFERENCES type([id]) ON DELETE CASCADE ON UPDATE CASCADE, "
                + "    [password] VARCHAR2(255) NOT NULL, "
                + "    [userName] VARCHAR2(100), "
                + "    [webSite] VARCHAR2(200), "
                + "    [notes] VARCHAR2(200))";
        query.execute(sql);

        sql = "CREATE TABLE if not exists [type]("
                + "    [id] INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, "
                + "    [name] VARCHAR2(100) NOT NULL, "
                + "    [notes] VARCHAR2(200))";
        query.execute(sql);
    }

    public void insertPassword(Password pass) {
        sql = "SELECT [id] FROM [type] WHERE name = '" + pass.getType() + "'";
        int type_id = query.getIntValue(sql, "id");
        sql = "INSERT INTO [password] ([id], [title], [type_id], [password], [userName], [webSite], [notes]) VALUES( "
                + "null, '" + pass.getTitle() + "',  '" + type_id + "', '" + pass.getPassword() + "', '" + pass.getUserName() + "', '" + pass.getURL() + "', '" + pass.getNotes() + "')";
        query.execute(sql);
    }

    /**
     * Заповнює БД стандартними значеннями, що необхідні для коректної роботи програми
     */
    public void insertDefaultValue() {
        sql = "INSERT INTO 'type' ('id', 'name', 'notes') VALUES ("
                + "null, 'Загальні', 'Тип \"Загальні\" призначений для "
                + "зберігання паролів, які не відповідають існуючим в БД типам.')";
        query.updateValue(sql);

        sql = "INSERT INTO 'type' ('id', 'name', 'notes') VALUES ("
                + "null, 'Операційні системи', 'Цей тип використовується для зберігання паролів користувачів операційних систем "
                + "(Windows, Linux, ...).')";
        query.updateValue(sql);

        sql = "INSERT INTO 'type' ('id', 'name', 'notes') VALUES ("
                + "null, 'Соціальні мережі', 'VK, Facebook, LinkedIn, ...')";
        query.updateValue(sql);

        sql = "INSERT INTO 'type' ('id', 'name', 'notes') VALUES ("
                + "null, 'Мережі', '')";
        query.updateValue(sql);

        sql = "INSERT INTO 'type' ('id', 'name', 'notes') VALUES ("
                + "null, 'Електронна пошта', '')";
        query.updateValue(sql);

        sql = "INSERT INTO 'type' ('id', 'name', 'notes') VALUES ("
                + "null, 'Web-сайти', '')";
        query.updateValue(sql);

        sql = "INSERT INTO 'type' ('id', 'name', 'notes') VALUES ("
                + "null, 'Pin-коди', '')";
        query.updateValue(sql);

    }

    /**
     * Перевіряє, чи переданий пароль існує в БД
     * @param password пароль
     * @return 
     */
    public boolean isTrueMasster(String password) {
        sql = "SELECT `id` FROM `master` WHERE `password`='" + password + "'";
        int value = query.getIntValue(sql, "id");
        return value == 1;
    }

    /**
     * Викликає метод класу SessionIdentifierGenerator для генерації паролю
     * @see SessionIdentifierGenerator#nextSessionId()
     * @return String згенерований пароль 
     */
    public String generatePassword() {
        SessionIdentifierGenerator generator = new SessionIdentifierGenerator();
        return generator.nextSessionId();
    }

    /**
     * Перевіряє, чи схема підключеної БД відповідає схемі програмної БД 
     * @return true або false
     */
    public boolean isDbValid() {
        sql = "SELECT count(*) AS count FROM sqlite_master WHERE type = 'table' AND "
                + "(name = 'master' OR name = 'password' OR name = 'type')";
        int result = query.getIntValue(sql, "count");
        return result == 3;
    }

    /**
     * Переавіряє, чи встановлено master-пароль
     * @return true або false
     */
    public boolean isMasterPasswordSet() {
        sql = "SELECT id FROM master";
        int id = query.getIntValue(sql, "id");
        return id == 1;
    }

    /**
     * Створює новий master-пароль
     * @param  password master-пароль, який буде створено 
     */
    public void createMasterPassword(String password) {
        sql = "INSERT INTO master (id, password) VALUES (1, '" + password + "')";
        query.updateValue(sql);
    }

    /**
     * Оновлює елемент JTree з типами паролів
     * @param tree елемент типу JTree
     */
    public void updateTypeOfPassword(JTree tree) {
        sql = "SELECT name FROM type";
        query.updateJTree(tree, sql, "Типи паролів", "name");
    }

    /**
     * Змінює елементи заданої колонки таблиці на JPasswordField
     * @see PasswordFieldTableCellRenderer
     * @param table таблиця
     * @param columnIndex  індекс колнки, що необхідно замінити
     */
    public void replaseColumnInTable(JTable table, int columnIndex) {

        TableColumn column = table.getColumnModel().getColumn(columnIndex);

        column.setCellRenderer(new PasswordFieldTableCellRenderer());
        column.setCellEditor(new DefaultCellEditor(new JPasswordField()));

    }

    /**
     * Оновлює таблицю за вказаним параметром(вибраним типом паролю)
     * @param table таблиця
     * @param selectedNodeName  вибраний тип паролю
     */
    public void updateTablePassword(JTable table, String selectedNodeName) {

        sql = "SELECT id FROM type WHERE name = '" + selectedNodeName + "'";
        int id = query.getIntValue(sql, "id");
        sql = "SELECT "
                + "       p.title AS 'Назва', "
                + "       p.userName AS 'Користувач', "
                + "       p.password AS 'Пароль', "
                + "       p.webSite AS 'Web-сайт', "
                + "       p.notes AS 'Нотатки' "
                + "FROM password AS p "
                + "WHERE type_id =" + id + "";
        query.updateTable(table, sql);

        replaseColumnInTable(table, 2);

    }

    /**
     * Здійснює оновлення таблиці шуканими паролями
     * @param table таблиця
     * @param searchText шукане значення
     */
    public void updateTablePasswordForSearsh(JTable table, String searchText) {

        sql = "SELECT "
                + "       p.title AS 'Назва', "
                + "       p.userName AS 'Користувач', "
                + "       p.password AS 'Пароль', "
                + "       p.webSite AS 'Web-сайт', "
                + "       p.notes AS 'Нотатки' "
                + "FROM password AS p "
                + "WHERE p.title LIKE '%" + searchText + "%' OR "
                + "p.userName LIKE '%" + searchText + "%' OR "
                + "p.webSite LIKE '%" + searchText + "%' OR "
                + "p.notes LIKE '%" + searchText + "%'";
        query.updateTable(table, sql);

        replaseColumnInTable(table, 2);

    }

    /**
     * Встановлює ActionListener для вкнопки відміни
     * @param btn кнопка відміни
     */
    public void setActionListenerForCalcel(JButton btn) {
        for (ActionListener al : btn.getActionListeners()) {
            btn.removeActionListener(al);
        }
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

    }

    /**
     * Видаляє вибраний пароль у таблиці
     * @param tree дерево типів JTree
     * @param table таблиця з паролями
     */
    public void deletePassword(JTree tree, JTable table) {
        int row = table.getSelectedRow();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        if (row == -1 || node == null || node.isRoot()) {
            return;
        } else {

            LoginView login = new LoginView();
            login.setVisible(true);

            JButton btnOk = login.getBtnok();
            JButton btnClose = login.getBtnclose();
            JPasswordField txtPass = login.getTxtpass();

            //видалення обробника подій на кнопку btnOk
            for (ActionListener al : btnOk.getActionListeners()) {
                btnOk.removeActionListener(al);
            }
            //видалення обробника подій на кнопку btnClose
            for (ActionListener al : btnClose.getActionListeners()) {
                btnClose.removeActionListener(al);
            }
            //видалення обробника подій з txtPass
            for (KeyListener kl : txtPass.getKeyListeners()) {
                btnOk.removeKeyListener(kl);
            }

            btnOk.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    if (txtPass.getText().length() == 0) {
                        return;
                    } else {
                        String pass = escaping(txtPass.getText());
                        pass = Encryptor.encrypt(pass);

                        if (isTrueMasster(pass)) {

                            String type = node.toString();
                            sql = "SELECT id FROM type WHERE name= '" + type + "'";
                            int id = query.getIntValue(sql, "id");
                            sql = "SELECT id FROM password WHERE title = '" + table.getValueAt(row, 0).toString() + "' "
                                    + "AND type_id='" + id + "' "
                                    + "AND userName = '" + table.getValueAt(row, 1).toString() + "' "
                                    + "AND password = '" + table.getValueAt(row, 2).toString() + "' "
                                    + "AND webSite = '" + table.getValueAt(row, 3).toString() + "' ";
                            id = query.getIntValue(sql, "id");
                            sql = "DELETE FROM password WHERE id = '" + id + "'";
                            query.updateValue(sql);
                            updateTablePassword(table, type);

                            MainView mainView = MainView.getInstanse();
                            login.setVisible(false);
                            mainView.setVisible(true);
                            login.dispose();
                            //followUp(mainView);
                        } else {
                            JOptionPane.showMessageDialog(LoginView.getInstanse(), "Неправильний master-пароль!");
                            login.setVisible(false);
                        }
                    }

                }
            });
            btnClose.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    login.setVisible(false);
                    login.dispose();
                }

            });

            txtPass.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {

                }

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == com.sun.glass.events.KeyEvent.VK_ENTER) {
                        if (txtPass.getText().length() == 0) {
                            return;
                        } else {
                            String pass = escaping(txtPass.getText());
                            pass = Encryptor.encrypt(pass);

                            if (isTrueMasster(pass)) {

                                String type = node.toString();
                                sql = "SELECT id FROM type WHERE name= '" + type + "'";
                                int id = query.getIntValue(sql, "id");
                                sql = "SELECT id FROM password WHERE title = '" + table.getValueAt(row, 0).toString() + "' "
                                        + "AND type_id='" + id + "' "
                                        + "AND userName = '" + table.getValueAt(row, 1).toString() + "' "
                                        + "AND password = '" + table.getValueAt(row, 2).toString() + "' "
                                        + "AND webSite = '" + table.getValueAt(row, 3).toString() + "' ";
                                id = query.getIntValue(sql, "id");
                                sql = "DELETE FROM password WHERE id = '" + id + "'";
                                query.updateValue(sql);
                                updateTablePassword(table, type);

                                MainView mainView = MainView.getInstanse();
                                login.setVisible(false);
                                mainView.setVisible(true);
                                login.dispose();
                                //followUp(mainView);
                            } else {
                                JOptionPane.showMessageDialog(LoginView.getInstanse(), "Неправильний master-пароль!");
                                login.setVisible(false);
                            }
                        }
                    }

                }

                @Override
                public void keyReleased(KeyEvent e) {

                }

            });
        }
    }

    /**
     * Всавляє новий пароль до БД
     */
    public void insertPassword() {
        LoginView login = new LoginView();
        login.setVisible(true);

        JButton btnOk = login.getBtnok();
        JButton btnClose = login.getBtnclose();
        JPasswordField txtPass = login.getTxtpass();

        //видалення обробника подій на кнопку btnOk
        for (ActionListener al : btnOk.getActionListeners()) {
            btnOk.removeActionListener(al);
        }
        //видалення обробника подій на кнопку btnClose
        for (ActionListener al : btnClose.getActionListeners()) {
            btnClose.removeActionListener(al);
        }
        //видалення обробника подій з txtPass
        for (KeyListener kl : txtPass.getKeyListeners()) {
            btnOk.removeKeyListener(kl);
        }

        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (txtPass.getText().length() == 0) {
                    return;
                } else {
                    String pass = escaping(txtPass.getText());
                    pass = Encryptor.encrypt(pass);

                    if (isTrueMasster(pass)) {

                        PasswordInfo info = PasswordInfo.getInstanse();
                        info.btnSaveForInsertingActionListener();
                        info.setTitle("Створити пароль");
                        info.clearPasswordInfo();
                        updateType(info.getCboxType());
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) MainView.getInstanse().getjTree1().getLastSelectedPathComponent();
                        if (!node.isRoot() || node != null) {
                            info.cboxType.setSelectedItem(node.toString());
                        }
                        info.show();
                        login.dispose();

                    } else {
                        JOptionPane.showMessageDialog(LoginView.getInstanse(), "Неправильний master-пароль!");
                        login.setVisible(false);
                        login.dispose();
                    }
                }

            }
        });
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login.setVisible(false);
                login.dispose();
            }

        });

        txtPass.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == com.sun.glass.events.KeyEvent.VK_ENTER) {
                    if (txtPass.getText().length() == 0) {
                        return;
                    } else {
                        String pass = escaping(txtPass.getText());
                        pass = Encryptor.encrypt(pass);

                        if (isTrueMasster(pass)) {

                            PasswordInfo info = PasswordInfo.getInstanse();
                            info.btnSaveForInsertingActionListener();
                            info.setTitle("Створити пароль");
                            info.clearPasswordInfo();
                            updateType(info.getCboxType());
                            DefaultMutableTreeNode node = (DefaultMutableTreeNode) MainView.getInstanse().getjTree1().getLastSelectedPathComponent();
                            if (!node.isRoot() || node != null) {
                                info.cboxType.setSelectedItem(node.toString());
                            }
                            info.show();
                            login.dispose();

                        } else {
                            JOptionPane.showMessageDialog(LoginView.getInstanse(), "Неправильний master-пароль!");
                            login.setVisible(false);
                        }
                    }
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }

        });
    }

    /**
     * Викликає PasswordInfo для редагування паролю
     * @param tree дерево типів JTree
     * @param table таблиця з паролями
     */
    public void editPassword(JTree tree, JTable table) {
        int row = table.getSelectedRow();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        if (row == -1 || node == null || node.isRoot()) {
            return;
        } else {

            LoginView login = new LoginView();
            login.setVisible(true);

            String type = node.toString();

            JButton btnOk = login.getBtnok();
            JButton btnClose = login.getBtnclose();
            JPasswordField txtPass = login.getTxtpass();

            //видалення обробника подій на кнопку btnOk
            for (ActionListener al : btnOk.getActionListeners()) {
                btnOk.removeActionListener(al);
            }
            //видалення обробника подій на кнопку btnClose
            for (ActionListener al : btnClose.getActionListeners()) {
                btnClose.removeActionListener(al);
            }
            //видалення обробника подій з txtPass
            for (KeyListener kl : txtPass.getKeyListeners()) {
                txtPass.removeKeyListener(kl);
            }

            btnOk.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    if (txtPass.getText().length() == 0) {
                        return;
                    } else {
                        String pass = escaping(txtPass.getText());
                        pass = Encryptor.encrypt(pass);

                        if (isTrueMasster(pass)) {
                            //якщо пароль введено правильно
                            sql = "SELECT id FROM type WHERE name= '" + type + "'";
                            int id = query.getIntValue(sql, "id");
                            sql = "SELECT id FROM password WHERE title = '" + table.getValueAt(row, 0).toString() + "' "
                                    + "AND type_id='" + id + "' "
                                    + "AND userName = '" + table.getValueAt(row, 1).toString() + "' "
                                    + "AND password = '" + table.getValueAt(row, 2).toString() + "' "
                                    + "AND webSite = '" + table.getValueAt(row, 3).toString() + "' ";
                            id = query.getIntValue(sql, "id");

                            PasswordInfo info = PasswordInfo.getInstanse();
                            info.setTitle("Редагуати пароль " + table.getValueAt(row, 0).toString());
                            info.clearPasswordInfo();

                            //встановлюємо тип
                            JComboBox cboxType = info.getCboxType();
                            updateType(cboxType);
                            cboxType.setSelectedItem(type);

                            //встановлюємо назву
                            JTextField title = info.getTxtTitle();
                            title.setText(table.getValueAt(row, 0).toString());

                            //встановлюємо користувача
                            JTextField user = info.getTxtUserName();
                            user.setText(table.getValueAt(row, 1).toString());

                            //встановлюємо пароль
                            JPasswordField passFieldPassword = info.getPassFieldPassword();
                            passFieldPassword.setText(Encryptor.decrypt(table.getValueAt(row, 2).toString()));
                            System.out.println(passFieldPassword.getText());
                            //встановлюємо пароль
                            JPasswordField passFieldRepeat = info.getPassFieldRepeat();
                            passFieldRepeat.setText(Encryptor.decrypt(table.getValueAt(row, 2).toString()));

                            //встановлюємо URL
                            JTextField URL = info.getTxtURL();
                            URL.setText(table.getValueAt(row, 3).toString());

                            //встановлюємо нотатки
                            JTextArea getTxtAreaNotes = info.getTxtAreaNotes();
                            String notes;
                            if (table.getValueAt(row, 4) == null) {
                                notes = "";
                            } else {
                                notes = table.getValueAt(row, 4).toString();
                            }
                            getTxtAreaNotes.setText(notes);

                            info.setPassword_id(id);
                            info.btnSaveForEditingActionListener();
                            info.show();

                            login.setVisible(false);
                            //followUp(mainView);
                        } else {
                            JOptionPane.showMessageDialog(LoginView.getInstanse(), "Неправильний master-пароль!");
                            login.setVisible(false);
                        }
                    }
                    //login.dispose();
                }
            });
            btnClose.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    login.setVisible(false);
                    login.dispose();
                }

            });

            txtPass.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {

                }

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == com.sun.glass.events.KeyEvent.VK_ENTER) {
                        if (txtPass.getText().length() == 0) {
                            return;
                        } else {
                            String pass = escaping(txtPass.getText());
                            pass = Encryptor.encrypt(pass);

                            if (isTrueMasster(pass)) {

                                //якщо пароль введено правильно
                                sql = "SELECT id FROM type WHERE name= '" + type + "'";
                                int id = query.getIntValue(sql, "id");
                                sql = "SELECT id FROM password WHERE title = '" + table.getValueAt(row, 0).toString() + "' "
                                        + "AND type_id='" + id + "' "
                                        + "AND userName = '" + table.getValueAt(row, 1).toString() + "' "
                                        + "AND password = '" + table.getValueAt(row, 2).toString() + "' "
                                        + "AND webSite = '" + table.getValueAt(row, 3).toString() + "' ";
                                id = query.getIntValue(sql, "id");
                                PasswordInfo info = PasswordInfo.getInstanse();
                                info.setTitle("Редагуати пароль " + table.getValueAt(row, 0).toString());
                                info.clearPasswordInfo();

                                //встановлюємо тип
                                JComboBox cboxType = info.getCboxType();
                                updateType(cboxType);
                                cboxType.setSelectedItem(type);

                                //встановлюємо назву
                                JTextField title = info.getTxtTitle();
                                title.setText(table.getValueAt(row, 0).toString());

                                //встановлюємо користувача
                                JTextField user = info.getTxtUserName();
                                user.setText(table.getValueAt(row, 1).toString());

                                //встановлюємо пароль
                                JPasswordField passFieldPassword = info.getPassFieldPassword();
                                passFieldPassword.setText(Encryptor.decrypt(table.getValueAt(row, 2).toString()));

                                //встановлюємо пароль
                                JPasswordField passFieldRepeat = info.getPassFieldRepeat();
                                passFieldRepeat.setText(Encryptor.decrypt(table.getValueAt(row, 2).toString()));

                                //встановлюємо URL
                                JTextField URL = info.getTxtURL();
                                URL.setText(table.getValueAt(row, 3).toString());

                                //встановлюємо нотатки
                                JTextArea getTxtAreaNotes = info.getTxtAreaNotes();
                                String notes;
                                if (table.getValueAt(row, 4) == null) {
                                    notes = "";
                                } else {
                                    notes = table.getValueAt(row, 4).toString();
                                }
                                getTxtAreaNotes.setText(notes);

                                info.setPassword_id(id);
                                info.btnSaveForEditingActionListener();
                                info.show();

                                login.setVisible(false);
                                login.dispose();

                                //followUp(mainView);
                            } else {
                                JOptionPane.showMessageDialog(LoginView.getInstanse(), "Неправильний master-пароль!");
                                login.setVisible(false);
                            }
                        }
                    }

                }

                @Override
                public void keyReleased(KeyEvent e) {

                }

            });
        }
    }

    /**
     * Виконуэ оновлення master-паролю
     * @param password новий master-пароль
     */
    public void updateMasterPassword(String password) {
        sql = "UPDATE [master] SET [password] = '" + password + "' WHERE [id] = 1";
        query.updateValue(sql);
    }

    /**
     * Викликає вікно EditMasterView для зміни master-паролю
     */
    public void editMasterPassword() {
        LoginView login = new LoginView();
        login.setVisible(true);
        JButton btnOk = login.getBtnok();
        JButton btnClose = login.getBtnclose();
        JPasswordField txtPass = login.getTxtpass();

        //видалення обробника подій на кнопку btnOk
        for (ActionListener al : btnOk.getActionListeners()) {
            btnOk.removeActionListener(al);
        }
        //видалення обробника подій на кнопку btnClose
        for (ActionListener al : btnClose.getActionListeners()) {
            btnClose.removeActionListener(al);
        }
        //видалення обробника подій з txtPass
        for (KeyListener kl : txtPass.getKeyListeners()) {
            txtPass.removeKeyListener(kl);
        }
        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (txtPass.getText().length() == 0) {
                    return;
                } else {
                    String pass = escaping(txtPass.getText());
                    pass = Encryptor.encrypt(pass);

                    if (isTrueMasster(pass)) {

                        EditMasterView editMaster = new EditMasterView();
                        editMaster.setVisible(true);
                        login.setVisible(false);
                        login.dispose();

                    } else {
                        JOptionPane.showMessageDialog(LoginView.getInstanse(), "Неправильний master-пароль!");
                        login.setVisible(false);
                    }
                }

            }
        });
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login.setVisible(false);
                login.dispose();
            }

        });

        txtPass.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == com.sun.glass.events.KeyEvent.VK_ENTER) {
                    if (txtPass.getText().length() == 0) {
                        return;
                    } else {
                        String pass = escaping(txtPass.getText());
                        pass = Encryptor.encrypt(pass);

                        if (isTrueMasster(pass)) {

                            EditMasterView editMaster = new EditMasterView();
                            editMaster.setVisible(true);
                            login.setVisible(false);
                            login.dispose();

                        } else {
                            JOptionPane.showMessageDialog(LoginView.getInstanse(), "Неправильний master-пароль!");
                            login.setVisible(false);
                        }
                    }
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }

        });
    }

    /**
     * Змінює дані користувацького паролю
     * @param pass об'єкт типу Password
     */
    public void saveEditedPassword(Password pass) {
        sql = "SELECT id FROM type WHERE name = '" + pass.getType() + "'";
        int type_id = query.getIntValue(sql, "id");

        sql = "UPDATE password "
                + "SET title = '" + pass.getTitle() + "', "
                + "type_id = '" + type_id + "', "
                + "password = '" + pass.getPassword() + "', "
                + "userName = '" + pass.getUserName() + "', "
                + "webSite = '" + pass.getURL() + "', "
                + "notes = '" + pass.getNotes() + "'"
                + "WHERE id = '" + pass.getId() + "'";
        query.updateValue(sql);
    }

    /**
     * <p>Запускає таймер, який прослуховує події мишки і клавіатури</p>
     * <p>Таймер спрацьовує через 30 сек із періодом в 30 сек</p>
     * @see CheckAction
     * @param frame1 JFrame для прослуховування подій
     * @param frame2 JFrame для прослуховування подій
     */
    public void followUp(JFrame frame1, JFrame frame2) {
        Timer timer = new Timer();
        timer.schedule(new CheckAction(frame1, frame2), 30000, 30000);
    }

    /**
     * записує типи паролів в JComboBox
     * @param cbox елемент JComboBox
     */
    public void updateType(JComboBox cbox) {
        sql = "SELECT name FROM type";
        query.updateComBox(cbox, sql, "name");
    }

}
