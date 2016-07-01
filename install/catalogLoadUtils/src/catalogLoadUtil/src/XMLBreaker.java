

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringEscapeUtils;

import atg.adapter.gsa.xml.ImportFileParser;
import atg.adapter.gsa.xml.ImportItem;


public class XMLBreaker {
	
	static ExecutorService MEDIA_THREAD_POOL = Executors.newFixedThreadPool(3);
	public static List<File> doBreakDownMedia(final File pSourceFile,  int itemsCount, final File pOutputFolder) throws Exception {
		System.out.println("START: BreakDownMedia. " + itemsCount + " to break.");
		final List<File> files = new ArrayList<File>();
		final int length = 10000;		
		System.out.println("START: BreakDownMedia. Items per file - " + length);
		int filesCount  = (int) Math.ceil(itemsCount / ((double) length));
		System.out.println("START: BreakDownMedia. Files count - " + filesCount);
		for (int i = 0; i < filesCount; i++) {			
			final int[] step  = {i};
			MEDIA_THREAD_POOL.execute(new Runnable() {			
				public void run() {					
					try {
						files.add(breakDownMedia(pSourceFile, pOutputFolder, length * step[0], length));
						System.gc();
					} catch (Exception e) {						
						e.printStackTrace();
					}
				}			
			});			
		}
		MEDIA_THREAD_POOL.shutdown();
		try {
			MEDIA_THREAD_POOL.awaitTermination(3600, TimeUnit.SECONDS);
		} catch (Exception e) {
			
		}
		System.out.println("END: BreakDownMedia");
		
		return files;
	}
	
	static ExecutorService MAGAS_THREAD_POOL = Executors.newFixedThreadPool(3);
	public static List<File> doBreakDownSKUMagasins(final File pSourceFile,  int itemsCount, final File pOutputFolder) throws Exception {
		System.out.println("START: BreakDownSKUMagasins. " + itemsCount + " to break.");
		final List<File> files = new ArrayList<File>();
		final int length = 10000;
		System.out.println("START: BreakDownSKUMagasins. Items per file - " + length);
		int filesCount  = (int) Math.ceil(itemsCount / ((double) length));
		System.out.println("START: BreakDownSKUMagasins. Files count - " + filesCount);
		for (int i = 0; i < filesCount; i++) {
			final int[] step  = {i};
			MAGAS_THREAD_POOL.execute(new Runnable() {			
				public void run() {					
					try {
						files.add(breakDownSKUMagasins(pSourceFile, pOutputFolder,length * step[0], length));
						System.gc();
					} catch (Exception e) {						
						e.printStackTrace();
					}
				}			
			});
			
		}
		MAGAS_THREAD_POOL.shutdown();
		try {
			MAGAS_THREAD_POOL.awaitTermination(3600, TimeUnit.SECONDS);
		} catch (Exception e) {
			
		}
		System.out.println("END: BreakDownSKUMagasins");
		
		return files;
	}
	
	public static List<File> doBreakDownCrossSells(final File pSourceFile,  int itemsCount, final File pOutputFolder) throws Exception {
		System.out.println("START: doBreakDownCrossSells. " + itemsCount + " to break.");
		final List<File> files = new ArrayList<File>();
		final int length = 2000;
		System.out.println("START: BreakDownCrossSells. Items per file - " + length);
		int filesCount  = (int) Math.ceil(itemsCount / ((double) length));
		System.out.println("START: BreakDownCrossSells. Files count - " + filesCount);
		for (int i = 0; i < filesCount; i++) {
			final int[] step  = {i};
			files.add(breakDownCrossSells(pSourceFile, pOutputFolder, length * i, length));			
		}		
		System.out.println("END: doBreakDownCrossSells");
		
		return files;
		
	}
	public static List<File> doBreakDownBundledLinks(final File pSourceFile,  int itemsCount, final File pOutputFolder) throws Exception {
		System.out.println("START: doBreakDownBundledLinks. " + itemsCount + " to break.");
		final List<File> files = new ArrayList<File>();
		final int length = 500;
		System.out.println("START: BreakDownBundledLinks. Items per file - " + length);
		int filesCount  = (int) Math.ceil(itemsCount / ((double) length));
		System.out.println("START: BreakDownBundledLinks. Files count - " + filesCount);
		for (int i = 0; i < filesCount; i++) {
			final int[] step  = {i};
			files.add(breakDownBundledLinks(pSourceFile, pOutputFolder, length * i, length));			
		}		
		System.out.println("END: doBreakDownBundledLinks");
		
		return files;
	}
	
