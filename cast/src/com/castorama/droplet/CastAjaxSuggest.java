package com.castorama.droplet;

import java.io.IOException;

import java.util.Locale;

import javax.servlet.ServletException;

import javax.transaction.TransactionManager;

import atg.core.util.StringUtils;

import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;

import atg.nucleus.naming.ParameterName;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

import atg.repository.rql.RqlStatement;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import atg.userdirectory.droplet.Constants;
import atg.userdirectory.droplet.Utils;

/**
 * Droplet that generate the list of hints.
 * <dt>Input params:
 * <dd>
 * <li><code>QUERY_PARAM_NAME</code> - query string
 * <dt>Oparams:
 * <dd>
 * <li><code>output</code> if the list of search results starts with <code>
 * alpha</code> is not empty
 * <dd>
 * <li><code>empty</code> if the list of search results starts with <code>
 * alpha</code> is empty
 * <dt>Output params:
 * <dd>
 * <li><code>hints</code> list of search results
 *
 * @see    #QUERY_PARAM_NAME
 * @author Katsiaryna Dmitrievich
 */
public class CastAjaxSuggest extends DynamoServlet implements Constants {
    /** query property in TypeAheadRepository. */
    private static final String QUERY_PROP_TYPE_AHEAD = "query";

    /** Item descriptor name of TypeAheadRepository. */
    private static final String TYPE_AHEAD_ITEM_DESCRIPTOR_NAME = "hint";

    /** Input parameter name for query string. */
    private static final ParameterName QUERY_PARAM_NAME = ParameterName.getParameterName("q");

    /** Output parameter name for list of hints. */
    private static final String HINTS_PARAM = "hints";

    /** Output oparam name. */
    private static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** Oparam: empty. */
    private static final ParameterName EMPTY = ParameterName.getParameterName("empty");

    /** Type Ahead repository. */
    private Repository repository;

    /** Transaction manager. */
    private TransactionManager mTransactionManager;

    /** The count of hints that should be returned from the repository. */
    private int mHintsCount;

    /** enable/disable type ahead in search input field. */
    private boolean mTypeAheadEnable;

    /**
     * Searches search results that are started with value from param
     * QUERY_PARAM_NAME
     *
     * @param  pRequest  - request
     * @param  pResponse - response
     *
     * @throws IOException
     * @throws ServletException
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        String userQuery = pRequest.getParameter(QUERY_PARAM_NAME);
        if (isMissingParameters(userQuery) || !isTypeAheadEnable()) {
            pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
            return;
        }
        try {
            // do a search if the user entered at least 3 Letters
            if ((userQuery != null) && (userQuery.trim().length() >= 3)) {
                userQuery = userQuery.toLowerCase(Locale.FRANCE);
                String[] hints = getHints(userQuery);
                if (hints != null) {
                    pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
                    pRequest.setParameter(HINTS_PARAM, hints);
                } else {
                    pRequest.serviceParameter(EMPTY, pRequest, pResponse);
                }

            }
        } catch (Throwable tr) {
            // log any exception that might occur
            logError("exception in CastoAjaxSuggest", tr);
            throw new ServletException("exception in CastoAjaxSuggest", tr);
        }
    }

    /**
     * Searches hints via query string.
     *
     * @param  userQuery - query to search
     *
     * @return the list of keywords
     *
     * @throws ServletException
     */
    private String[] getHints(String userQuery) throws ServletException {
        String[] hints = null;

        final Repository repos = getRepository();
        try {
            final RqlStatement findHintRQL =
                RqlStatement.parseRqlStatement("query STARTS WITH ?0 ORDER BY hintCount SORT DESC RANGE +" +
                                               getHintsCount());
            final RepositoryView hintView = repos.getView(TYPE_AHEAD_ITEM_DESCRIPTOR_NAME);
            Object[] rqlparams = new Object[1];
            rqlparams[0] = userQuery;
            final RepositoryItem[] hintsList = findHintRQL.executeQuery(hintView, rqlparams);

            if ((hintsList != null) && (hintsList.length > 0)) {
                hints = new String[hintsList.length];
                for (int i = 0; i < hintsList.length; i++) {
                    RepositoryItem repositoryItem = hintsList[i];
                    hints[i] = (String) repositoryItem.getPropertyValue(QUERY_PROP_TYPE_AHEAD);
                }
            }
        } catch (RepositoryException e) {
            if (isLoggingDebug()) {
                logDebug(e.getCause());
            }
        }  // end try-catch

        return hints;
    }

    /**
     * Gets Hint Repository
     *
     * @return the repository
     */
    public Repository getRepository() {
        return repository;
    }

    /**
     * Sets Hint repository
     *
     * @param repository the repository to set
     */
    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    /**
     * Gets transaction manager.
     *
     * @return the mTransactionManager
     */
    public TransactionManager getTransactionManager() {
        return mTransactionManager;
    }

    /**
     * Sets transaction manager.
     *
     * @param transactionManager the mTransactionManager to set
     */
    public void setTransactionManager(TransactionManager transactionManager) {
        mTransactionManager = transactionManager;
    }

    /**
     * This method will determine if there are any missing parameters passed to
     * this droplet. If there are then an error is logged and this method will
     * return true. This method will look for the following parameters:<br>
     *
     * <ul>
     * <li>userQuery - this is the query string that is used to look up the
     * hints. If it is missing, then the droplet will consider this an error
     * condition.
     * </ul>
     *
     * @param  userQuery a <code>String</code> value
     *
     * @return a <code>boolean</code> value
     */
    protected boolean isMissingParameters(String userQuery) {
        if (StringUtils.isBlank(userQuery)) {
            if (isLoggingError()) {
                Object[] args = {QUERY_PARAM_NAME.getName()};
                logError(Utils.fillInArgs(MISSING_REQUIRED_PARAM, args));
            }
            return true;
        }

        return false;
    }

    /**
     * Gets the count of hints that should be returned from the repository
     *
     * @return the mHintsCount
     */
    public int getHintsCount() {
        return mHintsCount;
    }

    /**
     * Sets the count of hints that should be returned from the repository
     *
     * @param hintsCount the mHintsCount to set
     */
    public void setHintsCount(int hintsCount) {
        mHintsCount = hintsCount;
    }

    /**
     * Gets if enable type ahead in search input field.
     *
     * @return the typeAheadEnable
     */
    public boolean isTypeAheadEnable() {
        return mTypeAheadEnable;
    }

    /**
     * Sets if enable type ahead in search input field.
     *
     * @param typeAheadEnable the typeAheadEnable to set
     */
    public void setTypeAheadEnable(boolean typeAheadEnable) {
        this.mTypeAheadEnable = typeAheadEnable;
    }
}
