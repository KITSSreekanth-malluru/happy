package com.castorama.integration.bazaarvoice;

import atg.repository.RepositoryItem;

/**
 * @author Andrew_Logvinov
 */
class CategoryProductFeed {
	
	private final RepositoryItem item;

	private final String externalId;
	
	private final String name;
	
	private String categoryPageUrl;

	private String parentExternalId;
	
	public CategoryProductFeed(
			RepositoryItem itm,
			String externalId,
			String name) {
		
		this.item = itm;		
		this.externalId = externalId;
		this.name = name;
	}
	

	/**
	 * @return the parentExternalId
	 */
	public String getParentExternalId() {
		return parentExternalId;
	}

	/**
	 * @param parentExternalId the parentExternalId to set
	 */
	public void setParentExternalId(String parentExternalId) {
		this.parentExternalId = parentExternalId;
	}

	/**
	 * @return the externalId
	 */
	public String getExternalId() {
		return externalId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the categoryPageUrl
	 */
	public String getCategoryPageUrl() {
		return categoryPageUrl;
	}


	public void setCategoryPageUrl(String categoryPageUrl) {
		this.categoryPageUrl = categoryPageUrl;
	}


	/**
	 * @return the item
	 */
	public RepositoryItem getItem() {
		return item;
	}
}
