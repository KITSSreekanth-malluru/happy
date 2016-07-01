/**
 *
 */
package com.castorama.droplet;

import java.io.IOException;

import java.util.Calendar;

import javax.servlet.ServletException;

import atg.nucleus.naming.ParameterName;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

import atg.repository.rql.RqlStatement;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * CastoStageDroplet class which shows a list of available Castorama trainings.
 *
 * @author  EPAM team
 */
public class CastoStageDroplet extends DynamoServlet {
    /** OUTPUT constant */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** EMPTY constant */
    public static final ParameterName EMPTY = ParameterName.getParameterName("empty");

    /** RQL_STATEMENT constant */
    public static final String RQL_STATEMENT = " datestage > ?0 ORDER BY DATESTAGE ASC ";

    /** STAGE constant */
    public static final String STAGE = "STAGE";

    /** TRAININGS constant */
    public static final String TRAININGS = "trainings";

    /** repository property */
    private Repository mRepository = null;

    /**
     * Gets trainings from repository and shows only those, which will be in future.
     *
     * @param  pRequest  request
     * @param  pResponse response
     *
     * @throws ServletException error
     * @throws IOException      error
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        RepositoryItem[] trainings = null;

        try {
            RepositoryView view = getRepository().getView(STAGE);
            RqlStatement statement = RqlStatement.parseRqlStatement(RQL_STATEMENT);
            java.sql.Date jsqlD = new java.sql.Date(Calendar.getInstance().getTime().getTime());
            trainings = statement.executeQuery(view, new Object[] {jsqlD});
        } catch (RepositoryException e) {
        	if (isLoggingDebug()) {
        		logDebug(e.toString());
        	}
        }

        if (trainings != null) {
            pRequest.setParameter(TRAININGS, trainings);
            pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
        } else {
            pRequest.serviceParameter(EMPTY, pRequest, pResponse);
        }

    }

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
     * @param repository parameter to set.
     */
    public void setRepository(Repository repository) {
        this.mRepository = repository;
    }

}
