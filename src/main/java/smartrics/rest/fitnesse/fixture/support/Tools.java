/*  Copyright 2008 Fabrizio Cannizzo
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
package smartrics.rest.fitnesse.fixture.support;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.copy.HierarchicalStreamCopier;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;

/**
 * Misc tool methods for string/xml/xpath manipulation.
 * 
 * @author smartrics
 * 
 */
public final class Tools {

	private Tools() {

	}

	/**
	 * @param ns
	 *            the name space
	 * @param xpathExpression
	 *            the expression
	 * @param content
	 *            the content
	 * @return the list of nodes matching the supplied XPath.
	 */
	public static NodeList extractXPath(Map<String, String> ns,
			String xpathExpression, String content) {
		return (NodeList) extractXPath(ns, xpathExpression, content,
				XPathConstants.NODESET, null);
	}

	/**
	 * 
	 * @param ns
	 * @param xpathExpression
	 * @param content
	 * @param encoding
	 * @return the list of nodes matching the supplied XPath.
	 */
	public static NodeList extractXPath(Map<String, String> ns,
			String xpathExpression, String content, String encoding) {
		return (NodeList) extractXPath(ns, xpathExpression, content,
				XPathConstants.NODESET, encoding);
	}

	/**
	 * @param xpathExpression
	 * @param content
	 * @param returnType
	 * @return the list of nodes matching the supplied XPath.
	 */
	public static Object extractXPath(String xpathExpression, String content,
			QName returnType) {
		return extractXPath(xpathExpression, content, returnType, null);
	}

	/**
	 * 
	 * @param xpathExpression
	 * @param content
	 * @param returnType
	 * @param encoding
	 * @return the list of nodes mathching the supplied XPath.
	 */
	public static Object extractXPath(String xpathExpression, String content,
			QName returnType, String encoding) {
		// Use the java Xpath API to return a NodeList to the caller so they can
		// iterate through
		return extractXPath(new HashMap<String, String>(), xpathExpression,
				content, returnType, encoding);
	}

	/**
	 * 
	 * @param ns
	 * @param xpathExpression
	 * @param content
	 * @param returnType
	 * @return the list of nodes mathching the supplied XPath.
	 */
	public static Object extractXPath(Map<String, String> ns,
			String xpathExpression, String content, QName returnType) {
		return extractXPath(ns, xpathExpression, content, returnType, null);
	}

	/**
	 * extract the XPath from the content. the return value type is passed in
	 * input using one of the {@link XPathConstants}. See also
	 * {@link XPathExpression#evaluate(Object item, QName returnType)} ;
	 * 
	 * @param ns
	 * @param xpathExpression
	 * @param content
	 * @param returnType
	 * @param charset
	 * @return the result
	 */
	public static Object extractXPath(Map<String, String> ns,
			String xpathExpression, String content, QName returnType,
			String charset) {
		if (null == ns) {
			ns = new HashMap<String, String>();
		}
		String ch = charset;
		if (ch == null) {
			ch = Charset.defaultCharset().name();
		}
		Document doc = toDocument(content, charset);
		XPathExpression expr = toExpression(ns, xpathExpression);
		try {
			Object o = expr.evaluate(doc, returnType);
			return o;
		} catch (XPathExpressionException e) {
			throw new IllegalArgumentException(
					"xPath expression cannot be executed: " + xpathExpression);
		}
	}

	/**
	 * @param result
	 * @return the serialised as xml result of an xpath expression evaluation
	 */
	public static String xPathResultToXmlString(Object result) {
		if (result == null) {
			return null;
		}
		try {
			StringWriter sw = new StringWriter();
			Transformer serializer = TransformerFactory.newInstance()
					.newTransformer();
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			serializer.setOutputProperty(OutputKeys.MEDIA_TYPE, "text/xml");
			if (result instanceof NodeList) {
				serializer.transform(
						new DOMSource(((NodeList) result).item(0)),
						new StreamResult(sw));
			} else if (result instanceof Node) {
				serializer.transform(new DOMSource((Node) result),
						new StreamResult(sw));
			} else {
				return result.toString();
			}
			return sw.toString();
		} catch (Exception e) {
			throw new RuntimeException("Transformation caused an exception", e);
		}
	}

