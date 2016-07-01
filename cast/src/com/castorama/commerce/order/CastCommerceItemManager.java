package com.castorama.commerce.order;

import atg.beans.PropertyNotFoundException;

import atg.commerce.CommerceException;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemManager;

import atg.commerce.pricing.ItemPriceInfo;

import com.castorama.commerce.pricing.CopySkuInfoToCommerceItem;

/**
 * ToDo: DOCUMENT ME!
 * 
 * @author EPAM team
 * @version ToDo: INSERT VERSION NUMBER
 */
public class CastCommerceItemManager extends CommerceItemManager {

	@Override
	public CommerceItem createCommerceItem(String pItemType, String pCatalogRefId, Object pCatalogRef,
			String pProductId, Object pProductRef, long pQuantity, String pCatalogKey, String pCatalogId,
			ItemPriceInfo pPriceInfo) throws CommerceException {
		CommerceItem ci = super.createCommerceItem(pItemType, pCatalogRefId, pCatalogRef, pProductId, pProductRef,
				pQuantity, pCatalogKey, pCatalogId, pPriceInfo);

		try {
			CopySkuInfoToCommerceItem.copySkuInfoToCommerceItem(ci);
		} catch (Exception e) {
			if (isLoggingError()) {
				logError(e.getMessage());
			}
		}

		return ci;
	}
}
