package com.castorama.integration.order.exp;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.service.scheduler.Schedulable;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;

import com.castorama.commerce.profile.Constants;
import com.castorama.constantes.CastoConstantes;
import com.castorama.constantes.CastoConstantesCommande;
import com.castorama.constantes.CastoConstantesOrders;

public class ExportOrderManager extends GenericService implements Schedulable{
	
	private static final String CC_ORDERS_QUERY = "deliveryType = ?0 AND submittedDate >= ?1 AND submittedDate < ?2";
	private static final String GET_SHIPPED_ITEMS_QUERY = "CVENTE = ?0 AND TFACTURE_AVOIR_C400 = 1";
	private static final String CC_DELIVERY_TYPE = "clickAndCollect";
	private static final String STATUS_PROPERTY_BO = "tetat_commande_c602";
	private static final String CODE_ARTICLE_COMM_ITEM_PROP = "codeArticle";
	private static final String CESCLAVE_VTE_LIGNE_PROP = "cesclave";
	private static final DecimalFormat DECIMAL_PRICE_FORMATTER = new DecimalFormat("#.##");
	private static final String QTE_SAISI_PROP = "QTE_SAISI";
	private static final String FILENAME_EXTENSION = ".txt";
	private static final String DATE_FORMAT = "yyyy_MM_dd";
	private static final String M_PV_SAISI_LIGNE_PROP = "M_PV_SAISI";

	private String separator;
	private Repository orderRepositoryBO;
	private Repository orderRepository;
	private Repository profileRepository;
	private ExportOrderTools exportOrderTools;
	private List<String> statusesToExport = new ArrayList<String>();
	private Integer daysAgoForCC;
	private Integer daysAgoForWeb;
	private Integer mostExpensiveNumber;
	private String folderName;
	private String fileNamePrefix;
	private boolean enabled;
	
	//private boolean skipUnshippedProducts;

	@Override
	public void performScheduledTask(Scheduler arg0, ScheduledJob arg1) {
		exportOrders();
	}
	
	public void exportOrders() {
		if(isEnabled()) {
			if(isLoggingInfo()) {
				logInfo("Start exporting CC and Web orders... ");
			}
			
			exportOrderTools = new ExportOrderTools();
			List<OrderInfoToExport> infoToExport = processOrders();
			writeToFile(infoToExport);
				
			if(isLoggingInfo()) {
				logInfo("Export was finished");
			}
		}
		
	}
	
	private List<OrderInfoToExport> processOrders() {
		Map<String, List<String>> mostExpProdByUser = new HashMap<String, List<String>>();
		Map<String, Map<String, Double>> skusAmountByProfile = getMergedSkusAmountByProfile();
		mostExpProdByUser = selectTopSkusByProfile(skusAmountByProfile);
		
		List<OrderInfoToExport> resultList = fillOrderInfoToExport(mostExpProdByUser, true);
		if(isLoggingDebug()) {
			logDebug("processing orders is completed.");
		}
		
		return resultList;
	}
	
	private Map<String, List<String>> selectTopSkusByProfile(Map<String, Map<String, Double>> skusAmountByProfile) {
		Map<String, List<String>> topSkusByProfile = new HashMap<String, List<String>>();
		String skuIdMask = "Casto000000";
		int maskLength = skuIdMask.length();
		String codeArticle = null;
		for(String profileId : skusAmountByProfile.keySet()) {
			List<StrDoublePair> sortedPairs = exportOrderTools.convertToPairsList(skusAmountByProfile.get(profileId));
			List<String> topSkus = new ArrayList<String>();
			int iterationNumber = sortedPairs.size() > mostExpensiveNumber ? mostExpensiveNumber : sortedPairs.size();
			for(int i = 0; i < iterationNumber; i++) {
				codeArticle = sortedPairs.get(i).getKey();
				topSkus.add(skuIdMask.substring(0, maskLength - codeArticle.length()) + codeArticle);
			}
			
			topSkusByProfile.put(profileId, topSkus);
		}
		
		return topSkusByProfile;
	}
	
