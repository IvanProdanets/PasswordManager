package Views;

import DB.DBconn;
import DES.Encryptor;
import Models.Password;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import main.Functions;

/**
 * Клас описує фрейм для створення та редагування користувацьких паролів
 * @author Admin
 */
public class PasswordInfo extends javax.swing.JFrame {

    /**
     * Повертає ідентифікатор паролю
     * @return 
     */
    public int getPassword_id() {
        return password_id;
    }

    /**
     * Встановлює ідентифікатор паролю
     * @param password_id 
     */
    public void setPassword_id(int password_id) {
        this.password_id = password_id;
    }

    Connection conn = null;
    DBconn db = DBconn.getInstance();
    Functions func = new Functions();
    private static volatile PasswordInfo instanse;
    private int password_id;
    
    /**
     * Конструктор створює підключення до БД
     */
    public PasswordInfo() {
        initComponents();
        try {
            conn = db.getConnection();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
        }
    }

    /**
     * Повертає екземпляр об'єкту PasswordInfo
     * @return 
     */
    public static PasswordInfo getInstanse() {

        if (instanse == null) {
            synchronized (PasswordInfo.class) {
                if (instanse == null) {
                    instanse = new PasswordInfo();
                }

            }
        }
        return instanse;

    }


    /**
     * Очищує всі поля на фреймі
     */
    public void clearPasswordInfo() {
        txtTitle.setText("");
        txtUserName.setText("");
        passFieldPassword.setText("");
        passFieldRepeat.setText("");
        txtURL.setText("");
        txtAreaNotes.setText("");
    }

    /**
     * Повертає JButton з PasswordInfo
     * @return JButton
     */
    public JButton getBtnSave() {
        return btnSave;
    }
    
    
    /**
     * Встановлює btnSave для PasswordInfo
     * @param btnSave  JButton
     */
    public void setBtnSave(JButton btnSave) {
        this.btnSave = btnSave;
    }

    /**
     * Перевіряє, чи існують незаповнені поля
     * @return true або false
     */
    public boolean existEmptyFields() {

        if (txtTitle.getText().equals("")
                || txtUserName.getText().equals("")
                || passFieldPassword.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Заповніть поля, позначені зірочкою");
            return true;
        }
        return false;

    }

    /**
     * Перевіряє, чи паролі введено однакові(пароль і пароль для перевірки)
     * @return true або false
     */
    public boolean isPasswordsEquals() {

        if (passFieldPassword.getText().equals(passFieldRepeat.getText())) {
            return true;
        } else {
            JOptionPane.showMessageDialog(this, "Паролі не співпадають!");
            return false;
        }

    }

