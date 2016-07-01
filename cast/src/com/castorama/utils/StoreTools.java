/**
 *
 */
package com.castorama.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import atg.nucleus.GenericService;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

/**
 * Helper class for store functionality.
 *
 * @author EPAM Team
 */
public class StoreTools extends GenericService {
    /** MAGASIN constant. */
    public static final String MAGASIN = "magasin";

    /** LATITUDE constant. */
	public static final String LATITUDE = "latitude";

    /** LONGITUDE constant. */
	public static final String LONGITUDE = "longitude";

	/** MAGASIN_QUERY constant. */
    private static final String MAGASIN_QUERY = "entite.adresse.departement.numero != 999";

    /** ENTITE constant. */
    private static final String ENTITE = "entite";

    /** ADRESSE constant. */
    private static final String ADRESSE = "adresse";

    /** STORE_ID constant. */
    public static final String STORE_ID = "storeId";

    /** NOM constant. */
    public static final String NOM = "nom";
    
    /** Postal code property name constant   */
    public static final String POSTAL_CODE = "cp";
    
    /** LOCAL_PRICES constant. */
    public static final String LOCAL_PRICES = "localPrix";

    /** ISCC constant. */
    public static final String ISCC = "retraitMagasin";

    /** STORE_URL constant. */
    public static final String STORE_URL = "storeUrl";
    
    /** ID constant    */
    public static final String ID = "id";
    
    /** STREET constant     */
    public static final String STREET = "rue";
    
    /** TREE_MAP_COMPARATOR comparator. */
    private static final Comparator<StoreToDistance> STORE_BY_DISTANCE_COMPARATOR =
        new Comparator<StoreToDistance>() {
            public int compare(final StoreToDistance pO1, final StoreToDistance pO2) {
                int c = (pO1.getDistance()).compareTo(pO2.getDistance());
                if (c != 0)
                    return c;
                else
                    return (pO1.getStoreId()).compareTo(pO2.getStoreId());
            }
        };

    /** mStoreRepository property. */
    private Repository mStoreRepository;

    /**
     * Returns storeRepository property.
     *
     * @return storeRepository property.
     */
    public Repository getStoreRepository() {
        return mStoreRepository;
    }

    /**
     * Sets the value of the storeRepository property.
     *
     * @param pStoreRepository parameter to set.
     */
    public void setStoreRepository(Repository pStoreRepository) {
        mStoreRepository = pStoreRepository;
    }

    /**
     * Method find closest store by pLatitude and pLongitude (double values for
     * this parameters should be provided) passed. If pProximityDistance
     * parameter was passed then method checked whether closest store is
     * situated in provided proximity, otherwise (pProximityDistance not passed)
     * then ATG store id for closest store is returned.
     *
     * @param  pLatitude          parameter
     * @param  pLongitude         parameter
     * @param  pProximityDistance parameter
     *
     * @return atg id for store.
     */
    public String findClothestStoreAccordingProximity(Double pLatitude, Double pLongitude, Double pProximityDistance) {
        String storeId = null;

        TreeSet<StoreToDistance> storeDistanceToStoreId =
            (TreeSet<StoreToDistance>) calculateDistancesForStores(pLatitude, pLongitude);
        if ((storeDistanceToStoreId != null) && !storeDistanceToStoreId.isEmpty()) {
            Double distance = storeDistanceToStoreId.first().getDistance();
            if ((distance != null)) {
                if ((((pProximityDistance != null) && (distance <= pProximityDistance)) ||
                         (pProximityDistance == null))) {
                    storeId = storeDistanceToStoreId.first().getStoreId();
                } else {
                    storeId = storeDistanceToStoreId.first().getStoreId();
                }
            }

        }

        return storeId;
    }

    /**
     * Find N closest stores.
     *
     * @param  pLatitude             parameter
     * @param  pLongitude            parameter
     * @param  pNumberOfStoresInList parameter
     *
     * @return N closest stores.
     */
    public SortedSet<StoreToDistance> findNClothestStores(Double pLatitude, Double pLongitude,
                                                          Integer pNumberOfStoresInList) {
        SortedSet<StoreToDistance> subStoreDistanceToStoreId = null;
        TreeSet<StoreToDistance> storeDistanceToStoreId =
            (TreeSet<StoreToDistance>) calculateDistancesForStores(pLatitude, pLongitude);

        if ((storeDistanceToStoreId != null) && !storeDistanceToStoreId.isEmpty()) {
            if ((pNumberOfStoresInList == null) || (pNumberOfStoresInList <= 0)) {
                pNumberOfStoresInList = 1;
            }
            if (storeDistanceToStoreId.size() > pNumberOfStoresInList) {
                subStoreDistanceToStoreId =
                    storeDistanceToStoreId.subSet(storeDistanceToStoreId.first(),
                                                  storeDistanceToStoreId.toArray(new StoreToDistance[] {})[pNumberOfStoresInList]);
            } else {
                subStoreDistanceToStoreId = storeDistanceToStoreId;
            }

        }
        return subStoreDistanceToStoreId;
    }

