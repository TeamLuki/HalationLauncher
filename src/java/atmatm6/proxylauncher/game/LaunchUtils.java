package atmatm6.proxylauncher.game;

import atmatm6.proxylauncher.launcher.ProfileUtils;
import com.sun.javaws.exceptions.InvalidArgumentException;

public class LaunchUtils {
    public static void main(String[] args){

    }

    public void launch(){
        try {
            ProfileUtils.read("version");
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }
    }
}