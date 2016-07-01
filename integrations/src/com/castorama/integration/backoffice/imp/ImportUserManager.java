package com.castorama.integration.backoffice.imp;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.transaction.SystemException;

import atg.adapter.gsa.xml.TemplateParser;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.xml.tools.DefaultXMLToolsFactory;
import atg.xml.tools.XMLToDOMParser;
import atg.xml.tools.XMLToolsFactory;

import com.castorama.integration.Constants;
import com.castorama.integration.IntegrationBase;
import com.castorama.integration.xml.ImportFileParser;
import com.castorama.integration.xml.ImportItem;

/**
 * Import User Manager
 * Imports users.
 * @author Vasili_Ivus
 *
 */
public class ImportUserManager extends IntegrationBase {

	/**
	 * Default transaction timeout
	 */
	private static final int DEFAULT_TRANSACTION_TIMEOUT = 1000;
	
	/**
	 * List of files to import
	 */
	private String[] mImportFilesList;
	
	/**
	 * Chunk size
	 */
	private Integer mChunkSize;
	
	/**
	 * the no-argument constructor
	 */
	public ImportUserManager() {}
	
	/**
	 * try to import users
	 */
	public void importUsers() {
		long currentTimeMillis = System.currentTimeMillis();
		if (isLoggingDebug()) {
			logDebug("start - ImportUserManager - importUsers");
		}

		try {
			File rootDir = getRootFolder();
			File workingDir = getWorkingDir();
			checkMandatoryFolders();
			checkProperties();

			PrintWriter logWriter;
			String[] importFilesList = getImportFilesList();
			if ( null != importFilesList ) {
				for (int i = 0; i < importFilesList.length; i++) {
					File importFile = new File(rootDir, Constants.INPUT_FOLDER + "/" + importFilesList[i]);
					if ( importFile.exists() && importFile.isFile() ) {
						File workingFile = getWorkingFile(importFile);
						if (isLoggingDebug()) {
							logDebug("processing: " + workingFile);
						}
						if (workingFile == null) {
							continue;
						}
						File statusFile = new File(workingDir, getFileName(workingFile) + ".log");
						logWriter = new PrintWriter(statusFile);
						logWriter.println("Data file: " + workingFile.getName());
						
						List<String> tmpNames = parseXml(workingFile, null);
						if (tmpNames == null || tmpNames.isEmpty()) {
							if (isLoggingDebug()) {
								logDebug("Temp Files not found. ");
							}
							logWriter.close();
							continue;
						}
						String itemTmpName = tmpNames.get(0);
						ImportItem[] impItems = getImportItems(workingDir, itemTmpName);
						int chunkSize = getChunkSize(); 
						if ( 0 < chunkSize ) {
							for (int start = 0, end = chunkSize; end < impItems.length; start += chunkSize, end += chunkSize) {
								ImportItem[] chunk = new ImportItem[chunkSize];
								System.arraycopy(impItems, start, chunk, 0, chunkSize);
								uploadXml(generateImportXml(chunk), logWriter);
							}
						} else {
							uploadXml(generateImportXml(impItems), logWriter);
						}
						removeTmpFile(workingDir, tmpNames);
					} else {
						if ( isLoggingDebug() ) {
							logDebug("File " + importFilesList[i] + "doesn't exist.");
						}
					}
						
				}
			}
		} catch (Exception e) {
			if (isLoggingError()) {
				logError("Exception occured: ", e);
			}
		}
		if (isLoggingDebug()) {
			logDebug("finish - ImportItemManager - uploadFiles");
		}
		logWarning("import users complete : " + (System.currentTimeMillis() - currentTimeMillis));
	}
	
	/**
	 * Parse import file and gets import items array
	 * @param workingDir working directory
	 * @param importFileName import file name
	 * @return import items array
	 */
	private ImportItem[] getImportItems(File workingDir, String importFileName) {
		if (isLoggingDebug()) {
			logDebug("XML file: " + importFileName);
		}
		
		ImportFileParser parser = new ImportFileParser(ImportFileParser.PHASE_ADD_UPDATE, 
				new File(workingDir, importFileName));
		return parser.parseFile();
	}
	
	/**
	 * Removes temporary files
	 * @param workingDir working directory
	 * @param tmpFileNames list temporary file names
	 */
	private void removeTmpFile(File workingDir, List<String> tmpFileNames) {
		for (String tmpName : tmpFileNames) {
			File tmp = new File(workingDir, tmpName);
			if (tmp.exists()) {
				tmp.delete();
			}
		}
	}

	/**
	 * Generates xml to import by import items array
	 * Right encoding, replacing specific data. 
	 * @param importItems import items array
	 * @return file name of import file
	 * @throws Exception
	 */
	private InputStream generateImportXml(ImportItem[] importItems) throws Exception {
		InputStream result;
		StringBuilder sb = new StringBuilder(1024);
		try {
			sb.append(XML_HEADER);
			if ( null != importItems ) {
				for(ImportItem item : importItems) {
					String repository = item.getRepositoryName();
					sb.append("<add-item item-descriptor=\"").append(item.getItemDescriptor()).append("\"");
					if ( null != repository && 0 < repository.trim().length() ) {
						sb.append(" repository=\"").append(item.getRepositoryName()).append("\"");
					}
					sb.append(" id=\"").append(item.getItemId()).append("\" >\n");
					HashMap<String, String> properties = item.getProperties();
					for (Iterator<String> it = properties.keySet().iterator(); it.hasNext(); ) {
						String key = it.next();
						sb.append("<set-property name=\"").append(key).append("\"><![CDATA[").append(properties.get(key)).append("]]></set-property>\n");
					}
					sb.append("</add-item>\n");
				}
			}
			sb.append("</" + TemplateParser.TAG_IMPORT_ITEMS + ">\n");
			sb.append(TemplateParser.XML_FILE_FOOTER);
			result = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
		} finally {
		}
		return result;
	}

