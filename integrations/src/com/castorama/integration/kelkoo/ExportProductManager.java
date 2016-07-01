package com.castorama.integration.kelkoo;

import java.io.File;
import java.io.PrintWriter;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import atg.commerce.inventory.RepositoryInventoryManager;
import atg.commerce.pricing.priceLists.PriceListManager;

import atg.nucleus.ServiceException;

import atg.repository.Repository;
import atg.repository.RepositoryItem;

import atg.search.client.SearchClientService;
import atg.search.client.SearchSession;
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
public class ExportProductManager extends IntegrationBase implements Schedulable {

    /* Search client config information */
    private static final String SEARCH_DOCPROP_REPO_ID = "$repositoryId";
    private static final String SEARCH_DOCPROP_REPO_DESCR_NAME = 
        "$itemDescriptor.itemDescriptorName";
    
    /* Repository information */
    private static final String REPO_DISPLAY_NAME = "displayName";
    
    /* ProductRepository information */
    private static final String PROD_PROP_CHILDSKUS = "childSKUs";
    private static final String PROD_PROP_PAR_CATS = "parentCategories";
    private static final String PROD_CAT_PROP_PAR_CAT = "parentCategory";
    private static final String PROD_SKU_PROP_DESCR = "LibelleClientLong";
    private static final String PROD_SKU_PROP_GARANTIE = "Garantie";
    private static final String PROD_SKU_PROP_SOUMISA = "SoumisaD3E";
    private static final String PROD_SKU_PROP_ECOTAXE = "ecoTaxeEnEuro";
    private static final String PROD_SKU_PROP_MARK = "MarqueCommerciale";
    private static final String PROD_SKU_PROP_ONSALE = "onSale";
    private static final String PROD_SKU_PROP_EXOPFE = "exonerationPFE";
    private static final String PROD_SKU_PROP_TRANSP = "transporteur";
    private static final String PROD_SKU_PROP_DELAY = "DelaiApproFournisseur";
    private static final String PROD_SKU_PROP_TYPEEXP = "typeExpedition";
    private static final String PROD_SKU_PROP_TA = "typeArticle";
    private static final String PROD_SKU_PROP_RMMC = "retraitMomentaneMotifsCodifies";
    private static final String PROD_SKU_PROP_DD = "onSaleDiscountDisplay";
    
    /* priceListManager information */
    private static final String PLM_PL = "priceList";
    private static final String PLM_LP = "listPrice";
    private static final String PLM_SALEPL = "salePriceList";
    
    /* Kelkoo data file information */
    private static final String DF_EXTENSION = ".csv";
    private static final String DF_DELIMETER = "|";
    private static final String DF_NAME_PREFIX = "KELKOO_";
    private static final SimpleDateFormat DF_NAME_DATE_FORMAT = 
        new SimpleDateFormat("yyyyMMdd_HHmmss");
    private static final String[] DF_COLUMN_NAMES = {
        "SkuId", "Url de la page", "Stock", "Libelle", "Description",
        "Garantie", "SoumisaD3E", "Eco-taxe en euros", "Image",
        "PageListe name", "CatNiveau 1 name", "CatNiveau 2 name",
        "CatNiveau 3 name", "CatNiveau 4 name", "CatNiveau 5 name",
        "PageListe id", "CatNiveau 1 id", "CatNiveau 2 id", 
        "CatNiveau 3 id", "CatNiveau 4 id", "CatNiveau 5 id", "Marque",
        "List Price", "On Sale", "Sale Price", "Frais de livraison",
        "Delai de livraison en jr", "Type Article",
        "RetraitMomentaneMotifsCodifies", "Display discount"
    };
    private static final int DF_PAR_CAT_DEPTH = 5;
    private static final String DF_MSG_FREE_DELIV = "Port gratuit";
    
    private static final int DF_FRAC_DIGITS_NUM = 4;
    
    private static final int DF_COND_VENDU_INT_MAG = 1;
    private static final int DF_COND_VENDU_INT = 2;
    private static final int DF_COND_VENDU_MAG = 3;
    private static final String DF_MSG_VENDU_INT_MAG = 
        "Vendu sur Internet et en magasin";
    private static final String DF_MSG_VENDU_INT = "Exclusif Internet";
    private static final String DF_MSG_VENDU_MAG = "Vendu uniquement en magasin";
    private static final String DF_MSG_TA_UNKNOWN = "Type Article inconnu";
    
