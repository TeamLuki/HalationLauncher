package atmatm6.proxylauncher.misc;

import org.json.simple.JSONObject;

public class DownloadTask implements Runnable {
    private String bbhash;
    private String hash;
    private boolean isLib;
    private String libloc = "https://libraries.minecraft.net";
    public DownloadTask(String hash){
        bbhash = hash.substring(0,1);
        this.hash = hash;
        isLib = false;
    }
    public DownloadTask(JSONObject libjson){
        isLib = true;
    }

    @Override
    public void run() {

    }
}
