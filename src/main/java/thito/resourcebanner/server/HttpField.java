package thito.resourcebanner.server;

public enum HttpField {
	Accept("Accept"), AcceptCharset("Accept-Charset"), AcceptEncoding("Accept-Encoding"), AcceptLanguage(
			"Accept-Language"), AcceptRanges("Accept-Ranges"), Age("Age"), Allow("Allow"), Authorization(
					"Authorization"), CacheControl("Cache-Control"), Connection("Connection"), ContentEncoding(
							"Content-Encoding"), ContentLanguage("Content-Language"), ContentLength(
									"Content-Length"), ContentLocation("Content-Location"), ContentRange(
											"Content-Range"), ContentType("Content-Type"), Date("Date"), ETag(
													"ETag"), Expect("Expect"), Expires("Expires"), From("From"), Host(
															"Host"), IfMatch("If-Match"), IfModifiedSince(
																	"If-Modified-Since"), IfNoneMatch(
																			"If-None-Match"), IfRange(
																					"If-Range"), IfUnmodifiedSince(
																							"If-Unmodified-Since"), LastModified(
																									"Last-Modified"), Location(
																											"Location"), MaxForwards(
																													"Max-Forwards"), Pragma(
																															"Pragma"), ProxyAuthenticate(
																																	"Proxy-Authenticate"), ProxyAuthorization(
																																			"Proxy-Authorization"), Range(
																																					"Range"), Referer(
																																							"Referer"), RetryAfter(
																																									"Retry-After"), Server(
																																											"Server"), TE(
																																													"TE"), Trailer(
																																															"Trailer"), TransferEncoding(
																																																	"Transfer-Encoding"), Upgrade(
																																																			"Upgrade"), UserAgent(
																																																					"User-Agent"), Vary(
																																																							"Vary"), Via(
																																																									"Via"), Warning(
																																																											"Warning"), WwwAuthenticate(
																																																													"WWW-Authenticate");
	String x;

	HttpField(String n) {
		x = n;
	}

	@Override
	public String toString() {
		return x;
	}
}