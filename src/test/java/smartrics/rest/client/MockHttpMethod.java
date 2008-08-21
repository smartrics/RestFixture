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
package smartrics.rest.client;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethodBase;

import static org.junit.Assert.assertTrue;

public class MockHttpMethod extends HttpMethodBase{

	private String name;
	private int statusCode;
	private boolean connectionReleased = false;

	public MockHttpMethod(String name){
		this.name=name;
	}

	public int getStatusCode(){
		return statusCode;
	}

	public String getStatusText(){
		return "status text";
	}

	public String getResponseBodyAsString(){
		return "response";
	}

	public void setStatusCode(int rc){
		this.statusCode = rc;
	}

	public Header[] getResponseHeaders(){
		Header h1 = new Header("name1", "value1");
		Header h2 = new Header("name1", "value1");
		return new Header[]{h1, h2};
	}
	public String getName(){
		return name;
	}

	@Override
	public void releaseConnection() {
		connectionReleased = true;
	}

	public void verifyConnectionReleased(){
		assertTrue("connection not released on mock http method", connectionReleased);
	}

}