	static ExecutorService PRODUCTS_THREAD_POOL = Executors.newFixedThreadPool(3);
	public static List<File> doBreakDownProducts(final File pSourceFile,  int itemsCount, final File pOutputFolder) throws Exception {
		System.out.println("START: BreakDownProducts. " + itemsCount + " to break.");
		final List<File> files = new ArrayList<File>();
		final int length = 5000;
		System.out.println("START: BreakDownProducts. Items per file - " + length);
		int filesCount  = (int) Math.ceil(itemsCount / ((double) length));
		System.out.println("START: BreakDownProducts. Files count - " + filesCount);
		for (int i = 0; i < filesCount; i++) {
			final int[] step  = {i};
			PRODUCTS_THREAD_POOL.execute(new Runnable() {			
				public void run() {					
					try {
						files.add(breakDownProducts(pSourceFile, pOutputFolder,length * step[0], length));
						System.gc();
					} catch (Exception e) {						
						e.printStackTrace();
					}
				}			
			});
			
		}
		PRODUCTS_THREAD_POOL.shutdown();
		try {
			PRODUCTS_THREAD_POOL.awaitTermination(3600, TimeUnit.SECONDS);
		} catch (Exception e) {
			
		}
		System.out.println("END: BreakDownProducts");
		
		return files;
	}
	
	static ExecutorService SKUS_THREAD_POOL = Executors.newFixedThreadPool(3);
	public static List<File> doBreakDownSKUs(final File pSourceFile, int itemsCount, final File pOutputFolder) throws Exception {
		System.out.println("START: BreakDownSKUs. " + itemsCount + " to break.");
		final List<File> files = new ArrayList<File>();
		final int length = 5000;	
		System.out.println("START: BreakDownSKUs. Items per file - " + length);
		int filesCount  = (int) Math.ceil(itemsCount / ((double) length));
		System.out.println("START: BreakDownSKUs. Files count - " + filesCount);
		for (int i = 0; i < filesCount; i++) {
			final int[] step  = {i};
			SKUS_THREAD_POOL.execute(new Runnable() {			
				public void run() {					
					try {
						files.add(breakDownSKUs(pSourceFile, pOutputFolder, length * step[0], length));
						System.gc();
					} catch (Exception e) {						
						e.printStackTrace();
					}
				}			
			});				
		}
		SKUS_THREAD_POOL.shutdown();
		try {
			SKUS_THREAD_POOL.awaitTermination(3600, TimeUnit.SECONDS);
		} catch (Exception e) {
			
		}
		System.out.println("END: BreakDownSKUs");
		
		return files;
	}

	public static void writeToFile(File file, ImportItem[] importItems) throws Exception {
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
		BufferedWriter f = new BufferedWriter(osw, 1024 * 32);
		f.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
		f.write("<!DOCTYPE gsa-template SYSTEM \"dynamosystemresource:/atg/dtds/gsa/gsa_1.0.dtd\">\n");
		f.write("<gsa-template>\n");
		f.write("    <import-items>\n");
		for (int i = 0; i < importItems.length; i++) {
			String id = importItems[i].getItemId();
			f.write("        <add-item item-descriptor=\"" + importItems[i].getItemDescriptor() + "\" id=\""
					+ StringEscapeUtils.escapeXml(id) + "\">\n");
			Map<String, String> properties = importItems[i].getProperties();
			Iterator iterator = properties.keySet().iterator();
			String propertyName = null, propertyValue = null;
			while (iterator.hasNext()) {
				propertyName = (String) iterator.next();
				propertyValue = (String) properties.get(propertyName);
				f.write("            <set-property name=\"" + propertyName + "\"><![CDATA[" + propertyValue
						+ "]]></set-property>\n");

			}
			f.write("        </add-item>\n");
		}
		f.write("    </import-items>\n");
		f.write("</gsa-template>");

		f.close();

	}
	
	public static void writeToFileUpdate(File file, ImportItem[] importItems) throws Exception {
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
		BufferedWriter f = new BufferedWriter(osw, 1024 * 32);
		f.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
		f.write("<!DOCTYPE gsa-template SYSTEM \"dynamosystemresource:/atg/dtds/gsa/gsa_1.0.dtd\">\n");
		f.write("<gsa-template>\n");		
		for (int i = 0; i < importItems.length; i++) {
			String id = importItems[i].getItemId();
			f.write("        <update-item item-descriptor=\"" + importItems[i].getItemDescriptor() + "\" id=\""
					+ StringEscapeUtils.escapeXml(id) + "\">\n");
			Map<String, String> properties = importItems[i].getProperties();
			Iterator iterator = properties.keySet().iterator();
			String propertyName = null, propertyValue = null;
			while (iterator.hasNext()) {
				propertyName = (String) iterator.next();
				propertyValue = (String) properties.get(propertyName);
				f.write("            <set-property name=\"" + propertyName + "\"><![CDATA[" + propertyValue
						+ "]]></set-property>\n");

			}
			f.write("        </update-item>\n");
		}		
		f.write("</gsa-template>");

		f.close();

	}
	
