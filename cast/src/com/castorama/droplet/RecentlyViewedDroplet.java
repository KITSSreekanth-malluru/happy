package com.castorama.droplet;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;

import com.castorama.commerce.catalog.CastCatalogTools;
import com.castorama.commerce.profile.SessionBean;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.OrderHolder;

import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import atg.userprofiling.Profile;

/**
 *
 * @author EPAM team
 */
public class RecentlyViewedDroplet extends DynamoServlet {
    /** HOW_MANY constant. */
    public static final String HOW_MANY = "howMany";

    /** INDEX_NAME constant. */
    public static final String INDEX_NAME = "indexName";

    /** ELEMENT_NAME constant. */
    public static final String ELEMENT_NAME = "elementName";

    /** OUTPUT_START constant. */
    public static final String OUTPUT_START = "outputStart";

    /** OUTPUT_END constant. */
    public static final String OUTPUT_END = "outputEnd";

    /** OUTPUT constant. */
    public static final String OUTPUT = "output";

    /** EMPTY constant. */
    public static final String EMPTY = "empty";

    /** INDEX constant. */
    public static final String INDEX = "index";

    /** COUNT constant. */
    public static final String COUNT = "count";

    /** ELEMENT constant. */
    public static final String ELEMENT = "element";

    /** catalogTools constant. */
    CastCatalogTools mCatalogTools;

    /** orderHolder constant. */
    private OrderHolder mOrderHolder;

    /** profile constant. */
    private Profile mProfile;

    /** Current session. */
    private SessionBean mSessionBean;

    /**
     *
     * @param  pRequest  parameter
     * @param  pResponse parameter
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        List out = new ArrayList();
        int count;
        try {
            count = Integer.parseInt((String) pRequest.getObjectParameter(HOW_MANY));
        } catch (Exception e) {
            count = Integer.MAX_VALUE;
        }

        CastCatalogTools catalogTools = getCatalogTools();
        try {
            List commerceItems = getOrderHolder().getCurrent().getCommerceItems();
            Set<String> alreadyAdded = new HashSet<String>(); 
            Collection productsViewed = (Collection) getProfile().getPropertyValue("productsViewed");
            if (null != productsViewed) {
            	// check products that were deleted from shopping cart
            	SessionBean sb = getSessionBean();
            	if(sb != null) {
        			List<String> productsDeleted = (List<String>)sb.getValues().get("productsDeleted");
        			if(productsDeleted != null) {
        				for(String prodId:productsDeleted) {
                            if (out.size() < count) {
	        					if(!alreadyAdded.contains(prodId) && productsViewed.contains(prodId)) {
	                                RepositoryItem product = catalogTools.findProduct(prodId);
	                                if ((null != product) && checkProductOnlineSale(product) &&
	                                        checkNotInOrder(commerceItems, product)) {
	                                    out.add(product);
	                                    alreadyAdded.add(prodId);
	                                }
	        					}
                            } else {
                                break;
                            }
        				}
        			}
            	}
            	// check all other viewed products
                for (Iterator it = productsViewed.iterator(); it.hasNext();) {
                    if (out.size() < count) {
                        String rId = (String) it.next();
                    	if(!alreadyAdded.contains(rId)) {
	                        RepositoryItem product = catalogTools.findProduct(rId);
	                        if ((null != product) && checkProductOnlineSale(product) &&
	                                checkNotInOrder(commerceItems, product)) {
	                            out.add(product);
	                            alreadyAdded.add(rId);
	                        }
                    	}
                    } else {
                        break;
                    }
                }
            }

        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e);
            }
        }  // end try-catch

        if (out.isEmpty()) {
            pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
        } else {
            String indexName = pRequest.getParameter(INDEX_NAME);
            if (indexName == null) {
                indexName = INDEX;
            }
            String elementName = pRequest.getParameter(ELEMENT_NAME);
            if (elementName == null) {
                elementName = ELEMENT;
            }

            pRequest.serviceLocalParameter(OUTPUT_START, pRequest, pResponse);
            for (int i = 0; (i < out.size()) && (i < count); i++) {
                pRequest.setParameter(COUNT, new Integer(i + 1));
                pRequest.setParameter(indexName, new Integer(i));
                pRequest.setParameter(elementName, out.get(i));
                pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
            }
            pRequest.serviceLocalParameter(OUTPUT_END, pRequest, pResponse);
        }  // end if-else
    }

    /**
     * Returns productSKU property.
     *
     * @param  product parameter to set.
     *
     * @return productSKU property.
     */
    private RepositoryItem getProductSKU(RepositoryItem product) {
        RepositoryItem result = null;
        if (null != product) {
            List skus = (List) product.getPropertyValue("childSKUs");
            if ((null != skus) && (0 < skus.size())) {
                result = (RepositoryItem) skus.get(0);
            }
        }
        return result;
    }

    /**
     *
     * @param  product parameter
     *
     * @return 
     */
    private boolean checkProductOnlineSale(RepositoryItem product) {
        //      boolean result = false;
        //      RepositoryItem sku = getProductSKU(product);
        //      if ( null != sku ) {
        //          Boolean onSale = (Boolean) sku.getPropertyValue("onSale");
        //          if ( null != onSale && onSale ) {
        //              result = true;
        //          }
        //      }
        //      return result;
        return true;
    }

    /**
     *
     * @param  commerceItems parameter
     * @param  product       parameter
     *
     * @return 
     */
    private boolean checkNotInOrder(List commerceItems, RepositoryItem product) {
        boolean result = true;
        RepositoryItem sku = getProductSKU(product);
        if (null != sku) {
            for (Iterator it = commerceItems.iterator(); it.hasNext();) {
                CommerceItem Ci = (CommerceItem) it.next();
                if (Ci.getCatalogRefId().equals(sku.getPropertyValue("Id"))) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Returns catalogTools property.
     *
     * @return catalogTools property.
     */
    public CastCatalogTools getCatalogTools() {
        return mCatalogTools;
    }

    /**
     * Sets the value of the catalogTools property.
     *
     * @param pCatalogTools parameter to set.
     */
    public void setCatalogTools(CastCatalogTools pCatalogTools) {
        mCatalogTools = pCatalogTools;
    }

    /**
     * Returns orderHolder property.
     *
     * @return orderHolder property.
     */
    public OrderHolder getOrderHolder() {
        return mOrderHolder;
    }

    /**
     * Sets the value of the orderHolder property.
     *
     * @param orderHolder parameter to set.
     */
    public void setOrderHolder(OrderHolder pOrderHolder) {
        mOrderHolder = pOrderHolder;
    }

    /**
     * Returns profile property.
     *
     * @return profile property.
     */
    public Profile getProfile() {
        return mProfile;
    }

    /**
     * Sets the value of the profile property.
     *
     * @param profile parameter to set.
     */
    public void setProfile(Profile pProfile) {
        mProfile = pProfile;
    }
    /**
     * Gets session.
     *
     * @return the sessionBean
     */

    public SessionBean getSessionBean() {
        return mSessionBean;
    }

    /**
     * Sets session.
     *
     * @param sessionBean the sessionBean to set
     */
    public void setSessionBean(SessionBean sessionBean) {
        this.mSessionBean = sessionBean;
    }

}
