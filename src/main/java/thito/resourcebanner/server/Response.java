package thito.resourcebanner.server;

public enum Response {
	ACCEPTED(202, "Accepted"), ALREADY_REPORTED(208, "Already Reported (WebDAV; RFC 5842)"), BAD_GATEWAY(502,
			"Bad Gateway"),

	BAD_REQUEST(400, "Bad Request"), BANDWIDTH_LIMIT_EXCEEDED(509,
			"Bandwidth Limit Exceeded (Apache bw/limited extension)"), CONFLICT(409, "Conflict"), CONNECTION_TIMED_OUT(
					522, "Connection timed out"), CONTINUE(100, "Continue"), CREATED(201,
							"Created"), EXPECTATION_FAILED(417, "Expectation Failed"), FORBIDDEN(403,
									"Forbidden"), FOUND(302, "Found"), GATEWAY_TIMEOUT(504, "Gateway Timeout"),

	GONE(410, "Gone"), HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"), IM_USED(226,
			"IM Used (RFC 3229)"), INSUFFICIENT_STORAGE(507,
					"Insufficient Storage (WebDAV; RFC 4918)"), INTERNAL_SERVER_ERROR(500,
							"Internal Server Error"), LENGTH_REQUIRED(411, "Length Required"), LOOP_DETECTED(508,
									"Loop Detected (WebDAV; RFC 5842)"), METHOD_NOT_ALLOWED(405,
											"Method Not Allowed"), MOVED_PERMANENTLY(301, "Moved Permanently"),

	MULTI_STATUS(207, "Multi-Status (WebDAV; RFC 4918"), MULTIPLE_CHOICES(300,
			"Multiple Choices"), NETWORK_AUTHENTICATION_REQUIRED(511,
					"Network Authentication Required (RFC 6585)"), NO_CONTENT(204,
							"No Content"), NON_AUTHORITATIVE_INFORMATION(203,
									"Non-Authoritative Information"), NOT_ACCEPTABLE(406, "Not Acceptable"), NOT_EXTEND(
											510,
											"Not Extended (RFC 2774)"), NOT_FOUND(404, "Not Found"), NOT_IMPLEMENTED(
													501, "Not Implemented"), NOT_MODIFIED(304, "Not Modified"), OK(200,
															"OK"), OTHER(-1, "Other"), PARTIAL_CONTENT(206,
																	"Partial Content"), PAYMENT_REQUIRED(402,
																			"Payment Required"), PERMANENT_REDIRECT(308,
																					"Permanent Redirect (approved as experimental RFC)[12]"), PRECONDITION_FAILED(
																							412,
																							"Precondition Failed"), PROCESSING(
																									102,
																									"Processing"), PROXY_AUTHENTICATION_REQUIRED(
																											407,
																											"Proxy Authentication Required"),

	PROXY_DECLINED_REQUEST(523, "Proxy Declined Request"), REQUEST_ENTITY_TOO_LARGE(413,
			"Request Entity Too Large"), REQUEST_TIMEOUT(408, "Request Timeout"), REQUEST_URI_TOO_LONG(414,
					"Request-URI Too Long"), REQUESTED_RANGE_NOT_SATISFIABLE(416,
							"Requested Range Not Satisfiable"), RESET_CONTENT(205, "Reset Content"), SEE_OTHER(303,
									"See Other (since HTTP/1.1)"), SERVICE_UNAVAILABLE(503,
											"Service Unavailable"), SWITCH_PROXY(306,
													"Switch Proxy"), SWITCHING_PROTOCOL(101,
															"Switching Protocols"), TEMPORARY_REDIRECT(307,
																	"Temporary Redirect (since HTTP/1.1)"), TIMEOUT_OCCURRED(
																			524,
																			"A timeout occurred"), UNAUTHORIZED(401,
																					"Unauthorized"), UNSUPPORTED_MEDIA_TYPE(
																							415,
																							"Unsupported Media Type"), USE_PROXY(
																									305,
																									"Use Proxy (since HTTP/1.1)"), VARIANT_ALSO_NEGOTIATES(
																											506,
																											"Variant Also Negotiates (RFC 2295)");

	public static Response valueOf(int x) {
		for (final Response r : values()) {
			if (r.code == x) {
				return r;
			}
		}
		return OTHER;
	}

	private int code;
	private String desc;

	Response(int code, String desc) {
		this.code = code;
		this.desc = desc;

	}

	public boolean bad() {
		return code >= 400;
	}

	public int code() {
		return code;
	}

	public String info() {
		return desc;
	}

}
