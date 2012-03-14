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

import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import smartrics.rest.client.RestData.Header;
import smartrics.rest.fitnesse.fixture.RestFixtureWithSeq.Model;
import smartrics.rest.fitnesse.fixture.support.CellWrapper;
import smartrics.rest.fitnesse.fixture.support.Tools;

import com.patternity.graphic.behavioral.Agent;
import com.patternity.graphic.behavioral.Message;
import com.patternity.graphic.behavioral.Note;
import com.patternity.graphic.dag.Node;
import com.patternity.graphic.layout.sequence.SequenceLayout;
import com.patternity.util.TemplatedWriter;

import fit.Counts;
import fit.FixtureListener;
import fit.Parse;
import fit.exception.FitFailureException;
import fitnesse.components.Base64;

/**
 * An extension of RestFixture that generates a sequence diagrams for a table
 * fixture. Sequence diagrams are generated as SVG files using <a
 * href="">Patternity Graphic</a>. <br/>
 * Each picture can then be transcoded into either PNG or JPG format (via <a
 * href="">Batik transcoder API</a>). The format is inferred by the file
 * extension. <br/>
 * The fixture supports a configuration property.
 * <table border="1">
 * <tr>
 * <td>restfixture.graphs.dir</td>
 * <td>destination directory where the images with sequence diagrams will be
 * created. The directory will be created if not existent; the fixture will fail
 * if the directory can't be created</td>
 * </tr>
 * </table>
 * <br/>
 * If the directory specified by restfixture.graphs.dir is created under
 * <code><i>FitNesseRoot</i>/files</code> the generated images can be embedded
 * in the FitNesse pages. <br/>
 * Including images can be achieved via <code>!img</code> (for PNG or JPG) or
 * via a specific FitNesse symbol, <code>!svg</code>, for native svg files
 * {@see smartrics.rest.fitnesse.fixture.SvgImage} <br/>
 * <b>NOTE</b>: This class only works with Fit runner (not Slim) <br/>
 * Using the fixture is straightforward. Like the RestFixture, the hostname
 * needs to be specified. Additionally a new cell needs to be supplied with some
 * data pertaining the creation of the image file. <br/>
 * <table border="1">
 * <tr>
 * <td>RestFixtureWithSeq</td>
 * <td>hostname</td>
 * <td>image data</td>
 * </tr>
 * </table>
 * <br/>
 * Image data is a string containing path to the image file, relative to the
 * value of the <code>restfixture.graphs.dir</code> directory. <br/>
 * The string is followed by a list of attributes passed to the SVG generator
 * for inclusion in the SVG file. for example
 * 
 * <table border="1">
 * <tr>
 * <td>RestFixtureWithSeq</td>
 * <td>hostname</td>
 * <td>post_images/a_post_image.svg viewBox="0 0 200 200" width="100"
 * height="150"</td>
 * </tr>
 * </table>
 * <br/>
 * If the file path contains spaces, it must be included in double quotes. Each
 * attribute value must be included in double quotes.
 * 
 * @author fabrizio
 * 
 */
public class RestFixtureWithSeq extends RestFixture {

    public interface Model {

        void delete(String res, String args, String ret);

        void comment(String body);

        void get(String res, String args, String ret);

        void post(String res, String args, String result);

        void put(String res, String args, String ret);

    }

    static final Log LOG = LogFactory.getLog(RestFixtureWithSeq.class);

    /**
     * default directory where the diagrams are generated. The value is
     * <code>new File("restfixture")</code>, a directory relative to the default
     * fitnesse root directory.
     */
    private File graphFileDir;

    /**
     * this fixture instance picture name
     */
    private String pictureName;

    /**
     * this fixture instance picture data. picture data is a composite string
     * containig the path to the image file and a sequence of attributes in the
     * form of name=value.
     */
    private String pictureData;

    private Model model;

    private boolean initialised;

    /**
     * listens to events raised by the fixture and captures them in the model.
     */
    private MyFixtureListener myFixtureListener;

    /**
     * svg attributes
     */
    private Map<String, String> attributes;

    /**
     * file format
     */
    private String format;

