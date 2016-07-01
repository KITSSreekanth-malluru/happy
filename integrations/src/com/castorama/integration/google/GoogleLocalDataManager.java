package com.castorama.integration.google;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import atg.adapter.gsa.GSARepository;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.nucleus.ServiceException;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.search.client.SearchClientException;
import atg.search.client.SearchClientService;
import atg.search.client.SearchSession;
import atg.search.routing.command.IncompleteCommandException;
import atg.search.routing.command.search.QueryRequest;
import atg.search.routing.command.search.QueryRequest.Response;
import atg.search.routing.command.search.Result;
import atg.service.scheduler.Schedulable;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;

import com.castorama.integration.Constants;
import com.castorama.integration.IntegrationBase;
import com.castorama.seo.SEOUtils;

/**
 * 
 * 
 * @author EPAM team
 * @version
 */
public class GoogleLocalDataManager extends IntegrationBase implements Schedulable {

    /* Search client config information */
    private static final String SEARCH_DOCPROP_REPO_ID = "$repositoryId";
    
    /* priceListManager information */
    private static final String PLM_PL = "priceList";
    private static final String PLM_LP = "listPrice";
    
    /* Repository information */
    private static final String REPO_DISPLAY_NAME = "displayName";
    
    /* magasinRepository information */
    private static final String MAGASIN_ITEM_DESCR = "magasin";
    private static final String MAGASIN_STORE_ID = "storeId";
    private static final String MAGASIN_REGION_NOM = "nom";
    private static final String MAGASIN_TEL = "tel";
    private static final String MAGASIN_RUE = "rue";
    private static final String MAGASIN_ADRESSE = "adresse";
    private static final String MAGASIN_ENTITE = "entite";
    private static final String MAGASIN_VILLE = "ville";
    private static final String MAGASIN_DEPARTEMENT = "departement";
    private static final String MAGASIN_REGION = "region";
    private static final String MAGASIN_CP = "cp";
    private static final String MAGASIN_STORE_URL = "storeUrl";
    private static final String MAGASIN_DESCRIPTION = "description";
    private static final String MAGASIN_LATITUDE = "latitude";
    private static final String MAGASIN_LONGITUDE = "longitude";
    
    /* ProductRepository information */
    private static final String PROD_SKU_ITEM_DESCR = "casto_sku";
    private static final String PROD_CAT_ITEM_DESCR = "casto_category";
    private static final String PROD_SKU_CA = "CodeArticle";
    private static final String PROD_SKU_TITLE = "LibelleDescriptifArticle";
    private static final String PROD_PROD_PAR_CATS = "parentCategories";
    private static final String PROD_SKU_PARENT_PRODUCTS = "parentProducts";
    private static final String PROD_CAT_PAR_CAT = "parentCategory";
    private static final String PROD_SKU_DESCR = "LibelleClientLong";
    private static final String PROD_SKU_MARK = "MarqueCommerciale";
    private static final String PROD_SKU_POIDS = "PoidsUV";
    private static final String PROD_CAT_FEATURED_PROD = "featuredProduct";
    private static final String PROD_CAT_MAG_CASTO_NAME = "Magasin Castorama";
    private static final String PROD_SKU_COLOR = "couleur";
    private static final String PROD_SKU_EAN = "CodeEAN";
    
    /* Google local status file information */
    private static final String DF_STATUS_NAME_PREFIX = "Supervision_TXT_";
    private static final String DF_STATUS_NAME_POSTFIX = "_Google.html";
    
    /* Google local ShopsListingsData file information */
    private static final String DF_SHOPS_NAME = "shopslistingdata.txt";
    private static final String[] DF_SHOPS_COLUMN_NAMES = {
        // REQ
        "store code", "name", "main phone", "address line 1", "city", "state", 
        "postal code", "country code", "home page", "category",
        // REC
        // OPT
        "address line 2", "description", "currency", "established date", 
        "latitude", "longitude"
    };
    private static final String DF_MAGASIN_COUNTRY_CODE = "FR";
    private static final String DF_MAGASIN_CATEGORY = "Magasin de bricolage";
    private static final String DF_MAGASIN_CURRENCY = "EUR";
    private static final String DF_MAGASIN_NAME = "Castorama";
    
    /* Google local ProductsListingData file information */
    private static final String DF_PRODUCTS_NAME = "productslistingdata.txt";
    private static final String[] DF_PRODUCTS_COLUMN_NAMES = {
        // REQ
        "id", "title", "description", "condition",
        // REC
        "link", "image link", "price",
        // OPT
        "promotional text", "featured product", "gtin", "brand", "product type", 
        "weight", "size", "color"
    };
    private static final String DF_IMAGE_PREFIX = 
        "http://www.castorama.fr/images/products/h/h_";
    private static final String DF_PAGE_URL_PREFIX = "http://www.castorama.fr/store";
    private static final String DF_IMAGE_EXT = ".jpg";
    private static final String DF_CASTO_PREFIX = "casto";
    private static final String DF_PRODUCT_TYPE_DELIMETER = ">";
    private static final String DF_WEIGHT_POSTFIX = " kg";
    private static final String DF_PRODUCT_CONDITION = "New";
    
