package smartrics.rest.fitnesse.fixture;

import org.apache.commons.httpclient.HttpClient;

import smartrics.rest.client.RestClient;
import smartrics.rest.client.RestClientImpl;
import smartrics.rest.client.RestRequest;
import smartrics.rest.config.Config;
import smartrics.rest.fitnesse.fixture.RestFixture.Runner;
import smartrics.rest.fitnesse.fixture.support.HttpClientBuilder;

/**
 * Factory of all dependencies the rest fixture needs
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
        throw new IllegalStateException("Runner " + runner.name() + " not supprted");
    }

}
