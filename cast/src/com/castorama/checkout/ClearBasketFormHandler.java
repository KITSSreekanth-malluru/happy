package com.castorama.checkout;

import static com.castorama.utils.ContextTools.BONNES_AFFAIRES_POPUP_SESSION_PARAM;
import static com.castorama.utils.ContextTools.BONNES_AFFAIRES_PRODUCT_SESSION_PARAM;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.purchase.PurchaseProcessFormHandler;
import atg.droplet.DropletFormException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.castorama.servlet.CastoramaContextServlet;
import com.castorama.utils.ContextState;
import com.castorama.utils.ContextTools;

/**
 * 
 * @author EPAM team
 */
public class ClearBasketFormHandler extends PurchaseProcessFormHandler {
    /** useContext property. */
    private String useContext;

    /** successURL property. */
    private String successURL;

    /** contextServlet property. */
    private CastoramaContextServlet contextServlet;

    /**
     * Erases user basket
     * 
     * @param pRequest
     *            DynamoHttpServletRequest object
     * @param pResponse
     *            DynamoHttpServletResponse object
     * 
     * @throws ServletException
     *             exception
     * @throws IOException
     *             exception
     */
    public boolean handleUseContext(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        ContextState cs = getContextTools().getContextState(pRequest, pResponse);
        
        if (cs.isDisplayBonnesAffairesPopup()) {
            pRequest.getSession().removeAttribute(BONNES_AFFAIRES_POPUP_SESSION_PARAM);
            pRequest.getSession().removeAttribute(BONNES_AFFAIRES_PRODUCT_SESSION_PARAM);
            cs.setDisplayBonnesAffairesPopup(false);
            cs.setSavedBAProductId(null);
            getContextServlet().setShoppingContext(pRequest, pResponse, cs);
        } else if (cs.isDisplayEraseBasketPopup()) {
            boolean eraseBasket = (useContext!=null && cs.getNewContext()!=null)?useContext.equals(cs.getNewContext()):false;
            if (eraseBasket) {
                getContextTools().clearBasket(cs);
                getContextServlet().setShoppingContext(pRequest, pResponse, cs);
            } else {
                cs.setNewContext(cs.getCurrentContext());
                getContextServlet().setShoppingContext(pRequest, pResponse, cs);
            }
        }
        
        if (getSuccessURL() != null) {
            pResponse.sendLocalRedirect(getSuccessURL(), pRequest);
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * atg.commerce.order.purchase.PurchaseProcessFormHandler#afterSet(atg.servlet
     * .DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
     */
    @Override
    public boolean afterSet(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws DropletFormException {
        RepositoryItem profileItem = getProfile();
        OrderHolder curShCart = getShoppingCart();
        if ((profileItem != null) && (curShCart != null) && (curShCart.getCurrent() != null)) {
            Order curOrder = curShCart.getCurrent();
            if (curOrder != null) {
                if (isLoggingInfo()) {
                    logInfo("ClearBasketFormHandler release lock : Profile ID : " + profileItem.getRepositoryId() + "; Order ID : " + curOrder.getId());
                }
            }
        }
        return super.afterSet(pRequest, pResponse);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * atg.commerce.order.purchase.PurchaseProcessFormHandler#beforeSet(atg.
     * servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
     */
    @Override
    public boolean beforeSet(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws DropletFormException {
        RepositoryItem profileItem = getProfile();
        OrderHolder curShCart = getShoppingCart();
        if ((profileItem != null) && (curShCart != null) && (curShCart.getCurrent() != null)) {
            Order curOrder = curShCart.getCurrent();
            if (curOrder != null) {
                if (isLoggingInfo()) {
                    logInfo("ClearBasketFormHandler get lock : Profile ID : " + profileItem.getRepositoryId() + "; Order ID : " + curOrder.getId());
                }
            }
        }
        return super.beforeSet(pRequest, pResponse);
    }

    /**
     * @return the useContext
     */
    public String getUseContext() {
        return useContext;
    }

    /**
     * @param useContext
     *            the useContext to set
     */
    public void setUseContext(String useContext) {
        this.useContext = useContext;
    }

    /**
     * @return the successURL
     */
    public String getSuccessURL() {
        return successURL;
    }

    /**
     * @param successURL the successURL to set
     */
    public void setSuccessURL(String successURL) {
        this.successURL = successURL;
    }

    /**
     * @return the contextServlet
     */
    public CastoramaContextServlet getContextServlet() {
        return contextServlet;
    }

    /**
     * @param contextServlet
     *            the contextServlet to set
     */
    public void setContextServlet(CastoramaContextServlet contextServlet) {
        this.contextServlet = contextServlet;
    }

    /**
     * @return the contextTools
     */
    public ContextTools getContextTools() {
        return getContextServlet().getContextTools();
    }
}
