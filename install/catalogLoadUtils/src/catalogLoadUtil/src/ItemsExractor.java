import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.StringEscapeUtils;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import atg.adapter.gsa.xml.DoneException;
import atg.adapter.gsa.xml.ImportFileParser;
import atg.adapter.gsa.xml.ImportItem;
import atg.adapter.gsa.xml.TemplateParser;
import atg.xml.XMLFileEntityResolver;

public class ItemsExractor extends DefaultHandler implements ContentHandler, ErrorHandler {

	private static final String ITEM_OPE_SPECIALE = "ope_speciale";
	private static final String ITEM_OUVERTURE_EXCEP = "ouverture_excep";
	private static final String ITEM_SERVICE = "service";
	private static final String ITEM_OFFRE = "offre";
	private static final String ITEM_ACTUALITE = "actualite";
	private static final String ITEM_DEPARTEMENT = "departement";
	private static final String ITEM_ADRESSE = "adresse";
	private static final String ITEM_ENTITE = "entite";
	private static final String ITEM_SERVICE_L = "serviceL";
	private static final String ITEM_MAGASIN_SERVICE_ASSOCIATION = "magasinServiceAssociation";
	private static final String ITEM_IMAGE_AUX = "image_aux";
	private static final String ITEM_REGION = "region";
	private static final String ITEM_MAGASIN = "magasin";
	private static final String UTF_8 = "UTF-8";
	private static final String IMPORTS_END_TAG = "</imports>";
	private static final String IMPORTS_START_TAG = "<imports>\n";
	private static final String DOCTYPE_IMPORTS = "<!DOCTYPE imports SYSTEM \"dynamosystemresource:/atg/dtds/gsa/multi_import_1.0.dtd\">\n";
	private static final String DOCTYPE_GSA_TEMPLATE = "<!DOCTYPE gsa-template SYSTEM \"dynamosystemresource:/atg/dtds/gsa/gsa_1.0.dtd\">\n";
	private static final String GSA_TEMPLATE_END_TAG = "</gsa-template>";
	private static final String GSA_TEMPLATE_START_TAG = "<gsa-template>\n";
	private static final String XML_VERSION = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n";

	private static final String ITEM_REPONSE_AFFINAGE = "reponseAffinage";
	private static final String ITEM_QUESTION_AFFINAGE = "questionAffinage";
	private static final String ITEM_CODES_UNITE = "codesUnite";
	private static final String ITEM_COULEUR = "couleur";
	private static final String ITEM_ATTRIBUTS_CASTO = "Attributs Casto";
	private static final String ITEM_NUANCIER = "nuancier";
	private static final String ITEM_MARQUE = "marque";
	private static final String ITEM_CROSS_SELLING_SKU = "CrossSellingSku";
	private static final String ITEM_CASTO_SKUS_MAGASINS = "casto_skus_magasins";
	private static final String ITEM_MEDIA_INTERNAL_BINARY = "media-internal-binary";
	private static final String ITEM_MEDIA_INTERNAL_TEXT = "media-internal-text";
	private static final String ITEM_MEDIA_EXTERNAL = "media-external";
	private static final String ITEM_FOLDER = "folder";
	private static final String ITEM_CATEGORY = "category";
	private static final String ITEM_CASTO_INSPI = "casto_inspi";
	private static final String ITEM_CASTO_CATEGORY = "casto_category";
	private static final String ITEM_CONFIGURABLE_SKU = "configurableSku";
	private static final String ITEM_SKU_LINK = "sku-link";
	private static final String ITEM_SKU = "sku";
	private static final String ITEM_CASTO_SKU = "casto_sku";
	private static final String ITEM_PRODUCT = "product";
	private static final String ITEM_CASTO_PRODUCT = "casto_product";
	private static final String ITEM_SHIPPING_DISCOUNT_PERCENT_OFF = "Shipping Discount - Percent Off";
	private static final String ITEM_ORDER_DISCOUNT_PERCENT_OFF = "Order Discount - Percent Off";
	private static final String ITEM_ORDER_DISCOUNT_AMOUNT_OFF = "Order Discount - Amount Off";
	private static final String ITEM_SHIPPING_DISCOUNT_FIXED_PRICE = "Shipping Discount - Fixed Price";
	private static final String ITEM_DISCOUNT_FIXED_PRICE = "Item Discount - Fixed Price";
	private static final String ITEM_SHIPPING_DISCOUNT_AMOUNT_OFF = "Shipping Discount - Amount Off";
	private static final String ITEM_DISCOUNT_AMOUNT_OFF = "Item Discount - Amount Off";
	private static final String ITEM_DISCOUNT_PERCENT_OFF = "Item Discount - Percent Off";

	private static final String PROPERTY_FIXED_RELATED_PRODUCTS = "fixedRelatedProducts";
	private static final String PROPERTY_PARENT_CATEGORY = "parentCategory";
	private static final String PROPERTY_CASTO_ANCESTOR_CATEGORIES = "castoAncestorCategories";
	private static final String PROPERTY_ANCESTOR_CATEGORIES = "ancestorCategories";
	private static final String PROPERTY_RETRAIT_MOMENTANE_DATE_DEBUT = "retraitMomentaneDateDebut";
	private static final String PROPERTY_RELATED_SKU = "relatedSku";
	private static final String PROPERTY_BUNDLE_LINKS = "bundleLinks";
	private static final String PROPERTY_CROSS_SELLING = "crossSelling";

	private static final String FILE_ALL_MAGASINS_XML = "all_magasins.xml";
	private static final String FILE_ALL_PROMOS_XML = "all_promos.xml";
	private static final String FILE_ALL_AUX_XML = "all_aux.xml";
	private static final String FILE_ALL_CROSS_SELLING_SKU_XML = "all_CrossSellingSku.xml";
	private static final String FILE_ALL_CASTO_SKUS_MAGASINS_XML = "all_casto_skus_magasins.xml";
	private static final String FILE_ALL_MEDIAS_XML = "all_medias.xml";
	private static final String FILE_ALL_CATEGORIES_XML = "all_categories.xml";
	private static final String FILE_ALL_ROOT_CATEGORIES_XML = "all_root_categories.xml";
	private static final String FILE_ALL_SKUS_W_BUNDLE_LINKS_XML = "all_skus_w_bundleLinks.xml";
	private static final String FILE_ALL_SKUS_W_CROSS_XML = "all_skus_w_cross.xml";
	private static final String FILE_ALL_SKU_LINKS_XML = "all_sku_links.xml";
	private static final String FILE_ALL_SKUS_XML = "all_skus.xml";	
	private static final String FILE_ALL_PRODUCTS_W_REL_XML = "all_products_w_rel.xml";
	private static final String FILE_ALL_PRODUCTS_XML = "all_products.xml";

	private static final String FOLDER_SKUS_MAGASINS = "skus_magasins";
	private static final String FOLDER_SKUS = "skus";
	private static final String FOLDER_PRODUCTS = "products";
	private static final String FOLDER_MEDIAS = "medias";

	private static final List<String> badSKUSData = new ArrayList<String>();

	static {
		badSKUSData.add("Casto290003");
		badSKUSData.add("Casto299153");
		badSKUSData.add("Casto300734");
		badSKUSData.add("Casto300723");
		badSKUSData.add("Casto290199");
		badSKUSData.add("Casto288827");
		badSKUSData.add("Casto474215");
		badSKUSData.add("Casto540637");
		badSKUSData.add("Casto483733");
		badSKUSData.add("Casto840984");
		badSKUSData.add("Casto495957");
	}

	public final static int ADD = 1;
	public final static int UPDATE = 3;
	private File mImportFile;
	private SAXParser mParser = null;
	private String mItemDescriptor = null;
	private String mItemId = null;
	private String mRepositoryName = null;
	private Stack<String> mTagNames = new Stack<String>();
	private StringBuffer mPropertyValue = null;
	private HashMap<String, String> mProperties = null;
	private String mPropertyName = null;
	private int mAction;
	private String[] mItemsFilter;
	private int mStartIndex = -1;
	private int mBatchSize = -1;
	private int mPhase;
	private Writer mOutputStreamWriter;
	private List<String> mPropsToRemove;
	private List<String> mPropsToExtract;
	private Map<String, String> mPropsToAppend;
	
	private File mExtractFile;
	private boolean mSkipUpdate = false;
	
	private static void printhelp() {
		System.out.println("[-h]");
		System.out.println("		help");
		System.out.println("[-r]");
		System.out.println("		Set this flag if the utility should only generate report for specified XML");
		System.out.println("-file <file>");
		System.out.println("		use to specify XML file to be loaded");
		System.out.println("-outDir <folder>");
		System.out.println("		use to specify folder where working files should be stored");
		System.out.println("-installDataPath <folder>");
		System.out.println("		use to specify folder where basic install data files are located");
		System.out.println("[-listPriceName <name>]");
		System.out.println("		use to specify name for price list");
		System.out.println("[-salePriceName <name>]");
		System.out.println("		use to specify name for sale price list");
		System.out.println("[-pivotGen]");
		System.out.println("		flag to switch on approximate generation of pivot categories");
		System.out.println("[-skipPricesGen]");
		System.out.println("		flag to switch off generatrion prices based on SKU info");
		System.out.println("[-skipInvGen]");
		System.out.println("		flag to switch off generatrion inventory based on SKU info");
		System.out.println("[-threads <number>]");
		System.out.println("		number of threads to be run in parallel. Default - 3.");

	}

