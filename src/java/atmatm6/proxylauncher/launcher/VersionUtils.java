package atmatm6.proxylauncher.launcher;

import atmatm6.proxylauncher.misc.HttpRunner;
import com.sun.javaws.exceptions.InvalidArgumentException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.Map;

public class VersionUtils {
    static Map<String, String[]> versions;
    public static Map<String, String[]> getVersions() {
        if (!(versions instanceof HashMap)) {
            versions = new HashMap<>();
            CloseableHttpResponse resp = null;
            try {
                resp = HttpRunner.get("http://launchermeta.mojang.com/mc/game/version_manifest.json");
                String body = EntityUtils.toString(resp.getEntity());
                JSONObject obj = (JSONObject) new JSONParser().parse(body);
                JSONArray versionArray = (JSONArray) obj.get("versions");
                JSONObject latest = (JSONObject) obj.get("latest");
                versions.put("latestRelease",new String[]{String.valueOf(latest.get("release"))});
                versions.put("latestSnapshot",new String[]{String.valueOf(latest.get("snapthot"))});
                for (int temp=0;temp<versionArray.size();temp+=1) {
                    JSONObject version = (JSONObject) versionArray.get(temp);
                    String vername = (String) version.get("id");
                    String type = (String) version.get("type");
                    String metaurl = (String) version.get("url");
                    String[] strings = new String[]{type, metaurl};
                    versions.put(vername,strings);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return versions;
    }
    public static JSONObject getVersionManifest(String version){
        try {
            String url = getVersions().get(version)[1];
            CloseableHttpResponse resp = HttpRunner.get("url");
            return null;
        } catch (Exception ignored){}
        return null;
    }
    public static String getSelectedVersion() throws InvalidArgumentException { //TODO: CHECK SELECTED VERSION
        throw new NotImplementedException();
//      return (String) ProfileUtils.read("version");
    }

    public static String getLatestVersion() {
        return getVersions().get("latestRelease")[0];
    }
}
