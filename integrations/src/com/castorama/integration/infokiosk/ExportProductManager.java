package com.castorama.integration.infokiosk;

import atg.adapter.gsa.GSARepository;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

import com.castorama.commerce.catalog.CastCatalogTools;

import com.castorama.integration.Constants;
import com.castorama.integration.backoffice.exp.UtilFormat;
import com.castorama.integration.journal.JournalItem;
import com.castorama.integration.journal.ProcessingJournalService;
import com.castorama.integration.util.MiscUtils;

import com.castorama.utils.ServerSetting;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * ToDo: DOCUMENT ME!
 *
 * @author  EPAM team
 * @version ToDo: INSERT VERSION NUMBER
 */
public class ExportProductManager extends SingletonSchedulableService {
    /** NEW_LINE property */
    protected static String NEW_LINE = "<br>";

    /** PROP_CHILD_PRODUCTS property */
    private static final String PROP_CHILD_PRODUCTS = "childProducts";

    /** PROP_CHILD_SKUS property */
    private static final String PROP_CHILD_SKUS = "childSKUs";

    /** PROP_CHILD_CATEGORIES property */
    private static final String PROP_CHILD_CATEGORIES = "childCategories";

    /** ELEMENT_CATEGORY property */
    private static final String ELEMENT_CATEGORY = "cat";

    /** ATTR_CATEGORY_ID property */
    private static final String ATTR_CATEGORY_ID = "catid";

    /** ATTR_CATEGORY_NAV property */
    private static final String ATTR_CATEGORY_NAV = "nav";

    /** ELEMENT_TYPE_CATEGORY property */
    private static final String ELEMENT_TYPE_CATEGORY = "typCat";

    /** ELEMENT_DISP_NAME_CATEGORY property */
    private static final String ELEMENT_DISP_NAME_CATEGORY = "libCat";

    /** ELEMENT_PRODUCT property */
    private static final String ELEMENT_PRODUCT = "pdt";

    /** ELEMENT_PRODUCT_LBL property */
    private static final String ELEMENT_PRODUCT_LBL = "lbl";

    /** ELEMENT_PRODUCT_DESC property */
    private static final String ELEMENT_PRODUCT_DESC = "desc";

    /** ELEMENT_PRODUCT_IMG property */
    private static final String ELEMENT_PRODUCT_IMG = "img";

    /** ATTR_CODE_ARTICLE property */
    private static final String ATTR_CODE_ARTICLE = "codeArticle";

    /** ITEM_CROSSSELLING property */
    private static final String ITEM_CROSSSELLING = "ikCrossSelling";

    /** ITEM_SKU property */
    private static final String ITEM_SKU = "sku";

    /** ELEMENT_CROSSSELLING property */
    private static final String ELEMENT_CROSSSELLING = "crossseling";

    /** JPG_EXT property */
    private static final String JPG_EXT = ".jpg";

    /** KEY_BORNEZRM_SEQ property */
    private static final String KEY_BORNEZRM_SEQ = "BorneZRM";

    /** PROPERTY_LIBELLE_DESCRIPTION property */
    private static final String PROPERTY_LIBELLE_DESCRIPTION = "LibelleDescriptifArticle";

    /** PROPERTY_CATEGORY_TYPE property */
    private static final String PROPERTY_CATEGORY_TYPE = "ikCatType";

    /** PROPERTY_CATEGORY_TYPE_NAV property */
    private static final String PROPERTY_CATEGORY_TYPE_NAV = "ikCatTypeNav";

    /** active property */
    private boolean active;

    /** isProgress property */
    private boolean isProgress = false;

    /** workingDir property */
    private File workingDir = null;

    /** outputDir property */
    private File outputDir = null;

    /** errorDir property */
    private File errorDir;

    /** archiveDir property */
    private File archiveDir = null;

    /** imagesDir property */
    private File imagesDir;

    /** repository property */
    private Repository repository = null;

    /** rootDir property */
    private String rootDir;

    /** borneMagasinId property */
    private String borneMagasinId;

