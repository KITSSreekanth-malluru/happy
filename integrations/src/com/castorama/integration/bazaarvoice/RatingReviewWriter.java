package com.castorama.integration.bazaarvoice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.dom4j.dom.DOMDocument;
import org.dom4j.dom.DOMDocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

class RatingReviewWriter {

	private static final DecimalFormat ratingsFormat = new DecimalFormat();
	
	private int itemsPerFile = 20;
	
	private int itemsInFile = 0;
	
	private int fileSequence = 1;
	
	private DOMDocument document;
	
	private final File outputDir;

	
	static {
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		
		ratingsFormat.setDecimalFormatSymbols(dfs);
		ratingsFormat.setGroupingUsed(false);
		ratingsFormat.setMinimumFractionDigits(2);
		ratingsFormat.setMaximumFractionDigits(2);
		ratingsFormat.setMinimumIntegerDigits(1);
	}
	
	
	public RatingReviewWriter(File outputDir) {
		this.outputDir = outputDir;
	}
	

	public void write(RatingReview ratingElement) throws IOException {
		DOMDocument doc = getDocument();

		Element itm = doc.createElement("update-item");
		
		String extId = ratingElement.getExternalId(); 
		
		if (extId.startsWith(Const.PRODUCT_ID_PREFIX)){
			itm.setAttribute("item-descriptor", "product");
			
			extId = extId.substring(Const.PRODUCT_ID_PREFIX.length());			
		} else if (extId.startsWith(Const.DOCUMENT_ID_PREFIX)){
			itm.setAttribute("item-descriptor", "castoramaDocument");
			
			extId = extId.substring(Const.DOCUMENT_ID_PREFIX.length());			
		} else {
			// TODO log here
			return;
		}
		
		itm.setAttribute("id", extId);
		
		Element setItm = doc.createElement("set-property");
		setItm.setAttribute("name", "rating");

        Text text = doc.createTextNode(ratingsFormat.format(ratingElement.getAvgRating()));
        setItm.appendChild(text);
		
		Node el = doc.getFirstChild();
		itm.appendChild(setItm);		
		el.appendChild(itm);		
		
		itemsInFile ++;
	}

	public void close() throws IOException {
		if (null != document && itemsInFile > 0) {
			FileOutputStream fos = new FileOutputStream(new File(getOutputDir(), "" + (fileSequence ++) + ".xml"));
			fos.write(document.asXML().getBytes("UTF-8"));
			
			fos.close();
		}
			
		document = null;
		itemsInFile = 0;
	}
	
	private DOMDocument getDocument() throws IOException {
		if (itemsInFile >= itemsPerFile) {
			close();
		}
		
		if (null == document) {
			DOMDocumentType dt = new DOMDocumentType("gsa-template", "http://www.atg.com/dtds/gsa/gsa_1.0.dtd");
			
			document = new DOMDocument(dt);
			document.appendChild(document.createElement("gsa-template"));		
		}		
		
		return document;
	}

	public int getItemsPerFile() {
		return itemsPerFile;
	}

	public void setItemsPerFile(int itemsPerFile) {
		this.itemsPerFile = itemsPerFile;
	}

	public File getOutputDir() {
		return outputDir;
	}
}