    /**
     * Встановлює ActionListener на кнопку btnSave для дії редагування
     * @see Functions#saveEditedPassword(Password) 
     */
    public void btnSaveForEditingActionListener() {
        JFrame info = this;
        for (ActionListener al : btnSave.getActionListeners()) {
            btnSave.removeActionListener(al);
        }
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isPasswordsEquals() & !existEmptyFields()) {
                    Password pass = new Password();
                    pass.setId(getPassword_id());
                    pass.setTitle(txtTitle.getText());
                    pass.setType(cboxType.getSelectedItem().toString());
                    pass.setUserName(txtUserName.getText());
                    pass.setPassword(Encryptor.encrypt(passFieldPassword.getText()));
                    pass.setURL(txtURL.getText());
                    pass.setNotes(txtAreaNotes.getText());
                    info.hide();
                    func.saveEditedPassword(pass);
                }
                

            }
        });

    }

    /**
     * Встановлює ActionListener на кнопку btnSave для дії додавання паролю
     * Додає дані з форми в БД
     * @see Functions#insertPassword(Password) 
     */
    public void btnSaveForInsertingActionListener() {
        JFrame info = this;
        for (ActionListener al : btnSave.getActionListeners()) {
            btnSave.removeActionListener(al);
        }
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isPasswordsEquals() & !existEmptyFields()) {
                    Password pass = new Password();
                    pass.setId(getPassword_id());
                    pass.setTitle(txtTitle.getText());
                    pass.setType(cboxType.getSelectedItem().toString());
                    pass.setUserName(txtUserName.getText());
                    pass.setPassword(Encryptor.encrypt(passFieldPassword.getText()));
                    pass.setURL(txtURL.getText());
                    pass.setNotes(txtAreaNotes.getText());
                    func.insertPassword(pass);
                    info.hide();
                    MainView main = MainView.getInstanse();
                    func.updateTablePassword(main.getjTable1(), cboxType.getSelectedItem().toString());
                    
                }
            }

        });
        

        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cboxType = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        txtTitle = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtUserName = new javax.swing.JTextField();
        passFieldPassword = new javax.swing.JPasswordField();
        passFieldRepeat = new javax.swing.JPasswordField();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtAreaNotes = new javax.swing.JTextArea();
        btnSave = new javax.swing.JButton();
        btnCanlec = new javax.swing.JButton();
        btnShowPassword = new javax.swing.JButton();
        btnGeneratePassword = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtURL = new javax.swing.JTextField();

        setLocationByPlatform(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel1.setText("<html>Група:  <sup>&#42;</sup></html>");

        jLabel2.setText("<html>Назва:  <sup>&#42;</sup></html>");

        jLabel3.setText("<html>Користувач:  <sup>&#42;</sup></html>");

        jLabel4.setText("<html>Пароль:  <sup>&#42;</sup></html>");

        jLabel5.setText("<html>Повторити:  <sup>&#42;</sup></html>");

        jLabel6.setText("Нотатки:");

        txtAreaNotes.setColumns(20);
        txtAreaNotes.setRows(5);
        jScrollPane3.setViewportView(txtAreaNotes);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/floppy.png"))); // NOI18N
        btnSave.setText("Зберегти");
        btnSave.setAlignmentX(10.0F);
        btnSave.setAlignmentY(10.0F);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnCanlec.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        btnCanlec.setText("Відміна");
        btnCanlec.setAlignmentX(10.0F);
        btnCanlec.setAlignmentY(10.0F);
        btnCanlec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCanlecActionPerformed(evt);
            }
        });

        btnShowPassword.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/eye_view_see_watch_show_sleep-24.png"))); // NOI18N
        btnShowPassword.setToolTipText("Показати пароль");
        btnShowPassword.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnShowPasswordMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnShowPasswordMouseReleased(evt);
            }
        });
        btnShowPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowPasswordActionPerformed(evt);
            }
        });

        btnGeneratePassword.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/App-password-icon-24.png"))); // NOI18N
        btnGeneratePassword.setToolTipText("Згенерувати пароль");
        btnGeneratePassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGeneratePasswordActionPerformed(evt);
            }
        });

        jLabel7.setText("URL:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(btnSave)
                            .addGap(39, 39, 39)
                            .addComponent(btnCanlec))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(50, 50, 50)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(50, 50, 50)
                                .addComponent(txtURL))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(50, 50, 50)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(passFieldRepeat, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(passFieldPassword, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtUserName, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboxType, javax.swing.GroupLayout.Alignment.LEADING, 0, 287, Short.MAX_VALUE)
                            .addComponent(txtTitle, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnShowPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnGeneratePassword, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))))
                .addGap(11, 11, 11))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboxType, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(passFieldPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnShowPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(passFieldRepeat, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGeneratePassword, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtURL, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jLabel6)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCanlec, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Зберігає дані з форми в БД
     * @see Functions#insertPassword(Password) 
     * @param evt 
     */
    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (isPasswordsEquals() & !existEmptyFields()) {
            Password pass = new Password();
            pass.setTitle(txtTitle.getText());
            pass.setType(cboxType.getSelectedItem().toString());
            pass.setUserName(txtUserName.getText());
            pass.setPassword(passFieldPassword.getText());
            pass.setURL(txtURL.getText());
            pass.setNotes(txtAreaNotes.getText());
            func.insertPassword(pass);
            this.hide();
        }

    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnShowPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowPasswordActionPerformed
        
    }//GEN-LAST:event_btnShowPasswordActionPerformed

    /**
     * Дозволяє passFieldPassword та passFieldRepeat копіювати значення паролів
     * @param evt 
     */
    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
       passFieldPassword.putClientProperty("JPasswordField.cutCopyAllowed",true);
       passFieldRepeat.putClientProperty("JPasswordField.cutCopyAllowed",true);
    }//GEN-LAST:event_formWindowOpened

    /**
     * Кнопка описує генерацію паролю
     * @see Functions#generatePassword() 
     * @param evt 
     */
    private void btnGeneratePasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGeneratePasswordActionPerformed
        String generatedPassword = func.generatePassword();
        passFieldPassword.setText(generatedPassword);
        passFieldRepeat.setText(generatedPassword);
    }//GEN-LAST:event_btnGeneratePasswordActionPerformed

    /**
     * Приховує та очищує форму PasswordInfo
     * @param evt 
     */
    private void btnCanlecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCanlecActionPerformed
        this.hide();
        this.clearPasswordInfo();
    }//GEN-LAST:event_btnCanlecActionPerformed

    /**
     * Показує приховані в полях паролі
     * @param evt 
     */
    private void btnShowPasswordMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnShowPasswordMousePressed
        passFieldPassword.setEchoChar((char) 0);
        passFieldRepeat.setEchoChar((char) 0);
    }//GEN-LAST:event_btnShowPasswordMousePressed

    /**
     * Приховує в полях паролі
     * @param evt 
     */
    private void btnShowPasswordMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnShowPasswordMouseReleased
        passFieldPassword.setEchoChar('*');
        passFieldRepeat.setEchoChar('*');
    }//GEN-LAST:event_btnShowPasswordMouseReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PasswordInfo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                PasswordInfo info = new PasswordInfo();
                info.setLocationRelativeTo(null);
                info.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCanlec;
    private javax.swing.JButton btnGeneratePassword;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnShowPassword;
    public javax.swing.JComboBox<String> cboxType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPasswordField passFieldPassword;
    private javax.swing.JPasswordField passFieldRepeat;
    private javax.swing.JTextArea txtAreaNotes;
    private javax.swing.JTextField txtTitle;
    private javax.swing.JTextField txtURL;
    private javax.swing.JTextField txtUserName;
    // End of variables declaration//GEN-END:variables

    public JComboBox getCboxType() {
        return cboxType;
    }

    public void setCboxType(JComboBox cboxType) {
        this.cboxType = cboxType;
    }

    public JPasswordField getPassFieldPassword() {
        return passFieldPassword;
    }

    public void setPassFieldPassword(JPasswordField passFieldPassword) {
        this.passFieldPassword = passFieldPassword;
    }

    public JPasswordField getPassFieldRepeat() {
        return passFieldRepeat;
    }

    public void setPassFieldRepeat(JPasswordField passFieldRepeat) {
        this.passFieldRepeat = passFieldRepeat;
    }

    public JTextArea getTxtAreaNotes() {
        return txtAreaNotes;
    }

    public void setTxtAreaNotes(JTextArea txtAreaNotes) {
        this.txtAreaNotes = txtAreaNotes;
    }

    public JTextField getTxtTitle() {
        return txtTitle;
    }

    public void setTxtTitle(JTextField txtTitle) {
        this.txtTitle = txtTitle;
    }

    public JTextField getTxtURL() {
        return txtURL;
    }

    public void setTxtURL(JTextField txtURL) {
        this.txtURL = txtURL;
    }

    public JTextField getTxtUserName() {
        return txtUserName;
    }

    public void setTxtUserName(JTextField txtUserName) {
        this.txtUserName = txtUserName;
    }
}