	private Map<String, Map<String, Double>> getMergedSkusAmountByProfile() {
		Map<String, Map<String, Double>> skusAmountByProfileCC = processCCOrders();
		Map<String, Map<String, Double>> skusAmountByProfileWEB = processWebOrders();
		Map<String, Map<String, Double>> sortedAndMergedSkus = new HashMap<String, Map<String,Double>>(skusAmountByProfileWEB);
		for(String profileId : skusAmountByProfileCC.keySet()) {
			if(sortedAndMergedSkus.containsKey(profileId)) {
				Map<String, Double> skusAmountPairs = skusAmountByProfileCC.get(profileId);
				Map<String, Double> resultSkusAmountPair = sortedAndMergedSkus.get(profileId);
				for(String codeArticle : skusAmountPairs.keySet()) {
					if(resultSkusAmountPair.containsKey(codeArticle)) {
						Double newAmount = resultSkusAmountPair.get(codeArticle) + skusAmountPairs.get(codeArticle);
						resultSkusAmountPair.put(codeArticle, newAmount);
					} else {
						resultSkusAmountPair.put(codeArticle, skusAmountPairs.get(codeArticle));
					}
				}
			} else {
				sortedAndMergedSkus.put(profileId, skusAmountByProfileCC.get(profileId));
			}
		}
				
		return sortedAndMergedSkus;
	}
	
	/**
	 * 
	 * @return pairs codearticle:amount for each profileId for CC orders
	 */
	private Map<String, Map<String, Double>> processCCOrders() {
		Set<RepositoryItem> ccOrders = new HashSet<RepositoryItem> (getCCOrders());
		Map<String, List<RepositoryItem>> ccCommItems = exportOrderTools.groupCommItemsByUser(ccOrders);
		Map<String, Map<String, Double>> skusAmountByProfileCC = calculateAmountOfSkus(ccCommItems);
		return skusAmountByProfileCC;
	}
	
