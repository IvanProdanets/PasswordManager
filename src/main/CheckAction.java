package main;

import Views.LoginView;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.TimerTask;
import javax.swing.JFrame;

/**
 * Клас слухає події мишки і клавіаткри
 * @author Admin
 */
public class CheckAction extends TimerTask {

    static JFrame mainView;
    static JFrame passView;
    static long timeOfLastAction;
    public static final int TIME_TO_RUN = 30000;

    /**
     * Встановлює KeyListener та MouseListener для переданих фреймыв
     * @param jframe MainView 
     * @param jframe1 PasswordInfo 
     */
    CheckAction(JFrame jframe, JFrame jframe1) {
        mainView = jframe;
        passView = jframe1;
        LoginView l = LoginView.getInstanse();
        l.setTitle("Мастер-пароль");
        l.setDefaultCloseOperation(1);
        mainView.addKeyListener(keyListener);
        mainView.addMouseListener(mouseListener);
        passView.addKeyListener(keyListener);
        passView.addMouseListener(mouseListener);
    }
    
    KeyListener keyListener = new KeyListener() {
        @Override
        public void keyPressed(KeyEvent keyEvent) {
            timeOfLastAction = System.currentTimeMillis();
        }

        @Override
        public void keyReleased(KeyEvent keyEvent) {
            timeOfLastAction = System.currentTimeMillis();
        }

        @Override
        public void keyTyped(KeyEvent keyEvent) {
        }

    };

    //перериваємо таймер при виникненні події мишки
    MouseListener mouseListener = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
            timeOfLastAction = System.currentTimeMillis();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            timeOfLastAction = System.currentTimeMillis();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            timeOfLastAction = System.currentTimeMillis();
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            timeOfLastAction = System.currentTimeMillis();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            timeOfLastAction = System.currentTimeMillis();
        }

    };

    /**
     * Перевіряє, чи виникали події KeyListener та MouseListener для переданих фреймыв
     */
    @Override
    public void run() {
        System.out.println("run-current-time " + System.currentTimeMillis());
        if ((System.currentTimeMillis() - timeOfLastAction) < TIME_TO_RUN) {
            System.out.println("timeIF - " + (System.currentTimeMillis() - timeOfLastAction));
            timeOfLastAction = System.currentTimeMillis();
            return;
        }
        System.out.println("timeELSE - " + (System.currentTimeMillis() - timeOfLastAction));
        mainView.setVisible(false);
        passView.setEnabled(false);
        LoginView l = LoginView.getInstanse();
        l.setVisible(true);
        this.cancel();
        System.out.println("Timer finished");
    }
}