	/**
	 * @param ns
	 * @param xpathExpression
	 * @return true if the expression is valid
	 */
	public static boolean isValidXPath(Map<String, String> ns,
			String xpathExpression) {
		try {
			toExpression(ns, xpathExpression);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	/**
	 * @param ns
	 * @param xpathExpression
	 * @return the parsed string as {@link XPathExpression}
	 */
	public static XPathExpression toExpression(Map<String, String> ns,
			String xpathExpression) {
		try {
			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpath = xpathFactory.newXPath();
			if (ns.size() > 0) {
				xpath.setNamespaceContext(toNsContext(ns));
			}
			XPathExpression expr = xpath.compile(xpathExpression);
			return expr;
		} catch (XPathExpressionException e) {
			throw new IllegalArgumentException(
					"xPath expression can not be compiled: " + xpathExpression,
					e);
		}
	}

	private static NamespaceContext toNsContext(final Map<String, String> ns) {
		NamespaceContext ctx = new NamespaceContext() {

			@Override
			public String getNamespaceURI(String prefix) {
				String u = ns.get(prefix);
				if (null == u) {
					return XMLConstants.NULL_NS_URI;
				}
				return u;
			}

			@Override
			public String getPrefix(String namespaceURI) {
				for (String k : ns.keySet()) {
					if (ns.get(k).equals(namespaceURI)) {
						return k;
					}
				}
				return null;
			}

			@Override
			public Iterator<?> getPrefixes(String namespaceURI) {
				return null;
			}

		};
		return ctx;
	}

	private static Document toDocument(String content, String charset) {
		String ch = charset;
		if (ch == null) {
			ch = Charset.defaultCharset().name();
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(getInputStreamFromString(content, ch));
			return doc;
		} catch (ParserConfigurationException e) {
			throw new IllegalArgumentException(
					"parser for last response body caused an error", e);
		} catch (SAXException e) {
			throw new IllegalArgumentException(
					"last response body cannot be parsed", e);
		} catch (IOException e) {
			throw new IllegalArgumentException(
					"IO Exception when reading the document", e);
		}
	}

	/**
	 * this method uses @link {@link JSONObject} to parse the string and return
	 * true if parse succeeds.
	 * 
	 * @param presumeblyJson
	 *            string with some json (possibly).
	 * @return true if json is valid
	 */
	public static boolean isValidJson(String presumeblyJson) {
		Object o = null;
		try {
			o = new JSONObject(presumeblyJson);
		} catch (JSONException e) {
			return false;
		}
		return o != null;
	}

	/**
	 * @param json
	 *            the json string
	 * @return the string as xml.
	 */
	public static String fromJSONtoXML(String json) {
		HierarchicalStreamDriver driver = new JettisonMappedXmlDriver();
		StringReader reader = new StringReader(json);
		HierarchicalStreamReader hsr = driver.createReader(reader);
		StringWriter writer = new StringWriter();
		try {
			new HierarchicalStreamCopier().copy(hsr, new PrettyPrintWriter(
					writer));
			return writer.toString();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	/**
	 * Yet another stream 2 string function.
	 * 
	 * @param is
	 *            the stream
	 * @return the string.
	 */
	public static String getStringFromInputStream(InputStream is) {
		return getStringFromInputStream(is, Charset.defaultCharset().name());
	}

	/**
	 * Yet another stream 2 string function.
	 * 
	 * @param is
	 *            the stream
	 * @param encoding
	 *            the encoding of the bytes in the stream
	 * @return the string.
	 */
	public static String getStringFromInputStream(InputStream is,
			String encoding) {
		String line = null;
		if (is == null) {
			return "";
		}
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(is, encoding));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Unsupported encoding: "
					+ encoding, e);
		}
		StringBuilder sb = new StringBuilder();
		try {
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("Unable to read from stream", e);
		}
		return sb.toString();
	}

	/**
	 * Yet another stream 2 string function.
	 * 
	 * @param string
	 *            the string
	 * @param encoding
	 *            the encoding of the bytes in the stream
	 * @return the input stream.
	 */
	public static InputStream getInputStreamFromString(String string,
			String encoding) {
		if (string == null) {
			throw new IllegalArgumentException("null input");
		}
		try {
			byte[] byteArray = string.getBytes(encoding);
			return new ByteArrayInputStream(byteArray);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Unsupported encoding: "
					+ encoding);
		}
	}

