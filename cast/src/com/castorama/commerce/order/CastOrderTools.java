package com.castorama.commerce.order;

import atg.adapter.gsa.GSAItem;
import atg.commerce.CommerceException;
import atg.commerce.inventory.InventoryException;
import atg.commerce.order.*;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.*;
import atg.repository.rql.RqlStatement;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfo;
import atg.userprofiling.email.TemplateEmailInfoImpl;
import atg.userprofiling.email.TemplateEmailSender;

import com.castorama.CastConfiguration;
import com.castorama.commerce.inventory.CastRepositoryInventoryManager;
import com.castorama.commerce.inventory.CastRepositoryInventoryManager.StockLevelStatus;
import com.castorama.commerce.inventory.CastRepositoryInventoryManager.StockLevelStatusAndAvail;
import com.castorama.commerce.pricing.Constants;
import com.castorama.constantes.CastoConstantesDefense;
import com.castorama.pricing.CastItemPriceInfo;
import com.castorama.utils.PDFGenerator;
import com.castorama.utils.PDFTools;
import com.castorama.utils.StoreTools;

import java.io.File;
import java.util.*;

import static com.castorama.commerce.profile.Constants.EMAIL_PROFILE_PROP;

/**
 * Extends OrderTools to add Castorama utility methods.
 * used by  /atg/commerce/order/OrderTools component.
 *
 * @author Epam Team
 */
public class CastOrderTools extends OrderTools {

    /**
     * SALE_PRICE_LOCAL_DESCRIPTION constant.
     */
    private static final String SALE_PRICE_LOCAL_DESCRIPTION = "Sale price local";

    /**
     * LIST_PRICE constant.
     */
    private static final String LIST_PRICE = "List price";

    /**
     * SALE_PRICE constant.
     */
    private static final String SALE_PRICE = "Sale price";

    /**
     * CLICK_AND_COLLECT_ORDER_TYPE constant.
     */
    private static final String CLICK_AND_COLLECT_ORDER_TYPE = "clickAndCollect";

    /**
     * PAYEMENT_GROUPS constant.
     */
    private static final String PAYEMENT_GROUPS = "paymentGroups";

    /**
     * COMMERCE_ITEM_BACKUP constant.
     */
    private static final String COMMERCE_ITEM_BACKUP = "commerce_item_backup";

    /**
     * ORDER_BACKUP constant.
     */
    private static final String ORDER_BACKUP = "order_backup";

    /**
     * PRICE_LIST_EXCEPTION constant
     */
    static final String PRICE_LIST_EXCEPTION = "PriceListException";

    /**
     * PRICE_LIST constant
     */
    public static final String PRICE_LIST = "priceList";

    /**
     * BUNDLE_LINKS constant.
     */
    private static final String BUNDLE_LINKS = "bundleLinks";

    /**
     * ITEM constant.
     */
    public static final String ITEM = "item";
    
    /**
     * PDF_RXTENSION constant
     */
    public static final String PDF_EXTENSION = ".pdf";

    /**
     * maximum number of attempts in order cleanup
     */
    private static final int clearOrderMaxAttempts = 500;

    /**
     * orderBackupRepository property
     */
    private Repository mOrderBackupRepository;

    /**
     * commerceItemManager property
     */
    private CommerceItemManager mCommerceItemManager;

    /**
     * productCatalogRepository property
     */
    private Repository mProductCatalogRepository;

    /**
     * pourcentageAtout property
     */
    private double mPourcentageAtout;

    /**
     * templateEmailSender property
     */
    private TemplateEmailSender mTemplateEmailSender = null;

    PriceListManager mPriceListManager;

    /**
     * Store tools.
     */
    private StoreTools storeTools;
    
    /**
     * PDF Tools
     */
    private PDFTools pdfTools;
       
    private CastConfiguration castConfiguration;

	public CastConfiguration getCastConfiguration() {
		return castConfiguration;
	}

	public void setCastConfiguration(CastConfiguration castConfiguration) {
		this.castConfiguration = castConfiguration;
	}

	/**
     * Sets the Price List Manager
     */
    public void setPriceListManager(PriceListManager pPriceListManager) {
        mPriceListManager = pPriceListManager;
    }

    /**
     * Returns the Price List Manager
     */
    public PriceListManager getPriceListManager() {
        return mPriceListManager;
    }

    /**
     * Returns orderBackupRepository property.
     *
     * @return orderBackupRepository property.
     */
    public Repository getOrderBackupRepository() {
        return mOrderBackupRepository;
    }

    /**
     * Sets the value of the orderBackupRepository property.
     *
     * @param pOrderBackupRepository parameter to set.
     */
    public void setOrderBackupRepository(Repository pOrderBackupRepository) {
        mOrderBackupRepository = pOrderBackupRepository;
    }

    /**
     * Returns commerceItemManager property.
     *
     * @return commerceItemManager property.
     */
    public CommerceItemManager getCommerceItemManager() {
        return mCommerceItemManager;
    }

    /**
     * Sets the value of the commerceItemManager property.
     *
     * @param pCommerceItemManager parameter to set.
     */
    public void setCommerceItemManager(CommerceItemManager pCommerceItemManager) {
        mCommerceItemManager = pCommerceItemManager;
    }

    /**
     * Returns productCatalogRepository property.
     *
     * @return productCatalogRepository property.
     */
    public Repository getProductCatalogRepository() {
        return mProductCatalogRepository;
    }

    /**
     * Sets the value of the productCatalogRepository property.
     *
     * @param pProductCatalogRepository parameter to set.
     */
    public void setProductCatalogRepository(Repository pProductCatalogRepository) {
        mProductCatalogRepository = pProductCatalogRepository;
    }

