package com.castorama.integration.infokiosk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.transaction.SystemException;

import atg.adapter.gsa.xml.TemplateParser;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.DynamoEnv;
import atg.nucleus.ServiceException;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.castorama.integration.Constants;
import com.castorama.integration.IntegrationBase;
import com.castorama.integration.util.MiscUtils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * 
 * 
 * @author EPAM team
 * @version
 */
public class ImportProductManager extends IntegrationBase {
    
    /* Project import information */
    private static final int SUCCESS_IMPORT = 0;
    
    /* ProductCatalog repository information */
    private static final String REPO_ITEM_DESCR_CAT_FOLDER = "catalogFolder";
    private static final String REPO_ITEM_DESCR_CAT = "catalog";
    private static final String REPO_ITEM_DESCR_CATEGORY = "casto_category";
    private static final String REPO_ITEM_DESCR_SKU = "casto_sku";
    private static final String REPO_ITEM_DESCR_CATEGORY_TYPE = "casto_ik_category_type";
    
    private static final String REPO_PROPERTY_CAT_FOLDER_CHILD_ITEMS = "childItems";
    private static final String REPO_PROPERTY_SKU_PARENT_PRODUCTS = "parentProducts";
    
    private static final String REPO_FILTER_SKU_CODE_ARTICLE = "CodeArticle = ?0";
    private static final String REPO_FILTER_CAT_TYPES_ALL = "ALL";
    
    private static final String REPO_CATEGORY_ID_PREFIX = "cat_kiosk_id_";
    
    private static final String REPO_SKU_CASTO_PREFIX = "Casto";
    private static final String REPO_CS_SKU_PREFIX = "cross";
    
    private static final String REPO_CAT_FOLDER_ID = "CatalogFolder";
    
    /* InfoKiosk data file information */
    private static final String INPUT_DATA_FILE_EXTENSION = ".xls";
    private static final int ROW_START_FROM = 4;
    private static final int COLUMNS_NUMBER = 43;
    private static final int CODE_ARTICLE_COLUMN_NUM = 32;
    
    /* Import file information */
    private static final String OUTPUT_DATA_FILE_EXTENSION = ".xml";
    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
    private static final String XML_DOCTYPE = "<!DOCTYPE gsa-template SYSTEM \"dynamosystemresource:/atg/dtds/gsa/gsa_1.0.dtd\">";
    private static final String TAG_IMPORT_ITEMS = "<" + TemplateParser.TAG_IMPORT_ITEMS + ">\n";
    private static final String END_TAG_IMPORT_ITEMS = "</" + TemplateParser.TAG_IMPORT_ITEMS + ">\n";
    
    private static final String ADD_CATALOG_FOLDER_ITEM_START = "<add-item item-descriptor=\"catalogFolder\" id=\"CatalogFolder\" >\n";
    private static final String ADD_CATALOG_ITEM_START = "<add-item item-descriptor=\"catalog\" id=\"";
    private static final String ADD_CATEGORY_ITEM_START = "<add-item item-descriptor=\"casto_category\" id=\"";
    private static final String ADD_SKU_ITEM_START = "<add-item item-descriptor=\"casto_sku\" id=\"";
    private static final String ADD_CS_SKU_ITEM_START = "<add-item item-descriptor=\"CrossSellingSku\" id=\"";
    private static final String ADD_CAT_TYPE_ITEM_START = "<add-item item-descriptor=\"casto_ik_category_type\" id=\"";
    private static final String ADD_ITEM_START_CLOSE = "\" >\n";
    private static final String ADD_ITEM_END = "</add-item>\n\n";
    
