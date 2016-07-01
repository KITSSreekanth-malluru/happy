package com.castorama.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.sql.DataSource;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.pdf.PDFEncryption;

import atg.core.io.FileUtils;
import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.castorama.CastConfiguration;
import com.lowagie.text.pdf.PdfWriter;

/**
 * 
 * @author EPAM
 * 
 * helper class for operations with PDF
 *
 */
public class PDFTools extends GenericService{
	
	private String charset;
	  
    private Repository cgvInfoRepository;
    
    /** CGV_HTML constant */
    private static final String CGV_HTML = "CGV_HTML";
    
    /** CGV_INFO constant */
    private static final String CGV_INFO = "cgv_info";
    
    /** LAST_MODIFIED_DATE constant */
    private static final String LAST_MODIFIED_DATE = "lastModifiedDate";
    
    /** DATE_FORMAT constant */
    private static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";
    
    /** LAST_MODIFIED_PARAM constant */
    private static final String LAST_MODIFIED_PARAM = "Last-Modified";
    
    /** IF_MODIFIED_SINCE_PARAM constant */
    private static final String IF_MODIFIED_SINCE_PARAM = "If-Modified-Since";
    
    /** CGV_TEMPLATE_NAME constant */
    private static final String CGV_TEMPLATE_NAME = "cgv_template.pdf";
    
    private CastConfiguration castConfiguration;
    
    private int connectTimeoutHTMLSource;
    private int readTimeoutHTMLSource;
    
    private int connectTimeoutLastModifiedDate;
    private int readTimeoutLastModifiedDate;
    
	public int getConnectTimeoutLastModifiedDate() {
        return connectTimeoutLastModifiedDate;
    }

    public void setConnectTimeoutLastModifiedDate(int connectTimeoutLastModifiedDate) {
        this.connectTimeoutLastModifiedDate = connectTimeoutLastModifiedDate;
    }

    public int getReadTimeoutLastModifiedDate() {
        return readTimeoutLastModifiedDate;
    }

    public void setReadTimeoutLastModifiedDate(int readTimeoutLastModifiedDate) {
        this.readTimeoutLastModifiedDate = readTimeoutLastModifiedDate;
    }

    public int getConnectTimeoutHTMLSource() {
        return connectTimeoutHTMLSource;
    }

    public void setConnectTimeoutHTMLSource(int connectTimeoutHTMLSource) {
        this.connectTimeoutHTMLSource = connectTimeoutHTMLSource;
    }

    public int getReadTimeoutHTMLSource() {
        return readTimeoutHTMLSource;
    }

    public void setReadTimeoutHTMLSource(int readTimeoutHTMLSource) {
        this.readTimeoutHTMLSource = readTimeoutHTMLSource;
    }

    public CastConfiguration getCastConfiguration() {
		return castConfiguration;
	}

	public void setCastConfiguration(CastConfiguration castConfiguration) {
		this.castConfiguration = castConfiguration;
	}

	public Repository getCgvInfoRepository() {
		return cgvInfoRepository;
	}

	public void setCgvInfoRepository(Repository cgvInfoRepository) {
		this.cgvInfoRepository = cgvInfoRepository;
	}

	/**
	 * Read data from URL, convert data to PDF-format
	 * 
	 * @param url - source from this URL will be converted to PDF
	 * @param filepath - file with this path will be created
	 * @param baseUrl - baseURL for downloading source like css-files and images for generating PDF
	 */
	public boolean createFileFromURL(String url, String filepath, String baseUrl){
		boolean isCompleted = false;
		
		if(!StringUtils.isEmpty(url) && !StringUtils.isEmpty(filepath) && !StringUtils.isEmpty(baseUrl)){
			//if url is relative
			if(url.startsWith("/")){
				url = baseUrl + url;
			}
			String cleanedContent = cleanHtmlSourceFromUrl(url);
			if(null != cleanedContent){
				isCompleted = createPDFFromString(cleanedContent, filepath, baseUrl);
			}
		}
		return isCompleted;
	}
	