	/**
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws Exception {		
		
		if (args != null && args.length == 1 && "-h".equals(args[0])) {
			printhelp();
			System.exit(0);
		}	
		
		
		if (args == null || args.length < 6) {
			System.out.println("Please, specify -file, -outDir and -installDataPath");
			printhelp();
			System.exit(0);
		}

		String fileName = null;
		String outDir = null;
		String installDataPath = null;
		boolean pivotGen = false;
		boolean reportOnly = false;
		boolean genPrices = true;
		boolean genInv = true;
		boolean genSKUMagas = false;
		final String[] listPriceName = new String[1];
		final String[] salePriceName = new String[1];
		int threadsNumber = 3;
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-file")) {
				if (i == args.length - 1) {
					System.out.println("Please, specify -file, -outDir and -installDataPath");
					printhelp();
					System.exit(0);
				}
				fileName = args[++i];
			} else if (args[i].equals("-outDir")) {
				if (i == args.length - 1) {
					System.out.println("Please, specify -file, -outDir and -installDataPath");
					printhelp();
					System.exit(0);
				}
				outDir = args[++i];
			} else if (args[i].equals("-listPriceName")) {
				if (i == args.length - 1) {
					System.out.println("Please, specify name for listPriceName");
					printhelp();
					System.exit(0);
				}
				listPriceName[0] = args[++i];
			} else if (args[i].equals("-salePriceName")) {
				if (i == args.length - 1) {
					System.out.println("Please, specify name for salePriceName");
					printhelp();
					System.exit(0);
				}
				salePriceName[0] = args[++i];
			} else if (args[i].equals("-installDataPath")) {
				if (i == args.length - 1) {
					System.out.println("Please, specify name for installDataPath");
					printhelp();
					System.exit(0);
				}
				installDataPath = args[++i];
			} else if (args[i].equals("-pivotGen")) {
				pivotGen = true;
			} else if (args[i].equals("-threads")) {
				if (i == args.length - 1) {
					System.out.println("Please, specify number of threads");
					printhelp();
					System.exit(0);
				}
				try {
					threadsNumber = Integer.parseInt(args[++i]);
				} catch (Exception e) {
					System.out.println("Please, specify valid number");
					System.exit(0);
				}
			} else if (args[i].equals("-r")) {
				reportOnly = true;
			} else if (args[i].equals("-skipPricesGen")) {
				genPrices = false;
			} else if (args[i].equals("-skipInvGen")) {
				genInv = false;
			}
		}
		if (listPriceName[0] == null) {
			listPriceName[0] = "listPrices";
		}
		if (salePriceName[0] == null) {
			salePriceName[0] = "salePrices";
		}		
		String format = "| %1$-21s = %2$-46s|\n";

		System.out.println(" -----------------------------Configuration-----------------------------");
		System.out.format(format, "Source XML", fileName);
		System.out.format(format, "Output Directory", outDir);
		System.out.format(format, "Install Data Path", installDataPath);
		System.out.format(format, "Price List Name", listPriceName[0]);
		System.out.format(format, "Sale Price List Name", salePriceName[0]);
		System.out.format(format, "Number of Threads", threadsNumber);	
		System.out.format(format, "Generate Prices", genPrices);	
		System.out.format(format, "Generate Inventory", genInv);	
		System.out.format(format, "Report Only", reportOnly);
		System.out.println(" -----------------------------------------------------------------------");
		System.out.println();
		
		Thread memoryThread = new Thread() {
			{
				setDaemon(true);
			}
			private static final int MB = 1024 * 1024;

			public void run() {
				while (!isInterrupted() && Thread.currentThread() == this) {
					System.out.println("--------------------------------");
					System.out.println("| TOTAL JVM MEMORY: " + Runtime.getRuntime().totalMemory() / MB + "M");
					System.out.println("| FREE JVM MEMORY: " + Runtime.getRuntime().freeMemory() / MB + "M");
					System.out.println("--------------------------------");
					try {
						Thread.sleep(30000);
					} catch (Exception e) {
					}
				}

			}
		};
		memoryThread.start();

		final File xmlFile = new File(fileName);
		final File outputDir = new File(outDir);
		outputDir.mkdirs();
		long start = System.currentTimeMillis();

		System.out.println("Analyzing...");
		ImportFileParser par = new ImportFileParser(ImportFileParser.PHASE_ANALIZE, xmlFile);
		par.setEncoding("ISO8859-1");
		ImportItem[] importItems = par.parseFile();
		final Map<String, Integer> itemsTypeMap = new HashMap<String, Integer>();
		for (int i = 0; i < importItems.length; i++) {
			Integer count = itemsTypeMap.get(importItems[i].getItemDescriptor());
			if (count == null) {
				count = 0;
			}
			itemsTypeMap.put(importItems[i].getItemDescriptor(), ++count);
		}
		String formatAn = "| %1$-35s | %2$-10s|\n";
		Set<String> keys = itemsTypeMap.keySet();
		System.out.println("The following items to extract...");
		System.out.println();
		System.out.println("--------------------------------------------------");
		System.out.format(formatAn, "Name", "Number");
		System.out.println("--------------------------------------------------");
		int total = 0;
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String key = iterator.next();
			int number = itemsTypeMap.get(key);
			System.out.format(formatAn, key, number);
			total += number;
		}
		System.out.println("--------------------------------------------------");
		System.out.format(formatAn, "Total", total);
		System.out.println("--------------------------------------------------");
		
		if (reportOnly) {
			return;
		}
		
		ExecutorService threadPool = Executors.newFixedThreadPool(threadsNumber);
		final Map<Integer, LoadData> pubFilesToLoad = new TreeMap<Integer, LoadData>();
		final Map<Integer, LoadData> prodFilesToLoad = new TreeMap<Integer, LoadData>();

		threadPool.execute(new Runnable() {
			public void run() {
				ExtractionResult res = extractMedias(xmlFile, outputDir, itemsTypeMap);
				System.gc();
				if (res.file != null && res.file != null) {
					try {
						List<File> files = XMLBreaker.doBreakDownMedia(res.file, res.extractedItemsCount, new File(
								outputDir, FOLDER_MEDIAS));
						pubFilesToLoad.put(1, new LoadData(res.repository, (File[]) files
								.toArray(new File[files.size()])));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		threadPool.execute(new Runnable() {
			public void run() {
				ExtractionResult res = extractProductsWithRelated(xmlFile, outputDir, itemsTypeMap);
				pubFilesToLoad.put(80, new LoadData(res.repository, new File[] { res.file }));
				System.gc();
			}
		});

		/*
		 * threadPool.execute(new Runnable() { public void run() { extractProductsWithParentCategories(xmlFile,
		 * outputDir); System.gc(); } });
		 */

		threadPool.execute(new Runnable() {
			public void run() {
				ExtractionResult res = extractMagasins(xmlFile, outputDir, itemsTypeMap);
				pubFilesToLoad.put(-10, new LoadData(res.repository, new File[] { res.file }));
				System.gc();
			}
		});

		threadPool.execute(new Runnable() {
			public void run() {
				ExtractionResult res = extractMessageRegionRepository(xmlFile, outputDir, itemsTypeMap);
				pubFilesToLoad.put(-20, new LoadData(res.repository, new File[] { res.file }));
				System.gc();
			}
		});

