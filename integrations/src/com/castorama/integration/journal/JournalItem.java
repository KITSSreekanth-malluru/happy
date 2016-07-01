package com.castorama.integration.journal;

import atg.repository.MutableRepositoryItem;

/**
 * @author Andrew_Logvinov
 */
public class JournalItem {

	private final MutableRepositoryItem item;

	/**
	 * @return the item
	 */
	MutableRepositoryItem getItem() {
		return item;
	}

	JournalItem(MutableRepositoryItem item) {
		this.item = item;
	}

	public long getSequence() {
		return (Long) item.getPropertyValue(ProcessingJournalService.FIELD_SEQUENCE);
	}
}
