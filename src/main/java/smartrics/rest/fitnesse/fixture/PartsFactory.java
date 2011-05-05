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

import org.apache.commons.httpclient.HttpClient;

import smartrics.rest.client.RestClient;
import smartrics.rest.client.RestClientImpl;
import smartrics.rest.client.RestRequest;
import smartrics.rest.config.Config;
import smartrics.rest.fitnesse.fixture.RestFixture.Runner;
import smartrics.rest.fitnesse.fixture.support.BodyTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.BodyTypeAdapterFactory;
import smartrics.rest.fitnesse.fixture.support.CellFormatter;
import smartrics.rest.fitnesse.fixture.support.ContentType;
import smartrics.rest.fitnesse.fixture.support.HttpClientBuilder;

/**
 * Factory of all dependencies the rest fixture needs.
 * 
 * @author fabrizio
 * 
 */
public class PartsFactory {
    /**
     * Builds a rest client configured with the given config implementation.
     * 
     * @param config
     *            the configuration for the rest client to build
     * @return the rest client
     */
    public RestClient buildRestClient(Config config) {
        HttpClient httpClient = new HttpClientBuilder().createHttpClient(config);
        return new RestClientImpl(httpClient);
    }

    /**
     * Builds a empty rest request.
     * 
     * @return the rest request.
     */
    public RestRequest buildRestRequest() {
        return new RestRequest();
    }

    /**
     * Builds the appropriate formatter for a type of runner on this
     * RestFixture.
     * 
     * @param runner
     *            the runner used to execute this RestFixture
     * @return a formatter instance of CellFormatter
     */
    public CellFormatter<?> buildCellFormatter(Runner runner) {
        if (runner == null) {
            throw new IllegalArgumentException("Runner is null");
        }
        if (Runner.SLIM.equals(runner)) {
            return new SlimFormatter();
        }
        if (Runner.FIT.equals(runner)) {
            return new FitFormatter();
        }
        throw new IllegalStateException("Runner " + runner.name() + " not supported");
    }

    /**
     * returns a {@link smartrics.rest.fitnesse.fixture.support.BodyTypeAdapter}
     * for the content type in input.
     * 
     * @param ct
     *            the content type
     * @return the
     *         {@link smartrics.rest.fitnesse.fixture.support.BodyTypeAdapter}
     */
    public BodyTypeAdapter buildBodyTypeAdapter(ContentType ct) {
        return BodyTypeAdapterFactory.getBodyTypeAdapter(ct);
    }
}
