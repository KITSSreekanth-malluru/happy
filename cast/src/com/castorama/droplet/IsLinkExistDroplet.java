package com.castorama.droplet;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class IsLinkExistDroplet extends DynamoServlet {

    /** USER_ID constant. */
    private static final String USER_ID="userId";
    
    /** PROPERTY_EMAIL constant. */
    private static final String PROPERTY_EMAIL="email";
    
    /** DATE constant. */
    private static final String PROPERTY_DATE="date";
    
    /** KEY_PARAM constant. */
    public static final String KEY= "key";
    
    /** Password link item descriptor name. */
    private static final String LINK_DESCRIPTOR = "passwordLink";
    
    /** OUTPUT constant. */
    private static final String OUTPUT = "output";

    /** EMPTY constant. */
    private static final String EMPTY = "empty";
    
    /** Repository property. */
    private Repository profileRepository;
    
    /** Change password link active period property. */
    private long changePasswordLinkActivePeriod;

    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) 
            throws ServletException, IOException {
        
        String keyLink = (String) pRequest.getObjectParameter(KEY);
        if (!StringUtils.isBlank(keyLink)) {
            RepositoryItemDescriptor linkDescriptor;
            RepositoryItem[] linkItem = null;
            try {
                linkDescriptor = getProfileRepository().getItemDescriptor(LINK_DESCRIPTOR);
                RepositoryView linkView = linkDescriptor.getRepositoryView();
                QueryBuilder linkBuilder = linkView.getQueryBuilder();
                Query linkQuery = linkBuilder.createComparisonQuery(
                        linkBuilder.createPropertyQueryExpression(KEY),
                        linkBuilder.createConstantQueryExpression(keyLink),
                        QueryBuilder.EQUALS);
                linkItem = linkView.executeQuery(linkQuery);  
            } catch (RepositoryException e) {
                if (isLoggingError()) {
                    logError(e.getMessage());
                }
            }
            if (linkItem != null) {
                Date data = (Date)linkItem[0].getPropertyValue(PROPERTY_DATE);
                long periodFromLinkCreation = new Date().getTime() - data.getTime();
                if(periodFromLinkCreation <= getChangePasswordLinkActivePeriod()) {
                    String userId = (String)linkItem[0].getPropertyValue(PROPERTY_EMAIL);
                    pRequest.setParameter(USER_ID, userId);
                    pRequest.setParameter(KEY, keyLink);
                    pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
                } else {
                pRequest.serviceParameter(EMPTY, pRequest, pResponse);
                }
            } else {
                pRequest.serviceParameter(EMPTY, pRequest, pResponse);
            }
        }
    }

    public Repository getProfileRepository() {
        return profileRepository;
    }

    public void setProfileRepository(Repository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public long getChangePasswordLinkActivePeriod() {
        return changePasswordLinkActivePeriod;
    }

    public void setChangePasswordLinkActivePeriod(
            long changePasswordLinkActivePeriod) {
        this.changePasswordLinkActivePeriod = changePasswordLinkActivePeriod;
    }
}
