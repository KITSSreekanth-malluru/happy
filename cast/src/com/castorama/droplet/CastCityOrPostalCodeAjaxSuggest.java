package com.castorama.droplet;

import java.io.IOException;

import java.util.Locale;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;

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
 * Droplet that generate the list of hints. <dt>Input params:
 * <dd>
 * <li><code>QUERY_PARAM_NAME</code> - query string
 * <dt>Oparams:
 * <dd>
 * <li><code>output</code> if the list of search results starts with <code>
 * QUERY_PARAM_NAME</code> is not empty
 * <dd>
 * <li><code>empty</code> if the list of search results starts with <code>
 * QUERY_PARAM_NAME</code> is empty
 * <dt>Output params:
 * <dd>
 * <li><code>hints</code> list of search results
 * 
 * @see #QUERY_PARAM_NAME
 * @author Mikalai Khatsko
 */
public class CastCityOrPostalCodeAjaxSuggest extends DynamoServlet
        implements
        Constants {

    /** CITY_RQL_QUERY constant. */
    private static final String        CITY_RQL_QUERY              = "ville STARTS WITH ?0 ORDER BY ville SORT ASC RANGE +";

    /** HINT_PARTS_SEPARATOR constant. */
    private static final String        HINT_PARTS_SEPARATOR        = ", ";

    /** CODE_POSTAL_RQL_QUERY constant. */
    private static final String        CODE_POSTAL_RQL_QUERY       = "code_postal STARTS WITH ?0 ORDER BY code_postal SORT ASC RANGE +";

    /** PATTERN_NUMERICAL constant. */
    private static final Pattern       PATTERN_NUMERICAL           = Pattern
                                                                           .compile("^[0-9]+$");

    /** code postal query property in CodePostalRepository. */
    private static final String        CODE_POSTAL_PROP            = "code_postal";

    /** city query property in CodePostalRepository. */
    private static final String        CITY_PROP                   = "ville";

    /** Item descriptor name of CodePostalRepository. */
    private static final String        CODE_POSTAL_DESCRIPTOR_NAME = "code_postal";

    /** Input parameter name for query string. */
    private static final ParameterName QUERY_PARAM_NAME            = ParameterName
                                                                           .getParameterName("q");

    /** Output parameter name for list of hints. */
    private static final String        HINTS_PARAM                 = "hints";

    /** Output oparam name. */
    private static final ParameterName OUTPUT                      = ParameterName
                                                                           .getParameterName("output");

    /** Oparam: empty. */
    private static final ParameterName EMPTY                       = ParameterName
                                                                           .getParameterName("empty");

    /** Code postal repository. */
    private Repository                 repository;

    /** The count of hints that should be returned from the repository. */
    private int                        mHintsCount;

    /** enable/disable suggestions in search input field. */
    private boolean                    mSuggestionsEnable;

    /**
     * Searches search results that are started with value from param
     * QUERY_PARAM_NAME
     * 
     * @param pRequest - request
     * @param pResponse - response
     * 
     * @throws IOException
     * @throws ServletException
     */
    public void service(DynamoHttpServletRequest pRequest,
            DynamoHttpServletResponse pResponse) throws ServletException,
            IOException {
        String userQuery = pRequest.getParameter(QUERY_PARAM_NAME);
        if (isMissingParameters(userQuery) || !isSuggestionsEnable()) {
            pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
            return;
        }
        try {
            // do a search if the user entered at least 2 Letters
            if ((userQuery != null) && (userQuery.trim().length() >= 2)) {
                userQuery = userQuery.toUpperCase(Locale.FRANCE);
                String[] hints = null;
                if (PATTERN_NUMERICAL.matcher(userQuery).matches()) {
                    hints = getCodePostalHints(userQuery);
                } else {
                    hints = getCityHints(userQuery);
                }
                if (hints != null) {
                    pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
                    pRequest.setParameter(HINTS_PARAM, hints);
                } else {
                    pRequest.serviceParameter(EMPTY, pRequest, pResponse);
                }

            }
        } catch (Throwable tr) {
            // log any exception that might occur
            if (isLoggingError()) {
                logError("exception in CastCityOrPostalCodeAjaxSuggest", tr);
            }
            throw new ServletException(
                    "exception in CastCityOrPostalCodeAjaxSuggest", tr);
        }
    }

    /**
     * Searches hints via query string.
     * 
     * @param userQuery - query to search
     * 
     * @return the list of keywords
     * 
     * @throws ServletException
     */
    private String[] getCodePostalHints(String userQuery)
            throws ServletException {
        String[] hints = null;

        final Repository repos = getRepository();
        try {
            final RqlStatement findHintRQL = RqlStatement
                    .parseRqlStatement(CODE_POSTAL_RQL_QUERY + getHintsCount());
            final RepositoryView hintView = repos
                    .getView(CODE_POSTAL_DESCRIPTOR_NAME);
            Object[] rqlparams = new Object[1];
            rqlparams[0] = userQuery;
            final RepositoryItem[] hintsList = findHintRQL.executeQuery(
                    hintView, rqlparams);

            if ((hintsList != null) && (hintsList.length > 0)) {
                hints = new String[hintsList.length];
                for (int i = 0; i < hintsList.length; i++) {
                    RepositoryItem repositoryItem = hintsList[i];
                    StringBuffer sb = new StringBuffer();
                    sb.append((String) repositoryItem
                            .getPropertyValue(CODE_POSTAL_PROP));
                    sb.append(HINT_PARTS_SEPARATOR);
                    sb.append((String) repositoryItem
                            .getPropertyValue(CITY_PROP));
                    hints[i] = sb.toString();
                }
            }
        } catch (RepositoryException e) {
            if (isLoggingError()) {
                logError(e.getCause());
            }
        } // end try-catch

        return hints;
    }

    /**
     * Searches hints via query string.
     * 
     * @param userQuery - query to search
     * 
     * @return the list of keywords
     * 
     * @throws ServletException
     */
    private String[] getCityHints(String userQuery) throws ServletException {
        String[] hints = null;

        final Repository repos = getRepository();
        try {
            final RqlStatement findHintRQL = RqlStatement
                    .parseRqlStatement(CITY_RQL_QUERY + getHintsCount());
            final RepositoryView hintView = repos
                    .getView(CODE_POSTAL_DESCRIPTOR_NAME);
            Object[] rqlparams = new Object[1];
            rqlparams[0] = userQuery;
            final RepositoryItem[] hintsList = findHintRQL.executeQuery(
                    hintView, rqlparams);

            if ((hintsList != null) && (hintsList.length > 0)) {
                hints = new String[hintsList.length];
                for (int i = 0; i < hintsList.length; i++) {
                    RepositoryItem repositoryItem = hintsList[i];
                    StringBuffer sb = new StringBuffer();
                    sb.append((String) repositoryItem
                            .getPropertyValue(CITY_PROP));
                    sb.append(HINT_PARTS_SEPARATOR);
                    sb.append((String) repositoryItem
                            .getPropertyValue(CODE_POSTAL_PROP));
                    hints[i] = sb.toString();
                }
            }
        } catch (RepositoryException e) {
            if (isLoggingError()) {
                logError(e.getCause());
            }
        } // end try-catch

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
     * @param userQuery a <code>String</code> value
     * 
     * @return a <code>boolean</code> value
     */
    protected boolean isMissingParameters(String userQuery) {
        if (StringUtils.isBlank(userQuery)) {
            if (isLoggingError()) {
                Object[] args = { QUERY_PARAM_NAME.getName() };
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
     * Gets if enable suggestion in search input field.
     * 
     * @return the suggestionsEnable
     */
    public boolean isSuggestionsEnable() {
        return mSuggestionsEnable;
    }

    /**
     * Sets if enable suggestions in search input field.
     * 
     * @param pSuggestionsEnable the suggestionsEnable to set
     */
    public void setSuggestionsEnable(boolean pSuggestionsEnable) {
        this.mSuggestionsEnable = pSuggestionsEnable;
    }
}