    /**
     * Returns list stores with distances.
     *
     * @param  pLatitude  parameter
     * @param  pLongitude parameter
     *
     * @return list stores with distances.
     */
    public SortedSet<StoreToDistance> findClothestStores(Double pLatitude, Double pLongitude) {
        SortedSet<StoreToDistance> subStoreDistanceToStoreId =
            (TreeSet<StoreToDistance>) calculateDistancesForStores(pLatitude, pLongitude);

        return subStoreDistanceToStoreId;
    }

    /**
     * Service calculates distances for provided set of stores.
     *
     * @param  pLatitude  parameter
     * @param  pLongitude parameter
     *
     * @return distances for provided set of stores.
     */
    public Set<StoreToDistance> calculateDistancesForStores(Double pLatitude, Double pLongitude) {
        Set<StoreToDistance> storeDistanceToStoreId = new TreeSet<StoreToDistance>(STORE_BY_DISTANCE_COMPARATOR);
        Repository rep = getStoreRepository();
        if (rep != null) {
            try {
                RepositoryView repView = rep.getView(MAGASIN);
                RqlStatement storesRQL = RqlStatement.parseRqlStatement(MAGASIN_QUERY);

                RepositoryItem[] stores = storesRQL.executeQuery(repView, null);
                if ((stores != null) && (stores.length > 0)) {
                    for (RepositoryItem store : stores) {
                        if (store != null) {
                            Double storeLatitude = (Double) store.getPropertyValue(LATITUDE);
                            Double storeLongitude = (Double) store.getPropertyValue(LONGITUDE);
                            if ((storeLatitude != null) && (storeLongitude != null)) {
                                Double distanceToStore = new BigDecimal(
                                        calculateDistance(pLatitude, pLongitude, storeLatitude, storeLongitude)).setScale(1, RoundingMode.HALF_UP).doubleValue();
                                if ((distanceToStore != null) && (distanceToStore >= 0)) {
                                    storeDistanceToStoreId.add(new StoreToDistance(store.getRepositoryId(),
                                                                                   distanceToStore));
                                }
                            }
                        }
                    }
                }

            } catch (RepositoryException e) {
                if (isLoggingError()) {
                    logError("Error occured in com.castorama.mobile.store.StoreTools.findClothestStore(...): ",e);
                }
            }  // end try-catch
        }  // end if
        return storeDistanceToStoreId;
    }
    
    public RepositoryItem[] getAllStores(){
    	Repository storeRep = getStoreRepository();
    	RepositoryItem[] stores = null;
    	try {
    		RepositoryView repView = storeRep.getView(MAGASIN);
    		RqlStatement storesRQL = RqlStatement.parseRqlStatement(MAGASIN_QUERY);
            stores = storesRQL.executeQuery(repView, null);
    		
    	} catch(RepositoryException exc){
    		if(isLoggingError()){
    			logError("Error occured in com.castorama.utils.StoreTools.getAllStores(...): ",exc);
    		}
    	}
    	return stores;
    }

    /**
     * Returns storeAddress property.
     *
     * @param  pStore parameter to set.
     *
     * @return storeAddress property.
     */
    public RepositoryItem getStoreAddress(RepositoryItem pStore) {
        RepositoryItem storeAddress = null;
        if (pStore != null) {
            RepositoryItem entite = (RepositoryItem) pStore.getPropertyValue(ENTITE);
            if (entite != null) {
                storeAddress = (RepositoryItem) entite.getPropertyValue(ADRESSE);
            }
        }

        return storeAddress;

    }

    /**
     * Returns storeAddress property.
     *
     * @param  pStoreId parameter to set.
     *
     * @return storeAddress repository item.
     *
     * @throws RepositoryException
     */
    public RepositoryItem getStoreAddress(String pStoreId) throws RepositoryException {
        RepositoryItem storeAddress = null;
        Repository rep = getStoreRepository();
        if ((rep != null) && (pStoreId != null)) {
            RepositoryItem store = rep.getItem(pStoreId, MAGASIN);
            if (store != null) {
                RepositoryItem entite = (RepositoryItem) store.getPropertyValue(ENTITE);
                if (entite != null) {
                    storeAddress = (RepositoryItem) entite.getPropertyValue(ADRESSE);
                }
            }
        }

        return storeAddress;

    }

    /**
     * Returns nom property.
     *
     * @param  pStoreId parameter to set.
     *
     * @return nom.
     *
     * @throws RepositoryException
     */
    public String getStoreNom(String pStoreId) throws RepositoryException {
    	String storeNom = null;
        Repository rep = getStoreRepository();
        if ((rep != null) && (pStoreId != null)) {
            RepositoryItem store = rep.getItem(pStoreId, MAGASIN);
            if (store != null) {
                storeNom = (String) store.getPropertyValue(NOM);
            }
        }

        return storeNom;
    }

