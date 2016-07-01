/**
 * 
 */
package com.castorama.droplet;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import com.castorama.pricing.CastItemPriceInfo;

import atg.adapter.gsa.ChangeAwareList;
import atg.adapter.gsa.GSAItem;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingTools;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import static com.castorama.droplet.CastoDropletsPricingTools.*;

/**
 * @author MK
 *
 */
public class PriceBlockDataGeneratorDroplet extends DynamoServlet {

    /** OUTPUT constant. */
    public static final String OUTPUT = "output";
    
    /** ERROR constant. */
    public static final String ERROR = "error";

    private PricingTools mPricingTools;

    private CastoDropletsPricingTools mDropletsPricingTools;

    /**
     *
     * ToDo: DOCUMENT ME!
     *
     * @param  pRequest  parameter
     * @param  pResponse parameter
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    @SuppressWarnings("unused")
    @Override public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                           throws ServletException, IOException {

        Object productObject = pRequest.getObjectParameter("product");
        Object skuObject = pRequest.getObjectParameter("sku");
        Object pageTypeObject = pRequest.getObjectParameter("pageType");
        Object printVersionObject = pRequest.getObjectParameter("printVersion");
        Object tauxTVAObject = pRequest.getObjectParameter("tauxTVA");
        Object skuIdObject = pRequest.getObjectParameter("skuId");
        Object featuredSkuIdObject = pRequest.getObjectParameter("featuredSkuId");
        Object featuredProductObject = pRequest.getObjectParameter("featuredProduct");
        Object storeIsCCObject = pRequest.getObjectParameter("storeIsCC");
        Object storeObject = pRequest.getObjectParameter("store");
        Object svAvailableMapObject = pRequest.getObjectParameter("svAvailableMap");
        RepositoryItem profile = (RepositoryItem)pRequest.getObjectParameter("profile");

        if (!checkIsGSAItem(productObject)) return;
        //check sku class
        if (!checkIsGSAItem(skuObject)) return;
        if (!checkIsGSAItem(storeObject)) return;

        GSAItem product = (GSAItem) productObject;
        GSAItem sku = (GSAItem) skuObject;
        String pageType = (String) pageTypeObject;
        boolean displayPrintVersion = ((Boolean)printVersionObject)!=null?((Boolean)printVersionObject).booleanValue():false;
        float tauxTVA = ((Float)tauxTVAObject)!=null?((Float)tauxTVAObject).floatValue():0f;
        String skuId =  (String) skuIdObject;
        String featuredSkuId =  (String) featuredSkuIdObject;
        boolean featuredProduct =  featuredProductObject!=null?true:false;
        boolean storeIsCC = storeIsCCObject != null?(Boolean) storeIsCCObject:false;
        GSAItem store =  (GSAItem) storeObject;
        Map<Integer, Integer> svAvailableMap =  (Map<Integer, Integer>) svAvailableMapObject; 
        
        List childSKUs = (List) product.getPropertyValue("childSKUs");
        boolean multiSku = childSKUs.size() > 1;
        String realId = sku.getRepositoryId();


        //requiredProperties
        String productType = (String) product.getPropertyValue("type");

        ItemPriceInfo mainSkuPriceInfo = null;
        try {
            mainSkuPriceInfo = getPricingTools().calculatePrice(product, sku, 1, false, profile, getPricingTools().getDefaultLocale(), null);
//                    (product, sku, 1);
        } catch (PricingException e) {
            // TODO Auto-generated catch block
            if (isLoggingError()){
                logError(e);
            }
            return;
        }

        double listPrice = 0d;
        double salePrice = 0d;
        double cardPrice = 0d;
        double discount = 0d;
        boolean displayOnDiscount = false;
        boolean localPriceApplied = ((CastItemPriceInfo)mainSkuPriceInfo).isLocalPriceApplied();
        boolean isValidByDateCardPrice = false;
        boolean showGPDiscount = false;
        boolean useDiscountImage = false;
//        boolean showPercent = false;
        double displayTaxFreePrice = 0d;
//        boolean showD3e = false;
        boolean displayShowEconomize = false;
        double displayEconomizeAmount = 0d;
        boolean displayShowPUPUVPrice = false;
        boolean displayShowCardPrice = false;
        double displayPUPUVPrice = 0d;
        double salePriceFract = 0d;
        double listPriceFract = 0d;
        double displayListPrice = 0d;
        double displaySalePrice = 0d;
        double displayCardPrice = 0d;
        double displayCardPriceByM2 = 0d;
        double displayOneM2Price = 0d;
        String displayLibelle = null;
        double displayAllTaxIncludedPrice = 0d;
        boolean showDiscount = false;
        //MK is duplicate logic - removed
        boolean displayShowCastaCardPdPng = false;
        boolean showCardPrice = false;
        boolean displayShowOffresCarteCastoramaLink = false;
        boolean displayShowFreeShippingMessage = false;
        boolean displaySoumisaD3E = false;
        float displayEcoTax = 0f;
        

        //group product logic
        if (productType != null && productType.equals("casto-grouped-product")){
            double groupedTaxFreePrice = 0d;
            double linkedSkusAmount = 0d;
            double groupedAverageTaxTVA = 0d;
            int groupedTotalItemCount = 0;
            ChangeAwareList bundleLinks =  (ChangeAwareList) sku.getPropertyValue("bundleLinks");
            for (Object skuLinkObject: bundleLinks){

                if(!checkIsGSAItem(skuLinkObject)) return;
                GSAItem skuLink = (GSAItem) skuLinkObject;

                //create local variables
                long linkedSkuQuantity = ((Long)skuLink.getPropertyValue("quantity")).longValue();
                Object linkedSkuObject = skuLink.getPropertyValue("item");
                if (!checkIsGSAItem(linkedSkuObject)) return;
                GSAItem linkedSku = (GSAItem) linkedSkuObject;
                float linkedSkuTaxTVA = linkedSku.getPropertyValue("tauxTVA")!=null?((Float)linkedSku.getPropertyValue("tauxTVA")).floatValue():0f;
                groupedAverageTaxTVA+=linkedSkuTaxTVA*linkedSkuQuantity;
                groupedTotalItemCount+=linkedSkuQuantity;
                
                try {
                    ItemPriceInfo linkedSkuPriceInfo = getPricingTools().calculatePrice(product, linkedSku, linkedSkuQuantity);
                    if (!(linkedSkuPriceInfo instanceof CastItemPriceInfo)){
                        if (isLoggingError()){
                            logError("linkedSkuPriceInfo isn't instance of CastItemPriceInfo. linkedSkuPriceInfo is " + linkedSkuPriceInfo.getClass());
                        }
                        return;
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
                    linkedSkusAmount+= linkedSkuAmount * linkedSkuQuantity;
                    double linkedSkuTFPrice = calculateTaxFreePrice(linkedSkuTaxTVA, linkedSkuAmount);
                    groupedTaxFreePrice = groupedTaxFreePrice + linkedSkuTFPrice * linkedSkuQuantity;


                } catch (PricingException e) {
                    if (isLoggingError()){
                        logError(e);
                    }
                    pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
                }
                

            }
            groupedAverageTaxTVA = groupedAverageTaxTVA / groupedTotalItemCount;

            salePrice = mainSkuPriceInfo.getAmount();
            if (salePrice >= listPrice) {
                showGPDiscount = false;
            }

            Map<String, Object> linkedParams = getDropletsPricingTools().calculateForCastPriceDroplet(new BigDecimal(linkedSkusAmount), 
                    new BigDecimal(salePrice), null, false, false, localPriceApplied);

            discount = linkedParams.get(ECONOMY)!=null?((BigDecimal) linkedParams.get(ECONOMY)).doubleValue(): 0d;
            double groupedTFDiscount = 0d;
            if (discount != 0d){
                groupedTFDiscount =discount/(1+(groupedAverageTaxTVA/100));
            }

            groupedTaxFreePrice = groupedTaxFreePrice - groupedTFDiscount;
            displayTaxFreePrice = groupedTaxFreePrice;

        }

        listPrice = mainSkuPriceInfo.getListPrice();
        salePrice = mainSkuPriceInfo.getAmount();
        cardPrice = ((CastItemPriceInfo)mainSkuPriceInfo).getCardPrice();
        isValidByDateCardPrice = ((CastItemPriceInfo)mainSkuPriceInfo).getIsValidByDateCardPrice();
        boolean hideCardPrice = multiSku && 
                (
                        ((skuId==null || skuId.length()==0) && (featuredSkuId==null || featuredSkuId.length()==0)) 
                        || !(
                                realId.equals(skuId) 
                                || ( 
                                        realId.equals(featuredSkuId) 
                                        && featuredProduct
                                    )
                            )
                       );
        boolean multiSkuANDSkuNotSelected = multiSku && (skuId== null || skuId.length() == 0) && (featuredSkuId== null || featuredSkuId.length() == 0 );
        Map<String, Object> mainSkuParams = getDropletsPricingTools().calculateForCastPriceDroplet(new BigDecimal(listPrice), 
                new BigDecimal(salePrice), null, false, false, localPriceApplied);

        String percent = (String) mainSkuParams.get(PERCENT);
        discount = (mainSkuParams.get(ECONOMY))!= null?((BigDecimal) mainSkuParams.get(ECONOMY)).doubleValue():0d;
        displayOnDiscount = (mainSkuParams.get(ON_DISCOUNT)!=null)?((Boolean)mainSkuParams.get(ON_DISCOUNT)).booleanValue():false;
        String CUP = (String) sku.getPropertyValue("CUP");
        boolean displayShowM2PriceFirst = sku.getPropertyValue("showM2PriceFirst")!=null?(Boolean) sku.getPropertyValue("showM2PriceFirst"):false;
        displayShowM2PriceFirst = "m2".equalsIgnoreCase(CUP) && displayShowM2PriceFirst;
        boolean displayShowM2Price = CUP != null;//"m2".equalsIgnoreCase(CUP);
        float displayPUPUV = getFloatValue(sku, "PUPUV");
        Boolean onSaleDiscountDisplayParam = ((CastItemPriceInfo)mainSkuPriceInfo).getOnSaleDiscountDisplay();
        boolean onSaleDiscountDisplay = onSaleDiscountDisplayParam != null ? onSaleDiscountDisplayParam.booleanValue() : false;
        boolean onSaleParam = ((CastItemPriceInfo)mainSkuPriceInfo).isOnSale();
        displaySoumisaD3E = sku.getPropertyValue("SoumisaD3E")!=null?((Boolean)sku.getPropertyValue("SoumisaD3E")).booleanValue():false;
        String currencyCode = mainSkuPriceInfo.getCurrencyCode();

        String displayPercent = null;
        boolean displayShowDiscount = false;
        boolean isValidCardPrice = cardPrice > 0 && isValidByDateCardPrice && !hideCardPrice;
        boolean isProductDetailsPageType = pageType != null && pageType.length()>0 && pageType.equals("productDetails");
        boolean isNotGroupProductType = productType != null && !productType.equals("casto-grouped-product");
        if (displaySoumisaD3E) {
            displayEcoTax = getFloatValue(sku, "ecoTaxeEnEuro");
        }
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

                    displayShowEconomize = true;
                    if (isProductDetailsPageType) {
                        displayAllTaxIncludedPrice = salePrice;
                        if (isNotGroupProductType) {
                            displayTaxFreePrice = calculateTaxFreePrice(tauxTVA, salePrice);
                        }
                    }
                    if (percent != null && percent.length() > 0) {
                        displayPercent = percent;
                    }
                    if (displayShowM2PriceFirst) {
                        Map<String, Object> params = getDropletsPricingTools().calculateForUnitPriceCodeLookupDroplet(sku, listPrice);
                        displayListPrice = getDoubleValue(params);
                        displayLibelle = (String) params.get(LIBELLE);
                        params = getDropletsPricingTools().calculateForUnitPriceCodeLookupDroplet(sku, salePrice);
                        displaySalePrice = getDoubleValue(params);
                        displayLibelle = (String) params.get(LIBELLE);
                        displayShowPUPUVPrice = true;
                        displayPUPUVPrice = salePrice;

                        displayEconomizeAmount = discount / displayPUPUV;
                    } else {
                        displayListPrice = listPrice;
                        displaySalePrice = salePrice;
                        displayEconomizeAmount = discount;
                        if (displayShowM2Price) {
                            Map<String, Object> params = getDropletsPricingTools().calculateForUnitPriceCodeLookupDroplet(sku, salePrice);
                            displayOneM2Price = getDoubleValue(params);
                            displayLibelle = (String) params.get(LIBELLE);
                        }
                    }

                    if (isValidCardPrice) {
                        displayShowCardPrice = true;
                        displayCardPrice = cardPrice;
                        if (displayShowM2Price) {
                            Map<String, Object> params = getDropletsPricingTools().calculateForUnitPriceCodeLookupDroplet(sku, cardPrice);
                            displayCardPriceByM2 = getDoubleValue(params);
                            displayLibelle = (String) params.get(LIBELLE);
                        }

                        if (displayPrintVersion) {
                            displayShowCastaCardPdPng = true;
                        } else {
                            displayShowOffresCarteCastoramaLink = true;
                        }
                    }

                } // end if displayShowDiscount == true
                else 
                {
                    displayListPrice = salePrice;
                    if (isProductDetailsPageType) {
                        displayAllTaxIncludedPrice = salePrice;
                        if (isNotGroupProductType) {
                            displayTaxFreePrice = calculateTaxFreePrice(tauxTVA, salePrice);
                        }

                    }
                    Map<String, Object> params = getDropletsPricingTools().calculateForUnitPriceCodeLookupDroplet(sku, salePrice);
                    displayOneM2Price = getDoubleValue(params);
                    displayLibelle = (String) params.get(LIBELLE);
                    if (displayShowM2PriceFirst) {
                        displayShowPUPUVPrice = true;
                        displayPUPUVPrice = salePrice;
                    }

                    if (isValidCardPrice) {
                        displayShowCardPrice = true;
                        displayCardPrice = cardPrice;
                        params = getDropletsPricingTools().calculateForUnitPriceCodeLookupDroplet(sku, cardPrice);
                        displayCardPriceByM2 = getDoubleValue(params);
                        displayLibelle = (String) params.get(LIBELLE);
                        if (displayPrintVersion) {
                            displayShowCastaCardPdPng = true;
                        } else {
                            displayShowOffresCarteCastoramaLink = true;
                        }
                    }

                } // end else for if displayShowDiscount == true

            } //end if onDiscount ==  true
            else 
            {

                displayListPrice = listPrice;
                if (isProductDetailsPageType) {
                    displayAllTaxIncludedPrice = listPrice;
                    if (isNotGroupProductType) {
                        displayTaxFreePrice = calculateTaxFreePrice(tauxTVA, listPrice);
                    }
                }

                Map<String, Object> params;
                if (displayShowM2Price) {
                    params = getDropletsPricingTools().calculateForUnitPriceCodeLookupDroplet(sku, listPrice);
                    displayOneM2Price = getDoubleValue(params);
                    displayLibelle = (String) params.get(LIBELLE);
                    if (displayShowM2PriceFirst) {
                        displayShowPUPUVPrice = true;
                        displayPUPUVPrice = listPrice;
                    }
                }

                if (isValidCardPrice) {
                    displayShowCardPrice = true;
                    displayCardPrice = cardPrice;
                    params = getDropletsPricingTools().calculateForUnitPriceCodeLookupDroplet(sku, cardPrice);
                    displayCardPriceByM2 = getDoubleValue(params);
                    displayLibelle = (String) params.get(LIBELLE);
                    if (displayPrintVersion) {
                        displayShowCastaCardPdPng = true;
                    } else {
                        displayShowOffresCarteCastoramaLink = true;
                    }
                }
                
            } // end else for if onDiscount ==  true
            //check show free shipping message 
            if (!storeIsCC && !multiSkuANDSkuNotSelected) {
                Map<String, Object> freeShippingParams = getDropletsPricingTools().calculateForFreeShippingDroplet(sku, store, svAvailableMap);
                if (freeShippingParams != null) {
                    displayShowFreeShippingMessage = true;
                }
            }



            pRequest.setParameter("displayOnSale", displayOnDiscount);
            pRequest.setParameter("displayShowDiscount", displayShowDiscount);
            pRequest.setParameter("displayShowM2PriceFirst", displayShowM2PriceFirst);
            pRequest.setParameter("displayShowM2Price", displayShowM2Price);
            pRequest.setParameter("displayPercent", displayPercent);
            pRequest.setParameter("displayPrintVersion", displayPrintVersion);
            pRequest.setParameter("displayListPrice", displayListPrice);
            pRequest.setParameter("displaySalePrice", displaySalePrice);
            pRequest.setParameter("displayCardPrice", displayCardPrice);
            pRequest.setParameter("displayCardPriceByM2", displayCardPriceByM2);
            pRequest.setParameter("displayLibelle", displayLibelle);
            pRequest.setParameter("displayAllTaxIncludedPrice", displayAllTaxIncludedPrice);
            pRequest.setParameter("displayTaxFreePrice", displayTaxFreePrice);
            pRequest.setParameter("displayOneM2Price", displayOneM2Price);
            pRequest.setParameter("displayShowD3e", displaySoumisaD3E);
            pRequest.setParameter("displayEcoTax", displayEcoTax);
            pRequest.setParameter("displayShowEconomize", displayShowEconomize);
            pRequest.setParameter("displayEconomizeAmount", displayEconomizeAmount);
            pRequest.setParameter("displayShowPUPUVPrice", displayShowPUPUVPrice);
            pRequest.setParameter("displayPUPUVPrice", displayPUPUVPrice);
            pRequest.setParameter("displayPUPUV", displayPUPUV);
            pRequest.setParameter("displayShowCardPrice", displayShowCardPrice);
            pRequest.setParameter("displayShowCastaCardPdPng", displayShowCastaCardPdPng);
            pRequest.setParameter("displayShowOffresCarteCastoramaLink", displayShowOffresCarteCastoramaLink);
            pRequest.setParameter("displayShowFreeShippingMessage", displayShowFreeShippingMessage);
            pRequest.setParameter("displayCurrencyCode", currencyCode);

            pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        
        } catch (RepositoryException e) {
            if (isLoggingError()) {
                logError(e);
            }
            pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
        }

    }

    /**
     * @param tauxTVA
     * @param salePrice
     * @return
     */
    private double calculateTaxFreePrice(float tauxTVA, double salePrice) {
        return salePrice / (1 + (tauxTVA / 100));
    }

    /**
     * @param sku
     * @return
     */
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

    /**
     * @param obj
     */
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

    /**
     * @return the pricingTools
     */
    public PricingTools getPricingTools() {
        return mPricingTools;
    }

    /**
     * @param pPricingTools the pricingTools to set
     */
    public void setPricingTools(PricingTools pPricingTools) {
        this.mPricingTools = pPricingTools;
    }

    /**
     * @return the dropletsPricingTools
     */
    public CastoDropletsPricingTools getDropletsPricingTools() {
        return mDropletsPricingTools;
    }

    /**
     * @param dropletsPricingTools the dropletsPricingTools to set
     */
    public void setDropletsPricingTools(CastoDropletsPricingTools dropletsPricingTools) {
        this.mDropletsPricingTools = dropletsPricingTools;
    }

}