    private static final String SET_PROPERTY_CHILD_ITEMS_START = "\t<set-property name=\"childItems\"><![CDATA[";
    private static final String SET_PROPERTY_ROOT_CATS_START = "\t<set-property name=\"rootCategories\"><![CDATA[";
    private static final String SET_PROPERTY_DISPLAY_NAME_START = "\t<set-property name=\"displayName\"><![CDATA[";
    private static final String SET_PROPERTY_TYPE_START = "\t<set-property name=\"ikCatType\"><![CDATA[";
    private static final String SET_PROPERTY_TYPE_NAV_START = "\t<set-property name=\"ikCatTypeNav\"><![CDATA[";
    private static final String SET_PROPERTY_PARENT_CAT_START = "\t<set-property name=\"parentCategory\"><![CDATA[";
    private static final String SET_PROPERTY_CHILD_CATS_START = "\t<set-property name=\"fixedChildCategories\"><![CDATA[";
    private static final String SET_PROPERTY_CHILD_PRODS_START = "\t<set-property name=\"fixedChildProducts\"><![CDATA[";
    private static final String SET_PROPERTY_CODE_ARTICLE_START = "\t<set-property name=\"CodeArticle\"><![CDATA[";
    private static final String SET_PROPERTY_CROSS_SELLS_START = "\t<set-property name=\"ikCrossSelling\"><![CDATA[";
    private static final String SET_PROPERTY_SKU_START = "\t<set-property name=\"sku\"><![CDATA[";
    private static final String SET_PROPERTY_END = "]]></set-property>\n";
    
    private static final String DELIMETER_COMMA = ",";
    
    /* Log file information */
    private static final String LOG_FILE_EXTENSION = ".log";
    
    /* bornesCatalogId property */
    private String bornesCatalogId;
    
    /* bornesCatalogName property */
    private String bornesCatalogName;
    
    /* rootCategoryName property */
    private String rootCategoryName;
    
    /* import project properties */
    private String projectName;
    private String userName;
    private String comment;
    private String deploymentWorkflow;
    
    /* idGen property */
    private AtomicInteger idGen = null;

    /* inProgress flag property */
    private boolean inProgress = false;
    
    /* logging properties */
    private int skuInXml;
    private int skuNotFound;
    
    public String getBornesCatalogId() {
        return bornesCatalogId;
    }

    public void setBornesCatalogId(String bornesCatalogId) {
        this.bornesCatalogId = bornesCatalogId;
    }

    public String getBornesCatalogName() {
        return bornesCatalogName;
    }

    public void setBornesCatalogName(String bornesCatalogName) {
        this.bornesCatalogName = bornesCatalogName;
    }

    public String getRootCategoryName() {
        return rootCategoryName;
    }

