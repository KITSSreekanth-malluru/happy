package com.castorama.repository.search.indexing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import java.rmi.RemoteException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import atg.adapter.gsa.ChangeAwareList;
import atg.core.net.URLEscaper;
import atg.core.net.URLEscaperISO88591;

import atg.core.util.StringUtils;

import atg.nucleus.GenericRMIService;
import atg.nucleus.ServiceException;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

import atg.repository.rql.RqlStatement;

import atg.search.routing.command.indexing.Metadata;

/**
 * Implementation of catalog document scanner interface, for collecting
 * castorama document items.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class RemoteCatalogDocumentScannerImpl extends GenericRMIService implements RemoteCatalogDocumentScanner {
    /** protocol property */
    private String mProtocol;

    /** host property */
    private String mHost;

    /** port property */
    private String mPort;

    /** fileHostPath property */
    private String mFileHostPath;

    /** repository property */
    private Repository mRepository;

    /** itemDescriptorName property */
    private String mItemDescriptorName = "castoramaDocument";

    /**
     * Constructor
     *
     * @throws RemoteException exception
     */
    public RemoteCatalogDocumentScannerImpl() throws RemoteException {
    }

    /**
     * Returns protocol property.
     *
     * @return protocol property.
     */
    public String getProtocol() {
        return mProtocol;
    }

    /**
     * Sets the value of the protocol property.
     *
     * @param pProtocol parameter to set.
     */
    public void setProtocol(String pProtocol) {
        mProtocol = pProtocol;
    }

    /**
     * Returns host property.
     *
     * @return host property.
     */
    public String getHost() {
        return mHost;
    }

    /**
     * Sets the value of the host property.
     *
     * @param pHost parameter to set.
     */
    public void setHost(String pHost) {
        mHost = pHost;
    }

    /**
     * Returns port property.
     *
     * @return port property.
     */
    public String getPort() {
        return mPort;
    }

    /**
     * Sets the value of the port property.
     *
     * @param pPort parameter to set.
     */
    public void setPort(String pPort) {
        mPort = pPort;
    }

    /**
     * Returns repository property.
     *
     * @return repository property.
     */
    public Repository getRepository() {
        return mRepository;
    }

    /**
     * Sets the value of the repository property.
     *
     * @param pRepository parameter to set.
     */
    public void setRepository(Repository pRepository) {
        mRepository = pRepository;
    }

    /**
     * Returns itemDescriptorName property.
     *
     * @return itemDescriptorName property.
     */
    public String getItemDescriptorName() {
        return mItemDescriptorName;
    }

    /**
     * Sets the value of the itemDescriptorName property.
     *
     * @param pItemDescriptorName parameter to set.
     */
    public void setItemDescriptorName(String pItemDescriptorName) {
        mItemDescriptorName = pItemDescriptorName;
    }

    /**
     * Returns fileHostPath property.
     *
     * @return fileHostPath property.
     */
    public String getFileHostPath() {
        return mFileHostPath;
    }

    /**
     * Sets the value of the fileHostPath property.
     *
     * @param pFileHostPath parameter to set.
     */
    public void setFileHostPath(String pFileHostPath) {
        mFileHostPath = pFileHostPath;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.nucleus.GenericRMIService#doStartService()
     */
    @Override public void doStartService() throws ServiceException {
        if ((StringUtils.isBlank(getProtocol()) || StringUtils.isBlank(getHost()) || StringUtils.isBlank(getPort())) &&
                StringUtils.isBlank(getFileHostPath())) {
            if (isLoggingError()) {
                logError("RemoteCatalogDocumentScanner component isn't properly configured, please set fileHostPath or (protocol && host && port)");
            }
        }
        super.doStartService();
    }

    /**
     * Returns list of CastoramaDocument items for indexing.
     *
     * @return list of CastoramaDocument items for indexing
     *
     * @throws RemoteException exception
     */
    public ArrayList<CastoramaDocument> getDocumentsForIndexing() throws RemoteException {
        String path = getConvertedPath();
        if (!StringUtils.isBlank(path)) {
            // String fileHostPath = getFileHostPath();
            Repository rep = getRepository();
            RepositoryItem[] docItems = null;
            HashMap<String, CastoramaDocument> docLists = new HashMap<String,CastoramaDocument>();
            try {
                if ((rep != null) && (getItemDescriptorName() != null)) {
                    RepositoryView repView = rep.getView(getItemDescriptorName());
                    if (repView != null) {
                        RqlStatement statement = RqlStatement.parseRqlStatement("ALL");
                        docItems = statement.executeQuery(repView, null);
                    }

                }
            } catch (RepositoryException e) {
                if (isLoggingError()) {
                    logError("com.castorama.repository.search.indexing.RemoteCatalogDocumentScannerImpl " +
                             e.getMessage());
                }
            }
            InputStream in = null;
            if (docItems != null) {
                CastoramaDocument castDoc = null;
                Metadata metadataItem = null;
                for (RepositoryItem docItem : docItems) {
                    Integer documentType = (Integer) docItem.getPropertyValue("documentType");
                    
                    String documentSubType = null;
                    Object documentSubTypeItem = docItem.getPropertyValue("documentSubType");
                    if (documentSubTypeItem != null) {
                        documentSubType = (String)((RepositoryItem)documentSubTypeItem).getPropertyValue("typeTitle");
                        documentSubType = documentSubType.replace(" ", "_");
                    }
                    
                    String title = (String) docItem.getPropertyValue("title");
                    String description = (String) docItem.getPropertyValue("description");
                    String relativeURL = (String) docItem.getPropertyValue("relativeURL");
                    Double rating = (Double) docItem.getPropertyValue("rating");
                    Object categoriesObj = docItem.getPropertyValue("categories");
                    Object keywordsObj = docItem.getPropertyValue("keywords");
                    ChangeAwareList keywords = (ChangeAwareList)keywordsObj;
                    try {
                        if ((categoriesObj != null) && (categoriesObj instanceof java.util.Set)) {
                            java.util.Set<RepositoryItem> categoriesList = (java.util.Set) categoriesObj;
                            if (!StringUtils.isBlank(relativeURL)) {
                                URL documentURL = new URL(path + escapeUrlString(relativeURL));

                                URLConnection urlConnection = documentURL.openConnection();

                                if (isURLexists((HttpURLConnection) urlConnection)) {
                                    in = urlConnection.getInputStream();

                                    if (in != null) {
                                        List<String> list = new ArrayList<String>();
                                        list.add(title);
                                        list.add(description);
                                        if (keywords != null && !keywords.isEmpty()) {
                                            Iterator it = keywords.getBaseList().iterator();
                                            while (it.hasNext()){
                                                String temp = (String)it.next();
                                                if (temp != null){
                                                    list.add(temp);
//                                                    list.clear();
                                                }
                                            }
                                        }
                                        byte[] data = readFile(in, list);
                                        in.close();
                                        if ((data != null) && (data.length > 0)) {
                                            for (RepositoryItem catRepItem : categoriesList) {
                                                if (catRepItem != null) {
                                                    castDoc = new CastoramaDocument();
                                                    castDoc.setRepositoryId(docItem.getRepositoryId());
                                                    castDoc.setCategoryId(catRepItem.getRepositoryId());
                                                    if (!StringUtils.isBlank(catRepItem.getRepositoryId())) {
                                                        metadataItem = new Metadata();
                                                        metadataItem.setName("atg:string,docset:castDocAncestorCategories.$repositoryId");
                                                        metadataItem.setContent(catRepItem.getRepositoryId());
                                                        
                                                        metadataItem.setMode(Metadata.AddMode.APPEND);
                                                        if (docLists.get(docItem.getRepositoryId())!=null){
                                                            docLists.get(docItem.getRepositoryId()).getDocumentMetadataList().add(metadataItem);
                                                            continue;
                                                        } else {
                                                            castDoc.getDocumentMetadataList().add(metadataItem);
                                                        }
                                                    }
                                                    if (!StringUtils.isBlank(docItem.getRepositoryId())) {
                                                        metadataItem = new Metadata();
                                                        metadataItem.setName("atg:string,index:$repositoryId");
                                                        metadataItem.setContent(docItem.getRepositoryId());

                                                        metadataItem.setMode(Metadata.AddMode.APPEND);
                                                        castDoc.getDocumentMetadataList().add(metadataItem);
                                                    }
                                                    if (!StringUtils.isBlank(description)) {
                                                        metadataItem = new Metadata();
                                                        metadataItem.setName("atg:string:description");
                                                        metadataItem.setContent(description);

                                                        metadataItem.setMode(Metadata.AddMode.APPEND);
                                                        castDoc.getDocumentMetadataList().add(metadataItem);
                                                    }
                                                    if (!StringUtils.isBlank(title)) {
                                                        metadataItem = new Metadata();
                                                        metadataItem.setName("atg:string:title");
                                                        metadataItem.setContent(title);

                                                        metadataItem.setMode(Metadata.AddMode.APPEND);
                                                        castDoc.getDocumentMetadataList().add(metadataItem);
                                                    }
                                                    if (documentType != null) {
                                                        metadataItem = new Metadata();
                                                        metadataItem.setName("atg:integer:documentType");
                                                        metadataItem.setContent(documentType.toString());

                                                        metadataItem.setMode(Metadata.AddMode.APPEND);
                                                        castDoc.getDocumentMetadataList().add(metadataItem);
                                                    }
                                                    
                                                    if (documentSubType != null) {
                                                        metadataItem = new Metadata();
                                                        metadataItem.setName("atg:string:documentSubType");
                                                        metadataItem.setContent(documentSubType);

                                                        metadataItem.setMode(Metadata.AddMode.APPEND);
                                                        castDoc.getDocumentMetadataList().add(metadataItem);
                                                    }

                                                    if (rating != null) {
                                                        metadataItem = new Metadata();
                                                        metadataItem.setName("atg:float:rating");
                                                        metadataItem.setContent(rating.toString());

                                                        metadataItem.setMode(Metadata.AddMode.APPEND);
                                                        castDoc.getDocumentMetadataList().add(metadataItem);
                                                    }

                                                    if (!StringUtils.isBlank(relativeURL)) {
                                                        castDoc.setRelativeURL((String) relativeURL);
                                                    }

                                                    castDoc.setFile(data);
                                                    if (docLists.get(castDoc.getRepositoryId())!=null){
                                                        docLists.get(castDoc.getRepositoryId()).getDocumentMetadataList().addAll(castDoc.getDocumentMetadataList());
                                                    } else {
                                                        docLists.put(castDoc.getRepositoryId(),castDoc);
                                                    }
                                                }  // end if
                                            }  // end for
                                        }  // end if

                                        if (urlConnection instanceof HttpURLConnection) {
                                            ((HttpURLConnection) urlConnection).disconnect();
                                        }
                                    }  // end if
                                }  // end if
                            }  // end if
                        }  // end if
                    } catch (MalformedURLException e) {
                        if (isLoggingError()) {
                            logError(e.getMessage());
                        }
                    } catch (UnknownHostException e) {
                        if (isLoggingError()) {
                            logError("Wrong host: please provide correct host : " + e.getMessage());
                        }
                    } catch (IOException e) {
                        if (isLoggingError()) {
                            logError(e.getMessage());
                        }

                    } finally {
                        try {
                            if (in != null) {
                                in.close();
                            }
                        } catch (IOException e) {
                            if (isLoggingError()) {
                                logError(e.getMessage());
                            }
                        }
                    }  // end try-catch-finally
                }  // end for
                return new ArrayList<CastoramaDocument>(docLists.values());
            }  // end if
        }  // end if
        return null;
    }

    /**
     * Check whether pDocumentURL exist or no.
     *
     * @param  pDocumentURL address to test
     *
     * @return true if document exist and available through pDocumentURL address
     *
     * @throws IOException exception
     */
    private boolean isURLexists(URLConnection pDocumentURL) throws IOException {
        if ((pDocumentURL != null) && (pDocumentURL instanceof HttpURLConnection) &&
                (((HttpURLConnection) pDocumentURL).getResponseCode() == HttpURLConnection.HTTP_OK)) {
            return true;
        }
        return false;

    }

    /**
     * Returns converted path
     *
     * @return convertedPath
     */
    private String getConvertedPath() {
        if (!StringUtils.isBlank(getFileHostPath())) {
            try {
                URL url = new URL(getFileHostPath());
                return url.getProtocol() + "://" + url.getHost() +
                       ((url.getPort() != -1) ? (":" + url.getPort()) : "") +
                       ((url.getPath().length() > 0) ? escapeUrlString(url.getPath()) : "");
            } catch (MalformedURLException e) {
                if (isLoggingError()) {
                    logError(e.getMessage());
                }
                return null;
            }

        } else {
            if (!StringUtils.isBlank(getProtocol()) && !StringUtils.isBlank(getHost()) &&
                    !StringUtils.isBlank(getPort())) {
                return getProtocol() + "://" + getHost() + ":" + getPort();
            }
        }
        return null;
    }

    /**
     * Util method for path escaping
     *
     * @param  pStr parameter
     *
     * @return escaped path
     */
    private final String escapeUrlString(final String pStr) {
        URLEscaper sEscaper = new URLEscaperISO88591();

        if (StringUtils.isBlank(pStr)) {
            return "";
        }

        StringBuffer sb = new StringBuffer(pStr.length());
        sEscaper.setUnescaped(' ', false);
        sEscaper.escapeAndAppendUrl(sb, pStr.toCharArray());

        return sb.toString();
    }

    /**
     * Read document and returns array of bytes for indexing
     *
     * @param  pInputStream         pHttpURLConnection parameter
     * @param  pAdditionalPrameters parameter
     *
     * @return array of bytes for indexing
     */
    public byte[] readFile(InputStream pInputStream, List<String> pAdditionalPrameters) {
        byte[] documentByteArray = null;
        if (pInputStream != null) {
            BufferedReader fr = null;
            BufferedWriter fw = null;
            try {
                InputStreamReader isr = new InputStreamReader(pInputStream, "UTF-8");
                fr = new BufferedReader(isr);
                char[] chars = new char[1024 * 4];
                int index = -1;

                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024 * 4);
                fw = new BufferedWriter(new OutputStreamWriter(baos));
                while ((index = fr.read(chars)) != -1) {
                    fw.write(chars, 0, index);
                }
                if ((pAdditionalPrameters != null) && !pAdditionalPrameters.isEmpty()) {
                    for (String str : pAdditionalPrameters) {
                        if (!StringUtils.isBlank(str)) {
                            fw.write("<div>" + str + "</div>");
                        }
                    }
                }
                fr.close();
                fw.close();
                documentByteArray = baos.toByteArray();
            } catch (IOException e) {
                if (isLoggingError()) {
                    logError(e.getMessage());
                }
            } finally {
                try {
                    if (fr != null) {
                        fr.close();
                    }
                    if (fw != null) {
                        fw.close();
                    }
                } catch (IOException e) {
                    if (isLoggingError()) {
                        logError(e.getMessage());
                    }
                }
            }  // end try-catch-finally
        }  // end if

        return documentByteArray;
    }

}
