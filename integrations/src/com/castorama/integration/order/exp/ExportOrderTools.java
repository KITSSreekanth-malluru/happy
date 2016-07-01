package com.castorama.integration.order.exp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.castorama.constantes.CastoConstantes;
import com.castorama.constantes.CastoConstantesOrders;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.nucleus.GenericService;
import atg.repository.RepositoryItem;

public class ExportOrderTools extends GenericService{
	
	public static final String CATALOG_REF_IR_PROP = "catalogRefId";
	public static final String AMOUNT_SUB_PROPERTY = "priceInfo.amount";
	public static final String PRODUCT_ID_PROPERTY = "productId";

	/**
	 * summ amount from all user's commerceItems and select top 'mostExpensiveNumber'
	 * 
	 * @param commerceItems
	 * @param mostExpnumber - number of most expensive product among commerceItems
	 * @return list of most popular sku_id
	 */
	public List<String> selectTopItemsByPrice(List<RepositoryItem> commerceItems, Integer mostExpnumber) {
		Map<String, Double> productsWithAmounts = new HashMap<String, Double>();
		try {
			for(RepositoryItem item : commerceItems) {
				String skuId = (String) item.getPropertyValue(CATALOG_REF_IR_PROP);
				Double amount = (Double) DynamicBeans.getSubPropertyValue(item, AMOUNT_SUB_PROPERTY);
				if(productsWithAmounts.get(skuId) == null) {
					productsWithAmounts.put(skuId, amount);
				} else {
					Double newAmount = productsWithAmounts.get(skuId) + amount;
					productsWithAmounts.put(skuId, newAmount);
				}
			}
		} catch (PropertyNotFoundException e) {
			if(isLoggingError()) {
				logError("Error while calculating most expensive products: ", e);
			}
		}
		
		List<StrDoublePair> sortedList = convertToPairsList(productsWithAmounts);
		List<String> popularProductsIds = new ArrayList<String>();
		for(StrDoublePair pair : sortedList) {
			popularProductsIds.add(pair.getKey());
		}
		
		if(popularProductsIds.size() <= mostExpnumber) {
			return popularProductsIds;
		}
		return popularProductsIds.subList(0, mostExpnumber);
		
	}
		
	List<StrDoublePair> convertToPairsList(Map<String, Double> productsWithAmounts) {
		List<StrDoublePair> sortedPairs = new ArrayList<StrDoublePair>();
	    for(String key : productsWithAmounts.keySet()) {
	    	sortedPairs.add(new StrDoublePair(key, productsWithAmounts.get(key)));
	    }
	    Collections.sort(sortedPairs);
	    
	    return sortedPairs;
	}
	
	/**
	 * method retrieves map where key - user_id, value - list of commerceItems 
	 * from all orders related to the particular uses 
	 * 
	 * @param orders - orders to extract all commerce items associated with user (by profileId)
	 * @return all commerce items associated with user
	 */
	@SuppressWarnings("unchecked")
	public Map<String, List<RepositoryItem>> groupCommItemsByUser(Set<RepositoryItem> orders) {
		Map<String, List<RepositoryItem>> ordersGroupedByUser = new HashMap<String, List<RepositoryItem>>();
		for(RepositoryItem order : orders) {
			String profileId = (String) order.getPropertyValue(CastoConstantes.ORDER_PROPERTY_PROFILE_ID);
			if(ordersGroupedByUser.get(profileId) == null) {
				ordersGroupedByUser.put(profileId, new ArrayList<RepositoryItem>());
			}
			List<RepositoryItem> commerceItems = (List<RepositoryItem>) order.getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_COMMERCE_ITEMS);
			ordersGroupedByUser.get(profileId).addAll(commerceItems);
		}
		
		return ordersGroupedByUser;
	}
	
	/**
	 * 
	 * @param orders - orders to extract all commerce items associated with user (by profileId) 
	 * @param ordersCIs - commerce item to add to result list of commerce items associated with user
	 * @return all commerce items, which were shipped to user, associated with user
	 */
	public Map<String, List<RepositoryItem>> groupCommItemsByUser(Set<RepositoryItem> orders, Map<String, List<RepositoryItem>> ordersCIs) {
		Map<String, List<RepositoryItem>> cisGroupedByUser = groupCommItemsByUser(orders);
		if(ordersCIs != null && ordersCIs.size() != 0) {
			for(String profileId : ordersCIs.keySet()) {
				if(cisGroupedByUser.get(profileId) == null) {
					cisGroupedByUser.put(profileId, ordersCIs.get(profileId));
				} else {
					cisGroupedByUser.get(profileId).addAll(ordersCIs.get(profileId));
				}
			}
		}
		
		return cisGroupedByUser;
	}
	
	/**
	 * Method filters commerce items of from orders. 
	 * 
	 * @param webOrders
	 * @param shippedOrdersSkuIds - map, where key - order_id, value - skus from partially shipped order, 
	 * which were shipped to user 
	 * @return Map where key - profileId, value - list of commerce items, which were shipped to user
	 */
	@SuppressWarnings("unchecked")
	public Map<String, List<RepositoryItem>> getFilteredCommItems(List<RepositoryItem> webOrders, Map<String, List<String>> shippedOrdersSkuIds) {
		Map<String, List<RepositoryItem>> shippedCI = new HashMap<String, List<RepositoryItem>>();
		if(webOrders != null && webOrders.size() > 0) {
			for(RepositoryItem webOrder : webOrders) {
				List<RepositoryItem> orderCIs = (List<RepositoryItem>) webOrder.getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_COMMERCE_ITEMS);
				String profileId = (String) webOrder.getPropertyValue(CastoConstantes.ORDER_PROPERTY_PROFILE_ID);
				if(orderCIs != null) {
					if(shippedCI.get(profileId) == null) {
						shippedCI.put(profileId, new ArrayList<RepositoryItem>());
					}
					if(shippedOrdersSkuIds.containsKey(webOrder.getRepositoryId())) {
						for(RepositoryItem ci : orderCIs) {
							String skuId = (String) ci.getPropertyValue(CATALOG_REF_IR_PROP);
							if(shippedOrdersSkuIds.get(webOrder.getRepositoryId()).contains(skuId)) {
								shippedCI.get(profileId).add(ci);
							}
						}
					} else {
						shippedCI.get(profileId).addAll(orderCIs);
					}
				}
			}
		}		
		
		return shippedCI;
	}
}

class StrDoublePair implements Comparable<StrDoublePair>{
	
	public StrDoublePair(String key, Double value) {
		super();
		this.key = key;
		this.value = value;
	}
	private String key;
	private Double value;
	
	@Override
	public int compareTo(StrDoublePair o) {
		
		return -(this.value.compareTo(o.getValue()));
	}

	public String getKey() {
		return key;
	}

	public Double getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "Pair [key=" + key + ", value=" + value + "]";
	}
}