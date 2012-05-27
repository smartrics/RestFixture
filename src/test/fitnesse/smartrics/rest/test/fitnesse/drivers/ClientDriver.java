/*  Copyright 2012 Fabrizio Cannizzo
 *
 *  This file is part of RestFixture.
 *
 *  RestFixture (http://code.google.com/p/rest-fixture/) is free software:
 *  you can redistribute it and/or modify it under the terms of the
 *  GNU Lesser General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  RestFixture is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with RestFixture.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  If you want to contact the author please leave a comment here
 *  http://smartrics.blogspot.com/2008/08/get-fitnesse-with-some-rest.html
 */
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
