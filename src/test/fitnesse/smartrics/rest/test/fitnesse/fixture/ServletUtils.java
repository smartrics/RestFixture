package smartrics.rest.test.fitnesse.fixture;

import java.io.IOException;
import java.io.InputStream;

public final class ServletUtils {

    private ServletUtils() {

    }

    public static String sanitiseUri(String rUri) {
        String uri = rUri;
        if (uri.endsWith("/")) {
            uri = uri.substring(0, uri.length() - 1);
        }
        return uri;
    }

    public static String getContent(InputStream is) throws IOException {
        StringBuffer sBuff = new StringBuffer();
        int c;
        while ((c = is.read()) != -1) {
            sBuff.append((char) c);
        }
        String content = sBuff.toString();
        return content;
    }

}
