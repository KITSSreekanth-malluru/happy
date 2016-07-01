package com.castorama.scenario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.transaction.TransactionManager;

import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;
import atg.process.ProcessException;
import atg.process.ProcessExecutionContext;
import atg.process.action.ActionImpl;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.castorama.scenario.configuration.SearchMessageHandlerConfiguration;

/**
 * TypeAhead Handler Action.
 *
 * @author Andrei_Raichonak
 */
public class SearchMessageHandlerAction extends ActionImpl {
    /** lastModified property in TypeAheadRepository. */
    private static final String LAST_MODIFIED_PROP = "lastModified";

    /** hintCount property in TypeAheadRepository. */
    private static final String HINT_COUNT_PROP = "hintCount";

    /** query property in TypeAheadRepository. */
    private static final String QUERY_PROP_TYPE_AHEAD = "query";

    /** Item descriptor name of TypeAheadRepository. */
    private static final String TYPE_AHEAD_ITEM_DESCRIPTOR_NAME = "hint";

    /** Maximum size of type ahead repository. */
    private static final int REPOSITORY_SIZE = 4000;

    /** mLogging property */
    private ApplicationLogging mLogging = ClassLoggingFactory.getFactory().getLoggerForClass(SearchMessageHandlerAction.class);

    /** Type Ahead Repository. */
    private Repository mTypeAheadRepository;

    /** Transaction Manager. */
    private TransactionManager mTransactionManager = null;

    /**
     * @see atg.process.action.ActionImpl#configure(java.lang.Object)
     */
    public void configure(Object pConfiguration) throws ProcessException {
        SearchMessageHandlerConfiguration config = (SearchMessageHandlerConfiguration) pConfiguration;
        mTypeAheadRepository = config.getTypeAheadRepository();
        mTransactionManager = config.getTransactionManager();

        if (mTypeAheadRepository == null) {
        	if (mLogging.isLoggingWarning()) {
        		mLogging.logWarning("TypeAheadRepository wasn't found");
        	}
            throw new ProcessException("TypeAheadRepository wasn't found");
        }
        if (mTransactionManager == null) {
        	if (mLogging.isLoggingWarning()) {
        		mLogging.logWarning("TransactionManager wasn't not found");
        	}
            throw new ProcessException("TransactionManager wasn't not found");
        }
    }

    /**
     * @see atg.process.action.ActionImpl#executeAction(atg.process.ProcessExecutionContext)
     */
    protected void executeAction(ProcessExecutionContext pContext) throws ProcessException {
        SearchMessage msg = (SearchMessage) pContext.getMessage();
        TransactionManager tm = getTransactionManager();
        TransactionDemarcation td = new TransactionDemarcation();
        boolean success = false;

        try {
            if (tm != null) {
                td.begin(tm, TransactionDemarcation.REQUIRES_NEW);
            }

            if ((msg != null) && (msg.getSearchQuery().trim().length() > 3)) {
                String query = msg.getSearchQuery().trim().toLowerCase();

                MutableRepository repository = (MutableRepository) mTypeAheadRepository;
                try {
                    RepositoryItem searchesHint = searchQuery(query);

                    if (searchesHint == null) {
                        correctRepositorySize();
                        MutableRepositoryItem hint = repository.createItem(TYPE_AHEAD_ITEM_DESCRIPTOR_NAME);
                        hint.setPropertyValue(QUERY_PROP_TYPE_AHEAD, query);
                        hint.setPropertyValue(LAST_MODIFIED_PROP, new Date());
                        hint.setPropertyValue(HINT_COUNT_PROP, 1);
                        repository.addItem(hint);
                        if (mLogging.isLoggingDebug()) {
                    		mLogging.logDebug("Hint \"" + query + "\" was added into TypeAheadRepository." );
                    	}
                    } else {
                        String id = searchesHint.getRepositoryId();
                        MutableRepositoryItem hint = repository.getItemForUpdate(id, TYPE_AHEAD_ITEM_DESCRIPTOR_NAME);
                        Integer hintCount = (Integer) hint.getPropertyValue(HINT_COUNT_PROP);

                        if (hintCount == null) {
                            hintCount = 1;
                        } else {
                            hintCount += 1;
                        }

                        hint.setPropertyValue(HINT_COUNT_PROP, hintCount);
                        repository.updateItem(hint);
                        if (mLogging.isLoggingDebug()) {
                    		mLogging.logDebug("Hint with id " + id + " was updated." );
                    	}
                    }  // end if-else
                    success = true;
                } catch (RepositoryException e) {
                	if (mLogging.isLoggingWarning()) {
                		mLogging.logWarning("Can not add item in TypeAheadRepository with query = \"" + query + "\"\n");
                	}
                    throw new ProcessException("Can not add item in TypeAheadRepository with query = \"" + query + "\"\n", e);
                }  // end try-catch
            }  // end if
        } catch (TransactionDemarcationException e) {
        	if (mLogging.isLoggingWarning()) {
        		mLogging.logWarning("Error was occurred during a TransactionDemarcation. Can not add item in TypeAheadRepository.");
        	}
        } finally {
            try {
                if (tm != null) {
                    td.end(!success);
                }
            } catch (TransactionDemarcationException e) {
            }
        }  // end try-catch-finally
    }  // end method executeAction

