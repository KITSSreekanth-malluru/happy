package com.castorama.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Tabs for grouped products.
 * 
 * @author Alena_Karpenkava
 * 
 */
public class LinkedSkuTabNamesIteratorDroplet extends DynamoServlet {

	/** Output parameters */
	public static String OUTPUT = "output";
	public static String GROUPED_PROD_MAP = "groupedProdMap";

	/** Input parameters */
	public static String BUNDLE_LINKS = "bundleLinks";
	public static String ASSEMBLY_OPTIONS = "assemblyOptions";

	/** Constants */
	public static String TAB_NAME = "tabName";
	public static String ITEM = "item";

	/**
	 * Grouped product may have additional (dynamic) tabs displayed in the case where business user linked additional
	 * SKUs (via SKU link) with a corresponding "tabName" The SKU should be displayed on the tub that will be named
	 * respectively. SKUs associated with a groped product via the SKU link without "tabName" should not be displayed.
	 * Tab corresponding to the "tabName" should only be displayed if there are SKUs available and displayed on this
	 * tab, otherwise tab should not be displayed.
	 * 
	 * @param pRequest -
	 *            request
	 * @param pResponse -
	 *            response
	 * @throws IOException -
	 *             exception
	 * @throws ServletException -
	 *             exception
	 */
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {

		List bundleLinks = (List) pRequest.getObjectParameter(BUNDLE_LINKS);
		List assemblyOptions = (List) pRequest.getObjectParameter(ASSEMBLY_OPTIONS);
		if (bundleLinks != null) {
			List allSkus = new ArrayList();
			allSkus.addAll(bundleLinks);
			if (assemblyOptions != null) {
				allSkus.addAll(assemblyOptions);

				List tabList = new ArrayList();

				for (int i = 0; i < allSkus.size(); i++) {
					RepositoryItem skuLink = (RepositoryItem) allSkus.get(i);
					if (skuLink != null) {
						String tabName = (String) skuLink.getPropertyValue(TAB_NAME);
						if (tabName != null) {
							tabList.add(tabName);
						}
					}
				}

				Set tabNames = new LinkedHashSet(tabList);
				Map groupedProdMap = new HashMap();

				Iterator iterator = tabNames.iterator();
				while (iterator.hasNext()) {
					String element = (String) iterator.next();
					List skuList = new ArrayList();
					for (int i = 0; i < allSkus.size(); i++) {
						RepositoryItem skuLink = (RepositoryItem) allSkus.get(i);
						if (element.equals(skuLink.getPropertyValue(TAB_NAME))) {
							skuList.add(skuLink.getPropertyValue(ITEM));
						}
					}
					groupedProdMap.put(element, skuList);
				}

				pRequest.setParameter(GROUPED_PROD_MAP, groupedProdMap);
				pRequest.setParameter(TAB_NAME, tabNames);
			}
		}

		pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
	}
}
