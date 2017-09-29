
package Models;

import main.Functions;


/**
 * Клас описує об'єкт Пароль. Викоористовується для запису даних в БД
 * @author Admin
 */
public class Password {

    /** {@value }Ідентифікатор паролю */
    private int id;
    
    /** Назва паролю */
    private String title;
    
    /** Тип паролю. Типи визначены в БД */
    private String type;
    
    /** Користувач або логін до паролю */
    private String userName;
    
    /** Пароль */
    private String password;
    
    /** URL-адреса для паролю. Не є обовязковим */
    private String URL;
    
    /** Додаткові відомості про пароль */
    private String notes;

    private final Functions func = new Functions();
    
    /**
     * Повертає URL
     * @return  String URL-адреса для паролю, якщо встановлено
     */
    public String getURL() {
        return URL;
    }

    /**
     * Встановлює URL 
     * @param URL String URL-адреса для паролю
     */
    public void setURL(String URL) {
        this.URL = func.escaping(URL);
    }


    /**
     * Повертає id
     * @return int id паролю
     */
    public int getId(){
        return id;
    }

    /**
     * Повертає назву паролю
     * @return Stting назва паролю
     */
    public String getTitle() {
        return title;
    }

    /**
     * Встановлює назву паролю
     * @param title назва парою
     */ 
    public void setTitle(String title) {
        this.title = func.escaping(title);
    }

    /**
     * Повертає ім'я користувача або логін для паролю
     * @return  String ім'я користувача 
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Встановлює ім'я користувача або логін для паролю, попередньо екранований
     * @param userName ім'я користувача
     */
    public void setUserName(String userName) {
        this.userName = func.escaping(userName);
    }

    /**
     * Повертає String пароль
     * @return пароль
     */
    public String getPassword() {
        return password;
    }

    /**
     * Встановлює пароль, попедньо екранувавши значення
     * @param password  пароль
     */
    public void setPassword(String password) {
        
        this.password = func.escaping(password);
    }

    /**
     * Повертає нотатки
     * @return нотатки
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Встановлює нотатки, попередньо екранувавши значення
     * @param notes  String  нотатки
     */
    public void setNotes(String notes) {
        this.notes = func.escaping(notes);
    }
    /**
     * Повертає ідентифікатор паролю
     * @param id ідентифікатор паролю
     */
    public void setId(int id){
        this.id = id;
    }
    
    /**
     * Повертає тип паролю
     * @return String тип паролю
     */
    public String getType(){
        return type;
    }
    /**
     * Встановлює тип паролю
     * @param type  тип паролю
     */
    public void setType(String type){
        this.type = func.escaping(type);
    }

    
}
