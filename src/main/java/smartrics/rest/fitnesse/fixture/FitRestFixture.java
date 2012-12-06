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

public class FitRestFixture extends ActionFixture {
	private static final Logger LOG = LoggerFactory.getLogger(FitRestFixture.class);

	private RestFixture restFixture;
	
	public String toString() {
		return restFixture.toString();
	}


	public String getLastEvaluation() {
		return restFixture.getLastEvaluation();
	}


	public String getBaseUrl() {
		return restFixture.getBaseUrl();
	}


	public void setBaseUrl(Url url) {
		restFixture.setBaseUrl(url);
	}


	public Map<String, String> getDefaultHeaders() {
		return restFixture.getDefaultHeaders();
	}


	public CellFormatter<?> getFormatter() {
		return restFixture.getFormatter();
	}


	public void setMultipartFileName() {
		restFixture.setMultipartFileName();
	}


	public String getMultipartFileName() {
		return restFixture.getMultipartFileName();
	}


	public void setFileName() {
		restFixture.setFileName();
	}


	public String getFileName() {
		return restFixture.getFileName();
	}


	public void setMultipartFileParameterName() {
		restFixture.setMultipartFileParameterName();
	}


	public String getMultipartFileParameterName() {
		return restFixture.getMultipartFileParameterName();
	}


	public void setBody() {
		restFixture.setBody();
	}


	public void setHeader() {
		restFixture.setHeader();
	}


	public void setHeaders() {
		restFixture.setHeaders();
	}

	public void PUT() {
		restFixture.PUT();
	}


	public void GET() {
		restFixture.GET();
	}


	public void DELETE() {
		restFixture.DELETE();
	}


	public void POST() {
		restFixture.POST();
	}


	public void let() {
		restFixture.let();
	}


	public void comment() {
		restFixture.comment();
	}


	public void evalJs() {
		restFixture.evalJs();
	}


	public void processRow(RowWrapper<?> currentRow) {
		restFixture.processRow(currentRow);
	}


	public Map<String, String> getHeaders() {
		return restFixture.getHeaders();
	}


	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void doCells(Parse parse) {
		if(restFixture == null) {
			restFixture = new RestFixture();
			restFixture.setConfig(Config.getConfig(getConfigNameFromArgs()));
			String url = getBaseUrlFromArgs();
			if (url != null) {
				restFixture.setBaseUrl(new Url(Tools.fromSimpleTag(url)));
			}
			restFixture.initialize(Runner.FIT);
			((FitFormatter) restFixture.getFormatter()).setActionFixtureDelegate(this);
		}
		RowWrapper currentRow = new FitRow(parse);
		try {
			restFixture.processRow(currentRow);
        } catch (Exception exception) {
            LOG.error("Exception when processing row " + currentRow.getCell(0).text(), exception);
            restFixture.getFormatter().exception(currentRow.getCell(0), exception);
		}
	}

	/**
	 * Process args to extract the optional config name.
	 * 
	 * @return
	 */
	protected String getConfigNameFromArgs() {
		if (args.length >= 2) {
			return args[1];
		}
		return null;
	}

	/**
	 * Process args ({@see fit.Fixture}) for Fit runner to extract the baseUrl
	 * of each Rest request, first parameter of each RestFixture table.
	 * 
	 * @return
	 */
	protected String getBaseUrlFromArgs() {
		if (args.length > 0) {
			return args[0];
		}
		return null;
	}


	public Config getConfig() {
		return restFixture.getConfig();
	}

}