    public void setRootCategoryName(String rootCategoryName) {
        this.rootCategoryName = rootCategoryName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDeploymentWorkflow() {
        return deploymentWorkflow;
    }

    public void setDeploymentWorkflow(String deploymentWorkflow) {
        this.deploymentWorkflow = deploymentWorkflow;
    }

    /** */
    public void importProducts() throws ServiceException {
        if (inProgress) {
            throw new ServiceException(
                    "Unable to start products import... The service is already started. Please wait...");
        }

        if (isLoggingDebug()) {
            logDebug("start - ImportProductManager - importProducts");
        }

        inProgress = true;

        try {
            generateXmlImportFiles();
            importXmlFiles();
        } finally {
            inProgress = false;
        }

        if (isLoggingDebug()) {
            logDebug("finish - ImportProductManager - importProducts");
        }
    }

    /**
     * generates xml import files from xls and stores it in output directory
     */
    public void generateXmlImportFiles() {

        if (isLoggingDebug()) {
            logDebug("Generating XML import files...");
        }
        
        idGen = new AtomicInteger(1);
        
        try {
            if (isBornesCatalogCreated()) {
                if (isLoggingWarning()) {
                    logWarning("Bornes Catalog is already created. Process will be stopped!");
                }
                return;
            }
        
            if (isLoggingInfo()) {
                logInfo("Processing xls files...");
            }
            
            checkMandatoryFolders();
            
            File outputDir = getDir(Constants.OUTPUT_FOLDER);
            
            File xlsFile = null;
            while ((xlsFile = getWorkingFile()) != null) {
                if (isLoggingInfo()) {
                    logInfo("Processing: " + xlsFile);
                }
                
                File xmlFile = null;
                PrintWriter printWriter = null;
                
                try {
                
                    InfoKioskCategory borneMagasin = parseInfoKioskXls(xlsFile);
                    populateCategoriesWithId(borneMagasin);
                    
                    if ((borneMagasin.getSubcategories() != null) 
                            && (borneMagasin.getSubcategories().size() > 0)) {
                        
                        xmlFile = new File(outputDir, getFileName(xlsFile) 
                                + OUTPUT_DATA_FILE_EXTENSION);
                        printWriter = new PrintWriter(xmlFile, ENCODING);
    
                        printWriter.println(XML_HEADER);
                        printWriter.println(XML_DOCTYPE);
                        printWriter.println(TAG_GSA_TEMPLATE);
                        printWriter.println(TAG_IMPORT_ITEMS);
                        
                        StringBuilder sb = new StringBuilder();
                        
                        Set<Integer> createdCsSkus = new HashSet<Integer>();
                        Set<Integer> notCheckedCsSkus = new HashSet<Integer>();
                        Set<Integer> notFoundCsSkus = new HashSet<Integer>();
                        Set<String> catTypes = getAvailableCatTypes();
                        
                        if (isLoggingDebug()) {
                            logDebug("Available category types : " + catTypes.toString());
                        }
                        
                        skuInXml = 0;
                        skuNotFound = 0;
                        
                        generateBornesXml(borneMagasin, null, null, 
                                sb, createdCsSkus, notCheckedCsSkus, notFoundCsSkus, catTypes);
                        
                        printWriter.print(sb.toString());
                        
                        printWriter.println(END_TAG_IMPORT_ITEMS);
                        printWriter.println(END_TAG_GSA_TEMPLATE);
                        
                        if (isLoggingInfo()) {
                            logInfo("Number of SKUs in xml file : " + skuInXml);
                            logInfo("Number of SKUs not found in repository : " + skuNotFound);
                            logInfo("Number of cross selling SKUs in xml file : " + createdCsSkus.size());
                            logInfo("Number of cross selling SKUs not found in repository : " + notFoundCsSkus.size());
                            
                            int notCheckedCs = 0;
                            for (Integer ca : notCheckedCsSkus) {
                                if ((!createdCsSkus.contains(ca)) && (!notFoundCsSkus.contains(ca))) {
                                    notCheckedCs++;
                                }
                            }
                            
                            logInfo("Number of cross selling SKUs not checked : " + notCheckedCs);
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
                    if (xmlFile != null) {
                        xmlFile.delete();
                    }
                } finally {
                    if (printWriter != null) {
                        printWriter.close();
                        printWriter = null;
                    }
                    
                    if (xlsFile != null) {
                        xlsFile.delete();
                    }
                }
            }
            
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e);
            }
        }
        
        if (isLoggingDebug()) {
            logDebug("XML import files generated.");
        }
    }
    
    /**
     * imports xml files
     */
    public void importXmlFiles() {
        
        if (isLoggingDebug()) {
            logDebug("Importing XML files...");
        }
        
        File logFile = null;
        PrintWriter logWriter = null;
        
        try {
            File[] importFiles = getDir(Constants.OUTPUT_FOLDER).listFiles();
            if ((importFiles != null) && (importFiles.length > 0)) {
                for (File f : importFiles) {
                    if (f.isFile() && f.exists()) {
                        if (f.getName().endsWith(OUTPUT_DATA_FILE_EXTENSION)) {
                            
                            logFile = new File(getWorkingDir(), getFileName(f) + LOG_FILE_EXTENSION);
                            logWriter = new PrintWriter(logFile);
                            
                            if (isLoggingInfo()) {
                                logInfo("Importing file : " + f.getName());
                            }
                            
                            boolean result = false;
                            try {
                                result = importXmlProject(f.getAbsolutePath(), logWriter);
                            } finally {
                                if (logWriter != null) {
                                    logWriter.close();
                                }
                            }
                            
                            if (isLoggingInfo()) {
                                if (result) {
                                    logInfo("Import of file : " + f.getName() + " succeed");
                                } else {
                                    logInfo("Import of file : " + f.getName() + " failed"); 
                                }
                            }
                            
                            File destDir = null;
                            if (result) {
                                destDir = getArchiveDir();
                            } else {
                                destDir = getErrorDir(); 
                            }
                            
                            MiscUtils.copyFile(f, new File(destDir.getAbsolutePath() 
                                    + File.separator + f.getName()));
                            f.delete();
                            
                            MiscUtils.copyFile(logFile, new File(destDir.getAbsolutePath() 
                                    + File.separator + logFile.getName()));
                            logFile.delete();
                            
                        } else {
                            if (isLoggingWarning()) {
                                logWarning("Unknown file in output directory : " + f.getName());
                            }
                        }
                    }
                }
            }
            
        } catch (IOException ioe) {
            if (isLoggingError()) {
                logError(ioe);
            }
        }
        
        if (isLoggingDebug()) {
            logDebug("XML import files import finished.");
        }
    }
    
    private File getWorkingFile() throws IOException {
        File inputDir = new File(getRootFolder(), Constants.INPUT_FOLDER);
        File[] inpFiles = inputDir.listFiles();

        File inpFile = null;
        File workFile = null;
        if ((inpFiles != null) && (inpFiles.length > 0)) {
            for (File f : inpFiles) {
                if (f.isFile() && f.exists()) {
                    if (f.getName().endsWith(INPUT_DATA_FILE_EXTENSION)) {
                        inpFile = f;
                        break;
                    } else {
                        if (isLoggingWarning()) {
                            logWarning("Unknown file in input directory : " + f.getName());
                        }
                    }
                }
            }
        }
        
        if (inpFile != null) {
            String name = getFileName(inpFile);
            String workName =
                name + "_" + Constants.DATE_FORMAT_ARCHIVE.format(new Date()) +
                inpFile.getName().substring(name.length(), inpFile.getName().length());
            workFile = new File(getWorkingDir(), workName);

            MiscUtils.copyFile(inpFile, workFile);

            inpFile.delete();
        }
        
        return workFile;
    }
    
    private InfoKioskCategory parseInfoKioskXls(File xlsFile) throws IOException {
        
        if (isLoggingDebug()) {
            logDebug("Start parsing infokiosk xls...");
        }
        
        int skuInXls = 0;
        Set<Integer> uniqueCs = new HashSet<Integer>();
        
        InfoKioskCategory borneMagasin = new InfoKioskCategory(rootCategoryName, null, null);
        
        HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(xlsFile));
        HSSFSheet sheet = wb.getSheetAt(0);
        int rowsNum = sheet.getPhysicalNumberOfRows();
        
        if (isLoggingDebug()) {
            logDebug("Physical number of rows in xls = " + rowsNum);
            logDebug("Start parsing data from row " + ROW_START_FROM);
        }
        
        for (int r = ROW_START_FROM - 1; r < rowsNum; r++) {
            HSSFRow row = sheet.getRow(r);
            if (row != null) {
                int columnNum = 0;
                
                if (getCellValue(row.getCell(columnNum)) == null) {
                    if (isLoggingDebug()) {
                        logDebug("Real number of rows in xls = " + (r - ROW_START_FROM + 1));
                    }
                    break;
                }
                
                List<String> debugString = new ArrayList<String>();
                
                InfoKioskCategory currCat = borneMagasin;
                
                while (columnNum < CODE_ARTICLE_COLUMN_NUM) {
                    
                    String typeCat = null;
                    if (columnNum != 0) {
                        if (getCellValue(row.getCell(columnNum)) != null) {
                            typeCat = getCellValue(row.getCell(columnNum)).toString();
                        }
                        columnNum++;
                    }
                    String catName = null;
                    if (getCellValue(row.getCell(columnNum)) != null) {
                        catName = getCellValue(row.getCell(columnNum)).toString();
                    }
                    columnNum++;
                    
                    String typeNavCat = null;
                    if (getCellValue(row.getCell(columnNum)) != null) {
                        typeNavCat = getCellValue(row.getCell(columnNum)).toString();
                    }
                    columnNum++;
                    
                    if (catName == null) {
                        break;
                    }
                    
                    if (isLoggingDebug()) {
                        if (debugString.size() != 0) {
                            debugString.add(typeCat);
                        }
                        debugString.add(catName);
                        debugString.add(typeNavCat);
                    }
                    
                    if (currCat.getSubcategories() == null) {
                        currCat.setSubcategories(new HashMap<String, InfoKioskCategory>());
                    }
                    Map<String, InfoKioskCategory> subcats = currCat.getSubcategories();
                    
                    if (!subcats.containsKey(catName)) {
                        currCat = new InfoKioskCategory(catName, typeCat, typeNavCat);
                        subcats.put(catName, currCat);
                    } else {
                        currCat = subcats.get(catName);    
                    }
                }
                
                columnNum = CODE_ARTICLE_COLUMN_NUM;
                
                Integer codeArticle = null;
                if (getCellValue(row.getCell(columnNum)) instanceof Integer) {
                    codeArticle = (Integer) getCellValue(row.getCell(columnNum));
                } else {
                    String ca = null;
                    try {
                        ca = (String) getCellValue(row.getCell(columnNum));
                        codeArticle = Integer.parseInt(ca);
                    } catch (NumberFormatException nfe) {
                        if (isLoggingWarning()) {
                            logWarning("CodeArticle must be integer : " + ca);
                        }
                        continue;
                    }
                }
                columnNum++;
                
                skuInXls++;
                
                if (isLoggingDebug()) {
                    debugString.add(codeArticle.toString());
                }
                
                List<Integer> crossSellings = new ArrayList<Integer>();
                while (columnNum < COLUMNS_NUMBER) {
                    
                    Integer crossSelling = null;
                    if (getCellValue(row.getCell(columnNum)) instanceof Integer) {
                        crossSelling = (Integer) getCellValue(row.getCell(columnNum));
                        if (crossSelling == null) {
                            break;
                        }
                    } else {
                        String cs = (String) getCellValue(row.getCell(columnNum));
                        if (cs == null) {
                            break;
                        }
                        
                        try {
                            crossSelling = Integer.parseInt(cs);
                        } catch (NumberFormatException nfe) {
                            if (isLoggingWarning()) {
                                logWarning("CrossSellings must be integer : " + cs);
                            }
                        }
                    }
                    columnNum++;
                    
                    if (!uniqueCs.contains(crossSelling)) {
                        uniqueCs.add(crossSelling);
                    }
                    
                    crossSellings.add(crossSelling);
                    
                    if (isLoggingDebug()) {
                        debugString.add(crossSelling.toString());
                    }
                }
                
                if (currCat.getSkus() == null) {
                    currCat.setSkus(new HashMap<Integer, List<Integer>>());
                }
                
                currCat.getSkus().put(codeArticle, crossSellings);
                
                if (isLoggingDebug()) {
                    logDebug("Row " + (r + 1) + " : " + debugString.toString());
                }
                
            } else {
                if (isLoggingWarning()) {
                    logWarning("Xls file contains empty row : " + (r + 1));
                }
            }
        }
        
        if (isLoggingInfo()) {
            logInfo("Number of SKUs found in xls file : " + skuInXls);
            logInfo("Number of unique cross selling SKUs found in xls file : " + uniqueCs.size());
        }
        
        if (isLoggingDebug()) {
            logDebug("Finish parsing infokiosk xls...");
        }
        
        return borneMagasin;
    }
    
