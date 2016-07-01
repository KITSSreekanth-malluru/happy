package com.castorama.integration.backoffice.imp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.SystemException;

import atg.adapter.gsa.GSARepository;
import atg.adapter.gsa.xml.TemplateParser;
import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemImpl;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.PaymentGroupImpl;
import atg.commerce.order.PropertyNameConstants;
import atg.commerce.order.ShippingGroupImpl;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.states.StateDefinitions;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.xml.tools.DefaultXMLToolsFactory;
import atg.xml.tools.XMLToDOMParser;
import atg.xml.tools.XMLToolsFactory;

import com.castorama.commerce.order.CastOrderManager;
import com.castorama.commerce.states.BOOrderStates;
import com.castorama.integration.Constants;
import com.castorama.integration.IntegrationBase;
import com.castorama.integration.util.MiscUtils;
import com.castorama.mail.MailSender;
import com.castorama.payment.PaymentStates;

public class MigrationOrderManager extends IntegrationBase {
	private static final String[] EMAIL_PROPS = {
		MailSender.CONFIRMATION_LATOUT,
		MailSender.CONFIRMATION_CB,
		MailSender.CONFIRMATION_CB_AND_GC,
		MailSender.RECEPTION_CHECK,
		MailSender.RECEPTION_NO_CHECK,
		MailSender.INVALIDATING_CHECK,
		//MailSender.CONFIRMATION_SHIPMENT,
		MailSender.REFUND_SUBMITED
		};
	
	private File workingDir;
	private File errorDir;
	private File archiveDir;
	
	private boolean stopMigration = false;
	
	private Repository orderRepository;
	private CastOrderManager orderManager;
	private BOOrderStates orderStates;
	
	private boolean needUpdateOrder;
	
	public boolean getNeedUpdateOrder() {
		return needUpdateOrder;
	}

	public void setNeedUpdateOrder(boolean needUpdateOrder) {
		this.needUpdateOrder = needUpdateOrder;
	}

	public Repository getOrderRepository() {
		return orderRepository;
	}

	public void setOrderRepository(Repository orderRepository) {
		this.orderRepository = orderRepository;
	}
	
