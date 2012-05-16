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
package smartrics.rest.fitnesse.fixture;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import util.Maybe;
import fitnesse.html.HtmlTag;
import fitnesse.wikitext.parser.Matcher;
import fitnesse.wikitext.parser.Parser;
import fitnesse.wikitext.parser.Rule;
import fitnesse.wikitext.parser.Symbol;
import fitnesse.wikitext.parser.SymbolProvider;
import fitnesse.wikitext.parser.SymbolType;
import fitnesse.wikitext.parser.Translation;
import fitnesse.wikitext.parser.Translator;

/**
 * Allows inclusion of an SVG file in a FitNesse page. For the file to be
 * reachable over HTTP in FitNesse, it needs to be store under (a directory in)
 * <code><i>FitNesseRoot</i>/files</code>.
 * 
 * For the symbol to be deployed, put the RestFixture.jar in the FitNesse start
 * command (eg:
 * <code>java -classpath <i>path_to</i>/RestFixture.jar -jar fitnesse.jar</code>
 * ) and add to the plugins.properties file the following property:
 * <code>SymbolTypes=smartrics.rest.fitnesse.fixture.SvgImage</code>. See <a
 * href="link to blog where fitnesse symbols are explained">this</a> for more
 * details.
 * 
 * To use the symbol, is sufficient to specify in a line in the test page the
 * following:
 * 
 * <code>!svg <i>/files/path/to/image.svg rendering_mode</i></code>
 * 
 * The <code><i>rendering_mode</i></code> is one of the following:
 * 
 * <table border="1">
 * <tr>
 * <td>inline</td>
 * <td>the image is included as is, hence using the &lt;svg> tag</td>
 * </tr>
 * <tr>
 * <td>embed</td>
 * <td>the image is rendered using the &lt;embed> tag</td>
 * </tr>
 * <tr>
 * <td>object</td>
 * <td>the image is rendered using the &lt;object> tag</td>
 * </tr>
 * <tr>
 * <td>img</td>
 * <td>the image is rendered using the &lt;img> tag</td>
 * </tr>
 * <tr>
 * <td>iframe</td>
 * <td>the image is rendered using the &lt;iframe> tag</td>
 * </tr>
 * <tr>
 * <td>anchor</td>
 * <td>the image is rendered using the &lt;a> tag</td>
 * </tr>
 * </table>
 * Each mode (except <code>inline</code>) will point to the file in the fitnesse
 * server.
 * 
 * Basically this means that when embed is used, the conent of the SVG file is
 * read at that point in time and substituted as the page is rendering. With the
 * others, an HTML tag is rendered that has a src that points to the remote
 * file, hence the image is rendered when the browser decides to.
 * 
 * Also note that <b>rendering SVG files embedded in HTML pages is one of the
 * least supported features across all browsers. So the end result of embedding
 * an SVG is highly dependant on the browser and unluckely to be portable.</b>
 * 
 * @author fabrizio
 * 
 */
public class SvgImage extends SymbolType implements Rule, Translation {

    static final Log LOG = LogFactory.getLog(SvgImage.class);

    /**
     * The selected rendering mode.
     */
    private enum Mode {
        inline, embed("embed", "src", "type=\"image/svg+xml\""), object("object", "data", "type=\"image/svg+xml\""), iframe("iframe", "src"), img("img", "src"), anchor("a", "href");

        public String toString(String stuff) {
            if (this.equals(inline)) {
                return stuff;
            }
            String s = "";
            if (otherAttr != null) {
                s = otherAttr;
            }
            return "<" + tag + " " + srcAttr + "=\"" + stuff + "\" " + s + " />";
        }

        private Mode() {
            this(null, null, null);
        }

        private Mode(String tag, String srcAttr) {
            this(tag, srcAttr, null);
        }

        private Mode(String tag, String srcAttr, String otherAttr) {
            this.tag = tag;
            this.srcAttr = srcAttr;
            this.otherAttr = otherAttr;
        }

        private String tag;
        private String srcAttr;
        private String otherAttr;
    }

    private Mode defaultMode = Mode.inline;

    public SvgImage() {
        super("SVG-Image");
        wikiMatcher(new Matcher().string("!svg"));
        htmlTranslation(this);
        wikiRule(this);
    }

	public Maybe<Symbol> parse(Symbol current, Parser parser) {
		Symbol targetList = parser.parseToEnds(-1,
				SymbolProvider.pathRuleProvider,
				new SymbolType[] { SymbolType.Newline });
		return new Maybe<Symbol>(current.add(targetList));
	}

    public String toTarget(Translator translator, Symbol symbol) {
        String symContent = symbol.getContent();
        String target = symContent + translator.translate(symbol.childAt(0));
        return toTarget(translator, target, symbol);
    }

    public String toTarget(Translator translator, String body, Symbol args) {
        Symbol symbol = getPathSymbol(args);
        if (symbol == null) {
            return error("Missing image path");
        }
        String line = translator.translate(symbol);
        line = line.replaceAll("\\s+", " ").trim();
        String[] parts = line.split(" ");
        String location = null;
        Mode mode = defaultMode;
        if (parts.length > 0) {
            location = parts[0];
        }
        if (parts.length > 1) {
            for (String part : parts) {
                if (part.contains("mode=")) {
                    mode = parseMode(part);
                }
            }
        }
        return inlineSvg(mode, location);
    }

    private Mode parseMode(String s) {
        String mString = null;
        Mode mode = defaultMode;
        try {
            mString = s.trim();
            String[] subParts = mString.split("=");
            mode = Enum.valueOf(Mode.class, subParts[1].trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Mode not supported: " + mString);
        }
        return mode;
    }

    private Symbol getPathSymbol(Symbol args) {
        if (args.getChildren().size() > 0) {
            return args.childAt(0);
        }
        return null;
    }

    private String inlineSvg(Mode tag, String location) {
        if (location == null) {
            return error("Invalid file path: null");
        }
        if (tag.equals(Mode.inline)) {
            String content = read(location);
            return tag.toString(content);
        } else {
            return tag.toString(location);
        }
    }


    private String read(String path) {
        String loc = path.trim();
        if (loc.startsWith("/files")) {
            loc = "FitNesseRoot" + path.trim();
        }
        String content = null;
        File f = new File(loc);
        try {
            if (f.exists()) {
                content = readFile(f);
            } else {
                content = error("File not found: " + f.getAbsolutePath() + ", path=" + path);
            }
        } catch (FileNotFoundException e) {
            content = error("File not found: " + loc + ", path=" + path + ", err=" + e.getMessage());
        } catch (RuntimeException e) {
            content = error("Unable to read: " + loc + ", path=" + path);
        } 
        return content;
    }

    private String readFile(File f) throws FileNotFoundException {
    	FileReader reader = new FileReader(f);
        BufferedReader r = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        String line;
        LOG.debug("Reading file " + f.getAbsolutePath());
        try {
            while ((line = r.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to read from stream", e);
        } finally {
        	try {
				r.close();
			} catch (IOException e) {
		        LOG.debug("Exception closing file reader for file " + f.getAbsolutePath());
			}
        }
        return sb.toString();
    }

    private String error(String string) {
        HtmlTag tag = new HtmlTag("p");
        tag.addAttribute("style", "color:red");
        tag.add(string);
        return tag.htmlInline();
    }

}