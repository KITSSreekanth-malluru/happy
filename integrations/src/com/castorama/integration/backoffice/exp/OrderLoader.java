/**
 * 
 */
package com.castorama.integration.backoffice.exp;

import static com.castorama.constantes.CastoConstantesOrders.ORDER_PROPERTY_DELIVERY_TYPE;
import static com.castorama.constantes.CastoConstantesOrders.DELIVERY_TYPE_OPTION_CLICK_AND_COLLECT;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.castorama.commerce.states.BOOrderStates;
import com.castorama.integration.backoffice.exp.ArrayItemIterator;

import atg.commerce.states.OrderStates;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

/**
 * @author Andrew_Logvinov
 *
 */
class OrderLoader {

    private static final String WEB_CONTEXT_STORE_ID = "999";

    /**
     * ORIGINE_MAGASIN_PROPERTY_NAME constant.
     */
    private static final String ORIGINE_MAGASIN_PROPERTY_NAME = "origineMagasin";

    /**
     * ID_PROPERTY_NAME constant.
     */
    private static final String ID_PROPERTY_NAME = "id";

    /**
     * NUMERO_FICHIER_EXPORT_PROPERTY_NAME constant.
     */
    private static final String NUMERO_FICHIER_EXPORT_PROPERTY_NAME = "numeroFichierExport";

    /**
     * BO_STATE_PROPERTY_NAME constant.
     */
    private static final String BO_STATE_PROPERTY_NAME = "bostate";

    /**
     * STATE_PROPERTY_NAME constant.
     */
    private static final String STATE_PROPERTY_NAME = "state";

    /**
     * EXPORT_DATE_PROPERTY_NAME constant.
     */
    private static final String EXPORT_DATE_PROPERTY_NAME = "exportdate";

    /**
     * ORDER_PROPERTY_NAME constant.
     */
    private static final String ORDER_PROPERTY_NAME = "order";

    /**
     * MAGASIN_ID_PROPERTY_NAME constant.
     */
    private static final String MAGASIN_ID_PROPERTY_NAME = "magasinId";

    private final Repository repository;

    private final Map<String, List<RepositoryItem>> magasinesOrders = new HashMap<String, List<RepositoryItem>>();

    /**
     * @param repository 
     * 
     */
    public OrderLoader(Repository repository) {
        this.repository = repository;
    }

    private String originalMagazin;

    private OrderStates orderStates;

    private BOOrderStates boOrderStates;

    /**
     * Return iterator by stores and build orders-per-store map.
     * 
     * @return iterator by stores
     * @throws RepositoryException
     */
    public Iterator<String> storesIterator() throws RepositoryException {
        RepositoryItem[] items = getExportedOrders();

        if (null == items) {
            items = new RepositoryItem[]{};
        }

        ArrayItemIterator iterator = excludeOriginalMagasin(items);

        buildStoresMap(iterator);

        return magasinesOrders.keySet().iterator();
    }


    /**
     * @param items
     * @return
     */
    private ArrayItemIterator excludeOriginalMagasin(RepositoryItem[] items) {
        ArrayItemIterator iterator = new ArrayItemIterator(items) {

            @Override
            public RepositoryItem next() {
                RepositoryItem result = super.next();

                if (result != null) {
                    RepositoryItem mag = (RepositoryItem) result.getPropertyValue(ORIGINE_MAGASIN_PROPERTY_NAME);
                    if (mag == null) {
                        return null;
                    }

                    String mag_id = "" + mag.getPropertyValue(ID_PROPERTY_NAME);
                    if (!mag_id.equals(originalMagazin)) {
                        return null;
                    }
                }

                return result;
            }
        };
        return iterator;
    }

    /**
     * Get all exported orders.
     * 
     * @return items
     * @throws RepositoryException
     */
    private RepositoryItem[] getExportedOrders() throws RepositoryException {
        MutableRepository repo = (MutableRepository) getRepository();
        RepositoryView view = repo.getView(ORDER_PROPERTY_NAME);
        QueryBuilder qb = view.getQueryBuilder();
        
        Query query = qb.createAndQuery(new Query[]{
                qb.createIsNullQuery(qb.createPropertyQueryExpression(EXPORT_DATE_PROPERTY_NAME)),
                qb.createComparisonQuery(qb.createPropertyQueryExpression(STATE_PROPERTY_NAME),
                        qb.createConstantQueryExpression(orderStates.getStateString(orderStates.getStateValue(OrderStates.INCOMPLETE))),
                        QueryBuilder.NOT_EQUALS),
                qb.createComparisonQuery(qb.createPropertyQueryExpression(BO_STATE_PROPERTY_NAME),
                        qb.createConstantQueryExpression(boOrderStates.getStateString(boOrderStates.getStateValue(BOOrderStates.VALIDE))),
                        QueryBuilder.EQUALS)
        });

        RepositoryItem[] items = view.executeQuery(query);

        return items;
    }

    /**
     * Build stores map. Put all D2H orders to 999 store, and all C&C orders in appropriate stores.
     * 
     * @param it
     */
    private void buildStoresMap( ArrayItemIterator it){
        while (it.hasNext()) {
            RepositoryItem itm = it.next();
            
            String orderDeliveryType = (String) itm.getPropertyValue(ORDER_PROPERTY_DELIVERY_TYPE);
            
            // store for d2h orders
            String ordermagasinId = WEB_CONTEXT_STORE_ID;
            // store for c&c orders
            if (orderDeliveryType.equals(DELIVERY_TYPE_OPTION_CLICK_AND_COLLECT)) {
                ordermagasinId = (String) itm.getPropertyValue(MAGASIN_ID_PROPERTY_NAME);
            }
            
            if (!magasinesOrders.containsKey(ordermagasinId)){
                List<RepositoryItem> magasinOrders = new ArrayList<RepositoryItem>();
                magasinOrders.add(itm);
                magasinesOrders.put(ordermagasinId, magasinOrders);
            } else {
                List<RepositoryItem> magasinOrders = magasinesOrders.get(ordermagasinId);
                magasinOrders.add(itm);
            }
        }
    }

    /**
     * Return iterator by orders for selected store.
     * 
     * @param magasinId
     * @return iterator by orders
     * @throws RepositoryException
     */
    public Iterator<RepositoryItem> ordersIterator(String magasinId) throws RepositoryException {
        List<RepositoryItem> orders = magasinesOrders.get(magasinId);
        RepositoryItem[] empty = {};
        RepositoryItem[] ordersArray = orders.toArray(empty);
        ArrayItemIterator iterator = new ArrayItemIterator(ordersArray);

        return iterator;
    }

    /**
     * @param itm
     * @param sequence
     * @throws RepositoryException
     */
    public void registerExported(RepositoryItem itm, String sequence) throws RepositoryException {
        MutableRepositoryItem mri = (MutableRepositoryItem) itm;
        mri.setPropertyValue(EXPORT_DATE_PROPERTY_NAME, new Date());
        mri.setPropertyValue(NUMERO_FICHIER_EXPORT_PROPERTY_NAME, sequence);

        ((MutableRepository)getRepository()).updateItem((MutableRepositoryItem) itm);
    }

    /**
     * @return the repository
     */
    public Repository getRepository() {
        return repository;
    }

    public void setOriginalMagazin(String originalMagazin) {
        this.originalMagazin = originalMagazin;
    }

    public void setBoOrderStates(BOOrderStates boOrderStates) {
        this.boOrderStates = boOrderStates;
    }

    public void setOrderStates(OrderStates orderStates) {
        this.orderStates = orderStates;
    }

}
