package com.castorama.integration.webservice.inventory.droplet;

import atg.commerce.order.CommerceItemImpl;
import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.userprofiling.Profile;

import com.castorama.commerce.order.CastOrderImpl;
import com.castorama.commerce.profile.CastProfileTools;
import com.castorama.integration.webservice.inventory.model.WebServiceConfiguration;
import com.castorama.integration.webservice.inventory.model.WebServiceConstants;
import com.castorama.utils.ContextState;
import com.castorama.utils.ContextTools;

import javax.servlet.ServletException;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author EPAM team
 */
public class RequestUrlBuilderDroplet extends DynamoServlet {

    //region Statics
    private static final ParameterName OUTPUT = ParameterName.getParameterName("output");
    private static final String ORDER_ID_PARAMETER_NAME = "orderId";
    private static final String REQUEST_URL_PARAMETER_NAME = "requestUrl";
    private static final String CURRENT_LOCAL_STORE_PROFILE_PROPERTY = "currentLocalStore";
    private static final String STORE_ID_PROPERTY = "storeId";
    private static final String CODE_ARTICLE_PROPERTY = "codeArticle";
    //endregion

    //region Dependencies
    private WebServiceConfiguration configuration;
    private CastProfileTools profileTools;
    private ContextTools contextTools;
    //endregion

    private static Set<Integer> getItemCodeArticlesFromOrder(CastOrderImpl currentLocal) {
        List commerceItems = currentLocal.getCommerceItems();
        Set<Integer> codeArticles = new HashSet<Integer>();
        for (Object item : commerceItems) {
            CommerceItemImpl commerceItem = (CommerceItemImpl) item;
            Integer codeArticle = (Integer) commerceItem.getPropertyValue(CODE_ARTICLE_PROPERTY);
            codeArticles.add(codeArticle);
        }

        return codeArticles;
    }

    @Override
    public void service(DynamoHttpServletRequest request, DynamoHttpServletResponse response)
            throws ServletException, IOException {

        ContextState contextState = contextTools.getContextState(request, response);
        CastOrderImpl currentLocal = contextState.getShoppingCart().getCurrentLocal();
        Set<Integer> codeArticles = getItemCodeArticlesFromOrder(currentLocal);
        String orderId = request.getParameter(ORDER_ID_PARAMETER_NAME);
        String storeId = getCurrentLocalStoreId(contextState.getProfile());

        StringBuilder stringBuilder = new StringBuilder();
        for (Integer codeArticle : codeArticles) {
            stringBuilder.append(codeArticle).append(",");
        }
        String codeArticlesAsString = stringBuilder.toString();
        codeArticlesAsString = codeArticlesAsString.substring(0, codeArticlesAsString.length() - 1);
        String requestUrl = MessageFormat.format(
                configuration.getAjaxRequestUrlTemplate(), orderId, storeId,
                new SimpleDateFormat(WebServiceConstants.DATE_FORMAT).format(Calendar.getInstance().getTime()), codeArticlesAsString
        );

        request.setParameter(REQUEST_URL_PARAMETER_NAME, requestUrl);
        request.serviceLocalParameter(OUTPUT, request, response);
    }

    private String getCurrentLocalStoreId(Profile profile) {
        RepositoryItem localStore =
                (RepositoryItem) profile.getPropertyValue(CURRENT_LOCAL_STORE_PROFILE_PROPERTY);

        return (String) localStore.getPropertyValue(STORE_ID_PROPERTY);
    }

    //region Getters\Setters
    public WebServiceConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(WebServiceConfiguration configuration) {
        this.configuration = configuration;
    }

    public CastProfileTools getProfileTools() {
        return profileTools;
    }

    public void setProfileTools(CastProfileTools profileTools) {
        this.profileTools = profileTools;
    }

    public ContextTools getContextTools() {
        return contextTools;
    }

    public void setContextTools(ContextTools contextTools) {
        this.contextTools = contextTools;
    }
    //endregion
}