    private static final String DF_FRACZERO = ".0";
    private static final String DF_INTZERO = "0.";
    private static final String DF_INT_TRUE = "1";
    private static final String DF_INT_FALSE = "0";
    
    private static final String DF_IMAGE_PREFIX = 
        "http://www.castorama.fr/images/products/h/h_"; 
    private static final String DF_IMAGE_EXT = ".jpg";
    
    private static final String DF_URL_PREFIX = "/store/"; 
    private static final String DF_URL_EXT = ".html";
    
    private static final String DF_PRODNAME_EXCL_PATTERN = "[^a-zA-Z0-9_-]";
    private static final String DF_STR_QUES = "?";
    private static final String DF_STR_EMPTY = "";
    private static final String DF_STR_HYPHEN = "-";
    private static final char DF_CH_SLASH = '/';
    private static final char DF_CH_SPACE = ' ';
    private static final char DF_CH_HYPHEN = '-';
    private static final char DF_CH_9 = (char) 9;
    private static final char DF_CH_10 = (char) 10;
    private static final char DF_CH_13 = (char) 13;
    
    private static final String DF_CASTO_PREFIX = "casto"; 
    
    private static final int DF_COND_DELAY_MORE_6 = 35;
    private static final int DF_COND_DELAY_MORE_5 = 28;
    private static final int DF_COND_DELAY_MORE_4 = 21;
    private static final int DF_COND_DELAY_MORE_3 = 14;
    private static final int DF_COND_DELAY_MORE_2 = 7;
    private static final int DF_COND_DELAY_MORE_1 = 0;
    private static final String DF_MSG_DELAY_6 = "6 - 8 semaines";
    private static final String DF_MSG_DELAY_5 = "5 semaines";
    private static final String DF_MSG_DELAY_4 = "4 semaines";
    private static final String DF_MSG_DELAY_3 = "3 semaines";
    private static final String DF_MSG_DELAY_2 = "2 semaines";
    private static final String DF_MSG_DELAY_1 = "1 semaine";
    
    private static final String DF_COND_TRANSP = "COLISSIMO";
    
    /* scheduler property */
    private Scheduler scheduler;

    /* schedule property */
    private Schedule schedule;

    /* jobId property */
    private int jobId;

    /* inProgress flag property */
    private boolean inProgress = false;
    
    /* searchClientService property */
    private SearchClientService searchClientService;
    
    /* searchRequest property */
    private QueryRequest searchRequest;
    
    /* repositoryInventoryManager property */
    private RepositoryInventoryManager repositoryInventoryManager;
    
    /* priceListManager property */
    private PriceListManager priceListManager;
    
    /* productRepository property */
    private Repository productRepository;
    
    /* orderRepository property */
    private Repository orderRepository;
    
    /* shippingCalculator property */
    private KelkooWeightShippingCalculatorImpl shippingCalculator;
    
    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
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
    
    public RepositoryInventoryManager getRepositoryInventoryManager() {
        return repositoryInventoryManager;
    }
    
