package smartrics.rest.fitnesse.fixture.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.w3c.dom.NodeList;

import fit.Parse;

public class StringTypeAdapter extends RestDataTypeAdapter {

	public StringTypeAdapter() {
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object expected, Object actual) {
		String se = "null";
		if(expected!=null)
			se = expected.toString();
		String sa = "null";
		if(actual!=null)
			sa = actual.toString();
		return se.equals(sa);
	}

	@Override
	public Object parse(String s)
	{
		if("null".equals(s))
			return null;
		if("blank".equals(s))
			return "";
		return s;
	}

	@Override
	public String toString(Object obj) {
		if(obj==null)
			return "null";
		if("".equals(obj.toString().trim()))
			return "blank";
		return obj.toString();

	}
}
