package atmatm6.proxylauncher.utils;

import com.sun.istack.internal.Nullable;
import org.apache.http.auth.InvalidCredentialsException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class LoginUtils {
    private CloseableHttpClient client;
    private String email;
    private String password;
    private JSONParser parser;

    // Simple initialization
    public LoginUtils(){
        client = HttpClients.createDefault();
        parser = new JSONParser();
    }

    // This looks shady af but if you're here then you're the shady one ;)
    // You can null each detail if you've already inputted it and changed it between now and before
    public void setLoginDetails(@Nullable String email, @Nullable String password){
        if (email != null) this.email = email;
        if (password != null) this.password = password;
    }

    // Returns true if successful
    public boolean authenticate() throws InvalidCredentialsException, IOException, ParseException, ParserConfigurationException, SAXException {
        if (email == null || password == null) throw new InvalidCredentialsException();
        HttpPost post = new HttpPost("https://authserver.mojang.com/authenticate");
        String json = "{\"agent\": {" +
                "\"name\": \"Minecraft\"," +
                "\"version\": 1" +
                "}," +
                "\"username\":\""+email+"\"," +
                "\"password\":\""+password+"\"}";
        StringEntity entity = new StringEntity(json);
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

    public boolean valid() throws NotImplementedException{
        ProfileUtils.read("");
        return false;
    }
    public boolean refresh() throws NotImplementedException{
        throw new NotImplementedException();
    }
}