	/**
	 * upload import file
	 * @param tmpFile import file name
	 * @param log log writer
	 * @return true when imports ok, false otherwise
	 */
	private boolean uploadXml(InputStream importStream, PrintWriter log) {
		boolean result = false;
		if ( null != importStream ) {
			if (isLoggingDebug()) {
				logDebug("start - uploadXml");
			}
	
			XMLToolsFactory factory = DefaultXMLToolsFactory.getInstance();
			XMLToDOMParser domParser = getXMLToDOMParser(factory);
	
			boolean rollback = true;
			TransactionDemarcation trd = new TransactionDemarcation();
			try {
				
				try {
					getTransactionManager().setTransactionTimeout(getTransactionTimeout());
					trd.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);
	
					TemplateParser.addToTemplate(importStream, null, domParser,
							(atg.adapter.gsa.GSARepository) getRepository(), TemplateParser.PARSE_NORMAL,
							false, new PrintWriter(System.out), null);
	
					rollback = false;
					result = true;
				} catch (Exception e) {
					if (isLoggingError()) {
						logError("Exception occured trying to add/update items", e);
					}
				} finally {
					try {
						trd.end(rollback);
					} catch (TransactionDemarcationException e) {
						logError("TransactionDemarcationException occured: ", e);
					}
					getTransactionManager().setTransactionTimeout(0);
				}
	
			} catch (SystemException e) {
				if (isLoggingError()) {
					logError("setTransactionTimeout failed. ", e);
				}
			} catch (Exception e) {
				if (isLoggingError()) {
					logError("Exception occured: ", e);
				}
			}
			if (isLoggingDebug()) {
				logDebug("finish -  uploadXml");
			}
		}
		return result;
	}
	

	private void checkProperties() {
		if (getTransactionTimeout() == null) {
			setTransactionTimeout(DEFAULT_TRANSACTION_TIMEOUT);
		}

		if (isLoggingDebug()) {
			logDebug("transactionTimeout=" + getTransactionTimeout());
		}
	}
	
	protected List<String> parseXml(File workingFile, String prefixTmpFile) throws Exception {
		if (workingFile == null) {
			if (isLoggingDebug()) {
				logDebug("Source file is null.");
			}
			return null;
		}

		List<String> tmpFileNames = new ArrayList<String>();
		BufferedReader reader = null;
		File tmpFile = null;
		PrintWriter writer = null;

		int cnt = 10000;
		try {

			reader = new BufferedReader(new FileReader(workingFile));
			String line;
			boolean isItem = false;
			String xmlId = "";
			while ((line = reader.readLine()) != null) {
				if (line.indexOf("<XML") != -1) {
					xmlId = getXmlId(line);
				} else if (line.indexOf(TAG_GSA_TEMPLATE) != -1) {
					// create file
					cnt++;
					if (xmlId.length() > 0) {
						xmlId = xmlId + "_";
					}
					String tmpName = xmlId + prefixTmpFile + Integer.toString(cnt) + "_"
							+ Constants.DATE_FORMAT_ARCHIVE.format(new Date()) + ".xml";

					tmpFile = new File(workingFile.getParentFile(), tmpName);

					tmpFileNames.add(tmpName);

					writer = new PrintWriter(tmpFile, "UTF-8");
					writer.write(XML_HEADER);
					isItem = false;
				} else if (line.indexOf(END_TAG_GSA_TEMPLATE) != -1) {
					// close file
					writer.println(XML_FOOTER);
					writer.flush();
					writer.close();
					xmlId = "";
					isItem = false;
				} else if (line.indexOf(TemplateParser.TAG_ADD_ITEM) != -1) {
					isItem = true;
				}

				if (isItem) {
					//line = line.replaceAll(SING_NULL, TemplateParser.NEW_LINE);
					//writer.println(line);
					line = line.replaceAll("\\[Monsieur\\]", "[mr]");
					line = line.replaceAll("\\[Mlle\\]", "[miss]");
					line = line.replaceAll("\\[Madame\\]", "[mrs]");
					line = line.replaceAll(SING_NULL, "");
					writer.write(line);
				}

				if (line.indexOf(END_TAG_ADD_ITEM) != -1) {
					isItem = false;
				}
			}
		} finally {
			if (reader != null) {
				reader.close();
			}

			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}

		return tmpFileNames;
	}

    private String getXmlId (String line) {
    	if (line.indexOf("Id") != -1) {
            String mLine = line.substring(0, line.indexOf("Repository"));
            return mLine.substring(mLine.indexOf("\"")+1,mLine.lastIndexOf("\""));
    	}
    	return "";
    }

    /**
     * Returns list of files to import
     * @return list of files to import
     */
	public String[] getImportFilesList() {
		return mImportFilesList;
	}

	/**
     * Returns list of files to import
	 * @param pImportFileList list of files to import
	 */
	public void setImportFilesList(String[] pImportFilesList) {
		this.mImportFilesList = pImportFilesList;
	}

	/**
	 * Returns chunk size
	 * @return chunk size
	 */
	public Integer getChunkSize() {
		return mChunkSize;
	}

	/**
	 * Sets chunk size
	 * @param pChunkSize chunk size
	 */
	public void setChunkSize(Integer pChunkSize) {
		this.mChunkSize = pChunkSize;
	}

}
