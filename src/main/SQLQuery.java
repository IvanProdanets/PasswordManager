package main;

import DB.DBconn;
import DB.DbUtils;
import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * Клас містить функції CRUD
 * @author Admin
 */
public class SQLQuery {

    private Connection conn = null;
    private DBconn db = DBconn.getInstance();
    private ResultSet rs = null;
    private PreparedStatement pst = null;
    private String sql = null;

    /**
     * Викликає екземпляр класу DBconn та  підключається до БД
     */
    SQLQuery() {
        try {
            conn = db.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(SQLQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Клас може виконувати Insert, Delete, Update записів у БД(в залежності від SQL запиту)
     * @param sql Sql запит
     */
    public void updateValue(String sql) {
        try {
            pst = conn.prepareStatement(sql);
            pst.executeUpdate();
        } catch (SQLException | HeadlessException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * Виконує створення таблиць в бд або інші операції (в залежності від SQL запиту)
     * @param sql Sql запит
     */
    public void execute(String sql) {
        try {
            pst = conn.prepareStatement(sql);
            pst.execute();
        } catch (SQLException | HeadlessException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * Повертає одне значення типу String за результатом SQL запиту 
     * @param sql Sql запит
     * @param colum_name ім'я колонки, з якої треба вибрати значення
     * @return результат запиту типу String
     */
    public String getStringValue(String sql, String colum_name) {
        String value = null;
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                value = rs.getString(colum_name);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return value;
    }

    /**
     * Повертає одне значення типу Int за результатом SQL запиту 
     * @param sql Sql запит
     * @param colum_name ім'я колонки, з якої треба вибрати значення
     * @return результат запиту типу  Int
     */
    public int getIntValue(String sql, String colum_name) {
        int value = 0;
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                value = rs.getInt(colum_name);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return value;
    }

    /**
     * Повертає одне значення типу boolean за результатом SQL запиту 
     * @param sql Sql запит
     * @param colum_name ім'я колонки, з якої треба вибрати значення
     * @return результат запиту типу boolean
     */
    public boolean getBoolValue(String sql, String colum_name) {
        boolean value = false;
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                value = rs.getBoolean(colum_name);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return value;
    }

    /**
     * Записує в JComboBox результати SQL запиту
     * @param cbox куди записати, тип JComboBox
     * @param sql SQL запит
     * @param parametr ім'я колонки, з якої треба вибрати значення
     */
    public void updateComBox(JComboBox cbox, String sql, String parametr) {
        cbox.removeAllItems();
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                cbox.addItem(rs.getString(parametr));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * Витягує записи з БД в таблицю, встановлюючи для неї модель
     * @see DB.DbUtils#resultSetToTableModel(ResultSet)
     * @param table таблиця, в яку вносити результу 
     * @param sql SQL-запит
     */
    public void updateTable(JTable table, String sql) {
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            table.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * Заповнює елемент JTree результами SQL-запиту
     * @param tree куди вносити результат запиту, елемент JTree
     * @param sql SQL-запит
     * @param rootName встановити кореневий вузол String
     * @param sqlParam ім'я колонки, з якої треба вибрати значення
     */
    public void updateJTree(JTree tree, String sql, String rootName, String sqlParam) {
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            tree.setModel(null);
            DefaultMutableTreeNode top;
            top = new DefaultMutableTreeNode(rootName);
            DefaultMutableTreeNode node;
            while (rs.next()) {
                node = new DefaultMutableTreeNode(rs.getString(sqlParam));
                top.add(node);
            }
            DefaultTreeModel model = new DefaultTreeModel(top);
            tree.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

}
