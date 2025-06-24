/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package live.gavesh;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.UIManager;
import live.gavesh.pages.*;
import live.gavesh.util.DatabaseHelper;
import live.gavesh.util.ReportHelper;

/**
 *
 * @author gav
 */
public class Main {

    
    public static void main(String args[]) {
        FlatLightLaf.setup();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(new FlatLightLaf());
                } catch (Exception e) {
                    System.out.println("Error setting themes " + e.getMessage());
                }
                LoginForm login = new LoginForm();
                login.setVisible(true);
            }
        });
    }

}
