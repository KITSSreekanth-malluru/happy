package com.castorama.droplet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.json.JSONArray;
import atg.json.JSONException;
import atg.json.JSONObject;
import atg.json.JSONTokener;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.castorama.CastConfiguration;
import com.castorama.utils.ContextTools;
import com.castorama.utils.StoreTools;
import com.castorama.utils.StoreTools.StoreToDistance;
import com.google.gson.GsonBuilder;

/**
 * Droplet for retrieving CLOSEST_STORES_NUMBER nearest stores according to
 * user's latitude/longitude position.
 *
 * @author EPAM Team
 */
public class GeolocationDroplet extends DynamoServlet {
    /**
     * Latitude parameter name.
     */
    private static final String PARAM_LATITUDE = "latitude";

    /**
     * Longitude parameter name.
     */
    private static final String PARAM_LONGITUDE = "longitude";

    /**
     * IsFrance parameter name.
     */
    private static final String PARAM_OUT_ISFRANCE = "isFrance";
    
    /** 
     * storeId parameter name 
     */
    private static final String PARAM_STORE_ID = "storeId";
    
    /**
     * postalCode parameter name
     */
    private static final String PARAM_POSTAL_CODE = "postalCode";

    /**
     * OUTPUT parameter name.
     */
    private static final String OUTPUT = "output";

    /**
     * PARAM_OUT_BANNERINFO parameter name.
     */
    private static final String PARAM_OUT_GEOPOPUPINFO = "info";

    /**
     * GEOCODING_CLIENT_PARAM constant.
     */
    private static final String GEOCODING_CLIENT_PARAM = "client";

    /**
     * GEOCODING_SIGNATURE_PARAM constant.
     */
    private static final String GEOCODING_SIGNATURE_PARAM = "signature";

    /**
     * GEOCODING_ADDRESS_PARAM constant
     */
    private static final String GEOCODING_ADDRESS_PARAM = "address";

    /**
     * GEO_RESPONSE_ENCODING constant.
     */
    private static final String GEO_RESPONSE_ENCODING = "utf-8";

    /**
     * GEO_RESPONSE_ROOT_ELEMENT constant.
     */
    private static final String GEO_RESPONSE_ROOT_ELEMENT = "results";
    
    /**
     * GEO_RESPONSE_GEOMETRY_ELEMENT constant
     */
    private static final String GEO_RESPONSE_GEOMETRY_ELEMENT = "geometry";
    
    private static final String GEO_RESPONSE_LOCATION_ELEMENT = "location";
    
    private static final String GEO_RESPONSE_LATITUDE_ELEMENT = "lat";
    
    private static final String GEO_RESPONSE_LONGITUDE_ELEMENT = "lng";
    
    private static final String GEO_RESPONSE_STATUS = "status";

    private static final String GEO_RESPONSE_FRANCE_CODE = "FR";
    
    private static final String GEO_RESPONSE_SHORT_NAME_ELEMENT = "short_name";
    
    private static final String GEO_RESPONSE_ADDRESS_COMPONENTS = "address_components";

    /**
     * FORMATTED_ADDRESS constant.
     */
    private static final String FORMATTED_ADDRESS = "formatted_address";

    private static final String PARAM_NUMBER_STORES_IN_LIST = "numberOfStoresInList";

    private static final String ERROR_INVALID_PARAMETERS = "invalid latitude/longitude/numberOfStoresInList parameters";

    private static final String ERROR_MISSING_PARAMETERS = "missing latitude/longitude parameters";

    /**
     * Store tools.
     */
    private StoreTools storeTools;

    /**
     * Context tools.
     */
    private ContextTools contextTools;

    /**
     * Configuration property.
     */
    private CastConfiguration configuration;

    /**
     * connectTimeout property.
     */
    private int connectTimeout;

    /**
     * readTimeout property.
     */
    private int readTimeout;

