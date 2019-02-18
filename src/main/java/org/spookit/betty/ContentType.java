package org.spookit.betty;

public enum ContentType {
	Unknown("file/unknown"), ApplicationJavaScript("application/javascript"), ApplicationJSON(
			"application/json"), ApplicationXWWWForm("application/x-www-form-urlencoded"), ApplicationXML(
					"application/xml"), ApplicationZIP("application/zip"), ApplicationPDF(
							"application/pdf"), ApplicationSQL("application/sql"), ApplicationGraphQL(
									"application/graphql"), ApplicationLDJSON("application/ld+json"), ApplicationMSWord(
											"application/msword (.doc)"), ApplicationMSWordDocument(
													"application/vnd.openxmlformats-officedocument.wordprocessingml.document(.docx)"), ApplicationMSExcel(
															"application/vnd.ms-excel (.xls)"), ApplicationMSExcelDocument(
																	"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet (.xlsx)"), ApplicationMSPowerPoint(
																			"application/vnd.ms-powerpoint (.ppt)"), ApplicationMSPowerPointDocument(
																					"application/vnd.openxmlformats-officedocument.presentationml.presentation (.pptx)"), ApplicationOpenDocumentText(
																							"application/vnd.oasis.opendocument.text (.odt)"), AudioMPEG(
																									"audio/mpeg"), AudioOGG(
																											"audio/ogg"), MultipartFormData(
																													"multipart/form-data"), TextCSS(
																															"text/css"), TextHTML(
																																	"text/html"), TextXML(
																																			"text/xml"), TextCSV(
																																					"text/csv"), TextPlain(
																																							"text/plain"), ImagePNG(
																																									"image/png"), ImageJPEG(
																																											"image/jpeg"), ImageGIF(
																																													"image/gif");
	String c;

	ContentType(String n) {
		c = n;
	}

	@Override
	public String toString() {
		return c;
	}
}
