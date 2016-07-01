package com.castorama.droplet;

import atg.adapter.gsa.ChangeAwareList;
import atg.adapter.gsa.ChangeAwareSet;
import atg.adapter.gsa.GSAItem;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingTools;
import atg.repository.*;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import com.castorama.pricing.CastItemPriceInfo;

import javax.servlet.ServletException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static com.castorama.droplet.CastoDropletsPricingTools.ON_DISCOUNT;
import static com.castorama.droplet.CastoDropletsPricingTools.PRICE_PER_UNITE;

/**
 * Created with IntelliJ IDEA.
 * User: casto_dev
 * Date: 2/18/16
 * Time: 8:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestPricesForExportDroplet extends DynamoServlet {

    private final static String LOCAL_PRICE_LIST = "localPriceList";
    private final static String CURRENT_LOCAL_STORE = "currentLocalStore";
    private final static String WRAPPED_CURRENT_LOCAL_STORE = "wrappedCurrentLocalStore";
    private final static String EXPORT_PROFILE_ID = "exp";
    private final static String EXPORT_FOR_STORE_MESSAGE = "Export for store: ";
    private final static String LOGIN = "login";
    private final static String PASSWORD = "password";
    private final static String USER = "user";


    private PricingTools pricingTools;
    private CastoDropletsPricingTools mDropletsPricingTools;
    private Repository productRepository;
    private Repository storeRepository;
    private Repository profileAdapterRepository;

    @Override
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        String skuId = pRequest.getParameter("skuId");
        String storeCode = pRequest.getParameter("storeCode");

        try {
            RepositoryItem sku = getProductRepository().getItem(skuId, "sku");

            Object rqlparams[] = new Object[1];
            rqlparams[0] = storeCode;
            RqlStatement getStoreRQL = RqlStatement.parseRqlStatement("storeId = ?0");
            RepositoryView storeView = getStoreRepository().getView("magasin");
            RepositoryItem[] storeList = getStoreRQL.executeQuery(storeView, rqlparams);
            RepositoryItem store = storeList[0];

            List<RepositoryItem> products = getParentProduct(sku);

            if (products != null && !products.isEmpty()) {

                RepositoryItem profile;
                RepositoryItem localPriceList = (RepositoryItem) store.getPropertyValue(LOCAL_PRICE_LIST);
                if (localPriceList != null) {
                    profile = generateProfile(store, localPriceList);
                } else {
                    profile = generateProfile(store);
                }
                List<Double> listPrices = new ArrayList<Double>(products.size());
                List<Double> salePrices = new ArrayList<Double>(products.size());
                for (int i = 0; i < products.size(); i++) {
                    double[] prices = calculatePrices(sku, products.get(i), profile);
                    listPrices.add(prices[0]);
                    salePrices.add(prices[1]);
                }
                pRequest.setParameter("listPrices", listPrices);
                pRequest.setParameter("salePrices", salePrices);
                pRequest.serviceParameter("output", pRequest, pResponse);
            } else {
                pRequest.serviceLocalParameter("empty", pRequest, pResponse);
            }
        } catch (RepositoryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private RepositoryItem generateProfile(RepositoryItem pStore) throws RepositoryException {
        MutableRepository mutableProfileRepo = (MutableRepository) getProfileAdapterRepository();
        MutableRepositoryItem mutableProfile = createExportProfile(pStore.getRepositoryId());
        mutableProfile.setPropertyValue(CURRENT_LOCAL_STORE, pStore);
        mutableProfile.setPropertyValue(WRAPPED_CURRENT_LOCAL_STORE, pStore);
        mutableProfileRepo.updateItem(mutableProfile);
        return mutableProfile;
    }

    private RepositoryItem generateProfile(RepositoryItem pStore, RepositoryItem localPriceList) throws RepositoryException {
        MutableRepository mutableProfileRepo = (MutableRepository) getProfileAdapterRepository();
        MutableRepositoryItem mutableProfile = createExportProfile(pStore.getRepositoryId());
        mutableProfile.setPropertyValue(CURRENT_LOCAL_STORE, pStore);
        mutableProfile.setPropertyValue(WRAPPED_CURRENT_LOCAL_STORE, pStore);
        mutableProfile.setPropertyValue(LOCAL_PRICE_LIST, localPriceList);
        mutableProfileRepo.updateItem(mutableProfile);
        return mutableProfile;
    }

    private List<RepositoryItem> getParentProduct (RepositoryItem sku) {
        if (sku != null) {
            ChangeAwareSet parentProductsSet = (ChangeAwareSet) sku.getPropertyValue("parentProducts");
            if (parentProductsSet != null) {
                Set<Object> parentProductsBaseSet = parentProductsSet.getBaseSet();
                List<RepositoryItem> parentProducts  = new ArrayList<RepositoryItem>(1);
                RepositoryItem parentProduct = null;
                Iterator<Object> iterator = parentProductsBaseSet.iterator();
                while (iterator.hasNext()) {
                    parentProduct = (RepositoryItem) iterator.next();
                    if (parentProduct != null && ((String)parentProduct.getPropertyValue("type")) != null && !((String)parentProduct.getPropertyValue("type")).equals("casto-pack")) {
                        parentProducts.add(parentProduct);
                    }
                }
                return parentProducts;
            }
        }
        return null;
    }

    private MutableRepositoryItem createExportProfile(String pStoreId) throws RepositoryException {
        MutableRepository mutableProfileRepo = (MutableRepository) getProfileAdapterRepository();
        MutableRepositoryItem mutableProfile = (MutableRepositoryItem) mutableProfileRepo.getItem(EXPORT_PROFILE_ID + pStoreId, USER);
        if (mutableProfile == null) {
            mutableProfile = mutableProfileRepo.createItem(EXPORT_PROFILE_ID + pStoreId, USER);
            mutableProfile.setPropertyValue(LOGIN, EXPORT_PROFILE_ID + pStoreId);
            mutableProfile.setPropertyValue(PASSWORD, EXPORT_PROFILE_ID + pStoreId);
            mutableProfileRepo.addItem(mutableProfile);
        }
        return mutableProfile;
    }

    private double[] calculatePrices(RepositoryItem pSku, RepositoryItem pProduct, RepositoryItem pProfile) throws ServletException, IOException {
        RepositoryItem profile = pProfile;

        GSAItem product = (GSAItem) pProduct;
        GSAItem sku = (GSAItem) pSku;

        //requiredProperties
        String productType = (String) product.getPropertyValue("type");

        ItemPriceInfo mainSkuPriceInfo = null;
        try {
            mainSkuPriceInfo = getPricingTools().calculatePrice(product, sku, 1, false, profile, getPricingTools().getDefaultLocale(), null);
//                    (product, sku, 1);
        } catch (PricingException e) {
            if (isLoggingError()){
                logError(e);
            }
            return null;
        }

        double listPrice = 0d;
        double salePrice = 0d;
        boolean displayOnDiscount = false;
        boolean localPriceApplied = ((CastItemPriceInfo)mainSkuPriceInfo).isLocalPriceApplied();
        boolean showGPDiscount = false;
//        boolean showPercent = false;
//        boolean showD3e = false;
        double displayListPrice = 0d;
        double displaySalePrice = 0d;

        //group product logic
        if (productType != null && productType.equals("casto-grouped-product")){
            ChangeAwareList bundleLinks =  (ChangeAwareList) sku.getPropertyValue("bundleLinks");
            for (Object skuLinkObject: bundleLinks){

                if(!checkIsGSAItem(skuLinkObject)) return null;
                GSAItem skuLink = (GSAItem) skuLinkObject;

                //create local variables
                long linkedSkuQuantity = ((Long)skuLink.getPropertyValue("quantity")).longValue();
                Object linkedSkuObject = skuLink.getPropertyValue("item");
                if (!checkIsGSAItem(linkedSkuObject)) return null;
                GSAItem linkedSku = (GSAItem) linkedSkuObject;
                try {
                    ItemPriceInfo linkedSkuPriceInfo = getPricingTools().calculatePrice(product, linkedSku, linkedSkuQuantity);
                    if (!(linkedSkuPriceInfo instanceof CastItemPriceInfo)){
                        if (isLoggingError()){
                            logError("linkedSkuPriceInfo isn't instance of CastItemPriceInfo. linkedSkuPriceInfo is " + linkedSkuPriceInfo.getClass());
                        }
                        return null;
                    }
                    CastItemPriceInfo castLinkedSkuPriceInfo = (CastItemPriceInfo) linkedSkuPriceInfo;
                    double linkedSkuLP = castLinkedSkuPriceInfo.getListPrice();
                    double linkedSkuAmount = castLinkedSkuPriceInfo.getAmount();
                    boolean linkedSkuOnSale = castLinkedSkuPriceInfo.isOnSale();
                    boolean linkedSkuOnSaleDiscountDisplay = castLinkedSkuPriceInfo.getOnSaleDiscountDisplay().booleanValue();

                    if ( linkedSkuAmount < linkedSkuLP && (!linkedSkuOnSale || linkedSkuOnSaleDiscountDisplay) ){
                        showGPDiscount=true;
                    }
                    if ( linkedSkuAmount < linkedSkuLP && linkedSkuOnSale && !linkedSkuOnSaleDiscountDisplay ) {
                        linkedSkuLP = linkedSkuAmount;
                    }

                    listPrice += linkedSkuLP * linkedSkuQuantity;

                } catch (PricingException e) {
                    if (isLoggingError()){
                        logError(e);
                    }
                }


            }

            salePrice = mainSkuPriceInfo.getAmount();
            if (salePrice >= listPrice) {
                showGPDiscount = false;
            }
        }

        listPrice = mainSkuPriceInfo.getListPrice();
        salePrice = mainSkuPriceInfo.getAmount();

        Map<String, Object> mainSkuParams = getDropletsPricingTools().calculateForCastPriceDroplet(new BigDecimal(listPrice),
                new BigDecimal(salePrice), null, false, false, localPriceApplied);

        displayOnDiscount = (mainSkuParams.get(ON_DISCOUNT)!=null)?((Boolean)mainSkuParams.get(ON_DISCOUNT)).booleanValue():false;
        String CUP = (String) sku.getPropertyValue("CUP");
        boolean displayShowM2PriceFirst = sku.getPropertyValue("showM2PriceFirst")!=null?(Boolean) sku.getPropertyValue("showM2PriceFirst"):false;
        displayShowM2PriceFirst = "m2".equalsIgnoreCase(CUP) && displayShowM2PriceFirst;
        Boolean onSaleDiscountDisplayParam = ((CastItemPriceInfo)mainSkuPriceInfo).getOnSaleDiscountDisplay();
        boolean onSaleDiscountDisplay = onSaleDiscountDisplayParam != null ? onSaleDiscountDisplayParam.booleanValue() : false;
        boolean onSaleParam = ((CastItemPriceInfo)mainSkuPriceInfo).isOnSale();

        boolean displayShowDiscount = false;
        try {
            if (displayOnDiscount) {
                boolean needToDisplayPromotion =
                        !(onSaleParam && !onSaleDiscountDisplay) // Promotion on regular product
                                || (productType != null && productType.equals("casto-pack")); // Promotion on pack

                displayShowDiscount = (salePrice < listPrice)
                        && ((onSaleParam && onSaleDiscountDisplay)
                        || (showGPDiscount)
                        || (mainSkuPriceInfo.isDiscounted() && needToDisplayPromotion)); // ATG Promotion

                if (displayShowDiscount) {

                    if (displayShowM2PriceFirst) {
                        Map<String, Object> params = getDropletsPricingTools().calculateForUnitPriceCodeLookupDroplet(sku, listPrice);
                        displayListPrice = getDoubleValue(params);
                        params = getDropletsPricingTools().calculateForUnitPriceCodeLookupDroplet(sku, salePrice);
                        displaySalePrice = getDoubleValue(params);
                    } else {
                        displayListPrice = listPrice;
                        displaySalePrice = salePrice;
                    }
                } // end if displayShowDiscount == true
                else
                {
                    displayListPrice = salePrice;
                } // end else for if displayShowDiscount == true

            } //end if onDiscount ==  true
            else
            {
                displayListPrice = listPrice;
            } // end else for if onDiscount ==  true
            double[] prices = {displayListPrice, displaySalePrice};
            return prices;



        } catch (RepositoryException e) {
            if (isLoggingError()) {
                logError(e);
            }
            return null;
        }
    }


    private float getFloatValue(GSAItem sku, String propertyName) {
        return sku.getPropertyValue(propertyName)!=null?(Float) sku.getPropertyValue(propertyName):0f;
    }

    /**
     * @param params
     * @return
     */
    private double getDoubleValue(Map<String, Object> params) {
        return params.get(PRICE_PER_UNITE)!=null?((Double) params.get(PRICE_PER_UNITE)).doubleValue():0d;
    }

    private boolean checkIsGSAItem(Object obj) {
        //TODO make more simple
        //check product class
        if (!(obj instanceof GSAItem)) {
            if (isLoggingError()){
                logError("Obj isn't instance of GSAItem. obj is " + (obj!=null?obj.getClass():null));
            }
            return false;
        }
        return true;
    }


    public CastoDropletsPricingTools getDropletsPricingTools() {
        return mDropletsPricingTools;
    }

    public void setDropletsPricingTools(CastoDropletsPricingTools mDropletsPricingTools) {
        this.mDropletsPricingTools = mDropletsPricingTools;
    }

    public PricingTools getPricingTools() {
        return pricingTools;
    }

    public void setPricingTools(PricingTools pricingTools) {
        this.pricingTools = pricingTools;
    }

    public Repository getProductRepository() {
        return productRepository;
    }

    public void setProductRepository(Repository productRepository) {
        this.productRepository = productRepository;
    }

    public Repository getStoreRepository() {
        return storeRepository;
    }

    public void setStoreRepository(Repository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public Repository getProfileAdapterRepository() {
        return profileAdapterRepository;
    }

    public void setProfileAdapterRepository(Repository profileAdapterRepository) {
        this.profileAdapterRepository = profileAdapterRepository;
    }
}
