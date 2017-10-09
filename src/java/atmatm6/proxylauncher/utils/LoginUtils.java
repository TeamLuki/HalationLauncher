package atmatm6.proxylauncher.utils;

import com.sun.istack.internal.Nullable;
import com.sun.javaws.exceptions.InvalidArgumentException;
import org.apache.http.auth.InvalidCredentialsException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class LoginUtils {
    private CloseableHttpClient client;
    private String email;
    private String password;
    private JSONParser parser;
    private String server = "https://authserver.mojang.com";

    // Simple initialization
    public LoginUtils(){
        client = HttpClients.createDefault();
        parser = new JSONParser();
    }

    // This looks shady af but if you're here then you're the shady one ;)
    // You can null each detail if you've already inputted it and changed it between now and before
    // I probably won't need to do it, but it's there.
    public void setLoginDetails(@Nullable String email, @Nullable String password){
        if (email != null) this.email = email;
        if (password != null) this.password = password;
    }

    // Returns true if successful
    public boolean authenticate() throws InvalidCredentialsException, IOException, ParseException, ParserConfigurationException, SAXException, InvalidArgumentException {
        if (email == null || password == null) throw new InvalidCredentialsException();
        String[] re = ProfileUtils.read("tokens");
        boolean useclto = false;
        if (!re[0].equals("it's cold outside")) useclto = true;
        HttpPost post = new HttpPost(server+"/authenticate");
        JSONObject obj = new JSONObject();
        JSONObject agent = new JSONObject();
        agent.put("version",1);
        agent.put("name","Minecraft");
        obj.put("agent",agent);
        obj.put("username", email);
        obj.put("password",password);
        if (useclto) obj.put("clientToken",re[1]);
        StringEntity entity = new StringEntity(obj.toJSONString());
        post.setEntity(entity);
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-type", "application/json");
        CloseableHttpResponse resp = client.execute(post);
        String body = EntityUtils.toString(resp.getEntity());
        if (resp.getStatusLine().getStatusCode() == 200){
            ProfileUtils.write("auth",parser.parse(body));
            return true;
        } else {
            throw new InvalidCredentialsException("An issue occcured, please don't come again.");
        }
    }

    // returns false if invalid
    public boolean refresh() throws IOException, ParseException, ParserConfigurationException, SAXException, InvalidArgumentException {
        String[] re = ProfileUtils.read("tokens");
        if (!validate(re)) return false;
        HttpPost post = new HttpPost(server + "/");
        JSONObject obj = new JSONObject();
        obj.put("accessToken",re[0]);
        obj.put("clientToken",re[1]);
        StringEntity se = new StringEntity(obj.toJSONString());
        post.setEntity(se);
        post.setHeader("Content-type", "application/json");
        CloseableHttpResponse resp = client.execute(post);
        String body = EntityUtils.toString(resp.getEntity());
        if (resp.getStatusLine().getStatusCode() == 200){
            ProfileUtils.write("auth",parser.parse(body));
            return true;
        }
        return false;
    }

    private boolean validate(String[] re) throws IOException {
        HttpPost post = new HttpPost(server + "/validate");
        JSONObject obj = new JSONObject();
        obj.put("accessToken",re[0]);
        obj.put("clientToken",re[1]);
        StringEntity se = new StringEntity(obj.toJSONString());
        post.setEntity(se);
        post.setHeader("Content-type", "application/json");
        CloseableHttpResponse resp = client.execute(post);
        String body = EntityUtils.toString(resp.getEntity());
        if (resp.getStatusLine().getStatusCode() == 204)return true;
        return false;
    }
}
