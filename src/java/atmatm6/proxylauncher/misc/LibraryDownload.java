package atmatm6.proxylauncher.misc;

import atmatm6.proxylauncher.launcher.ProfileUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import static atmatm6.proxylauncher.launcher.ProfileUtils.path;
import static atmatm6.proxylauncher.misc.HttpRunner.get;

@SuppressWarnings("ReplaceAllDot")
public class LibraryDownload implements Runnable {
    private JSONObject obj;
    public LibraryDownload(Object libjson){
        obj = (JSONObject) libjson;
    }

    @Override
    public void run(){
        String sep = System.getProperty("file.seperator");
        String workingDir;
        JSONObject artifact = (JSONObject)((JSONObject)obj.get("downloads")).get("artifact");
        String path = ((String) artifact.get("path")).replaceAll("/",sep);
        System.out.println(path);
        try {
            workingDir = (String) ProfileUtils.read("workingDir");
            String jarLocation = path(workingDir,path);
            File jar = new File(jarLocation);
            CloseableHttpResponse resp = get((String) artifact.get("url"));
            InputStream is = resp.getEntity().getContent();
            FileOutputStream fos = new FileOutputStream(jar);
            int inByte;
            while((inByte = is.read()) != -1) fos.write(inByte);
            is.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
