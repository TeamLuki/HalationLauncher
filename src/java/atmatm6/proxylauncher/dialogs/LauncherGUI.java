package atmatm6.proxylauncher.dialogs;

import atmatm6.proxylauncher.game.LaunchUtils;
import atmatm6.proxylauncher.launcher.LoginUtils;
import atmatm6.proxylauncher.launcher.VersionUtils;
import atmatm6.proxylauncher.misc.SortedComboBoxModel;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LauncherGUI extends JFrame {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTabbedPane tabs;
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
            we.load("http://texttale.square7.ch/proxynews.html"); //TODO: Lead to a dreamlive page when website exists
            we.setJavaScriptEnabled(false);
            jpane.setScene(new Scene(wv));
        });
        newsPanel.add(jpane, BorderLayout.CENTER);
        buttonOK.addActionListener(e -> {
            try {
                onOK();
            } catch (IllegalArgumentException e1) {
                e1.printStackTrace();
            }
        });
        JFrame self = this;
        buttonCancel.addActionListener(e -> onCancel());
        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
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
                    buf.append(str + "\n");
                }
                try { stream.close(); } catch (Throwable ignore) {}
                JOptionPane.showMessageDialog(self,buf.toString(),title, JOptionPane.OK_OPTION);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        SortedComboBoxModel<String> model = new SortedComboBoxModel<>();
        VersionUtils.getVersions().forEach((k,v) -> {
            model.addElement(k);
        });
        versionComboBox.setModel(model);
    }

    private void onOK() throws IllegalArgumentException {
        LaunchUtils launch = new LaunchUtils();
        launch.launch();
    }

    private void onCancel() {
        dispose();
        System.exit(0);
    }
}
