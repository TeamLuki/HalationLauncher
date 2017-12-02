package atmatm6.proxylauncher.game;

import atmatm6.proxylauncher.launcher.VersionUtils;
import atmatm6.proxylauncher.misc.LibraryDownload;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Downloader {
    void downloadGameFiles() {
        try {
            JSONObject obj = VersionUtils.getVersionManifest(VersionUtils.getSelectedVersion());
            assert obj != null;
            JSONArray array = (JSONArray) obj.get("libraries");
            ExecutorService es = Executors.newFixedThreadPool(15);
            for (Object anArray : array) {
                es.submit(new LibraryDownload(anArray));
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
