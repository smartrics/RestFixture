package smartrics.rest.fitnesse.fixture.support;

import static org.junit.Assert.*;

import java.net.URL;

import org.junit.Test;

public class UrlTest {
	@Test(expected=IllegalArgumentException.class)
	public void mustRejectEmptyStringUriRepresentations() {
		new Url("");
	}
	@Test(expected=IllegalArgumentException.class)
	public void mustRejectNullStringUriRepresentations() {
		new Url(null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void mustRejectMalformedUrls(){
		new Url("aaa");
	}

	@Test(expected=IllegalArgumentException.class)
	public void mustRejectUrlsWithNoHost(){
		new Url("http:///a");
	}

	@Test
	public void mustAcceptWellFormedUrl(){
		String url = "http://a.com";
		Url u = new Url(url);
		assertEquals(url, u.toString());
	}

	@Test
	public void tailingSlashOnAUrlIsOptional(){
		Url u1 = new Url("http://a.com/");
		Url u2 = new Url("http://a.com");
		assertEquals(u1.toString(), u2.toString());
	}

	@Test
	public void mustBuildUrlTailingPathToBaseUrl(){
		Url u = new Url("http://a.com");
		URL fullUrl1 = u.buildURL("/path");
		URL fullUrl2 = u.buildURL("path");
		assertEquals("http://a.com/path", fullUrl1.toExternalForm());
		assertEquals("http://a.com/path", fullUrl2.toExternalForm());
	}

	@Test(expected=IllegalArgumentException.class)
	public void mustRejectNullPaths(){
		Url u = new Url("http://a.com");
		u.buildURL(null);
	}
}
