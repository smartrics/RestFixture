package smartrics.rest.client;

/**
 * Wraps a REST request/response object
 */
public class RestResponse extends RestData{
	private String statusText;
	private Integer statusCode;

	public Integer getStatusCode() {
		return statusCode;
	}

	public RestResponse setStatusCode(Integer sCode) {
		this.statusCode = sCode;
		return this;
	}

	public String getStatusText() {
		return statusText;
	}

	public RestResponse setStatusText(String st) {
		this.statusText = st;
		return this;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (getStatusCode() != null)
			builder.append(String.format("[%s] %s", this.getStatusCode(), this.getStatusText()));
		builder.append(LINE_SEPARATOR);
		builder.append(super.toString());
		return builder.toString();
	}

}