    @SuppressWarnings("rawtypes")
    private CellWrapper cell;

    public RestFixtureWithSeq() {
        super();
        this.initialised = false;
        LOG.info("Default ctor");
    }

    public RestFixtureWithSeq(String hostName, String pictureData) {
        super(hostName);
        this.pictureData = pictureData;
        this.initialised = false;
    }

    public RestFixtureWithSeq(String hostName, String configName, String pictureData) {
        super(hostName, configName);
        this.pictureData = pictureData;
        this.initialised = false;
    }

    public RestFixtureWithSeq(PartsFactory partsFactory, String hostName, String configName, String pictureData) {
        super(partsFactory, hostName, configName);
        this.pictureData = pictureData;
        this.initialised = false;
    }

    /**
     * embeds as a &lt;img> with content encoded the model caprured so far.
     */
    public void embed() {
        cell = row.getCell(1);
        byte[] content = PictureGenerator.generate(model.toString(), parseAttributes(cell.body()), "template.svg", format);
        cell.body(getFormatter().gray("<img src=\"data:image/" + format + ";base64," + new String(Base64.encode(content)) + "\" />"));
    }

    public void setModel(Model model) {
        this.model = model;
    }

    @Override
    protected void initialize(Runner runner) {
        super.initialize(runner);
        initializeFields();
        createSequenceModel();
        initialised = true;
        String defaultPicsDir = System.getProperty("restfixture.graphs.dir", "FitNesseRoot/files/restfixture");
        String picsDir = getConfig().get("restfixture.graphs.dir", defaultPicsDir);
        graphFileDir = new File(picsDir);
        if (!graphFileDir.exists()) {
            if (!graphFileDir.mkdirs()) {
                throw new FitFailureException("Unable to create the diagrams destination dir '" + graphFileDir.getAbsolutePath() + "'");
            } else {
                LOG.info("Created diagrams destination directory '" + graphFileDir.getAbsolutePath() + "'");
            }
        }
        myFixtureListener = new MyFixtureListener(new File(graphFileDir, this.getPictureName()).getAbsolutePath(), model, attributes);
        setFixtureListener(myFixtureListener);
    }

    protected String getPictureDataFromArgs() {
        if (args.length > 1) {
            return args[args.length - 1];
        }
        return null;
    }

    @Override
    protected String getConfigNameFromArgs() {
        if (args.length == 3) {
            return args[1];
        }
        return null;
    }

    @Override
    protected String getBaseUrlFromArgs() {
        if (args.length > 1) {
            return args[0];
        }
        return null;
    }

    /**
     * State of the RestFixtureWithSeq is valid (or true) if both the baseUrl
     * and picture name are not null. These parameters are the first and second
     * in input to the fixture.
     * 
     * @return true if valid.
     */
    @Override
    protected boolean validateState() {
        return getBaseUrl() != null && pictureData != null;
    }

    @Override
    protected void notifyInvalidState(boolean state) {
        if (!state) {
            throw new FitFailureException("Both baseUrl and picture data (containing the picture name) need to be passed to the fixture");
        }
    }

    protected void createSequenceModel() {
        if (!initialised) {
            LOG.info("Initialising sequence model");
            this.model = new SequenceModel();
        }
    }


    /**
     * Method invoked to start processing the current table. Action fixtures
     * seem not to cope correctly with overriding the default
     * <code>fit.FixtureListener</code>. The need for overriding this method is
     * to make sure that the listener set in <code>this.listener</code> is
     * correctly invoked to complete the sequence diagram generation.
     */
    @Override
    public void doTable(Parse table) {
        super.doTable(table);
        listener.tableFinished(table);
    }

    /**
     * Note: for SLIM to find this method it has to be defined in the java file
     * after the override of the ActionFixture method
     */
    @Override
    public List<List<String>> doTable(List<List<String>> rows) {
        List<List<String>> result = super.doTable(rows);
        listener.tableFinished(null);
        return result;
    }

    /**
     * Overrides the RestFixture doCells to set up the mandatory pictureName
     * when using Fit runner.
     */
    @Override
    public void doCells(Parse table) {
        this.pictureData = getPictureDataFromArgs();
        super.doCells(table);
    }

