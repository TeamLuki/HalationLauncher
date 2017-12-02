package atmatm6.proxylauncher.game;

import atmatm6.proxylauncher.launcher.ProfileUtils;

import java.io.File;
import java.io.IOException;

public class LaunchUtils {
    public void launch() {
        Downloader dl = new Downloader();
        dl.downloadGameFiles();
        try {
            Process minecraft = new ProcessBuilder(constructProcessString("")).directory((File) ProfileUtils.read("launchdir")).start();
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
    public static String[] constructProcessString(String s){
        return new String[]{""};
    }
}