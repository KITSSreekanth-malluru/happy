package com.castorama.integration.bazaarvoice;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import atg.core.util.StringUtils;

class DataFeedWriter {
	
	/**
	 * 
	 */
	private static DateFormat outFileFormat = new SimpleDateFormat("yyyyMMdd_HH-mm-ss");
	
	private static String ENCODING = "UTF-8";
	
	private String currentGroupElement = null;
	
	private OutputStream outputStream;
	
	private final File outboxFolder;

	private File resultFile;

	public DataFeedWriter(File outboxFolder) {
		this.outboxFolder = outboxFolder;
	}

	/**
	 * @return the outboxFolder
	 */
	public File getOutboxFolder() {
		return outboxFolder;
	}

	public void write(CategoryProductFeed category) throws IOException {
		setCurrentGroupElement("Categories");
		
		openElement("Category");
		
		oneElement("ExternalId", category.getExternalId());
		oneElement("ParentExternalId", category.getParentExternalId());
		oneElement("Name", category.getName());
		oneElement("CategoryPageUrl", category.getCategoryPageUrl());
		
		closeElement("Category");
	}

	public void write(ProductFeed product) throws IOException {
		if (null == product.getExternalId() || product.getExternalId().length() == 0) {
			throw new NullPointerException("External ID is null");
		}
		
		if (null == product.getCategoryExternalId() || product.getCategoryExternalId().length() == 0) {
			throw new NullPointerException(product.getExternalId() + ": category ID is: " + product.getCategoryExternalId());
		}
		
		setCurrentGroupElement("Products");

		openElement("Product");
		
		if (null != product.getExternalId() && !"".equals(product.getExternalId())) {
			if (product.isDocument()) {
				oneElement("ExternalId", Const.DOCUMENT_ID_PREFIX + product.getExternalId());
			} else {
				oneElement("ExternalId", Const.PRODUCT_ID_PREFIX + product.getExternalId());
			}
		}
		
		oneElement("Name", product.getName());
		
		if (null == product.getDescription() || "".equals(product.getDescription())) {
			oneElement("Description", product.getName());
		} else {
			oneElement("Description", product.getDescription());
		}
		
		if (null != product.getBrand() || !"".equals(product.getBrand())) {
			openElement("Brand");
			oneElement("Name", product.getBrand());
			closeElement("Brand");
		}
		
		oneElement("CategoryExternalId", product.getCategoryExternalId());
		oneElement("ProductPageUrl", product.getProductPageUrl());
		oneImgElement("ImageUrl", product.getImageUrl());
		oneElement("ModelNumber", product.getModelNumber());
		oneElement("EAN", product.getEAN());
		oneElement("ManufacturerPartNumber", product.getManufacturerPartNumber());
		oneElement("UPC", product.getUPC());
		oneElement("ISBN", product.getISBN());
		
		closeElement("Product");
	}

	public void close() throws IOException {
		if (null != outputStream) {
			if (null != getCurrentGroupElement()) {
				closeGroupElement();
				
				this.currentGroupElement = null;
			}

			outputStream.write(("</Feed>\n").getBytes(ENCODING));
			
			outputStream.flush();
			outputStream.close();
			
			outputStream = null;
		}
	}

	/**
	 * @return the currentGroupElement
	 */
	public String getCurrentGroupElement() {
		return currentGroupElement;
	}

	/**
	 * @param currentGroupElement the currentGroupElement to set
	 * @throws IOException 
	 */
	public void setCurrentGroupElement(String currentGroupElement) throws IOException {
		if (currentGroupElement.equals(this.currentGroupElement)){
			return;
		}
		
		if (null != this.currentGroupElement) {
			closeGroupElement();
		}
		
		openElement(currentGroupElement);
		
		this.currentGroupElement = currentGroupElement;
	}

	private void oneElement(String element, String value) throws IOException {
		if (null == value || "".equals(value)) {
			return;
		}
		
		OutputStream os = getOutputStream();
		
		value = value.replaceAll("&", "&amp;");
		value = value.replaceAll("<", "&lt;");
		value = value.replaceAll(">", "&gt;");
		
		os.write(("<" + element + ">" + value + "</" + element + ">\n").getBytes(ENCODING));
	}

	private void oneImgElement(String element, String value) throws IOException {
		if (StringUtils.isBlank(value)) {
			value = "";
		}
		
		OutputStream os = getOutputStream();
		
		value = value.replaceAll("&", "&amp;");
		value = value.replaceAll("<", "&lt;");
		value = value.replaceAll(">", "&gt;");
		
		os.write(("<" + element + ">" + value + "</" + element + ">\n").getBytes(ENCODING));
	}

	private void openElement(String element) throws IOException {
		OutputStream os = getOutputStream();
		os.write(("<" + element + ">\n").getBytes(ENCODING));
	}

	private void closeElement(String element) throws IOException {
		OutputStream os = getOutputStream();
		os.write(("</" + element + ">\n").getBytes(ENCODING));
	}

	private void closeGroupElement() throws IOException {
		closeElement(currentGroupElement);
	}

	public OutputStream getOutputStream() throws IOException {
		if (null == outputStream) {
			setResultFile(new File(outboxFolder, "DataFeed_" + outFileFormat.format(new Date()) + ".xml" ));
			
			outputStream = new BufferedOutputStream(new FileOutputStream(getResultFile()));
			
			outputStream.write(("<?xml version=\"1.0\" encoding=\""+ ENCODING + "\"?>\n").getBytes(ENCODING));
			outputStream.write(("<Feed  xmlns=\"http://www.bazaarvoice.com/xs/PRR/ProductFeed/3.7\" \n").getBytes(ENCODING));
			outputStream.write(("name=\"CASTORAMA\"\n").getBytes(ENCODING));
			outputStream.write(("incremental=\"false\"\n").getBytes(ENCODING));
			
			DateFormat extractDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			DateFormat extractTimeFormat = new SimpleDateFormat("HH:mm:ss.SSSS");
			
			outputStream.write(("extractDate=\"" + extractDateFormat.format(new Date()) + 'T' + 
					extractTimeFormat.format(new Date()) + "\">\n").getBytes(ENCODING));
			
			this.currentGroupElement = null;
		}
		
		return outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	/**
	 * @return the resultFile
	 */
	public File getResultFile() {
		return resultFile;
	}

	/**
	 * @param resultFile the resultFile to set
	 */
	public void setResultFile(File resultFile) {
		this.resultFile = resultFile;
	}
	
}