    @Override
    public void setBody() {
        super.setBody();
    }

    /**
     * a DELETE generates a message and a return arrows.
     */
    @Override
    public void DELETE() {
        super.DELETE();
        String res = getLastRequest().getResource();
        String args = getLastRequest().getQuery();
        String ret = "status=" + getLastResponse().getStatusCode().toString();
        model.delete(res, args, ret);
    }

    @Override
    public void comment() {
        super.comment();
        @SuppressWarnings("rawtypes")
        CellWrapper messageCell = row.getCell(1);
        String body = messageCell.body();
        String plainBody = Tools.fromHtml(body).trim();
        model.comment(plainBody);
    }

    /**
     * a GET generates a message and a return arrows.
     */
    @Override
    public void GET() {
        super.GET();
        String res = getResource();
        String args = getLastRequest().getQuery();
        String ret = "status=" + getLastResponse().getStatusCode().toString();
        model.get(res, args, ret);
    }

    /**
     * a POST generates a message to the resource type, which in turn generates
     * a create to the resource just created. The resource uri must be defined
     * in the <code>Location</code> header in the POST response. A return arrow
     * is then generated.
     */
    @Override
    public void POST() {
        super.POST();
        String res = getResource();
        String id = getIdFromLocationHeader();
        // could ever be that the POST to /abc returns a location of /qwe/1 ??
        String result = String.format("id=%s, status=%s", id, getLastResponse().getStatusCode().toString());
        String args = getLastRequest().getQuery();
        model.post(res, args, result);
    }

    /**
     * a PUT generates a message and a return arrows.
     */
    @Override
    public void PUT() {
        super.PUT();
        String res = getResource();
        String args = getLastRequest().getQuery();
        String ret = "status=" + getLastResponse().getStatusCode().toString();
        model.put(res, args, ret);
    }

    /**
     * the picture name is the second parameter of the fixture.
     * 
     * @return the picture name
     */
    String getPictureName() {
        return pictureName;
    }

    void setFixtureListener(FixtureListener l) {
        super.listener = l;
    }

    private static String getPictureFormat(String pictureName) {
        int pos = pictureName.indexOf(".");
        if (pos >= 0) {
            return pictureName.substring(pos + 1).toLowerCase();
        }
        throw new IllegalArgumentException("The picture name must terminate with an extension of .svg, .png, .jpg");
    }

    private void initializeFields() {
        if (!initialised) {
            String data = pictureData;
            LOG.info("Picture data = " + pictureData);
            int[] pos = getPositionOfNextOfTokenOptionallyInDoubleQuotes(data);
            this.pictureName = data.substring(pos[0], pos[1]);
            LOG.info("Found picture name: " + pictureName);
            if (pos[1] < data.length()) {
                data = data.substring(pos[1] + 1);
                this.attributes = parseAttributes(data);
            }
            this.format = getPictureFormat(pictureName);
        }
    }

    private static Map<String, String> parseAttributes(String data) {
        Map<String, String> foundAttributes = new HashMap<String, String>();
        while (true) {
            int eqPos = data.indexOf("=");
            if (eqPos < 0) {
                break;
            }
            String aName = data.substring(0, eqPos);
            LOG.info("Found attribute name: " + aName);
            data = data.substring(eqPos + 1);
            int[] pos = getPositionOfNextOfTokenOptionallyInDoubleQuotes(data);
            String aVal = data.substring(pos[0], pos[1]);
            LOG.info("Found attribute val: " + aVal + ", pos[" + pos[0] + ", " + pos[1] + "]");
            foundAttributes.put(aName, aVal);
            if (data.length() - aVal.length() == 0) {
                break;
            }
            data = data.substring(pos[1] + 1);
        }
        return foundAttributes;
    }

    private static int[] getPositionOfNextOfTokenOptionallyInDoubleQuotes(String data) {
        String del = " ";
        int start = 0;
        if (data.trim().startsWith("\"")) {
            del = "\"";
            start = 1;
        }
        int end = data.indexOf(del, start + 1);
        if (end == -1) {
            end = data.length();
        }
        return new int[] { start, end };
    }

