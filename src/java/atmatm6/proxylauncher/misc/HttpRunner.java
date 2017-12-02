package atmatm6.proxylauncher.misc;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.concurrent.*;

public class HttpRunner {
    private static ExecutorService es;
    private static CloseableHttpClient client;
    public static void setup(){
        es = Executors.newFixedThreadPool(15);
        client = HttpClients.createDefault();
    }
    public static CloseableHttpResponse post(String url, String payload) throws Exception {
        Future<CloseableHttpResponse> task = es.submit(() -> {
            System.out.println("Thread" + new Thread().currentThread().getId() + "is performing a post request at \n"+url);
            try {
                HttpPost post = new HttpPost(url);
                StringEntity se = new StringEntity(payload);
                post.setEntity(se);
                post.setHeader("Accept", "application/json");
                post.setHeader("Content-type", "application/json");
                return client.execute(post);
            } catch (Exception ignored){
                return null;
            }
        });
        return task.get();
    }

    public static CloseableHttpResponse get(String url) throws Exception {
        Future<CloseableHttpResponse> task = es.submit(() -> {
            System.out.println("Thread " + new Thread().currentThread().getName() + " is performing a get request at:\n\t"+url);
            try {
                HttpGet get = new HttpGet(url);
                get.setHeader("Accept", "application/json");
                return client.execute(get);
            } catch (Exception ignored) {
                return null;
            }
        });
        return task.get();
    }
    /*in case i need it, it's there
    * public static void setup(int poolCount){
    *     es = Executors.newFixedThreadPool(poolCount);
    * }
    */
}
