/**
 *
 */
package com.castorama.mobile.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

import atg.repository.rql.RqlStatement;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Converts short URL for QR code to Long URL
 *
 * @author Katsiaryna_Sharstsiuk
 */
public class VideoURLConverterDroplet extends DynamoServlet {
    /** RQL_QUERY constant. */
    private static final String RQL_QUERY = "shortURL = ?0";

    /** VIDEO_ITEM constant. */
    private static final String VIDEO_ITEM = "videoItem";

    /** ERROR constant. */
    private static final String ERROR = "error";

    /** EMPTY constant. */
    private static final String EMPTY = "empty";

    /** OUTPUT constant. */
    public static final String OUTPUT = "output";

    /** SHORT_URL constant. */
    private static final String SHORT_URL = "shortURL";

    /** repository property */
    private Repository mRepository;

    /** itemDescriptorName property */
    private String mItemDescriptorName = "iPhoneVideo";

    /**
     * Returns repository property.
     *
     * @return repository property.
     */
    public Repository getRepository() {
        return mRepository;
    }

    /**
     * Sets the value of the repository property.
     *
     * @param pRepository parameter to set.
     */
    public void setRepository(Repository pRepository) {
        mRepository = pRepository;
    }

    /**
     * Returns itemDescriptorName property.
     *
     * @return itemDescriptorName property.
     */
    public String getItemDescriptorName() {
        return mItemDescriptorName;
    }

    /**
     * Sets the value of the itemDescriptorName property.
     *
     * @param pItemDescriptorName parameter to set.
     */
    public void setItemDescriptorName(String pItemDescriptorName) {
        mItemDescriptorName = pItemDescriptorName;
    }

    /**
     * Converts short URL for QR code to Long URL.
     *
     * @param  pRequest  parameter.
     * @param  pResponse parameter.
     *
     * @throws IOException      exception
     * @throws ServletException exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                 throws IOException, ServletException {
        String shortURL = pRequest.getParameter(SHORT_URL);
        Repository repository = getRepository();
        if (!StringUtils.isBlank(shortURL) && (repository != null) && (getItemDescriptorName() != null)) {
            RepositoryItem videoItem = null;
            boolean result = false;
            RepositoryItem[] videoItems = null;
            try {
                RepositoryView repView = repository.getView(getItemDescriptorName());
                if (repView != null) {
                    RqlStatement statement = RqlStatement.parseRqlStatement(RQL_QUERY);
                    Object[] params = {shortURL};
                    videoItems = statement.executeQuery(repView, params);
                }
            } catch (RepositoryException e) {
                if (isLoggingError()) {
                    logError(e.getMessage());
                }
            }
            if ((videoItems != null) && (videoItems.length > 0)) {
                videoItem = videoItems[0];
                if (videoItem != null) {
                    result = true;
                }
            }
            if (result) {
                pRequest.setParameter(VIDEO_ITEM, videoItem);
                pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
            } else {
                pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
            }
        } else {
            pRequest.setParameter(VIDEO_ITEM, null);
            pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
        }  // end if-else

    }

}
