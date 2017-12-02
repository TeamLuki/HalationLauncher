package atmatm6.proxylauncher.launcher;

import com.sun.javaws.exceptions.InvalidArgumentException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

import static atmatm6.proxylauncher.misc.HttpRunner.get;

public class VersionUtils {
    private static Map<String, String[]> versions;
    public static Map<String, String[]> getVersions() {
        if (!(versions instanceof HashMap)) {
            versions = new HashMap<>();
            CloseableHttpResponse resp = null;
            try {
                resp = get("https://launchermeta.mojang.com/mc/game/version_manifest.json");
                String body = EntityUtils.toString(resp.getEntity());
                JSONObject obj = (JSONObject) new JSONParser().parse(body);
                JSONArray versionArray = (JSONArray) obj.get("versions");
                JSONObject latest = (JSONObject) obj.get("latest");
                versions.put("latestRelease",new String[]{String.valueOf(latest.get("release"))});
                versions.put("latestSnapshot",new String[]{String.valueOf(latest.get("snapthot"))});
                for (Object aVersionArray : versionArray) {
                    JSONObject version = (JSONObject) aVersionArray;
                    String vername = (String) version.get("id");
                    String type = (String) version.get("type");
                    String metaurl = (String) version.get("url");
                    String[] strings = new String[]{type, metaurl};
                    versions.put(vername, strings);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        System.out.println(versions);
        return versions;
    }
    public static JSONObject getVersionManifest(String version){
        try {
            String url = getVersions().get(version)[1];
            CloseableHttpResponse resp = get(url);
            return (JSONObject) new JSONParser().parse(EntityUtils.toString(resp.getEntity()));
        } catch (Exception ignored){}
        return null;
    }
    public static String getSelectedVersion() throws InvalidArgumentException {
        Element profiles = (Element) ProfileUtils.read("version");
        return profiles.getAttributeNode("version").getValue();
    }

    public static String getLatestVersion() {
        return getVersions().get("latestRelease")[0];
    }
}
