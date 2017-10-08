package atmatm6.proxylauncher.dialogs;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LauncherGUI extends JFrame {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTabbedPane tabs;
    private JComboBox profileChooser;
    private JPanel newsPanel;
    private JButton signOutButton;
    private JSpinner spinner1;

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