    /**
     * Returns pourcentageAtout property.
     *
     * @return pourcentageAtout property.
     */
    public double getPourcentageAtout() {
        return mPourcentageAtout;
    }

    /**
     * Sets the value of the pourcentageAtout property.
     *
     * @param pPourcentageAtout parameter to set.
     */
    public void setPourcentageAtout(double pPourcentageAtout) {
        mPourcentageAtout = pPourcentageAtout;
    }

    /**
     * Returns templateEmailSender property.
     *
     * @return templateEmailSender property.
     */
    public TemplateEmailSender getTemplateEmailSender() {
        return mTemplateEmailSender;
    }

    /**
     * Sets the value of the templateEmailSender property.
     *
     * @param pTemplateEmailSender parameter to set.
     */
    public void setTemplateEmailSender(TemplateEmailSender pTemplateEmailSender) {
        mTemplateEmailSender = pTemplateEmailSender;
    }

    /**
     * @return the storeTools
     */
    public StoreTools getStoreTools() {
        return storeTools;
    }

    /**
     * @param storeTools the storeTools to set
     */
    public void setStoreTools(StoreTools storeTools) {
        this.storeTools = storeTools;
    }

    public PDFTools getPdfTools() {
		return pdfTools;
	}

	public void setPdfTools(PDFTools pdfTools) {
		this.pdfTools = pdfTools;
	}

	/**
     * Set creation date.
     *
     * @param pType parameter.
     * @return Commerce item.
     * @throws CommerceException exception.
     */
    @Override
    public CommerceItem createCommerceItem(String pType) throws CommerceException {
        CommerceItem result = super.createCommerceItem(pType);
        if (result instanceof CastCommerceItemImpl) {
            CastCommerceItemImpl item = (CastCommerceItemImpl) result;
            item.setCreationDate(new Date(System.currentTimeMillis()));
        }
        return result;
    }

    /**
     * The method, which will affect the total amount of the order.
     *
     * @param pOrder The command to edit.
     * @return true if the update is ok, false otherwise.
     */
    public boolean affecterTotalCommande(CastOrderImpl pOrder) {
        OrderPriceInfo priceInfo = pOrder.getPriceInfo();
        boolean ret = true;
        if (priceInfo == null) {
            ret = false;
        } else {
            pOrder.setMontantTotalCommandeTTC(priceInfo.getTotal());
        }
        return ret;
    }

    /**
     * Method which affects the amount of shipping not stored.
     *
     * @param pOrder The command to edit.
     * @return True if the update went well, false otherwise.
     */
    public boolean affecterMontantFraisLivraisonNonRemise(CastOrderImpl pOrder) {
        boolean result = false;
        if (null != pOrder) {
            double montant = 0.0;
            List<ShippingGroup> shippingGroups = (List<ShippingGroup>) pOrder.getShippingGroups();
            for (ShippingGroup shippingGroup : shippingGroups) {
                ShippingPriceInfo priceInfo = shippingGroup.getPriceInfo();
                if (null != priceInfo) {
                    List<PricingAdjustment> adjustments = (List<PricingAdjustment>) priceInfo.getAdjustments();
                    if (null != adjustments) {
                        for (PricingAdjustment ajustement : adjustments) {
                            montant += ajustement.getTotalAdjustment();
                            result = true;
                        }
                    }
                }
            }
            if (pOrder.isPayeCarteAtout()) {
                pOrder.setMontantFraisLivraisonNonRemise(montant);
            } else {
                pOrder.setMontantFraisLivraisonNonRemise(montant);
            }
        }  // end if

        return result;
    }

    /**
     * Method which affects the amount of stored charge of preparing the order.
     *
     * @param pOrder The command to edit.
     * @return True if the update went well, false otherwise.
     */
    public boolean affecterMontantRemiseLivraisonEtMonteeEtage(CastOrderImpl pOrder) {
        boolean ret = true;

        ShippingGroup shippingGroup;

        for (Iterator shippingGroups = pOrder.getShippingGroups().iterator(); shippingGroups.hasNext(); ) {
            shippingGroup = (ShippingGroup) shippingGroups.next();

            if (CastoConstantesDefense.LIVRAISON_A_DOMICILE.equals(shippingGroup.getShippingMethod())) {
                ShippingPriceInfo priceInfo = shippingGroup.getPriceInfo();

                if (null == priceInfo) {
                    logError("CastoOrderTools.affecterMontantRemiseLivraisonEtMonteeEtage(CastoOrder) : price info null.");

                    ret = false;
                } else {
                    pOrder.setMontantRemiseLivraisonEtMonteeEtage(pOrder.getMontantFraisLivraisonNonRemise() -
                            priceInfo.getAmount());
                }

                break;
            }
        }

        return ret;
    }

    /**
     * Method which affects the amount of stored charge of preparing the order.
     *
     * @param pOrder The command to edit.
     * @return True if the update went well, false otherwise.
     */
    public boolean affecterMontantFraisDePreparationRemise(CastOrderImpl pOrder) {
        boolean ret = true;

        pOrder.setMontantFraisDePreparationRemise(0);

        return ret;
    }