    private String[] guessParts(String res) {
        String[] empty = new String[] { "?", "" };
        if (res == null) {
            return empty;
        }
        String myRes = res.trim();
        if (myRes.isEmpty()) {
            return empty;
        }
        int pos = myRes.lastIndexOf("/");
        if (pos == myRes.length() - 1) {
            pos = -1;
            myRes = myRes.substring(0, myRes.length() - 1);
        }
        String[] parts = new String[2];
        if (pos >= 0) {
            parts[0] = myRes.substring(0, pos);
            parts[1] = myRes.substring(pos + 1);
        } else {
            parts[0] = myRes;
            parts[1] = "";
        }
        return parts;
    }

    private String getIdFromLocationHeader() {
        List<Header> list = getLastResponse().getHeader("Location");
        String location = "";
        if (list != null && !list.isEmpty()) {
            location = list.get(0).getValue();
        }
        String[] parts = guessParts(location);
        return parts[1];
    }

    private String getResource() {
        String res = getLastRequest().getResource();
        if (res.endsWith("/")) {
            res = res.substring(0, res.length() - 1);
        }
        return res;
    }
}

/**
 * Holds the sequence diagram model, specifically abstratcs out the underlying
 * library constructing the SVG picture.
 * 
 * @author fabrizio
 * 
 */
class SequenceModel implements Model {
    private Map<String, Resource> resourceToAgentMap;
    private Node root;
    private SequenceLayout layout;
    /**
     * Hints to the SVG files generator.
     */
    private static int DEFAULT_FONT_SIZE = 16;
    private static int DEFAULT_AGENT_STEP = 150;
    private static int DEFAULT_TIME_STEP = 25;

    SequenceModel() {
        Message message = new Message(null, null);
        Node root = new Node(message);
        SequenceLayout layout = new SequenceLayout(DEFAULT_FONT_SIZE);
        layout.setAgentStep(DEFAULT_AGENT_STEP);
        layout.setTimeStep(DEFAULT_TIME_STEP);

        this.root = root;
        this.layout = layout;
        this.resourceToAgentMap = new HashMap<String, Resource>();
    }

    public void comment(String text) {
        root.add(new Node(new Note(Tools.fromHtml(text))));
    }

    public void get(String resource, String query, String result) {
        message(Message.SYNC, resource, "GET", query, result);
    }

    public void post(String resource, String query, String result) {
        message(Message.SYNC, resource, "POST", query, result);
    }

    public void put(String resource, String query, String result) {
        message(Message.SYNC, resource, "PUT", query, result);
    }

    public void delete(String resource, String query, String result) {
        message(Message.DESTROY, resource, "DELETE", query, result);
    }

    public String toString() {
        return layout.layout(root);
    }

    private void message(int type, String resourceTo, String method, String args, String result) {
        Agent agentTo = agentFor(resourceTo);
        String methodSignature = args == null ? method : method + "(" + args + ")";
        String resultString = result == null ? "" : result;
        Message message = new Message(type, agentTo, methodSignature, resultString);
        root.add(new Node(message));
    }

    private Resource agentFor(String resource) {
        Resource a = resourceToAgentMap.get(resource);
        if (a == null) {
            boolean isActivable = true;
            a = new Resource(resource, isActivable);
            resourceToAgentMap.put(resource, a);
        }
        return a;
    }
}

/**
 * A representation of a resource for the purposes of generating the sequence
 * diagram.
 * 
 * @author fabrizio
 * 
 */
class Resource extends Agent {

    public Resource(String type, boolean isActivable) {
        super(type, "", isActivable);
    }

    public String toString() {
        return isEllipsis() ? "..." : (new StringBuilder()).append(getType()).toString();
    }
}

/**
 * A <code>fit.FixtureListener</code> that listens for a table being completed.
 * the action performed on table completion is the actual graph generation.
 * 
 * @author fabrizio
 */
class MyFixtureListener implements FixtureListener {

    private final Model model;
    private final String picFileName;
    private final Map<String, String> attributes;

