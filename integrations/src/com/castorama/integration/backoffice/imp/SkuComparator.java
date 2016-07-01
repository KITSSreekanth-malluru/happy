package com.castorama.integration.backoffice.imp;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import atg.core.util.StringUtils;
import atg.repository.RepositoryItem;

public class SkuComparator {
	private RepositoryItem skuItem;
	private boolean filterData;
	
	private Document document;

	public SkuComparator() {
	}

	public SkuComparator(boolean filterData) {
		this.filterData = filterData;
	}
	
	public RepositoryItem getSkuItem() {
		return skuItem;
	}

	public void setSkuItem(RepositoryItem skuItem) {
		this.skuItem = skuItem;
	}
	
	public boolean isFilterData() {
		return filterData;
	}

	public void setFilterData(boolean filterData) {
		this.filterData = filterData;
	}

	public void loadXMLFrom(String xml) throws org.xml.sax.SAXException, java.io.IOException {
		String s = new String(xml.getBytes("ISO-8859-1"), "UTF-8");
		document = loadXMLFrom(new java.io.ByteArrayInputStream(s.getBytes("UTF-8")));
	}

	public Document loadXMLFrom(InputStream is) throws SAXException, IOException {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException ex) {
			ex.printStackTrace();
		}
		Document doc = builder.parse(is);
		
		is.close();
		
		return doc;
	}
	
	private static final String SET_PROPERTY = "set-property";

	private static final String PROP_valStockDestock = "valStockDestock";
	private static final String PROP_dateInitDestock = "dateInitDestock";
	private static final String PROP_dateDebDestock = "dateDebDestock";
	private static final String PROP_dateFinDestock = "dateFinDestock";
	private static final String PROP_CACHESKUCOMPTEUR = "CACHESKUCOMPTEUR";

	private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
	
	public boolean isEqualsSku(String xml) throws org.xml.sax.SAXException, java.io.IOException {
		if (isFilterData()) {
			loadXMLFrom(xml);
			return isEqualsSku();
		} else {
			return false;
		}
	}
	
	public boolean isEqualsSku() {
		boolean result = true;
		try {
			NodeList nodes = document.getElementsByTagName(SET_PROPERTY);
			if (nodes != null) {
				int len = nodes.getLength();
				for (int i=0; i < len; i++) {
					Node node = nodes.item(i);
					Node child = node.getFirstChild();
					String value = null;
					if (child != null) {
						value = child.getNodeValue();
					}
					Node n = node.getAttributes().getNamedItem("name");
					String nodeValue = n.getNodeValue();
					
					if (!compareProperty(nodeValue, value)) {
						result = false; 
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public boolean isEqualsInventory() {
		boolean result = false;
		try {
			NodeList nodes = document.getElementsByTagName(SET_PROPERTY);
			if (nodes != null) {
				int len = nodes.getLength();
				for (int i=0; i < len; i++) {
					Node node = nodes.item(i);
					Node child = node.getFirstChild();
					String value = null;
					if (child != null) {
						value = child.getNodeValue();
					}
					Node n = node.getAttributes().getNamedItem("name");
					String nodeValue = n.getNodeValue();

					if (PROP_valStockDestock.equals(nodeValue) 
							|| PROP_dateInitDestock.equals(nodeValue) 
							|| PROP_dateDebDestock.equals(nodeValue) 
							|| PROP_dateFinDestock.equals(nodeValue) 
							|| PROP_CACHESKUCOMPTEUR.equals(nodeValue)) {
						
						if (!compareProperty(nodeValue, value)) {
							result = false; 
						}
						
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	private boolean compareProperty(String nodeName, String nodeValue) {
		boolean result = false;
		Object riValue = null;
		try {
			riValue = getSkuItem().getPropertyValue(nodeName);

			
			if (riValue == null) {
				result = StringUtils.isEmpty(nodeValue);
			} else if (StringUtils.isEmpty(nodeValue)) {
				result = false;
			} else if (riValue instanceof String) {
				String riVal = new String(((String)riValue).getBytes("ISO-8859-1"), "UTF-8");
				result = (riVal.compareTo(nodeValue) == 0);
				
			} else if (riValue instanceof Integer) {
				Integer riVal = (Integer) riValue;
				Integer nv = Integer.valueOf(nodeValue);
				
				result = (riVal.compareTo(nv) == 0);
				
			} else if (riValue instanceof Double) {
				Double riVal = (Double) riValue;
				Double nv = Double.valueOf(nodeValue);
				
				result = (riVal.compareTo(nv) == 0);
				
			} else if (riValue instanceof Float) {
				Float riVal = (Float) riValue;
				Float nv = Float.valueOf(nodeValue);
				
				result = (riVal.compareTo(nv) == 0);
				
			} else if (riValue instanceof Boolean) {
				Boolean riVal = (Boolean) riValue;
				Boolean nv = Boolean.valueOf(nodeValue);
				
				result = (riVal.compareTo(nv) == 0);

			} else if (riValue instanceof Date) {
				Date riVal = (Date) riValue;
				Date nvd = null;
				if (nodeValue.indexOf(":") == -1) {
					nvd = DATE_FORMAT.parse(nodeValue);
				} else {
					nvd = DATETIME_FORMAT.parse(nodeValue);
				}
				result = (riVal.compareTo(nvd) == 0); 
			} else {
				//System.out.println("!!!! type not define");
			}
			
			if (PROP_dateInitDestock.equals(nodeName)) {
				if (!StringUtils.isBlank(nodeValue)) {
					Date dateInitDestock = DATE_FORMAT.parse(nodeValue);
					Date currentDate = DATE_FORMAT.parse(DATE_FORMAT.format(new Date()));
					
					if (currentDate.compareTo(dateInitDestock) == 0) {
						result = false;
					}
				}
			}
			
			if (PROP_dateFinDestock.equals(nodeName)) {
				if (!StringUtils.isBlank(nodeValue)) {
					Date dateFinDestock = DATE_FORMAT.parse(nodeValue);
					Calendar calFinDestock = Calendar.getInstance();
					calFinDestock.setTime(dateFinDestock);
					calFinDestock.add(Calendar.DATE, 1);
					dateFinDestock = calFinDestock.getTime();

					Date currentDate = DATE_FORMAT.parse(DATE_FORMAT.format(new Date()));
					
					if (currentDate.compareTo(dateFinDestock) == 0) {
						result = false;
					}
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!result) {
			//System.out.print("nodeName=" + nodeName + "; nodeValue=" + nodeValue + "; repItemValue=" + riValue + "; result=" +result);
		}
		return result;
	}
	
	

}
