package atmatm6.proxylauncher.launcher;

import com.sun.istack.internal.Nullable;
import com.sun.javaws.exceptions.InvalidArgumentException;
import org.apache.http.auth.InvalidCredentialsException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import static atmatm6.proxylauncher.misc.HttpRunner.post;

public class LoginUtils {
    private static CloseableHttpClient client;
    private static String email;
    private static String password;
    private static JSONParser parser;
    private static String server = "https://authserver.mojang.com";

    // Simple initialization
    public static void setup(){
        client = HttpClients.createDefault();
        parser = new JSONParser();
    }

    // This looks shady af but if you're here then you're the shady one ;)
    // You can null each detail if you've already inputted it and changed it between now and before
    // I probably won't need to do it, but it's there.
    public static void setLoginDetails(@Nullable String email, @Nullable String password){
        if (email != null) setEmail(email);
        if (password != null) setPassword(password);
    }

    // Returns true if successful
    public static boolean authenticate() throws Exception {
        if (email == null || password == null) throw new InvalidCredentialsException();
        String[] re = (String[]) ProfileUtils.read("tokens");
        boolean useclto = false;
        if (!re[0].equals("it's cold outside")) useclto = true;
        JSONObject obj = new JSONObject();
        JSONObject agent = new JSONObject();
        agent.put("version",1);
        agent.put("name","Minecraft");
        obj.put("agent",agent);
        obj.put("username", email);
        obj.put("password",password);
        if (useclto) obj.put("clientToken",re[1]);
        CloseableHttpResponse resp = post(server + "/authenticate",obj.toJSONString());
        String body = EntityUtils.toString(resp.getEntity());
        if (resp.getStatusLine().getStatusCode() == 200){
            ProfileUtils.write("auth",parser.parse(body));
            return true;
        } else {
            throw new InvalidCredentialsException("An issue occcured, please don't come again.");
        }
    }

    private static void setEmail(String email) {
        LoginUtils.email = email;
    }

    private static void setPassword(String password) {
        LoginUtils.password = password;
    }

    // returns false if invalid
    public static boolean refresh() throws Exception {
        String[] re = (String[]) ProfileUtils.read("tokens");
        if (!validate(re)) return false;
        HttpPost post = new HttpPost(server + "/refresh");
        JSONObject obj = new JSONObject();
        obj.put("accessToken",re[0]);
        obj.put("clientToken",re[1]);
        CloseableHttpResponse resp = post(server + "/refresh", obj.toJSONString());
        String body = EntityUtils.toString(resp.getEntity());
        if (resp.getStatusLine().getStatusCode() == 200){
            ProfileUtils.write("auth",parser.parse(body));
            return true;
        }
        return false;
    }

    private static boolean validate(String[] re) throws Exception {
        if (re[0].equals("it's cold outside")) return false;
        HttpPost post = new HttpPost(server + "/validate");
        JSONObject obj = new JSONObject();
        obj.put("accessToken",re[0]);
        obj.put("clientToken",re[1]);
        CloseableHttpResponse resp = post("","");
        String body = EntityUtils.toString(resp.getEntity());
        if (resp.getStatusLine().getStatusCode() == 204)return true;
        return false;
    }

    public static void signOutOrInvalidate() throws Exception {
        HttpPost post;
        JSONObject obj;
        if (email == null || password == null) {
            String[] re  = new String[]{"it's cold outside"};
            try {
                re = (String[]) ProfileUtils.read("tokens");
            } catch (InvalidArgumentException ignored){}
            if (re[0].equals("it's cold outside") )throw new Exception();
            post = new HttpPost();
            obj = new JSONObject();
            obj.put("accessToken",re[0]);
            obj.put("clientToken",re[1]);
        } else {
            post = new HttpPost();
            obj = new JSONObject();
            obj.put("username",email);
            obj.put("password",password);
        }
        CloseableHttpResponse resp = post(server + "", obj.toJSONString());
        if (resp.getStatusLine().getStatusCode() != 200) throw new Exception();
        else {
            ProfileUtils.write("remAuth",null);
        }
    }
}