	public CastOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(CastOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public BOOrderStates getOrderStates() {
		return orderStates;
	}

	public void setOrderStates(BOOrderStates orderStates) {
		this.orderStates = orderStates;
	}

	public void stopMigrationOrders() {
		stopMigration = true;
		
	}
	
	public void migrationOrders () throws FileNotFoundException {
		if (isLoggingDebug()) {
			logDebug("start - MigrationOrderManager - migrationOrders. ");
		}

		stopMigration = false;
		
		checkMandatoryFolders();
		try {
			
			workingDir = getWorkingDir();
			errorDir = getErrorDir();
			archiveDir = getDir(Constants.ARCHIVE_FOLDER);

			File[] boFiles = getUploadFiles(null);
			if (boFiles == null || boFiles.length == 0) {
				if (isLoggingDebug()) {
					logDebug("Files for upload not found.  " + getRootDir() + File.separator
							+ Constants.INPUT_FOLDER);
				}
				return;
			}

			PrintWriter logWriter = null;
			long start = System.currentTimeMillis();
			
			//List<String> boOrderIds = new ArrayList<String>();

			for (File file : boFiles) {
				File statusFile = new File(workingDir, getFileName(file) + ".log");
				logWriter = new PrintWriter(statusFile);
				logWriter.println("Import Date: " + Constants.DATE_FORMAT_LOG.format(new Date()));

				try {
					File workingFile = getWorkingFile(file);
					if (isLoggingDebug()) {
						logDebug("processing: " + workingFile);
					}
					if (workingFile == null) {
						continue;
					}

					int countSuccessOrder = 0;
					int countSuccessFOOrder = 0;
					int countFailOrder = 0;
					int countFailFOOrder = 0;
					StringBuilder listFailedOrders = new StringBuilder();
					StringBuilder listFailedFOOrders = new StringBuilder();
					try {
						List<String> tmpNames = parseFluxXml(workingFile);
						if (tmpNames == null || tmpNames.isEmpty()) {
							if (isLoggingDebug()) {
								logDebug("Temp Files not found.");
							}
							return;
						}

						for (String tmpName : tmpNames) {
							File tmpFile = new File(workingDir, tmpName);
							if (uploadXml(tmpFile)) {
								countSuccessOrder++;
								try {
									saveOrdersFO(getOrderIdFromName(tmpFile.getName()));
									countSuccessFOOrder++;
								} catch (Exception e) {
									if (isLoggingError()) {
										logError("Exception occured : " + getOrderIdFromName(tmpFile.getName()), e);
									}
									countFailFOOrder++;
									listFailedFOOrders.append(getOrderIdFromName(tmpFile.getName())).append("\n");
								}
								
								//boOrderIds.add(getOrderIdFromName(tmpFile.getName()));
							} else {
								countFailOrder++;
								listFailedOrders.append(getOrderIdFromName(tmpFile.getName())).append("\n");
							}
							tmpFile.delete();
							
							if (stopMigration) {
								break;
							}
						}


						((GSARepository)getOrderRepository()).invalidateCaches();
						((GSARepository)getRepository()).invalidateCaches();
						
						String status = countFailOrder > 0 ? "Failed" : "Success";
						logWriter.println("Status: " + status);
						logWriter.println("Back-Office order repository:");
						logWriter.println("Number of orders imported successfully: " + countSuccessOrder);
						logWriter.println("Number of failed orders: " + countFailOrder);
						if (countFailOrder > 0) {
							logWriter.println("List of failed orders:");
							logWriter.println(listFailedOrders.toString());
						}
						logWriter.println();
						logWriter.println("ATG order repository:");
						logWriter.println("Number of orders imported successfully: " + countSuccessFOOrder);
						logWriter.println("Number of failed orders: " + countFailFOOrder);
						if (countFailFOOrder > 0) {
							logWriter.println("List of failed orders:");
							logWriter.println(listFailedFOOrders.toString());
						}
						
						long elapsed = System.currentTimeMillis() - start;
						if (isLoggingDebug()) {
							logDebug("finish -  upload BO orders: " + (elapsed / 60000) + " min(s)");
						}
						logWriter.println();
						logWriter.println("Orders migration time: " + (elapsed / 60000) + " min(s)");
						
						logWriter.flush();
						logWriter.close();

						if (countFailOrder > 0) {
							moveFileIn(workingFile, errorDir);
							moveFileIn(statusFile, errorDir);
						} else {
							workingFile.delete();
							moveFileIn(statusFile, archiveDir);
							
						}

					} catch (Exception e) {
						if (isLoggingError()) {
							logError("Exception occured: ", e);
						}
						if (logWriter != null) {
							logWriter.println("Status: Failed");
							e.printStackTrace(logWriter);
							logWriter.close();
							moveFileIn(statusFile, errorDir);
						}
					}
				} catch (Exception e) {
					if (isLoggingError()) {
						logError("Exception occured: ", e);
					}
					if (logWriter != null) {
						logWriter.println("Status: Failed");
						e.printStackTrace(logWriter);
						logWriter.close();
						moveFileIn(statusFile, errorDir);
					}
				}
				
				if (stopMigration) {
					break;
				}
				
			}

		} catch (Exception e) {
			if (isLoggingError()) {
				logError("Exception occured: ", e);
			}
		} finally {
			MiscUtils.deleteFolder(workingDir);
		}
		if (isLoggingDebug()) {
			logDebug("finish - MigrationOrderManager - migrationOrders ");
		}
	}
	
	private boolean uploadXml(File tmpFile) {
		if (isLoggingDebug()) {
			logDebug("start - uploadXml: " + tmpFile.getAbsolutePath());
		}

		XMLToolsFactory factory = DefaultXMLToolsFactory.getInstance();
		XMLToDOMParser domParser = getXMLToDOMParser(factory);

		boolean rollback = true;
		boolean result = false;
		TransactionDemarcation trd = new TransactionDemarcation();
		try {
			InputStream inputStream = new FileInputStream(tmpFile);
			
			try {
				trd.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

				TemplateParser.addToTemplate(inputStream, null, domParser,
						(atg.adapter.gsa.GSARepository) getRepository(), TemplateParser.PARSE_NORMAL,
						false, new PrintWriter(System.out), null);

				rollback = false;
				result = true;
			} catch (Exception e) {
				if (isLoggingError()) {
					logError("Exception occured trying to add/update items", e);
				}
			} finally {
				try {
					inputStream.close();
				} catch (IOException ioe) {
					if (isLoggingError()) {
						logError("IOException occured: ", ioe);
					}
				}
				try {
					trd.end(rollback);
				} catch (TransactionDemarcationException e) {
					logError("TransactionDemarcationException occured: ", e);
				}
				getTransactionManager().setTransactionTimeout(0);
			}
		} catch (SystemException e) {
			if (isLoggingError()) {
				logError("setTransactionTimeout failed. ", e);
			}
		} catch (Exception e) {
			if (isLoggingError()) {
				logError("Exception occured: ", e);
			}
		}
		if (isLoggingDebug()) {
			logDebug("finish -  uploadXml: result="+result);
		}
		return result;
	}
	
	private void saveOrdersFO(String boOrderId) throws Exception {
		if (isLoggingDebug()) {
			logDebug("import order FO " + boOrderId);
		}
		String query = "NCOMMANDE_CLIENT = ?0";
		RepositoryView ordersView = getRepository().getView("BO_SCD_VENTE_WEB");
		RqlStatement statment = RqlStatement.parseRqlStatement(query);
		RepositoryItem[] orders = statment.executeQueryUncached(ordersView, new Object[] {boOrderId});
		if (orders != null) {
			for (RepositoryItem boOrder : orders) {
				MutableRepositoryItem foOrder = (MutableRepositoryItem)getOrderManager().getOrderFO(boOrderId);

				TransactionDemarcation trd = new TransactionDemarcation();
				boolean rollback = false;
				try {
					try {
						trd.begin(getTransactionManager());
		    			if (foOrder == null) {
		    				String profileId = (String)boOrder.getPropertyValue("CCLIENT_WEB");
		                    OrderImpl order =
		                        (OrderImpl) getOrderManager().createOrder(profileId, boOrderId, getOrderManager().getOrderTools().getDefaultOrderType());
		                    
		            		populateOrder(boOrderId, boOrder, order.getRepositoryItem(), order);
					        populateGroups(boOrderId, order.getRepositoryItem(), boOrder);
					        populateCommerceItems(boOrder.getRepositoryId(),order);
					        
					        setEmailProperties(order.getRepositoryItem(), boOrder.getRepositoryId());

		                    getOrderManager().addOrder(order);
		    			} else {
		    				if (getNeedUpdateOrder()) {
		    					
		    					populateOrder(boOrderId, boOrder, foOrder, null);
		    			        populateGroups(boOrderId, foOrder, boOrder);
		    			        
		    			        populateCommerceItems(boOrder.getRepositoryId(), foOrder);
		    			        
						        setEmailProperties(foOrder, boOrder.getRepositoryId());
		    					
		    					((MutableRepository)getOrderRepository()).updateItem(foOrder);
		    					
		    					//getOrderManager().updateOrder(order);
		    					
		    				}
		    			}
		    			
		    			updateContactEmailed(boOrder.getRepositoryId());
		    			
					} catch (Exception e) {
						rollback = true;
						if (isLoggingError()) {
							logError(e);
						}
						throw e;
					} finally {
						trd.end(rollback);
					}
					
				} catch (TransactionDemarcationException e) {
					if (isLoggingError()) {
						logError("TransactionDemarcationException occured: ", e);
					}
				}
			}
		}
	}

	private void populateOrder(String boOrderId, RepositoryItem boOrder, MutableRepositoryItem order, OrderImpl orderC) throws Exception {
		
        Date submittedDate = (Date) boOrder.getPropertyValue("DCREATION");
        order.setPropertyValue(PropertyNameConstants.SUBMITTEDDATE, submittedDate);

        Float totalTTC = 0F;
        if (boOrder.getPropertyValue("MTOTAL_TTC") != null) {
            totalTTC = (Float) boOrder.getPropertyValue("MTOTAL_TTC");
        }
        Float totalPFT = 0F;
        if (boOrder.getPropertyValue("TOTAL_PFT") != null) {
        	totalPFT = (Float) boOrder.getPropertyValue("TOTAL_PFT");
        }
        Float totalPFE = 0F;
        if (boOrder.getPropertyValue("TOTAL_PFE") != null) {
        	totalPFE = (Float) boOrder.getPropertyValue("TOTAL_PFE");
        }
        Float totalPFTPay = 0F;
        if (boOrder.getPropertyValue("MPFT_PAYEE") != null) {
        	totalPFTPay = (Float) boOrder.getPropertyValue("MPFT_PAYEE");
        }
        Float totalPFEPay = 0F;
        if (boOrder.getPropertyValue("MPFE_PAYEE") != null) {
        	totalPFEPay = (Float) boOrder.getPropertyValue("MPFE_PAYEE");
        }

        Float rawSubtotal = totalTTC - (totalPFE + totalPFT);
        Float amount = totalTTC - totalPFT;
        
        if (orderC != null) {
            orderC.getPriceInfo().setAmount(Double.parseDouble(amount.toString()));

            orderC.getPriceInfo().setRawSubtotal(Double.parseDouble(rawSubtotal.toString()));
        } else {
            MutableRepositoryItem priceInfo = (MutableRepositoryItem)order.getPropertyValue(PropertyNameConstants.PRICEINFO);
            if (priceInfo == null) {
            	if (isLoggingDebug()) {
            		logDebug(" Order - priceInfo is null. Create item  orderPriceInfo.");
            	}
            	priceInfo = ((MutableRepository)getOrderRepository()).createItem("orderPriceInfo");
            }

            priceInfo.setPropertyValue(PropertyNameConstants.AMOUNT, Double.parseDouble(amount.toString()));

            priceInfo.setPropertyValue("rawSubtotal", Double.parseDouble(rawSubtotal.toString()));
            
            order.setPropertyValue(PropertyNameConstants.PRICEINFO, priceInfo);
        }
        
        order.setPropertyValue("montantTotalCommandeTTC", Double.parseDouble(totalTTC.toString()));
        
        /*
        if (totalPFTPay != 0 && totalPFT != 0) {
            Double procFees = Double.parseDouble(totalPFTPay.toString())/Double.parseDouble(totalPFT.toString());
            order.setPropertyValue("processingFees", procFees);
        } else {
            order.setPropertyValue("processingFees", 0D);
        }
		*/
        if (totalPFT != 0) {
            order.setPropertyValue("processingFees", Double.parseDouble(totalPFT.toString()));
        } else {
            order.setPropertyValue("processingFees", 0D);
        }
        /*
        if (totalPFEPay != 0 && totalPFE != 0) {
            Double shipFees = Double.parseDouble(totalPFEPay.toString())/Double.parseDouble(totalPFE.toString());
            order.setPropertyValue("shippingFees", shipFees);
        } else {
            order.setPropertyValue("shippingFees", 0D);
        }
        */
        if (totalPFE != 0) {
            order.setPropertyValue("shippingFees", Double.parseDouble(totalPFE.toString()));
        } else {
            order.setPropertyValue("shippingFees", 0D);
        }
        
        double tw = 0.0;
        if (boOrder.getPropertyValue("MMASSE") != null) {
        	Integer totalWeight = (Integer) boOrder.getPropertyValue("MMASSE");
        	tw = totalWeight/1000;
        }
        order.setPropertyValue("totalWeight", tw);

        order.setPropertyValue(PropertyNameConstants.STATE, StateDefinitions.ORDERSTATES.SUBMITTED.toUpperCase());

		short boState = (Short) boOrder.getPropertyValue("TETAT_COMMANDE_C602");
		int state = new Integer(boState);

		order.setPropertyValue("fEtatIntegration", true);
		order.setPropertyValue("BOState", getOrderStates().getStateString(state));
		order.setPropertyValue("BOStateNum", state);
		order.setPropertyValue("BOStateDetail", getOrderStates().getStateDescription(state));
		order.setPropertyValue("exportdate", new Date());
	}
	

	private void populateGroups(String boOrderId, MutableRepositoryItem foOrder, RepositoryItem boOrder) throws Exception {
		String query = "NCOMMANDE_CLIENT = ?0";
		RepositoryView view = getRepository().getView("BO_SCD_FACTURE_AVOIR");
		RqlStatement statment = RqlStatement.parseRqlStatement(query);
		RepositoryItem[] factures = statment.executeQueryUncached(view, new Object[] {boOrderId});
		if (factures != null) {
			List payGroups = (List) foOrder.getPropertyValue(PropertyNameConstants.PAYMENTGROUPS);
			List shipGroups = (List) foOrder.getPropertyValue(PropertyNameConstants.SHIPPINGGROUPS);
			
			Short paymentCode = (Short) boOrder.getPropertyValue("CMODE_PAIEMENT_C607");
			
			for (int i = 0; i < factures.length; i++) {
				RepositoryItem item = factures[i];
				
				if (item.getPropertyValue("MTOTAL_TTC") != null || (Float)item.getPropertyValue("MTOTAL_TTC") > 0) {
					if (payGroups != null && !payGroups.isEmpty()) {
						MutableRepositoryItem pg = (MutableRepositoryItem)payGroups.get(0);
						populatePaymentItem(item, pg);
						pg.setPropertyValue(PropertyNameConstants.PAYMENTMETHOD, getPaymentMethod(paymentCode));
					} else {
						PaymentGroupImpl pg = (PaymentGroupImpl)getOrderManager().getPaymentGroupManager().createPaymentGroup();
						populatePaymentItem(item, pg.getRepositoryItem());
						pg.setPaymentMethod(getPaymentMethod(paymentCode));
						foOrder.setPropertyValue(PropertyNameConstants.PAYMENTGROUP, pg.getRepositoryItem());
					}
					
					if (shipGroups != null && !shipGroups.isEmpty()) {
						MutableRepositoryItem shGr = (MutableRepositoryItem)shipGroups.get(0);
						populateShippingItem(item, shGr);
					} else {
						ShippingGroupImpl shGr = (ShippingGroupImpl)getOrderManager().getShippingGroupManager().createShippingGroup();
						populateShippingItem(item, shGr.getRepositoryItem());
						foOrder.setPropertyValue(PropertyNameConstants.SHIPPINGGROUP, shGr.getRepositoryItem());
					}
					
					break;
				}
			}
		}
	}
	
	private void populatePaymentItem(RepositoryItem item, MutableRepositoryItem pg) {
		String[] val = splitNom(item.getPropertyValue("LNOM_FACTURE"));
		pg.setPropertyValue(PropertyNameConstants.PREFIX, val[0]);
		pg.setPropertyValue(PropertyNameConstants.FIRSTNAME, val[1]);
		pg.setPropertyValue(PropertyNameConstants.LASTNAME, val[2]);
		pg.setPropertyValue(PropertyNameConstants.ADDRESS1, getValueAsString(item.getPropertyValue("LADRESSE_1_FACTURE")));
		pg.setPropertyValue(PropertyNameConstants.ADDRESS2, getValueAsString(item.getPropertyValue("LADRESSE_2_FACTURE")));
		pg.setPropertyValue(PropertyNameConstants.ADDRESS3, getValueAsString(item.getPropertyValue("LADRESSE_3_FACTURE")));

		pg.setPropertyValue(PropertyNameConstants.CITY, getValueAsString(item.getPropertyValue("LVILLE_FACTURE")));
		pg.setPropertyValue(PropertyNameConstants.COUNTRY, getIntegerAsString(item.getPropertyValue("CPAYS_FACTURE")));
		pg.setPropertyValue(PropertyNameConstants.POSTALCODE, getLongAsString(item.getPropertyValue("CODE_POSTAL_FACTURE")));
	}
	
	private void populateShippingItem(RepositoryItem item, MutableRepositoryItem shGr) {
		String[] val = splitNom(item.getPropertyValue("LNOM_LIVRAISON"));
		shGr.setPropertyValue(PropertyNameConstants.PREFIX, val[0]);
		shGr.setPropertyValue(PropertyNameConstants.FIRSTNAME, val[1]);
		shGr.setPropertyValue(PropertyNameConstants.LASTNAME, val[2]);
		shGr.setPropertyValue(PropertyNameConstants.ADDRESS1, getValueAsString(item.getPropertyValue("LADRESSE_1_LIVRAISON")));
		shGr.setPropertyValue(PropertyNameConstants.ADDRESS2, getValueAsString(item.getPropertyValue("LADRESSE_2_LIVRAISON")));
		shGr.setPropertyValue(PropertyNameConstants.ADDRESS3, getValueAsString(item.getPropertyValue("LADRESSE_3_LIVRAISON")));
		shGr.setPropertyValue(PropertyNameConstants.POSTALCODE, getIntegerAsString(item.getPropertyValue("CODE_POSTAL_LIVRAISON")));
		shGr.setPropertyValue(PropertyNameConstants.COUNTRY, getByteAsString(item.getPropertyValue("CPAYS_LIVRAISON")));
		//shGr.setPropertyValue(PropertyNameConstants.PHONENUMBER, getValueAsString(item.getPropertyValue("NTEL_LIVRAISON")));
	}

	private void populateCommerceItems(String cventeId, OrderImpl foOrder) throws Exception {
		String query = "CVENTE = ?0";
		RepositoryView view = getRepository().getView("BO_SCD_VENTE_LIGNE");
		RqlStatement statement = RqlStatement.parseRqlStatement(query);

		//
		String queryVte = "CVENTE = ?0 AND CESCLAVE = ?1";
		RepositoryView viewVte = getRepository().getView("BO_VTE_VENTE_LIGNE");
		RqlStatement statementVte = RqlStatement.parseRqlStatement(queryVte);
		
		RepositoryItem[] items = statement.executeQuery(view, new Object[] {cventeId});
		RepositoryItem[] itemsVte = null;
		if (items != null) {
			List commerceItems = foOrder.getCommerceItems();
			for (int i = 0; i < items.length; i++) {
				RepositoryItem item = items[i];
				RepositoryItem itemVte = null;
				itemsVte = statementVte.executeQuery(viewVte, new Object[] {cventeId, item.getPropertyValue("CESCLAVE")});
				if (itemsVte != null) {
					itemVte = itemsVte[0];
				} else {
					logError("BO_VTE_VENTE_LIGNE not found by CVENTE=" + cventeId +" AND CESCLAVE=" + item.getPropertyValue("CESCLAVE"));
				}
				if (commerceItems != null && !commerceItems.isEmpty() && commerceItems.size() > i) {
					CommerceItem commItem = (CommerceItem)commerceItems.get(i);
					populateCommerceItem(item, commItem, itemVte);
				} else {
					CommerceItem commItem = populateCommerceItem(item, null, itemVte);
					if (commItem != null) {
						foOrder.addCommerceItem(commItem);
					}
				}
			}
		}
	}

	private CommerceItem populateCommerceItem(RepositoryItem venteLigne, CommerceItem commItem, RepositoryItem vteLine) {
		Integer catalogRef = 0;
		if (venteLigne.getPropertyValue("CESCLAVE") != null) {
			catalogRef = (Integer)venteLigne.getPropertyValue("CESCLAVE");
		}
		
		Long quantity = 0l;
		if (venteLigne.getPropertyValue("QUNITE_VENTE") != null) {
			Float val = (Float)venteLigne.getPropertyValue("QUNITE_VENTE");
			quantity = val.longValue();
		}

		Double listPrice = 0D;
		if (venteLigne.getPropertyValue("PV_INITIAL") != null) {
			Float val = (Float)venteLigne.getPropertyValue("PV_INITIAL");
			listPrice = Double.parseDouble(val.toString());
		}
		
		String displayName = null;
		if (vteLine != null) {
			displayName = (String)vteLine.getPropertyValue("LARTICLE_COMP");
		}

		try {
			String catalogRefId = "Casto" + Integer.toString(catalogRef);
			if (commItem == null) {
				if (quantity != 0) {
					commItem = getOrderManager().getCommerceItemManager()
					.createCommerceItem(catalogRefId, null, quantity);
				} else {
					commItem = getOrderManager().getCommerceItemManager()
					.createCommerceItem(catalogRefId, null, 1);
					commItem.setQuantity(0);
				}
			} else {
				//commItem.setCatalogRefId(catalogRefId);
				commItem.setQuantity(quantity);
			}
			if (commItem.getPriceInfo() == null) {
				try {
					commItem.setPriceInfo((ItemPriceInfo)getOrderManager().getOrderTools().getDefaultItemPriceInfoClass().newInstance());
					commItem.getPriceInfo().setListPrice(listPrice);
					commItem.getPriceInfo().setAmount(listPrice * quantity);
				} catch (Exception e) {
					if (isLoggingError()) {
						logError(e);
					}
				}
				 
			} else {
				commItem.getPriceInfo().setListPrice(listPrice);
				commItem.getPriceInfo().setAmount(listPrice * quantity);
			}
			((CommerceItemImpl)commItem).setPropertyValue("deliveryPeriod", venteLigne.getPropertyValue("DELAI_EXPEDITION_ANNONCE"));
			((CommerceItemImpl)commItem).setPropertyValue("codeArticle", catalogRef);
			((CommerceItemImpl)commItem).setPropertyValue("displayName", displayName);
					
		} catch (CommerceException e) {
			if (isLoggingInfo()) {
				logInfo(e.getMessage());
				logInfo("catalogRef="+catalogRef+"; quantity="+quantity+"; listPrice="+listPrice);
			}
		}
		return commItem;
	}
	

	private void populateCommerceItems(String cventeId, MutableRepositoryItem foOrder) throws Exception {
		String query = "CVENTE = ?0";
		RepositoryView view = getRepository().getView("BO_SCD_VENTE_LIGNE");
		RqlStatement statement = RqlStatement.parseRqlStatement(query);

		//
		String queryVte = "CVENTE = ?0 AND CESCLAVE = ?1";
		RepositoryView viewVte = getRepository().getView("BO_VTE_VENTE_LIGNE");
		RqlStatement statementVte = RqlStatement.parseRqlStatement(queryVte);
		
		RepositoryItem[] items = statement.executeQuery(view, new Object[] {cventeId});
		RepositoryItem[] itemsVte = null;
		if (items != null) {
			List commerceItems = (List)foOrder.getPropertyValue(PropertyNameConstants.COMMERCEITEMS);
			int commSize = commerceItems.size();
			for (int i = 0; i < items.length; i++) {
				RepositoryItem item = items[i];
				RepositoryItem itemVte = null;
				itemsVte = statementVte.executeQuery(viewVte, new Object[] {cventeId, item.getPropertyValue("CESCLAVE")});
				if (itemsVte != null) {
					itemVte = itemsVte[0];
				} else {
					logError("BO_VTE_VENTE_LIGNE not found by CVENTE=" + cventeId +" AND CESCLAVE=" + item.getPropertyValue("CESCLAVE"));
				}
				if (commerceItems != null && !commerceItems.isEmpty() && commSize > i) {
					MutableRepositoryItem commItem = (MutableRepositoryItem)commerceItems.get(i);
					populateCommerceRepoItem(item, commItem, itemVte);
				} else {
					MutableRepositoryItem commItem = ((MutableRepository)getOrderRepository()).createItem(PropertyNameConstants.COMMERCEITEM);
					commItem = populateCommerceRepoItem(item, commItem, itemVte);
					((MutableRepository)getOrderRepository()).addItem(commItem);
					commerceItems.add(commItem);
					foOrder.setPropertyValue(PropertyNameConstants.COMMERCEITEMS, commerceItems);
				}
			}
		}
	}

	private MutableRepositoryItem populateCommerceRepoItem(RepositoryItem venteLigne, MutableRepositoryItem commItem, RepositoryItem vteLine) {
		Integer catalogRef = 0;
		if (venteLigne.getPropertyValue("CESCLAVE") != null) {
			catalogRef = (Integer)venteLigne.getPropertyValue("CESCLAVE");
		}
		
		Long quantity = 0l;
		if (venteLigne.getPropertyValue("QUNITE_VENTE") != null) {
			Float val = (Float)venteLigne.getPropertyValue("QUNITE_VENTE");
			quantity = val.longValue();
		}

		Double listPrice = 0D;
		if (venteLigne.getPropertyValue("PV_INITIAL") != null) {
			Float val = (Float)venteLigne.getPropertyValue("PV_INITIAL");
			listPrice = Double.parseDouble(val.toString());
		}
		
		String displayName = null;
		if (vteLine != null) {
			displayName = (String)vteLine.getPropertyValue("LARTICLE_COMP");
		}

		try {
			String catalogRefId = "Casto" + Integer.toString(catalogRef);
			if (commItem == null) {
				commItem = ((MutableRepository)getOrderRepository()).createItem(PropertyNameConstants.COMMERCEITEM);
			}
			commItem.setPropertyValue(PropertyNameConstants.CATALOGREFID, catalogRefId);
			commItem.setPropertyValue(PropertyNameConstants.QUANTITY, quantity);
			commItem.setPropertyValue("deliveryPeriod", venteLigne.getPropertyValue("DELAI_EXPEDITION_ANNONCE"));
			commItem.setPropertyValue("codeArticle", catalogRef);
			commItem.setPropertyValue("displayName", displayName);

			MutableRepositoryItem priceInfo = (MutableRepositoryItem) commItem.getPropertyValue(PropertyNameConstants.PRICEINFO);
			if (priceInfo == null) {
				try {
					priceInfo = ((MutableRepository)getOrderRepository()).createItem("itemPriceInfo");
					priceInfo.setPropertyValue("listPrice", listPrice);
					priceInfo.setPropertyValue("amount", listPrice * quantity);
					((MutableRepository)getOrderRepository()).addItem(priceInfo);
					commItem.setPropertyValue(PropertyNameConstants.PRICEINFO, priceInfo);
				} catch (Exception e) {
					if (isLoggingError()) {
						logError(e);
					}
				}
				 
			} else {
				priceInfo.setPropertyValue("listPrice", listPrice);
				priceInfo.setPropertyValue("amount", listPrice * quantity);
			}
					
		} catch (RepositoryException e) {
			if (isLoggingError()) {
				logError(e);
			}
		}
		return commItem;
	}
	
	private void updateContactEmailed(String cventeId) throws RepositoryException {
		String query = "CVENTE = ?0";
		RepositoryView view = getRepository().getView("BO_SCD_CONTACT");
		RqlStatement statement = RqlStatement.parseRqlStatement(query);
		RepositoryItem[] items = statement.executeQuery(view, new Object[] {cventeId});
		if (items != null) {
			for (RepositoryItem item : items) {
				((MutableRepositoryItem)item).setPropertyValue("EMAILED", new Date());
				((MutableRepository)getRepository()).updateItem((MutableRepositoryItem)item);
			}
		}
	}
	
	private void setEmailProperties(MutableRepositoryItem foOrder, String cventeId) throws RepositoryException {
		Map map = new HashMap();
		for (String prop : EMAIL_PROPS) {
			map.put(prop, true);
		}
		
		String query = "CVENTE = ?0";
		RepositoryView view = getRepository().getView("BO_VTE_ENLEVEMENT");
		RqlStatement statement = RqlStatement.parseRqlStatement(query);
		RepositoryItem[] items = statement.executeQuery(view, new Object[] {cventeId});
		if (items != null) {
			for (RepositoryItem item : items) {
                int thisState = (Short) item.getPropertyValue("CETAT_OL_C651");
                
                String shipFlag = "";
                
                switch (thisState) {
				case 3:
				case 4:
					shipFlag = MailSender.SHIPMENT_PREPARATION + item.getRepositoryId();
					map.put(shipFlag, true);
					break;
				case 5:
				case 9:
					shipFlag = MailSender.CONFIRMATION_SHIPMENT + item.getRepositoryId();
					map.put(shipFlag, true);
					break;

				default:
					break;
				}
				
			}
		}

		foOrder.setPropertyValue(MailSender.ORDER_EMAILS, map);
	}

	private String getPaymentMethod(short code) {
		String result = "";
		switch (code) {
		case 1:
			result = PaymentStates.CREDIT_CARD;
			break;
		case 2:
			result = PaymentStates.ATOUT;
			break;
		case 3:
			result = PaymentStates.CHEQUE;
			break;
		case 4:
			result = PaymentStates.CADEAU;
			break;
		case 5:
			result = PaymentStates.MIX;
			break;
		default:
			break;
		}
		
		return result;
	}
	
	private String[] splitNom(Object value) {
		String result = getValueAsString(value);
		String[] arr = new String[] {"", "", ""};
		if (result != null) {
			String[] a = result.split(" ", 2);
			switch (a.length) {
			case 1:
				arr[2] = a[0].length() > 35 ? a[0].substring(0, 35) : a[0];
				break;
			case 2:
				arr[0] = a[0];
				arr[2] = a[1];
				String test = a[1];
				while (test.length() > 35) {
					int idx = test.lastIndexOf(" ");
					arr[1] = a[1].substring(idx +1);
					arr[2] = a[1].substring(0, idx);
					test = test.substring(0, idx);
				}
				break;
			default:
				break;
			}
		}
		return arr;
	}

	private String getNom(Object value) {
		String result = getValueAsString(value);
		if (result != null && result.length() > 35) {
			result = result.substring(0, 35);
		}
		return result;
	}

	private String getValueAsString(Object value) {
		String result = null;
		if (value != null) {
			result = (String) value;
		}
		return result;
	}

	private String getIntegerAsString(Object value) {
		String result = null;
		if (value != null) {
			result = Integer.toString((Integer) value);
		}
		return result;
	}
	
	private String getLongAsString(Object value) {
		String result = null;
		if (value != null) {
			result = Long.toString((Long) value);
		}
		return result;
	}

	private String getByteAsString(Object value) {
		String result = null;
		if (value != null) {
			result = Byte.toString((Byte) value);
		}
		return result;
	}

	private String getOrderIdFromName(String fileName) {
		return fileName.substring(0, fileName.indexOf("_"));
	}

}
