package com.castorama.integration;

import java.text.SimpleDateFormat;

public class Constants {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String SEPARATOR = "/";
    // folder names
    public static final String ARCHIVE_FOLDER = "archive";
    public static final String INPUT_FOLDER = "input";
    public static final String WORKING_FOLDER = "working";
    public static final String ERROR_FOLDER = "error";
    public static final String OUTPUT_FOLDER = "output";
    public static final String STATUS_FOLDER = "status";

    // date formats
    public static final SimpleDateFormat DATE_FORMAT_YYYYMMDD = new SimpleDateFormat("yyyyMMdd");
    public static final SimpleDateFormat DATE_FORMAT_STATUS = new SimpleDateFormat("MM/dd/yyyy HH:mmaaa");
    public static final SimpleDateFormat DATE_FORMAT_ARCHIVE = new SimpleDateFormat("yyyyMMdd_HH-mm-ss");
    public static final SimpleDateFormat DATE_FORMAT_CSV = new SimpleDateFormat("yyyyMMdd_HH-mm-ss");
    public static final SimpleDateFormat DATE_FORMAT_EXPERIAN = new SimpleDateFormat("dd/MM/yyyy");
    public static final SimpleDateFormat DATE_FORMAT_PROJECT = new SimpleDateFormat("MMM d yyyy HH:mmaaa");
    public static final SimpleDateFormat DATE_FORMAT_BDDCC = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat DATE_FORMAT_BDDCC_HEADER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    public static final SimpleDateFormat DATE_FORMAT_LOG = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
    // properties names
    public static final String PROPERTY_CODE_ARTICLE = "CodeArticle";
    public static final String PROPERTY_PLUS_DU_PRODUIT = "PlusDuProduit";
    public static final String PROPERTY_DISPLAY_NAME = "displayName";
    public static final String PROPERTY_LIBELLE_CLIENT_LONG = "LibelleClientLong";
    public static final String PROPERTY_DESCRIPTION = "description";
    public static final String PROPERTY_GARANTIE = "Garantie";
    public static final String PROPERTY_POIDSUV = "PoidsUV";

    public static final String PROPERTY_LARGE_IMAGE = "largeImage";
    public static final String PROPERTY_URL_PICTO = "urlPicto";
    public static final String PROPERTY_URL = "url";
    public static final String PROPERTY_PATH = "path";
    public static final String PROPERTY_MARQUE_COMMERCIALE = "MarqueCommerciale";

    public static final String PROPERTY_MEDIA_MARQUE = "mediaMarque";
    public static final String PROPERTY_SKU_ID = "SKU_ID";
    public static final String PROPERTY_DATE_EXPORT = "DATE_EXPORT";
    public static final String PROPERTY_IMAGE_URL = "IMAGE_URL";
    public static final String PROPERTY_BRAND_URL = "BRAND_URL";
    public static final String PROPERTY_IMAGE_DATE_EXPORT = "IMAGE_DATE_EXPORT";
    public static final String PROPERTY_BRAND_DATE_EXPORT = "BRAND_DATE_EXPORT";
    
    public static final String PROPERTY_PARENT_PRODUCTS = "parentProducts";
    public static final String PROPERTY_LONG_DESCRIPTION = "longDescription";
    public static final String PROPERTY_NORMES_TEXT = "normesText";
    public static final String PROPERTY_RESTRICTION_USAGE = "RestrictionsUsage";
    public static final String PROPERTY_CONTRAINTES_UTILISATION = "ContraintesUtilisation";

    public static final String PROPERTY_LAST_NAME = "lastName"; 
    public static final String PROPERTY_FIRST_NAME = "firstName"; 
    public static final String PROPERTY_LOGIN = "login"; 
    public static final String PROPERTY_CIVILITE = "civilite"; 
    //item descriptions
    public static final String ITEM_MARQUE = "marque";
    public static final String ITEM_CASTO_SKU = "casto_sku";
    public static final String ITEM_USER = "user";
    //file naming
    public static final SimpleDateFormat DATA_FILE_NAME_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmm");
    public static final String DATA_FILE_NAME_PREFIX = "CASTORAMA_";
    public static final String MASS_DATA_FILE_NAME_PREFIX = "CASTORAMA_mass_";
    public static final String DATA_FILE_EXTENSION = ".csv";
    public static final String LOG_FILE_EXTENSION = ".log";

}
