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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import smartrics.rest.fitnesse.fixture.RestFixture.Runner;
import smartrics.rest.fitnesse.fixture.support.CellFormatter;
import smartrics.rest.fitnesse.fixture.support.Config;
import smartrics.rest.fitnesse.fixture.support.RowWrapper;
import smartrics.rest.fitnesse.fixture.support.Tools;
import smartrics.rest.fitnesse.fixture.support.Url;
import fit.ActionFixture;
import fit.Parse;

/**
 * A {@link RestFixture} implementation for Fit runner. The class is simply an
 * {@link ActionFixture} implementation that delegates to an instance of
 * {@link RestFixture}.
 * 
 * @author smartrics
 */
public class FitRestFixture extends ActionFixture {
	private static final Logger LOG = LoggerFactory
			.getLogger(FitRestFixture.class);

	private RestFixture restFixture;

	public String toString() {
		return restFixture.toString();
	}

	/**
	 * See {@link RestFixture#getLastEvaluation()}
	 * 
	 * @return last JS evaluation
	 */
	public String getLastEvaluation() {
		return restFixture.getLastEvaluation();
	}

	/**
	 * @return delegates to {@link RestFixture#getBaseUrl()}
	 */
	public String getBaseUrl() {
		return restFixture.getBaseUrl();
	}

	public void setBaseUri(String uri) {   //mqm  - it comes as a string in a scenario.
		this.setBaseUrl(new Url(uri));
	}

	/**
	 * delegates to {@link RestFixture#setBaseUrl(Url)}
	 * 
	 * @param url
	 *            the base url.
	 */
	public void setBaseUrl(Url url) {
		restFixture.setBaseUrl(url);
	}

	/**
	 * delegates to {@link RestFixture#getDefaultHeaders()}
	 * 
	 * @return the default headers.
	 */
	public Map<String, String> getDefaultHeaders() {
		return restFixture.getDefaultHeaders();
	}

	/**
	 * delegates to {@link RestFixture#getFormatter()}
	 * 
	 * @return the cell formatter for Fit.
	 */
	public CellFormatter<?> getFormatter() {
		return restFixture.getFormatter();
	}

	/**
	 * delegates to {@link RestFixture#setMultipartFileName()}
	 */
	public void setMultipartFileName() {
		restFixture.setMultipartFileName();
	}

	/**
	 * delegates to {@link RestFixture#getMultipartFileName()}
	 * 
	 * @return the multipart filename to upload.
	 */
	public String getMultipartFileName() {
		return restFixture.getMultipartFileName();
	}

	/**
	 * delegates to {@link RestFixture#setFileName()}
	 */
	public void setFileName() {
		restFixture.setFileName();
	}

	/**
	 * delegates to {@link RestFixture#getFileName()}
	 * 
	 * @return the name of the file to upload
	 */
	public String getFileName() {
		return restFixture.getFileName();
	}

	/**
	 * delegates to {@link RestFixture#setMultipartFileParameterName()}
	 */
	public void setMultipartFileParameterName() {
		restFixture.setMultipartFileParameterName();
	}

	/**
	 * delegates to {@link RestFixture#getMultipartFileParameterName()}
	 * 
	 * @return the name of the parameter containing the multipart file to
	 *         upload.
	 */
	public String getMultipartFileParameterName() {
		return restFixture.getMultipartFileParameterName();
	}

	/**
	 * delegates to {@link RestFixture#setBody()}
	 */
	public void setBody() {
		restFixture.setBody();
	}

	/**
	 * delegates to {@link RestFixture#setHeader()}
	 */
	public void setHeader() {
		restFixture.setHeader();
	}

	/**
	 * delegates to {@link RestFixture#setHeaders()}
	 */
	public void setHeaders() {
		restFixture.setHeaders();
	}

	/**
	 * delegates to {@link RestFixture#PUT()}
	 */
	public void PUT() {
		restFixture.PUT();
	}

	/**
	 * delegates to {@link RestFixture#GET()}
	 */
	public void GET() {
		restFixture.GET();
	}

	/**
	 * delegates to {@link RestFixture#DELETE()}
	 */
	public void DELETE() {
		restFixture.DELETE();
	}

	/**
	 * delegates to {@link RestFixture#POST()}
	 */
	public void POST() {
		restFixture.POST();
	}

	/**
	 * delegates to {@link RestFixture#HEAD()}
	 */
	public void HEAD() {
		restFixture.HEAD();
	}

	/**
	 * delegates to {@link RestFixture#OPTIONS()}
	 */
	public void OPTIONS() {
		restFixture.OPTIONS();
	}

	/**
	 * delegates to {@link RestFixture#TRACE()}
	 */
	public void TRACE() {
		restFixture.TRACE();
	}

	/**
	 * delegates to {@link RestFixture#let()}
	 */
	public void let() {
		restFixture.let();
	}

	/**
	 * delegates to {@link RestFixture#comment()}
	 */
	public void comment() {
		restFixture.comment();
	}

	/**
	 * delegates to {@link RestFixture#evalJs()}
	 */
	public void evalJs() {
		restFixture.evalJs();
	}

	/**
	 * delegates to {@link RestFixture#processRow(RowWrapper)}
	 * 
	 * @param currentRow
	 *            the row to process.
	 */
	public void processRow(RowWrapper<?> currentRow) {
		restFixture.processRow(currentRow);
	}

	/**
	 * delegates to {@link RestFixture#getHeaders()}
	 * 
	 * @return the headers.
	 */
	public Map<String, String> getHeaders() {
		return restFixture.getHeaders();
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void doCells(Parse parse) {
		if (restFixture == null) {
			restFixture = new RestFixture();
			restFixture.setConfig(Config.getConfig(getConfigNameFromArgs()));
			String url = getBaseUrlFromArgs();
			if (url != null) {
				restFixture.setBaseUrl(new Url(Tools.fromSimpleTag(url)));
			}
			restFixture.initialize(Runner.FIT);
			((FitFormatter) restFixture.getFormatter())
					.setActionFixtureDelegate(this);
		}
		RowWrapper currentRow = new FitRow(parse);
		try {
			restFixture.processRow(currentRow);
		} catch (Exception exception) {
			LOG.error("Exception when processing row "
					+ currentRow.getCell(0).text(), exception);
			restFixture.getFormatter().exception(currentRow.getCell(0),
					exception);
		}
	}

	/**
	 * @return optional config name
	 */
	protected String getConfigNameFromArgs() {
		if (args.length >= 2) {
			return args[1];
		}
		return null;
	}

	/**
	 * @return Process args ({@link fit.Fixture}) for Fit runner to extract the
	 *         baseUrl of each Rest request, first parameter of each RestFixture
	 *         table.
	 */
	protected String getBaseUrlFromArgs() {
		if (args.length > 0) {
			return args[0];
		}
		return null;
	}

	/**
	 * @return the config
	 */
	public Config getConfig() {
		return restFixture.getConfig();
	}

}