    /**
     * Method which affects the amount of discounts applied to the order.
     *
     * @param pOrder the command to edit.
     * @return True if the update went well, false otherwise.
     */
    public boolean affecterMontantRemiseTotal(CastOrderImpl pOrder) {
        boolean ret = true;
        double montant;

        montant = pOrder.getMontantRemiseLivraisonEtMonteeEtage();

        List commerceItems = pOrder.getCommerceItems();
        CommerceItem item;
        ItemPriceInfo priceInfo;
        double remise;

        if (null != commerceItems) {
            for (Iterator iterator = commerceItems.iterator(); iterator.hasNext(); ) {
                item = (CommerceItem) iterator.next();

                priceInfo = item.getPriceInfo();

                remise = priceInfo.getRawTotalPrice() - priceInfo.getAmount();

                montant += remise;
            }
        }

        OrderPriceInfo orderPriceInfo = pOrder.getPriceInfo();

        if (null == orderPriceInfo) {
            ret = false;
        } else {
            PricingAdjustment ajustement;
            boolean isFirst = true;

            for (Iterator iterator = orderPriceInfo.getAdjustments().iterator(); iterator.hasNext(); ) {
                ajustement = (PricingAdjustment) iterator.next();

                if (isFirst) {
                    isFirst = false;
                } else {
                    montant += Math.abs(ajustement.getTotalAdjustment());
                }
            }
        }

        return ret;
    }

    /**
     * Method which affects the amounts of PFL and PFT Order CDs.
     *
     * @param pOrder The command to edit.
     * @return True if the update went well, false otherwise.
     */
    public boolean affecterMontantsPFLPFT(CastOrderImpl pOrder) {
        boolean ret;

        if (pOrder == null) {
            ret = false;
        } else {
            List shippingGroups = pOrder.getShippingGroups();

            double shippingPrice = 0.0;

            double handlingPrice = 0.0;

            if ((shippingGroups != null) && (shippingGroups instanceof Collection)) {
                for (ShippingGroup shippingGroup : (List<ShippingGroup>) shippingGroups) {
                    ShippingPriceInfo info = shippingGroup.getPriceInfo();
                    if ((info != null) && (info.getAdjustments() != null)) {
                        for (PricingAdjustment pa : (List<PricingAdjustment>) info.getAdjustments()) {
                            double tmpAmount = pa.getTotalAdjustment();
                            if (Constants.HANDLING_PRICE_ADJUSTMENT_DESCRIPTION.equalsIgnoreCase(pa.getAdjustmentDescription())) {
                                handlingPrice += tmpAmount;
                            } else {
                                shippingPrice += tmpAmount;
                            }
                        }
                    }
                }
            }
            pOrder.setMontantPFLTotal(shippingPrice);
            pOrder.setMontantPFTTotal(handlingPrice);

            logInfo("shippingPrice: " + shippingPrice + ", handlingPrice" + handlingPrice);

            ret = true;
        }  // end if-else

        return ret;
    }

    /**
     * Utility method for sending confirmation email.
     *
     * @param pTemplate for email
     * @param pProfile  profile
     * @param pOrder    order item
     * @param pParams   other parameters
     */
    public void sendConfirmationEmail(TemplateEmailInfo pTemplate, RepositoryItem pProfile, Order pOrder, Map pParams) {
        TemplateEmailInfoImpl templateEmailInfo = (TemplateEmailInfoImpl) pTemplate.copy();
        Map params = new HashMap();

        params.put("user", pProfile);

        params.put("order", pOrder);
        params.put("params", pParams);

        List recipents = new ArrayList();
        recipents.add(pProfile.getPropertyValue(EMAIL_PROFILE_PROP));

        String subjectsBase = templateEmailInfo.getMessageSubject();
        templateEmailInfo.setMessageSubject(subjectsBase + pOrder.getId());

        templateEmailInfo.setTemplateParameters(params);

        try {
            getTemplateEmailSender().sendEmailMessage(templateEmailInfo, recipents, true, false);
        } catch (TemplateEmailException e) {
        }

    }

    /**
     * Method which preserves the order on the cart recap
     *
     * @param pOrder   The command to back up.
     * @param pProfile The profile contains information.
     * @return True if the update went well, false otherwise.
     */
    public boolean sauvegarderCommande(CastOrderImpl pOrder, Profile pProfile) {
        boolean ret = true;
        if (pOrder != null) {
            if (pProfile != null) {
                synchronized (pOrder) {
                    Set cceItemsBackUp = backupCceItems(pOrder);
                    backupCde(pOrder, pProfile, cceItemsBackUp);
                }
            } else {
                if (isLoggingDebug()) {
                    logDebug("pProfile=null");
                }
            }
        } else {
            if (isLoggingDebug()) {
                logDebug("pOrder=null");
            }
        }

        return ret;
    }

