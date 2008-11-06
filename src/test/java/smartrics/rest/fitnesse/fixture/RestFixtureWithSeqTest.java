package smartrics.rest.fitnesse.fixture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import smartrics.rest.client.RestClient;
import smartrics.rest.config.Config;
import smartrics.sequencediagram.Create;
import smartrics.sequencediagram.Event;
import smartrics.sequencediagram.Message;
import smartrics.sequencediagram.Return;
import fit.Counts;
import fit.FixtureListener;
import fit.Parse;
import fit.exception.FitFailureException;
import fit.exception.FitParseException;

public class RestFixtureWithSeqTest {
	
	private RestFixtureWithSeq fixture;
	private RestFixtureTestHelper helper;

	@Before
	public void setUp() {
		helper = new RestFixtureTestHelper();
		fixture = new RestFixtureWithSeq() {
			{
				super.args = new String[] { "http://localhost:8080",
						"sequence.gif" };
			}
			
			@Override
			protected RestClient buildRestClient() {
				return new MockRestClient();
			}
		};
	}

	@Test
	public void shouldHaveConfigNameAsOptionalSecondParameterToDefaultWhenNotSpecified()
			throws FitParseException {
		fixture.doCells(new Parse("<table><tr><td></td></tr></table>"));
		assertEquals(Config.DEFAULT_CONFIG_NAME, fixture.getConfig().getName());
	}

	@Test
	public void shouldHaveConfigNameAsOptionalSecondParameterToBeSetToSpecifiedValue()
			throws FitParseException {
		fixture = new RestFixtureWithSeq() {
			{
				super.args = new String[] { "http://localhost:8080",
						"configName", "sequence.gif" };
			}
		};
		fixture.doCells(new Parse("<table><tr><td></td></tr></table>"));
		assertEquals("configName", fixture.getConfig().getName());
		assertEquals("sequence.gif", fixture.getPictureName());
	}

	@Test(expected = FitFailureException.class)
	public void mustNotifyCallerThatPictureNameIsMandatory()
			throws FitParseException {
		fixture = new RestFixtureWithSeq() {
			{
				super.args = new String[] { "http://localhost:8080" };
			}
		};
		fixture.doCells(new Parse("<table><tr><td></td></tr></table>"));
	}

	@Test
	public void mustGenerateTwoEventsForPut() {
		Parse t = helper.createFitTestInstance(helper.createFitTestRow("PUT",
				"/uri", "", "", ""));
		fixture.doCells(t);
		assertOnMethods("PUT");
	}

	@Test
	public void mustGenerateTwoEventsForGet() {
		Parse t = helper.createFitTestInstance(helper.createFitTestRow("GET",
				"/uri", "", "", ""));
		fixture.doCells(t);
		assertOnMethods("GET");
	}

	@Test
	public void mustGenerateTwoEventsForDelete() {
		Parse t = helper.createFitTestInstance(helper.createFitTestRow(
				"DELETE", "/uri", "", "", ""));
		fixture.doCells(t);
		assertOnMethods("DELETE");
	}

	@Test
	public void mustGenerateThreeEventsForPost() {
		Parse t = helper.createFitTestInstance(helper.createFitTestRow("POST",
				"/uri", "", "", ""));
		fixture.doCells(t);
		assertEquals(3, fixture.getModel().getEvents().size());
		Event event = fixture.getModel().getEvents().get(0);
		assertTrue(event instanceof Message);
		assertEquals("POST", event.getName());
		assertTrue(fixture.getModel().getEvents().get(1) instanceof Create);
		assertTrue(fixture.getModel().getEvents().get(2) instanceof Return);
	}

	@Test
	public void mustInvokeListenerWhenTableProcessCompletes() {
		MockFixtureListener l = new MockFixtureListener();
		RestFixtureWithSeq fixture = new RestFixtureWithSeq() {
			@Override
			public void doCells(Parse p) {
				// do nothing
			}
		};
		fixture.setFixtureListener(l);
		// TODO: need to look at this - artificial Parse to make doTable pass...
		Parse p0 = new Parse("table", "", null, null);
		Parse p1 = new Parse("table", "", p0, p0);
		Parse p2 = new Parse("table", "", p1, p1);
		Parse p3 = new Parse("table", "", p2, p2);
		fixture.doTable(p3);
		assertTrue("Listener tableFinished not called", l.called);
	}

	private void assertOnMethods(String name) {
		assertEquals(2, fixture.getModel().getEvents().size());
		Event event = fixture.getModel().getEvents().get(0);
		assertTrue(event instanceof Message);
		assertTrue(fixture.getModel().getEvents().get(1) instanceof Return);
		assertEquals(name, event.getName());
	}

	private static class MockFixtureListener implements FixtureListener {
		boolean called = false;

		@Override
		public void tableFinished(Parse table) {
			called = true;
		}

		@Override
		public void tablesFinished(Counts count) {
			// TODO Auto-generated method stub

		}
	}
}
