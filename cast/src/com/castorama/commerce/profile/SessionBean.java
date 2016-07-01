package com.castorama.commerce.profile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import atg.nucleus.GenericService;

/**
 * Session bean component
 * @author Epam team
 * 
 */
public class SessionBean extends GenericService {

	/**
	 * Values map.
	 */
	private Map mValues;

	/**
	 * A map containing miscellaneous session values.
	 * 
	 * @return the values.
	 */
	public Map getValues() {
		if (mValues == null) {
			mValues = Collections.synchronizedMap(new HashMap());
		}
		return mValues;
	}

}
