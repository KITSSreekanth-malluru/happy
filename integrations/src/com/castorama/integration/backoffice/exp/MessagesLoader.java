package com.castorama.integration.backoffice.exp;

import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;

import com.castorama.integration.backoffice.exp.ArrayItemIterator;

import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.QueryBuilder;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

class MessagesLoader {
	
	private final Repository repository;

	public MessagesLoader(Repository repository) {
		this.repository = repository;
	}

	public Iterator<RepositoryItem> iterator() throws RepositoryException {
        MutableRepository repo = (MutableRepository) getRepository();
		RepositoryView view = repo.getView("CASTO_CONTACT");
		QueryBuilder qb = view.getQueryBuilder();
		
		RepositoryItem[] items = view.executeQuery(
				qb.createIsNullQuery(qb.createPropertyQueryExpression("DATEENVOYEBO")));
		
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

	/**
	 * @param itm
	 * @throws RepositoryException
	 */
	public void registerExported(RepositoryItem itm) throws RepositoryException {
		MutableRepositoryItem mri = (MutableRepositoryItem) itm;
		mri.setPropertyValue("DATEENVOYEBO", new Date());
		((MutableRepository)getRepository()).updateItem((MutableRepositoryItem) itm);
	}

}
