package com.castorama.integration.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import atg.xml.XMLFileEntityResolver;
import atg.adapter.gsa.xml.TemplateParser;

/**
 * ImportFileParser File parser for startSQLRepository formatted files.
 */

public class ImportFileParser extends DefaultHandler implements ContentHandler, ErrorHandler {
	public static final int PHASE_ADD_UPDATE = 0;
	public static final int PHASE_REFERENCE_UPDATE = 1;
	public static final int PHASE_DELETE = 2;
	public static final int PHASE_ANALIZE = 999;
	private int mPhase;
	private File mImportFile;
	private SAXParser mParser = null;

	private ArrayList<ImportItem> mImportItemsArrayList = null;
	private int mAction;
	private String mItemDescriptor = null;
	private String mItemId = null;
	private String mRepositoryName = null;
	private Stack<String> mTagNames = new Stack<String>();
	private StringBuffer mPropertyValue = null;
	private HashMap<String, String> mProperties = null;
	private String mPropertyName = null;
	private String[] mItemsFilter;
	private int mStartIndex = -1;
	private int mBatchSize = -1;
	private String mEncoding = "UTF-8";
	/**
	 * Construct a new import file parser.
	 */

	public ImportFileParser(int pPhase, File pImportFile) {
		mPhase = pPhase;
		mImportFile = pImportFile;
	}

	public ImportFileParser(int pPhase, File pImportFile, String[] pItemsFilter) {
		mPhase = pPhase;
		mImportFile = pImportFile;
		mItemsFilter = pItemsFilter;
	}

	public ImportFileParser(int pPhase, File pImportFile, String[] pItemsFilter, int pStartIndex, int pBatchSize) {
		mPhase = pPhase;
		mImportFile = pImportFile;
		mItemsFilter = pItemsFilter;
		mStartIndex = pStartIndex;
		mBatchSize = pBatchSize;
	}

	public ImportItem[] parseFile() {
		// Variable declarations.

		SAXParserFactory factory = null;
		XMLReader reader = null;
		ImportItem[] importItems = null;

		// Create parser.

		try {
			factory = SAXParserFactory.newInstance();

			mParser = factory.newSAXParser();
			reader = mParser.getXMLReader();
			reader.setEntityResolver(new XMLFileEntityResolver());
			reader.setFeature("http://xml.org/sax/features/validation", false);
			reader.setContentHandler(this);
			reader.setErrorHandler(this);
		} catch (ParserConfigurationException e) {
			System.out.println("PARSER: ParserConfigurationException: " + e.getMessage());
			return (null);
		} catch (SAXException e) {
			System.out.println("PARSER: SAXException: " + e.getMessage());
			return (null);
		}

		// Create an input source and parse the string.

		mImportItemsArrayList = new ArrayList<ImportItem>();

		try {
			InputSource is = new InputSource(new FileInputStream(mImportFile));
			is.setEncoding(mEncoding);
			reader.parse(is);
		} catch (SAXException e) {
			if (!(e.getException() instanceof DoneException)) {
				System.out.println("PARSER: SAXException: " + e.getMessage());
				return null;
			} else {
				//System.out.println("DONE");
			}
		} catch (IOException e) {
			System.out.println("PARSER: IOException: " + e.getMessage());
			return (null);
		}

		// Create the array.

		importItems = new ImportItem[mImportItemsArrayList.size()];
		importItems = (ImportItem[]) mImportItemsArrayList.toArray(importItems);
		
		mImportItemsArrayList = null;

		return (importItems);
	}

	// ----------------------------
	// ContentHandler implementation
	// ----------------------------

	public void startElement(String namespaceURI, String localName, String qname, Attributes pAttributes) {
		mTagNames.push(qname);

		if (TemplateParser.TAG_ADD_ITEM.equals(qname)) {
			readTagAttributes(pAttributes);
			mAction = ImportItem.M_ACTION_ADD;
			if (mPhase != PHASE_ANALIZE) {
				mProperties = new HashMap<String, String>();
			}
			Integer count = descriptors.get(mItemDescriptor);
			if (count == null) {
				count = 0;
			}
			count++;

			descriptors.put(mItemDescriptor, count);
		} else if (TemplateParser.TAG_UPDATE_ITEM.equals(qname)) {
			readTagAttributes(pAttributes);
			mAction = ImportItem.M_ACTION_UPDATE;
			if (mPhase != PHASE_ANALIZE) {
				mProperties = new HashMap<String, String>();
			}
		} else if (TemplateParser.TAG_REMOVE_ITEM.equals(qname)) {
			readTagAttributes(pAttributes);
			mAction = ImportItem.M_ACTION_DELETE;
			if (mPhase != PHASE_ANALIZE) {
				mProperties = new HashMap<String, String>();
			}
		} else if (TemplateParser.TAG_SET_PROPERTY.equals(qname)) {
			String propertyName = pAttributes.getValue(TemplateParser.ATTR_NAME);
			mPropertyName = propertyName;
			mPropertyValue = new StringBuffer();
		}
	}

