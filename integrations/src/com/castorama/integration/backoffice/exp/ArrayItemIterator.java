/**
 * 
 */
package com.castorama.integration.backoffice.exp;

import java.util.Iterator;

import atg.repository.RepositoryItem;

/**
 * @author Andrew_Logvinov
 *
 */
class ArrayItemIterator implements Iterator<RepositoryItem> {
	private final RepositoryItem[] items;
	
	private int currentId = -1;

	public ArrayItemIterator(RepositoryItem[] items) {
		this.items = items;
	}

	public boolean hasNext() {
		return (currentId + 1) < items.length;
	}

	public RepositoryItem next() {
		return items[++currentId];
	}

	public void remove() {
		throw new UnsupportedOperationException(getClass().getName() + " void remove()");			
	}			
}
