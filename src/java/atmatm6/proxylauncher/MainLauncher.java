package atmatm6.proxylauncher;

import atmatm6.proxylauncher.dialogs.LauncherGUI;
import atmatm6.proxylauncher.dialogs.LoginDialog;
import atmatm6.proxylauncher.launcher.LoginUtils;
import atmatm6.proxylauncher.launcher.ProfileUtils;
import atmatm6.proxylauncher.misc.HttpRunner;
import atmatm6.proxylauncher.misc.TextAreaOutputStream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintStream;


public class MainLauncher {

    public static void main(String[] args){
        if (GraphicsEnvironment.isHeadless()){
            System.out.println("Requires a non-headless computer sorry.");
            System.exit(1);
        }
        //forgot what the text area and print stream were actually for rip
        JTextArea local = new JTextArea();
        PrintStream out = new PrintStream(new TextAreaOutputStream(local));
        ProfileUtils pu = new ProfileUtils();
        pu.setup();
        LoginUtils.setup();
        HttpRunner.setup();
        boolean runLogin = true;
        try {
            runLogin = LoginUtils.refresh();
        } catch (Exception ignored) {
            System.out.println("erare");
        }
        if (runLogin) {
            final LoginDialog logindialog = new LoginDialog();
            logindialog.pack();
            logindialog.setVisible(true);
            logindialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    if (logindialog.closing) System.exit(0);
                    LauncherGUI lg = new LauncherGUI();
                    lg.pack();
                    lg.setVisible(true);
                }
            });
        } else {
            LauncherGUI lg = new LauncherGUI();
            lg.pack();
            lg.setVisible(true);
        }
    }
}
