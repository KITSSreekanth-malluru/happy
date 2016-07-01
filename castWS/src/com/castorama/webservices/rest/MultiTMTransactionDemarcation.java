package com.castorama.webservices.rest;

import java.util.HashSet;

import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.logging.ApplicationLoggingImpl;

/**
 * <p>This class behaves in the same manner as <tt>atg.dtm.TransactionDemarcation</tt> does
 * with the exception that it allows multiple transactions be handled as a single unit. In particular,
 * the whole transaction is considered for <em>rollback only</em> if any of the active sub-transactions
 * is marked for <em>rollback only</em> or is rolling back or has been rolled back.</p>
 * 
 * <p>The class is thread-safe to the same extent as the transaction managers used with it are.</p>
 * <p><tt>MultiTMTransactionDemarcation</tt> supports transaction to the same extent as the
 * transaction managers used with it do.</p>
 * <p>Note that if a sub-transaction fail to commit or rollback than the state of the whole transaction
 * is <em>undefined</em>.</p>
 * 
 * @see atg.dtm.TransactionDemarcation
 */
// TODO move this class to a proper package
// TODO check if this code makes sense. Probably, JTA requires a single transaction manager to be used by the appserver
public class MultiTMTransactionDemarcation {
    // the collection of unique transaction managers that are to be used in a single transaction
    private HashSet<TransactionManager> tmSet;
    
    private TransactionDemarcation[] tds;
    
    private final ApplicationLoggingImpl log = new ApplicationLoggingImpl(MultiTMTransactionDemarcation.class.getName());
    
    /**
     * <p>This function should be called at the beginning of a section of code to be
     * demarcated by a transaction.</p>
     * 
     * <p>If this function returns successfully, then <tt>{@link #end(boolean)}</tt> must be
     * called at some point in the future.</p>
     *
     * @param tms the transaction managers for which the code is to be run as a transaction.
     * Duplicate transaction managers are regarded as if they are a single manager.
     * 
     * @param transAttrib specifies what kind of transactional behavior will be performed
     * in the code. The attributes used are the same as for
     * <tt>atg.dtm.TransactionDemarcation#begin(TransactionManager, int)</tt>.
     * 
     * @throws TransactionDemarcationException if a transaction cannot be set up by any of
     * the transaction managers.
   **/
    public synchronized void begin(final TransactionManager[] tms, final int transAttrib) throws TransactionDemarcationException {
        // ensures that each transaction manager is used only once
        tmSet = new HashSet<TransactionManager>();
        for (final TransactionManager tm : tms) {
            tmSet.add(tm);
        }
        
        tds = new TransactionDemarcation[tmSet.size()];
        int i = 0;
        for (final TransactionManager tm : tmSet) {
            final TransactionDemarcation td = new TransactionDemarcation();
            try {
                td.begin(tm, transAttrib);
            } catch (TransactionDemarcationException ex) {
                // rolling back already started transactions in the reverse order
                for (int j = i - 1; j >= 0; --j) {
                    tryEndTransaction(tds[j], true);
                }
                throw ex;
            }
            tds[i++] = td;
        }
    }
    
    /**
     * <p>This function must be called at the end of a section of code demarcated
     * by this <tt>MultiTMTransactionDemarcation</tt>, preferably in a finally clause.
     * It will commit (or rollback if marked for rollback only) any created transaction,
     * and resume any suspended transaction. If any of the active transactions is marked for
     * <em>rollback only</em> or is rolling back or has been rolled back then the whole
     * transaction is rolled back despite of the value of the <i>rollback</i> flag.</p>
     * 
     * @param rollback indicates if the transaction should be rolled back.
     * If it is <tt>true</tt>, the transaction will be rolled back. If it is <tt>false</tt>,
     * then the transaction (or any of sub-transactions) will be rolled back only iff
     * it (or any of sub-transactions) has been marked for <em>rollback only</em>.
     * 
     * @throws TransactionDemarcationException
     * <ul>
     *  <li>if the demarcated code changed the current transaction or a sub-transaction
     *  (by suspending and not resuming, or creating and not committing). If this happens
     *  then the state of the whole transaction is undefined.</li>
     *  <li>if a <tt>javax.transaction.SystemException</tt> is thrown during transaction
     *  processing. The state of the whole transaction is undefined if this happens.</li>
     * </ul>
     **/
    public synchronized void end(final boolean rollback) throws TransactionDemarcationException {
        boolean doRollback = rollback;
        if (!doRollback) {
            try {
                for (final TransactionDemarcation td: tds) {
                    /* if the transaction is to be or already is rolled back then all other transactions
                       are to roll back. */
                    final int status = td.getTransaction().getStatus();
                    final boolean forRollback = (status == Status.STATUS_MARKED_ROLLBACK ||
                                                status == Status.STATUS_ROLLEDBACK ||
                                                status == Status.STATUS_ROLLING_BACK);
                    if (forRollback) {
                        doRollback = true;
                        break;
                    }
                }
            } catch (SystemException ex) {
                /* SystemException indicates that an unexpected error condition has been encountered
                   that prevents future transaction services from proceeding. */
                throw new TransactionDemarcationException(ex);
            }
        }
        // this multi-commit/rollback operation works only assuming that tryEndTransaction always returns success
        boolean allSucceded = true;
        // it is always better to release resources in the reverse order as they were acquired
        for (int i = tds.length - 1; i >= 0; --i) {
            allSucceded &= tryEndTransaction(tds[i], doRollback);
        }
        if (!allSucceded) {
            throw new TransactionDemarcationException(
                    (doRollback ? "Rollback" : "Commit") + " of some of the repository transactions have failed");
        }
    }
    
    private boolean tryEndTransaction(final TransactionDemarcation td, final boolean rollback) {
        try {
            td.end(rollback);
            return true;
        } catch (TransactionDemarcationException ex) {
            if (log.isLoggingError()) {
                log.logError(ex);
            }
            return false;
        }
    }
}
