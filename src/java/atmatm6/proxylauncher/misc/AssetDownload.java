package atmatm6.proxylauncher.misc;

import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.BufferedInputStream;

import static atmatm6.proxylauncher.misc.HttpRunner.get;

public class AssetDownload implements Runnable {
    private String bbhash;
    private String hash;
    private String libdloc = "";
    public AssetDownload(String hash){
        bbhash = hash.substring(0,1);
        this.hash = hash;
    }

    @Override
    public void run() {
        try {
            CloseableHttpResponse resp = get("temp");
            BufferedInputStream is = new BufferedInputStream(resp.getEntity().getContent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
