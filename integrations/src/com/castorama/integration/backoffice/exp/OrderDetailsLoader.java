/**
 * 
 */
package com.castorama.integration.backoffice.exp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.castorama.integration.backoffice.exp.ArrayItemIterator;

import atg.repository.RepositoryItem;

/**
 * @author Andrew_Logvinov
 *
 */
class OrderDetailsLoader {
	
	private RepositoryItem order;
	

	public OrderDetailsLoader(RepositoryItem order) {
		this.order = order;
	}


	public Iterator<RepositoryItem> iterator() {
		Collection details = (Collection) order.getPropertyValue("commerceItems");
		List<RepositoryItem> items = new ArrayList<RepositoryItem>();
		if (null != details) {
			Iterator it = details.iterator();
			
			while (it.hasNext()) {
				items.add((RepositoryItem) it.next());
			}
		}
		
		return new ArrayItemIterator(items.toArray(new RepositoryItem[items.size()]));
	}

}
