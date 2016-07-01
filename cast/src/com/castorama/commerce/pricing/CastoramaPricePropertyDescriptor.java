package com.castorama.commerce.pricing;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.TransactionManager;

import atg.commerce.pricing.priceLists.PriceListException;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;

/**
 * 
 * @author
 */
public class CastoramaPricePropertyDescriptor extends RepositoryPropertyDescriptor {

    private static final long serialVersionUID = -5025140173797869799L;
    
    private static final String NULL = "NULL";
    
    private static final String PLR_LOOKUP_PATH = "dynamo:/atg/commerce/pricing/priceLists/PriceLists";
    private static final String PLM_LOOKUP_PATH = "dynamo:/atg/commerce/pricing/priceLists/PriceListManager";
    private static final String TM_LOOKUP_PATH = "dynamo:/atg/dynamo/transaction/TransactionManager";
    
    private static final String PLR_PL_ID = "cardPrices";
    private static final String PLR_LP = "listPrice";
    private static final String PLR_P = "price";
    
    
    @Override
    public Object getPropertyValue(RepositoryItemImpl pItem, Object pValue) {
        String skuId = pItem.getRepositoryId();
        if (skuId.contains(":")) {
            skuId = skuId.substring(0, skuId.indexOf(':'));
        }
        
        Double castoramaPrice = null;
        try {
            javax.naming.InitialContext ctx = new InitialContext();
            PriceListManager plm = (PriceListManager) ctx.lookup(PLM_LOOKUP_PATH);
            RepositoryItem cardPricesPL = plm.getPriceList(PLR_PL_ID);
            if (cardPricesPL != null) {
                RepositoryItem price = plm.getPrice(cardPricesPL, null, skuId, false);
                if (price != null) {
                    castoramaPrice = (Double) price.getPropertyValue(PLR_LP);
                }
            }
        } catch (PriceListException ple) {
        } catch (NamingException ne) {
        }
        
        return castoramaPrice;
    }
    
    @Override
    public void setPropertyValue(RepositoryItemImpl pItem, Object pValue) {
        String skuId = pItem.getRepositoryId();
        if (skuId.contains(":")) {
            skuId = skuId.substring(0, skuId.indexOf(':'));
        }
        
        try {
            javax.naming.InitialContext ctx = new InitialContext();
            PriceListManager plm = (PriceListManager) ctx.lookup(PLM_LOOKUP_PATH);
            MutableRepository priceLists = (MutableRepository) ctx.lookup(PLR_LOOKUP_PATH);
            TransactionManager transactionManager = (TransactionManager) ctx.lookup(TM_LOOKUP_PATH);
            
            String priceId = null;
            RepositoryItem cardPricesPL = plm.getPriceList(PLR_PL_ID);
            if (cardPricesPL != null) {
                RepositoryItem price = plm.getPrice(cardPricesPL, null, skuId, false);
                if (price != null) {
                    priceId = price.getRepositoryId();
                }
            }
                
            boolean rollback = false;
            TransactionDemarcation trd = new TransactionDemarcation();
            try {
                trd.begin(transactionManager, TransactionDemarcation.REQUIRES_NEW);
                
                if (NULL.equals(pValue.toString())) {
                    if (priceId != null) {
                        priceLists.removeItem(priceId, PLR_P);
                    }
                } else {
                    if (priceId != null) {
                        MutableRepositoryItem mutPrice = priceLists.getItemForUpdate(priceId, PLR_P);
                        mutPrice.setPropertyValue(PLR_LP, (Double) pValue);
                    } else {
                        if (cardPricesPL != null) {
                            plm.createListPrice(cardPricesPL, null, skuId, (Double) pValue);
                        }
                    }
                }
            } catch (Exception e) {
                rollback = true;
            } finally {
                try {
                    trd.end(rollback);
                } catch (TransactionDemarcationException e) { }
            }
        } catch (PriceListException ple) {
        } catch (NamingException ne) {
        }
            
        super.setPropertyValue(pItem, pValue);
    }
    
    @Override
    public boolean isQueryable() {
        return false;
    }

    @SuppressWarnings("unchecked")
    public Class getPropertyType() {
        return Double.class;
    }

    /**
     * Perform type checking.
     */
    @SuppressWarnings("unchecked")
    public void setPropertyType(Class pClass) {
        if (pClass != Double.class)
            throw new IllegalArgumentException("Castorama price properties must be Double");
        super.setPropertyType(pClass);
    }

    @SuppressWarnings("unchecked")
    public void setComponentPropertyType(Class pClass) {
        if (pClass != null)
            throw new IllegalArgumentException("Castorama price properties must be scalars");
    }

    public void setPropertyItemDescriptor(RepositoryItemDescriptor pDesc) {
        if (pDesc != null)
            throw new IllegalArgumentException("Castorama price properties must be Double");
    }

    public void setComponentItemDescriptor(RepositoryItemDescriptor pDesc) {
        if (pDesc != null)
            throw new IllegalArgumentException("Castorama price properties must be scalars");
    }
}