	/**
	 * 
	 * @return pairs codearticle:amount for each profileId for Web orders
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Map<String, Double>> processWebOrders() {
		Map<String, Set<RepositoryItem>> ordersDeliveries = selectDeliveriesByOrder();
		Map<String, Map<String, Double>> deliveredSkusByProfile = new HashMap<String, Map<String,Double>>();
		if(ordersDeliveries.isEmpty()) {
			if(isLoggingWarning()){
				logWarning("There were no deliveries(OLs) to export");
			}
			return deliveredSkusByProfile;
		}
		Set<String> orderIds = ordersDeliveries.keySet();
		try {
			RepositoryItem[] foOrders = getOrderRepository().
					getItems((String[]) orderIds.toArray(new String[orderIds.size()]), CastoConstantes.ORDER_ITEM_NAME);
			
			for(RepositoryItem order : foOrders) {
				String profileId = (String) order.getPropertyValue(CastoConstantes.ORDER_PROPERTY_PROFILE_ID);
				if(deliveredSkusByProfile.get(profileId) == null) {
					deliveredSkusByProfile.put(profileId, new HashMap<String, Double>());
				}
				List<RepositoryItem> orderedAndShippedItems = new ArrayList<RepositoryItem>();
				Map<RepositoryItem, Double> exceededNumberOfShippedItems = new HashMap<RepositoryItem, Double>();
				Map<String, Double> skusWithSumm = deliveredSkusByProfile.get(profileId);
				Set<RepositoryItem> deliveries = ordersDeliveries.get(order.getRepositoryId());
				List<RepositoryItem> commItems = (List<RepositoryItem>) order.
						getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_COMMERCE_ITEMS);
				for(RepositoryItem commItem : commItems) {
					String codeArticle = String.valueOf(commItem.getPropertyValue(CODE_ARTICLE_COMM_ITEM_PROP));
					for(RepositoryItem delivery : deliveries) {
						String cesclave = String.valueOf(delivery.getPropertyValue(CESCLAVE_VTE_LIGNE_PROP));
						if(codeArticle != null && cesclave != null && codeArticle.equals(cesclave)) {
							orderedAndShippedItems.add(delivery);
							Float quantity = (Float) delivery.getPropertyValue(QTE_SAISI_PROP);
							if(quantity != 0) { // skip calculation if zero quantity
								Double deliveredAmount = recalculateAmount(commItem, quantity.doubleValue());
								Double exceededNumber = getExceededNumberOfItems(commItem, quantity.doubleValue());
								if(exceededNumber > 0) {
									exceededNumberOfShippedItems.put(delivery, exceededNumber);
								}
								summarizeAmountBySku(skusWithSumm, cesclave, deliveredAmount);
							}
						}
					}
				}
				// code below is to check whether the number of delivered items was more than 
				// the number of ordered (casto-pack case) and to check whether there were no ordered items, 
				// but they were delivered (casto-pack case)
				Double amount = 0d;
				for(RepositoryItem delivery : deliveries) {
					if(orderedAndShippedItems.contains(delivery)) {
						// case 1: number of delivered items is more than ordered by user
						if(exceededNumberOfShippedItems.containsKey(delivery)) {
							amount = calculateExceededAmount(delivery, exceededNumberOfShippedItems.get(delivery));
							summarizeAmountBySku(skusWithSumm, delivery, amount);
						}
					} else {
						amount = calculateExceededAmount(delivery);
						summarizeAmountBySku(skusWithSumm, delivery, amount);
					}
				}
				
			}	
			
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		
		return deliveredSkusByProfile;
	}
	
	private Double calculateExceededAmount(RepositoryItem delivery) {
		Float quantity = (Float) delivery.getPropertyValue(QTE_SAISI_PROP);
		return calculateExceededAmount(delivery, quantity.doubleValue());
	}
	
	private Double calculateExceededAmount(RepositoryItem delivery, Double quantity) {
		Float price = (Float) delivery.getPropertyValue(M_PV_SAISI_LIGNE_PROP);
		return price.doubleValue() * quantity;
	}
	
	private void summarizeAmountBySku(Map<String, Double> skusWithSumm, RepositoryItem delivery, Double deliveredAmount) {
		String cesclave = String.valueOf(delivery.getPropertyValue(CESCLAVE_VTE_LIGNE_PROP));
		summarizeAmountBySku(skusWithSumm, cesclave, deliveredAmount);
	}
	
	private void summarizeAmountBySku(Map<String, Double> skusWithSumm, String cesclave, Double deliveredAmount) {
		if(deliveredAmount != 0) {
			if(!skusWithSumm.containsKey(cesclave)) {
				skusWithSumm.put(cesclave, deliveredAmount);
			} else {
				skusWithSumm.put(cesclave, skusWithSumm.get(cesclave) + deliveredAmount);
			}
		}
	}
	
	private void writeToFile(List<OrderInfoToExport> exportInfo) {
		try {
			File file = new File(getFolderName(), getFilename());
			ObjectStringWriter writer = new ObjectStringWriter(file);
			writer.write(exportInfo);
			writer.closeWriter();
			if(isLoggingDebug()) {
				logDebug("Data was successfully exported to " + file.getAbsolutePath());
			}
		} catch (Exception e) {
			if(isLoggingError()) {
				logError("Error while writing order's info to file ", e);
			}
		}
	}
	
	private List<OrderInfoToExport> fillOrderInfoToExport(Map<String, List<String>> mostExpProdByUser, boolean fillWithEmptyStr) {
		List<OrderInfoToExport> ordersToExport = new ArrayList<OrderInfoToExport>();
		String emptyStr = "";
		if(mostExpProdByUser == null || mostExpProdByUser.isEmpty()) {
			return ordersToExport;
		}
		try {
			for(String profileId : mostExpProdByUser.keySet()) {
				RepositoryItem user = getProfileRepository().getItem(profileId, Constants.DESCRIPTOR_NAME_PROFILE);
				//double check whether profile with particular id exists
				if(user != null) {
					String email = (String) user.getPropertyValue(Constants.LOGIN_PROFILE_PROP);
					String name = (String) user.getPropertyValue(Constants.FIRST_NAME_PROFILE_PROP);
					String lastName = (String) user.getPropertyValue(Constants.LAST_NAME_PROFILE_PROP);
					OrderInfoToExport orderInfo = new OrderInfoToExport(email, name, lastName, separator);
					orderInfo.setProdReferences(mostExpProdByUser.get(profileId));
					if(fillWithEmptyStr && orderInfo.getProdReferences().size() < getMostExpensiveNumber()) {
						for(int i = orderInfo.getProdReferences().size(); i < getMostExpensiveNumber(); i++) {
							orderInfo.getProdReferences().add(emptyStr);
						}
					}
					ordersToExport.add(orderInfo);
				} else if(isLoggingWarning()) {
					logWarning("No user found by id: " + profileId);
				}
			}
		} catch (RepositoryException e) {
			if(isLoggingError()) {
				logError("Error while extracting profile info from repository ", e);
			}
		}
		
		return ordersToExport;
	}

	/**
	 * 
	 * @return list of merged CC and Web orders
	 */
	private Set<RepositoryItem> getMergedCCandWebOrders() {
		List<RepositoryItem> webOrders = getWebOrders();
		List<RepositoryItem> ccOrders = getCCOrders();
		Set<RepositoryItem> mergedOrders = new HashSet<RepositoryItem>();
		if(webOrders != null) {
			mergedOrders.addAll(webOrders);
		}
		if(ccOrders != null) {
			mergedOrders.addAll(ccOrders);
		}
		return mergedOrders;
	}
	
