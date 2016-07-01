package com.castorama.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;

import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.castorama.constantes.CastoConstantesCommande;

/**
 * Extends DynamoServlet to returns list of delivery items, without zero items.
 * 
 * @author Vasili_Ivus
 *
 */
public class CastOrderDeliveryItemsDroplet extends DynamoServlet {

	public final static String ELEMENT = "element";
	public final static String INDEX = "index";
	public final static String COUNT = "count";
	public final static String SIZE = "size";

	public final static String DELIVERY = "delivery";
	public final static String OUTPUT_START = "outputStart";
	public final static String OUTPUT_END = "outputEnd";
	public final static String OUTPUT = "output";
	public final static String EMPTY = "empty";
	public final static String ELEMENT_NAME = "elementName";
	public final static String INDEX_NAME = "indexName";

    /**
    * Returns list of delivery items, without zero items.
    * 
    * @param  pRequest parameter
    * @param  pResponse parameter
    *
    * @throws ServletException exception, when logic error.
    * @throws IOException      exception, when io error.
    */
	@Override 
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) 
			throws ServletException, IOException {

		boolean isEmpty = true;
		
		Object delivery = pRequest.getObjectParameter(DELIVERY);
	    if ( delivery instanceof RepositoryItem ) {
	    	Set<RepositoryItem> deliveryItems = (Set<RepositoryItem>) ((RepositoryItem ) delivery).getPropertyValue(CastoConstantesCommande.BO_ORDER_PROPERTY_VTE_LIGNES);
	    	List<RepositoryItem> outputItems = new ArrayList<RepositoryItem>(deliveryItems.size());
			for (RepositoryItem deliveryItem : deliveryItems) {
				Float quantity = (Float) deliveryItem.getPropertyValue(CastoConstantesCommande.BO_ORDER_PROPERTY_QTE_SAISI);
				if ( null != quantity && 0 < quantity.intValue() ) {
					outputItems.add(deliveryItem);
				}
			}
			int size = outputItems.size();
			if ( 0 < size ) {
			    pRequest.setParameter(SIZE, size);
			    pRequest.serviceLocalParameter(OUTPUT_START, pRequest, pResponse);
			    int count = 0;
			    String indexName = pRequest.getParameter(INDEX_NAME);
			    if ( null == indexName || 0 == indexName.trim().length() ) {
			    	indexName = INDEX;
			    }
			    String elementName = pRequest.getParameter(ELEMENT_NAME);
			    if ( null == elementName || 0 == elementName.trim().length() ) {
			    	elementName = ELEMENT;
			    }
			    Iterator<RepositoryItem> iterator = outputItems.iterator(); 
			    while (iterator.hasNext() ) {
			    	pRequest.setParameter(COUNT, count + 1);
			    	pRequest.setParameter(indexName, count);
			    	pRequest.setParameter(elementName, iterator.next());
			        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
			        count++;
			   }
			   pRequest.serviceLocalParameter(OUTPUT_END, pRequest, pResponse);
			   isEmpty = false;
			}
			if ( isEmpty ) {
				pRequest.serviceLocalParameter (EMPTY, pRequest, pResponse);
			}
		}

   }

}