    /**
     * @param f
     *            the fixture instance backing up a table. it's necessary as the
     *            file name is only know at execution time and the
     *            <code>args</code> array containing the file name for the
     *            diagram is not known until the fixture has been created @
     * @param supportFilesDir
     *            the directory containing the support files needed to generate
     *            the diagram
     * @param graphDir
     *            the directory where the sequence diagram is generated
     */
    public MyFixtureListener(String outFileName, Model m, Map<String, String> attr) {
        model = m;
        attributes = attr != null ? attr : new HashMap<String, String>();
        picFileName = outFileName;
    }

    /**
     * generates the sequence diagram with the events collected in the model.
     */
    public void tableFinished(Parse parse) {
        int pos = picFileName.lastIndexOf(".");
        String format = "svg";
        if (pos > 0) {
            format = picFileName.substring(pos + 1).toLowerCase();
        }
        byte[] content = PictureGenerator.generate(model.toString(), attributes, "template.svg", format);
        File f = new File(picFileName);
        try {
            f.createNewFile();
        } catch (IOException e1) {
            throw new IllegalArgumentException("Unable to create output picture file: " + f.getAbsolutePath(), e1);
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
        } catch (FileNotFoundException e1) {
            throw new IllegalArgumentException("Unable to find output picture file: " + f.getAbsolutePath(), e1);
        }
        try {
            fos.write(content);
        } catch (IOException e1) {
            throw new IllegalArgumentException("Unable to write output picture file: " + f.getAbsolutePath(), e1);
        }
        try {
            fos.flush();
        } catch (IOException e1) {
            throw new IllegalArgumentException("Unable to flush output picture file: " + f.getAbsolutePath());
        }
        try {
            if (fos != null) {
                fos.close();
            }
        } catch (IOException e) {
        }
    }

    /**
     * not used.
     */
    public void tablesFinished(Counts counts) {
    }

}

class PictureGenerator {

    public static byte[] generate(String content, Map<String, String> attributes, String svgTemplate, String format) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final TemplatedWriter writer = new TemplatedWriter(baos, svgTemplate);
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> e : attributes.entrySet()) {
            sb.append(e.getKey()).append("=\"").append(e.getValue()).append("\" ");
        }
        writer.write(content, sb.toString());
        byte[] ret = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) {
        }
        if (format.equals("svg")) {
            return ret;
        } else {
            Integer w = null;
            if (attributes.get("width") != null) {
                try {
                    w = Integer.parseInt(attributes.get("width"));
                } catch (NumberFormatException e) {

                }
            }
            Integer h = null;
            if (attributes.get("height") != null) {
                try {
                    h = Integer.parseInt(attributes.get("height"));
                } catch (NumberFormatException e) {

                }
            }
            return transcode(ret, format, w, h);
        }
    }

    public static byte[] transcode(byte[] svg, String format, Integer w, Integer h) {
        ImageTranscoder trans = null;
        if (format.equals("jpg")) {
            trans = new JPEGTranscoder();
        } else if (format.equals("png")) {
            trans = new PNGTranscoder();
        } else {
            throw new IllegalArgumentException("Unsupported raster format. Only jpg and png: " + format);
        }
        TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(svg));
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        TranscoderOutput output = new TranscoderOutput(ostream);
        if (w != null && h != null) {
            Rectangle aoi = new Rectangle(w, h);
            trans.addTranscodingHint(JPEGTranscoder.KEY_WIDTH, new Float(aoi.width));
            trans.addTranscodingHint(JPEGTranscoder.KEY_HEIGHT, new Float(aoi.height));
            trans.addTranscodingHint(ImageTranscoder.KEY_FORCE_TRANSPARENT_WHITE, Boolean.FALSE);
            trans.addTranscodingHint(JPEGTranscoder.KEY_AOI, aoi);
        }
        try {
            trans.transcode(input, output);
        } catch (TranscoderException e) {
            throw new IllegalStateException("Unable to transcode to format: " + format, e);
        }
        try {
            ostream.flush();
        } catch (IOException e) {
            // should be safe to ignore
        }
        try {
            ostream.close();
        } catch (IOException e) {
            // should be safe to ignore
        }
        return ostream.toByteArray();
    }

}