    public void setRepositoryInventoryManager(
            RepositoryInventoryManager repositoryInventoryManager) {
        this.repositoryInventoryManager = repositoryInventoryManager;
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
    
    public Repository getOrderRepository() {
        return orderRepository;
    }
    
    public void setOrderRepository(Repository orderRepository) {
        this.orderRepository = orderRepository;
    }
    
    public KelkooWeightShippingCalculatorImpl getShippingCalculator() {
        return shippingCalculator;
    }
    
    public void setShippingCalculator(
            KelkooWeightShippingCalculatorImpl shippingCalculator) {
        this.shippingCalculator = shippingCalculator;
    }

    /**
     * Connects to the queue and starts listening for messages.
     * 
     * @throws ServiceException If a service exception occurs.
     */
    public void doStartService() throws ServiceException {
        ScheduledJob job = new ScheduledJob("ExportProductManager",
                "Exports products data to Kelkoo file", getAbsoluteName(), 
                getSchedule(), this, ScheduledJob.SCHEDULER_THREAD);
        jobId = getScheduler().addScheduledJob(job);

        if (isLoggingDebug()) {
            logDebug("ExportProductManager service started. Job id : "
                    + jobId);
        }
    }

    /**
     * Stops listening for messages and disconnects from the queue.
     * 
     * @throws ServiceException If a service exception occurs.
     */
    public void doStopService() throws ServiceException {
        getScheduler().removeScheduledJob(jobId);

        if (isLoggingDebug()) {
            logDebug("ExportProductManager service stopped. Job id : "
                    + jobId);
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
            exportProducts();
        } catch (ServiceException se) {
            if (isLoggingError()) {
                logError(se);
            }
        }
    }

    /** */
    public void exportProducts() throws ServiceException {
        if (inProgress) {
            throw new ServiceException(
                    "Unable to start products export... The service is already started. Please wait...");
        }

        if (isLoggingDebug()) {
            logDebug("start - ExportProductManager - exportProducts");
        }

        inProgress = true;

        try {
            generateDataFile();
        } finally {
            inProgress = false;
        }

        if (isLoggingDebug()) {
            logDebug("finish - ExportProductManager - exportProducts");
        }
    }

    /**
     * generates data file and stores it in output directory
     */
    @SuppressWarnings("unchecked")
    public void generateDataFile() {

        if (isLoggingDebug()) {
            logDebug("Generating CSV file...");
        }

        int recordsExported = 0;

        File csvFile = null;
        PrintWriter printWriter = null;
        
        if (isLoggingInfo()) {
            logInfo("Searching for products...");
        }
        
        try {
            SearchSession searchSession = searchClientService.beginSession();
            
            searchRequest.setPageNum(0);
            Response searchResponse = searchClientService.search(searchRequest, searchSession);
            int resultsSize = searchResponse.getDocCandidates();
            int pageSize = searchResponse.getPageSize();
            
            if (resultsSize > 0) {
                
                Repository productRepository = getProductRepository();
                
                // using treemap to sort skus and make result unique
                TreeMap<RepositoryItem, RepositoryItem> skusMap = new TreeMap<RepositoryItem, RepositoryItem>();
                
                int pagesNumber = (int)(resultsSize / pageSize) + 1;
                for (int i = 0; i < pagesNumber; i++) {
                    if (i != 0) {
                        searchRequest.setPageNum(i);
                        searchResponse = searchClientService.search(searchRequest, searchSession);
                    }
                    
                    List<Result> results = (List<Result>) searchResponse.getResults();
                    for (Result result : results) {
                        String prodId = (String) result.getDocument().getProperties()
                                .get(SEARCH_DOCPROP_REPO_ID);
                        
                        String itemDescrName = (String) result.getDocument().getProperties()
                            .get(SEARCH_DOCPROP_REPO_DESCR_NAME);
                        
                        RepositoryItem product = productRepository.getItem(prodId, itemDescrName);
                        
                        List<RepositoryItem> skus = (List<RepositoryItem>) 
                            product.getPropertyValue(PROD_PROP_CHILDSKUS);
                        
                        for (RepositoryItem sku : skus) {
                            if (!skusMap.containsKey(sku)) {
                                skusMap.put(sku, product);
                            }
                        }
                    }
                }
                
                File outputDir = getDir(Constants.OUTPUT_FOLDER);
                
                csvFile = new File(outputDir, DF_NAME_PREFIX
                        + DF_NAME_DATE_FORMAT.format(new Date())
                        + DF_EXTENSION);
                
                if (csvFile.createNewFile()) {
                    
                    if (isLoggingInfo()) {
                        logInfo("Start writing of csv file : "
                                + csvFile.getName());
                    }
    
                    printWriter = new PrintWriter(csvFile, ENCODING);
                    
                    // write column names
                    for (int i = 0; i < DF_COLUMN_NAMES.length - 1; i++) {
                        printWriter.print(DF_COLUMN_NAMES[i]);
                        printWriter.print(DF_DELIMETER);
                    }
                    printWriter.print(DF_COLUMN_NAMES[DF_COLUMN_NAMES.length - 1]);
                    printWriter.println();
                    
                    for (Map.Entry<RepositoryItem, RepositoryItem> skuEntry : skusMap.entrySet()) {
                        manageDataFileColumns(printWriter, skuEntry.getKey(), skuEntry.getValue());
                        recordsExported++;
                    }
                    
                    if (isLoggingInfo()) {
                        logInfo("Writing of csv file finished : "
                                + csvFile.getName()
                                + " . Number of records: "
                                + recordsExported);
                    }
                }
            }
            
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e);
            }

            if (printWriter != null) {
                printWriter.close();
                printWriter = null;
            }
            if (csvFile != null) {
                csvFile.delete();
            }

        } finally {
            if (printWriter != null) {
                printWriter.close();
                printWriter = null;
            }
        }
        
