package atmatm6.proxylauncher.misc;

import org.apache.http.client.methods.HttpGet;

public class AssetDownload implements Runnable {
    private String bbhash;
    private String hash;
    private String libloc = "http://resources.download.minecraft.net/";
    public AssetDownload(String hash){
        bbhash = hash.substring(0,1);
        this.hash = hash;
    }

    @Override
    public void run() {
        HttpGet get = new HttpGet();
    }
}