    /* Temporary table for searchable products information */
    private static final String INSERT_SEARCHABLE_PRODUCT = 
        "INSERT INTO CASTO_TMP_SEARCHABLE_PRODUCTS (SKU_ID, CODEARTICLE) VALUES(?, ?)";
    private static final String CLEAR_SEARCHABLE_PRODUCT_TABLE = 
        "TRUNCATE TABLE CASTO_TMP_SEARCHABLE_PRODUCTS";
    
    
    /* scheduler property */
    private Scheduler scheduler;
    
    /* dataSource property */
    private DataSource dataSource;

    /* shopsListingsSchedule property */
    private Schedule shopsListingsSchedule;
    
    /* productsListingSchedule property */
    private Schedule productsListingSchedule;

    /* shopsListingsJobId property */
    private int shopsListingsJobId;
    
    /* productsListingJobId property */
    private int productsListingJobId;

    /* shopsListingsInProgress flag property */
    private boolean shopsListingsInProgress = false;
    
    /* productsListingInProgress flag property */
    private boolean productsListingInProgress = false;
    
    /* searchClientService property */
    private SearchClientService searchClientService;
    
    /* searchRequest property */
    private QueryRequest searchRequest;
    
    /* priceListManager property */
    private PriceListManager priceListManager;
    
    /* productRepository property */
    private Repository productRepository;
    
    /* magasinRepository property */
    private Repository magasinRepository;
    
    /* shopsDataFileDelimeter property */
    private String shopsDataFileDelimeter;
    
    /* productsDataFileDelimeter property */
    private String productsDataFileDelimeter;
    
    /* promotionalText property */
    private String promotionalText;
    
    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
    
    public DataSource getDataSource() {
        return dataSource;
    }
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public Schedule getShopsListingsSchedule() {
        return shopsListingsSchedule;
    }

    public void setShopsListingsSchedule(Schedule shopsListingsSchedule) {
        this.shopsListingsSchedule = shopsListingsSchedule;
    }

    public Schedule getProductsListingSchedule() {
        return productsListingSchedule;
    }

    public void setProductsListingSchedule(Schedule productsListingSchedule) {
        this.productsListingSchedule = productsListingSchedule;
    }

    public SearchClientService getSearchClientService() {
        return searchClientService;
    }
    
    public void setSearchClientService(SearchClientService searchClientService) {
        this.searchClientService = searchClientService;
    }
    
    public QueryRequest getSearchRequest() {
        return searchRequest;
    }
    
    public void setSearchRequest(QueryRequest searchRequest) {
        this.searchRequest = searchRequest;
    }
    
    public PriceListManager getPriceListManager() {
        return priceListManager;
    }
    
    public void setPriceListManager(PriceListManager priceListManager) {
        this.priceListManager = priceListManager;
    }
    
    public Repository getProductRepository() {
        return productRepository;
    }
    
    public void setProductRepository(Repository productRepository) {
        this.productRepository = productRepository;
    }
    
    public Repository getMagasinRepository() {
        return magasinRepository;
    }
    
    public void setMagasinRepository(Repository magasinRepository) {
        this.magasinRepository = magasinRepository;
    }

    public String getShopsDataFileDelimeter() {
        return shopsDataFileDelimeter;
    }

    public void setShopsDataFileDelimeter(String shopsDataFileDelimeter) {
        this.shopsDataFileDelimeter = shopsDataFileDelimeter;
    }

    public String getProductsDataFileDelimeter() {
        return productsDataFileDelimeter;
    }

    public void setProductsDataFileDelimeter(String productsDataFileDelimeter) {
        this.productsDataFileDelimeter = productsDataFileDelimeter;
    }

    public String getPromotionalText() {
        return promotionalText;
    }

    public void setPromotionalText(String promotionalText) {
        this.promotionalText = promotionalText;
    }

    /**
     * Connects to the queue and starts listening for messages.
     * 
     * @throws ServiceException If a service exception occurs.
     */
    public void doStartService() throws ServiceException {
        ScheduledJob job = new ScheduledJob("ShopsListingsDataManager",
                "Exports ShopsListingsData to Google Local file", getAbsoluteName(), 
                getShopsListingsSchedule(), this, ScheduledJob.SCHEDULER_THREAD);
        shopsListingsJobId = getScheduler().addScheduledJob(job);
        
        if (isLoggingDebug()) {
            logDebug("ShopsListingsDataManager service started. Job id : "
                    + shopsListingsJobId);
        }
        
        job = new ScheduledJob("ProductsListingDataManager",
                "Exports ProductsListingData to Google Local file", getAbsoluteName(), 
                getProductsListingSchedule(), this, ScheduledJob.SCHEDULER_THREAD);
        productsListingJobId = getScheduler().addScheduledJob(job);
        
        if (isLoggingDebug()) {
            logDebug("ProductsListingDataManager service started. Job id : "
                    + productsListingJobId);
        }
    }

