package com.castorama.commerce.order.invoice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.castorama.commerce.order.CastOrderManager;
import com.castorama.constantes.CastoConstantesOrders;
import com.castorama.utils.CastPlaceList;

import atg.repository.RepositoryItem;

import atg.servlet.GenericServletService;
import atg.userprofiling.ProfileServices;

/**
 * 
 *
 * @author  EPAM Team
 * 
 */
public class GeneratePdfServlet extends GenericServletService {

    private static final String DELIVERY_ID_PARAM = "delivery_id";
    public static final String CONTENT_TYPE_PDF = "application/pdf";

    private CastOrderManager mOrderManager;
    private CastPlaceList mCountryList;
    private ProfileServices profileServices;

    /**
     *
     *
     * @param  pServletRequest  parameter
     * @param  pServletResponse parameter
     *
     * @throws IOException      exception
     * @throws ServletException exception
     */
    @Override public void handleService(ServletRequest pServletRequest, ServletResponse pServletResponse)
                                 throws IOException, ServletException {
        final ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        final String deliveryId = pServletRequest.getParameter(DELIVERY_ID_PARAM);
        RepositoryItem orderBOItem = mOrderManager.getFactureBO(deliveryId);
        OutputStream out = null;

        if (orderBOItem != null) {
            RepositoryItem orderFOItem = mOrderManager.getOrderFO(orderBOItem);
            String profileId = (String) orderFOItem.getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_PROFILEID);
            String currProfileId = getProfileServices().getCurrentProfileId();
            if (!currProfileId.equalsIgnoreCase(profileId)) {
                if (isLoggingError()) {
                    logError("Facture with id=" + deliveryId + " doesn't belong to profile with id=" + currProfileId);
                }
                return;
            }
            
            Map params = new HashMap();
            params.put("url",
                       pServletRequest.getScheme() + "://" + pServletRequest.getServerName() + ":" +
                       pServletRequest.getServerPort());

            try {
                PrintPdfHelper.getInstance().generateInvoicePdf(pdf, orderBOItem, params, mCountryList);

                // Set header
                pServletResponse.setContentType(CONTENT_TYPE_PDF);
                pServletResponse.setContentLength(pdf.size());

                out = pServletResponse.getOutputStream();
                pdf.writeTo(out);
                out.flush();
            } catch (IOException e) {
                logError(e);
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        } else {
            logError("Facture with id=" + deliveryId + " was not found.");
        }  // end if-else
    }

    /**
     * Returns orderManager property.
     *
     * @return orderManager property.
     */
    public CastOrderManager getOrderManager() {
        return mOrderManager;
    }

    /**
     * Sets the value of the orderManager property.
     *
     * @param pOrderManager parameter to set.
     */
    public void setOrderManager(CastOrderManager pOrderManager) {
        this.mOrderManager = pOrderManager;
    }

    public CastPlaceList getCountryList() {
        return mCountryList;
    }

    public void setCountryList(CastPlaceList countryList) {
        mCountryList = countryList;
    }

    public ProfileServices getProfileServices() {
        return profileServices;
    }

    public void setProfileServices(ProfileServices profileServices) {
        this.profileServices = profileServices;
    }
}