	/**
	 * converts a map to string
	 * 
	 * @param map
	 *            the map to convert
	 * @param nvSep
	 *            the nvp separator
	 * @param entrySep
	 *            the separator of each entry
	 * @return the serialised map.
	 */
	public static String convertMapToString(Map<String, String> map,
			String nvSep, String entrySep) {
		StringBuffer sb = new StringBuffer();
		if (map != null) {
			for (Entry<String, String> entry : map.entrySet()) {
				String el = entry.getKey();
				sb.append(convertEntryToString(el, map.get(el), nvSep)).append(
						entrySep);
			}
		}
		String repr = sb.toString();
		int pos = repr.lastIndexOf(entrySep);
		return repr.substring(0, pos);
	}

	/**
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 * @param nvSep
	 *            the separator
	 * @return the kvp as a string <code>&lt;name>&lt;sep>&lt;value></code>.
	 */
	public static String convertEntryToString(String name, String value, String nvSep) {
		return String.format("%s%s%s", name, nvSep, value);
	}

	/**
	 * @param text the text
	 * @param expr the regex
	 * @return true if regex matches text.
	 */
	public static boolean regex(String text, String expr) {
		try {
			Pattern p = Pattern.compile(expr);
			boolean find = p.matcher(text).find();
			return find;
		} catch (PatternSyntaxException e) {
			throw new IllegalArgumentException("Invalid regex " + expr);
		}
	}

	/**
	 * parses a map from a string.
	 * 
	 * @param expStr
	 *            the string with the serialised map.
	 * @param nvSep
	 *            the separator for keys and values.
	 * @param entrySep
	 *            the separator for entries in the map.
	 * @param cleanTags
	 *            if true the value is cleaned from any present html tag.
	 * @return the parsed map.
	 */
	public static Map<String, String> convertStringToMap(final String expStr,
			final String nvSep, final String entrySep, boolean cleanTags) {
		String sanitisedExpStr = expStr.trim();
		sanitisedExpStr = removeOpenEscape(sanitisedExpStr);
		sanitisedExpStr = removeCloseEscape(sanitisedExpStr);
		sanitisedExpStr = sanitisedExpStr.trim();
		String[] nvpArray = sanitisedExpStr.split(entrySep);
		Map<String, String> ret = new HashMap<String, String>();
		for (String nvp : nvpArray) {
			try {
				nvp = nvp.trim();
				if ("".equals(nvp)) {
					continue;
				}
				nvp = removeOpenEscape(nvp).trim();
				nvp = removeCloseEscape(nvp).trim();
				String[] nvpArr = nvp.split(nvSep);
				String k, v;
				k = nvpArr[0].trim();
				v = "";
				if (nvpArr.length == 2) {
					v = nvpArr[1].trim();
				} else if (nvpArr.length > 2) {
					int pos = nvp.indexOf(nvSep) + nvSep.length();
					v = nvp.substring(pos).trim();
				}
				if (cleanTags) {
					ret.put(k, fromSimpleTag(v));
				} else {
					ret.put(k, v);
				}
			} catch (RuntimeException e) {
				throw new IllegalArgumentException(
						"Each entry in the must be separated by '"
								+ entrySep
								+ "' and each entry must be expressed as a name"
								+ nvSep + "value");
			}
		}
		return ret;
	}

	/**
	 * @param message
	 *            the message to be included in the collapsable section header.
	 * @param content
	 *            the content collapsed.
	 * @return a string with the html/js code to implement a collapsable section
	 *         in fitnesse.
	 */
	public static String makeToggleCollapseable(boolean printAsHtml, String message, String content) {
		if(!printAsHtml) {
			return content;
		}
		Random random = new Random();
		String id = Integer.toString(content.hashCode())
				+ Long.toString(random.nextLong());
		StringBuffer sb = new StringBuffer();
		sb.append("<a href=\"javascript:toggleCollapsable('" + id + "');\">");
		sb.append("<img src='/files/images/collapsableClosed.gif' class='left' id='img"
				+ id + "'/>" + message + "</a>");
		sb.append("<div class='hidden' id='" + id + "'>").append(content)
				.append("</div>");
		return sb.toString();
	}