    /**
     * Service method to handle geolocation actions.
     *
     * @param pRequest  parameter
     * @param pResponse parameter
     * @throws IOException      exception
     * @throws ServletException exception
     */
    @Override
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {
        // Get lat/lng from URL parameters
        Double lat = null, lng = null;
        Integer numberOfStoresInListInt = null;
        GeolocationServiceResponse serviceResponse = new GeolocationServiceResponse();
        String pLatitude = pRequest.getParameter(PARAM_LATITUDE);
        String pLongitude = pRequest.getParameter(PARAM_LONGITUDE);
        String numberOfStoresInList = pRequest.getParameter(PARAM_NUMBER_STORES_IN_LIST);
        String storeId = pRequest.getParameter(PARAM_STORE_ID);
        String postalCode = pRequest.getParameter(PARAM_POSTAL_CODE);
        List<GeolocationStoreInfo> stores = null;
        
        if (!StringUtils.isBlank(numberOfStoresInList)) {
            numberOfStoresInListInt = Integer.parseInt(numberOfStoresInList);
        }
        if (!StringUtils.isEmpty(pLatitude) && !StringUtils.isEmpty(pLongitude)) {
            try {
                if (!StringUtils.isBlank(pLatitude)) {
                    lat = Double.valueOf(pLatitude);
                }
                if (!StringUtils.isBlank(pLongitude)) {
                    lng = Double.valueOf(pLongitude);
                }
                boolean isFrance = false; 
                if(configuration.isGoogleApiEnabled()){
                	isFrance = isFrance(lat, lng);
                }
                serviceResponse.setFrance(isFrance);
                if(isFrance){
	                if (lat != null && lng != null) {
	                    if (numberOfStoresInListInt == null) {
	                        stores = fillClosestStores(lat, lng);
	                    } else {
	                        stores = fillClosestStores(lat, lng, numberOfStoresInListInt);
	                    }
	                    //one of parameters lat/lng are missed
	                } else {
	                    serviceResponse.setError(ERROR_MISSING_PARAMETERS);
	                } 
                } else {
                    stores = fillAllStores();
                }
                //if stores is null result json will not contain property "stores"
                serviceResponse.setStores(stores);
            } catch (Exception e) {
                if (isLoggingError()) {
                    logError("Error during detect user geolocation: invalid latitude/longitude/numberOfStoresInList parameters: " + e.getMessage());
                }
                serviceResponse.setError(ERROR_INVALID_PARAMETERS);
            }
        } else if (!StringUtils.isBlank(storeId)){
        	RepositoryItem store = storeTools.getStore(storeId);
        	lat = (Double) store.getPropertyValue(StoreTools.LATITUDE);
        	lng = (Double) store.getPropertyValue(StoreTools.LONGITUDE);
        	if(lat != null && lng != null){
        		if (numberOfStoresInListInt == null) {
                    stores = fillClosestStores(lat, lng);
                } else {
                    stores = fillClosestStores(lat, lng, numberOfStoresInListInt);
                }
        	} else {
        		stores = fillAllStores();
        	}
    		serviceResponse.setStores(stores);
        } else if (!StringUtils.isBlank(postalCode)) { 
        	stores = fillClosestStores(postalCode);
        	serviceResponse.setStores(stores);
        } else {
            stores = fillAllStores();
            serviceResponse.setStores(stores);
        }
        pRequest.setParameter(PARAM_OUT_GEOPOPUPINFO, serviceResponse);
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
    }

