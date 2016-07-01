package com.castorama.utils;

import atg.nucleus.GenericService;

public class PDFGenerator extends GenericService {

	private static final String FILE_EXTENSION = ".pdf";
	private static final String FILE_SEPARATOR_PROPERTY = "file.separator";

	private PDFTools pdfTools;
	private String baseUrl;
	private String destinationFolder;
	private String filename;
	private String url;

	public void generatePDF() {
		String fullPathToFile = buildFilepath();
		pdfTools.createFileFromURL(url, fullPathToFile, baseUrl);
	}

	private String buildFilepath() {
		StringBuffer sb = new StringBuffer();
		sb.append(getDestinationFolder())
				.append(System.getProperty(FILE_SEPARATOR_PROPERTY))
				.append(getFilename()).append(FILE_EXTENSION);
		return sb.toString();
	}

	public PDFTools getPdfTools() {
		return pdfTools;
	}

	public void setPdfTools(PDFTools pdfTool) {
		this.pdfTools = pdfTool;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getDestinationFolder() {
		return destinationFolder;
	}

	public void setDestinationFolder(String destinationFolder) {
		this.destinationFolder = destinationFolder;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