	/**
	 * Substitutions:
	 * <table border="1">
	 * <tr><td><code>&lt;pre></code> and <code>&lt;/pre></code></td><td><code>""</code></td></tr>
	 * <tr><td><code>&lt;</code></td><td><code>&amp;lt;</code></td></tr>
	 * <tr><td><code>\n</code></td><td><code>&lt;br /></code></td></tr>
	 * <tr><td><code>&nbsp;</code> <i>(space)</i></td><td><code>&amp;nbsp;</code></td></tr>
	 * <tr><td><code>-----</code> <i>(5 hyphens)</i></td><td><code>&lt;hr /></code></td></tr>
	 * </table>
	 * 
	 * @param printAsHtml if true, it won't escape to html - set to true for fitnesse versions greater than 20130513
	 * @param text
	 *            some text.
	 * @return the html.
	 */
	public static String toHtml(boolean printAsHtml, String text) {
		if(printAsHtml) {
			return text.replaceAll("<pre>", "").replaceAll("</pre>", "")
					.replaceAll("<", "&lt;").replaceAll(">", "&gt;")
					.replaceAll("\n", "<br/>").replaceAll("\t", "    ")
					.replaceAll(" ", "&nbsp;").replaceAll("-----", "<hr/>");
		} else {
			return text;
		}
	}

	/**
	 * @param c
	 *            some text
	 * @return the text within <code>&lt;code></code> tags.
	 */
	public static String toCode(String c) {
		return "<code>" + c + "</code>";
	}

	/**
	 * @param somethingWithinATag
	 *            some text enclosed in some html tag.
	 * @return the text within the tag.
	 */ 
	public static String fromSimpleTag(String somethingWithinATag) {
		return somethingWithinATag.replaceAll("<[^>]+>", "").replace(
				"</[^>]+>", "");
	}

	/**
	 * @param text some html
	 * @return the text stripped out of all tags.
	 * 
	 */
	public static String fromHtml(String text) {
		String ls = "\n";
		return text.replaceAll("<br[\\s]*/>", ls).replaceAll("<BR[\\s]*/>", ls)
				.replaceAll("<span[^>]*>", "").replaceAll("</span>", "")
				.replaceAll("<pre>", "").replaceAll("</pre>", "")
				.replaceAll("&nbsp;", " ").replaceAll("&gt;", ">")
				.replaceAll("&amp;", "&").replaceAll("&lt;", "<")
				.replaceAll("&nbsp;", " ");
	}

	/**
	 * @param string a string
	 * @return the string htmlified as a fitnesse label.
	 */
	public static String toHtmlLabel(boolean printAsHtml, String string) {
		if(printAsHtml) {
			return "<i><span class='fit_label'>" + string + "</span></i>";
		} else {
			return string;
		}
	}

	/**
	 * @param printAsHtml if true prints the string as html (for compatibility with fitnesse v ? 2o13)
	 * @param href
	 *            a string ending up in the anchor href.
	 * @param text
	 *            a string within anchors.
	 * @return the string htmlified as a html link.
	 */
	public static String toHtmlLink(boolean printAsHtml, String href, String text) {
		if(printAsHtml) {
			return "<a href='" + href + "'>" + text + "</a>";
		} else {
			return text + " (see: " + href + ")";
		}
	}