    /**
     * Checks repository size and remove unnecessary elements from it.
     *
     * @throws RepositoryException RepositoryException
     */
    private void correctRepositorySize() throws RepositoryException {
        int countOfElemsToRemove = getCountOfElemsToRemove();
        if (countOfElemsToRemove > 0) {
            removeOldestElements(countOfElemsToRemove);
        }
    }

    /**
     * Checks count of items in repository: <code>REPOSITORY_SIZE</code> -
     * maximum size.
     *
     * @return <code>false</code> if size of repository less than <code>
     *         REPOSITORY_SIZE</code>, <code>true</code> otherwise
     *
     * @throws RepositoryException
     */
    private int getCountOfElemsToRemove() throws RepositoryException {
        int result = 0;

        final RqlStatement findHintRQL = RqlStatement.parseRqlStatement("ALL");
        final RepositoryView hintView = mTypeAheadRepository.getView(TYPE_AHEAD_ITEM_DESCRIPTOR_NAME);

        final int repozitorySize = findHintRQL.executeCountQuery(hintView, null);

        if (repozitorySize > REPOSITORY_SIZE) {
            result = repozitorySize - REPOSITORY_SIZE + 1;
        }
        if (mLogging.isLoggingDebug()) {
    		mLogging.logDebug("Repository size is " + repozitorySize + " elements. " + result +  " elements should be removed. ");
    	}
        return result;
    }

    /**
     * Remove the oldest element in repository.
     *
     * @param  countElemsToRemove - number of elements to delete
     *
     * @throws RepositoryException
     */
    private void removeOldestElements(int countElemsToRemove) throws RepositoryException {
        List<String> items = getOldestElements(countElemsToRemove);
        Collections.sort(items);
        for (Iterator<String> it = items.iterator(); it.hasNext();) {
	        MutableRepository repos = (MutableRepository) mTypeAheadRepository;
            repos.removeItem(it.next(), TYPE_AHEAD_ITEM_DESCRIPTOR_NAME);
        }
    }

    /**
     * Gets the id of the oldest element in repository.
     *
     * @param  countOldestElems - number of elements to delete
     *
     * @return id of the oldest element in repository or null if repository is
     *         empty.
     *
     * @throws RepositoryException
     */
    private List<String> getOldestElements(int countOldestElems) throws RepositoryException {
        final RqlStatement findHintRQL =
            RqlStatement.parseRqlStatement("ALL ORDER BY hintCount, lastModified RANGE +" + countOldestElems);
        final RepositoryView hintView = mTypeAheadRepository.getView(TYPE_AHEAD_ITEM_DESCRIPTOR_NAME);

        final RepositoryItem[] hintList = findHintRQL.executeQuery(hintView, null);
        List<String> resultIds = null;
        if (hintList != null && hintList.length > 0) {
        	resultIds = new ArrayList<String>(hintList.length);		
		    for (int i = 0; i < hintList.length; i++) {
		        RepositoryItem hint = hintList[i];
		        if (hint != null) {
		            resultIds.add(hint.getRepositoryId());
		        }
		    }
        }
        return resultIds;
        
    }

    /**
     * Searches <code>query</code> in the typeAhedRepository
     * (<code>mTypeAheadRepository</code>) repository.
     *
     * @param  query - qusery to search
     *
     * @return <code>null</code> if there is no recored for <code>query</code>,
     *         otherwise - first record.
     *
     * @throws RepositoryException RepositoryException
     */
    private RepositoryItem searchQuery(final String query) throws RepositoryException {
        RepositoryItem hint = null;

        final RqlStatement findHintRQL = RqlStatement.parseRqlStatement("query = ?0");
        final RepositoryView hintView = mTypeAheadRepository.getView(TYPE_AHEAD_ITEM_DESCRIPTOR_NAME);
        Object[] rqlparams = new Object[1];
        rqlparams[0] = query;

        final RepositoryItem[] hintList = findHintRQL.executeQuery(hintView, rqlparams);

        if ((hintList != null) && (hintList.length > 0)) {
            hint = hintList[0];
        }
        
        if (mLogging.isLoggingDebug()) {
    		mLogging.logDebug("Query \"" + query + "\" was found in the repository.\n");
    	}

        return hint;
    }

    /**
     * Sets the transactionManager property, used to manage transactions in the
     * handler.
     *
     * @param pTransactionManager - transaction manager
     */
    public void setTransactionManager(TransactionManager pTransactionManager) {
        mTransactionManager = pTransactionManager;
    }

    /**
     * Returns the transactionManager property, used to manage transactions in
     * the handler.
     *
     * @return Transaction Manager
     */
    public TransactionManager getTransactionManager() {
        return mTransactionManager;
    }

    /**
     * Returns logging property.
     *
     * @return logging property.
     */
    public ApplicationLogging getLogging() {
        // TODO Auto-generated method stub
        return mLogging;
    }
}
