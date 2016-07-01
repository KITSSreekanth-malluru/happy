package com.castorama.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;

import atg.nucleus.naming.ParameterName;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Service method returns correct URL for "order cart atout" pop-up content.
 * URLS for prod and dev should be defined at the component's .properties file.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastCarteAtout extends DynamoServlet {
    /** OUTPUT parameter name. */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** EMPTY parameter name. */
    public static final ParameterName EMPTY = ParameterName.getParameterName("empty");

    /** PROD parameter name. */
    public static final String PROD = "prod";

    /** DEV parameter name. */
    public static final String DEV = "dev";

    /** URL constant. */
    public static final String URL = "url";

    /** typeUrl property */
    private String mTypeUrl;

    /** devUrlTranscred property */
    private String mDevUrlTranscred;

    /** prodUrlTranscred property */
    private String mProdUrlTranscred;

    /**
     * Returns typeUrl property.
     *
     * @return typeUrl property.
     */
    public String getTypeUrl() {
        return mTypeUrl;
    }

    /**
     * Sets the value of the typeUrl property.
     *
     * @param pTypeUrl parameter to set.
     */
    public void setTypeUrl(String pTypeUrl) {
        mTypeUrl = pTypeUrl;
    }

    /**
     * Returns devUrlTranscred property.
     *
     * @return devUrlTranscred property.
     */
    public String getDevUrlTranscred() {
        return mDevUrlTranscred;
    }

    /**
     * Sets the value of the devUrlTranscred property.
     *
     * @param pDevUrlTranscred parameter to set.
     */
    public void setDevUrlTranscred(String pDevUrlTranscred) {
        mDevUrlTranscred = pDevUrlTranscred;
    }

    /**
     * Returns prodUrlTranscred property.
     *
     * @return prodUrlTranscred property.
     */
    public String getProdUrlTranscred() {
        return mProdUrlTranscred;
    }

    /**
     * Sets the value of the prodUrlTranscred property.
     *
     * @param pProdUrlTranscred parameter to set.
     */
    public void setProdUrlTranscred(String pProdUrlTranscred) {
        mProdUrlTranscred = pProdUrlTranscred;
    }

    /**
     * Set correct URL for "order cart atout" pop-up content.
     *
     * @param  pRequest  request
     * @param  pResponse response
     *
     * @throws ServletException error
     * @throws IOException      error
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        String typeUrl = getTypeUrl();
        String url = "";
        if (DEV.equals(typeUrl)) {
            url = getDevUrlTranscred();
        } else if (PROD.equals(typeUrl)) {
            url = getProdUrlTranscred();
        }

        if (!StringUtils.isBlank(url)) {
            pRequest.setParameter(URL, url);
            pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
        } else {
            pRequest.serviceParameter(EMPTY, pRequest, pResponse);
        }

    }

}
