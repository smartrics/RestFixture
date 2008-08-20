package smartrics.rest.fitnesse.fixture.support;

import java.net.MalformedURLException;
import java.net.URL;

public class Url {

	private URL baseUrl;

	public Url(String url) {
		try {
			if (url == null || "".equals(url.trim())) {
				throw new IllegalArgumentException("Null or empty input: " + url);
			}
			String u = url;
			if(url.endsWith("/")){
				u = url.substring(0, u.length() - 1);
			}
			baseUrl = new URL(u);
			if ("".equals(baseUrl.getHost())) {
				throw new IllegalArgumentException("No host specified in base URL: " + url);
			}
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Malformed base URL: " + url, e);
		}
	}

	public String getBaseUrl() {
		return baseUrl.toExternalForm();
	}

	public String toString() {
		return getBaseUrl();
	}

	public URL buildURL(String file) {
		try {
			return new URL(baseUrl, file);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Invalid URL part: " + file);
		}
	}

}
