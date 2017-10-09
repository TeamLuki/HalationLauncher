package atmatm6.proxylauncher.dialogs;

import atmatm6.proxylauncher.launcher.LoginUtils;
import org.apache.http.auth.InvalidCredentialsException;

import javax.swing.*;
import java.awt.event.*;

public class LoginDialog extends JFrame {
    private JPanel contentPane;
    public JButton buttonOK;
    private JButton buttonCancel;
    public JTextField emailField;
    public JPasswordField passwordField;
    private JLabel errorField;
    private JComboBox loggedInComboBox;
    private JLabel loggedinlabel;
    public boolean closing;
    /* Maybe add more later, currently using boolean
    * true - Exit
    * false - Launching
    */

    public LoginDialog() {
        setTitle("Login to ProxyLauncher");
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);
        loggedInComboBox.setVisible(false);
        loggedinlabel.setVisible(false);
        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK(){
        String s = new String(passwordField.getPassword());
        LoginUtils.setLoginDetails(emailField.getText(),s);
        try {
            if (LoginUtils.authenticate()){
                closing = false;
                dispose();
            }
        } catch (InvalidCredentialsException e) {
            errorField.setText("Sorry, invalid connection credentials, please try again.");
        } catch (Exception ignored) {}
    }

    private void onCancel() {
        System.out.println("Canceled the login, shutting down");
        closing = true;
        dispose();
    }
}
