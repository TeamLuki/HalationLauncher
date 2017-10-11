package atmatm6.proxylauncher.misc;

import org.json.simple.JSONObject;

public class LibraryDownload implements Runnable {
    JSONObject obj;
    String server = "https://libraries.minecraft.net/";
    public LibraryDownload(Object libjson){
        obj = (JSONObject) libjson;
    }

    @Override
    public void run(){

    }
}
