package com.castorama.droplet;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

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
 * Droplet that search all cities with provided postal code.
 * <dt>Input params:
 * <dd>
 * <li><code>CODE_POSTAL</code> - searching postal code
 * <dt>Oparams:
 * <dd>
 * <li><code>output</code> if the list of cities with postal code <code>
 * CODE_POSTAL</code> is not empty
 * <dd>
 * <li><code>empty</code> if the list of cities with postal code <code>
 * CODE_POSTAL</code> is empty or if <code>CODE_POSTAL</code> is not set
 * <dt>Output params:
 * <dd>
 * <li><code>LISTE_VILLES</code> list of cities with postal code <code>
 * CODE_POSTAL</code>
 *
 * @see    #CODE_POSTAL
 * @see    #LISTE_VILLES
 * @author Katsiaryna_Dmitrievich
 */
public class CastCityViaPostalCode extends DynamoServlet implements Constants {
    /** Parameter that contains the array of cities. */
    private static final String LISTE_VILLES = "listeVilles";
    /** Country count output parameter. */
    private static final String COUNT = "count";

    /** City column name in DB. */
    private static final String VILLE = "ville";

    /** Postal code column name in db. */
    private static final String POSTAL = "code_postal";

    /** Postal code droplet parameter name. */
    private static final ParameterName CODE_POSTAL = ParameterName.getParameterName("codePostal");

    /** Code Postal Repository path. */
    private Repository mCodePostalRepository;

    /**
     * gets Code Postal Repository
     *
     * @return mCodePostalRepository
     */
    public Repository getCodePostalRepository() {
        return mCodePostalRepository;
    }

    /**
     * Sets CodePostalRepository
     *
     * @param pCodePostalRepository - repository to set
     */
    public void setCodePostalRepository(Repository pCodePostalRepository) {
        mCodePostalRepository = pCodePostalRepository;
    }

    /**
     * Search all cities with provided postal code {@value #CODE_POSTAL}
     *
     * @param     pRequest  the servlet's request
     * @param     pResponse the servlet's response
     *
     * @exception ServletException if there was an error while executing the
     *                             code
     * @exception IOException      if there was an error with servlet io
     */
    @Override public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                           throws ServletException, IOException {
        if (isLoggingDebug()) {
            logDebug(this.getClass() + ".service : begin");
        }
        String codePostal = pRequest.getParameter(CODE_POSTAL);
        if (isMissingParameters(codePostal)) {
            pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
            return;
        }
        List<String> cities = new ArrayList<String>();

        Repository codePostalRepository = getCodePostalRepository();

        try {
            RqlStatement findCodePostal = RqlStatement.parseRqlStatement("code_postal = ?0");
            RepositoryView postalView = codePostalRepository.getView(POSTAL);
            RepositoryItem[] citiesList = findCodePostal.executeQuery(postalView, new Object[] {codePostal});

            if (null != citiesList) {
                int size = citiesList.length;

                for (int i = 0; i < size; i++) {
                    cities.add(citiesList[i].getPropertyValue(VILLE).toString());
                }
                pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
                pRequest.setParameter(LISTE_VILLES, cities);
                pRequest.setParameter(COUNT, cities.size());

            } else {
                pRequest.serviceParameter(EMPTY, pRequest, pResponse);
            }

        } catch (RepositoryException rpe) {
            logError(this.getClass() + ".service : ERROR ==> " + rpe.toString());
        }  // end try-catch
    }

    /**
     * This method will determine if there are any missing parameters passed to
     * this droplet. If there are then an error is logged and this method will
     * return true. This method will look for the following parameters:<br>
     *
     * <ul>
     * <li>codePostal - this is the postal code that is used to look up the
     * city. If it is missing, then the droplet will consider this an error
     * condition.
     * </ul>
     *
     * @param  codePostal a <code>String</code> value
     *
     * @return a <code>boolean</code> value
     */
    protected boolean isMissingParameters(String codePostal) {
        if (StringUtils.isBlank(codePostal)) {
            if (isLoggingError()) {
                Object[] args = {CODE_POSTAL.getName()};
                logError(Utils.fillInArgs(MISSING_REQUIRED_PARAM, args));
            }
            return true;
        }

        return false;
    }
}
