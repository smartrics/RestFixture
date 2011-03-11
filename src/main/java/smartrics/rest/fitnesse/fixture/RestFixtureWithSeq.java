package smartrics.rest.fitnesse.fixture;

import java.io.File;

import smartrics.rest.client.RestData.Header;
import smartrics.rest.config.Config;
import smartrics.sequencediagram.Builder;
import smartrics.sequencediagram.Create;
import smartrics.sequencediagram.GraphGenerator;
import smartrics.sequencediagram.Message;
import smartrics.sequencediagram.Model;
import smartrics.sequencediagram.PicDiagram;
import smartrics.sequencediagram.Return;
import fit.Counts;
import fit.FixtureListener;
import fit.Parse;
import fit.exception.FitFailureException;

/**
 * An extension of RestFixture that generates a sequence diagrams for a table
 * fixture. Sequence diagrams are generated using PIC language templates defined
 * in <a href="http://www.umlgraph.org">UMLGraph</a>.
 *
 * @author fabrizio
 *
 */
public class RestFixtureWithSeq extends RestFixture {

	/**
	 * directory where the support files needed to generate the seuqence
	 * diagrams are. The default value is <code>new File("pic")</code>, implying
	 * that the location is relative to the fitnesse server default directory.
	 */
	public static final File SUPPORT_FILES_DIR = new File("etc/restfixture");

	/**
	 * default directory where the diagrams are generated. The value is
	 * <code>new File("restfixture")</code>, a directory relative to the default
	 * fitnesse root directory.
	 */
	public static final File GRAPH_FILES_DIR;
	static {
		String picsDir = System.getProperty("restfixture.graphs.dir",
				"FitNesseRoot/files/restfixture");
		GRAPH_FILES_DIR = new File(picsDir);
	}

	/**
	 * the name of the object representing the fixture (eg the client executing
	 * REST requests)
	 */
	private static final String FIXTURE = "fixture";

	private PicDiagram diagram;

	private Model model;

	private Builder builder;

	private String pictureName;

	public RestFixtureWithSeq() {
		super();
		create(new PicDiagram(), new Model());
		setFixtureListener(new MyFixtureListener(this, builder,
				SUPPORT_FILES_DIR, GRAPH_FILES_DIR));
	}

	@Override
	protected void processArguments(String[] args) {
		super.processArguments(args);
		if (args.length == 2) {
			pictureName = args[1];
			config = new Config();
		}
		if (args.length == 3) {
			config = new Config(args[1]);
			pictureName = args[2];
		}
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
			throw new FitFailureException(
					"Both baseUrl and picture name need to be passed to the fixture");
		}
	}

	void create(PicDiagram d, Model m) {
		diagram = d;
		model = m;
		builder = new Builder(model, diagram);
	}

	/**
	 * Method invoked to start processing the current table. Action fixtures
	 * seem not to cope correctly with overriding the default
	 * <code>fit.FixtureListener</code>. The need for overriding this method is
	 * to make sure that the listener set in <code>this.listener</code> is
	 * correctly invoked to complete the sequence diagram generation.
	 */
	@Override
	public void doTable(Parse parse1) {
		super.doTable(parse1);
		listener.tableFinished(parse1);
	}

	/**
	 * a DELETE generates a message and a return arrows
	 */
	@Override
	public void DELETE() {
		super.DELETE();
		model.addEvent(new Message(FIXTURE, getLastRequest().getResource(),
				"DELETE"));
		model.addEvent(new Return(getLastRequest().getResource(), FIXTURE,
				getLastResponse().getStatusCode().toString()));
	}

	/**
	 * a GET generates a message and a return arrows
	 */
	@Override
	public void GET() {
		super.GET();
		model.addEvent(new Message(FIXTURE, getLastRequest().getResource(),
				"GET", getLastRequest().getQuery()));
		model.addEvent(new Return(getLastRequest().getResource(), FIXTURE,
				getLastResponse().getStatusCode().toString()));
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
		model.addEvent(new Message(FIXTURE, getLastRequest().getResource(),
				"POST"));
		Header list = getLastResponse().getHeader("Location").get(0);
		String location = "";
		if (list != null)
			location = list.getValue();
		model.addEvent(new Create(getLastRequest().getResource(), location,
				"POST"), true);
		String id = "";
		int lastIndexOf = location.lastIndexOf("/");
		if (lastIndexOf >= 0)
			id = location.substring(lastIndexOf);
		model.addEvent(new Return(getLastRequest().getResource(), FIXTURE,
				getLastResponse().getStatusCode().toString(), id));
	}

	/**
	 * a PUT generates a message and a return arrows
	 */
	@Override
	public void PUT() {
		super.PUT();
		model.addEvent(new Message(FIXTURE, getLastRequest().getResource(),
				"PUT"));
		model.addEvent(new Return(getLastRequest().getResource(), FIXTURE,
				getLastResponse().getStatusCode().toString()));
	}

	/**
	 * the picture name is the second parameter of the fixture
	 *
	 * @return the picture name
	 */
	String getPictureName() {
		return pictureName;
	}

	void setModel(Model m) {
		this.model = m;
	}

	Model getModel() {
		return this.model;
	}

	void setFixtureListener(FixtureListener l) {
		super.listener = l;
	}

}

