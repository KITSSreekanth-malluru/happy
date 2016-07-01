/**
 * 
 */
package com.castorama.integration.backoffice.exp;

import java.util.Iterator;

import com.castorama.commerce.states.BOOrderStates;
import com.castorama.integration.backoffice.exp.ArrayItemIterator;

import atg.commerce.states.OrderStates;
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
class LogSipsLoader {
	
	private final Repository repository;
	
	/**
	 * @param repository 
	 * @param repository2 
	 * 
	 */
	public LogSipsLoader(Repository repository) {
		this.repository = repository;
	}

	public Iterator<RepositoryItem> iterator(RepositoryItem itm) throws RepositoryException {
        MutableRepository repo = (MutableRepository) getRepository();
		RepositoryView view = repo.getView("logsips");
		QueryBuilder qb = view.getQueryBuilder();
		
		Query query = qb.createAndQuery(new Query[]{
				qb.createComparisonQuery(
						qb.createPropertyQueryExpression("order_id"),
						qb.createConstantQueryExpression(itm.getPropertyValue("id")),
						QueryBuilder.EQUALS),
				qb.createComparisonQuery(
						qb.createPropertyQueryExpression("response_code"),
						qb.createConstantQueryExpression("00000"),
						QueryBuilder.EQUALS)
						
		});
			
		RepositoryItem[] items = view.executeQuery(query);
		
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
