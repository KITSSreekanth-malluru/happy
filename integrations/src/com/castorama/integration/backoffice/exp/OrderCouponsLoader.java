/**
 * 
 */
package com.castorama.integration.backoffice.exp;

import java.util.Iterator;

import com.castorama.integration.backoffice.exp.ArrayItemIterator;
import com.castorama.model.Coupon;

import atg.repository.MutableRepository;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

/**
 * @author Andrew_Logvinov
 *
 */
class OrderCouponsLoader {
	
	private final Repository repository;
	
	/**
	 * @param repository 
	 * @param repository2 
	 * 
	 */
	public OrderCouponsLoader(Repository repository) {
		this.repository = repository;
	}

	public Iterator<RepositoryItem> iterator(String orderId) throws RepositoryException {
		RepositoryItem[] items = null;
		
		if (null != orderId) {
	        MutableRepository repo = (MutableRepository) getRepository();
			RepositoryView view = repo.getView("casto_coupons");
			QueryBuilder qb = view.getQueryBuilder();
			
			Query query = qb.createComparisonQuery(qb.createPropertyQueryExpression(Coupon.ORDER_ID),
							qb.createConstantQueryExpression(orderId),
							QueryBuilder.EQUALS);
			
			items = view.executeQuery(query);
		}
		
		if (null == items) {
			items = new RepositoryItem[]{};
		}
		
		return new ArrayItemIterator(items);
	}
	
	/**
	 * @return the repository
	 */
	public Repository getRepository() {
		return repository;
	}
}
