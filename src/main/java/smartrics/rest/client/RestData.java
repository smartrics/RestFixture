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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Base class for holding shared data between RestRequest and RestResponse
 */
public abstract class RestData {
	public final static String LINE_SEPARATOR = System.getProperty("line.separator");

	/**
	 * Holds the Http Header
	 */
	public static class Header{
		private String name;
		private String value;
		public Header(String name, String value){
			if(name==null||value==null)
				throw new IllegalArgumentException("Name or Value is null");
			this.name = name;
			this.value = value;
		}
		public String getName() {return name;}
		public String getValue() {return value;}
		@Override
		public int hashCode(){
			return getName().hashCode() + 37 * getValue().hashCode();
		}
		@Override
		public boolean equals(Object o){
			if(!(o instanceof Header))
				return false;
			Header h = (Header)o;
			return getName().equals(h.getName()) && getValue().equals(h.getValue());
		}
		@Override
		public String toString(){
			return String.format("%s:%s", getName(), getValue());
		}
	}

	private List<Header> headers = new ArrayList<Header>();
	private String body;
	private String resource;
	private Long transactionId;

	public String getBody() {
		return body;
	}

	public RestData setBody(String body) {
		this.body = body;
		return this;
	}

	public String getResource() {
		return resource;
	}

	public RestData setResource(String resource) {
		this.resource = resource;
		return this;
	}

	public RestData setTransactionId(Long txId){
		this.transactionId = txId;
		return this;
	}

	public Long getTransactionId(){
		return transactionId;
	}

	public List<Header> getHeaders() {
		return Collections.unmodifiableList(headers);
	}

	public Header getHeader(String name) {
		for(Header h : headers){
			if(h.getName().equalsIgnoreCase(name)){
				return h;
			}
		}
		return null;
	}

	public RestData addHeader(String name, String value){
		this.headers.add(new Header(name, value));
		return this;
	}

	public RestData addHeaders(Map<String, String> headers){
		for(Map.Entry<String, String> e : headers.entrySet()){
			addHeader(e.getKey(), e.getValue());
		}
		return this;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(Header h : getHeaders()){
			builder.append(h).append(LINE_SEPARATOR);
		}
		if(body!=null){
			builder.append(LINE_SEPARATOR);
			builder.append(this.getBody());
		} else {
			builder.append("[empty/null body]");
		}
		return builder.toString();
	}
}
