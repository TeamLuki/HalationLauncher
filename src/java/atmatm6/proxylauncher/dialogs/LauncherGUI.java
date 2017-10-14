package atmatm6.proxylauncher.dialogs;

import atmatm6.proxylauncher.launcher.LoginUtils;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LauncherGUI extends JFrame {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTabbedPane tabs;
    private JComboBox profileChooser;
    private JPanel newsPanel;
    private JButton signOutButton;
    private JSpinner spinner1;
    private JComboBox versionComboBox;
    private JTextField hostBox;
    private JButton aboutButton;

    public LauncherGUI() {
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);
        JFXPanel jpane = new JFXPanel();
        Platform.runLater(() -> {
            WebView wv = new WebView();
            WebEngine we = wv.getEngine();
            we.load("http://texttale.square7.ch/proxynews.html");
            we.setJavaScriptEnabled(false);
            jpane.setScene(new Scene(wv));
        });
        newsPanel.add(jpane, BorderLayout.CENTER);
        buttonOK.addActionListener(e -> onOK());
        JFrame self = this;

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
        signOutButton.addActionListener(e -> {
            try {
                LoginUtils.signOutOrInvalidate();
                System.out.print("Cya!");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        aboutButton.addActionListener(e -> {
            try {
                InputStream stream = LauncherGUI.class.getResourceAsStream("about.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String title = reader.readLine();
                StringBuffer buf = new StringBuffer();
                String str;
                while ((str = reader.readLine()) != null) {
                    buf.append(str + "\n" );
                }
                try { stream.close(); } catch (Throwable ignore) {}
                JOptionPane.showMessageDialog(self,buf.toString(),title, JOptionPane.OK_OPTION);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    private void onOK() {
        dispose();
        throw new NotImplementedException();
    }

    private void onCancel() {
        dispose();
    }
}
