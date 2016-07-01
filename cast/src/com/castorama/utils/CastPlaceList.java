package com.castorama.utils;

import atg.commerce.util.PlaceList;
import atg.nucleus.ServiceException;

public class CastPlaceList extends PlaceList {

	@Override
	public void doStartService() throws ServiceException {
	    if (mInitStrings == null || mInitStrings.length == 0)
	        mPlaces = null;
	      else if (mInitStrings.length % 3 != 0)
	        throw new ServiceException("Number of initialization strings must be divisible on 3.");
	      else
	      {
	        int place = 0;
	        int limit = mInitStrings.length;
	        CastPlace[] places = new CastPlace[limit / 3];
	        for (int n = 0 ; n < limit ; n += 3)
	        {
	          if (isLoggingDebug())
	            logDebug("Creating place for code " + mInitStrings[n] + " named " + mInitStrings[n+1]);
	          places[place++] = new CastPlace(mInitStrings[n], mInitStrings[n+1], mInitStrings[n+2]);
	        }
	        mPlaces = places;
	      }
	}
	
	  public Place getPlaceForId(String pId)
	  {
	    int i=0;
	    CastPlace placeList[] = (CastPlace[])getPlaces();
	    for(i=0;i<placeList.length;i++) {
	    	CastPlace place = placeList[i];
	      if(pId.equals(place.getId()))
	        return place;

	    }
	    return null;
	  }
	
	
  public static class CastPlace extends Place {
	    String mId;

	    public CastPlace(String code, String name, String id) {
	    	super(code, name);
	    	mId = id.trim();
	    }

	    public String getId()        { return mId; }
	  }
	

    /**
     * <p>Searches for a place by the given place name. The first found place with the given name is returned.
     * If the place is not found then {@code null} is returned. The names are compared ignoring case for the default locale.</p>
     * 
     * @param placeName the name of the place to search for.
     * 
     * @return the first found place with the given name, or {@code null} if there is no place with this name.
     * 
     * @throws NullPointerException if the name is {@code null}.
     */
    // TODO think of optimising it by using better (than an unsorted array) data structures (e.g. mapping name->code)
    public Place getPlaceForName(final String placeName) {
        if (placeName == null) {
            throw new NullPointerException("placeName");
        }
        for (final Place place : getPlaces()) {
            String name = place.getDisplayName();
            if (name != null) {
                // CastPlaceList does not trim trim whitespaces while parsing the config file. Have to do this here
                // TODO trim names while parsing the config file and remove this trim here.
                name = name.trim();
            }
            if (placeName.equalsIgnoreCase(place.getDisplayName().trim())) {
                return place;
            }
        }
        return null;
    }
}
