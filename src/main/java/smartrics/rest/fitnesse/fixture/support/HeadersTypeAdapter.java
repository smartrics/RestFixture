package smartrics.rest.fitnesse.fixture.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import smartrics.rest.client.RestData.Header;

public class HeadersTypeAdapter extends RestDataTypeAdapter {

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object expectedObj, Object actualObj) {
		if (expectedObj == null || actualObj == null)
			return false;
		// r1 and r2 are Map<String, String> containing either the header
		// from the HTTP response or the data value in the expected cell
		// equals checks for r1 being a subset of r2
		Collection<Header> expected = (Collection<Header>) expectedObj;
		Collection<Header> actual = (Collection<Header>) actualObj;
		for (Header k : expected) {
			String eValue = k.getValue();
			Header aHdr = find(actual, k);
			if (aHdr == null) {
				addError("not found: [" + k + ":" + eValue.trim()+"]");
			}
		}
		return getErrors().size() == 0;
	}

	private Header find(Collection<Header> actual, Header k) {
		for(Header h : actual){
			if(h.getName().equals(k.getName()) && Tools.regex(h.getValue(), k.getValue()))
				return h;
		}
		return null;
	}

	@Override
	public Object parse(String s) throws Exception {
		// parses a cell content as a map of headers.
		// syntax is name:value\n*
		List<Header> expected = new ArrayList<Header>();
		if (!"".equals(s.trim())) {
			String expStr = Tools.fromHtml(s.trim());
			String[] nvpArray = expStr.split(System.getProperty("line.separator"));
			for(String nvp : nvpArray){
				try{
					String[] nvpEl = nvp.split(":");
					expected.add(new Header(nvpEl[0].trim(), nvpEl[1].trim()));
				} catch(RuntimeException e){
					throw new IllegalArgumentException("Each entry in the must be separated by \\n and each entry must be expressed as a name:value");
				}
			}
		}
		return expected;
	}

	@Override
	@SuppressWarnings("unchecked")
	public String toString(Object obj) {
		StringBuffer b = new StringBuffer();
		List<Header> list = (List<Header>) obj;
		for(Header h : list){
			b.append(h.getName()).append(" : ").append(h.getValue()).append(System.getProperty("line.separator"));
		}
		return Tools.toHtml(b.toString().trim());
	}

}