    /**
     * Backup commerce items from order.
     *
     * @param pOrder CDE saver
     * @return list with cce saver
     */
    private Set backupCceItems(CastOrderImpl pOrder) {
        Set cceItemsBackUp = null;
        MutableRepository orderBackupRepo = (MutableRepository) getOrderBackupRepository();
        if (orderBackupRepo != null) {

            clearCommerceItems(orderBackupRepo, pOrder.getId());

            List cceItems = pOrder.getCommerceItems();
            if ((cceItems != null) && (cceItems.size() > 0)) {
                MutableRepositoryItem cceBackup = null;
                boolean creationCce = false;
                CommerceItemImpl cceItem = null;
                try {
                    cceItemsBackUp = new HashSet();
                    for (Iterator it = cceItems.iterator(); it.hasNext(); ) {
                        cceItem = (CommerceItemImpl) it.next();
                        cceBackup =
                                (MutableRepositoryItem) orderBackupRepo.getItem(cceItem.getId(), COMMERCE_ITEM_BACKUP);
                        if (cceBackup == null) {
                            cceBackup = orderBackupRepo.createItem(cceItem.getId(), COMMERCE_ITEM_BACKUP);
                            creationCce = true;
                        } else {
                            if (isLoggingDebug()) {
                                logDebug("CastOrderTools.backupCde(CastOrderImpl, Profile pProfile, Set pCceItemsBackUp)(CastOrderImpl, pProfile) " +
                                        ": " + cceItem.getId() + " deja cree");
                            }
                        }

                        String codeArticle = (String) cceItem.getCatalogRefId();
                        if (codeArticle != null) {
                            cceBackup.setPropertyValue("codeArticle", codeArticle);
                        }

                        String cdeRef = (String) pOrder.getId();
                        if (cdeRef != null) {
                            cceBackup.setPropertyValue("cdeRef", cdeRef);
                        }

                        Long qte = new Long(cceItem.getQuantity());
                        if (qte != null) {
                            cceBackup.setPropertyValue("quantite", qte);
                        }

                        Double pxLigne = new Double(cceItem.getPriceInfo().getAmount());
                        if (pxLigne != null) {
                            cceBackup.setPropertyValue("prixLigneArticle", pxLigne);
                        }

                        if (creationCce) {
                            orderBackupRepo.addItem(cceBackup);
                        } else {
                            orderBackupRepo.updateItem(cceBackup);
                        }

                        cceItemsBackUp.add(cceBackup);

                        creationCce = false;
                    }  // end for

                } catch (RepositoryException rex) {
                    logError(rex);
                }  // end try-catch
            } else {
                if (isLoggingDebug()) {
                    logDebug("cceItems==null  || cceItems.size()==0");
                }
            }  // end if-else
        } else {
            if (isLoggingDebug()) {
                logDebug("getOrderBackupRepository()=null");
            }
        }  // end if-else

        return cceItemsBackUp;
    }