		threadPool.execute(new Runnable() {
			public void run() {
				ExtractionResult res = extractProducts(xmlFile, outputDir, itemsTypeMap);
				System.gc();
				if (res != null && res.file != null) {
					try {
						List<File> files = XMLBreaker.doBreakDownProducts(res.file, res.extractedItemsCount, new File(
								outputDir, FOLDER_PRODUCTS));
						pubFilesToLoad.put(70, new LoadData(res.repository, (File[]) files.toArray(new File[files
								.size()])));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		threadPool.execute(new Runnable() {
			public void run() {
				ExtractionResult res = extractSKULinks(xmlFile, outputDir, itemsTypeMap);
				pubFilesToLoad.put(50, new LoadData(res.repository, new File[] { res.file }));
				System.gc();
			}
		});

		threadPool.execute(new Runnable() {
			public void run() {
				ExtractionResult res = extractSKUWithCrossSell(xmlFile, outputDir, itemsTypeMap);
				/*if (res.file != null && res.file != null) {
					try {
						List<File> files = XMLBreaker.doBreakDownCrossSells(res.file, res.extractedItemsCount, new File(
								outputDir, "crossSells"));
						pubFilesToLoad.put(100, new LoadData(res.repository, (File[]) files
								.toArray(new File[files.size()])));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}*/
				pubFilesToLoad.put(100, new LoadData(res.repository, new File[] { res.file }));
				System.gc();
			}
		});

		threadPool.execute(new Runnable() {
			public void run() {
				ExtractionResult res = extractSKUWithBundledLinks(xmlFile, outputDir, itemsTypeMap);
				if (res.file != null && res.file != null) {
					try {
						List<File> files = XMLBreaker.doBreakDownBundledLinks(res.file, res.extractedItemsCount, new File(
								outputDir, "bundledLinks"));
						pubFilesToLoad.put(60, new LoadData(res.repository, (File[]) files
								.toArray(new File[files.size()])));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				//pubFilesToLoad.put(60, new LoadData(res.repository, new File[] { res.file }));
				System.gc();
			}
		});

		threadPool.execute(new Runnable() {
			public void run() {
				ExtractionResult res = extractSKUs(xmlFile, outputDir, itemsTypeMap);
				System.gc();
				if (res != null && res.file != null) {
					try {
						List<File> files = XMLBreaker.doBreakDownSKUs(res.file, res.extractedItemsCount, new File(
								outputDir, FOLDER_SKUS));
						pubFilesToLoad.put(40, new LoadData(res.repository, (File[]) files.toArray(new File[files
								.size()])));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		threadPool.execute(new Runnable() {
			public void run() {
				ExtractionResult res = extractCategories(xmlFile, outputDir, itemsTypeMap);
				pubFilesToLoad.put(110, new LoadData(res.repository, new File[] { res.file }));
				System.gc();
			}
		});
		
		if (genSKUMagas) {
			threadPool.execute(new Runnable() {
				public void run() {
					ExtractionResult res = extractCastoSKUMagasins(xmlFile, outputDir, itemsTypeMap);
					System.gc();
					if (res != null && res.file != null) {
						try {
							List<File> files = XMLBreaker.doBreakDownSKUMagasins(res.file, res.extractedItemsCount,
									new File(outputDir, FOLDER_SKUS_MAGASINS));
							pubFilesToLoad.put(30, new LoadData(res.repository, (File[]) files.toArray(new File[files
									.size()])));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
		}
		

		threadPool.execute(new Runnable() {
			public void run() {
				ExtractionResult res = extractCrossSellingSKUs(xmlFile, outputDir, itemsTypeMap);
				pubFilesToLoad.put(90, new LoadData(res.repository, new File[] { res.file }));
				System.gc();
			}
		});

		threadPool.execute(new Runnable() {
			public void run() {
				ExtractionResult res = extractAux(xmlFile, outputDir, itemsTypeMap);
				pubFilesToLoad.put(20, new LoadData(res.repository, new File[] { res.file }));
				System.gc();
			}
		});

		threadPool.execute(new Runnable() {
			public void run() {
				ExtractionResult res = extractPromos(xmlFile, outputDir, itemsTypeMap);
				pubFilesToLoad.put(120, new LoadData(res.repository, new File[] { res.file }));
				System.gc();
			}
		});
		/*
		threadPool.execute(new Runnable() {
			public void run() {
				try {
					List files = generateUpdatesForProducts(new File(outputDir, FOLDER_PRODUCTS), new File(outputDir,
							FOLDER_PRODUCTS));
					pubFilesToLoad.put(150, new LoadData("/atg/commerce/catalog/ProductCatalog", (File[]) files
							.toArray(new File[files.size()])));
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.gc();
			}
		});
		
		threadPool.execute(new Runnable() {
			public void run() {
				try {
					File file = generateUpdatesForCategories(new File(outputDir, FILE_ALL_CATEGORIES_XML), outputDir);
					pubFilesToLoad.put(140, new LoadData("/atg/commerce/catalog/ProductCatalog", new File[] { file }));
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.gc();
			}
		});
		*/
		
		threadPool.execute(new Runnable() {
			public void run() {
				try {
					File file = generateCatalogStructure(xmlFile, outputDir, itemsTypeMap);
					pubFilesToLoad.put(160, new LoadData("/atg/commerce/catalog/ProductCatalog", new File[] { file }));
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.gc();
			}
		});
				
		
		if (pivotGen) {
			threadPool.execute(new Runnable() {
				public void run() {
					try {
						File file = generateApproxPivot(xmlFile, outputDir);
						pubFilesToLoad.put(170, new LoadData("/atg/commerce/catalog/ProductCatalog", new File[] { file }));
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.gc();
				}
			});
		}
		
		if (genInv) {
			threadPool.execute(new Runnable() {
				public void run() {
					try {
						File file = generateInventoryForProducts(new File(outputDir, FOLDER_SKUS));
						prodFilesToLoad.put(210, new LoadData("/atg/commerce/inventory/InventoryRepository",
								new File[] { file }));
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.gc();
				}
			});
		}		
		
		if (genPrices) {
			threadPool.execute(new Runnable() {
				public void run() {
					try {
						File[] files = generatePricesForProducts(new File(outputDir, FOLDER_SKUS), listPriceName[0],
								salePriceName[0]);
						pubFilesToLoad.put(220, new LoadData("/atg/commerce/pricing/priceLists/PriceLists",
								files));					
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.gc();
				}
			});
		}		

		threadPool.shutdown();

		try {
			threadPool.awaitTermination(3600, TimeUnit.SECONDS);
		} catch (Exception e) {
		}

		memoryThread.interrupt();

		Date d = new Date(System.currentTimeMillis() - start);
		SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
		System.out.println("TOTAL: " + sdf.format(d));

		File prodInitCat = new File(outputDir, "prod_init.xml");
		FileOutputStream fos = new FileOutputStream(prodInitCat);
		OutputStreamWriter osw = new OutputStreamWriter(fos, UTF_8);
		BufferedWriter mOutputStreamWriter = new BufferedWriter(osw, 1024 * 32);
		try {
			mOutputStreamWriter.write(XML_VERSION);
			mOutputStreamWriter.write(DOCTYPE_GSA_TEMPLATE);
			mOutputStreamWriter.write(GSA_TEMPLATE_START_TAG);
			mOutputStreamWriter.write("<import-items>\n");

			mOutputStreamWriter.write("<add-item item-descriptor=\"catalog\" id=\"masterCatalog\">\n");
			mOutputStreamWriter.write("	<set-property name=\"displayName\"><![CDATA[Master Catalog]]></set-property>\n");
			mOutputStreamWriter
					.write("	<set-property name=\"directAncestorCatalogsAndSelf\"><![CDATA[masterCatalog]]></set-property>\n");
			mOutputStreamWriter
					.write("	<set-property name=\"creationDate\"><![CDATA[7/27/2009 13:01:19]]></set-property>\n");
			mOutputStreamWriter
					.write("	<set-property name=\"lastModifiedDate\"><![CDATA[7/27/2009 13:20:46]]></set-property>\n");
			mOutputStreamWriter.write("</add-item>\n");

			mOutputStreamWriter.write("<add-item item-descriptor=\"catalogFolder\" id=\"CatalogFolder\">\n");
			mOutputStreamWriter.write("	<set-property name=\"childItems\"><![CDATA[masterCatalog]]></set-property>\n");
			mOutputStreamWriter.write("	<set-property name=\"type\"><![CDATA[catalogFolder]]></set-property>\n");
			mOutputStreamWriter.write("	<set-property name=\"name\"><![CDATA[Catalogs]]></set-property>\n");
			mOutputStreamWriter.write("</add-item>\n");
			mOutputStreamWriter.write("</import-items>\n");
			mOutputStreamWriter.write(GSA_TEMPLATE_END_TAG);			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				mOutputStreamWriter.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		fos = new FileOutputStream(new File(outputDir, "import-config.xml"));
		osw = new OutputStreamWriter(fos, UTF_8);
		mOutputStreamWriter = new BufferedWriter(osw, 1024 * 32);
		try {
			mOutputStreamWriter.write(XML_VERSION);
			mOutputStreamWriter.write(DOCTYPE_IMPORTS);
			mOutputStreamWriter.write(IMPORTS_START_TAG);

			mOutputStreamWriter.write("<import repository=\"/atg/commerce/catalog/ProductCatalog\" filename=\""
					+ prodInitCat.getAbsolutePath() + "\"/>\n");

			Set<Integer> set = prodFilesToLoad.keySet();
			int outLength = outputDir.getAbsolutePath().length();
			for (Iterator<Integer> iterator = set.iterator(); iterator.hasNext();) {
				Integer order = iterator.next();
				LoadData loadData = prodFilesToLoad.get(order);
				for (int i = 0; i < loadData.files.length; i++) {
					File f = loadData.files[i];
					mOutputStreamWriter.write("<import repository=\"" + loadData.repository + "\" filename=\""
							+ f.getAbsolutePath() + "\"/>\n");
				}
			}
			File installDataFolder = new File(installDataPath);
			
			mOutputStreamWriter.write("<import repository=\"/atg/search/repository/RefinementRepository\" filename=\"" + new File(installDataFolder, "out-facets.xml").getAbsolutePath() + "\"/>\n");
			mOutputStreamWriter.write("<import repository=\"/atg/registry/Repository/CloudRepository\" filename=\"" + new File(installDataFolder, "cloud-data.xml").getAbsolutePath() + "\"/>\n");
			mOutputStreamWriter.write("<import repository=\"/atg/searchadmin/TextProcessingOptionsRepository\" filename=\"" + new File(installDataFolder, "out-text-processing-data.xml").getAbsolutePath() + "\"/>\n");  
			mOutputStreamWriter.write("<import repository=\"/atg/searchadmin/SearchAdminRepository\" filename=\"" + new File(installDataFolder, "out-search-project.xml").getAbsolutePath() + "\"/>\n");
			mOutputStreamWriter.write("<import repository=\"/atg/registry/Repository/CodePostalRepository\" filename=\"" + new File(installDataFolder, "CodePostalRepository.xml").getAbsolutePath() + "\"/>\n");
			mOutputStreamWriter.write("<import repository=\"/atg/registry/Repository/MotifRepository\" filename=\"" + new File(installDataFolder, "MotifRepository.xml").getAbsolutePath() + "\"/>\n");
			mOutputStreamWriter.write("<import repository=\"/atg/searchadmin/RefinementConfigRepository\" filename=\"" + new File(installDataFolder, "cast_doc_facets.xml").getAbsolutePath() + "\"/> \n");
			mOutputStreamWriter.write("<import repository=\"atg/userprofiling/ProfileAdapterRepository\" filename=\"" + new File(installDataFolder, "groups-privileges.xml").getAbsolutePath() + "\"/>\n");
			  
			mOutputStreamWriter.write(IMPORTS_END_TAG);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				mOutputStreamWriter.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		fos = new FileOutputStream(new File(outputDir, "import-config-pub.xml"));
		osw = new OutputStreamWriter(fos, UTF_8);
		mOutputStreamWriter = new BufferedWriter(osw, 1024 * 32);
		try {
			mOutputStreamWriter.write(XML_VERSION);
			mOutputStreamWriter.write(DOCTYPE_IMPORTS);
			mOutputStreamWriter.write(IMPORTS_START_TAG);
			Set<Integer> set = pubFilesToLoad.keySet();
			
			int counter = 0;
			File installDataFolder = new File(installDataPath);
			mOutputStreamWriter.write("<import repository=\"/atg/commerce/catalog/ProductCatalog\" filename=\""
					+ new File(installDataFolder, "cast-catalog-media-folders.xml").getAbsolutePath()
					+ "\" workspace=\"initial_" + counter++
					+ "\" comment=\"no_comments:generated by extraction tool\"/>\n");
			mOutputStreamWriter.write("<import repository=\"/atg/commerce/catalog/ProductCatalog\" filename=\""
					+ new File(installDataFolder, "cast-catalog-media-templates.xml").getAbsolutePath()
					+ "\" workspace=\"initial_" + counter++
					+ "\" comment=\"no_comments:generated by extraction tool\"/>\n");
			mOutputStreamWriter.write("<import repository=\"/atg/commerce/catalog/ProductCatalog\" filename=\""
					+ new File(installDataFolder, "cast-catalog-category-styles.xml").getAbsolutePath()
					+ "\" workspace=\"initial_" + counter++
					+ "\" comment=\"no_comments:generated by extraction tool\"/>\n");
			
			for (Iterator<Integer> iterator = set.iterator(); iterator.hasNext();) {
				Integer order = iterator.next();
				LoadData loadData = pubFilesToLoad.get(order);
				for (int i = 0; i < loadData.files.length; i++) {
					File f = loadData.files[i];
					mOutputStreamWriter.write("<import repository=\"" + loadData.repository + "\" filename=\""
							+ f.getAbsolutePath() + "\" workspace=\"initial_gen_" + counter++
							+ "\" comment=\"no_comments:generated by extraction tool\"/>\n");
				}
			}			
			
			mOutputStreamWriter
					.write("<import repository=\"/atg/registry/Repository/CastostageGSARepository\" filename=\""
							+ new File(installDataFolder, "castostageGSARepository.xml").getAbsolutePath()
							+ "\" workspace=\"initial_" + counter++
							+ "\" comment=\"no_comments:generated by extraction tool\"/>\n");
			
			mOutputStreamWriter
					.write("<import repository=\"/atg/registry/Repository/CatalogueGSARepository\" filename=\""
							+ new File(installDataFolder, "cast-catalogues-initial.xml").getAbsolutePath()
							+ "\" workspace=\"initial_" + counter++
							+ "\" comment=\"no_comments:generated by extraction tool\"/>\n");
			
			mOutputStreamWriter.write("<import repository=\"/atg/search/repository/RefinementRepository\" filename=\""
					+ new File(installDataFolder, "out-facets.xml").getAbsolutePath() + "\" workspace=\"initial_"
					+ counter++ + "\" comment=\"no_comments:generated by extraction tool\"/>\n");
			mOutputStreamWriter.write("<import repository=\"/atg/bizui/viewmapping/ViewMappingRepository\" filename=\""
					+ new File(installDataFolder, "viewmapping.xml").getAbsolutePath() + "\" workspace=\"initial_"
					+ counter++ + "\" comment=\"no_comments:generated by extraction tool\"/>\n");
			mOutputStreamWriter.write("<import repository=\"/atg/searchadmin/SearchAdminRepository\" filename=\""
					+ new File(installDataFolder, "searchadmin-data.xml").getAbsolutePath() + "\" workspace=\"initial_"
					+ counter++ + "\" comment=\"no_comments:generated by extraction tool\"/>\n");
			mOutputStreamWriter.write("<import repository=\"/atg/userprofiling/InternalProfileRepository\" filename=\""
					+ new File(installDataFolder, "searchadmin-profile.xml").getAbsolutePath()
					+ "\" workspace=\"initial_" + counter++
					+ "\" comment=\"no_comments:generated by extraction tool\"/>\n");
			mOutputStreamWriter.write("<import repository=\"/atg/searchadmin/TopicRepository\" filename=\""
					+ new File(installDataFolder, "topics-data.xml").getAbsolutePath() + "\" workspace=\"initial_"
					+ counter++ + "\" comment=\"no_comments:generated by extraction tool\"/>\n");
			mOutputStreamWriter.write("<import repository=\"/atg/searchadmin/RefinementConfigRepository\" filename=\""
					+ new File(installDataFolder, "cast_doc_facets.xml").getAbsolutePath() + "\" workspace=\"initial_"
					+ counter++ + "\" comment=\"no_comments:generated by extraction tool\"/>\n");
			mOutputStreamWriter.write("<import repository=\"/atg/bizui/viewmapping/ViewMappingRepository\" filename=\""
					+ new File(installDataFolder, "cast-multiedit.xml").getAbsolutePath() + "\" workspace=\"initial_"
					+ counter++ + "\" comment=\"no_comments:generated by extraction tool\"/>\n");
			mOutputStreamWriter.write(IMPORTS_END_TAG);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				mOutputStreamWriter.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	private static File generateInventoryForProducts(File pSkuFolder) throws IOException {
		System.out.println("Starting generating inventory for products...Thread=" + Thread.currentThread().getName());
		File[] files = pSkuFolder.listFiles(new FileFilter() {
			public boolean accept(File pPathname) {
				return pPathname.getName().startsWith("sku_");
			}
		});
		File result = new File(pSkuFolder, "inventory.xml");
		FileOutputStream fos = new FileOutputStream(result);
		OutputStreamWriter osw = new OutputStreamWriter(fos, UTF_8);
		BufferedWriter mOutputStreamWriter = new BufferedWriter(osw, 1024 * 32);

		try {
			mOutputStreamWriter.write(XML_VERSION);
			mOutputStreamWriter.write(DOCTYPE_GSA_TEMPLATE);
			mOutputStreamWriter.write(GSA_TEMPLATE_START_TAG);
			mOutputStreamWriter.write("<import-items>\n");
			
			for (int i = 0; i < files.length; i++) {
				ImportFileParser par = new ImportFileParser(ImportFileParser.PHASE_ADD_UPDATE, files[i]);
				ImportItem[] importItems = par.parseFile();
				for (int j = 0; j < importItems.length; j++) {
					String level = importItems[j].getProperties().get("CacheSKUCompteur");
					if (level == null) {
						System.out.println("WARNIGN: " + importItems[j].getItemId() + " doesn't have CacheSKUCompteur specified. Set inventory to 0");
						level = "-1";
					}
					//CacheSKUCompteur has value like 0.0
					Double doubleLevel = null;
					try {
						doubleLevel = Double.parseDouble(level);
					} catch (Exception e) {
						System.out.println("WARNIGN: " + importItems[j].getItemId() + " has bad CacheSKUCompteur specified " + level + ". Set inventory to 0");
						doubleLevel = 0d;
					}					
					
					String name = importItems[j].getProperties().get("displayName");
					mOutputStreamWriter.write("<add-item item-descriptor=\"inventory\" id=\"inv"
							+ StringEscapeUtils.escapeXml(importItems[j].getItemId()) + "\">\n");
					mOutputStreamWriter.write("	<set-property name=\"catalogRefId\"><![CDATA["
							+ StringEscapeUtils.escapeXml(importItems[j].getItemId()) + "]]></set-property>\n");
					mOutputStreamWriter.write("	<set-property name=\"stockLevel\"><![CDATA[" + doubleLevel.intValue()
							+ "]]></set-property>\n");
					mOutputStreamWriter.write("	<set-property name=\"displayName\"><![CDATA[" + name
							+ "]]></set-property>\n");
					mOutputStreamWriter
							.write("	<set-property name=\"creationDate\"><![CDATA[7/29/2009 11:00:00]]></set-property>\n");
					mOutputStreamWriter.write("</add-item>\n");
				}

			}
			mOutputStreamWriter.write("</import-items>\n");
			mOutputStreamWriter.write(GSA_TEMPLATE_END_TAG);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				mOutputStreamWriter.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		System.out.println("Generating prices for inventory finished...Thread=" + Thread.currentThread().getName());
		return result;
	}

	private static File[] generatePricesForProducts(File pSkuFolder, String pListPriceName, String pSaleListPriceName)
			throws IOException {
		System.out.println("Starting generating prices for products...Thread=" + Thread.currentThread().getName());
		File[] files = pSkuFolder.listFiles(new FileFilter() {
			public boolean accept(File pPathname) {
				return pPathname.getName().startsWith("sku_");
			}
		});
		int itemsPerFile = 10000;
		int filesCounter = 0;
		List<File> resFiles = new ArrayList<File>();
		
		File result = new File(pSkuFolder, "prices_0.xml");
		resFiles.add(result);
		FileOutputStream fos = new FileOutputStream(result);
		OutputStreamWriter osw = new OutputStreamWriter(fos, UTF_8);
		BufferedWriter mOutputStreamWriter = new BufferedWriter(osw, 1024 * 32);
		
		try {
			mOutputStreamWriter.write(XML_VERSION);
			mOutputStreamWriter.write(DOCTYPE_GSA_TEMPLATE);
			mOutputStreamWriter.write(GSA_TEMPLATE_START_TAG);
			mOutputStreamWriter.write("<import-items>\n");
			
			mOutputStreamWriter.write("	<add-item item-descriptor=\"priceListFolder\" id=\"PriceListFolder\">\n");
			mOutputStreamWriter.write("	<set-property name=\"name\"><![CDATA[Price Lists]]></set-property>\n");
			mOutputStreamWriter.write("	<set-property name=\"childItems\"><![CDATA[" + pListPriceName + ","
					+ pSaleListPriceName + "]]></set-property>\n");
			mOutputStreamWriter.write("</add-item>\n");

			mOutputStreamWriter.write("<add-item item-descriptor=\"priceList\" id=\"" + pListPriceName + "\">\n");
			mOutputStreamWriter.write(" <set-property name=\"displayName\"><![CDATA[List Prices]]></set-property>\n");
			mOutputStreamWriter.write(" <set-property name=\"locale\"><![CDATA[fr_FR_EURO]]></set-property>\n");
			mOutputStreamWriter.write("</add-item>\n");
			mOutputStreamWriter.write("<add-item item-descriptor=\"priceList\" id=\"" + pSaleListPriceName + "\">\n");
			mOutputStreamWriter.write("	<set-property name=\"displayName\"><![CDATA[Sale Prices]]></set-property>\n");
			mOutputStreamWriter.write("	<set-property name=\"locale\"><![CDATA[fr_FR_EURO]]></set-property>\n");
			mOutputStreamWriter.write("</add-item>\n");
			int counter = 0;
			for (int i = 0; i < files.length; i++) {
				ImportFileParser par = new ImportFileParser(ImportFileParser.PHASE_ADD_UPDATE, files[i]);
				ImportItem[] importItems = par.parseFile();

				for (int j = 0; j < importItems.length; j++) {
					String price = importItems[j].getProperties().get("prixVenteTTCEuro");
					if (price == null) {
						System.out.println("WARNING: " + importItems[j].getItemId() + " doesn't have prixVenteTTCEuro specified. Set price to 0");
						price = "0";
					}
					mOutputStreamWriter.write("<add-item item-descriptor=\"price\" id=\"lpx"
							+ StringEscapeUtils.escapeXml(importItems[j].getItemId()) + "\">\n");
					mOutputStreamWriter.write("	<set-property name=\"skuId\"><![CDATA["
							+ StringEscapeUtils.escapeXml(importItems[j].getItemId()) + "]]></set-property>\n");
					mOutputStreamWriter.write("	<set-property name=\"listPrice\"><![CDATA[" + price
							+ "]]></set-property>\n");
					mOutputStreamWriter.write("	<set-property name=\"priceList\"><![CDATA[" + pListPriceName
							+ "]]></set-property>\n");
					mOutputStreamWriter.write("</add-item>\n");
					
					counter++;
					if (counter == itemsPerFile) {
						counter = 0;
						filesCounter++;
						mOutputStreamWriter.write("</import-items>\n");
						mOutputStreamWriter.write(GSA_TEMPLATE_END_TAG);
						mOutputStreamWriter.close();
						result = new File(pSkuFolder, "prices_" + filesCounter + ".xml");
						resFiles.add(result);
						fos = new FileOutputStream(result);
						osw = new OutputStreamWriter(fos, UTF_8);
						mOutputStreamWriter = new BufferedWriter(osw, 1024 * 32);
						mOutputStreamWriter.write(XML_VERSION);
						mOutputStreamWriter.write(DOCTYPE_GSA_TEMPLATE);
						mOutputStreamWriter.write(GSA_TEMPLATE_START_TAG);
						mOutputStreamWriter.write("<import-items>\n");
					}
				}
			}
			mOutputStreamWriter.write("</import-items>\n");
			mOutputStreamWriter.write(GSA_TEMPLATE_END_TAG);
			mOutputStreamWriter.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		System.out.println("Generating prices for products finished...Thread=" + Thread.currentThread().getName());
		
		return (File[]) resFiles.toArray(new File[0]);
	}

	/*private static List generateUpdatesForProducts(File pProductsFolder, File pDestinationFolder) throws IOException {
		System.out.println("Starting generating updates for products...Thread=" + Thread.currentThread().getName());
		File[] files = pProductsFolder.listFiles();
		File[] result = new File[files.length];
		for (int i = 0; i < files.length; i++) {
			ImportFileParser par = new ImportFileParser(ImportFileParser.PHASE_ADD_UPDATE, files[i]);
			ImportItem[] importItems = par.parseFile();
			File sourceFile = new File(pProductsFolder, "update_" + files[i].getName());
			result[i] = sourceFile;

			FileOutputStream fos = new FileOutputStream(sourceFile);
			OutputStreamWriter osw = new OutputStreamWriter(fos, UTF_8);
			BufferedWriter mOutputStreamWriter = new BufferedWriter(osw, 1024 * 32);
			try {
				mOutputStreamWriter.write(XML_VERSION);
				mOutputStreamWriter.write(DOCTYPE_GSA_TEMPLATE);
				mOutputStreamWriter.write(GSA_TEMPLATE_START_TAG);

				for (int j = 0; j < importItems.length; j++) {
					mOutputStreamWriter.write("        <update-item item-descriptor=\""
							+ importItems[j].getItemDescriptor() + "\" id=\""
							+ StringEscapeUtils.escapeXml(importItems[j].getItemId()) + "\">\n");
					mOutputStreamWriter
							.write("            <set-property name=\"template\"><![CDATA[mBasicProdTemplate]]></set-property>\n");
					mOutputStreamWriter.write("        </update-item>\n");
				}

				mOutputStreamWriter.write(GSA_TEMPLATE_END_TAG);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					mOutputStreamWriter.close();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		System.out.println("Generating updates for products finished...Thread=" + Thread.currentThread().getName());
		return Arrays.asList(result);
	}

	private static File generateUpdatesForCategories(File pCategoriesFile, File pDestinationFolder) throws IOException {
		System.out.println("Starting generating updates for categories...Thread=" + Thread.currentThread().getName());
		ImportFileParser par = new ImportFileParser(ImportFileParser.PHASE_ADD_UPDATE, pCategoriesFile);
		ImportItem[] importItems = par.parseFile();
		File sourceFile = new File(pDestinationFolder, "update_" + pCategoriesFile.getName());

		FileOutputStream fos = new FileOutputStream(sourceFile);
		OutputStreamWriter osw = new OutputStreamWriter(fos, UTF_8);
		BufferedWriter mOutputStreamWriter = new BufferedWriter(osw, 1024 * 32);
		try {
			mOutputStreamWriter.write(XML_VERSION);
			mOutputStreamWriter.write(DOCTYPE_GSA_TEMPLATE);
			mOutputStreamWriter.write(GSA_TEMPLATE_START_TAG);

			for (int j = 0; j < importItems.length; j++) {
				mOutputStreamWriter.write("        <add-item item-descriptor=\"" + importItems[j].getItemDescriptor()
						+ "\" id=\"" + StringEscapeUtils.escapeXml(importItems[j].getItemId()) + "\">\n");
				mOutputStreamWriter
						.write("            <set-property name=\"template\"><![CDATA[mBasicCategoryTemplate]]></set-property>\n");
				mOutputStreamWriter.write("        </add-item>\n");
			}
			mOutputStreamWriter.write(GSA_TEMPLATE_END_TAG);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				mOutputStreamWriter.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		System.out.println("Generating updates for categories finished...Thread=" + Thread.currentThread().getName());
		return sourceFile;
	}
	*/
	private static int calculateItemsCount(Map<String, Integer> pItemsTypeMap, String[] items) {
		int count = 0;
		for (int i = 0; i < items.length; i++) {
			Integer number = pItemsTypeMap.get(items[i]);
			if (number != null) {
				count += number;
			}
		}
		return count;
	}

	private static ExtractionResult extractMessageRegionRepository(File pXMLFile, File pDestinationFolder,
			Map<String, Integer> pItemsTypeMap) {
		String taskName = "extracting MessageRegionRepository";
		System.out.println("starting " + taskName + "...Thread=" + Thread.currentThread().getName());
		long start = System.currentTimeMillis();
		File file = new File(pDestinationFolder, "MessageRegionRepository.xml");
		String[] items = new String[] { "messageRegionRepository" };
		ItemsExractor ext = new ItemsExractor(ADD, pXMLFile, file, items, 0, calculateItemsCount(pItemsTypeMap, items));
		List<String> pr = new ArrayList<String>();
		pr.add("regions");
		ext.setPropsToRemove(pr);
		try {
			ext.extract();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		int extractedItemsCount = ext.extractedItemsCount;
		System.out.println(taskName + " finished. " + extractedItemsCount + " item(s) extracted. Time="
				+ (System.currentTimeMillis() - start) + "mls");
		return new ExtractionResult(file, extractedItemsCount, "/atg/registry/Repository/MessageRegionRepository");
	}

	private static ExtractionResult extractMagasins(File pXMLFile, File pDestinationFolder,
			Map<String, Integer> pItemsTypeMap) {
		String taskName = "extracting magasins";
		System.out.println("starting " + taskName + "...Thread=" + Thread.currentThread().getName());
		long start = System.currentTimeMillis();
		File file = new File(pDestinationFolder, FILE_ALL_MAGASINS_XML);
		String[] items = new String[] { ITEM_MAGASIN, ITEM_REGION, ITEM_IMAGE_AUX, ITEM_MAGASIN_SERVICE_ASSOCIATION,
				ITEM_SERVICE, ITEM_SERVICE_L, ITEM_ENTITE, ITEM_ADRESSE, ITEM_DEPARTEMENT, ITEM_ACTUALITE, ITEM_OFFRE,
				ITEM_OUVERTURE_EXCEP, ITEM_OPE_SPECIALE };
		ItemsExractor ext = new ItemsExractor(ADD, pXMLFile, file, items, 0, calculateItemsCount(pItemsTypeMap, items));
		List<String> pr = new ArrayList<String>();
		pr.add("messages");
		ext.setPropsToRemove(pr);
		try {
			ext.extract();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		int extractedItemsCount = ext.extractedItemsCount;
		System.out.println(taskName + " finished. " + extractedItemsCount + " item(s) extracted. Time="
				+ (System.currentTimeMillis() - start) + "mls");
		return new ExtractionResult(file, extractedItemsCount, "/atg/registry/Repository/MagasinGSARepository");
	}

	private static ExtractionResult extractProducts(File pXMLFile, File pDestinationFolder,
			Map<String, Integer> pItemsTypeMap) {
		String taskName = "extracting casto_product and product";
		System.out.println("starting " + taskName + "...Thread=" + Thread.currentThread().getName());
		long start = System.currentTimeMillis();
		File file = new File(pDestinationFolder, FILE_ALL_PRODUCTS_XML);
		String[] items = new String[] { ITEM_CASTO_PRODUCT, ITEM_PRODUCT };
		ItemsExractor ext = new ItemsExractor(ADD, pXMLFile, file, items, 0, calculateItemsCount(pItemsTypeMap, items));
		List<String> pr = new ArrayList<String>();
		pr.add(PROPERTY_ANCESTOR_CATEGORIES);
		pr.add(PROPERTY_CASTO_ANCESTOR_CATEGORIES);
		pr.add(PROPERTY_PARENT_CATEGORY);
		pr.add(PROPERTY_FIXED_RELATED_PRODUCTS);
		ext.setPropsToRemove(pr);

		Map<String, String> map = new HashMap<String, String>();
		map.put("template", "mBasicProdTemplate");
		ext.setPropsToAppend(map);
		
		try {
			ext.extract();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		int extractedItemsCount = ext.extractedItemsCount;
		System.out.println(taskName + " finished. " + extractedItemsCount + " item(s) extracted. Time="
				+ (System.currentTimeMillis() - start) + "mls");
		return new ExtractionResult(file, extractedItemsCount, "/atg/commerce/catalog/ProductCatalog");
	}

	public static ExtractionResult extractProductsWithRelated(File pXMLFile, File pDestinationFolder,
			Map<String, Integer> pItemsTypeMap) {
		String taskName = "extracting products ('fixedRelatedProducts' property(s) only)";
		System.out.println("starting " + taskName + "...Thread=" + Thread.currentThread().getName());
		long start = System.currentTimeMillis();
		File file = new File(pDestinationFolder, FILE_ALL_PRODUCTS_W_REL_XML);
		String[] items = new String[] { ITEM_CASTO_PRODUCT, ITEM_PRODUCT };
		ItemsExractor ext = new ItemsExractor(UPDATE, pXMLFile, file, items, 0, calculateItemsCount(pItemsTypeMap,
				items));
		List<String> pr = new ArrayList<String>();
		pr.add(PROPERTY_FIXED_RELATED_PRODUCTS);
		ext.setPropsToExtract(pr);

		try {
			ext.extract();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		int extractedItemsCount = ext.extractedItemsCount;
		System.out.println(taskName + " finished. " + extractedItemsCount + " item(s) extracted. Time="
				+ (System.currentTimeMillis() - start) + "mls");
		return new ExtractionResult(file, extractedItemsCount, "/atg/commerce/catalog/ProductCatalog");
	}	

	public static ExtractionResult extractSKUs(File pXMLFile, File pDestinationFolder,
			Map<String, Integer> pItemsTypeMap) {
		String taskName = "extracting casto_sku and sku";
		System.out.println("starting " + taskName + "...Thread=" + Thread.currentThread().getName());
		long start = System.currentTimeMillis();
		File file = new File(pDestinationFolder, FILE_ALL_SKUS_XML);
		String[] items = new String[] { ITEM_CASTO_SKU, ITEM_SKU };
		ItemsExractor ext = new ItemsExractor(ADD, pXMLFile, file, items, 0, calculateItemsCount(pItemsTypeMap, items));
		List<String> pr = new ArrayList<String>();
		pr.add(PROPERTY_CROSS_SELLING);
		pr.add(PROPERTY_BUNDLE_LINKS);
		pr.add(PROPERTY_RELATED_SKU);
		pr.add("infos");
		ext.setPropsToRemove(pr);
		try {
			ext.extract();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		int extractedItemsCount = ext.extractedItemsCount;
		System.out.println(taskName + " finished. " + extractedItemsCount + " item(s) extracted. Time="
				+ (System.currentTimeMillis() - start) + "mls");
		return new ExtractionResult(file, extractedItemsCount, "/atg/commerce/catalog/ProductCatalog");
	}

	private static ExtractionResult extractSKULinks(File pXMLFile, File pDestinationFolder,
			Map<String, Integer> pItemsTypeMap) {
		String taskName = "extracting sku-link and configurableSku";
		System.out.println("starting " + taskName + "...Thread=" + Thread.currentThread().getName());
		long start = System.currentTimeMillis();
		File file = new File(pDestinationFolder, FILE_ALL_SKU_LINKS_XML);
		String[] items = new String[] { ITEM_SKU_LINK, ITEM_CONFIGURABLE_SKU };
		ItemsExractor ext = new ItemsExractor(ADD, pXMLFile, file, items, 0, calculateItemsCount(pItemsTypeMap, items));
		try {
			ext.extract();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		int extractedItemsCount = ext.extractedItemsCount;
		System.out.println(taskName + " finished. " + extractedItemsCount + " item(s) extracted. Time="
				+ (System.currentTimeMillis() - start) + "mls");
		return new ExtractionResult(file, extractedItemsCount, "/atg/commerce/catalog/ProductCatalog");
	}

	private static ExtractionResult extractSKUWithCrossSell(File pXMLFile, File pDestinationFolder,
			Map<String, Integer> pItemsTypeMap) {
		String taskName = "extracting sku-link and configurableSku ('crossSelling' property(s) only)";
		System.out.println("starting " + taskName + "...Thread=" + Thread.currentThread().getName());
		long start = System.currentTimeMillis();
		File file = new File(pDestinationFolder, FILE_ALL_SKUS_W_CROSS_XML);
		String[] items = new String[] { ITEM_CASTO_SKU, ITEM_SKU };
		ItemsExractor ext = new ItemsExractor(UPDATE, pXMLFile, file, items, 0, calculateItemsCount(pItemsTypeMap,
				items));
		List<String> pr = new ArrayList<String>();
		pr.add(PROPERTY_CROSS_SELLING);
		ext.setPropsToExtract(pr);
		ext.setSkipUpdate(true);
		
		try {
			ext.extract();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		int extractedItemsCount = ext.extractedItemsCount;
		System.out.println(taskName + " finished. " + extractedItemsCount + " item(s) extracted. Time="
				+ (System.currentTimeMillis() - start) + "mls");
		return new ExtractionResult(file, extractedItemsCount, "/atg/commerce/catalog/ProductCatalog");
	}

	// private static void extractSKUWithBadData1(File pXMLFile, File pDestinationFolder) {
	// System.out.println("Starting extracting SKUs ('bundleLinks' and 'relatedSku' property(s) only)...Thread="
	// + Thread.currentThread().getName());
	// long start = System.currentTimeMillis();
	// ItemsExractor ext = new ItemsExractor(UPDATE, pXMLFile, new File(pDestinationFolder, "bad1.xml"), new String[] {
	// ITEM_CASTO_SKU, ITEM_SKU });
	// List<String> pr = new ArrayList<String>();
	// pr.add(PROPERTY_SERVICES);
	// ext.setPropsToExtract(pr);
	// try {
	// ext.extract();
	// } catch (Exception e) {
	// e.printStackTrace();
	// return;
	// }
	// System.out.println("Extracting SKUs ('bundleLinks' and 'relatedSku' property(s) only) finished. Time="
	// + (System.currentTimeMillis() - start) + "mls");
	// }
	//
	// private static void extractSKUWithBadData2(File pXMLFile, File pDestinationFolder) {
	// System.out.println("Starting extracting SKUs ('bundleLinks' and 'relatedSku' property(s) only)...Thread="
	// + Thread.currentThread().getName());
	// long start = System.currentTimeMillis();
	// ItemsExractor ext = new ItemsExractor(UPDATE, pXMLFile, new File(pDestinationFolder, "bad2.xml"), new String[] {
	// ITEM_CASTO_SKU, ITEM_SKU });
	// List<String> pr = new ArrayList<String>();
	// pr.add(PROPERTY_RETRAIT_MOMENTANE_DATE_DEBUT);
	// ext.setPropsToExtract(pr);
	// try {
	// ext.extract();
	// } catch (Exception e) {
	// e.printStackTrace();
	// return;
	// }
	// System.out.println("Extracting SKUs ('bundleLinks' and 'relatedSku' property(s) only) finished. Time="
	// + (System.currentTimeMillis() - start) + "mls");
	// }

	private static ExtractionResult extractSKUWithBundledLinks(File pXMLFile, File pDestinationFolder,
			Map<String, Integer> pItemsTypeMap) {
		String taskName = "extracting SKUs ('bundleLinks' and 'relatedSku' property(s) only";
		System.out.println("starting " + taskName + "...Thread=" + Thread.currentThread().getName());
		long start = System.currentTimeMillis();
		File file = new File(pDestinationFolder, FILE_ALL_SKUS_W_BUNDLE_LINKS_XML);
		String[] items = new String[] { ITEM_CASTO_SKU, ITEM_SKU };
		ItemsExractor ext = new ItemsExractor(UPDATE, pXMLFile, file, items, 0, calculateItemsCount(pItemsTypeMap,
				items));
		List<String> pr = new ArrayList<String>();
		pr.add(PROPERTY_BUNDLE_LINKS);
		pr.add(PROPERTY_RELATED_SKU);
		ext.setPropsToExtract(pr);
		//ext.setSkipUpdate(true);
		try {
			ext.extract();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		ImportItem[] importItems = resolveCrossReferences(file);
		
		try {
			XMLBreaker.writeToFileUpdate(file, importItems);
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
		int extractedItemsCount = ext.extractedItemsCount;
		System.out.println(taskName + " finished. " + extractedItemsCount + " item(s) extracted. Time="
				+ (System.currentTimeMillis() - start) + "mls");
		return new ExtractionResult(file, extractedItemsCount, "/atg/commerce/catalog/ProductCatalog");
	}
	
	private static ImportItem[] resolveCrossReferences(File pFile) {
		System.out.println("Resolving Cross References");
		int countOfItemsWithBR = 0;
		ImportFileParser par = new ImportFileParser(ImportFileParser.PHASE_ADD_UPDATE, pFile, new String[] {
				"casto_sku", "sku" });
		ImportItem[] importItems = par.parseFile();
		Map<String, ImportItem> itemsMap = new HashMap<String, ImportItem>();
		for (int i = 0; i < importItems.length; i++) {
			itemsMap.put(importItems[i].getItemId(), importItems[i]);
		}
		for (int i = 0; i < importItems.length; i++) {
			ImportItem referrer = importItems[i];
			String relatedSku = referrer.getProperties().get(PROPERTY_RELATED_SKU);
			//System.out.println("relatedSku1 " + relatedSku);
			if (relatedSku == null || relatedSku.trim().length() == 0) {
				continue;
			}
			String[] relatedSkuIds = relatedSku.split(",");
			List relatedSkuIdsList = new ArrayList(Arrays.asList(relatedSkuIds));
			for (int j = 0; j < relatedSkuIds.length; j++) {
				ImportItem referee = itemsMap.get(relatedSkuIds[j]);
				if (referee != null) {
					String refereeRelatedSku = referee.getProperties().get(PROPERTY_RELATED_SKU);
					String[] refereeRelatedSkuIds = refereeRelatedSku.split(",");
					List refereeRelatedSkuIdsList = Arrays.asList(refereeRelatedSkuIds);
					if (refereeRelatedSkuIdsList.contains(referrer.getItemId())) {
						System.out.println(referrer.getItemId() + " has back reference from " + referee.getItemId() + ". Remove reference to " + referee.getItemId());
						relatedSkuIdsList.remove(referee.getItemId());
					}					
				}
			}
			if (relatedSkuIds.length != relatedSkuIdsList.size()) {
				countOfItemsWithBR++;
			}
			String updatedRelatedSku = "";
			for (int j = 0; j < relatedSkuIdsList.size(); j++) {
				String id = (String) relatedSkuIdsList.get(j);
				updatedRelatedSku += id;
				if (j != relatedSkuIdsList.size() - 1) {
					updatedRelatedSku += ",";
				}
			}
			//System.out.println("relatedSku2 " + updatedRelatedSku);
			referrer.getProperties().put(PROPERTY_RELATED_SKU, updatedRelatedSku);
		}
		//System.out.println(countOfItemsWithBR + " vs " + importItems.length);
		//System.out.println("Double Check");
		System.out.println("Cross References Resolved");
		return importItems;
		
	}
	private static boolean hasReference(ImportItem[] importItems, String pFrom, String pTo) {
		return false;
	}
	private static ExtractionResult extractCategories(File pXMLFile, File pDestinationFolder,
			Map<String, Integer> pItemsTypeMap) {
		String taskName = "extracting categories";
		System.out.println("starting " + taskName + "...Thread=" + Thread.currentThread().getName());
		long start = System.currentTimeMillis();
		File file = new File(pDestinationFolder, FILE_ALL_CATEGORIES_XML);
		String[] items = new String[] { ITEM_CASTO_CATEGORY, ITEM_CASTO_INSPI, ITEM_CATEGORY };
		ItemsExractor ext = new ItemsExractor(ADD, pXMLFile, file, items, 0, calculateItemsCount(pItemsTypeMap, items));
		List<String> pr = new ArrayList<String>();
		pr.add("root");
		ext.setPropsToRemove(pr);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("template", "mBasicCategoryTemplate");
		ext.setPropsToAppend(map);
		
		try {
			ext.extract();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		int extractedItemsCount = ext.extractedItemsCount;
		System.out.println(taskName + " finished. " + extractedItemsCount + " item(s) extracted. Time="
				+ (System.currentTimeMillis() - start) + "mls");
		return new ExtractionResult(file, extractedItemsCount, "/atg/commerce/catalog/ProductCatalog");
	}

	private static File generateCatalogStructure(File pXMLFile, File pDestinationFolder,
			Map<String, Integer> pItemsTypeMap) throws IOException {
		System.out.println("Starting generating catalog structure...Thread=" + Thread.currentThread().getName());
		long start = System.currentTimeMillis();
		File file = new File(pDestinationFolder, FILE_ALL_ROOT_CATEGORIES_XML);
		ItemsExractor ext = new ItemsExractor(ADD, pXMLFile, file, new String[] { ITEM_CASTO_CATEGORY,
				ITEM_CASTO_INSPI, ITEM_CATEGORY }, 0, calculateItemsCount(pItemsTypeMap, new String[] {
				ITEM_CASTO_CATEGORY, ITEM_CASTO_INSPI, ITEM_CATEGORY }));
		List<String> pr = new ArrayList<String>();
		pr.add("root");
		ext.setPropsToExtract(pr);
		try {
			ext.extract();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		ImportFileParser par = new ImportFileParser(ImportFileParser.PHASE_ADD_UPDATE, file);
		ImportItem items[] = par.parseFile();
		List<String> rootCategories = new ArrayList<String>();
		for (int i = 0; i < items.length; i++) {
			String value = items[i].getProperties().get("root");
			if ("true".equals(value)) {
				rootCategories.add(items[i].getItemId());
			}
		}
		file.delete();
		file = new File(pDestinationFolder, "update_catalog.xml");
		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter osw = new OutputStreamWriter(fos, UTF_8);
		BufferedWriter mOutputStreamWriter = new BufferedWriter(osw, 1024 * 32);
		try {
			mOutputStreamWriter.write(XML_VERSION);
			mOutputStreamWriter.write(DOCTYPE_GSA_TEMPLATE);
			mOutputStreamWriter.write(GSA_TEMPLATE_START_TAG);
			mOutputStreamWriter.write("<import-items>\n");

			mOutputStreamWriter.write("<add-item item-descriptor=\"catalog\" id=\"masterCatalog\">\n");
			mOutputStreamWriter.write("	<set-property name=\"rootCategories\"><![CDATA[cat10002]]></set-property>\n");
			mOutputStreamWriter.write("	<set-property name=\"topNavigationCategories\"><![CDATA[]]></set-property>\n");
			mOutputStreamWriter
					.write("	<set-property name=\"displayName\"><![CDATA[Master Catalog]]></set-property>\n");
			mOutputStreamWriter
					.write("	<set-property name=\"directAncestorCatalogsAndSelf\"><![CDATA[masterCatalog]]></set-property>\n");
			mOutputStreamWriter
					.write("	<set-property name=\"allRootCategories\"><![CDATA[cat10002]]></set-property>\n");
			mOutputStreamWriter
					.write("	<set-property name=\"creationDate\"><![CDATA[7/27/2009 13:01:19]]></set-property>\n");
			mOutputStreamWriter
					.write("	<set-property name=\"lastModifiedDate\"><![CDATA[7/27/2009 13:20:46]]></set-property>\n");
			mOutputStreamWriter.write("</add-item>\n");

			mOutputStreamWriter.write("<add-item item-descriptor=\"catalogFolder\" id=\"CatalogFolder\">\n");
			mOutputStreamWriter.write("	<set-property name=\"childItems\"><![CDATA[masterCatalog]]></set-property>\n");
			mOutputStreamWriter.write("	<set-property name=\"type\"><![CDATA[catalogFolder]]></set-property>\n");
			mOutputStreamWriter.write("	<set-property name=\"name\"><![CDATA[Catalogs]]></set-property>\n");
			mOutputStreamWriter.write("</add-item>\n");

			mOutputStreamWriter.write("<add-item item-descriptor=\"casto_category\" id=\"cat10002\">\n");
			mOutputStreamWriter.write("  <set-property name=\"type\"><![CDATA[casto_category]]></set-property>\n");
			mOutputStreamWriter.write("  <set-property name=\"Boutique_Event\"><![CDATA[0]]></set-property>\n");
			mOutputStreamWriter
					.write("  <set-property name=\"displayName\"><![CDATA[Magasin Castorama]]></set-property>\n");
			mOutputStreamWriter.write("  <set-property name=\"couleursMenu\"><![CDATA[test]]></set-property>\n");
			mOutputStreamWriter.write("  <set-property name=\"styleCategorie\"><![CDATA[test]]></set-property>\n");
			mOutputStreamWriter
					.write("  <set-property name=\"template\"><![CDATA[mBasicCategoryTemplate]]></set-property>\n");
			mOutputStreamWriter.write("  <set-property name=\"fixedChildCategories\"><![CDATA[");
			for (int i = 0; i < rootCategories.size(); i++) {
				if (i > 0) {
					mOutputStreamWriter.write(",");
				}
				mOutputStreamWriter.write(rootCategories.get(i));
			}
			mOutputStreamWriter.write("]]></set-property>\n");
			mOutputStreamWriter
					.write("  <set-property name=\"activationComparateur\"><![CDATA[oui]]></set-property>\n");
			mOutputStreamWriter
					.write("  <set-property name=\"computedCatalogs\"><![CDATA[masterCatalog]]></set-property>\n");
			mOutputStreamWriter
					.write("  <set-property name=\"parentCatalog\"><![CDATA[masterCatalog]]></set-property>\n");
			mOutputStreamWriter
					.write("  <set-property name=\"creationDate\"><![CDATA[4/6/2000 15:28:08]]></set-property>\n");
			mOutputStreamWriter
					.write("  <set-property name=\"computedCatalog\"><![CDATA[masterCatalog]]></set-property>\n");
			mOutputStreamWriter.write("</add-item>\n");

			mOutputStreamWriter.write("</import-items>\n");
			mOutputStreamWriter.write(GSA_TEMPLATE_END_TAG);
		} finally {
			try {
				mOutputStreamWriter.close();
			} catch (Exception e) {
			}
		}

		System.out.println("Generating catalog structure finished. Time=" + (System.currentTimeMillis() - start)
				+ "mls");
		return file;
	}

	static class Node {
		Node parent;
		ImportItem mItem;

		Node(ImportItem id) {
			mItem = id;
		}

		List<Node> childred = new ArrayList<Node>();

		void add(Node n) {
			childred.add(n);
			n.parent = Node.this;
		}
	}

	static class ExtractionResult {
		File file;
		int extractedItemsCount;
		String repository;

		ExtractionResult(File pFile, int pExtractedItemsCount, String pRepository) {
			file = pFile;
			extractedItemsCount = pExtractedItemsCount;
			repository = pRepository;
		}
	}

	static class LoadData {
		String repository;
		File[] files;

		LoadData(String pRepository, File[] pFiles) {
			repository = pRepository;
			files = pFiles;
		}
	}

	private static File generateApproxPivot(File pXMLFile, File pDestinationFolder) throws IOException {
		String taskName = "generate pivots";
		System.out.println("starting " + taskName + "...Thread=" + Thread.currentThread().getName());
		long start = System.currentTimeMillis();

		File file = new File(pDestinationFolder, "categories_with_roots.xml");
		ItemsExractor ext = new ItemsExractor(ADD, pXMLFile, file, new String[] { ITEM_CASTO_CATEGORY,
				ITEM_CASTO_INSPI, ITEM_CATEGORY });
		List<String> pr = new ArrayList<String>();
		pr.add("root");
		pr.add("fixedChildCategories");
		ext.setPropsToExtract(pr);
		try {
			ext.extract();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		ImportFileParser par = new ImportFileParser(ImportFileParser.PHASE_ADD_UPDATE, file);
		ImportItem items[] = par.parseFile();

		Node root = new Node(null);
		List<Node> rootCategories = new ArrayList<Node>();
		for (int i = 0; i < items.length; i++) {
			String value = items[i].getProperties().get("root");
			if ("true".equals(value)) {
				Node rootCat = new Node(items[i]);
				root.add(rootCat);
				rootCategories.add(rootCat);
			}
		}
		for (int i = 0; i < rootCategories.size(); i++) {
			buildTree(rootCategories.get(i), items);
		}
		Set<ImportItem> pivots = new HashSet<ImportItem>();
		for (int i = 0; i < rootCategories.size(); i++) {
			analizeTree(rootCategories.get(i), pivots);
		}

		file.delete();

		file = new File(pDestinationFolder, "update_pivots.xml");
		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter osw = new OutputStreamWriter(fos, UTF_8);
		BufferedWriter mOutputStreamWriter = new BufferedWriter(osw, 1024 * 32);

		try {
			mOutputStreamWriter.write(XML_VERSION);
			mOutputStreamWriter.write(DOCTYPE_GSA_TEMPLATE);
			mOutputStreamWriter.write(GSA_TEMPLATE_START_TAG);

			for (Iterator<ImportItem> iterator = pivots.iterator(); iterator.hasNext();) {
				ImportItem item = iterator.next();
				mOutputStreamWriter.write("        <update-item item-descriptor=\"" + item.getItemDescriptor()
						+ "\" id=\"" + StringEscapeUtils.escapeXml(item.getItemId()) + "\">\n");
				mOutputStreamWriter
						.write("            <set-property name=\"template\"><![CDATA[mPivotTemplate]]></set-property>\n");
				mOutputStreamWriter.write("            <set-property name=\"pivot\"><![CDATA[true]]></set-property>\n");
				mOutputStreamWriter.write("        </update-item>\n");
			}

			mOutputStreamWriter.write("</gsa-template>\n");
		} finally {
			try {
				mOutputStreamWriter.close();
			} catch (Exception e) {
			}
		}
		System.out.println(taskName + " finished. Time=" + (System.currentTimeMillis() - start) + "mls");
		return file;
	}

	private static void analizeTree(Node rootCat, Set<ImportItem> pivots) {
		List<Node> childred = rootCat.childred;
		boolean atLeastOneHaveChildren = false;
		for (int i = 0; i < childred.size(); i++) {
			if (childred.get(i).childred.size() > 0) {
				atLeastOneHaveChildren = true;
				break;
			}
		}
		if (!atLeastOneHaveChildren) {
			pivots.add(rootCat.mItem);
		} else {
			for (int i = 0; i < childred.size(); i++) {
				analizeTree(childred.get(i), pivots);
			}
		}
	}

	private static void buildTree(Node rootCat, ImportItem items[]) {
		String children = rootCat.mItem.getProperties().get("fixedChildCategories");
		if (children == null || children.trim().length() == 0) {
			return;
		}
		StringTokenizer tokenozer = new StringTokenizer(children, ",");
		for (int i = 0; i < tokenozer.countTokens(); i++) {
			String token = tokenozer.nextToken();
			ImportItem child = null;
			for (int j = 0; j < items.length; j++) {
				if (items[j].getItemId().equals(token.trim())) {
					child = items[j];
					break;
				}
			}
			if (child != null) {
				Node n = new Node(child);
				rootCat.add(n);
				buildTree(n, items);
			}
		}
	}

	public static ExtractionResult extractMedias(File pXMLFile, File pDestinationFolder,
			Map<String, Integer> pItemsTypeMap) {
		String taskName = "extracting media";
		System.out.println("starting " + taskName + "...Thread=" + Thread.currentThread().getName());
		long start = System.currentTimeMillis();
		File file = new File(pDestinationFolder, FILE_ALL_MEDIAS_XML);
		String[] items = new String[] { ITEM_FOLDER, ITEM_MEDIA_EXTERNAL, ITEM_MEDIA_INTERNAL_BINARY,
				ITEM_MEDIA_INTERNAL_TEXT };
		ItemsExractor ext = new ItemsExractor(ADD, pXMLFile, file, items, 0, calculateItemsCount(pItemsTypeMap, items));
		try {
			ext.extract();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		int extractedItemsCount = ext.extractedItemsCount;
		System.out.println(taskName + " finished. " + extractedItemsCount + " item(s) extracted. Time="
				+ (System.currentTimeMillis() - start) + "mls");
		return new ExtractionResult(file, extractedItemsCount, "/atg/commerce/catalog/ProductCatalog");
	}

	private static ExtractionResult extractCastoSKUMagasins(File pXMLFile, File pDestinationFolder,
			Map<String, Integer> pItemsTypeMap) {
		String taskName = "extracting casto_skus_magasins";
		System.out.println("starting " + taskName + "...Thread=" + Thread.currentThread().getName());
		long start = System.currentTimeMillis();
		File file = new File(pDestinationFolder, FILE_ALL_CASTO_SKUS_MAGASINS_XML);
		String[] items = new String[] { ITEM_CASTO_SKUS_MAGASINS };
		ItemsExractor ext = new ItemsExractor(ADD, pXMLFile, file, items, 0, calculateItemsCount(pItemsTypeMap, items));
		try {
			ext.extract();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		int extractedItemsCount = ext.extractedItemsCount;
		System.out.println(taskName + " finished. " + extractedItemsCount + " item(s) extracted. Time="
				+ (System.currentTimeMillis() - start) + "mls");
		return new ExtractionResult(file, extractedItemsCount, "/atg/commerce/catalog/ProductCatalog");
	}

	private static ExtractionResult extractCrossSellingSKUs(File pXMLFile, File pDestinationFolder,
			Map<String, Integer> pItemsTypeMap) {
		String taskName = "extracting CrossSellingSku";
		System.out.println("starting " + taskName + "...Thread=" + Thread.currentThread().getName());
		long start = System.currentTimeMillis();
		File file = new File(pDestinationFolder, FILE_ALL_CROSS_SELLING_SKU_XML);
		String[] items = new String[] { ITEM_CROSS_SELLING_SKU };
		ItemsExractor ext = new ItemsExractor(ADD, pXMLFile, file, items, 0, calculateItemsCount(pItemsTypeMap, items));
		try {
			ext.extract();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		int extractedItemsCount = ext.extractedItemsCount;
		System.out.println(taskName + " finished. " + extractedItemsCount + " item(s) extracted. Time="
				+ (System.currentTimeMillis() - start) + "mls");
		return new ExtractionResult(file, extractedItemsCount, "/atg/commerce/catalog/ProductCatalog");
	}

	private static ExtractionResult extractAux(File pXMLFile, File pDestinationFolder,
			Map<String, Integer> pItemsTypeMap) {
		String taskName = "extracting suplimentary items ('marque', 'nuancier', etc. )";
		System.out.println("starting " + taskName + "...Thread=" + Thread.currentThread().getName());
		long start = System.currentTimeMillis();
		File file = new File(pDestinationFolder, FILE_ALL_AUX_XML);
		String[] items = new String[] { ITEM_MARQUE, ITEM_NUANCIER, ITEM_ATTRIBUTS_CASTO, ITEM_COULEUR,
				ITEM_CODES_UNITE, ITEM_QUESTION_AFFINAGE, ITEM_REPONSE_AFFINAGE };
		ItemsExractor ext = new ItemsExractor(ADD, pXMLFile, file, items, 0, calculateItemsCount(pItemsTypeMap, items));
		try {
			ext.extract();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		int extractedItemsCount = ext.extractedItemsCount;
		System.out.println(taskName + " finished. " + extractedItemsCount + " item(s) extracted. Time="
				+ (System.currentTimeMillis() - start) + "mls");
		return new ExtractionResult(file, extractedItemsCount, "/atg/commerce/catalog/ProductCatalog");
	}

	private static ExtractionResult extractPromos(File pXMLFile, File pDestinationFolder,
			Map<String, Integer> pItemsTypeMap) {
		String taskName = "extracting promotions";
		System.out.println("starting " + taskName + "...Thread=" + Thread.currentThread().getName());
		long start = System.currentTimeMillis();
		File file = new File(pDestinationFolder, FILE_ALL_PROMOS_XML);
		String[] items = new String[] { ITEM_DISCOUNT_PERCENT_OFF, ITEM_DISCOUNT_AMOUNT_OFF,
				ITEM_SHIPPING_DISCOUNT_AMOUNT_OFF, ITEM_DISCOUNT_FIXED_PRICE, ITEM_DISCOUNT_AMOUNT_OFF,
				ITEM_SHIPPING_DISCOUNT_FIXED_PRICE, ITEM_ORDER_DISCOUNT_AMOUNT_OFF, ITEM_ORDER_DISCOUNT_PERCENT_OFF,
				ITEM_SHIPPING_DISCOUNT_PERCENT_OFF };
		ItemsExractor ext = new ItemsExractor(ADD, pXMLFile, file, items, 0, calculateItemsCount(pItemsTypeMap, items));
		try {
			ext.extract();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		int extractedItemsCount = ext.extractedItemsCount;
		System.out.println(taskName + " finished. " + extractedItemsCount + " item(s) extracted. Time="
				+ (System.currentTimeMillis() - start) + "mls");
		return new ExtractionResult(file, extractedItemsCount, "/atg/commerce/catalog/ProductCatalog");
	}

	public ItemsExractor(int pPhase, File pImportFile, File pExtractFile) {
		mPhase = pPhase;
		mImportFile = pImportFile;
		mExtractFile = pExtractFile;
	}

	public ItemsExractor(int pPhase, File pImportFile, File pExtractFile, String[] pItemsFilter) {
		mPhase = pPhase;
		mImportFile = pImportFile;
		mExtractFile = pExtractFile;
		mItemsFilter = pItemsFilter;
	}

	public ItemsExractor(int pPhase, File pImportFile, File pExtractFile, String[] pItemsFilter, int pStartIndex,
			int pBatchSize) {
		mPhase = pPhase;
		mImportFile = pImportFile;
		mExtractFile = pExtractFile;
		mItemsFilter = pItemsFilter;
		mStartIndex = pStartIndex;
		mBatchSize = pBatchSize;
	}

	public void extract() throws Exception {
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
			return;
		} catch (SAXException e) {
			System.out.println("PARSER: SAXException: " + e.getMessage());
			return;
		}

		// Create an input source and parse the string.

		mExtractFile.getParentFile().mkdirs();
		FileOutputStream fos = new FileOutputStream(mExtractFile);
		OutputStreamWriter osw = new OutputStreamWriter(fos, UTF_8);
		mOutputStreamWriter = new BufferedWriter(osw, 1024 * 32);

		mOutputStreamWriter.write(XML_VERSION);
		mOutputStreamWriter.write(DOCTYPE_GSA_TEMPLATE);
		mOutputStreamWriter.write(GSA_TEMPLATE_START_TAG);
		if (mPhase == ADD) {
			mOutputStreamWriter.write("<import-items>\n");
		}

		try {
			InputSource is = new InputSource(new FileInputStream(mImportFile));
			is.setEncoding("ISO-8859-1");
			reader.parse(is);
		} catch (SAXException e) {
			if (!(e.getException() instanceof DoneException)) {
				System.out.println("PARSER: SAXException: " + e.getMessage());
				return;
			} else {
				// System.out.println("DONE");
			}
		} catch (IOException e) {
			System.out.println("PARSER: IOException: " + e.getMessage());
			return;
		} finally {
			try {
				if (mPhase == ADD) {
					mOutputStreamWriter.write("</import-items>\n");
				}
				mOutputStreamWriter.write("</gsa-template>\n");
				mOutputStreamWriter.flush();
				mOutputStreamWriter.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	StringBuffer record = null;
	boolean wasProps = false;

	public void startElement(String namespaceURI, String localName, String qname, Attributes pAttributes)
			throws SAXException {
		mTagNames.push(qname);

		if (TemplateParser.TAG_ADD_ITEM.equals(qname)) {
			mItemDescriptor = pAttributes.getValue(TemplateParser.ATTR_ITEM_DESCRIPTOR);
			try {
				if (mItemsFilter == null || Arrays.asList(mItemsFilter).contains(mItemDescriptor)) {
					readTagAttributes(pAttributes);
					mAction = ImportItem.M_ACTION_ADD;
					mProperties = new HashMap<String, String>();

					record = new StringBuffer();
					record.append("    <" + (mPhase == ADD ? "add-item" : "update-item") + " item-descriptor=\""
							+ mItemDescriptor + "\" id=\"" + StringEscapeUtils.escapeXml(mItemId) + "\"");
					if (mPhase == UPDATE && isSkipUpdate()) {
						record.append(" skip-update=\"true\"");
					}
					record.append(">\n");
					wasProps = false;
					// mOutputStreamWriter.write(" <" + (mPhase == ADD ? "add-item" : "update-item") + "
					// item-descriptor=\"" + mItemDescriptor + "\" id=\""
					// + StringEscapeUtils.escapeXml(mItemId) + "\">\n");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (TemplateParser.TAG_UPDATE_ITEM.equals(qname)) {
			readTagAttributes(pAttributes);
			mAction = ImportItem.M_ACTION_UPDATE;
			mProperties = new HashMap<String, String>();
		} else if (TemplateParser.TAG_REMOVE_ITEM.equals(qname)) {
			readTagAttributes(pAttributes);
			mAction = ImportItem.M_ACTION_DELETE;
			mProperties = new HashMap<String, String>();
		} else if (TemplateParser.TAG_SET_PROPERTY.equals(qname)) {
			String propertyName = pAttributes.getValue(TemplateParser.ATTR_NAME);
			mPropertyName = propertyName;
			mPropertyValue = new StringBuffer();
		}
	}

	int counter = 0;
	int extractedItemsCount = 0;

	public void endElement(String namespaceURI, String localName, String pQName) throws SAXException {

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
			try {
				if (mPropsToAppend != null && mPropsToAppend.size() > 0) {
					Iterator<String> keys = mPropsToAppend.keySet().iterator();
					while (keys.hasNext()) {
						String propName = keys.next();
						String propval = mPropsToAppend.get(propName);
						record.append("        <set-property name=\"" + propName + "\"><![CDATA["
								+ propval + "]]></set-property>\n");
					}
				}
				record.append("    </" + (mPhase == ADD ? "add-item" : "update-item") + ">\n");
				// mOutputStreamWriter.write(" </" + (mPhase == ADD ? "add-item" : "update-item") + ">\n");
				if (mPhase == ADD || wasProps) {
					mOutputStreamWriter.write(record.toString());
					extractedItemsCount++;
					// mOutputStreamWriter.flush();
				}

				wasProps = false;
				record = null;
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		if (mBatchSize > 0 && mBatchSize == extractedItemsCount) {
			throw new SAXException(new DoneException());
		} else if (TemplateParser.TAG_SET_PROPERTY.equals(pQName)) {
			// mProperties.put(mPropertyName, mPropertyValue.toString());
			// mPropertyName = null;
			try {

				if ((mPropsToRemove == null || !mPropsToRemove.contains(mPropertyName))
						&& (mPropsToExtract == null || mPropsToExtract.contains(mPropertyName))) {
					/*
					 * mOutputStreamWriter.write(" <set-property name=\"" + mPropertyName + "\"><![CDATA[" +
					 * mPropertyValue.toString() + "]]></set-property>\n");
					 */
					if (PROPERTY_RETRAIT_MOMENTANE_DATE_DEBUT.equals(mPropertyName) && badSKUSData.contains(mItemId)) {
						record.append("        <set-property name=\"" + mPropertyName + "\"><![CDATA["
								+ "5/19/2009 00:00:00" + "]]></set-property>\n");
					} else {
						record.append("        <set-property name=\"" + mPropertyName + "\"><![CDATA["
								+ mPropertyValue.toString() + "]]></set-property>\n");
					}
					wasProps = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		mTagNames.pop();
	}

	private void readTagAttributes(Attributes pAttributes) {
		mItemDescriptor = pAttributes.getValue(TemplateParser.ATTR_ITEM_DESCRIPTOR);
		mItemId = pAttributes.getValue(TemplateParser.ATTR_ID);
		mRepositoryName = pAttributes.getValue(TemplateParser.ATTR_REPOSITORY);
	}

	public void characters(char[] chars, int start, int length) {
		if (TemplateParser.TAG_SET_PROPERTY.equals(mTagNames.peek())) {
			mPropertyValue.append(new String(chars, start, length));
		}
	}

	public void error(SAXParseException e) {
		System.out.println("ERROR: " + e.getMessage());
	}

	public List<String> getPropsToRemove() {
		return mPropsToRemove;
	}

	public void setPropsToRemove(List<String> pPropsToRemove) {
		mPropsToRemove = pPropsToRemove;
	}

	public List<String> getPropsToExtract() {
		return mPropsToExtract;
	}

	public void setPropsToExtract(List<String> pPropsToExtract) {
		mPropsToExtract = pPropsToExtract;
	}

	public boolean isSkipUpdate() {
		return mSkipUpdate;
	}

	public void setSkipUpdate(boolean pSkipUpdate) {
		mSkipUpdate = pSkipUpdate;
	}

	public Map<String, String> getPropsToAppend() {
		return mPropsToAppend;
	}

	public void setPropsToAppend(Map<String, String> pPropsToAppend) {
		mPropsToAppend = pPropsToAppend;
	}

}
