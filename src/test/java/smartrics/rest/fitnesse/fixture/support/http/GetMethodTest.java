package smartrics.rest.fitnesse.fixture.support.http;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.apache.commons.httpclient.HttpURL;
import org.junit.Test;

public class GetMethodTest {
    @Test
    public void buildsUriThatAllowSquareBracketsInQueryString() throws Exception {
        GetMethod m = new GetMethod();
        m.setURI(new HttpURL("http://localhost:8989/resources?attr[data]=blob"));
        assertThat(m.getURI(), is(instanceOf(HttpURL.class)));
        assertThat(m.getURI().getQuery(), is(equalTo("attr[data]=blob")));
    }
}