    /** catalogTools property */
    private CastCatalogTools catalogTools;

    /** serverSetting property */
    private ServerSetting serverSetting;

    /** journalService property */
    private ProcessingJournalService journalService;

    /** prefixXMLFile property */
    private String prefixXMLFile;

    /** prefixImgFile property */
    private String prefixImgFile;

    /** prefixLogFile property */
    private String prefixLogFile;

    /** countItems property */
    private int countItems = 0;

    /** countImages property */
    private int countImages = 0;

    /** status property */
    private int status = 2;

    /** largeImagesSet property */
    private Set<String> largeImagesSet;

    /** warning property */
    private StringBuilder warning;

    /**
     * ToDo: DOCUMENT ME!
     */
    public void exportProducts() {
        boolean oldActive = active;
        active = true;

        doScheduledTask(null, null);

        active = oldActive;
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param pScheduler ToDo: DOCUMENT ME!
     * @param pJob       ToDo: DOCUMENT ME!
     */
    @Override public void doScheduledTask(Scheduler pScheduler, ScheduledJob pJob) {
        if (!active) {
            if (isLoggingInfo()) {
                logInfo("The service is not active.");
            }
            return;
        }

        if (isProgress) {
            if (isLoggingError()) {
                logError("Unable to start products export... The service is already started. Please wait...");
            }
            return;
        }

        if (isLoggingDebug()) {
            logDebug("Start export products BorneMagasin from Repository");
        }
        isProgress = true;
        try {
            executeExport();
        } finally {
            isProgress = false;
        }

        if (isLoggingDebug()) {
            logDebug("End export products BorneMagasin from Repository");
        }

    }

    /**
     * ToDo: DOCUMENT ME!
     */
    protected void executeExport() {
        // Retrieve catalog
        if ((getRepository() == null) || (getCatalogTools() == null)) {
            if (isLoggingError()) {
                logError("incorrect configuration");
            }
            return;
        }
        
        ((GSARepository)getRepository()).invalidateCaches();

        largeImagesSet = new HashSet<String>();
        warning = new StringBuilder(500);

        JournalItem jItem = null;
        countItems = 0;
        countImages = 0;
        status = 0;
        long sequence = 0;

        SimpleDateFormat dateExpLog = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String expDate = dateExpLog.format(new Date());

        try {
            checkFolders();

            if (isLoggingInfo()) {
                logInfo("Root folder: " + getRootDir());
                logInfo("Working folder: " + workingDir);
                logInfo("Images folder: " + imagesDir);
                logInfo("Output folder: " + outputDir);
                logInfo("Archive folder: " + archiveDir);
            }

            RepositoryItem borneCatalog = getBorneCatalog();
            if (borneCatalog == null) {
                if (isLoggingError()) {
                    logError("Catalog 'Borne Magasin' is null.");
                }
                status = 2;
                return;
            }

            Document document = createDocument();

            jItem = getJournalService().registerStarting(KEY_BORNEZRM_SEQ);
            sequence = jItem.getSequence();

            Element listCategories = document.createElement("listecatBorne");
            document.appendChild(listCategories);

            createRootCategories(borneCatalog, listCategories, document);

            String xmlFileName = getFileName(getPrefixXMLFile(), sequence, ".xml");
            String imgFileName = getFileName(getPrefixImgFile(), sequence, "");

            if (isLoggingInfo()) {
                logInfo("XML File name: " + xmlFileName);
                logInfo("Imgages File name: " + imgFileName);
            }
            File xmlFile = saveDocument(document, xmlFileName);

            MiscUtils.copyFile(xmlFile, new File(outputDir, xmlFile.getName()));
            zippingImgs(imgFileName);

            getJournalService().registerFinishing(jItem);

        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e);
            }
            	if (jItem != null) {
                    try {
                        getJournalService().registerFails(jItem);
                    } catch (RepositoryException re) {
                        if (isLoggingError()) {
                            logError(re);
                        }
                    }
            	}
            status = 2;
        } finally {
        	if (archiveDir != null && workingDir != null) {
                try {
                    File logFile = writeLogFile(sequence, expDate);
                    MiscUtils.copyFile(logFile, new File(archiveDir, logFile.getName()));
                    MiscUtils.deleteFolder(workingDir);
                } catch (IOException e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                }
        	}
        }  // end try-catch-finally
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @return ToDo: DOCUMENT ME!
     *
     * @throws ParserConfigurationException ToDo: DOCUMENT ME!
     */
    protected Document createDocument() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        return docBuilder.newDocument();
    }

    /**
     * Returns borneCatalog property.
     *
     * @return borneCatalog property.
     *
     * @throws RepositoryException - exception
     */
    protected RepositoryItem getBorneCatalog() throws RepositoryException {
        return getCatalogTools().getCatalog(getBorneMagasinId());
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param  borneCatalog ToDo: DOCUMENT ME!
     * @param  listCats     ToDo: DOCUMENT ME!
     * @param  doc          ToDo: DOCUMENT ME!
     *
     * @throws Exception ToDo: DOCUMENT ME!
     */
    @SuppressWarnings("unchecked")
    protected void createRootCategories(RepositoryItem borneCatalog, Element listCats, Document doc) throws Exception {
        Collection<RepositoryItem> rootCategories =
            getCatalogTools().getRootCategoriesForCatalog(borneCatalog, getRepository());

        if (rootCategories != null) {
            for (RepositoryItem rootCategory : rootCategories) {
                if (isLoggingDebug()) {
                    logDebug("Root category: " + rootCategory);
                }

                handleCategory(rootCategory, listCats, doc);

            }  // end for
        } else {
            throw new Exception("Root categories not found for Catalog " + borneCatalog);
        }  // end if-else
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param category      ToDo: DOCUMENT ME!
     * @param parentElement ToDo: DOCUMENT ME!
     * @param doc           ToDo: DOCUMENT ME!
     */
    @SuppressWarnings("unchecked")
    protected void handleCategory(RepositoryItem category, Element parentElement, Document doc) {
        if (category == null) {
            if (isLoggingError()) {
                logError("Category is null. " + category);
            }
            return;
        }
        Collection<RepositoryItem> products =
            (Collection<RepositoryItem>) category.getPropertyValue(PROP_CHILD_PRODUCTS);
        if (products != null) {
            for (RepositoryItem product : products) {
                Collection<RepositoryItem> skus =
                    (Collection<RepositoryItem>) product.getPropertyValue(PROP_CHILD_SKUS);
                if (skus != null) {
                    for (RepositoryItem sku : skus) {
                        createProduct(sku, product, parentElement, doc);
                        countItems++;
                    }
                }
            }
        }  // end if

        Collection<RepositoryItem> categories =
            (Collection<RepositoryItem>) category.getPropertyValue(PROP_CHILD_CATEGORIES);
        if (categories != null) {
            for (RepositoryItem subCat : categories) {
                Element elemCategory = createCategory(subCat, parentElement, doc);
                handleCategory(subCat, elemCategory, doc);
            }
        }

    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param  category       ToDo: DOCUMENT ME!
     * @param  parentCategory ToDo: DOCUMENT ME!
     * @param  doc            ToDo: DOCUMENT ME!
     *
     * @return ToDo: DOCUMENT ME!
     */
    protected Element createCategory(RepositoryItem category, Element parentCategory, Document doc) {
        Element categ = doc.createElement(ELEMENT_CATEGORY);
        categ.setAttribute(ATTR_CATEGORY_ID, category.getRepositoryId());

        try {
            String navCat = (String) category.getPropertyValue(PROPERTY_CATEGORY_TYPE_NAV);
            if (navCat != null) {
                categ.setAttribute(ATTR_CATEGORY_NAV, navCat);
            } else {
                categ.setAttribute(ATTR_CATEGORY_NAV, "");
            }
        } catch (IllegalArgumentException e) {
            if (isLoggingError()) {
                logError("Property " + PROPERTY_CATEGORY_TYPE_NAV + " is not defined for category " + category);
            }
        }
        parentCategory.appendChild(categ);

        Element typeCat = doc.createElement(ELEMENT_TYPE_CATEGORY);
        categ.appendChild(typeCat);
        try {
        	RepositoryItem typeCategory = (RepositoryItem) category.getPropertyValue(PROPERTY_CATEGORY_TYPE);
            if (typeCategory != null) {
                typeCat.appendChild(doc.createCDATASection(typeCategory.getRepositoryId()));
            }
        } catch (IllegalArgumentException e) {
            if (isLoggingError()) {
                logError("Property " + PROPERTY_CATEGORY_TYPE + " is not defined for category " + category);
            }
        }

        String dispName = (String) category.getPropertyValue(Constants.PROPERTY_DISPLAY_NAME);
        if (dispName != null) {
            Element elem = doc.createElement(ELEMENT_DISP_NAME_CATEGORY);
            elem.appendChild(doc.createCDATASection(dispName));
            categ.appendChild(elem);
        }

        return categ;

    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param sku            category       ToDo: DOCUMENT ME!
     * @param product        ToDo: DOCUMENT ME!
     * @param parentCategory ToDo: DOCUMENT ME!
     * @param doc            ToDo: DOCUMENT ME!
     */
    @SuppressWarnings("unchecked")
    protected void createProduct(RepositoryItem sku, RepositoryItem product, Element parentCategory, Document doc) {
        Element prod = doc.createElement(ELEMENT_PRODUCT);

        Integer codeArticle = (Integer) sku.getPropertyValue(Constants.PROPERTY_CODE_ARTICLE);

        prod.setAttribute(ATTR_CODE_ARTICLE, String.valueOf(codeArticle));

        Element elemLbl = doc.createElement(ELEMENT_PRODUCT_LBL);
        prod.appendChild(elemLbl);
        String propString = (String) sku.getPropertyValue(PROPERTY_LIBELLE_DESCRIPTION);
        if (propString != null) {
            elemLbl.appendChild(doc.createCDATASection(propString));
        }

        Element elemLong = doc.createElement(ELEMENT_PRODUCT_DESC);
        prod.appendChild(elemLong);
        propString = (String) sku.getPropertyValue(Constants.PROPERTY_LIBELLE_CLIENT_LONG);
        if (propString != null) {
            elemLong.appendChild(doc.createCDATASection(propString));
        }

        String imageName = getImageName(codeArticle);
        Element elem = doc.createElement(ELEMENT_PRODUCT_IMG);
        prod.appendChild(elem);
        if (exportImage(sku, imageName)) {
            elem.appendChild(doc.createCDATASection(imageName));
        }

        Element crossSell = doc.createElement(ELEMENT_CROSSSELLING);
        prod.appendChild(crossSell);

        Collection<RepositoryItem> crossSelling = (Collection<RepositoryItem>) sku.getPropertyValue(ITEM_CROSSSELLING);
        if (crossSelling != null) {
            for (RepositoryItem crossSellingSku : crossSelling) {
                RepositoryItem skuItem = (RepositoryItem) crossSellingSku.getPropertyValue(ITEM_SKU);
                if (skuItem != null) {
                    createCrossSelling(skuItem, crossSell, doc);
                }
            }
        }

        parentCategory.appendChild(prod);
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param sku          ToDo: DOCUMENT ME!
     * @param crossSelling ToDo: DOCUMENT ME!
     * @param doc          ToDo: DOCUMENT ME!
     */
    protected void createCrossSelling(RepositoryItem sku, Element crossSelling, Document doc) {
        Element prod = doc.createElement(ELEMENT_PRODUCT);
        Integer codeArticle = (Integer) sku.getPropertyValue(Constants.PROPERTY_CODE_ARTICLE);
        prod.setAttribute(ATTR_CODE_ARTICLE, String.valueOf(codeArticle));
        crossSelling.appendChild(prod);

        String propString = (String) sku.getPropertyValue(Constants.PROPERTY_DISPLAY_NAME);
        if (propString != null) {
            Element elem = doc.createElement(ELEMENT_PRODUCT_LBL);
            elem.appendChild(doc.createCDATASection(propString));
            prod.appendChild(elem);
        }

        propString = (String) sku.getPropertyValue(Constants.PROPERTY_DESCRIPTION);
        if (propString != null) {
            Element elem = doc.createElement(ELEMENT_PRODUCT_DESC);
            elem.appendChild(doc.createCDATASection(propString));
            prod.appendChild(elem);
        }

        String imageName = getImageName(codeArticle);
        Element elem = doc.createElement(ELEMENT_PRODUCT_IMG);
        elem.appendChild(doc.createCDATASection(imageName));
        prod.appendChild(elem);

        exportImage(sku, imageName);
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param  document ToDo: DOCUMENT ME!
     * @param  file     ToDo: DOCUMENT ME!
     *
     * @return ToDo: DOCUMENT ME!
     *
     * @throws Exception ToDo: DOCUMENT ME!
     */
    protected File saveDocument(Document document, String file) throws Exception {
        FileOutputStream fos = null;
        File xmlFile = new File(workingDir, file);
        try {
            fos = new FileOutputStream(xmlFile);
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(fos);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

            transformer.transform(source, result);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                }
            }
        }  // end try-finally
        return xmlFile;
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param  sku       ToDo: DOCUMENT ME!
     * @param  imageName ToDo: DOCUMENT ME!
     *
     * @return ToDo: DOCUMENT ME!
     */
    protected boolean exportImage(RepositoryItem sku, String imageName) {
        boolean result = true;
        
        if ((null != imageName) && !"".equals(imageName.toString())) {
            
            String largeImageurl = "/images/products/h/" + imageName;
            
            if (largeImagesSet.contains(largeImageurl)) {
                return result;
            }
            try {
                if (downloadImage(imageName, largeImageurl)) {
                    countImages++;
                    largeImagesSet.add(largeImageurl);
                } else {
                    StringBuilder msg = new StringBuilder();
                    msg.append("Can not download image. URL ").append(largeImageurl)
                    .append(". SKU ").append(sku);
                    warning.append(msg).append(NEW_LINE);
                    if (isLoggingWarning()) {
                        logWarning(msg.toString());
                    }
                }
            } catch (MalformedURLException e) {
                if (isLoggingError()) {
                    logError(e);
                }
                result = false;
            }
        } else {
            result = false;
        }
        
        return result;
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param  mImageName ToDo: DOCUMENT ME!
     * @param  mImageUrl  ToDo: DOCUMENT ME!
     *
     * @return ToDo: DOCUMENT ME!
     *
     * @throws MalformedURLException ToDo: DOCUMENT ME!
     */
    protected boolean downloadImage(String mImageName, String mImageUrl) throws MalformedURLException {
        String imageUrl = mImageUrl;
        boolean result = true;
        OutputStream outStream = null;
        InputStream is = null;
        URL url = null;
        URLConnection urlConn = null;

        try {
            if (!imageUrl.startsWith("http://")) {
                imageUrl = getServerSetting().getHost() + imageUrl;
            }
            url = new URL(imageUrl);
            urlConn = url.openConnection();
            long lastModified = urlConn.getLastModified();
            if (lastModified == 0) {
                if (isLoggingError()) {
                    logError("URL " + imageUrl + " is invalid.");
                }
                return false;
            }

            is = urlConn.getInputStream();

            outStream = new BufferedOutputStream(new FileOutputStream(imagesDir + File.separator + mImageName));

            byte[] buf = new byte[32768];
            int bytesRead;
            while ((bytesRead = is.read(buf, 0, buf.length)) > 0) {
                outStream.write(buf, 0, bytesRead);
            }

        } catch (MalformedURLException e) {
            throw e;
        } catch (IOException e) {
            if (isLoggingError()) {
                logError(e);
            }
            result = false;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                if (isLoggingError()) {
                    logError(e);
                }
            }
        }  // end try-catch-finally
        return result;
    }

    /**
     * Returns active property.
     *
     * @return active property.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the value of the active property.
     *
     * @param active parameter to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * The ProductCatalog repository that holds the information
     *
     * @return the repository that holds the information
     */
    public Repository getRepository() {
        return repository;
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param repository set the ProductCatalog repository that holds the
     *                   information
     */
    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    /**
     * The home directory where files will be processed
     *
     * @return home directory where files will be processed
     */
    public String getRootDir() {
        return rootDir;
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param rootDir set home directory where files will be processed
     */
    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    /**
     * Returns borneMagasinId property.
     *
     * @return borneMagasinId property.
     */
    public String getBorneMagasinId() {
        return borneMagasinId;
    }

    /**
     * Sets the value of the borneMagasinId property.
     *
     * @param borneMagasinId parameter to set.
     */
    public void setBorneMagasinId(String borneMagasinId) {
        this.borneMagasinId = borneMagasinId;
    }

    /**
     * Returns catalogTools property.
     *
     * @return catalogTools property.
     */
    public CastCatalogTools getCatalogTools() {
        return catalogTools;
    }

    /**
     * Sets the value of the catalogTools property.
     *
     * @param catalogTools parameter to set.
     */
    public void setCatalogTools(CastCatalogTools catalogTools) {
        this.catalogTools = catalogTools;
    }

    /**
     * Returns serverSetting property.
     *
     * @return serverSetting property.
     */
    public ServerSetting getServerSetting() {
        return serverSetting;
    }

    /**
     * Sets the value of the serverSetting property.
     *
     * @param serverSetting parameter to set.
     */
    public void setServerSetting(ServerSetting serverSetting) {
        this.serverSetting = serverSetting;
    }

    /**
     * Returns journalService property.
     *
     * @return journalService property.
     */
    public ProcessingJournalService getJournalService() {
        return journalService;
    }

    /**
     * Sets the value of the journalService property.
     *
     * @param journalService parameter to set.
     */
    public void setJournalService(ProcessingJournalService journalService) {
        this.journalService = journalService;
    }

    /**
     * Returns prefixXMLFile property.
     *
     * @return prefixXMLFile property.
     */
    public String getPrefixXMLFile() {
        return prefixXMLFile;
    }

    /**
     * Sets the value of the prefixXMLFile property.
     *
     * @param prefixXMLFile parameter to set.
     */
    public void setPrefixXMLFile(String prefixXMLFile) {
        this.prefixXMLFile = prefixXMLFile;
    }

    /**
     * Returns prefixImgFile property.
     *
     * @return prefixImgFile property.
     */
    public String getPrefixImgFile() {
        return prefixImgFile;
    }

    /**
     * Sets the value of the prefixImgFile property.
     *
     * @param prefixImgFile parameter to set.
     */
    public void setPrefixImgFile(String prefixImgFile) {
        this.prefixImgFile = prefixImgFile;
    }

    /**
     * Returns prefixLogFile property.
     *
     * @return prefixLogFile property.
     */
    public String getPrefixLogFile() {
        return prefixLogFile;
    }

    /**
     * Sets the value of the prefixLogFile property.
     *
     * @param prefixLogFile parameter to set.
     */
    public void setPrefixLogFile(String prefixLogFile) {
        this.prefixLogFile = prefixLogFile;
    }

    /**
     * Returns imageName property.
     *
     * @param  codeArticle parameter to set.
     *
     * @return imageName property.
     */
    protected String getImageName(Integer codeArticle) {
        return "h_" + codeArticle.toString() + JPG_EXT;
    }

    /**
     * ToDo: DOCUMENT ME!
     */
    protected void checkFolders() {
        File rootDir = new File(getRootDir());
        if (!rootDir.exists()) {
            rootDir.mkdir();
        }
        workingDir = new File(rootDir, Constants.WORKING_FOLDER);
        workingDir = new File(workingDir, Long.toString(new Date().getTime()));
        if (!workingDir.exists()) {
            workingDir.mkdirs();
        }

        errorDir = new File(rootDir, Constants.ERROR_FOLDER);
        if (!errorDir.exists()) {
            errorDir.mkdir();
        }

        archiveDir = new File(rootDir, Constants.ARCHIVE_FOLDER);
        if (!archiveDir.exists()) {
            archiveDir.mkdir();
        }

        outputDir = new File(getRootDir(), Constants.OUTPUT_FOLDER);
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }

        imagesDir = new File(workingDir, "images");
        imagesDir.mkdir();

    }

    /**
     * Returns fileName property.
     *
     * @param  prefix   parameter to set.
     * @param  sequence parameter to set.
     * @param  ext      parameter to set.
     *
     * @return fileName property.
     */
    protected String getFileName(String prefix, long sequence, String ext) {
        StringBuilder sb = new StringBuilder();
        return sb.append((prefix != null) ? prefix : "")  //
               .append(UtilFormat.formatString(Long.toString(sequence), 6, '0', false))  //
               .append("_").append(Constants.DATE_FORMAT_YYYYMMDD.format(new Date()))  //
               .append(ext).toString();
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param  imgFileName ToDo: DOCUMENT ME!
     *
     * @throws Exception ToDo: DOCUMENT ME!
     */
    private void zippingImgs(String imgFileName) throws Exception {
        String pathZipImg = imagesDir.getAbsolutePath() + File.separator + imgFileName;
        MiscUtils.TarCompressFiles(imagesDir, pathZipImg);

        File tarImgFile = new File(pathZipImg);
        File tarImgOutFile = new File(outputDir, tarImgFile.getName());
        if (tarImgFile.exists()) {
            MiscUtils.copyFile(tarImgFile, tarImgOutFile);
        } else {
            if (isLoggingWarning()) {
                logWarning("Images File not found.");
            }
        }
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param  sequence ToDo: DOCUMENT ME!
     * @param  expDate  ToDo: DOCUMENT ME!
     *
     * @return ToDo: DOCUMENT ME!
     *
     * @throws IOException ToDo: DOCUMENT ME!
     */
    private File writeLogFile(long sequence, String expDate) throws IOException {
        String logFileName = getFileName(getPrefixLogFile(), sequence, ".html");
        
        if (isLoggingDebug()) {
            logDebug("Start writing log file " + logFileName);
        }
        
        File logFile = new File(workingDir, logFileName);
        FileWriter fw = null;
        StringBuilder log = new StringBuilder();
        InputStream is = null;
        try {
            is =
                ExportProductManager.class.getClassLoader().getResourceAsStream("com/castorama/integration/infokiosk/logHeader.properties");
            byte[] bytes = new byte[1024];
            int index = 0;
            while ((index = is.read(bytes)) > 0) {
                log.append(new String(bytes, 0, index));
            }
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
        StringBuilder logBody = new StringBuilder();
        try {
            is =
                ExportProductManager.class.getClassLoader().getResourceAsStream("com/castorama/integration/infokiosk/logBody.properties");
            byte[] bytes = new byte[1024];
            int index = 0;
            while ((index = is.read(bytes)) > 0) {
                logBody.append(new String(bytes, 0, index));
            }
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
        String statusLog = (status == 0) ? "OK" : "KO";
        String wrng = (warning.length() > 0) ? warning.toString() : "";
        String body =
            MessageFormat.format(logBody.toString(),
                                 new Object[] {countItems,  //
                                  countImages, expDate,  //
                                  UtilFormat.formatString(Long.toString(sequence), 6, '0', false),  //
                                  statusLog, wrng});
        log.append(body);
        
        if (isLoggingDebug()) {
            logDebug("Finish writing log file. Info : " + countItems + ", " + countImages
                    + ", " + expDate + ", " 
                    + UtilFormat.formatString(Long.toString(sequence), 6, '0', false)
                    + ", " + statusLog);
        }
        
        try {
            fw = new FileWriter(logFile);
            fw.write(log.toString());
        } finally {
            try {
                fw.close();
            } catch (Exception e) {
            }
        }

        return logFile;
    }

}
