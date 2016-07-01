/**
 * 
 */
package com.castorama.integration.journal;

import java.util.Date;

import atg.nucleus.GenericService;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryOptions;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.SortDirective;
import atg.repository.SortDirectives;

/**
 * @author Andrew_Logvinov
 */
public class ProcessingJournalService extends GenericService {
    
    private static final String EXPORT_RESULT_STARTED = "NON TERMINE";

    private static final String EXPORT_RESULT_FINISHED = "TERMINE";

    private static final String EXPORT_RESULT_ERROR = "ERREUR";
    
    static final String VIEW_NAME = "EXPORT_JOURNALISATION";
    
    static final String FIELD_SEQUENCE = "NUMERO_DE_SEQUENCE";

    static final String FIELD_PROCESS_NAME = "TYPE_DE_FLUX";

    static final String FIELD_RESULT = "RESULTAT";
        
    /**
     * 
     */
    private Repository repository;

    /**
     * @return the repository
     */
    public Repository getRepository() {
        return repository;
    }

    /**
     * @param repository the repository to set
     */
    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    /**
     * 
     */
    public ProcessingJournalService() {
    }
    
    public JournalItem registerStarting(String processName) throws RepositoryException {
        
        RepositoryView view = getRepository().getView(VIEW_NAME);
        
        QueryBuilder qb = view.getQueryBuilder();
        Query query = qb.createComparisonQuery(
                qb.createPropertyQueryExpression(FIELD_PROCESS_NAME),
                qb.createConstantQueryExpression(processName),
                QueryBuilder.EQUALS);
        
        SortDirectives sortDirectives = new SortDirectives();
        sortDirectives.addDirective(new SortDirective(FIELD_SEQUENCE, SortDirective.DIR_DESCENDING));
        RepositoryItem [] items = view.executeQuery(query, 
                new QueryOptions(0, 1, sortDirectives, new String[]{}));
        long sequence = 0;
        if (null != items && 0 != items.length) {
            sequence = (Long) items[0].getPropertyValue(FIELD_SEQUENCE);
        }
        
        sequence++;
        
        MutableRepository repo = (MutableRepository) getRepository();
        MutableRepositoryItem itm = repo.createItem(VIEW_NAME);
        
        itm.setPropertyValue(FIELD_PROCESS_NAME, processName);
        itm.setPropertyValue(FIELD_SEQUENCE, Long.valueOf(sequence));
        itm.setPropertyValue(FIELD_RESULT, EXPORT_RESULT_STARTED);
        
        itm = (MutableRepositoryItem) repo.addItem(itm);
        
        return new JournalItem(itm);
    }
    
    public void registerFinishing(JournalItem item) throws RepositoryException {
        MutableRepositoryItem itm = item.getItem();
        
        itm.setPropertyValue(FIELD_RESULT, EXPORT_RESULT_FINISHED);
        itm.setPropertyValue("DATE_RECEPTION", new Date());
            
        MutableRepository repo = (MutableRepository) getRepository();
        
        repo.updateItem(itm);
    }

    public void registerFails(JournalItem item) throws RepositoryException {
        MutableRepositoryItem itm = item.getItem();
        
        itm.setPropertyValue(FIELD_RESULT, EXPORT_RESULT_ERROR);
        itm.setPropertyValue("DATE_RECEPTION", new Date());
        
        MutableRepository repo = (MutableRepository) getRepository();
        
        repo.updateItem(itm);
    }
    
    public long getSequence(String processName) throws RepositoryException {
        RepositoryView view = getRepository().getView(VIEW_NAME);
        
        QueryBuilder qb = view.getQueryBuilder();
        Query query = qb.createComparisonQuery(
                qb.createPropertyQueryExpression(FIELD_PROCESS_NAME),
                qb.createConstantQueryExpression(processName),
                QueryBuilder.EQUALS);
        
        SortDirectives sortDirectives = new SortDirectives();
        sortDirectives.addDirective(new SortDirective(FIELD_SEQUENCE, SortDirective.DIR_DESCENDING));
        RepositoryItem [] items = view.executeQuery(query, 
                new QueryOptions(0, 1, sortDirectives, new String[]{}));
        long sequence = 0;
        if (null != items && 0 != items.length) {
            sequence = (Long) items[0].getPropertyValue(FIELD_SEQUENCE);
        }
        
        return sequence;
    }

    public void saveSequance(String processName, long sequence) throws RepositoryException {
        MutableRepository repo = (MutableRepository) getRepository();
        MutableRepositoryItem itm = repo.createItem(VIEW_NAME);
        
        itm.setPropertyValue(FIELD_PROCESS_NAME, processName);
        itm.setPropertyValue(FIELD_SEQUENCE, Long.valueOf(sequence));
        itm.setPropertyValue(FIELD_RESULT, EXPORT_RESULT_FINISHED);
        itm.setPropertyValue("DATE_RECEPTION", new Date());
        
        itm = (MutableRepositoryItem) repo.addItem(itm);
    }
}