    /**
     * Clear back-up order commerce items.
     *
     * @param pRepository back-up repository.
     * @param pOrderId    order id.
     */
    private void clearCommerceItems(MutableRepository pRepository, String pOrderId) {
        try {
            RqlStatement statement = RqlStatement.parseRqlStatement("cdeRef = ?0");
            RepositoryView view = pRepository.getView(COMMERCE_ITEM_BACKUP);
            RepositoryItem[] items = statement.executeQuery(view, new Object[]{pOrderId});
            if (null != items) {
                for (RepositoryItem item : items) {
                    pRepository.removeItem(item.getRepositoryId(), COMMERCE_ITEM_BACKUP);
                }
            }
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e);
            }
        }


    }

    /**
     * Backup CDE
     *
     * @param pOrder          CDE saver
     * @param pProfile        profile cde
     * @param pCceItemsBackUp cces items of cde
     */
    private void backupCde(CastOrderImpl pOrder, Profile pProfile, Set pCceItemsBackUp) {
        MutableRepository orderBackupRepo = (MutableRepository) getOrderBackupRepository();
        if (null != orderBackupRepo) {
            try {
                MutableRepositoryItem orderBackup = null;
                boolean creationBackup = false;
                orderBackup = (MutableRepositoryItem) orderBackupRepo.getItem(pOrder.getId(), ORDER_BACKUP);
                if (orderBackup == null) {
                    orderBackup = orderBackupRepo.createItem(pOrder.getId(), ORDER_BACKUP);
                    creationBackup = true;
                } else {
                    if (isLoggingDebug()) {
                        logDebug("CastoOrderTools.backupCde(CastoOrder, Profile, Set a_cceItemsBackUp) : " +
                                pOrder.getId() + " deja cree");
                    }
                }

                orderBackup.setPropertyValue("dateCommande", new Date());

                Double totalArticles = new Double(pOrder.getPriceInfo().getAmount());
                if (totalArticles != null) {
                    orderBackup.setPropertyValue("totalArticles", totalArticles);
                }

                Double totalPFL = pOrder.getShippingFees();
                if (totalPFL != null) {
                    orderBackup.setPropertyValue("FraisLivraison", totalPFL);
                }

                Double totalPFT = pOrder.getProcessingFees();
                if (totalPFT != null) {
                    orderBackup.setPropertyValue("FraisTraitement", totalPFT);
                }

                Double total = new Double(pOrder.getPriceInfo().getTotal());
                if (total != null) {
                    orderBackup.setPropertyValue("totalCommande", total);
                }

                String origineMag = (String) pOrder.getOrigineMagasin().getPropertyValue("nom");
                if (origineMag != null) {
                    orderBackup.setPropertyValue("origineMagasin", origineMag);
                }

                String login = (String) pProfile.getPropertyValue("login");
                if (login != null) {
                    orderBackup.setPropertyValue("login", login);
                }

                String nom = (String) pProfile.getPropertyValue("lastName");
                if (nom != null) {
                    orderBackup.setPropertyValue("nom", nom);
                }

                String prenom = (String) pProfile.getPropertyValue("firstName");
                if (prenom != null) {
                    orderBackup.setPropertyValue("prenom", prenom);
                }

                List paymentsGroups = (List) pOrder.getPropertyValue(PAYEMENT_GROUPS);
                for (Iterator iterator = paymentsGroups.iterator(); iterator.hasNext(); ) {
                    MutableRepositoryItem paymentGroup = (MutableRepositoryItem) iterator.next();

                    String moyenPaiement = (String) paymentGroup.getPropertyValue("paymentMethod");
                    if (moyenPaiement != null) {
                        orderBackup.setPropertyValue("moyenPaiement", moyenPaiement);
                    }

                    String telPortable = (String) paymentGroup.getPropertyValue("telPortable");
                    if (telPortable != null) {
                        orderBackup.setPropertyValue("telPortable", telPortable);
                    }

                    String telFixe = (String) paymentGroup.getPropertyValue("phoneNumber");
                    if (telFixe != null) {
                        orderBackup.setPropertyValue("telFixe", telFixe);
                    }
                }

                List hardGoodSg = (List) pOrder.getShippingGroups();
                for (Iterator iterator = hardGoodSg.iterator(); iterator.hasNext(); ) {
                    ShippingGroupImpl hgGroup = (ShippingGroupImpl) iterator.next();
                    String addressShippingAddress = (String) hgGroup.getPropertyValue("address1");
                    String postalCodeShippingAddress = (String) hgGroup.getPropertyValue("postalCode");
                    String cityShippingAddress = (String) hgGroup.getPropertyValue("city");
                    String countryShippingAddress = (String) hgGroup.getPropertyValue("country");

                    if ((addressShippingAddress != null) && (postalCodeShippingAddress != null) &&
                            (cityShippingAddress != null) && (countryShippingAddress != null)) {
                        String res =
                                addressShippingAddress + ", " + postalCodeShippingAddress + " " + cityShippingAddress +
                                        " " + countryShippingAddress;
                        orderBackup.setPropertyValue("adresseLibelleLiv", res);
                    }
                    break;
                }

                RepositoryItem billingAddress = (RepositoryItem) pProfile.getPropertyValue("billingAddress");
                String addressBillingAddress = (String) billingAddress.getPropertyValue("address1");
                String postalCodeBillingAddress = (String) billingAddress.getPropertyValue("postalCode");
                String cityBillingAddress = (String) billingAddress.getPropertyValue("city");
                String countryBillingAddress = (String) billingAddress.getPropertyValue("country");

                if ((addressBillingAddress != null) && (postalCodeBillingAddress != null) &&
                        (cityBillingAddress != null) && (countryBillingAddress != null)) {
                    String res =
                            addressBillingAddress + ", " + postalCodeBillingAddress + " " + cityBillingAddress + " " +
                                    countryBillingAddress;
                    orderBackup.setPropertyValue("adresseLibelleFactu", res);
                }

                if ((pCceItemsBackUp != null) && (pCceItemsBackUp.size() > 0)) {
                    orderBackup.setPropertyValue("listCceItems", pCceItemsBackUp);
                }

                if (creationBackup) {
                    orderBackupRepo.addItem(orderBackup);
                } else {
                    orderBackupRepo.updateItem(orderBackup);
                }
            } catch (RepositoryException rex) {
                logError(rex);
            }  // end try-catch
        } else {
            if (isLoggingDebug()) {
                logDebug("null==getOrderBackupRepository()");
            }
        }  // end if-else

    }

    private boolean isPriceMissed(RepositoryItem pItem) {
        boolean priceMissed = false;
        try {
            if (pItem != null) {
                List<RepositoryItem> bundleLinks = (List) pItem.getPropertyValue(BUNDLE_LINKS);
                if ((bundleLinks != null) && !bundleLinks.isEmpty()) {
                    for (RepositoryItem link : bundleLinks) {
                        priceMissed = isSkuPriceMissed((RepositoryItem) link.getPropertyValue(ITEM));
                        if (priceMissed) {
                            return true;
                        }
                    }
                } else {
                    priceMissed = isSkuPriceMissed(pItem);
                }
            }  // end if
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e.getMessage());
            }
            priceMissed = true;
        }  // end try-catch
        return priceMissed;
    }

    private boolean isSkuPriceMissed(RepositoryItem item) {
        boolean priceMissed = false;

        try {
            PriceListManager plManager = getPriceListManager();
            RepositoryItem profile = ServletUtil.getCurrentUserProfile();
            RepositoryItem priceList = plManager.getPriceList(profile, PRICE_LIST);
            RepositoryItem lPrice = null;
            if (priceList != null) {
                lPrice = plManager.getPrice(priceList, null, item.getRepositoryId());
                if (lPrice != null) {
                    Object oPrice = lPrice.getPropertyValue(plManager.getListPricePropertyName());
                    priceMissed = (oPrice == null);
                }
            }
        } catch (Exception e) {
            priceMissed = true;
            logError(PRICE_LIST_EXCEPTION, e);
        }

        return priceMissed;
    }


    /**
     * Removes list of illegal commerce items contained in pOrder - items for
     * "immediate withdrawal"
     *
     * @param pOrder parameter
     * @return
     */
    public boolean[] removeCommerceItemIds(Order pOrder) {
        // result[0] is true if at least one item was removed
        // result[1] is true if at least one item of c&c order hasn't got enough stock 
        boolean[] result = {false, false};
        if ((pOrder != null) && (getCommerceItemManager() != null) && (getInventoryManager() != null)) {
            List<CommerceItem> commerceItems = (List<CommerceItem>) pOrder.getCommerceItems();
            List<CommerceItem> commerceItemsForRemoval = new ArrayList<CommerceItem>();
            ShippingGroup shGroup = (ShippingGroup) pOrder.getShippingGroups().get(0);
            if ((commerceItems != null) && !commerceItems.isEmpty()) {
                for (CommerceItem ci : commerceItems) {
                    RepositoryItem sku = (RepositoryItem) ci.getAuxiliaryData().getCatalogRef();
                    if (sku != null) {
                        CastRepositoryInventoryManager inventoryManager = (CastRepositoryInventoryManager) getInventoryManager();

                        boolean isClickAndCollectDelivery = false;
                        RepositoryItem store = null;
                        if (pOrder instanceof CastOrderImpl) {
                            isClickAndCollectDelivery = CLICK_AND_COLLECT_ORDER_TYPE.equals(((CastOrderImpl) pOrder).getDeliveryType());
                            store = getStoreTools().getStore(((CastOrderImpl) pOrder).getMagasinId());
                        }

                        // store parameter
                        //TODO svAvailableMap parameter
                        StockLevelStatusAndAvail stockStatusAndAvailable = inventoryManager.inventoryStockLevelStatusCC(sku, store, null);
                        if (isClickAndCollectDelivery) {
                            if (stockStatusAndAvailable.getStockLevelStatus() == StockLevelStatus.S0 || 
                                    stockStatusAndAvailable.getStockLevelStatus() == StockLevelStatus.S1 || 
                                    stockStatusAndAvailable.getStockLevelStatus() == StockLevelStatus.S2 || 
                                    stockStatusAndAvailable.getStockLevelStatus() == StockLevelStatus.S3 || 
                                    stockStatusAndAvailable.getStockLevelStatus() == StockLevelStatus.S4 || 
                                    stockStatusAndAvailable.getStockLevelStatus() == StockLevelStatus.S5) {
                                commerceItemsForRemoval.add(ci);
                            } else {
                                CastCommerceItemImpl castCommerceItem = (CastCommerceItemImpl) ci;
                                int quantity = (int) castCommerceItem.getQuantity();
                                long stockVisQuantity = stockStatusAndAvailable.getSvStockAvailable();
                                if (quantity > stockVisQuantity) {
                                    result[1] = true;
                                    castCommerceItem.setQuantityWasDecreased(true);
                                    castCommerceItem.setCurrentInventoryValue(stockVisQuantity);
                                    castCommerceItem.setQuantity(stockVisQuantity);
                                    try {
                                        getCommerceItemManager().removeItemQuantityFromShippingGroup(
                                                pOrder, castCommerceItem.getId(), shGroup.getId(),
                                                quantity - stockVisQuantity
                                        );
                                    } catch (CommerceException e) {
                                        if (isLoggingError()) {
                                            logError(e);
                                        }
                                    }
                                }
                            }
                        } else {
                            try {
                                if (stockStatusAndAvailable.getStockLevelStatus() == StockLevelStatus.S0 || 
                                        stockStatusAndAvailable.getStockLevelStatus() == StockLevelStatus.S2 || 
                                        stockStatusAndAvailable.getStockLevelStatus() == StockLevelStatus.S7) {
                                    commerceItemsForRemoval.add(ci);
                                } else {
                                    long webStockQuantiry = inventoryManager.queryStockLevel(sku.getRepositoryId());
                                    if (webStockQuantiry < 0) webStockQuantiry = Long.MAX_VALUE;
                                    int quantity = (int) ci.getQuantity();
                                    if (quantity > webStockQuantiry && stockStatusAndAvailable.getStockLevelStatus() != StockLevelStatus.S3 
                                            && stockStatusAndAvailable.getStockLevelStatus() != StockLevelStatus.S4 
                                            && stockStatusAndAvailable.getStockLevelStatus() != StockLevelStatus.S5
                                            && stockStatusAndAvailable.getStockLevelStatus() != StockLevelStatus.S8 
                                            && stockStatusAndAvailable.getStockLevelStatus() != StockLevelStatus.S9 
                                            && stockStatusAndAvailable.getStockLevelStatus() != StockLevelStatus.S10) {
                                        ci.setQuantity(webStockQuantiry);
                                        if ( webStockQuantiry == 0 ) {
                                            commerceItemsForRemoval.add(ci);
                                        } else {
                                            try {
                                                getCommerceItemManager().removeItemQuantityFromShippingGroup(pOrder, ci.getId(), shGroup.getId(), quantity - webStockQuantiry);
                                            } catch (CommerceException e) {
                                                if (isLoggingError()) {
                                                    logError(e);
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (InventoryException e1) {
                                if (isLoggingError()) {
                                    logError(e1);
                                }
                            }
                        }
                        if (isPriceMissed(sku)) {
                            commerceItemsForRemoval.add(ci);
                        }
                    }
                }

                if (!commerceItemsForRemoval.isEmpty()) {
                    for (CommerceItem ci : commerceItemsForRemoval) {
                        try {
                            getCommerceItemManager().removeItemFromOrder(pOrder, ci.getId());
                            result[0] = true;
                        } catch (CommerceException e) {
                            if (isLoggingError()) {
                                logError(e.getMessage());
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Removes all commerce items from order in case of store is changed
     *
     * @param pOrder parameter
     * @return
     */
    public boolean clearOrder(Order pOrder) {
        TransactionDemarcation td = new TransactionDemarcation();
        boolean clearResult = true;
        int clearAttemptsCounter = 0;
        try {
            td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
            synchronized (pOrder) {
                if (pOrder != null) {
                    List<CommerceItem> commerceItems = pOrder.getCommerceItems();
                    if ((commerceItems != null) && !commerceItems.isEmpty() && (getCommerceItemManager() != null)) {
                        for (; commerceItems.size() > 0; ) {
                            try {
                                getCommerceItemManager().removeItemFromOrder(pOrder, commerceItems.get(0).getId());
                            } catch (CommerceException e) {
                                clearAttemptsCounter++;
                                if (clearAttemptsCounter > clearOrderMaxAttempts) {
                                    clearResult = false;
                                    if (isLoggingError()) {
                                        logError(e.getMessage());
                                    }
                                    break;
                                }
                            }
                        }
                        return clearResult;
                    }
                }
            }
        } catch (Exception e) {
            try {
                getTransactionManager().getTransaction().setRollbackOnly();
            } catch (javax.transaction.SystemException se) {
                if (isLoggingError()) {
                    logError(se);
                }
            }
        } finally {
            try {
                td.end();
            } catch (TransactionDemarcationException e) {
                if (isLoggingError()) {
                    logError(e);
                }
            }
        }

        return false;
    }

    /**
     * Returns order's discount amount.
     *
     * @param order parameter to set.
     * @return orderDiscount property.
     */
    public double getOrderDiscount(Order order) {
        double orderDiscount = 0.0;
        OrderPriceInfo orderPriceInfo = order.getPriceInfo();
        if ((orderPriceInfo != null) && (orderPriceInfo.getAdjustments() != null)) {
            for (PricingAdjustment pa : (List<PricingAdjustment>) orderPriceInfo.getAdjustments()) {
                double tmpAmount = pa.getTotalAdjustment();
                if (tmpAmount < 0) {
                    orderDiscount += tmpAmount;
                }
            }
        }
        return orderDiscount;
    }

    /**
     * Returns discount amount for all items in Order.
     *
     * @param order parameter to set.
     * @return itemsDiscount property.
     */
    public double getItemsDiscount(Order order) {
        double discount = 0.0;

        for (ShippingGroup shippingGroup : (List<ShippingGroup>) order.getShippingGroups()) {
            for (CommerceItemRelationship cir :
                    (List<CommerceItemRelationship>) shippingGroup.getCommerceItemRelationships()) {
                ItemPriceInfo info = cir.getCommerceItem().getPriceInfo();
                if ((info != null) && (info.getAdjustments() != null)) {
                    boolean isLocalPriceApplied = false;
                    if (info instanceof CastItemPriceInfo) {
                        isLocalPriceApplied = ((CastItemPriceInfo) info).isLocalPriceApplied();
                    }
                    for (PricingAdjustment pa : (List<PricingAdjustment>) info.getAdjustments()) {
                        double tmpAmount = pa.getTotalAdjustment();
                        String adjDescr = pa.getAdjustmentDescription();
                        if (tmpAmount < 0) {
                            //web sale price 
                            if (SALE_PRICE.equalsIgnoreCase(adjDescr) &&
                                    info instanceof com.castorama.pricing.CastItemPriceInfo &&
                                    ((com.castorama.pricing.CastItemPriceInfo) info).isOnSale() &&
                                    ((com.castorama.pricing.CastItemPriceInfo) info).getOnSaleDiscountDisplay()) {

                                discount += tmpAmount;
                                continue;
                            }
                            //local sale price
                            if (isLocalPriceApplied && SALE_PRICE_LOCAL_DESCRIPTION.equalsIgnoreCase(adjDescr) &&
                                    info instanceof com.castorama.pricing.CastItemPriceInfo &&
                                    ((com.castorama.pricing.CastItemPriceInfo) info).getOnSaleDiscountDisplay()) {

                                discount += tmpAmount;
                                continue;
                            }
                            // promoutions
                            if (adjDescr != null && !adjDescr.contains(SALE_PRICE) && !adjDescr.contains(LIST_PRICE)) {
                                discount += tmpAmount;
                            }
                        }
                    }
                }
            }
        }
        return discount;
    }

    /**
     * Returns two lists with merged relationships for two shipping groups
     * in case when shipping groups have the same delivery method.
     *
     * @param localGroup parameter to set.
     * @param webGroup   parameter to set
     * @return itemsDiscount property.
     */
    public ArrayList<ArrayList<CommerceItemRelationship>> calculateGroups(ShippingGroup webGroup, ShippingGroup localGroup) {
        ArrayList<CommerceItemRelationship> web = new ArrayList<CommerceItemRelationship>();
        ArrayList<CommerceItemRelationship> local = new ArrayList<CommerceItemRelationship>();
        ArrayList<ArrayList<CommerceItemRelationship>> result = new ArrayList<ArrayList<CommerceItemRelationship>>();
        result.add(web);
        result.add(local);
        if (webGroup.getShippingMethod().equalsIgnoreCase(localGroup.getShippingMethod())) {
            List<CommerceItemRelationship> webRelationships = webGroup.getCommerceItemRelationships();
            List<CommerceItemRelationship> localRelationships = localGroup.getCommerceItemRelationships();
            Set<GSAItem> skus = new TreeSet<GSAItem>();
            for (CommerceItemRelationship leftRel : webRelationships) {
                skus.add(getSkuByRelationship(leftRel));
            }
            for (CommerceItemRelationship rightRel : localRelationships) {
                skus.add(getSkuByRelationship(rightRel));
            }
            for (Object obj : skus) {
                if (obj != null) {
                    String productId = ((RepositoryItem) obj).getRepositoryId();
                    web.add(getRelationshipByProductId(webRelationships, productId));
                    local.add(getRelationshipByProductId(localRelationships, productId));
                } else {
                    if (isLoggingError()) {
                        logError("Error in calculateGroups method: obj = null.");
                    }
                }
            }

        }
        return result;
    }

    /**
     * Returns relationship from list by product id
     *
     * @param relationships
     * @param skuId
     */
    private CommerceItemRelationship getRelationshipByProductId(List<CommerceItemRelationship> relationships,
                                                                String skuId) {
        for (CommerceItemRelationship rel : relationships) {
            if (skuId != null && skuId.equals(getSkuByRelationship(rel).getRepositoryId())) {
                return rel;
            }
        }
        return null;
    }

    /**
     * Returns sku by relationship
     *
     * @param relationship
     */
    private GSAItem getSkuByRelationship(CommerceItemRelationship relationship) {
        if (relationship != null) {
            GSAItem sku = (GSAItem) relationship.getCommerceItem().getAuxiliaryData().getCatalogRef();
            if (sku == null) {
                sku = (GSAItem) relationship.getCommerceItem().getAuxiliaryData().getProductRef();
            }
            return sku;
        }
        return null;
    }

    /**
     * Remove items from order1 which were payen in order2. This functionality was added
     * to support merge functionality for Click and Collect project.
     *
     * @param order1 - from this order skus will be removed
     * @param order2 - skus from this order will be removed from order1
     */
    public void removePayedItems(CastOrderImpl order1, CastOrderImpl order2) {
        if (order1 == null)
            return;
        if (order2 == null)
            return;
        List shippingGroups1 = order1.getShippingGroups();
        ShippingGroup shippingGroup1 = null;
        if (shippingGroups1 != null && shippingGroups1.size() > 0 && shippingGroups1.get(0) instanceof ShippingGroup) {
            shippingGroup1 = (ShippingGroup) shippingGroups1.get(0);
        } else {
            return;
        }

        List shippingGroups2 = order2.getShippingGroups();
        ShippingGroup shippingGroup2 = null;
        if (shippingGroups2 != null && shippingGroups2.size() > 0 && shippingGroups2.get(0) instanceof ShippingGroup) {
            shippingGroup2 = (ShippingGroup) shippingGroups2.get(0);
        } else {
            return;
        }

        ArrayList<ArrayList<CommerceItemRelationship>> groups = calculateGroups(shippingGroup1, shippingGroup2);

        ArrayList<CommerceItemRelationship> from = groups.get(0);
        ArrayList<CommerceItemRelationship> which = groups.get(1);
        for (int i = 0; i < from.size(); i++) {
            CommerceItemRelationship ciFrom = from.get(i);
            if (ciFrom == null)
                continue;
            CommerceItemRelationship ciWhich = which.get(i);
            if (ciWhich == null)
                continue;
            String itemId = ciFrom.getCommerceItem().getId();
            try {
                // remove item from order
                getCommerceItemManager().removeItemFromOrder(order1, itemId);
            } catch (CommerceException e) {
                if (isLoggingError()) {
                    logError("Exception when order1.id=" + order1.getId() + ", order2.id=" + order2.getId(), e);
                }
            }
        }

        return;
    }
    
    /**
     * 
     * @param order
     * @return - array of PDF files to attach to the e-mail
     */
    public File[] createEmailAttachments(CastOrderImpl order){
    	if(null == order){
    		return null;
    	}
    	List<File> resultList = new ArrayList<File>();
    	String baseURL = castConfiguration.getBaseURL();
    	String destFolder = castConfiguration.getTempFilesDestFolder();
    	String orderId = order.getId();
    	String profileId = order.getProfileId();
    	boolean isFileCreated = false;
    	//invoice
    	String invoiceReportFilepath = buildFilePath(castConfiguration.getInvoicePDFPrefix(), orderId, castConfiguration.getInvoicePDFPostfix(), destFolder);
    	String invoiceReportURL = String.format(castConfiguration.getInvoiceUrlTemplatePath(), orderId, profileId);
    	isFileCreated = pdfTools.createFileFromURL(invoiceReportURL, invoiceReportFilepath, baseURL);
    	if(isFileCreated){
    		resultList.add(new File(invoiceReportFilepath));
    		isFileCreated = false;
    	}
    	//terms and conditions
    	String cgvReportFilepath = buildFilePath(castConfiguration.getCgvFilePrefix(), orderId, castConfiguration.getCgvFilePostfix(), destFolder);  	
    	isFileCreated = pdfTools.writeCGVFile(cgvReportFilepath);
    	if(isFileCreated){
    		resultList.add(new File(cgvReportFilepath));
    		isFileCreated = false;
    	}
    	//retraction form
    	String retrFormFilepath = buildFilePath(castConfiguration.getRetractionFormPrefix(), orderId, castConfiguration.getRetractionFormPostfix(), destFolder);
    	String retrFormURL = String.format(castConfiguration.getRetractionFormTemplatePath(), orderId, profileId);
    	isFileCreated = pdfTools.createFileFromURL(retrFormURL, retrFormFilepath, baseURL);
    	if(isFileCreated){
    		resultList.add(new File(retrFormFilepath));
    	}  	
    	
    	return resultList.toArray(new File[resultList.size()]);
    }
    
    /**
     * build filepath to store temporary PDF files
     * @param prefix - file's prefix
     * @param orderId
     * @param postfix - file's postfix
     * @param destinationFolder - destination folder to store PDF files
     * @return - full filepath
     */
    private String buildFilePath(String prefix, String orderId, String postfix, String destinationFolder){
    	StringBuffer sb = new StringBuffer();
    	if(!StringUtils.isEmpty(destinationFolder)){
        	sb.append(destinationFolder);    		
    		if(!destinationFolder.endsWith("/")){
    			sb.append("/");
    		}
    	}
    	if(!StringUtils.isEmpty(prefix)){
    		sb.append(prefix);
    	}
    	sb.append(orderId);
    	if(!StringUtils.isEmpty(postfix)){
    		sb.append(postfix);
    	}
    	sb.append(PDF_EXTENSION);
    	
    	return sb.toString();
    }
    
}
