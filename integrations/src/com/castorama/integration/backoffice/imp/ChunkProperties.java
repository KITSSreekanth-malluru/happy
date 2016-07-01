package com.castorama.integration.backoffice.imp;

public class ChunkProperties {
	private String skuFileName;
	private String priceFileName;
	private String productFileName;
	private String inventoryFileName;
	private int countItems;
	private boolean onlyInventory = false;;
	
	public ChunkProperties(String skuFileName) {
		this.skuFileName = skuFileName;
	}
	
	public String getSkuFileName() {
		return skuFileName;
	}
	public void setSkuFileName(String skuFileName) {
		this.skuFileName = skuFileName;
	}
	public String getPriceFileName() {
		return priceFileName;
	}
	public void setPriceFileName(String priceFileName) {
		this.priceFileName = priceFileName;
	}
	public String getProductFileName() {
		return productFileName;
	}
	public void setProductFileName(String productFileName) {
		this.productFileName = productFileName;
	}
	public String getInventoryFileName() {
		return inventoryFileName;
	}
	public void setInventoryFileName(String inventoryFileName) {
		this.inventoryFileName = inventoryFileName;
	}
	public int getCountItems() {
		return countItems;
	}
	public void setCountItems(int countItems) {
		this.countItems = countItems;
	}

	public boolean isOnlyInventory() {
		return onlyInventory;
	}

	public void setOnlyInventory(boolean onlyInventory) {
		this.onlyInventory = onlyInventory;
	}

}