	/** 
	 * Read and clean up the HTML to be well formed 
	 * 
	 * @param url - source from this URL will be cleaned to be well formed
	 * */
	public String cleanHtmlSourceFromUrl(String url){
		
		ByteArrayOutputStream cleanedHtmlOutputStream = new ByteArrayOutputStream();

		// Clean up the HTML to be well formed
		HtmlCleaner cleaner = new HtmlCleaner();
		CleanerProperties props = cleaner.getProperties();
		String content = null;
		try {
			if(!StringUtils.isEmpty(url)){
				URL targetUrl = new URL(url);
				URLConnection connection = targetUrl.openConnection();
				connection.setConnectTimeout(getConnectTimeoutHTMLSource());
				connection.setReadTimeout(getReadTimeoutHTMLSource());
				TagNode node = cleaner.clean(connection.getInputStream());
				new PrettyXmlSerializer(props).writeToStream(node,
						cleanedHtmlOutputStream);
				content = new String(cleanedHtmlOutputStream.toByteArray(), getCharset());
			}
		} catch (IOException e) {
			if(isLoggingError()){
				logError("Unable to read or clean up source from URL:" + url, e);
			}
		} finally {
			try {
				cleanedHtmlOutputStream.close();
			} catch (IOException e) {
				if(isLoggingError()){
					logError("Unable to close outputStream:", e);
				}
			}			
		}
		return content;
	}
	/**
	 * 
	 * @param content - to be written to PDF file
	 * @param filepath 
	 * @param baseUrl - URL for downloading sources (like css-files and images) for generating PDF
	 */
	public boolean createPDFFromString(String content, String filepath, String baseUrl){
		ITextRenderer renderer = new ITextRenderer();
		ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
		BufferedOutputStream bufferedOutputStream = null;
		boolean status = false;
		try {
			if(!StringUtils.isEmpty(baseUrl) && !StringUtils.isEmpty(filepath) && !StringUtils.isEmpty(content)){
				renderer.setDocumentFromString(content, baseUrl);
				renderer.layout();
				PDFEncryption pdfEncryption = new PDFEncryption();
				pdfEncryption.setAllowedPrivileges(PdfWriter.ALLOW_PRINTING);
				pdfEncryption.setEncryptionType(PdfWriter.STANDARD_ENCRYPTION_128);
				renderer.setPDFEncryption(pdfEncryption);
				
				renderer.createPDF(pdfOutputStream);
				
				// Finishing up
				renderer.finishPDF();
				pdfOutputStream.flush();
		
				ByteArrayInputStream inputStream = new ByteArrayInputStream(
						pdfOutputStream.toByteArray());
		
				FileOutputStream fileOutputStream = new FileOutputStream(filepath);
				bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
				int readResult = inputStream.read();
				while (readResult != -1) {
					byte b = (byte) readResult;
					bufferedOutputStream.write(b);
					readResult = inputStream.read();
				}
		
				bufferedOutputStream.flush();
				status = true;
				if(isLoggingDebug()){
					logDebug("PDF was file successfully generated: " + filepath);
				}
			} else {
				if(isLoggingError()){
					logError("Some of input parameters are empty...");
				}
			}
		} catch (Exception e) {
			if(isLoggingError()){
				logError("Error while creating PDF file", e);
			}
		} finally {
			try {
				pdfOutputStream.close();
				if(bufferedOutputStream != null){
					bufferedOutputStream.close();
				}
			} catch (IOException e) {
				if(isLoggingError()){
					logError("Unable to close outputStream", e);
				}
			}
			
		}
		return status;
	}
	
	private String buildCGVTemplateFilePath() {
		String cgvTemplateDestFolder = castConfiguration.getCgvTemplateDestFolder();
        StringBuffer cgvTemplateFilePath = new StringBuffer();
        if(!StringUtils.isEmpty(cgvTemplateDestFolder)){
        	cgvTemplateFilePath.append(cgvTemplateDestFolder);    		
    		if(!cgvTemplateDestFolder.endsWith("/")){
    			cgvTemplateFilePath.append("/");
    		}
    	}
        cgvTemplateFilePath.append(CGV_TEMPLATE_NAME);
        return cgvTemplateFilePath.toString();
	}
	
