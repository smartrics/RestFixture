package smartrics.rest.test.fitnesse.drivers;

import org.apache.commons.httpclient.HttpClient;

import smartrics.rest.client.RestClient;
import smartrics.rest.client.RestClientImpl;
import smartrics.rest.client.RestRequest;
import smartrics.rest.client.RestRequest.Method;
import smartrics.rest.client.RestResponse;

public class ClientDriver {

    public static void main(String[] args) {
        postForm(args);
    }

    public static void postForm(String[] args) {
        RestClient c = new RestClientImpl(new HttpClient());
        RestRequest req = new RestRequest();
        req.setBody("name=n&data=d1");
        req.setResource("/resources/");
        req.setMethod(Method.Post);
        req.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        RestResponse res = c.execute("http://localhost:8765", req);
        System.out.println("=======>\n" + res + "\n<=======");
    }

    public static void postXml(String[] args) {
        RestClient c = new RestClientImpl(new HttpClient());
        RestRequest req = new RestRequest();

        req.setBody("<resource><name>n</name><data>d1</data></resource>");
        req.setResource("/resources/");
        req.setMethod(Method.Post);
        RestResponse res = c.execute("http://localhost:8765", req);
        System.out.println("=======>\n" + res + "\n<=======");

        String loc = res.getHeader("Location").get(0).getValue();
        req.setResource(loc + ".json");
        req.setMethod(Method.Get);
        res = c.execute("http://localhost:8765", req);
        System.out.println("=======>\n" + res + "\n<=======");

        req.setMethod(Method.Put);
        req.setBody("<resource><name>another name</name><data>another data</data></resource>");
        res = c.execute("http://localhost:8765", req);
        System.out.println("=======>\n" + res + "\n<=======");

        req.setResource("/resources/");
        req.setMethod(Method.Get);
        res = c.execute("http://localhost:8765", req);
        System.out.println("=======>\n" + res + "\n<=======");

        req.setMethod(Method.Delete);
        req.setResource(loc);
        res = c.execute("http://localhost:8765", req);
        System.out.println("=======>\n" + res + "\n<=======");
    }

}
