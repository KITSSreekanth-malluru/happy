package com.castorama.integration.bazaarvoice;

import atg.repository.RepositoryItem;

class ProductFeed {
	
	private final RepositoryItem item;
	
	private final String externalId;
	
	private final String name;
	
	private final String description;
	
	private String brand;
	
	private final String categoryExternalId;
	
	private String productPageUrl;
	
	private String imageUrl;
	
	private String manufacturerPartNumber;
	
	private String modelNumber;
	
	private String EAN;

	private String UPC;

	private String ISBN;
	
	private boolean isDocument = false;
	
	
	public ProductFeed(
			RepositoryItem item,
			String externalId, 
			String name,
			String description,
			String categoryExternalId) {
		
		this.item = item;		
		this.externalId = externalId;
		this.name = name;
		this.description = description;
		this.categoryExternalId = categoryExternalId;
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the brand
	 */
	public String getBrand() {
		return brand;
	}

	/**
	 * @param brand the brand to set
	 */
	public void setBrand(String brand) {
		this.brand = brand;
	}

	/**
	 * @return the categoryExternalId
	 */
	public String getCategoryExternalId() {
		return categoryExternalId;
	}

	/**
	 * @return the productpageUrl
	 */
	public String getProductPageUrl() {
		return productPageUrl;
	}

	/**
	 * @return the imageUrl
	 */
	public String getImageUrl() {
		return imageUrl;
	}

	/**
	 * @return the manufacturerPartNumber
	 */
	public String getManufacturerPartNumber() {
		return manufacturerPartNumber;
	}

	/**
	 * @param manufacturerPartNumber the manufacturerPartNumber to set
	 */
	public void setManufacturerPartNumber(String manufacturerPartNumber) {
		this.manufacturerPartNumber = manufacturerPartNumber;
	}

	/**
	 * @return the eAN
	 */
	public String getEAN() {
		return EAN;
	}

	/**
	 * @param ean the eAN to set
	 */
	public void setEAN(String ean) {
		EAN = ean;
	}

	/**
	 * @return the modelNumber
	 */
	public String getModelNumber() {
		return modelNumber;
	}

	/**
	 * @param modelNumber the modelNumber to set
	 */
	public void setModelNumber(String modelNumber) {
		this.modelNumber = modelNumber;
	}

	/**
	 * @return the uPC
	 */
	public String getUPC() {
		return UPC;
	}

	/**
	 * @param upc the uPC to set
	 */
	public void setUPC(String upc) {
		UPC = upc;
	}

	/**
	 * @return the iSBN
	 */
	public String getISBN() {
		return ISBN;
	}

	/**
	 * @param isbn the iSBN to set
	 */
	public void setISBN(String isbn) {
		ISBN = isbn;
	}

	public void setProductPageUrl(String productPageUrl) {
		this.productPageUrl = productPageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	/**
	 * @return the isDocument
	 */
	public boolean isDocument() {
		return isDocument;
	}

	/**
	 * @param isDocument the isDocument to set
	 */
	public void setDocument(boolean isDocument) {
		this.isDocument = isDocument;
	}

	/**
	 * @return the item
	 */
	public RepositoryItem getItem() {
		return item;
	}

}
