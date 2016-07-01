package com.castorama.magasin;

import static com.castorama.magasin.MagasinConstants.*;

import atg.core.util.StringUtils;

import atg.droplet.DropletException;
import atg.droplet.GenericFormHandler;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import java.io.IOException;

import java.net.URLEncoder;

/**
 * Form Handler validates input data on store details page. Generates URLs.
 *
 * @author EPAM team
 */
public class CastMagasinItineraireFormHandler extends GenericFormHandler {
    /** Code for space. */
    private static final String SPACE_CODE = "%20";

    /** URL_ENCODING constant. */
    private static final String URL_ENCODING = "UTF-8";

    /** Street field. */
    private String mRue = "";

    /** City field. */
    private String mVille = "";

    /** Magasin id field. */
    private String mMagasinId = "";

    /** Url field. */
    private String mUrl = "";

    /** Success URL. */
    private String mItineraireSuccessURL;

    /** Error URL. */
    private String mItineraireErrorURL;

    /**
     * Gets url.
     *
     * @return mUrl
     */
    public String getSUrl() {
        return mUrl;
    }

    /**
     * Sets mUrl.
     *
     * @param purl to set mUrl
     */
    public void setSUrl(String purl) {
        mUrl = purl;
    }

    /**
     * Gets street.
     *
     * @return mRue
     */
    public String getRue() {
        return mRue;
    }

    /**
     * Sets street
     *
     * @param prue to set mRue
     */
    public void setRue(String prue) {
        mRue = prue;
    }

    /**
     * Gets city
     *
     * @return mVille
     */
    public String getVille() {
        return mVille;
    }

    /**
     * Sets city.
     *
     * @param pville
     */
    public void setVille(String pville) {
        mVille = pville;
    }

    /**
     * gets magasin id
     *
     * @return mMagasinId
     */
    public String getMagasinId() {
        return mMagasinId;
    }

    /**
     * Sets magasin id
     *
     * @param pmagasinId to set mMagasinId
     */
    public void setMagasinId(String pmagasinId) {
        mMagasinId = pmagasinId;
    }

    /**
     * Gets Success URL
     *
     * @return mItineraireSuccessURL
     */
    public String getItineraireSuccessURL() {
        return mItineraireSuccessURL;
    }

    /**
     * Sets Success URL
     *
     * @param pItineraireSuccessURL to set mItineraireSuccessURL
     */
    public void setItineraireSuccessURL(String pItineraireSuccessURL) {
        mItineraireSuccessURL = pItineraireSuccessURL;
    }

    /**
     * Gets error url.
     *
     * @return mItineraireErrorURL
     */
    public String getItineraireErrorURL() {
        return mItineraireErrorURL;
    }

    /**
     * Sets error url
     *
     * @param pItineraireErrorURL to set mItineraireErrorURL
     */
    public void setItineraireErrorURL(String pItineraireErrorURL) {
        mItineraireErrorURL = pItineraireErrorURL;
    }

    /**
     * Generates URL to external resource to gets way to magasin.
     *
     * @param  pRequest
     * @param  pResponse
     *
     * @return
     *
     * @throws IOException
     */
    public boolean handleItineraire(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                             throws IOException {
        boolean result = false;

        pRequest.setParameter(MAGASIN_ID, pRequest.getParameter(MAGASIN_ID));
        String street = mRue.trim();
        String city = mVille.trim();
        if (StringUtils.isBlank(mVille)) {
            addFormException(new DropletException("<b>Veuillez saisir votre ville de dйpart ou son code postal.</b>"));
        } else {
            if (StringUtils.isBlank(street) || ((street != null) && street.equalsIgnoreCase(RUE))) {
                street = CENTRE_VILLE;
            }

            while (street.indexOf(GAP) != -1) {
                String beginStreet = street.substring(0, street.indexOf(GAP));
                String endStreet = street.substring(street.indexOf(GAP) + 1);
                street = beginStreet + SPACE_CODE + endStreet;
            }
            street = URLEncoder.encode(street, URL_ENCODING);

            while (city.indexOf(GAP) != -1) {
                String beginCity = city.substring(0, city.indexOf(GAP));
                String endCity = city.substring(city.indexOf(GAP) + 1);
                city = beginCity + SPACE_CODE + endCity;
            }
            city = URLEncoder.encode(city, URL_ENCODING);

            mUrl =
                CHECK_WAY_URL + STREET0 + "=" + street + AMPERSAND + CITY0 + "=" + city + AMPERSAND +
                DATA_ID1_IS_CASTO + mMagasinId;

            pRequest.getSession().setAttribute(URL_IFRAME_SESSION, mUrl);

            pRequest.setParameter(FORM_OK, TRUE);
        }  // end if-else

        if (getFormError()) {
            if (getItineraireErrorURL() != null) {
                if (getItineraireErrorURL().indexOf('?') > -1) {
                    pResponse.sendLocalRedirect(getItineraireErrorURL() + AMPERSAND + MAGASIN_ID + "=" + mMagasinId,
                                                pRequest);
                } else {
                    pResponse.sendLocalRedirect(getItineraireErrorURL() + "?" + MAGASIN_ID + "=" + mMagasinId,
                                                pRequest);
                }
                return false;
            }
            result = true;
        }

        if (getItineraireSuccessURL() != null) {
            if (!StringUtils.isBlank(street)) {
                pResponse.sendLocalRedirect(getItineraireSuccessURL() + AMPERSAND + MAGASIN_ID + "=" + mMagasinId +
                                            AMPERSAND + STREET0 + "=" + street + AMPERSAND + CITY0 + "=" + city +
                                            AMPERSAND + DATA_ID1_IS_CASTO + mMagasinId + AMPERSAND + FORM_OK + "=" +
                                            TRUE, pRequest);
            } else {
                pResponse.sendLocalRedirect(getItineraireSuccessURL() + AMPERSAND + MAGASIN_ID + "=" + mMagasinId +
                                            AMPERSAND + STREET0 + "=" + AMPERSAND + CITY0 + "=" + city + AMPERSAND +
                                            DATA_ID1_IS_CASTO + mMagasinId + AMPERSAND + FORM_OK + "=" + TRUE,
                                            pRequest);
            }
            result = false;
        }

        result = true;

        return result;
    }
}