    /**
     * Calculates distance between 2 points according to Harvesine formula.
     *
     * @param  pStartLatitude   parameter
     * @param  pStartLongitude  parameter
     * @param  pFinishLatitude  parameter
     * @param  pFinishLongitude parameter
     *
     * @return distance between 2 points
     */
    public Double calculateDistance(Double pStartLatitude, Double pStartLongitude, Double pFinishLatitude,
                                    Double pFinishLongitude) {
        Double result = null;

        if ((pStartLatitude != null) && (pStartLongitude != null) && (pFinishLatitude != null) &&
                (pFinishLongitude != null)) {
            double earthRadius = 6371.010;  //in meters
            double dLat = Math.toRadians(pFinishLatitude - pStartLatitude);
            double dLng = Math.toRadians(pFinishLongitude - pStartLongitude);

            double a =
                (Math.sin(dLat / 2) * Math.sin(dLat / 2)) +
                (Math.cos(Math.toRadians(pStartLatitude)) * Math.cos(Math.toRadians(pFinishLatitude)) *
                 Math.sin(dLng / 2) * Math.sin(dLng / 2));
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double dist = earthRadius * c;
            result = dist;
        }
        return result;
    }

    /**
     * Returns store by store's id.
     *
     * @param pStoreId
     *
     * @return store
     */
    public RepositoryItem getStore(String pStoreId) {
        RepositoryItem store = null;
        Repository rep = getStoreRepository();
        if ((rep != null) && (pStoreId != null)) {
            try {
                store = rep.getItem(pStoreId, MAGASIN);
            } catch (RepositoryException e) {
                logError(e);
            }
        }
        return store;
    }

    /**
     * Returns store by castorama store's id.
     *
     * @param castStoreId
     *
     * @return store
     */
    public RepositoryItem getStoreByCastStoreId(String castStoreId) {
        RepositoryItem store = null;
        Repository rep = getStoreRepository();
        if ((rep != null) && (castStoreId != null)) {
            try {
                RqlStatement findStoreRQL = RqlStatement.parseRqlStatement("storeId = ?0");
                RepositoryView storeView = rep.getView(MAGASIN);
                Object[] rqlparams = new Object[] {castStoreId};
                RepositoryItem[] stores = findStoreRQL.executeQuery(storeView, rqlparams);
                if (stores!= null && stores.length > 0)
                    store = stores[0];
            } catch (RepositoryException e) {
                logError(e);
            }
        }
        return store;
    }

    /**
     * Returns true if store has localPrix=true or retraitMagasin=true.
     *
     * @param storeId
     *
     * @return true if store has localPrix=true or retraitMagasin=true
     */
    public boolean checkStoreLocalPricesOrClickAndCollect(String storeId) {
        RepositoryItem store = getStore(storeId);
        
        Boolean localPrices = null;
        Boolean iscc = null;
        
        if (store!=null) {
            localPrices = (Boolean)store.getPropertyValue(LOCAL_PRICES);
            iscc = (Boolean)store.getPropertyValue(ISCC);
        }
        
        if (localPrices == null) localPrices = false;
        if (iscc == null) iscc = false;
        
        return localPrices || iscc;
    }

    /**
     * Inner object for saving Store to Distance mapping.
     *
     * @author Katsiryna Sharstsiuk
     */
    public class StoreToDistance {
        /** mStoreId constant. */
        private String mStoreId;

        /** mDistance constant. */
        private Double mDistance;

        /**
         * Creates a new StoreToDistance object.
         *
         * @param pStoreId  parameter
         * @param pDistance parameter
         */
        StoreToDistance(String pStoreId, Double pDistance) {
            setStoreId(pStoreId);
            setDistance(pDistance);
        }

        /**
         * Returns storeId property.
         *
         * @return storeId property.
         */
        public String getStoreId() {
            return mStoreId;
        }

        /**
         * Sets the value of the storeId property.
         *
         * @param pStoreId parameter to set.
         */
        public void setStoreId(String pStoreId) {
            mStoreId = pStoreId;
        }

        /**
         * Returns distance property.
         *
         * @return distance property.
         */
        public Double getDistance() {
            return mDistance;
        }

        /**
         * Sets the value of the distance property.
         *
         * @param pDistance parameter to set.
         */
        public void setDistance(Double pDistance) {
            mDistance = pDistance;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + ((mDistance == null) ? 0 : mDistance.hashCode());
            result = (prime * result) + ((mStoreId == null) ? 0 : mStoreId.hashCode());
            return result;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final StoreToDistance other = (StoreToDistance) obj;
            if (mDistance == null) {
                if (other.mDistance != null) {
                    return false;
                }
            } else if (!mDistance.equals(other.mDistance)) {
                return false;
            }
            if (mStoreId == null) {
                if (other.mStoreId != null) {
                    return false;
                }
            } else if (!mStoreId.equals(other.mStoreId)) {
                return false;
            }
            return true;
        }

    }
}
