package smartrics.rest.fitnesse.fixture.support;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FitNesseVersionChecker {

	private static Logger LOG = LoggerFactory.getLogger(FitNesseVersionChecker.class);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean isPre2013() {
		try {
			// fitnesse.FitNesseVersion k; isAtLeast(String requiredVersion);
			Class fitNesseVerClass = Class.forName("fitnesse.FitNesseVersion");
			Object fv = fitNesseVerClass.newInstance();
			Method m = fitNesseVerClass.getMethod("isAtLeast", String.class);
			Object res = m.invoke(fv, "20130501");
			if(res != null) {
				boolean is2013Ver = Boolean.parseBoolean(res.toString());
				return !is2013Ver;
			}
		} catch(Exception e) {
			LOG.warn("Unable to determine fitnesse version. assuming pre 2013", e);
		}
		return true;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String version() {
		try {
			Class fitNesseVerClass = Class.forName("fitnesse.FitNesseVersion");
			Object fv = fitNesseVerClass.newInstance();
			Method m = fitNesseVerClass.getMethod("toString");
			Object res = m.invoke(fv);
			if(res != null) {
				return res.toString();
			}
		} catch(Exception e) {
			LOG.warn("Unable to determine fitnesse version. assuming pre 2013", e);
		}
		return "unknown";
	}
}
