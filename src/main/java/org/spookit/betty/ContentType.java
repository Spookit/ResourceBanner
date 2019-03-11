package org.spookit.betty;

public enum ContentType {
	ApplicationGraphQL("application/graphql"), ApplicationJavaScript("application/javascript"), ApplicationJSON(
			"application/json"), ApplicationLDJSON("application/ld+json"), ApplicationMSExcel(
					"application/vnd.ms-excel (.xls)"), ApplicationMSExcelDocument(
							"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet (.xlsx)"), ApplicationMSPowerPoint(
									"application/vnd.ms-powerpoint (.ppt)"), ApplicationMSPowerPointDocument(
											"application/vnd.openxmlformats-officedocument.presentationml.presentation (.pptx)"), ApplicationMSWord(
													"application/msword (.doc)"), ApplicationMSWordDocument(
															"application/vnd.openxmlformats-officedocument.wordprocessingml.document(.docx)"), ApplicationOpenDocumentText(
																	"application/vnd.oasis.opendocument.text (.odt)"), ApplicationPDF(
																			"application/pdf"), ApplicationSQL(
																					"application/sql"), ApplicationXML(
																							"application/xml"), ApplicationXWWWForm(
																									"application/x-www-form-urlencoded"), ApplicationZIP(
																											"application/zip"), AudioMPEG(
																													"audio/mpeg"), AudioOGG(
																															"audio/ogg"), ImageGIF(
																																	"image/gif"), ImageJPEG(
																																			"image/jpeg"), ImagePNG(
																																					"image/png"), MultipartFormData(
																																							"multipart/form-data"), TextCSS(
																																									"text/css"), TextCSV(
																																											"text/csv"), TextHTML(
																																													"text/html"), TextPlain(
																																															"text/plain"), TextXML(
																																																	"text/xml"), Unknown(
																																																			"file/unknown");
	String c;

	ContentType(String n) {
		c = n;
	}

	@Override
	public String toString() {
		return c;
	}
}