	/**
	 * @param expected
	 *            the expected value
	 * @param typeAdapter
	 *            the body adapter for the cell
	 * @param formatter
	 *            the formatter
	 * @param minLenForToggle
	 *            the value determining whether the content should be rendered
	 *            as a collapseable section.
	 * @return the formatted content for a cell with a wrong expectation
	 */
	public static String makeContentForWrongCell(boolean printAsHtml, String expected,
			RestDataTypeAdapter typeAdapter, CellFormatter<?> formatter,
			int minLenForToggle) {
		StringBuffer sb = new StringBuffer();
		sb.append(Tools.toHtml(printAsHtml, expected));
		if (formatter.isDisplayActual()) {
			sb.append(toHtml(printAsHtml, "\n"));
			if(!printAsHtml) {
				sb.append("[");
			}
			sb.append(formatter.label("expected"));
			if(!printAsHtml) {
				sb.append("]\n");
			}
			String actual = typeAdapter.toString();
			sb.append(toHtml(printAsHtml, "-----"));
			if(!printAsHtml) {
				sb.append("\n");
			}
			sb.append(toHtml(printAsHtml, "\n"));
			if (minLenForToggle >= 0 && actual.length() > minLenForToggle) {
				sb.append(makeToggleCollapseable(printAsHtml, "toggle actual",
						toHtml(printAsHtml, actual)));
			} else {
				sb.append(toHtml(printAsHtml, actual));
			}
			sb.append(toHtml(printAsHtml, "\n"));
			if(!printAsHtml) {
				sb.append("\n[");
			}
			sb.append(formatter.label("actual"));
			if(!printAsHtml) {
				sb.append("]\n");
			}
		}
		List<String> errors = typeAdapter.getErrors();
		if (errors.size() > 0) {
			if(!printAsHtml) {
				sb.append("\n\n");
			}
			sb.append(toHtml(printAsHtml, "-----"));
			sb.append(toHtml(printAsHtml, "\n"));
			for (String e : errors) {
				sb.append(toHtml(printAsHtml, e + "\n"));
			}
			sb.append(toHtml(printAsHtml, "\n"));
			if(!printAsHtml) {
				sb.append(toHtml(printAsHtml, "["));
			}
			sb.append(formatter.label("errors"));
			if(!printAsHtml) {
				sb.append(toHtml(printAsHtml, "]\n"));
			}
		}
		return sb.toString();
	}

	/**
	 * @param expected the expected value
	 * @param typeAdapter the body type adaptor
	 * @param formatter the formatter
	 *            the value determining whether the content should be rendered
	 *            as a collapseable section.
	 * @param minLenForToggle 
	 *            the value determining whether the content should be rendered
	 *            as a collapseable section.
	 * @return the formatted content for a cell with a right expectation
	 */
	public static String makeContentForRightCell(boolean printAsHtml, String expected,
			RestDataTypeAdapter typeAdapter, CellFormatter<?> formatter,
			int minLenForToggle) {
		StringBuffer sb = new StringBuffer();
		sb.append(toHtml(printAsHtml, expected));
		String actual = typeAdapter.toString();
        if (formatter.isDisplayActual() && !expected.equals(actual)) {
            //sb.append(toHtml("\n"));
			sb.append(toHtml(printAsHtml, "\n"));
			if(!printAsHtml) {
				sb.append(toHtml(printAsHtml, "["));
			}
			sb.append(formatter.label("expected"));
			if(!printAsHtml) {
				sb.append(toHtml(printAsHtml, "]\n\n"));
			}
			sb.append(toHtml(printAsHtml, "-----"));
			sb.append(toHtml(printAsHtml, "\n"));
			if (minLenForToggle >= 0 && actual.length() > minLenForToggle) {
				sb.append(makeToggleCollapseable(printAsHtml, "toggle actual",
						toHtml(printAsHtml, actual)));
			} else {
				sb.append(toHtml(printAsHtml, actual));
			}
			sb.append(toHtml(printAsHtml, "\n"));
			if(!printAsHtml) {
				sb.append(toHtml(printAsHtml, "["));
			}
			sb.append(formatter.label("actual"));
			if(!printAsHtml) {
				sb.append(toHtml(printAsHtml, "]"));
			}
		}
		return sb.toString();
	}

	private static String removeCloseEscape(String str) {
		return trimStartEnd("-!", str);
	}

	private static String removeOpenEscape(String str) {
		return trimStartEnd("!-", str);
	}

	private static String trimStartEnd(String pattern, String str) {
		if (str.startsWith(pattern)) {
			str = str.substring(2);
		}
		if (str.endsWith(pattern)) {
			str = str.substring(0, str.length() - 2);
		}
		return str;
	}

  public static String wrapInDiv(String body) {
    return String.format("<div>%s</div>", body);
  }

}