	private void readTagAttributes(Attributes pAttributes) {
		mItemDescriptor = pAttributes.getValue(TemplateParser.ATTR_ITEM_DESCRIPTOR);
		mItemId = pAttributes.getValue(TemplateParser.ATTR_ID);
		mRepositoryName = pAttributes.getValue(TemplateParser.ATTR_REPOSITORY);
	}

	// ----------------------------
	/**
	 * Reads characters between tags <foo>characters</foo> For startSQLRepository, this is typically a property value
	 * Note that values with a comma are actually multi values
	 */
	public void characters(char[] chars, int start, int length) {
		if (TemplateParser.TAG_SET_PROPERTY.equals(mTagNames.peek())) {
			mPropertyValue.append(new String(chars, start, length));
		}
	}

	// -----------------------------
	/**
	 * Called when a tag ends This method cleans up state between tags
	 */
	Map<String, Integer> descriptors = new HashMap<String, Integer>();
	int counter = 0;

	public void endElement(String namespaceURI, String localName, String pQName) throws SAXException{

		if (mItemsFilter != null && !Arrays.asList(mItemsFilter).contains(mItemDescriptor)) {
			return;
		}

		if (TemplateParser.TAG_ADD_ITEM.equals(pQName) && mStartIndex != -1) {

			if (counter >= mStartIndex && counter < (mStartIndex + mBatchSize)) {
				counter++;
			} else {
				counter++;
				return;
			}
		}

		if (TemplateParser.TAG_ADD_ITEM.equals(pQName)) {
			if (mPhase != PHASE_DELETE) {
				addImportItem();
			}
		} else if (TemplateParser.TAG_UPDATE_ITEM.equals(pQName)) {
			if (mPhase != PHASE_DELETE) {
				addImportItem();
			}
		} else if (TemplateParser.TAG_REMOVE_ITEM.equals(pQName)) {
			if (mPhase == PHASE_DELETE) {
				addImportItem();
			}
		} else if (TemplateParser.TAG_SET_PROPERTY.equals(pQName)) {
			if (mPhase != PHASE_ANALIZE) {
				mProperties.put(mPropertyName, mPropertyValue.toString());
			}			
			mPropertyName = null;
		}
		if (mBatchSize > 0 && mImportItemsArrayList.size() == mBatchSize) {
			throw new SAXException(new DoneException());
		}
		mTagNames.pop();
	}

	// ----------------------------
	/**
	 * Called when we start parsing this file
	 */
	public void startDocument() {
	}

	// ----------------------------
	/**
	 * Called when we are done parsing this file
	 */
	public void endDocument() {
	}

	HashMap<String, Integer> size = new HashMap<String, Integer>();

	private void addImportItem() {

		ImportItem importItem = new ImportItem();

		importItem.setAction(mAction);
		importItem.setItemDescriptor(mItemDescriptor);
		importItem.setItemId(mItemId);
		importItem.setRepositoryName(mRepositoryName);
		if (mPhase != PHASE_ANALIZE) {
			importItem.setProperties(mProperties);
		}
		

		mImportItemsArrayList.add(importItem);
		
		//if ((mImportItemsArrayList.size() % 5000) == 0) {
		//	System.out.println(mImportItemsArrayList.size());
		//}
	}

	// ----------------------------
	// ErrorHandler implementation
	// ----------------------------

	public void error(SAXParseException e) {
		System.out.println("ERROR: " + e.getMessage());
	}

	public void fatalError(SAXParseException e) {
		System.out.println("FATAL ERROR: " + e.getMessage());
	}

	public void warning(SAXParseException e) {
		System.out.println("WARNING: " + e.getMessage());
	}

	public String getEncoding() {
		return mEncoding;
	}

	public void setEncoding(String pEncoding) {
		mEncoding = pEncoding;
	}
}
