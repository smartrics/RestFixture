/*  Copyright 2011 Fabrizio Cannizzo
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

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import smartrics.rest.client.RestData.Header;
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

/**
 * An extension of RestFixture that generates a sequence diagrams for a table
 * fixture. Sequence diagrams are generated using PIC language templates defined
 * in <a href="http://www.umlgraph.org">UMLGraph</a>.
 * 
 * The deployment of the support files and the destination directory is
 * controlled by the following two configuration parameters
 * 
 * <table>
 * <tr>
 * <td>restfixture.graphs.dir</td>
 * <td>destination directory where the images with sequence diagrams will be
 * created. The directory will be created if not existent; the fixture will fail
 * if the directory can't be created</td>
 * </tr>
 * </table>
 * 
 * NOTE: This class only works with Fit runner (not Slim)
 * 
 * @author fabrizio
 * 
 */
public class RestFixtureWithSeq2 extends RestFixture {

    private static final Log LOG = LogFactory.getLog(RestFixtureWithSeq2.class);

    private int DEFAULT_FONT_SIZE = 16;
    private int DEFAULT_AGENT_STEP = 150;
    private int DEFAULT_TIME_STEP = 25;

    /**
     * default directory where the diagrams are generated. The value is
     * <code>new File("restfixture")</code>, a directory relative to the default
     * fitnesse root directory.
     */
    private File graphFileDir;

    private String pictureName;

    private SequenceModel model;

    private boolean initialised;

    public RestFixtureWithSeq2() {
        super();
    }

    public RestFixtureWithSeq2(String hostName, String pictureName) {
        super(hostName);
        this.pictureName = pictureName;
        this.initialised = false;
    }

    public RestFixtureWithSeq2(String hostName, String configName, String pictureName) {
        super(hostName, configName);
        this.pictureName = pictureName;
        this.initialised = false;
    }

    public RestFixtureWithSeq2(PartsFactory partsFactory, String hostName, String configName, String pictureName) {
        super(partsFactory, hostName, configName);
        this.pictureName = pictureName;
        this.initialised = false;
    }

    @Override
    protected void initialize(Runner runner) {
        super.initialize(runner);
        createSequenceModel();
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
        setFixtureListener(new MyFixtureListener2(new File(graphFileDir, this.getPictureName()).getAbsolutePath(), model));
    }

    protected String getPictureNameFromArgs() {
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
        return getBaseUrl() != null && getPictureName() != null;
    }

    @Override
    protected void notifyInvalidState(boolean state) {
        if (!state) {
            throw new FitFailureException("Both baseUrl and picture name need to be passed to the fixture");
        }
    }

    void createSequenceModel() {
        if (!initialised) {
            initialised = true;
            LOG.info("Initialising sequence model");
            Message message = new Message(null, null);
            Node root = new Node(message);
            SequenceLayout layout = new SequenceLayout(DEFAULT_FONT_SIZE);
            layout.setAgentStep(DEFAULT_AGENT_STEP);
            layout.setTimeStep(DEFAULT_TIME_STEP);
            this.model = new SequenceModel(layout, root);
        }
    }

    @Override
    public List<List<String>> doTable(List<List<String>> rows) {
        // return super.doTable(rows);
        throw new RuntimeException("This fixture is not supported on SLIM runner. Please use it with FIT");
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
     * Overrides the RestFixture doCells to set up the mandatory pictureName
     * when using Fit runner.
     */
    @Override
    public void doCells(Parse table) {
        this.pictureName = getPictureNameFromArgs();
        super.doCells(table);
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
        @SuppressWarnings("rawtypes")
        CellWrapper messageCell = row.getCell(1);
        model.comment(messageCell.body());
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

class SequenceModel {
    private static final Log LOG = LogFactory.getLog(SequenceModel.class);
    private Map<String, Resource> resourceToAgentMap;
    private Node root;
    private SequenceLayout layout;

    SequenceModel(SequenceLayout layout, Node root) {
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
class MyFixtureListener2 implements FixtureListener {

    private final SequenceModel model;
    private final String picFileName;

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
    public MyFixtureListener2(String outFileName, SequenceModel m) {
        model = m;
        picFileName = outFileName;
    }

    /**
     * generates the sequence diagram with the events collected in the model.
     */
    public void tableFinished(Parse parse) {
        try {
            final String s = model.toString();
            File graphFile = new File(picFileName);
            final TemplatedWriter writer = new TemplatedWriter(graphFile, "template.svg");
            // writer.write(s, "viewBox=\"0 0 1000 1000\"");
            writer.write(s, "");
        } catch (Exception e) {
            // ignore error -
            e.printStackTrace();
        }
    }

    /**
     * not used.
     */
    public void tablesFinished(Counts counts) {
    }

}
