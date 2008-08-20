package smartrics.rest.client;

/**
 * Wraps a REST request/response object
 */
public class RestRequest extends RestData{
	/**
	 * an http verb
	 */
	public enum Method {Get, Post, Put, Delete};
	private String query;
	private Method method;

	public Method getMethod() {
		return method;
	}

	public RestRequest setMethod(Method method) {
		this.method = method;
		return this;
	}

	public String getQuery() {
		return query;
	}

	public RestRequest setQuery(String query) {
		this.query = query;
		return this;
	}

	public boolean isValid(){
		return getMethod()!=null && getResource()!=null;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		if(getMethod()!=null)
			builder.append(getMethod().toString()).append(" ");
		if(getResource()!=null)
			builder.append(this.getResource());
		if(getQuery()!=null)
			builder.append("?").append(this.getQuery());
		builder.append(LINE_SEPARATOR);
		builder.append(super.toString());
		return builder.toString();
	}
}
