package atmatm6.proxylauncher;

import atmatm6.proxylauncher.dialogs.LauncherGUI;
import atmatm6.proxylauncher.dialogs.LoginDialog;
import atmatm6.proxylauncher.utils.LoginUtils;
import atmatm6.proxylauncher.utils.ProfileUtils;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainLauncher {

    public static void main(String[] args){
        ProfileUtils pu = new ProfileUtils();
        pu.setup();
        final LoginUtils loginUtils = new LoginUtils();

        // TODO: Check if logged in here.
        final LoginDialog logindialog = new LoginDialog();
        logindialog.setLoginutils(loginUtils);
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
    }
}
