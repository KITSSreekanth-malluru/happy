package com.castorama.integration.backoffice.exp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.castorama.integration.backoffice.exp.ArrayItemIterator;

import atg.repository.RepositoryItem;

class OrderPaymentLoader {
	
	private final RepositoryItem order;

	public OrderPaymentLoader(RepositoryItem order) {
		this.order = order;
	}

	@SuppressWarnings("unchecked")
	public Iterator<RepositoryItem> iterator() {
		Collection groups = (Collection) order.getPropertyValue("paymentGroups");
		List<RepositoryItem> items = new ArrayList<RepositoryItem>();
		if (null != groups) {
			Iterator it = groups.iterator();
			
			while (it.hasNext()) {
				items.add((RepositoryItem) it.next());
			}
		}
		
		return new ArrayItemIterator(items.toArray(new RepositoryItem[items.size()]));
	}

}
