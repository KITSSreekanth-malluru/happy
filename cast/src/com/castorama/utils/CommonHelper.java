package com.castorama.utils;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.servlet.ServletException;

import atg.core.util.ResourceUtils;

import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.droplet.GenericFormHandler;

import atg.nucleus.GenericService;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.RequestLocale;

/**
 * Helper class.
 *
 * @author Katsiaryna Dmitrievich
 */
public class CommonHelper extends GenericService {
    /** Database Error URL. */
    private String mDbErrorURL;

    /**
     * Generate form exception.
     *
     * @param pWhatException  parameter
     * @param pFormHandler    parameter
     * @param pResourceBundle parameter
     */
    public void generateFormException(String pWhatException, GenericFormHandler pFormHandler, String pResourceBundle) {
        ResourceBundle bundle = ResourceUtils.getBundle(pResourceBundle);
        String errorStr = bundle.getString(pWhatException);
        pFormHandler.addFormException(new DropletFormException(errorStr, pFormHandler.getAbsoluteName(),
                                                               pWhatException));
    }

    /**
     * Checks if item with <code>itemName=itemValue</code> exist in repository.
     *
     * @param  propName           searching property name
     * @param  propValue          searching property value
     * @param  repos              repository for search
     * @param  itemDescriptorName descriptor name
     *
     * @return RepositoryItem with <code>itemName=itemValue</code>
     *
     * @throws ServletException
     * @throws RepositoryException
     */
    public RepositoryItem repositoryItemByPropertyValue(final String propName, final String propValue,
                                                        final Repository repos, final String itemDescriptorName)
                                                 throws RepositoryException {
        RepositoryItem result = null;

        final RqlStatement findEmailRQL = RqlStatement.parseRqlStatement(propName + " = ?0");
        final RepositoryView emailView = repos.getView(itemDescriptorName);
        Object[] rqlparams = new Object[1];
        rqlparams[0] = propValue;
        RepositoryItem[] emailList = findEmailRQL.executeQuery(emailView, rqlparams);

        if ((emailList != null) && (emailList.length > 0)) {
            result = emailList[0];
        }

        return result;
    }

    /**
     * Checks if form handler <code>pFormHandler</code> contains {@link
     * RepositoryException}.
     *
     * @param  pFormHandler - form handler
     *
     * @return <code>true</code> if form handler <code>pFormHandler</code>
     *         contains {@link RepositoryException}, <code>false</code>
     *         otherwise
     */
    public boolean ifDbError(final GenericFormHandler pFormHandler) {
        boolean result = false;
        final Vector<DropletException> excs = (Vector<DropletException>) pFormHandler.getFormExceptions();
        for (DropletException dropletException : excs) {
            final Throwable cause = dropletException.getCause();
            if (cause instanceof RepositoryException) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Gets URL of database error page.
     *
     * @return the mDbErrorURL
     */
    public String getDbErrorURL() {
        return mDbErrorURL;
    }

    /**
     * Sets URL of database error page.
     *
     * @param dbErrorURL the mDbErrorURL to set
     */
    public void setDbErrorURL(String dbErrorURL) {
        mDbErrorURL = dbErrorURL;
    }

    /**
     * Returns the user's Locale object.
     * 
     * @param pRequest 			dynamo http servlet request.
     * 
     * @return the user's Locale object.
     * 
     * @throws ServletException when logic errors.
     * @throws IOException when io errors.
     *
     */
    public static Locale getUserLocale(DynamoHttpServletRequest pRequest) throws ServletException, IOException {
        RequestLocale requestLocale = pRequest.getRequestLocale();
        return ( null != requestLocale ) ? requestLocale.getLocale() : Locale.getDefault();
    }

}
