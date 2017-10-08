package atmatm6.proxylauncher;

import atmatm6.proxylauncher.dialogs.LoginDialog;
import atmatm6.proxylauncher.utils.LoginUtils;
import atmatm6.proxylauncher.utils.ProfileUtils;

public class MainLauncher {

    public static void main(String[] args){
        ProfileUtils pu = new ProfileUtils();
        pu.setup();
        // TODO: Check if logged in here.
        final LoginUtils loginUtils = new LoginUtils();
        final LoginDialog logindialog = new LoginDialog();
        logindialog.setLoginutils(loginUtils);
        logindialog.pack();
        logindialog.setVisible(true);
    }
}