	private List<RepositoryItem> getCCOrders() {
		List<RepositoryItem> ccOrders = null;
		Calendar fromTime = getMidnightTime(daysAgoForCC);
		Calendar toTime = getMidnightTime(daysAgoForCC - 1);
		// note that minus is placed before 2nd argument
		//fromTime.add(Calendar.DAY_OF_YEAR, -daysAgoForCC);
		try {
			RepositoryView foView = getOrderRepository().getView(CastoConstantes.ORDER_ITEM_NAME);
			Object[] params = new Object[]{CC_DELIVERY_TYPE, new Date(fromTime.getTimeInMillis()), new Date(toTime.getTimeInMillis())};
			RqlStatement statement = RqlStatement.parseRqlStatement(CC_ORDERS_QUERY);
			RepositoryItem[] orders = statement.executeQuery(foView, params);
			if(orders != null) {
				ccOrders = Arrays.asList(orders);
			} else if (isLoggingWarning()) {
				logWarning("There were no CC orders to export");
			}
		} catch (RepositoryException e) {
			if(isLoggingError()) {
				logError("Error while extracting CC orders", e);
			}
		}
		
		return ccOrders == null ? new ArrayList<RepositoryItem>() : ccOrders;
	}
	
	private Calendar getMidnightTime(Integer daysAgo) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -daysAgo);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}
	
	/**
	 * 
	 * @return list of web_order
	 */
	
	private List<RepositoryItem> getWebOrders() {
		List<RepositoryItem> orderList = null;
		Set<String> orderIds = getOrderIdsFromBO();
		try {
			if(orderIds != null && orderIds.size() != 0) {
				RepositoryItem[] orders = getOrderRepository().getItems(orderIds.toArray(new String[orderIds.size()]), CastoConstantes.ORDER_ITEM_NAME);
				orderList = Arrays.asList(orders);
			}
		} catch (RepositoryException e) {
			if(isLoggingError()) {
				logError("Error during accessing OrderRepository", e);
			}
		}
		
		return orderList == null ? new ArrayList<RepositoryItem>() : orderList;
	}
	
	/**
	 * 
	 * @return map where key - FO order_id, value - list of sku_id of shipped skus
	 */
	private Map<String, List<String>> getOrderShippedSkus() {
		Map<String, List<String>> orderShippedSkus = new HashMap<String, List<String>>();
		try {
			RepositoryView boView = getOrderRepositoryBO().getView(CastoConstantesOrders.BO_SCD_VENTE_LIGNE);
			RepositoryItem[] ordersBO = getBoOrders();
			RqlStatement shippedItemsStmt = RqlStatement.parseRqlStatement(GET_SHIPPED_ITEMS_QUERY);
			if(ordersBO != null && ordersBO.length > 0) {
				for(RepositoryItem orderBo : ordersBO) {
					if(((Short) orderBo.getPropertyValue(STATUS_PROPERTY_BO)) == 10) {
						RepositoryItem[] shippedItems = shippedItemsStmt.executeQuery(boView, new Object[]{orderBo.getRepositoryId()});
						if(shippedItems != null) {
							String key = (String) orderBo.getPropertyValue(CastoConstantesOrders.NCOMMANDE_CLIENT);
							orderShippedSkus.put(key, extractShippedSkuIds(shippedItems));
						}
					}
				}
			}
		} catch (RepositoryException e) {
			if(isLoggingError()) {
				logError("Error while extracting info from BO_SCD_VENTE_LIGNE", e);
			}
		}		
		
		return orderShippedSkus;
	}
	
	private List<String> extractShippedSkuIds(RepositoryItem[] shippedItems) {
		List<String> skuIds = new ArrayList<String>();
		for(RepositoryItem sku : shippedItems) {
			String codeArticle = (String) sku.getPropertyValue("CESCLAVE");
			skuIds.add("Casto" + codeArticle);
		}
		
		return skuIds;
	}
	
	/**
	 * 
	 * @return set of order_id of orders, which were shipped to clients
	 */
	private Set<String> getOrderIdsFromBO() {
		Set<String> orderIds = new HashSet<String>();
		RepositoryItem[] ordersBO = getBoOrders();
		if((ordersBO == null || ordersBO.length == 0) && isLoggingWarning()) {
			logWarning("BO repository does not contain orders to export for the last " + daysAgoForCC + "days");
		} else {
			for(RepositoryItem orderBO : ordersBO) {
				orderIds.add((String) orderBO.getPropertyValue(CastoConstantesOrders.NCOMMANDE_CLIENT));
			}
		}	
		
		return orderIds;
	}
	
	private RepositoryItem[] getBoOrders() {
		RepositoryItem[] ordersBO = null;
		try {
			RepositoryView boView = getOrderRepositoryBO().getView(CastoConstantesOrders.BO_ORDER_VIEW_NAME);		
			Query resultQuery = buildResultQueryForBO(boView.getQueryBuilder());
			ordersBO = boView.executeQuery(resultQuery);
		} catch (RepositoryException e) {
			if(isLoggingError()) {
				logError("Error during rql-query executing on BOGSARepository:", e);
			}
		}
		return ordersBO;
	}
	
	private Map<String, Map<String, Double>> calculateAmountOfSkus(Map<String, List<RepositoryItem>> commItemsByProfile) {
		Map<String, Map<String, Double>> topSkusByProfile = new HashMap<String, Map<String,Double>>();
		for(String profileId : commItemsByProfile.keySet()) {
			Map<String, Double> skuAmountPair =  new HashMap<String, Double>();
			for(RepositoryItem commItem : commItemsByProfile.get(profileId)) {
				try {
					String codeArticle = String.valueOf(commItem.getPropertyValue(CODE_ARTICLE_COMM_ITEM_PROP));
					Double amount = (Double) DynamicBeans.getSubPropertyValue(commItem, ExportOrderTools.AMOUNT_SUB_PROPERTY);
					if(!skuAmountPair.containsKey(codeArticle)) {
						skuAmountPair.put(codeArticle, amount);
					} else {
						skuAmountPair.put(codeArticle, skuAmountPair.get(codeArticle) + amount);
					}
				} catch (PropertyNotFoundException e) {
					e.printStackTrace();
				}
			}
			topSkusByProfile.put(profileId, skuAmountPair);
		}
		
		return topSkusByProfile;
	}
	
	/**
	 * 
	 * @return map where key - FO-oderId, value - Set of all deliveries of order
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Set<RepositoryItem>> selectDeliveriesByOrder() {
		Map<String, Set<RepositoryItem>> orderDeliveries = new HashMap<String, Set<RepositoryItem>>();
		try {
			RepositoryView boViewDelivery = getOrderRepositoryBO().getView(CastoConstantesOrders.BO_DELIVERIES_ITEM_NAME);
			Query delivQuery = buildQueryForDeliveries(boViewDelivery.getQueryBuilder());
			RepositoryItem[] deliveries = boViewDelivery.executeQuery(delivQuery);
			//select all deliveries for particular FO-orderId
			if(deliveries != null) {
				for(RepositoryItem delivery : deliveries) {
					//select BoOrder for extracting FO-orderId
					RepositoryItem boOrder = getOrderRepositoryBO().getItem(String.valueOf(delivery.getPropertyValue("CVENTE")), CastoConstantesOrders.BO_ORDER_VIEW_NAME);
					String foOrderId = (String) boOrder.getPropertyValue(CastoConstantesOrders.NCOMMANDE_CLIENT);
					
					//select delivered items from delivery
					Set<RepositoryItem> deliveryItems = (Set<RepositoryItem>) delivery.getPropertyValue(CastoConstantesCommande.BO_ORDER_PROPERTY_VTE_LIGNES);
					if(!orderDeliveries.containsKey(foOrderId)) {
						orderDeliveries.put(foOrderId, new HashSet<RepositoryItem>());
					}
					orderDeliveries.get(foOrderId).addAll(deliveryItems);
				}			
			}
		} catch (RepositoryException e) {
			if(isLoggingError()) {
				logError("Error during extracting deliveries from BO:", e);
			}
		}

		return orderDeliveries;
	}
	
	private Double recalculateAmount(RepositoryItem commerceItem, Double deliveredNumber) { 
		Double finalAmount = 0d;
		try {
			Double amount = (Double) DynamicBeans.getSubPropertyValue(commerceItem, ExportOrderTools.AMOUNT_SUB_PROPERTY);
			Long number = (Long) commerceItem.getPropertyValue(CastoConstantesOrders.CITEM_PROPERTY_QUANTITY);
			if(!(deliveredNumber.longValue() == number) && (amount != 0 && number != 0)) {
				if(deliveredNumber > number) {
					return amount;
				} else {
					Double part = deliveredNumber/number;
					finalAmount = Double.valueOf(DECIMAL_PRICE_FORMATTER.format(amount*part));
				}
			} else {
				return amount;
			}
		} catch (PropertyNotFoundException e) {
			e.printStackTrace();
		}
		return finalAmount;
	}
	
	private Double getExceededNumberOfItems(RepositoryItem commerceItem, Double deliveredNumber) {
		Long orderedNumber = (Long) commerceItem.getPropertyValue(CastoConstantesOrders.CITEM_PROPERTY_QUANTITY);
		return deliveredNumber - orderedNumber;
	}
	
	private Query buildResultQueryForBO(QueryBuilder qb) {
		Calendar curTime = Calendar.getInstance();
		// note that minus is placed before 2nd param
		curTime.add(Calendar.DAY_OF_YEAR, -daysAgoForCC);
		Query resultQuery = null;
		try {
			List<Query> queries = new ArrayList<Query>();
			QueryExpression dateExpedition;
			dateExpedition = qb.createPropertyQueryExpression("dexpedition");
			QueryExpression dateExpedVal = qb.createConstantQueryExpression(new Date(curTime.getTimeInMillis()));
			Query firstPart = qb.createComparisonQuery(dateExpedition, dateExpedVal, QueryBuilder.GREATER_THAN);
			
			QueryExpression tetat_commande = qb.createPropertyQueryExpression(STATUS_PROPERTY_BO);
			for(String status : statusesToExport) {
				QueryExpression statusExpr = qb.createConstantQueryExpression(Short.parseShort(status));
				queries.add(qb.createComparisonQuery(tetat_commande, statusExpr, QueryBuilder.EQUALS));
			}
			Query secondPart = qb.createOrQuery(queries.toArray(new Query[queries.size()]));
			resultQuery = qb.createAndQuery(new Query[]{firstPart, secondPart});
		} catch (RepositoryException e) {
			if(isLoggingError()) {
				logError("Error during rql-query building:", e);
			}
		}
		
		return resultQuery;
	}
	
	private Query buildQueryForDeliveries(QueryBuilder qb) {
		Calendar fromTime = getMidnightTime(daysAgoForWeb);
		Calendar toTime = getMidnightTime(daysAgoForWeb -1);
		// note that minus is placed before 2nd param
		Query resultQuery = null;
		try {
			List<Query> queries = new ArrayList<Query>();
			QueryExpression dateExpedition;
			dateExpedition = qb.createPropertyQueryExpression("DATE_EXPEDITION_REELLE");
			QueryExpression dateExpedValFrom = qb.createConstantQueryExpression(new Date(fromTime.getTimeInMillis()));
			QueryExpression dateExpedValTo = qb.createConstantQueryExpression(new Date(toTime.getTimeInMillis()));
			Query firstPartFrom = qb.createComparisonQuery(dateExpedition, dateExpedValFrom, QueryBuilder.GREATER_THAN_OR_EQUALS);
			Query firstPartTo = qb.createComparisonQuery(dateExpedition, dateExpedValTo, QueryBuilder.LESS_THAN);
			
			Query firstPart = qb.createAndQuery(new Query[]{firstPartFrom, firstPartTo});
			
			QueryExpression tetat_commande = qb.createPropertyQueryExpression("CETAT_OL_C651");
			for(String status : statusesToExport) {
				QueryExpression statusExpr = qb.createConstantQueryExpression(Short.parseShort(status));
				queries.add(qb.createComparisonQuery(tetat_commande, statusExpr, QueryBuilder.EQUALS));
			}
			Query secondPart = qb.createOrQuery(queries.toArray(new Query[queries.size()]));
			resultQuery = qb.createAndQuery(new Query[]{firstPart, secondPart});
		} catch (RepositoryException e) {
			if(isLoggingError()) {
				logError("Error during rql-query building:", e);
			}
		}
		
		return resultQuery;
	}
	
	private String getFilename() {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		String postfix = sdf.format(Calendar.getInstance().getTime());
		StringBuilder sb = new StringBuilder(getFileNamePrefix());
		sb.append(postfix).append(FILENAME_EXTENSION);
		return sb.toString();
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public List<String> getStatusesToExport() {
		return statusesToExport;
	}

	public void setStatusesToExport(List<String> statusesToExport) {
		this.statusesToExport = statusesToExport;
	}

	public Repository getOrderRepositoryBO() {
		return orderRepositoryBO;
	}

	public void setOrderRepositoryBO(Repository orderRepositoryBO) {
		this.orderRepositoryBO = orderRepositoryBO;
	}

	public Repository getOrderRepository() {
		return orderRepository;
	}

	public void setOrderRepository(Repository orderRepository) {
		this.orderRepository = orderRepository;
	}

	public Repository getProfileRepository() {
		return profileRepository;
	}

	public void setProfileRepository(Repository profileRepository) {
		this.profileRepository = profileRepository;
	}

	public Integer getMostExpensiveNumber() {
		return mostExpensiveNumber;
	}

	public void setMostExpensiveNumber(Integer mostExpensiveNumber) {
		this.mostExpensiveNumber = mostExpensiveNumber;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	private Scheduler scheduler;
	private Schedule schedule;
    private int jobId;

	public Scheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}
	
	@Override
    public void doStartService() throws ServiceException {
        ScheduledJob job = new ScheduledJob(getClass().getSimpleName(),
                "Export CC and Web orders",
                getAbsoluteName(),
                getSchedule(),
                this,
                ScheduledJob.SCHEDULER_THREAD);
        jobId = getScheduler().addScheduledJob(job);

        super.doStartService();
    }

    @Override
    public void doStopService() throws ServiceException {
        if (jobId > 0) {
            getScheduler().removeScheduledJob(jobId);
        }

        super.doStopService();
    }

	public int getJobId() {
		return jobId;
	}

	public void setJobId(int jobId) {
		this.jobId = jobId;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/*public boolean isSkipUnshippedProducts() {
		return skipUnshippedProducts;
	}

	public void setSkipUnshippedProducts(boolean skipUnshippedProducts) {
		this.skipUnshippedProducts = skipUnshippedProducts;
	}*/

	public Integer getDaysAgoForCC() {
		return daysAgoForCC;
	}

	public void setDaysAgoForCC(Integer daysAgoForCC) {
		this.daysAgoForCC = daysAgoForCC;
	}

	public Integer getDaysAgoForWeb() {
		return daysAgoForWeb;
	}

	public void setDaysAgoForWeb(Integer daysAgoForWeb) {
		this.daysAgoForWeb = daysAgoForWeb;
	}

	public String getFileNamePrefix() {
		return fileNamePrefix;
	}

	public void setFileNamePrefix(String fileNamePrefix) {
		this.fileNamePrefix = fileNamePrefix;
	}
}