/**
 * A <code>fit.FixtureListener</code> that listens for a table being completed.
 * the action performed on table completion is the actual graph generation.
 *
 * @author fabrizio
 */
class MyFixtureListener implements FixtureListener {

	private final Builder sequenceBuilder;
	private final RestFixtureWithSeq thisFixture;
	private final GraphGenerator graphGenerator;
	private final File graphDirectory;

	/**
	 * @param f
	 *            the fixture instance backing up a table. it's necessary as the
	 *            file name is only know at execution time and the
	 *            <code>args</code> array containing the file name for the
	 *            diagram is not known until the fixture has been created
	 * @param b
	 *            the sequence diagram builder
	 * @param supportFilesDir
	 *            the directory containing the support files needed to generate
	 *            the diagram
	 * @param graphDir
	 *            the directory where the sequence diagram is generated
	 */
	public MyFixtureListener(RestFixtureWithSeq f, Builder b,
			File supportFilesDir, File graphDir) {
		sequenceBuilder = b;
		thisFixture = f;
		graphDirectory = graphDir;
		graphGenerator = new GraphGenerator(supportFilesDir.getAbsolutePath());
	}

	/**
	 * generates the sequence diagram with the events collected in the model.
	 */
	public void tableFinished(Parse parse) {
		sequenceBuilder.build();
		String diag = sequenceBuilder.getDiagram().toString();
		try {
			File graphFile = new File(graphDirectory, thisFixture
					.getPictureName());
			init(graphFile);
			System.out.println("Generating sequence diagram in "
					+ graphFile.getAbsolutePath());
			graphGenerator.generateGif(diag, graphFile);
		} catch (Exception e) {
			// ignore error -
			e.printStackTrace();
		}
	}

	protected void init(File graphFile) {
		if (!graphDirectory.exists()) {
			try {
				graphDirectory.mkdirs();
			} catch (RuntimeException e) {
				throw new IllegalStateException("Cannot create "
						+ graphDirectory.getAbsolutePath(), e);
			}
		}
		// if (!graphFile.canWrite()) {
		// throw new IllegalStateException("Cannot write "
		// + graphFile.getAbsolutePath());
		// }
		if (graphFile.exists()) {
			try {
				graphFile.delete();
			} catch (RuntimeException e) {
				// ingore if cant delete
				// TODO: should throw exception if file not deleted?
			}
		}
	}

	/**
	 * not used.
	 */
	public void tablesFinished(Counts counts) {
	}

}
