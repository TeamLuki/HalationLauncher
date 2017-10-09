package atmatm6.proxylauncher;

import atmatm6.proxylauncher.dialogs.LauncherGUI;
import atmatm6.proxylauncher.dialogs.LoginDialog;
import atmatm6.proxylauncher.utils.LoginUtils;
import atmatm6.proxylauncher.utils.ProfileUtils;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class MainLauncher {

    public static void main(String[] args){
        ProfileUtils pu = new ProfileUtils();
        pu.setup();
        final LoginUtils loginUtils = new LoginUtils();
        boolean runLogin = false;
        try {
            runLogin = loginUtils.refresh();
        } catch (IOException | ParseException | ParserConfigurationException | SAXException ignored) {}
        // TODO: Check if logged in here.
        if (runLogin) {
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
        } else {
            LauncherGUI lg = new LauncherGUI();
            lg.pack();
            lg.setVisible(true);
        }
    }
}