    /**
     * Stops listening for messages and disconnects from the queue.
     * 
     * @throws ServiceException If a service exception occurs.
     */
    public void doStopService() throws ServiceException {
        getScheduler().removeScheduledJob(shopsListingsJobId);

        if (isLoggingDebug()) {
            logDebug("ShopsListingsDataManager service stopped. Job id : "
                    + shopsListingsJobId);
        }
        
        getScheduler().removeScheduledJob(productsListingJobId);

        if (isLoggingDebug()) {
            logDebug("ProductsListingDataManager service stopped. Job id : "
                    + productsListingJobId);
        }
    }

    /**
     * 
     * 
     * @param scheduler
     * @param job
     */
    public void performScheduledTask(Scheduler scheduler, ScheduledJob job) {
        if (isLoggingInfo()) {
            logInfo("Perform sheduled task. Job name : " + job.getJobName());
        }
        
        try {
            int jobId = job.getJobId();
            if (jobId == shopsListingsJobId) {
                exportShopsListingsData();
            } else if (jobId == productsListingJobId) {
                exportProductsListingData();
            }
        } catch (ServiceException se) {
            if (isLoggingError()) {
                logError(se);
            }
        }
    }

    /** */
    public void exportShopsListingsData() throws ServiceException {
        if (shopsListingsInProgress) {
            throw new ServiceException(
                    "Unable to start ShopsListingsData export... " +
                    "The service is already started. Please wait...");
        }

        if (isLoggingDebug()) {
            logDebug("start - GoogleLocalDataManager - exportShopsListingsData");
        }

        shopsListingsInProgress = true;

        try {
            generateShopsListingsDataFile();
        } finally {
            shopsListingsInProgress = false;
        }

        if (isLoggingDebug()) {
            logDebug("finish - GoogleLocalDataManager - exportShopsListingsData");
        }
    }
    
    /** */
    public void exportProductsListingData() throws ServiceException {
        if (productsListingInProgress) {
            throw new ServiceException(
                    "Unable to start ProductsListingData export... " +
                    "The service is already started. Please wait...");
        }

        if (isLoggingDebug()) {
            logDebug("start - GoogleLocalDataManager - exportProductsListingData");
        }

        productsListingInProgress = true;

        try {
            generateProductsListingDataFile();
        } finally {
            productsListingInProgress = false;
        }

        if (isLoggingDebug()) {
            logDebug("finish - GoogleLocalDataManager - exportProductsListingData");
        }
    }