	public static File breakDownSKUMagasins(File pSourceFile, File pOutputFolder, int startIndex, int length) throws Exception {
		System.out.println("Entering breakDownSKUMagasins. Thread=" + Thread.currentThread().getName());
		ImportFileParser par = new ImportFileParser(ImportFileParser.PHASE_ADD_UPDATE, pSourceFile,
				new String[] { "casto_skus_magasins" }, startIndex, length);
		ImportItem[] importItems = par.parseFile();

		pOutputFolder.mkdirs();
		
		File file = new File(pOutputFolder, "sku_magasins_" + startIndex + "-" + (startIndex + importItems.length - 1)
				+ ".xml");		
		System.out.println("breakDownSKUMagasins: writting to " + file.getAbsolutePath());
		writeToFile(file, importItems);	
		
		return file;
	}

	public static File breakDownProducts(File pSourceFile, File pOutputFolder, int startIndex, int length) throws Exception {
		System.out.println("Entering breakDownProducts. Thread=" + Thread.currentThread().getName());
		ImportFileParser par = new ImportFileParser(ImportFileParser.PHASE_ADD_UPDATE, pSourceFile,
				new String[] { "casto_product" }, startIndex, length);
		ImportItem[] importItems = par.parseFile();
		
		pOutputFolder.mkdirs();
		
		File file = new File(pOutputFolder, "product_" + startIndex + "-"
				+ (startIndex + importItems.length - 1) + ".xml");		
		System.out.println("breakDownProducts: writting to " + file.getAbsolutePath());
		writeToFile(file, importItems);		
		
		return file;
	}

	public static File breakDownSKUs(File pSourceFile, File pOutputFolder, int startIndex, int length) throws Exception {	
		System.out.println("Entering breakDownSKUs. Thread=" + Thread.currentThread().getName());
		ImportFileParser par = new ImportFileParser(ImportFileParser.PHASE_ADD_UPDATE, pSourceFile, new String[] {
				"casto_sku", "sku" }, startIndex, length);
		ImportItem[] importItems = par.parseFile();
		
		pOutputFolder.mkdirs();
		
		File file = new File(pOutputFolder, "sku_" + startIndex + "-" + (startIndex + importItems.length - 1)
				+ ".xml");
		System.out.println("breakDownSKUs: writting to " + file.getAbsolutePath());
		writeToFile(file, importItems);	
		
		return file;
	}
	
	public static File breakDownCrossSells (File pSourceFile, File pOutputFolder, int startIndex, int length) throws Exception {
		System.out.println("Entering breakDownCrossSells. Thread=" + Thread.currentThread().getName());
		ImportFileParser par = new ImportFileParser(ImportFileParser.PHASE_ADD_UPDATE, pSourceFile, new String[] {
				"casto_sku", "sku" }, startIndex, length);
		ImportItem[] importItems = par.parseFile();
		
		pOutputFolder.mkdirs();
		
		File file = new File(pOutputFolder, "crossSells_" + startIndex + "-" + (startIndex + importItems.length - 1)
				+ ".xml");
		System.out.println("breakDownCrossSells: writting to " + file.getAbsolutePath());
		writeToFileUpdate(file, importItems);	
		
		return file;
	}
	
	public static File breakDownBundledLinks (File pSourceFile, File pOutputFolder, int startIndex, int length) throws Exception {
		System.out.println("Entering breakDownBundledLinks. Thread=" + Thread.currentThread().getName());
		ImportFileParser par = new ImportFileParser(ImportFileParser.PHASE_ADD_UPDATE, pSourceFile, new String[] {
				"casto_sku", "sku" }, startIndex, length);
		ImportItem[] importItems = par.parseFile();
		
		pOutputFolder.mkdirs();
		
		File file = new File(pOutputFolder, "bundledLinks_" + startIndex + "-" + (startIndex + importItems.length - 1)
				+ ".xml");
		System.out.println("breakDownBundledLinks: writting to " + file.getAbsolutePath());
		writeToFileUpdate(file, importItems);	
		
		return file;
	}
	
	
	public static File breakDownMedia(File pSourceFile, File pOutputFolder, int startIndex, int length) throws Exception {	
		System.out.println("Entering breakDownMedia. Thread=" + Thread.currentThread().getName());
		ImportFileParser par = new ImportFileParser(ImportFileParser.PHASE_ADD_UPDATE, pSourceFile, new String[] {
				"folder", "media-external", "media-internal-binary" }, startIndex, length);
		ImportItem[] importItems = par.parseFile();
		
		pOutputFolder.mkdirs();
		
		File file = new File(pOutputFolder, "medias_" + startIndex + "-" + (startIndex + importItems.length - 1)
				+ ".xml");	
		System.out.println("breakDownMedia: writting to " + file.getAbsolutePath());
		writeToFile(file, importItems);	
		
		return file;
	}
}