        if (isLoggingDebug()) {
            logDebug("CSV file generated. Records number = " + recordsExported);
        }
    }
    
    /* manages columns of generated data file */
    @SuppressWarnings("unchecked")
    private void manageDataFileColumns(PrintWriter printWriter, RepositoryItem sku, 
            RepositoryItem product) throws Exception {
        printWriter.print(sku.getRepositoryId());
        printWriter.print(DF_DELIMETER);
        
        String productName = (String) product.getPropertyValue(REPO_DISPLAY_NAME);
        if (productName != null) {
            productName = productName.replace(DF_CH_SPACE, DF_CH_HYPHEN)
                    .replace(DF_CH_SLASH, DF_CH_HYPHEN);
            
            productName = productName.replace('\u00D8', 'O');
            productName = productName.replace('\u00F8', 'o');
            productName = SEOUtils.replaceChars(productName);
            
            productName = productName.replaceAll(DF_PRODNAME_EXCL_PATTERN, DF_STR_QUES);
            productName = productName.replace(DF_STR_QUES, DF_STR_EMPTY);
            String pageUrl = DF_URL_PREFIX + productName + DF_STR_HYPHEN + 
                    product.getRepositoryId() + DF_URL_EXT;
            printWriter.print(pageUrl);
        }
        printWriter.print(DF_DELIMETER);
        
        Long stockLevel = repositoryInventoryManager.queryStockLevel(
                sku.getRepositoryId());         
        printWriter.print(stockLevel);
        printWriter.print(DF_DELIMETER);
        
        String skuName = (String) sku.getPropertyValue(REPO_DISPLAY_NAME);
        if (skuName != null) {
            skuName = skuName.replace(DF_CH_9, DF_CH_SPACE)
                    .replace(DF_CH_10, DF_CH_SPACE).replace(DF_CH_13, DF_CH_SPACE);
            printWriter.print(skuName);
        }
        printWriter.print(DF_DELIMETER);
        
        String description = (String) sku.getPropertyValue(PROD_SKU_PROP_DESCR);
        if (description != null) {
            description = description.replace(DF_CH_9, DF_CH_SPACE)
                    .replace(DF_CH_10, DF_CH_SPACE).replace(DF_CH_13, DF_CH_SPACE);
            printWriter.print(description);
        }
        printWriter.print(DF_DELIMETER);
        
        String garantie = (String) sku.getPropertyValue(PROD_SKU_PROP_GARANTIE);
        if (garantie != null) {
            printWriter.print(garantie);
        }
        printWriter.print(DF_DELIMETER);
        
        Boolean soumisa = (Boolean) sku.getPropertyValue(PROD_SKU_PROP_SOUMISA);
        if (soumisa != null) {
            if (soumisa) {
                printWriter.print(DF_INT_TRUE);
            } else {
                printWriter.print(DF_INT_FALSE);
            }
        }
        printWriter.print(DF_DELIMETER);
        
        Float ecoTaxe = (Float) sku.getPropertyValue(PROD_SKU_PROP_ECOTAXE);
        if (ecoTaxe != null) {
            NumberFormat nf = NumberFormat.getInstance();
            nf.setGroupingUsed(false);
            nf.setMaximumFractionDigits(DF_FRAC_DIGITS_NUM);
            
            String et = nf.format(ecoTaxe);
            if (et.startsWith(DF_INTZERO)) {
                et = et.substring(1);
            }
            if (et.endsWith(DF_FRACZERO)) {
                et = et.substring(0, et.length() - 2);
            }
            printWriter.print(et);
        }
        printWriter.print(DF_DELIMETER);
        
        String skuImage = sku.getRepositoryId();
        if (DF_CASTO_PREFIX.equalsIgnoreCase(sku.getRepositoryId()
                .substring(0, DF_CASTO_PREFIX.length()))) {
            skuImage = skuImage.substring(DF_CASTO_PREFIX.length());
        }
        String image = DF_IMAGE_PREFIX + skuImage + DF_IMAGE_EXT;
        printWriter.print(image);
        printWriter.print(DF_DELIMETER);
        
        Set<RepositoryItem> parentCategories = 
            (Set<RepositoryItem>) product.getPropertyValue(PROD_PROP_PAR_CATS);
        if (parentCategories.size() > 1) {
            if (isLoggingWarning()) {
                logWarning("Sku (skuId=" + sku.getRepositoryId() + ") has more than 1 parent category.");
                logWarning(parentCategories.toString());
            }
        } else if (parentCategories.size() == 0) {
            throw new Exception("Sku (skuId=" + sku.getRepositoryId() + ") hasn't got parent category.");
        }
        
        RepositoryItem category = (RepositoryItem) parentCategories.toArray()[0];
        
        String categoryName = (String) category.getPropertyValue(REPO_DISPLAY_NAME); 
        if (categoryName != null) {
            printWriter.print(categoryName);
        }
        printWriter.print(DF_DELIMETER);
        
        RepositoryItem parentCat = category; 
        for (int i = 0; i < DF_PAR_CAT_DEPTH; i++) {
            if (parentCat != null) {
                parentCat = (RepositoryItem) parentCat.getPropertyValue(PROD_CAT_PROP_PAR_CAT);
                if (parentCat != null) {
                    String pcn = (String) parentCat.getPropertyValue(REPO_DISPLAY_NAME); 
                    if (pcn != null) {
                        printWriter.print(pcn);
                    }
                }
            }
            printWriter.print(DF_DELIMETER);
        }
        
        printWriter.print(category.getRepositoryId());
        printWriter.print(DF_DELIMETER);
        
        parentCat = category; 
        for (int i = 0; i < DF_PAR_CAT_DEPTH; i++) {
            if (parentCat != null) {
                parentCat = (RepositoryItem) parentCat.getPropertyValue(PROD_CAT_PROP_PAR_CAT);
                if (parentCat != null) {
                   printWriter.print(parentCat.getRepositoryId());
                }
            }
            printWriter.print(DF_DELIMETER);
        }
        
        String mark = (String) sku.getPropertyValue(PROD_SKU_PROP_MARK);
        if (mark != null) {
            printWriter.print(mark);
        }
        printWriter.print(DF_DELIMETER);
        
        RepositoryItem priceList = priceListManager.getPriceList(null, PLM_PL);
        if (priceList != null) {
            RepositoryItem price = priceListManager.getPrice(priceList, null, sku);
            if (price != null) {
                Double listPrice = (Double) price.getPropertyValue(PLM_LP);
                if (listPrice != null) {
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setGroupingUsed(false);
                    nf.setMaximumFractionDigits(DF_FRAC_DIGITS_NUM);
                    
                    String lp = nf.format(listPrice);
                    if (lp.startsWith(DF_INTZERO)) {
                        lp = lp.substring(1);
                    }
                    if (lp.endsWith(DF_FRACZERO)) {
                        lp = lp.substring(0, lp.length() - 2);
                    }
                    printWriter.print(lp);
                }
            }
        }
        printWriter.print(DF_DELIMETER);
        
        Boolean onSale = (Boolean) sku.getPropertyValue(PROD_SKU_PROP_ONSALE);
        if (onSale != null) {
            if (onSale) {
                printWriter.print(DF_INT_TRUE);
            } else {
                printWriter.print(DF_INT_FALSE);
            }
        }
        printWriter.print(DF_DELIMETER);
        
        RepositoryItem salePriceList = priceListManager.getPriceList(null, PLM_SALEPL);
        if (salePriceList != null) {
            RepositoryItem price = priceListManager.getPrice(salePriceList, null, sku);
            if (price != null) {
                Double salePrice = (Double) price.getPropertyValue(PLM_LP);
                if (salePrice != null) {
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setGroupingUsed(false);
                    nf.setMaximumFractionDigits(DF_FRAC_DIGITS_NUM);
                    
                    String sp = nf.format(salePrice);
                    if (sp.startsWith(DF_INTZERO)) {
                        sp = sp.substring(1);
                    }
                    if (sp.endsWith(DF_FRACZERO)) {
                        sp = sp.substring(0, sp.length() - 2);
                    }
                    printWriter.print(sp);
                }
            }
        }
        printWriter.print(DF_DELIMETER);
        
        Boolean epfe = (Boolean) sku.getPropertyValue(PROD_SKU_PROP_EXOPFE);
        if (epfe != null) {
            if (epfe) {
                printWriter.print(DF_MSG_FREE_DELIV);
            } else {
                double cost = shippingCalculator.getSkuShipmentFee(sku);
                if (cost == 0) {
                    printWriter.print(DF_MSG_FREE_DELIV);
                } else {
                    String p = null;
                    
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setGroupingUsed(false);
                    nf.setMaximumFractionDigits(DF_FRAC_DIGITS_NUM);
                    
                    p = nf.format(cost);
                
                    if (p.startsWith(DF_INTZERO)) {
                        p = p.substring(1);
                    }
                    if (p.endsWith(DF_FRACZERO)) {
                        p = p.substring(0, p.length() - 2);
                    }
                    printWriter.print(p);
                }
            }
        }
        printWriter.print(DF_DELIMETER);
        
        String transporteur = (String) sku.getPropertyValue(PROD_SKU_PROP_TRANSP);
        Double daf = (Double) sku.getPropertyValue(PROD_SKU_PROP_DELAY);
        if (daf == null) {
            daf = 0.0;
        }
        Integer typeExp = (Integer) sku.getPropertyValue(PROD_SKU_PROP_TYPEEXP);
        if (typeExp == null) {
            typeExp = 0;
        }
        double delay;
        if (typeExp == 1) {
            if (stockLevel == 0) {
                if (DF_COND_TRANSP.equals(transporteur)) {
                    delay = daf + 5;
                } else {
                    delay = daf + 7;
                }
            } else {
                if (DF_COND_TRANSP.equals(transporteur)) {
                    delay = 3.0;
                } else {
                    delay = 5.0;
                }
            }
        } else if (typeExp == 2) {
            delay = daf + 2;
        } else {
            delay = -3.0;
        }
        if (delay > DF_COND_DELAY_MORE_6) {
            printWriter.print(DF_MSG_DELAY_6);
        } else if (delay > DF_COND_DELAY_MORE_5) {
            printWriter.print(DF_MSG_DELAY_5);
        } else if (delay > DF_COND_DELAY_MORE_4) {
            printWriter.print(DF_MSG_DELAY_4);
        } else if (delay > DF_COND_DELAY_MORE_3) {
            printWriter.print(DF_MSG_DELAY_3);
        } else if (delay > DF_COND_DELAY_MORE_2) {
            printWriter.print(DF_MSG_DELAY_2);
        } else if (delay > DF_COND_DELAY_MORE_1) {
            printWriter.print(DF_MSG_DELAY_1);
        } else {
            printWriter.print(DF_MSG_DELAY_6);
        }
        printWriter.print(DF_DELIMETER);
        
        Integer typeArticle = (Integer) sku.getPropertyValue(PROD_SKU_PROP_TA);
        if (typeArticle == null) {
            typeArticle = 0;
        }
        if (typeArticle == DF_COND_VENDU_INT_MAG) {
            printWriter.print(DF_MSG_VENDU_INT_MAG);
        } else if (typeArticle == DF_COND_VENDU_INT) {
            printWriter.print(DF_MSG_VENDU_INT);
        } else if (typeArticle == DF_COND_VENDU_MAG) {
            printWriter.print(DF_MSG_VENDU_MAG);
        } else {
            printWriter.print(DF_MSG_TA_UNKNOWN);
        }
        printWriter.print(DF_DELIMETER);
        
        Integer rmmc = (Integer) sku.getPropertyValue(PROD_SKU_PROP_RMMC);
        if (rmmc != null) {
            printWriter.print(rmmc);
        }
        printWriter.print(DF_DELIMETER);
        
        Boolean displayDiscount = (Boolean) sku.getPropertyValue(PROD_SKU_PROP_DD);
        if (displayDiscount != null) {
            if (displayDiscount) {
                printWriter.print(DF_INT_TRUE);
            } else {
                printWriter.print(DF_INT_FALSE);
            }
        }
        
        printWriter.println();
    }
}