    /* */
    private void generateShopsListingsDataFile() {
        if (isLoggingDebug()) {
            logDebug("Generating ShopsListingsData txt file...");
        }
        
        String result = "KO";
        File txtFile = null;
        PrintWriter printWriter = null;
        
        try {
            ((GSARepository)magasinRepository).invalidateCaches();
            
            RqlStatement magasinsRql = RqlStatement.parseRqlStatement("ALL");
            RepositoryView magasinsView = magasinRepository.getView(MAGASIN_ITEM_DESCR);
            RepositoryItem[] magasinsList = magasinsRql.executeQuery(magasinsView, 
                    new Object[] {});

            if ((magasinsList != null) && (magasinsList.length > 0)) {
                boolean[] containsInfo = new boolean[DF_SHOPS_COLUMN_NAMES.length];
                boolean[] containsInfoPrev = new boolean[DF_SHOPS_COLUMN_NAMES.length];
                String[][] gShops = new String[magasinsList.length][];
                
                int shopsListingSize = 0;
                for (RepositoryItem magasin : magasinsList) {
                    String[] gShop = new String[DF_SHOPS_COLUMN_NAMES.length];
                    
                    String storeCode = (String) magasin.getPropertyValue(MAGASIN_STORE_ID);
                    if (storeCode != null) {
                        gShop[0] = storeCode;
                        containsInfo[0] = true;
                    } else {
                        if (isLoggingWarning()) {
                            logWarning("storeId is null for magasin with id = " 
                                    + magasin.getRepositoryId() + ". Row skipped");
                        }
                        continue;
                    }
                    
                    gShop[1] = DF_MAGASIN_NAME;
                    containsInfo[1] = true;
                    
                    RepositoryItem entite = 
                        (RepositoryItem) magasin.getPropertyValue(MAGASIN_ENTITE);
                    if (entite != null) {
                        RepositoryItem adresse = 
                            (RepositoryItem) entite.getPropertyValue(MAGASIN_ADRESSE);
                        if (adresse != null) {
                            String mainPhone = 
                                (String) adresse.getPropertyValue(MAGASIN_TEL);
                            if (mainPhone != null) {
                                gShop[2] = mainPhone;
                                containsInfo[2] = true;
                            } else {
                                if (isLoggingWarning()) {
                                    logWarning("tel is null for adresse with id = " 
                                            + adresse.getRepositoryId() + ". Row skipped");
                                }
                                continue;
                            }
                            
                            String addressLine1 = 
                                (String) adresse.getPropertyValue(MAGASIN_RUE);
                            if (addressLine1 != null) {
                                if (addressLine1.length() > 80) {
                                    addressLine1= addressLine1.substring(0, 80);
                                }
                                addressLine1 = addressLine1.replace('\n', ' ')
                                    .replace('\r', ' ').replace('\t', ' ')
                                    .replace("<br>", " ").replace("<BR>", " ");
                                gShop[3] = addressLine1;
                                containsInfo[3] = true;
                            } else {
                                if (isLoggingWarning()) {
                                    logWarning("rue is null for adresse with id = " 
                                            + adresse.getRepositoryId() + ". Row skipped");
                                }
                                continue;
                            }
                            
                            String city = (String) adresse.getPropertyValue(MAGASIN_VILLE);
                            if (city != null) {
                                gShop[4] = city;
                                containsInfo[4] = true;
                            } else {
                                if (isLoggingWarning()) {
                                    logWarning("ville is null for adresse with id = " 
                                            + adresse.getRepositoryId() + ". Row skipped");
                                }
                                continue;
                            }
                            
                            RepositoryItem departement = 
                                (RepositoryItem) adresse.getPropertyValue(MAGASIN_DEPARTEMENT);
                            if (departement != null) {
                                RepositoryItem region = 
                                    (RepositoryItem) departement.getPropertyValue(MAGASIN_REGION);
                                if (region != null) {
                                    String state = (String) region.getPropertyValue(MAGASIN_REGION_NOM);
                                    if (state != null) {
                                        if (state.length() > 80) {
                                            state= state.substring(0, 80);
                                        }
                                        gShop[5] = state;
                                        containsInfo[5] = true;
                                    } else {
                                        if (isLoggingWarning()) {
                                            logWarning("nom is null for region with id = " 
                                                    + region.getRepositoryId() + ". Row skipped");
                                        }
                                        continue;
                                    }
                                } else {
                                    if (isLoggingWarning()) {
                                        logWarning("region is null for departement with id = " 
                                                + departement.getRepositoryId() + ". Row skipped");
                                    }
                                    continue;
                                }
                            } else {
                                if (isLoggingWarning()) {
                                    logWarning("departement is null for adresse with id = " 
                                            + adresse.getRepositoryId() + ". Row skipped");
                                }
                                continue;
                            }
                            
                            String postalCode = (String) adresse.getPropertyValue(MAGASIN_CP);
                            if (postalCode != null) {
                                gShop[6] = postalCode;
                                containsInfo[6] = true;
                            } else {
                                if (isLoggingWarning()) {
                                    logWarning("cp is null for adresse with id = " 
                                            + adresse.getRepositoryId() + ". Row skipped");
                                }
                                continue;
                            }
                        } else {
                            if (isLoggingWarning()) {
                                logWarning("adresse is null for entite with id = " 
                                        + entite.getRepositoryId() + ". Row skipped");
                            }
                            continue;
                        }
                    } else {
                        if (isLoggingWarning()) {
                            logWarning("entite is null for magasin with id = " 
                                    + magasin.getRepositoryId() + ". Row skipped");
                        }
                        continue;
                    }
                    
                    gShop[7] = DF_MAGASIN_COUNTRY_CODE;
                    containsInfo[7] = true;
                    
                    String homePage = (String) magasin.getPropertyValue(MAGASIN_STORE_URL);
                    if (homePage != null) {
                        gShop[8] = homePage;
                        containsInfo[8] = true;
                    } else {
                        if (isLoggingWarning()) {
                            logWarning("storeUrl is null for magasin with id = " 
                                    + magasin.getRepositoryId() + ". Row skipped");
                        }
                        continue;
                    }
                    
                    gShop[9] = DF_MAGASIN_CATEGORY;
                    containsInfo[9] = true;
                    
                    //shop[10] = addressLine2;
                    //containsInfo[10] = true;
                    
                    String description = (String) magasin.getPropertyValue(MAGASIN_DESCRIPTION);
                    if (description != null) {
                        if (description.length() > 200) {
                            description= description.substring(0, 200);
                        }
                        gShop[11] = description.replace('\t', ' ').replace('\n', ' ')
                            .replace('\r', ' ');
                        containsInfo[11] = true;
                    }
                    
                    gShop[12] = DF_MAGASIN_CURRENCY;
                    containsInfo[12] = true;
                    
                    //shop[13] = establishedDate;
                    //containsInfo[13] = true;
                    
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setGroupingUsed(false);
                    nf.setMaximumFractionDigits(5);
                    nf.setMinimumFractionDigits(5);
                    
                    Double latitude = (Double) magasin.getPropertyValue(MAGASIN_LATITUDE);
                    if (latitude != null) {
                        gShop[14] = nf.format(latitude);
                        containsInfo[14] = true;
                    }
                    
                    Double longitude = (Double) magasin.getPropertyValue(MAGASIN_LONGITUDE);
                    if (longitude != null) {
                        gShop[15] = nf.format(longitude);
                        containsInfo[15] = true;
                    }
                    
                    gShops[shopsListingSize++] = gShop;
                    for (int i = 0; i < containsInfo.length; i++) {
                        containsInfoPrev[i] = containsInfo[i];
                    }
                }
                
                int resultCols = 0;
                for (boolean b : containsInfoPrev) {
                    if (b) resultCols++;
                }
                if (resultCols > 0) {
                    File outputDir = getDir(Constants.OUTPUT_FOLDER);
                    txtFile = new File(outputDir, DF_SHOPS_NAME);
                    
                    if (isLoggingInfo()) {
                        logInfo("Start writing of ShopsListingsData txt file : "
                                + txtFile.getName());
                    }
        
                    printWriter = new PrintWriter(txtFile, ENCODING);
                    
                    
                    for (int i = 0, j = 0; i < DF_SHOPS_COLUMN_NAMES.length; i++) {
                        if (containsInfoPrev[i]) {
                            printWriter.print(DF_SHOPS_COLUMN_NAMES[i]);
                            if (++j < resultCols) {
                                printWriter.print(shopsDataFileDelimeter);
                            }
                        }
                    }
                    printWriter.println();
                    
                    for (int i = 0; i < shopsListingSize; i++) {
                        for (int j = 0, k = 0; j < DF_SHOPS_COLUMN_NAMES.length; j++) {
                            if (containsInfoPrev[j]) {
                                if (gShops[i][j] != null) {
                                    printWriter.print(gShops[i][j]);
                                }
                                if (++k < resultCols) {
                                    printWriter.print(shopsDataFileDelimeter);
                                }
                            }
                        }
                        printWriter.println();
                    }
                    
                    if (isLoggingInfo()) {
                        logInfo("Writing of ShopsListingsData txt file finished : "
                                + txtFile.getName() + " . Number of records: " + shopsListingSize);
                    }
                } else {
                    if (isLoggingWarning()) {
                        logWarning("There are no rows with all required columns. " +
                        		"ShopsListingsData txt is not created");
                    }
                }
                result = "OK";
            } else {
                if (isLoggingWarning()) {
                    logWarning("No magasins found. ShopsListingsData txt is not created");
                }
                result = "OK";
            }
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e);
            }
            if (printWriter != null) {
                printWriter.close();
                printWriter = null;
            }
            if (txtFile != null) {
                txtFile.delete();
            }
        } finally {
            if (printWriter != null) {
                printWriter.close();
                printWriter = null;
            }
        }
        
        if (isLoggingDebug()) {
            logDebug("Generation of ShopsListingsData txt file finished");
        }
        
        generateStatusFile(DF_SHOPS_NAME, result);
    }
    
    /* */
    @SuppressWarnings("unchecked")
    private void generateProductsListingDataFile() {
        if (isLoggingDebug()) {
            logDebug("Generating ProductsListingData txt file...");
        }

        String result = "KO";
        File txtFile = null;
        PrintWriter printWriter = null;
        
        try {
            ((GSARepository)productRepository).invalidateCaches();
            
            Set<RepositoryItem> skus = getSearchableProducts();
            List<RepositoryItem> writedSkus = new ArrayList<RepositoryItem>(skus.size());
            
            if ((skus != null) && (skus.size() > 0)) {
                boolean[] containsInfo = new boolean[DF_PRODUCTS_COLUMN_NAMES.length];
                boolean[] containsInfoPrev = new boolean[DF_PRODUCTS_COLUMN_NAMES.length];
                String[][] gProducts = new String[skus.size()][];
                
                Set<RepositoryItem> featuredProds = getAllFeaturedProducts();
                
                int productListingSize = 0;
                for (RepositoryItem sku : skus) {
                    String[] gProduct = new String[DF_PRODUCTS_COLUMN_NAMES.length];
                    
                    Set<RepositoryItem> parentProds = (Set<RepositoryItem>) 
                        sku.getPropertyValue(PROD_SKU_PARENT_PRODUCTS);
                    
                    RepositoryItem product = (RepositoryItem) parentProds.toArray()[0];
                    boolean isFeatured = false;
                    
                    for (RepositoryItem prod : parentProds) {
                        if (featuredProds.contains(prod)) {
                            product = prod;
                            isFeatured = true;
                            break;
                        }
                    }
                    
                    gProduct[0] = sku.getRepositoryId();
                    containsInfo[0] = true;
                    
                    String title = (String) sku.getPropertyValue(PROD_SKU_TITLE);
                    if (title != null) {
                        gProduct[1] = title.replace('\t', ' ').replace('\n', ' ')
                            .replace('\r', ' ');
                        containsInfo[1] = true;
                    } else {
                        if (isLoggingWarning()) {
                            logWarning("LibelleDescriptifArticle is null for sku with id = " 
                                    + sku.getRepositoryId() + ". Row skipped");
                        }
                        continue;
                    }
                    
                    String description = (String) sku.getPropertyValue(PROD_SKU_DESCR);
                    if (description != null) {
                        gProduct[2] = description.replace('\t', ' ').replace('\n', ' ')
                            .replace('\r', ' ');
                        containsInfo[2] = true;
                    } else {
                        if (isLoggingWarning()) {
                            logWarning("LibelleClientLong is null for sku with id = " 
                                    + sku.getRepositoryId() + ". Row skipped");
                        }
                        continue;
                    }
                    
                    gProduct[3] = DF_PRODUCT_CONDITION;
                    containsInfo[3] = true;
                    
                    String pageUrl = DF_PAGE_URL_PREFIX + SEOUtils.getProductURL(product);
                    gProduct[4] = pageUrl;
                    containsInfo[4] = true;
                    
                    String skuImage = sku.getRepositoryId();
                    if (DF_CASTO_PREFIX.equalsIgnoreCase(sku.getRepositoryId()
                            .substring(0, DF_CASTO_PREFIX.length()))) {
                        skuImage = skuImage.substring(DF_CASTO_PREFIX.length());
                    }
                    String image = DF_IMAGE_PREFIX + skuImage + DF_IMAGE_EXT;
                    gProduct[5] = image;
                    containsInfo[5] = true;
                    
                    RepositoryItem priceList = priceListManager.getPriceList(null, PLM_PL);
                    if (priceList != null) {
                        RepositoryItem price = priceListManager.getPrice(priceList, null, sku);
                        if (price != null) {
                            Double listPrice = (Double) price.getPropertyValue(PLM_LP);
                            if (listPrice != null) {
                                NumberFormat nf = NumberFormat.getInstance();
                                nf.setGroupingUsed(false);
                                nf.setMaximumFractionDigits(2);
                                nf.setMinimumFractionDigits(2);
                                
                                gProduct[6] = nf.format(listPrice);
                                containsInfo[6] = true;
                            }
                        }
                    }
                    
                    if ((promotionalText != null) && (promotionalText.length() > 0)) {
                        gProduct[7] = promotionalText;
                        containsInfo[7] = true;
                    }
                    
                    gProduct[8] = isFeatured ? "y" : "n";
                    containsInfo[8] = true;
                    
                    String gtin = (String) sku.getPropertyValue(PROD_SKU_EAN);
                    if (gtin != null) {
                        gProduct[9] = gtin;
                        containsInfo[9] = true;
                    } else {
                        if (isLoggingWarning()) {
                            logWarning("CodeEAN is null for sku with id = " 
                                    + sku.getRepositoryId() + ". Row skipped");
                        }
                        continue;
                    }
                    
                    String brand = (String) sku.getPropertyValue(PROD_SKU_MARK);
                    if (brand != null) {
                        gProduct[10] = brand;
                        containsInfo[10] = true;
                    }
                    
                    List<String> catsHierarchy = null;
                    Set<RepositoryItem> parentCategories = 
                        (Set<RepositoryItem>) product.getPropertyValue(PROD_PROD_PAR_CATS);
                    if ((parentCategories != null) && (parentCategories.size() > 0)) {
                        for (RepositoryItem parentCategory : parentCategories) {
                            RepositoryItem currCat = parentCategory;
                            List<String> currCatHierarchy = new ArrayList<String>();
                            while (true) {
                                if (currCat == null) break;
                                String currCatName = (String) currCat.getPropertyValue(REPO_DISPLAY_NAME);
                                if (PROD_CAT_MAG_CASTO_NAME.equalsIgnoreCase(currCatName)) {
                                    catsHierarchy = currCatHierarchy;
                                    break;
                                }
                                currCatHierarchy.add(currCatName);
                                currCat = (RepositoryItem) currCat.getPropertyValue(PROD_CAT_PAR_CAT);
                            }
                            if (catsHierarchy != null) break;
                        }
                    }  else {
                        if (isLoggingWarning()) {
                            logWarning("Product (prodId=" + product.getRepositoryId() 
                                    + ") hasn't got parent category.");
                        }
                    }
                    if (catsHierarchy != null) {
                        StringBuilder productType = new StringBuilder();
                        for (int i = (catsHierarchy.size() - 1); i >= 0; i--) {
                            productType.append(catsHierarchy.get(i));
                            if (i > 0) {
                                productType.append(DF_PRODUCT_TYPE_DELIMETER);
                            }
                        }
                        gProduct[11] = productType.toString();
                        containsInfo[11] = true;
                    }
                    
                    Integer poids = (Integer) sku.getPropertyValue(PROD_SKU_POIDS);
                    if (poids != null) {
                        NumberFormat nf = NumberFormat.getInstance();
                        nf.setGroupingUsed(false);
                        nf.setMaximumFractionDigits(3);
                        nf.setMinimumFractionDigits(1);
                        String weight = nf.format(poids / (double) 1000).concat(DF_WEIGHT_POSTFIX);
                        
                        gProduct[12] = weight;
                        containsInfo[12] = true;
                    }
                    
                    //gProduct[13] = size;
                    //containsInfo[13] = true;
                    
                    String color = (String) sku.getPropertyValue(PROD_SKU_COLOR);
                    if (color != null) {
                        gProduct[14] = color;
                        containsInfo[14] = true;
                    }
                    
                    gProducts[productListingSize++] = gProduct;
                    for (int i = 0; i < containsInfo.length; i++) {
                        containsInfoPrev[i] = containsInfo[i];
                    }
                    
                    writedSkus.add(sku);
                }
                
                int resultCols = 0;
                for (boolean b : containsInfoPrev) {
                    if (b) resultCols++;
                }
                if (resultCols > 0) {
                    File outputDir = getDir(Constants.OUTPUT_FOLDER);
                    txtFile = new File(outputDir, DF_PRODUCTS_NAME);
                    
                    if (isLoggingInfo()) {
                        logInfo("Start writing of ProductsListingData txt file : "
                                + txtFile.getName());
                    }
    
                    printWriter = new PrintWriter(txtFile, ENCODING);
                    
                    
                    for (int i = 0, j = 0; i < DF_PRODUCTS_COLUMN_NAMES.length; i++) {
                        if (containsInfoPrev[i]) {
                            printWriter.print(DF_PRODUCTS_COLUMN_NAMES[i]);
                            if (++j < resultCols) {
                                printWriter.print(productsDataFileDelimeter);
                            }
                        }
                    }
                    printWriter.println();
                    
                    for (int i = 0; i < productListingSize; i++) {
                        for (int j = 0, k = 0; j < DF_PRODUCTS_COLUMN_NAMES.length; j++) {
                            if (containsInfoPrev[j]) {
                                if (gProducts[i][j] != null) {
                                    printWriter.print(gProducts[i][j]);
                                }
                                if (++k < resultCols) {
                                    printWriter.print(productsDataFileDelimeter);
                                }
                            }
                        }
                        printWriter.println();
                    }
                    
                    if (isLoggingInfo()) {
                        logInfo("Writing of ProductsListingData txt file finished : "
                                + txtFile.getName() + " . Number of records: " + productListingSize);
                    }
                } else {
                    if (isLoggingWarning()) {
                        logWarning("There are no rows with all required columns. " +
                                "ProductsListingData txt is not created");
                    }
                }
                
                writeProductsToTempTable(writedSkus);
                
                result = "OK";
            } else {
                if (isLoggingWarning()) {
                    logWarning("No products found. ProductsListingData txt is not created");
                }
                result = "OK";
            }
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e);
            }
            if (printWriter != null) {
                printWriter.close();
                printWriter = null;
            }
            if (txtFile != null) {
                txtFile.delete();
            }
        } finally {
            if (printWriter != null) {
                printWriter.close();
                printWriter = null;
            }
        }
        
        if (isLoggingDebug()) {
            logDebug("Generation of ProductsListingData txt file finished");
        }
        
        generateStatusFile(DF_PRODUCTS_NAME, result);
    }
    
    /* */
    @SuppressWarnings("unchecked")
    private Set<RepositoryItem> getSearchableProducts() 
            throws SearchClientException, RepositoryException, IncompleteCommandException {
        if (isLoggingInfo()) {
            logInfo("Searching for products...");
        }
        
        Set<RepositoryItem> skus = null;
        
        SearchSession searchSession = searchClientService.beginSession();
        QueryRequest localSearchRequest = QueryRequest.valueOf(searchRequest.toXML());
        localSearchRequest.setSearchEnvironmentName(searchRequest.getSearchEnvironmentName());
        
        localSearchRequest.setPageNum(0);
        Response searchResponse = searchClientService.search(localSearchRequest, searchSession);
        int resultsSize = searchResponse.getDocCandidates();
        int pageSize = searchResponse.getPageSize();
        
        if (resultsSize > 0) {
            skus = new HashSet<RepositoryItem>();
            
            RqlStatement skusRql = RqlStatement.parseRqlStatement("parentProducts INCLUDES ANY ?0");
            RepositoryView skusView = productRepository.getView(PROD_SKU_ITEM_DESCR);
            
            int pagesNumber = (int)(resultsSize / pageSize) + 1;
            for (int i = 0; i < pagesNumber; i++) {
                if (i != 0) {
                    localSearchRequest.setPageNum(i);
                    searchResponse = searchClientService.search(localSearchRequest, searchSession);
                }
                
                List<Result> results = (List<Result>) searchResponse.getResults();
                List<String> prodIds = new ArrayList<String>(pageSize);
                
                if ((results != null) && (results.size() > 0)) {
                    for (Result result : results) {
                        String prodId = (String) result.getDocument().getProperties()
                                .get(SEARCH_DOCPROP_REPO_ID);
                        prodIds.add(prodId);
                    }
                    
                    RepositoryItem[] pageSkus = skusRql.executeQuery(skusView, new Object[] { prodIds });
                    for (RepositoryItem pageSku: pageSkus) {
                        if (!skus.contains(pageSku)) {
                            skus.add(pageSku);
                        }
                    }
                }
            }
        }
        
        return skus;
    }
    
    /* */
    private Set<RepositoryItem> getAllFeaturedProducts() throws RepositoryException {
        Set<RepositoryItem> featuredProds = null;
        
        RqlStatement categoriesRql = RqlStatement.parseRqlStatement("ALL");
        RepositoryView categoriesView = productRepository.getView(PROD_CAT_ITEM_DESCR);
        RepositoryItem[] categories = categoriesRql.executeQuery(categoriesView, new Object[] { });
        
        if (categories.length > 0) {
            featuredProds = new HashSet<RepositoryItem>();
            for (RepositoryItem category : categories) {
                RepositoryItem featuredProduct = 
                    (RepositoryItem) category.getPropertyValue(PROD_CAT_FEATURED_PROD);
                if (featuredProduct != null) {
                    featuredProds.add(featuredProduct);
                }
            }
        }
            
        return featuredProds;
    }
    
    /* */
    private void writeProductsToTempTable(List<RepositoryItem> skus) {
        if (isLoggingDebug()) {
            logInfo("Start writing searchable products to temporary table");
        }
        
        Connection con = null;
        Statement clearS = null;
        PreparedStatement insertPs = null;
        boolean autoCommit = true;
        
        try {
            con = getDataSource().getConnection();
            autoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            
            clearS = con.createStatement();
            clearS.execute(CLEAR_SEARCHABLE_PRODUCT_TABLE);
            
            close(clearS);
            
            insertPs = con.prepareStatement(INSERT_SEARCHABLE_PRODUCT);
            for (RepositoryItem sku : skus) {
                try {
                    insertPs.setString(1, sku.getRepositoryId());
                    Integer codeArticle = (Integer) sku.getPropertyValue(PROD_SKU_CA);
                    insertPs.setInt(2, codeArticle);
                    insertPs.executeUpdate();
                } catch (SQLException sqlException) {
                    if (isLoggingError()) {
                        logError("Unable to write data for sku: " 
                                + sku.getRepositoryId(), sqlException);
                    }
                }
            }
            con.commit();
        } catch (SQLException e) {
            if (isLoggingError()) {
                logError(e);
            }
        } finally {
            close(clearS);
            close(insertPs);
            
            if (con != null) {
                try {
                    con.setAutoCommit(autoCommit);
                    con.close();
                } catch (SQLException e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                }
            }
        }
        
        if (isLoggingDebug()) {
            logInfo("Writing searchable products to temporary table finished");
        }
    }
    
    /* */
    private void close(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                if (isLoggingError()) {
                    logError(e);
                }
            }
        }
    }
       
    /* */
    private void generateStatusFile(String fileName, String result) {
        String statusFileName = DF_STATUS_NAME_PREFIX + fileName + DF_STATUS_NAME_POSTFIX;
        if (isLoggingDebug()) {
            logDebug("Generating status file " + statusFileName);
        }

        InputStream is = null;
        PrintWriter statusFileWriter = null;
        try {
            is = GoogleLocalDataManager.class.getClassLoader().getResourceAsStream(
                    "com/castorama/integration/google/statusFile.properties");
            StringBuilder sb = new StringBuilder();
            byte[] bytes = new byte[1024];
            int index = 0;
            while ((index = is.read(bytes)) > 0) {
                sb.append(new String(bytes, 0, index));
            }
            
            String fileData = MessageFormat.format(sb.toString(), 
                    new Object[] {fileName, Constants.DATE_FORMAT_LOG.format(new Date()), result});
            
            File statusDir = getDir(Constants.STATUS_FOLDER);
            File statusFile = new File(statusDir, statusFileName);
            statusFileWriter = new PrintWriter(statusFile);
            statusFileWriter.println(fileData);
        } catch (IOException ioe) {
            if (isLoggingError()) {
                logError("Can't generate status file: ", ioe);
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ioe) { }
            }
            if (statusFileWriter != null) {
                statusFileWriter.close();
            }
        }

        if (isLoggingDebug()) {
            logDebug("Status file generated.");
        }
    }
}