    /**
     * Determine which popup type need to display: in-France or not-in-France.
     *
     * @param lat - latitude of user's geolocation.
     * @param lng - longitude of user's geolocation.
     * @return isFrance
     * @throws IOException   exception
     * @throws JSONException exception
     */
    private boolean isFrance(Double lat, Double lng) throws JSONException, IOException {
        // not in france in case of html5 geolocation failed or same other cases
        if (lat == 0 && lng == 0) {
            return false;
        }

        boolean result = false;
        URLConnection connection = null;
        InputStream is = null;
        InputStreamReader reader = null;
        try {
            String reverseGeocodingUrl = String.format(getConfiguration().getGoogleReverseGeocodingAPIUrl(), lat, lng);
            String clientId = getConfiguration().getGoogleReverseGeocodingClientId();
            String encryptionkey = getConfiguration().getGoogleReverseGeocodingSignature();
            if (clientId != null && !"".equals(clientId) && encryptionkey != null && !"".equals(encryptionkey)) {
                StringBuilder urlSB = new StringBuilder(reverseGeocodingUrl);
                char ampSign = '&';
                char eqSign = '=';
                urlSB.append(ampSign).append(GEOCODING_CLIENT_PARAM).append(eqSign).append(clientId);
                //.append(ampSign).append(GEOCODING_SIGNATURE_PARAM).append(eqSign).append(signature);
                UrlSigner signer = new UrlSigner(encryptionkey);
                URL url = new URL(urlSB.toString());
                String signature = signer.signRequest(url.getPath(), url.getQuery());

                urlSB.append(ampSign).append(GEOCODING_SIGNATURE_PARAM).append(eqSign).append(signature);
                reverseGeocodingUrl = urlSB.toString();
            }

            connection = new URL(reverseGeocodingUrl).openConnection();
            connection.setConnectTimeout(getConnectTimeout());
            connection.setReadTimeout(getReadTimeout());
            connection.connect();
            is = connection.getInputStream();
            reader = new InputStreamReader(is, GEO_RESPONSE_ENCODING);
            JSONTokener jsonTokener = new JSONTokener(inputToString(reader));

            JSONObject root = new JSONObject(jsonTokener);
            JSONArray geocodingComponents = root.getJSONArray(GEO_RESPONSE_ROOT_ELEMENT);
            if (geocodingComponents.size() != 0) {
                JSONObject countryComponent = (JSONObject) geocodingComponents.get(geocodingComponents.length() - 1);

                String originalCountry = getConfiguration().getGoogleGeolocationCountry().trim();
                String userCountry = countryComponent.getString(FORMATTED_ADDRESS);
                result = originalCountry.equals(userCountry);
            } else {
                if (isLoggingDebug()) {
                    logDebug("No country info was returned from Google during geolocation detection: empty response. lat:" + lat + ", lng:" + lng);
                }
                result = false;
            }
        } catch (InvalidKeyException e) {
            if (isLoggingError())
                logError("Error during signature compute.", e);
        } catch (NoSuchAlgorithmException e) {
            if (isLoggingError())
                logError("Error during signature compute.", e);
        } catch (URISyntaxException e) {
            if (isLoggingError())
                logError("Error during signature compute.", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    if (isLoggingError()) {
                        logError("Exception during closing reader.", e);
                    }
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    if (isLoggingError())
                        logError("Error during closing input stream.", e);
                }
            }
            if ((connection != null) && (connection instanceof HttpURLConnection)) {
                try {
                    ((HttpURLConnection) connection).disconnect();
                } catch (Exception e) {
                    if (isLoggingError())
                        logError("Error during disconnect connection.", e);
                }
            }
        }
        return result;
    }

    /**
     * Converts input data to string.
     *
     * @param reader parameter
     * @return converted to string data.
     * @throws IOException exception
     */
    private String inputToString(Reader reader) throws IOException {
        StringBuffer buff = new StringBuffer();
        int i;

        while ((i = reader.read()) != -1) {
            char c = (char) i;
            buff.append(c);
        }

        return buff.toString();
    }

    /**
     * Method for filling up the JSON with CLOSEST_STORES_NUMBER nearest stores
     * according to user's latitude/longitude position.
     *
     * @param lat - latitude of user's geolocation.
     * @param lng - longitude of user's geolocation.
     * @return nearest stores
     * @throws RepositoryException
     */
    private List<GeolocationStoreInfo> fillClosestStores(Double lat, Double lng, Integer numberIfStoresInList) {
        List<GeolocationStoreInfo> stores = null;
        if (numberIfStoresInList == null || numberIfStoresInList <= 0) {
            return null;
        }
        Set<StoreToDistance> set = storeTools.findNClothestStores(lat, lng, numberIfStoresInList);
        if (set != null) {
            stores = new ArrayList<GeolocationStoreInfo>(numberIfStoresInList);
            for (StoreToDistance sd : set) {
                RepositoryItem repoStore = storeTools.getStore(sd.getStoreId());
                GeolocationStoreInfo store = new GeolocationStoreInfo();
                store.setId(sd.getStoreId());
                store.setDistance(sd.getDistance());
                store.setCC((Boolean) repoStore.getPropertyValue(StoreTools.ISCC));
                store.setLatitude((Double) repoStore.getPropertyValue(StoreTools.LATITUDE));
                store.setLongitude((Double) repoStore.getPropertyValue(StoreTools.LONGITUDE));
                store.setPostalCode((String) storeTools.getStoreAddress(repoStore).getPropertyValue(StoreTools.POSTAL_CODE));
                store.setStreet((String) storeTools.getStoreAddress(repoStore).getPropertyValue(StoreTools.STREET));
                store.setStoreId((String) repoStore.getPropertyValue(StoreTools.STORE_ID));
                store.setStoreName((String) repoStore.getPropertyValue(StoreTools.NOM));
                store.setStoreUrl((String) repoStore.getPropertyValue(StoreTools.STORE_URL));
                stores.add(store);
            }
        }
        return stores;
    }

    private List<GeolocationStoreInfo> fillClosestStores(Double lat, Double lng) {
        RepositoryItem[] stores = storeTools.getAllStores();
        return fillClosestStores(lat, lng, stores.length);
    }
    
    private List<GeolocationStoreInfo> fillClosestStores(String postalCode){
    	if(null == postalCode){
    		return null;
    	}
    	List<GeolocationStoreInfo> resultList = null;
    	URLConnection connection = null;
    	InputStream is = null;
    	InputStreamReader reader = null;
    	try {
	    	String reverseGeocodingUrl = String.format(getConfiguration().getGoogleReverseGeocodingAPIUrl(), "", "");
	        String addressParam = new String(postalCode.getBytes(), "UTF-8") + "%20france";
            char ampSign = '&';
            char eqSign = '=';
	    	StringBuilder sb = new StringBuilder(reverseGeocodingUrl);
	    	sb.append(ampSign).append(GEOCODING_ADDRESS_PARAM).append(eqSign).append(addressParam);
	    	reverseGeocodingUrl = sb.toString();
	        String clientId = getConfiguration().getGoogleReverseGeocodingClientId();
	        String encryptionkey = getConfiguration().getGoogleReverseGeocodingSignature();
	        if (clientId != null && !"".equals(clientId) && encryptionkey != null && !"".equals(encryptionkey)) {
	            StringBuilder urlSB = new StringBuilder(reverseGeocodingUrl);
	            urlSB.append(ampSign).append(GEOCODING_CLIENT_PARAM).append(eqSign).append(clientId);
	            //.append(ampSign).append(GEOCODING_SIGNATURE_PARAM).append(eqSign).append(signature);
	            UrlSigner signer = new UrlSigner(encryptionkey);
	            URL url = new URL(urlSB.toString());
	            String signature = signer.signRequest(url.getPath(), url.getQuery());
	
	            urlSB.append(ampSign).append(GEOCODING_SIGNATURE_PARAM).append(eqSign).append(signature);
	            reverseGeocodingUrl = urlSB.toString();
	        }
	
	        connection = new URL(reverseGeocodingUrl).openConnection();
	        connection.setConnectTimeout(getConnectTimeout());
	        connection.setReadTimeout(getReadTimeout());
	        connection.connect();
	        is = connection.getInputStream();
	        reader = new InputStreamReader(is, GEO_RESPONSE_ENCODING);
	        JSONTokener jsonTokener = new JSONTokener(inputToString(reader));

            JSONObject root = new JSONObject(jsonTokener);
            JSONArray geocodingComponents = root.getJSONArray(GEO_RESPONSE_ROOT_ELEMENT);
            String status = root.getString(GEO_RESPONSE_STATUS);
            if ((geocodingComponents.size() != 0) && ("OK".equals(status))) {
            	int numberOfResults = geocodingComponents.size();
            	int curIndex = 0;
            	JSONObject countryComponent = null;
            	while(curIndex < numberOfResults){
            		JSONArray currentResult = geocodingComponents.getJSONObject(curIndex).getJSONArray(GEO_RESPONSE_ADDRESS_COMPONENTS);
            		for(Object address : currentResult){
            			String shortName = ((JSONObject) address).getString(GEO_RESPONSE_SHORT_NAME_ELEMENT);
            			if(GEO_RESPONSE_FRANCE_CODE.equalsIgnoreCase(shortName)){
            				countryComponent = geocodingComponents.getJSONObject(curIndex);
            				curIndex = numberOfResults;
            				break;
            			}
            		}
            		curIndex++;
            	}
                
                if(null != countryComponent){
	                JSONObject geometry = (JSONObject) countryComponent.get(GEO_RESPONSE_GEOMETRY_ELEMENT);
	                JSONObject location = (JSONObject) geometry.get(GEO_RESPONSE_LOCATION_ELEMENT);
	                String latitude = location.getString(GEO_RESPONSE_LATITUDE_ELEMENT);
	                String longitude = location.getString(GEO_RESPONSE_LONGITUDE_ELEMENT);
	                if (!StringUtils.isBlank(latitude) && !StringUtils.isBlank(longitude)) {
	                    Double lat = Double.valueOf(latitude);
	                    Double lng = Double.valueOf(longitude);
	                    resultList = fillClosestStores(lat, lng);
	                }
                }
            } else {
                if (isLoggingDebug()) {
                    logDebug("No results for postal code" + postalCode + " or ");
                }
            }
    	} catch (InvalidKeyException e) {
            if (isLoggingError())
                logError("Error during signature compute.", e);
        } catch (NoSuchAlgorithmException e) {
            if (isLoggingError())
                logError("Error during signature compute.", e);
        } catch (URISyntaxException e) {
            if (isLoggingError())
                logError("Error during signature compute.", e);
        } catch(IOException e){
        	if (isLoggingError())
                logError("Unable to define latlng by postalCode.", e);
        } catch (JSONException e) {
        	if (isLoggingError())
                logError("Unable to parse JSON.", e);
		}finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    if (isLoggingError()) {
                        logError("Exception during closing reader.", e);
                    }
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    if (isLoggingError())
                        logError("Error during closing input stream.", e);
                }
            }
            if ((connection != null) && (connection instanceof HttpURLConnection)) {
                try {
                    ((HttpURLConnection) connection).disconnect();
                } catch (Exception e) {
                    if (isLoggingError())
                        logError("Error during disconnect connection.", e);
                }
            }
        }
		return resultList;
    }

    private List<GeolocationStoreInfo> fillAllStores() {
        RepositoryItem[] stores = storeTools.getAllStores();
        List<GeolocationStoreInfo> result = new ArrayList<GeolocationStoreInfo>(stores.length);
        if ((stores != null) || (stores.length > 0)) {
            for (RepositoryItem item : stores) {
                GeolocationStoreInfo store = new GeolocationStoreInfo();
                store.setId((String) item.getPropertyValue(StoreTools.ID));
                store.setCC((Boolean) item.getPropertyValue(StoreTools.ISCC));
                store.setLatitude((Double) item.getPropertyValue(StoreTools.LATITUDE));
                store.setLongitude((Double) item.getPropertyValue(StoreTools.LONGITUDE));
                store.setPostalCode((String) storeTools.getStoreAddress(item).getPropertyValue(StoreTools.POSTAL_CODE));
                store.setStreet((String) storeTools.getStoreAddress(item).getPropertyValue(StoreTools.STREET));
                store.setStoreId((String) item.getPropertyValue(StoreTools.STORE_ID));
                store.setStoreName((String) item.getPropertyValue(StoreTools.NOM));
                store.setStoreUrl((String) item.getPropertyValue(StoreTools.STORE_URL));
                result.add(store);
            }
        }
        Collections.sort(result, new PostalCodeComparator());
        return result;
    }

    /**
     * Returns storeTools property.
     *
     * @return storeTools property.
     */
    public StoreTools getStoreTools() {
        return storeTools;
    }

    /**
     * Sets the value of the storeTools property.
     *
     * @param storeTools parameter to set.
     */
    public void setStoreTools(StoreTools storeTools) {
        this.storeTools = storeTools;
    }

    /**
     * Returns contextTools property.
     *
     * @return the contextTools
     */
    public ContextTools getContextTools() {
        return contextTools;
    }

    /**
     * Sets the value of the contextTools property.
     *
     * @param contextTools the contextTools to set
     */
    public void setContextTools(ContextTools contextTools) {
        this.contextTools = contextTools;
    }

    /**
     * @return the configuration
     */
    public CastConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * @param configuration the configuration to set
     */
    public void setConfiguration(CastConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * @return the connectTimeout
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * @param connectTimeout the connectTimeout to set
     */
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * @return the readTimeout
     */
    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * @param readTimeout the readTimeout to set
     */
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public class GeoPopupInfo {
        private List<StoreInfo> stores;
        private String bannerId;
        private String nearestCastStoreId;

        /**
         * @return the stores
         */
        public List<StoreInfo> getStores() {
            return stores;
        }

        /**
         * @param stores the stores to set
         */
        public void setStores(List<StoreInfo> stores) {
            this.stores = stores;
        }

        /**
         * @return the bannerId
         */
        public String getBannerId() {
            return bannerId;
        }

        /**
         * @param bannerId the bannerId to set
         */
        public void setBannerId(String bannerId) {
            this.bannerId = bannerId;
        }

        /**
         * @return the nearestCastStoreId
         */
        public String getNearestCastStoreId() {
            return nearestCastStoreId;
        }

        /**
         * @param nearestCastStoreId the nearestCastStoreId to set
         */
        public void setNearestCastStoreId(String nearestCastStoreId) {
            this.nearestCastStoreId = nearestCastStoreId;
        }
    }

    public class StoreInfo {
        private String id;
        private String castStoreId;
        private boolean iscc;
        private String nom;
        private String storeUrl;

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @param id the id to set
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * @return the castStoreId
         */
        public String getCastStoreId() {
            return castStoreId;
        }

        /**
         * @param castStoreId the castStoreId to set
         */
        public void setCastStoreId(String castStoreId) {
            this.castStoreId = castStoreId;
        }

        /**
         * @return the iscc
         */
        public boolean isIscc() {
            return iscc;
        }

        /**
         * @param iscc the iscc to set
         */
        public void setIscc(boolean iscc) {
            this.iscc = iscc;
        }

        /**
         * @return the nom
         */
        public String getNom() {
            return nom;
        }

        /**
         * @param nom the nom to set
         */
        public void setNom(String nom) {
            this.nom = nom;
        }

        /**
         * @return the storeUrl
         */
        public String getStoreUrl() {
            return storeUrl;
        }
        /**
         * @param storeUrl the storeUrl to set
         *//*
        public void setStoreUrl(String storeUrl) {
            this.storeUrl = storeUrl;
        }*/
    }

    public class GeolocationStoreInfo {
        private String id;
        private String storeId;
        private String storeName;
        private String postalCode;
        private String storeUrl;
        private boolean isCC;
        private Double distance;
        private Double latitude;
        private Double longitude;
        private String street;

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getStoreId() {
            return storeId;
        }

        public void setStoreId(String storeId) {
            this.storeId = storeId;
        }

        public String getStoreName() {
            return storeName;
        }

        public void setStoreName(String storeName) {
            this.storeName = storeName;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        public String getStoreUrl() {
            return storeUrl;
        }

        public void setStoreUrl(String storeUrl) {
            this.storeUrl = storeUrl;
        }

        public boolean isCC() {
            return isCC;
        }

        public void setCC(boolean isCC) {
            this.isCC = isCC;
        }

        public Double getDistance() {
            return distance;
        }

        public void setDistance(Double distance) {
            this.distance = distance;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }
    }

    public class GeolocationServiceResponse {

        private List<GeolocationStoreInfo> stores;
        private boolean isFrance;
        private String error;

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public List<GeolocationStoreInfo> getStores() {
            return stores;
        }

        public void setStores(List<GeolocationStoreInfo> stores) {
            this.stores = stores;
        }

        public boolean isFrance() {
            return isFrance;
        }

        public void setFrance(boolean france) {
            isFrance = france;
        }

        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this);
        }

    }
    
    public class PostalCodeComparator implements Comparator<GeolocationStoreInfo>{

		@Override
		public int compare(GeolocationStoreInfo o1, GeolocationStoreInfo o2) {
			try {
				Integer o1PostCode = Integer.valueOf(o1.getPostalCode());
				Integer o2PostCode = Integer.valueOf(o2.getPostalCode());
				if(o1PostCode < o2PostCode){
					return -1;
				} else if (o1PostCode > o2PostCode) {
					return 1;
				} else {
					return 0;
				}
			} catch (NumberFormatException e) {
				if(isLoggingError()){
					logError("Unable to cast postalCode (java.lang.String) to Integer");
				}
				//place store with postalCode, which can't be casted to Integer, to the end of list 
				return 1;
			}
		}
    	
    }
}