    private Object getCellValue(HSSFCell cell) {
        Object value = null;

        if (cell != null) {
            if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                value = cell.getStringCellValue();
            } else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                value = (Integer) ((Double) cell.getNumericCellValue()).intValue();
            } else if (cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
            } else {
                if (isLoggingWarning()) {
                    logWarning("Cell (row: " + (cell.getRowIndex() + 1) + "; col: " + 
                            (cell.getColumnIndex() + 1) + ") contains wrong data");
                }
            }
        }

        return value;
    }
    
    private String generateCategoryId() throws Exception {
        // cat_kiosk_id_0001 .. cat_kiosk_id_9999
        
        int id = idGen.getAndIncrement();
        if (id == 9999) {
            throw new Exception("Maximum category id is reached! Process will be stopped!");
        }
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        nf.setMinimumIntegerDigits(4);
        
        return REPO_CATEGORY_ID_PREFIX + nf.format(id);
    }
    
    private String populateCategoriesWithId(InfoKioskCategory cat) throws Exception {
        String id = null;
        
        if (cat != null) {
            
            do {
                id = generateCategoryId();
            } while (getRepository().getItem(id, REPO_ITEM_DESCR_CATEGORY) != null);
            
            cat.setId(id);
            
            if (cat.getSubcategories() != null) {
                for (InfoKioskCategory subcat : cat.getSubcategories().values()) {
                    populateCategoriesWithId(subcat);
                }
            }
        }
        
        return id;
    }
    
    private boolean isBornesCatalogCreated() throws RepositoryException {
        RepositoryItem bornesCatalog = getRepository().getItem(bornesCatalogId,
                REPO_ITEM_DESCR_CAT);
        
        return (bornesCatalog != null);
    }
    
    @SuppressWarnings("unchecked")
    private void generateBornesXml(InfoKioskCategory cat, String parentCatId, String parentCatName, 
            StringBuilder sb, Set<Integer> createdCsSkus, Set<Integer> notCheckedCsSkus, 
            Set<Integer> notFoundCsSkus, Set<String> catTypes) throws RepositoryException {
        List<String> childs = new ArrayList<String>();
        
        if (cat != null) {
            if (cat.getSubcategories() != null) {
                for (InfoKioskCategory subcat : cat.getSubcategories().values()) {
                    childs.add(subcat.getId());
                    
                    generateBornesXml(subcat, cat.getId(), cat.getName(), sb, 
                            createdCsSkus, notCheckedCsSkus, notFoundCsSkus, catTypes);
                }
            }
            
            List<String> skusProds = generateSkusXml(cat, sb, createdCsSkus, 
                    notCheckedCsSkus, notFoundCsSkus);
            
            String catType = null;
            if (cat.getType() != null) {
                catType = cat.getType();
                if (!catTypes.contains(cat.getType())) {
                    
                    generateCatTypeXml(catType, sb);
                    catTypes.add(catType);
                    
                    if ((parentCatName != null) && (!parentCatName.equals(rootCategoryName))) {
                        if (isLoggingInfo()) {
                            logInfo("Adding new category type for category " + 
                                    cat.getName() + " : \"" +  catType + "\"");
                        }
                    }
                }
            } else {
                if ((parentCatName != null) && (!parentCatName.equals(rootCategoryName))) {
                    if (isLoggingWarning()) {
                        logWarning("No category type specified for category " + cat.getName());
                    }
                }
            }
            generateCategoryXml(cat.getId(), cat.getName(), catType, 
                    cat.getTypeNav(), parentCatId, childs, skusProds, sb);
            
            if (rootCategoryName.equals(cat.getName())) {
                generateCatalogXml(cat.getId(), sb);
                
                RepositoryItem catalogs = getRepository().getItem(REPO_CAT_FOLDER_ID,
                        REPO_ITEM_DESCR_CAT_FOLDER);
                List<RepositoryItem> childItems = (List<RepositoryItem>) 
                        catalogs.getPropertyValue(REPO_PROPERTY_CAT_FOLDER_CHILD_ITEMS);
                List<String> childsItemsIds = new ArrayList<String>();
                if (childItems != null) {
                    for (RepositoryItem childItem : childItems) {
                        if (!childItem.getRepositoryId().equals(bornesCatalogId)) {
                            childsItemsIds.add(childItem.getRepositoryId());
                        }
                    }
                }
                childsItemsIds.add(bornesCatalogId);
                
                generateCatalogFolderXml(childsItemsIds, sb);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private List<String> generateSkusXml(InfoKioskCategory cat, StringBuilder sb,
            Set<Integer> createdCsSkus, Set<Integer> notCheckedCsSkus, Set<Integer> notFoundCsSkus) 
            throws RepositoryException {
        
        List<String> skusProducts = new ArrayList<String>();
        
        Map<Integer, List<Integer>> skus = cat.getSkus();
        if (skus != null) {
            
            RqlStatement skuItemsRql = RqlStatement.parseRqlStatement(REPO_FILTER_SKU_CODE_ARTICLE);
            RepositoryView skuItemsView = getRepository().getView(REPO_ITEM_DESCR_SKU);
            
            for (Map.Entry<Integer, List<Integer>> sku : skus.entrySet()) {
                
                Object rqlparams[] = new Object[1];
                rqlparams[0] = sku.getKey();
                RepositoryItem[] skuItems = skuItemsRql.executeQuery(skuItemsView, rqlparams);
                
                if ((skuItems != null) && (skuItems.length > 0)) {
                    Set<RepositoryItem> prodsItems = (Set<RepositoryItem>) 
                        skuItems[0].getPropertyValue(REPO_PROPERTY_SKU_PARENT_PRODUCTS);
                    if (prodsItems != null) {
                        for (RepositoryItem prodItem : prodsItems) {
                            skusProducts.add(prodItem.getRepositoryId());
                        }
                    }
                    
                    List<Integer> presentCrossSkus = new ArrayList<Integer>();
                    for (Integer crossCA : sku.getValue()) {
                        if (!createdCsSkus.contains(crossCA)) {
                            rqlparams[0] = crossCA;
                            RepositoryItem[] skuCross = skuItemsRql.executeQuery(skuItemsView, rqlparams);
                            
                            if ((skuCross != null) && (skuCross.length > 0)) {
                                generateCsSkuXml(crossCA, skuCross[0].getRepositoryId(), sb);
                                createdCsSkus.add(crossCA);
                                
                                presentCrossSkus.add(crossCA);
                            } else {
                                if (!notFoundCsSkus.contains(crossCA)) {
                                    notFoundCsSkus.add(crossCA);
                                }
                                
                                if (isLoggingWarning()) {
                                    logWarning("Can't find cross SKU with CodeArticle = " + crossCA);
                                }
                            }
                        } else {
                            presentCrossSkus.add(crossCA);
                        }
                    }
                    
                    generateSkuXml(sku.getKey(), presentCrossSkus, sb);
                    skuInXml++;
                    
                } else {
                    skuNotFound++;
                    
                    for (Integer crossCA : sku.getValue()) {
                        if (!notCheckedCsSkus.contains(crossCA)) {
                            notCheckedCsSkus.add(crossCA);
                        }
                    }
                    
                    if (isLoggingWarning()) {
                        logWarning("Can't find SKU with CodeArticle = " + sku.getKey());
                    }
                }
            }
        }
        
        return skusProducts;
    }
    
    private Set<String> getAvailableCatTypes() throws RepositoryException {
        Set<String> catTypes = new HashSet<String>();
        
        RqlStatement catTypeItemsRql = RqlStatement.parseRqlStatement(REPO_FILTER_CAT_TYPES_ALL);
        RepositoryView catTypeItemsView = getRepository().getView(REPO_ITEM_DESCR_CATEGORY_TYPE);
        RepositoryItem[] catTypeItems = catTypeItemsRql.executeQuery(catTypeItemsView, new Object[0]);
        
        if (catTypeItems != null) {
            for (RepositoryItem catTypeItem : catTypeItems) {
                catTypes.add(catTypeItem.getRepositoryId());
            }
        }
        
        return catTypes;
    }
    
    private void generateCatalogFolderXml(List<String> childItems, StringBuilder sb) {
        sb.append(ADD_CATALOG_FOLDER_ITEM_START);
        if ((childItems != null) && (childItems.size() > 0)) {
            sb.append(SET_PROPERTY_CHILD_ITEMS_START);
            for (int i = 0; i < childItems.size(); i++) {
                sb.append(childItems.get(i));
                if (i < (childItems.size() - 1)) {
                    sb.append(DELIMETER_COMMA);
                }
            }
            sb.append(SET_PROPERTY_END);
        }
        sb.append(ADD_ITEM_END);
    }
    
    private void generateCatalogXml(String rootCatId, StringBuilder sb) {
        sb.append(ADD_CATALOG_ITEM_START).append(bornesCatalogId).append(ADD_ITEM_START_CLOSE);
        sb.append(SET_PROPERTY_DISPLAY_NAME_START).append(bornesCatalogName).append(SET_PROPERTY_END);
        if (rootCatId != null) {
            sb.append(SET_PROPERTY_ROOT_CATS_START).append(rootCatId).append(SET_PROPERTY_END);
        }
        sb.append(ADD_ITEM_END);
    }
    
    private void generateCategoryXml(String catId, String name, String type, String typeNav,
            String parentCatId, List<String> childCatsIds, List<String> childProdsIds, StringBuilder sb) {
        
        sb.append(ADD_CATEGORY_ITEM_START).append(catId).append(ADD_ITEM_START_CLOSE);
        sb.append(SET_PROPERTY_DISPLAY_NAME_START).append(name).append(SET_PROPERTY_END);
        if (type != null) {
            sb.append(SET_PROPERTY_TYPE_START).append(type).append(SET_PROPERTY_END);
        }
        if (typeNav != null) {
            sb.append(SET_PROPERTY_TYPE_NAV_START).append(typeNav).append(SET_PROPERTY_END);
        }
        if (parentCatId != null) {
            sb.append(SET_PROPERTY_PARENT_CAT_START).append(parentCatId).append(SET_PROPERTY_END);
        }
        if ((childCatsIds != null) && (childCatsIds.size() > 0)) {
            sb.append(SET_PROPERTY_CHILD_CATS_START);
            for (int i = 0; i < childCatsIds.size(); i++) {
                sb.append(childCatsIds.get(i));
                if (i < (childCatsIds.size() - 1)) {
                    sb.append(DELIMETER_COMMA);
                }
            }
            sb.append(SET_PROPERTY_END);
        }
        if ((childProdsIds != null) && (childProdsIds.size() > 0)) {
            sb.append(SET_PROPERTY_CHILD_PRODS_START);
            for (int i = 0; i < childProdsIds.size(); i++) {
                sb.append(childProdsIds.get(i));
                if (i < (childProdsIds.size() - 1)) {
                    sb.append(DELIMETER_COMMA);
                }
            }
            sb.append(SET_PROPERTY_END);
        }
        sb.append(ADD_ITEM_END);
    }
    
    private void generateSkuXml(Integer codeArticle, List<Integer> crossSells, StringBuilder sb) {
        
        sb.append(ADD_SKU_ITEM_START).append(REPO_SKU_CASTO_PREFIX + codeArticle)
                .append(ADD_ITEM_START_CLOSE);
        sb.append(SET_PROPERTY_CODE_ARTICLE_START).append(codeArticle).append(SET_PROPERTY_END);
        if ((crossSells != null) && (crossSells.size() > 0)) {
            sb.append(SET_PROPERTY_CROSS_SELLS_START);
            for (int i = 0; i < crossSells.size(); i++) {
                sb.append(REPO_CS_SKU_PREFIX + crossSells.get(i));
                if (i < (crossSells.size() - 1)) {
                    sb.append(DELIMETER_COMMA);
                }
            }
            sb.append(SET_PROPERTY_END);
        }
        sb.append(ADD_ITEM_END);
    }
    
    private void generateCsSkuXml(Integer csSkuCA, String skuId, StringBuilder sb) {
        sb.append(ADD_CS_SKU_ITEM_START).append(REPO_CS_SKU_PREFIX + csSkuCA)
                .append(ADD_ITEM_START_CLOSE);
        sb.append(SET_PROPERTY_SKU_START).append(skuId).append(SET_PROPERTY_END);
        sb.append(ADD_ITEM_END);
    }
    
    private void generateCatTypeXml(String catType, StringBuilder sb) {
        sb.append(ADD_CAT_TYPE_ITEM_START).append(catType).append(ADD_ITEM_START_CLOSE);
        sb.append(ADD_ITEM_END);
    }
    
    private boolean importXmlProject(String xmlFileName, PrintWriter logWriter) {
        String projName = projectName + Constants.DATE_FORMAT_PROJECT.format(new Date());
        
        if (isLoggingDebug()) {
            logDebug("start - importXmlProject: projectName=" + projName);
        }
        
        List<String> list = new ArrayList<String>();

        String dataDirProp = DynamoEnv.getProperty("atg.dynamo.data-dir");
        String serverNameProp = DynamoEnv.getProperty("atg.dynamo.server.name");
        String configPath = getNucleus().getConfigPath(serverNameProp);

        if (!StringUtils.isBlank(dataDirProp) && !StringUtils.isBlank(serverNameProp)) {
            configPath += File.pathSeparator + dataDirProp + "/servers/" + serverNameProp + "/localconfig";
        }

        String database = ((atg.adapter.gsa.GSARepository) getRepository()).getDatabaseName();
        String repository = ((atg.adapter.gsa.GSARepository) getRepository()).getAbsoluteName();

        list.add("-database");
        list.add(database);

        list.add("-configPath");
        list.add(configPath);

        list.add("-repository");
        list.add(repository);

        list.add("-project");
        list.add(projName);

        list.add("-user");
        list.add(userName);

        list.add("-comment");
        list.add(comment);

        list.add("-workflow");
        list.add(deploymentWorkflow);

        list.add("-import");
        list.add(xmlFileName);

        if (isLoggingDebug()) {
            logDebug("arguments: " + list);
        }

        int res = -1;
        boolean result = false;
        boolean rollback = true;
        
        TransactionDemarcation trd = new TransactionDemarcation();
        try {
            try {
                getTransactionManager().setTransactionTimeout(getTransactionTimeout());
                trd.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);
                
                res = TemplateParser.runParser(list.toArray(new String[0]), logWriter);

                if (res == SUCCESS_IMPORT) {
                    rollback = false;
                    result = true;
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
                logError("setTransactionTimeout failed", e);
            }
        } catch (TransactionDemarcationException e) {
            if (isLoggingError()) {
                logError("Creating transaction demarcation failed", e);
            }
        }

        if (isLoggingDebug()) {
            logDebug("finish -  importXmlProject. Result : " + result);
        }
        
        return result;
    }
}
