
package main;

import Views.CreateMaster;
import Views.LoginView;

/**
 * Головний клас. Запускаэ проект
 * @author Admin
 */
public class Main {
    
    private Functions func = new Functions();
    
    /**
     * Запускає вікно авторизації у випадку існування БД. 
     * Інакше запускає вікно для створення нового master паролю
     * @param args 
     */
    public static void main(String[] args) {
        
        Main main = new Main();
        
        //перевіряємо, чи встановлено БД
        if(main.func.isDbValid() && main.func.isMasterPasswordSet()){
            LoginView login = new LoginView();
            login.setVisible(true);
        } else {
            CreateMaster createMasterFrame = new CreateMaster();
            createMasterFrame.setVisible(true);
        }
    }
}
