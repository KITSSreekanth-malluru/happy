package com.castorama.droplet;

import static com.castorama.magasin.MagasinConstants.*;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.userdirectory.droplet.Constants;
import atg.userdirectory.droplet.Utils;

/**
 * Droplet that generates URL for calculating way to the store.
 * <dt>Input params:
 * <dd>
 * <li><code>STREET0</code> - destination street
 * <dd>
 * <li><code>CITY0</code> - destination city
 * <dd>
 * <li><code>MAGASIN_ID</code> - store
 * <dt>Oparams:
 * <dd>
 * <li><code>output</code> sets in any case
 * <dt>Output params:
 * <dd>
 * <li><code>URL</code> URL for calculating way to the store
 *
 * @see    #STREET0
 * @see    #CITY0
 * @see    #MAGASIN_ID
 * @see    #URL
 * @author Katsiaryna Dmitrievich
 */
public class CastMagasinItineraireDroplet extends DynamoServlet implements Constants {
	/** City parameter. */
	private static final ParameterName CITY0 = ParameterName.getParameterName("city0");
	/** Street parameter. */
	private static final ParameterName STREET0 = ParameterName.getParameterName("street0");
	/** Magasin id parameter. */
	private static final ParameterName MAGASIN_ID = ParameterName.getParameterName("magasinId");

	/**
	 * Sets URL for direction calculation from param.
	 */
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {
		String street = pRequest.getParameter(STREET0);
		String city = pRequest.getParameter(CITY0);
		String magasinId = pRequest.getParameter(MAGASIN_ID);

		if (isMissingParameters(street, city, magasinId)) {
			pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
			return;
		}

		String url = CHECK_WAY_URL + STREET0 + "=" + street + AMPERSAND + CITY0 + "=" + city + AMPERSAND
				+ DATA_ID1_IS_CASTO + magasinId;

		pRequest.setParameter(URL, url);

		pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
	}

	/**
	 * This method will determine if there are any missing parameters passed to this droplet. If there are then an error
	 * is logged and this method will return true. This method will look for the following parameters
	 * 
	 * @param alpha
	 *            a <code>String</code> value
	 * @param city
	 *            a <code>String</code> value
	 * @param magasinId
	 *            a <code>String</code> value
	 * @return a <code>boolean</code> value
	 */
	protected boolean isMissingParameters(String street, String city, String magasinId) {
		if (StringUtils.isBlank(street)) {
			if (isLoggingError()) {
				Object[] args = { STREET0.getName() };
				logError(Utils.fillInArgs(MISSING_REQUIRED_PARAM, args));
			}
			return true;
		}
		if (StringUtils.isBlank(city)) {
			if (isLoggingError()) {
				Object[] args = { CITY0.getName() };
				logError(Utils.fillInArgs(MISSING_REQUIRED_PARAM, args));
			}
			return true;
		}
		if (StringUtils.isBlank(magasinId)) {
			if (isLoggingError()) {
				Object[] args = { MAGASIN_ID.getName() };
				logError(Utils.fillInArgs(MISSING_REQUIRED_PARAM, args));
			}
			return true;
		}

		return false;
	}
}