	private boolean isCGVTemplateExist () {		
		String cgvTemplateFilePath = buildCGVTemplateFilePath();
		File templatePDF = new File (cgvTemplateFilePath);
		return templatePDF.exists();
	}
	
	private boolean generateCGVTemplatePDF () {
		
		boolean status = false;
		String baseURL = castConfiguration.getBaseURL();
		String cgvUrl = castConfiguration.getCgvPagePath();
        boolean isCGVTemplateExist = isCGVTemplateExist();
		String cgvTemplateFilePath = buildCGVTemplateFilePath();
        
		if(!isCGVTemplateExist) {
			//cgv_template.pdf doesn't exist
			status = createFileFromURL(cgvUrl, cgvTemplateFilePath, baseURL);
		} else {
			//compare last modified date of html file in db and apache
			try {
				Timestamp lastModifiedDateDB = (Timestamp)getCgvInfoRepository().getItem(CGV_HTML, CGV_INFO).getPropertyValue(LAST_MODIFIED_DATE);
				
				URL url = new URL(castConfiguration.getCgvPagePath());
				URLConnection urlc = url.openConnection();
				urlc.setConnectTimeout(getConnectTimeoutLastModifiedDate());
				urlc.setReadTimeout(getReadTimeoutLastModifiedDate());
				SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
				
				String lastModifiedDateString = new String();

				if(!StringUtils.isEmpty(urlc.getHeaderField(LAST_MODIFIED_PARAM))) {
					lastModifiedDateString = urlc.getHeaderField(LAST_MODIFIED_PARAM);
				} else if (!StringUtils.isEmpty(urlc.getHeaderField(IF_MODIFIED_SINCE_PARAM))) {
					lastModifiedDateString = urlc.getHeaderField(IF_MODIFIED_SINCE_PARAM);
				} 
				
				if(!StringUtils.isEmpty(lastModifiedDateString)) {
					
					Date lastModifiedDateApache = format.parse(lastModifiedDateString);
					Timestamp timestampApache = new Timestamp(lastModifiedDateApache.getTime());
					
					if (lastModifiedDateDB.before(timestampApache)) {
						createFileFromURL(cgvUrl, cgvTemplateFilePath, baseURL);
						
						RepositoryItem cgvInfo = getCgvInfoRepository().getItem(CGV_HTML, CGV_INFO);
						MutableRepository mutableRepository = (MutableRepository)cgvInfo.getRepository();
						MutableRepositoryItem mutableCgvInfo = mutableRepository.getItemForUpdate(cgvInfo.getRepositoryId(),
							cgvInfo.getItemDescriptor().getItemDescriptorName());
						mutableCgvInfo.setPropertyValue(LAST_MODIFIED_DATE, timestampApache);
						mutableRepository.updateItem(mutableCgvInfo);
					}
					status = true;
				} else {
					status = createFileFromURL(cgvUrl, cgvTemplateFilePath, baseURL);
				}
				
			} catch (RepositoryException e) {
				if(isLoggingError()){
					logError("Unable to find item (item-descriptor=CGV_INFO) with id = 'CGV_HTML' in newsletterRepository", e);
				}
			} catch (MalformedURLException e) {
				if(isLoggingError()){
					logError("Can't create  URL to CGV file", e);
				}
			} catch (IOException e) {
				if(isLoggingError()){
					logError("Unable to open connection to legal-notice.html", e);
				}
			} catch (ParseException e) {
				if(isLoggingError()){
					logError("Unable to parse last-modified header param", e);
				}
			}
		}
		return status;
	}
	
	public boolean writeCGVFile(String filepath) {
		
		boolean status = false;
		String cgvTemplateFilePath = buildCGVTemplateFilePath();
		if(generateCGVTemplatePDF()) {
			try {
				FileUtils.copyFile(cgvTemplateFilePath, filepath);
				status = true;
			} catch (IOException e) {
				if(isLoggingError()){
					logError("Unable to copy cgv pdf to temporary folder", e);
				}
			}	
		}
		return status;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

}
