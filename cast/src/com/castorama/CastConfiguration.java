package com.castorama;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import atg.nucleus.GenericService;

/**
 * Castorama utility class for sofinco and kitchen planner on PDP.
 *
 * @author Alena_Karpenkava
 */
public class CastConfiguration extends GenericService {
    /** httpProxyHost property. */
    private String mHttpProxyHost;

    /** httpProxyPort property. */
    private int mHttpProxyPort;

    /** httpProxyUser property. */
    private String mHttpProxyUser;

    /** httpProxyPassword property. */
    private String mHttpProxyPassword;
    
    private boolean mHttpProxyEnabled;

    /** httpsProxyHost property. */
    private String mHttpsProxyHost;

    /** httpsProxyPort property. */
    private int mHttpsProxyPort;
    
    /** gerer ma carte castorama */
    private String newLinkForGererMaCarte;
    
    private String newTextForLinkGererMaCarte;

    /** urlLegalNotice property. */
    private String mUrlLegalNotice;

    /** airEmissionUrl property. */
    private String mAirEmissionUrl;

    /** urlTranscred property. */
    private String mUrlTranscred;
    
    /** urlKitchenPlanner property. */
    private String mUrlKitchenPlanner;
    
    /** isEnabled property. */
    private String mIsKitchenPlannerEnabled;
    
    /** carouselProductsCount property. */
    private int mCarouselProductsCount;
    
    /** epticaUrl property. */
    private String mEpticaUrl;
    
    /** epticaUrl property. */
    private String mEpticaUrlContactService;

    /** epticaIFrameHeight property. */
    private int mEpticaIFrameHeight = 573;
    
    /** maxSkuCountForPricing property. */
    private int mMaxSkuCountForPricing = 20;
    
    /** displayAdviceBanners property. */
    private boolean mDisplayAdviceBanners = true;
    
    /** magasinsUrl property. */
    private String mMagasinsUrl;
    
    /** sofincoUrl property. */
    private String mSofincoUrl;
    
    /** staticContentPath property. */
    private String mStaticContentPath;
    
    /** deliveryPageMessage property. */
    private String mDeliveryPageMessage;
    
    /** oneTargeterTemplate property. */
    private boolean oneTargeterTemplate = false;
    
	 /** openInformation property. */
    private int openInformation;
    
    /** showServiceClient2012Flap property. */
    private boolean showServiceClient2012Flap = true;
    
    /** googleGeolocationAPIUrl property. */
    private String googleGeolocationAPIUrl;
    
    /** googleGeolocationAPIKey property. */
    private String googleGeolocationAPIKey;
    
    /** googleGeolocationCountry property. */
    private String googleGeolocationCountry;
    
    /** googleReverseGeocodingAPIUrl property. */
    private String googleReverseGeocodingAPIUrl;
    
    /** googleReverseGeocodingClientId property. */
    private String googleReverseGeocodingClientId;
    
    /** googleReverseGeocodingSignature property. */
    private String googleReverseGeocodingSignature;

    /** cacheCheckSeconds property. */
    private int cacheCheckSeconds;

    /** cacheCheckSecondsLong property. */
    private int cacheCheckSecondsLong;
    
    /** omnitureSynchDelay property. */
    private int omnitureSynchDelay;
    
    /** xslTemplatesUrl property. */
    private String mXslTemplatesUrl;
    
    /** activeCardCastorama property. */
    private boolean activeCardCastorama;
    
    /** bccListForConfirmationEmails property. */
    private ArrayList<String> bccListForConfirmationEmails;
    
    /** invoiceUrlTemplatePath property, should contain 2 parameters */
    private String invoiceUrlTemplatePath;
    
    /** path to CGV page property */
    private String cgvPagePath;
    
    /** retractionFromTemplatePath property, should contain 2 parameters */
    private String retractionFormTemplatePath;
    
    /** prefix for name of invoice PDF file */
    private String invoicePDFPrefix;
    
    /** postfix for name of invoice PDF file */
    private String invoicePDFPostfix;
    
    /** prefix for name of PDF file for retraction form */
    private String retractionFormPrefix;
    
    /** postfix for name of PDF file for retraction form */
    private String retractionFormPostfix;
    
    /** prefix for name of CGV pdf file */
    private String cgvFilePrefix;
    
    /** postfix for name of CGV pdf file */
    private String cgvFilePostfix;
    
    /** baseURL property */
    private String baseURL;
    
    /** tempFilesDestFolder property */
    private String tempFilesDestFolder;
    
    /** retractionFormElementsUrl property. */
    private String retractionFormElementsUrl;
    
    /** confirmEmailsDestFolder property. */
    private String confirmEmailsDestFolder;
    /** property for activating Omniture and GA. */
    private boolean activateOmniture;    /** cgvTemplateDestFolder property. */
    private String cgvTemplateDestFolder;

    /** googleMapEnabled property */
    private boolean googleMapEnabled;
		
    /**urlUserProjects property**/
    private String urlUserProjects;

    /**userProjectsLinkName property**/
    private String userProjectsLinkName;
    
    public String getCgvTemplateDestFolder() {
		return cgvTemplateDestFolder;
	}

	public void setCgvTemplateDestFolder(String cgvTemplateDestFolder) {
		this.cgvTemplateDestFolder = cgvTemplateDestFolder;
	}
       
    public boolean getActivateOmniture() {
		return activateOmniture;
	}

    public void setActivateOmniture(boolean activateOmniture) {
		this.activateOmniture = activateOmniture;
	}
    public String getConfirmEmailsDestFolder() {
		return confirmEmailsDestFolder;
	}

	public void setConfirmEmailsDestFolder(String confirmEmailsDestFolder) {
		this.confirmEmailsDestFolder = confirmEmailsDestFolder;
	}

    /** googleApiEnabled */
    private boolean googleApiEnabled;
     
    /** popupDisplayingDurationSeconds property */
    private int popupDisplayingDurationSeconds;
    
    public String getTempFilesDestFolder() {
		return tempFilesDestFolder;
	}

	public void setTempFilesDestFolder(String tempFilesDestFolder) {
		this.tempFilesDestFolder = tempFilesDestFolder;
	}

	public String getBaseURL() {
		return baseURL;
	}

	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}

	public ArrayList<String> getBccListForConfirmationEmails() {
		return bccListForConfirmationEmails;
	}

	public void setBccListForConfirmationEmails(
			ArrayList<String> bccListForConfirmationEmails) {
		this.bccListForConfirmationEmails = bccListForConfirmationEmails;
	}

	public boolean isActiveCardCastorama() {
        return activeCardCastorama;
    }
    
    public void setActiveCardCastorama(boolean activeCardCastorama) {
        this.activeCardCastorama = activeCardCastorama;
    }
        
    public boolean isShowServiceClient2012Flap() {
        return showServiceClient2012Flap;
    }

    public void setShowServiceClient2012Flap(boolean showServiceClient2012Flap) {
        this.showServiceClient2012Flap = showServiceClient2012Flap;
    }

    public int getOpenInformation() {
		return openInformation;
	}
    
    public void setOpenInformation(int openInformation) {
		this.openInformation = openInformation;
	}
    
    public boolean isOneTargeterTemplate() {
        return oneTargeterTemplate;
    }

    public void setOneTargeterTemplate(boolean oneTargeterTemplate) {
        this.oneTargeterTemplate = oneTargeterTemplate;
    }

    /**
     * Returns httpProxyHost property.
     *
     * @return httpProxyHost property.
     */
    public String getHttpProxyHost() {
        return mHttpProxyHost;
    }

    /**
     * Sets the value of the httpProxyHost property.
     *
     * @param pHttpProxyHost parameter to set.
     */
    public void setHttpProxyHost(String pHttpProxyHost) {
        mHttpProxyHost = pHttpProxyHost;
    }

    /**
     * Returns httpProxyPort property.
     *
     * @return httpProxyPort property.
     */
    public int getHttpProxyPort() {
        return mHttpProxyPort;
    }

    /**
     * Sets the value of the httpProxyPort property.
     *
     * @param pHttpProxyPort parameter to set.
     */
    public void setHttpProxyPort(int pHttpProxyPort) {
        mHttpProxyPort = pHttpProxyPort;
    }

    public boolean getHttpProxyEnabled() {
		return mHttpProxyEnabled;
	}

	public void setHttpProxyEnabled(boolean isProxyEnabled) {
		this.mHttpProxyEnabled = isProxyEnabled;
	}

	/**
     * Returns httpsProxyHost property.
     *
     * @return httpsProxyHost property.
     */
    public String getHttpsProxyHost() {
        return mHttpsProxyHost;
    }

    public String getHttpProxyUser() {
		return mHttpProxyUser;
	}

	public void setHttpProxyUser(String httpProxyUser) {
		mHttpProxyUser = httpProxyUser;
	}

	public String getHttpProxyPassword() {
		return mHttpProxyPassword;
	}

	public void setHttpProxyPassword(String httpProxyPassword) {
		mHttpProxyPassword = httpProxyPassword;
	}

	/**
     * Sets the value of the httpsProxyHost property.
     *
     * @param pHttpsProxyHost parameter to set.
     */
    public void setHttpsProxyHost(String pHttpsProxyHost) {
        mHttpsProxyHost = pHttpsProxyHost;
    }

    /**
     * Returns httpsProxyPort property.
     *
     * @return httpsProxyPort property.
     */
    public int getHttpsProxyPort() {
        return mHttpsProxyPort;
    }

    /**
     * Sets the value of the httpsProxyPort property.
     *
     * @param pHttpsProxyPort parameter to set.
     */
    public void setHttpsProxyPort(int pHttpsProxyPort) {
        mHttpsProxyPort = pHttpsProxyPort;
    }

    /**
     * Returns urlLegalNotice property.
     *
     * @return urlLegalNotice property.
     */
    public String getUrlLegalNotice() {
        return mUrlLegalNotice;
    }

    /**
     * Sets the value of the urlLegalNotice property.
     *
     * @param pUrlLegalNotice parameter to set.
     */
    public void setUrlLegalNotice(String pUrlLegalNotice) {
        mUrlLegalNotice = pUrlLegalNotice;
    }

    /**
     * Returns airEmissionUrl property.
     *
     * @return airEmissionUrl property.
     */
    public String getAirEmissionUrl() {
        return mAirEmissionUrl;
    }
    
    /**
     * Sets the value of the airEmissionUrl property.
     *
     * @param pAirEmissionUrl parameter to set.
     */
    public void setAirEmissionUrl(String pAirEmissionUrl) {
        mAirEmissionUrl = pAirEmissionUrl;
    }
    
    /**
     * Returns urlTranscred property.
     *
     * @return urlTranscred property.
     */
    public String getUrlTranscred() {
        return mUrlTranscred;
    }

    /**
     * Sets the value of the urlTranscred property.
     *
     * @param pUrlTranscred parameter to set.
     */
    public void setUrlTranscred(String pUrlTranscred) {
        mUrlTranscred = pUrlTranscred;
    }

    /**
     * Returns urlKitchenPlanner property.
     *
     * @return urlKitchenPlanner property.
     */
	public String getUrlKitchenPlanner() {
		return mUrlKitchenPlanner;
	}

	/**
     * Sets the value of the urlKitchenPlanner property.
     *
     * @param pUrlKitchenPlanner parameter to set.
     */
	public void setUrlKitchenPlanner(String pUrlKitchenPlanner) {
		mUrlKitchenPlanner = pUrlKitchenPlanner;
	}

	/**
	 * Returns isKitchenPlannerEnabled property.
	 * 
	 * @return the isKitchenPlannerEnabled
	 */
	public String getIsKitchenPlannerEnabled() {
		return mIsKitchenPlannerEnabled;
	}

	/**
	 * Sets the value of the isKitchenPlannerEnabled property.
	 * 
	 * @param pIsKitchenPlannerEnabled the isKitchenPlannerEnabled to set
	 */
	public void setIsKitchenPlannerEnabled(String pIsKitchenPlannerEnabled) {
		mIsKitchenPlannerEnabled = pIsKitchenPlannerEnabled;
	}

	public int getCarouselProductsCount() {
		return mCarouselProductsCount;
	}

	public void setCarouselProductsCount(int carouselProductsCount) {
		mCarouselProductsCount = carouselProductsCount;
	}

	public String getEpticaUrl() {
		return mEpticaUrl;
	}

	public void setEpticaUrl(String epticaUrl) {
		mEpticaUrl = epticaUrl;
	}

	public String getEpticaUrlContactService() {
        return mEpticaUrlContactService;
    }

    public void setEpticaUrlContactService(String epticaUrlContactService) {
        mEpticaUrlContactService = epticaUrlContactService;
    }

    public int getEpticaIFrameHeight() {
		return mEpticaIFrameHeight;
	}

	public void setEpticaIFrameHeight(int epticaIFrameHeight) {
		mEpticaIFrameHeight = epticaIFrameHeight;
	}

	public boolean getDisplayAdviceBanners() {
		return mDisplayAdviceBanners;
	}

	public void setDisplayAdviceBanners(boolean displayAdviceBanners) {
		mDisplayAdviceBanners = displayAdviceBanners;
	}

	public int getMaxSkuCountForPricing() {
		return mMaxSkuCountForPricing;
	}

	public void setMaxSkuCountForPricing(int maxSkuCountForPricing) {
		mMaxSkuCountForPricing = maxSkuCountForPricing;
	}
	
	public String getMagasinsUrl() {
        return mMagasinsUrl;
	}
	
	public void setMagasinsUrl(String magasinsUrl) {
        this.mMagasinsUrl = magasinsUrl;
    }
	
	public String getSofincoUrl() {
        return mSofincoUrl;
    }
	
	public void setSofincoUrl(String sofincoUrl) {
        this.mSofincoUrl = sofincoUrl;
    }
    
    public String getStaticContentPath() {
        return mStaticContentPath;
    }
    
    public void setStaticContentPath(String staticContentPath) {
        this.mStaticContentPath = staticContentPath;
    }
    
    public String getDeliveryPageMessage() {
        return mDeliveryPageMessage;
    }
    
    public void setDeliveryPageMessage(String deliveryPageMessage) {
        this.mDeliveryPageMessage = deliveryPageMessage;
    }
    
    public void setNewLinkForGererMaCarte(String newLinkForGererMaCarte) {
		this.newLinkForGererMaCarte = newLinkForGererMaCarte;
	}
    
    public String getNewLinkForGererMaCarte() {
		return newLinkForGererMaCarte;
	}
    
    public void setNewTextForLinkGererMaCarte(String newTextForLinkGererMaCarte) {
		this.newTextForLinkGererMaCarte = newTextForLinkGererMaCarte;
	}
    
    public String getNewTextForLinkGererMaCarte() {
		return newTextForLinkGererMaCarte;
	}

    /**
     * @return the googleGeolocationAPIUrl
     */
    public String getGoogleGeolocationAPIUrl() {
        return googleGeolocationAPIUrl;
    }

    /**
     * @param googleGeolocationAPIUrl the googleGeolocationAPIUrl to set
     */
    public void setGoogleGeolocationAPIUrl(String googleGeolocationAPIUrl) {
        this.googleGeolocationAPIUrl = googleGeolocationAPIUrl;
    }

    /**
     * Returns googleGeolocationAPIKey property.
     *
     * @return the googleGeolocationAPIKey
     */
    public String getGoogleGeolocationAPIKey() {
        return googleGeolocationAPIKey;
    }

    /**
     * Sets the value of the googleGeolocationAPIKey property.
     * 
     * @param googleGeolocationAPIKey the googleGeolocationAPIKey to set
     */
    public void setGoogleGeolocationAPIKey(String googleGeolocationAPIKey) {
        this.googleGeolocationAPIKey = googleGeolocationAPIKey;
    }

    /**
     * Returns googleGeolocationCountry property.
     *
     * @return the googleGeolocationCountry
     */
    public String getGoogleGeolocationCountry() {
        return googleGeolocationCountry;
    }

    /**
     * Sets the value of the googleGeolocationCountry property.
     * 
     * @param googleGeolocationCountry the googleGeolocationCountry to set
     */
    public void setGoogleGeolocationCountry(String googleGeolocationCountry) {
        this.googleGeolocationCountry = googleGeolocationCountry;
    }

    /**
     * @return the googleReverseGeocodingAPIUrl
     */
    public String getGoogleReverseGeocodingAPIUrl() {
        return googleReverseGeocodingAPIUrl;
    }

    /**
     * @param googleReverseGeocodingAPIUrl the googleReverseGeocodingAPIUrl to set
     */
    public void setGoogleReverseGeocodingAPIUrl(String googleReverseGeocodingAPIUrl) {
        this.googleReverseGeocodingAPIUrl = googleReverseGeocodingAPIUrl;
    }

    /**
     * @return the googleReverseGeocodingClientId
     */
    public String getGoogleReverseGeocodingClientId() {
        return googleReverseGeocodingClientId;
    }

    /**
     * @param googleReverseGeocodingClientId the googleReverseGeocodingClientId to set
     */
    public void setGoogleReverseGeocodingClientId(String googleReverseGeocodingClientId) {
        this.googleReverseGeocodingClientId = googleReverseGeocodingClientId;
    }

    /**
     * @return the googleReverseGeocodingSignature
     */
    public String getGoogleReverseGeocodingSignature() {
        return googleReverseGeocodingSignature;
    }

    /**
     * @param googleReverseGeocodingSignature the googleReverseGeocodingSignature to set
     */
    public void setGoogleReverseGeocodingSignature(String googleReverseGeocodingSignature) {
        this.googleReverseGeocodingSignature = googleReverseGeocodingSignature;
    }

    /**
     * @return the cacheCheckSeconds
     */
    public int getCacheCheckSeconds() {
        return cacheCheckSeconds;
    }

    /**
     * @param cacheCheckSeconds the cacheCheckSeconds to set
     */
    public void setCacheCheckSeconds(int cacheCheckSeconds) {
        this.cacheCheckSeconds = cacheCheckSeconds;
    }

    /**
     * @return the cacheCheckSecondsLong
     */
    public int getCacheCheckSecondsLong() {
        return cacheCheckSecondsLong;
    }

    /**
     * @param cacheCheckSecondsLong the cacheCheckSecondsLong to set
     */
    public void setCacheCheckSecondsLong(int cacheCheckSecondsLong) {
        this.cacheCheckSecondsLong = cacheCheckSecondsLong;
    }

    /**
     * @return the omnitureSynchDelay
     */
    public int getOmnitureSynchDelay() {
        return omnitureSynchDelay;
    }

    /**
     * @param omnitureSynchDelay the omnitureSynchDelay to set
     */
    public void setOmnitureSynchDelay(int omnitureSynchDelay) {
        this.omnitureSynchDelay = omnitureSynchDelay;
    }
    
    /**
     * @return the XslTemplatesUrl
     */
    public String getXslTemplatesUrl() {
        return mXslTemplatesUrl;
    }

    /**
     * @param XslTemplatesUrl the XslTemplatesUrl to set
     */
    public void setXslTemplatesUrl(String XslTemplatesUrl) {
        this.mXslTemplatesUrl = XslTemplatesUrl;
    }

	public String getInvoiceUrlTemplatePath() {
		return invoiceUrlTemplatePath;
	}

	public void setInvoiceUrlTemplatePath(String invoiceUrlTemplatePath) {
		this.invoiceUrlTemplatePath = invoiceUrlTemplatePath;
	}

	public String getCgvPagePath() {
		return cgvPagePath;
	}

	public void setCgvPagePath(String cgvPagePath) {
		this.cgvPagePath = cgvPagePath;
	}

	public String getRetractionFormTemplatePath() {
		return retractionFormTemplatePath;
	}

	public void setRetractionFormTemplatePath(String retractionFormTemplatePath) {
		this.retractionFormTemplatePath = retractionFormTemplatePath;
	}

	public String getInvoicePDFPrefix() {
		return invoicePDFPrefix;
	}

	public void setInvoicePDFPrefix(String invoicePDFPrefix) {
		this.invoicePDFPrefix = invoicePDFPrefix;
	}

	public String getInvoicePDFPostfix() {
		return invoicePDFPostfix;
	}

	public void setInvoicePDFPostfix(String invoicePDFPostFix) {
		this.invoicePDFPostfix = invoicePDFPostFix;
	}

	public String getRetractionFormPrefix() {
		return retractionFormPrefix;
	}

	public void setRetractionFormPrefix(String retractionFormPrefix) {
		this.retractionFormPrefix = retractionFormPrefix;
	}

	public String getRetractionFormPostfix() {
		return retractionFormPostfix;
	}

	public void setRetractionFormPostfix(String retractionFormPostfix) {
		this.retractionFormPostfix = retractionFormPostfix;
	}

	public String getCgvFilePrefix() {
		return cgvFilePrefix;
	}

	public void setCgvFilePrefix(String cgvFilePrefix) {
		this.cgvFilePrefix = cgvFilePrefix;
	}

	public String getCgvFilePostfix() {
		return cgvFilePostfix;
	}

	public void setCgvFilePostfix(String cgvFilepostFix) {
		this.cgvFilePostfix = cgvFilepostFix;
	}

	public String getRetractionFormElementsUrl() {
		return retractionFormElementsUrl;
	}

	public void setRetractionFormElementsUrl(String retractionFormElementsUrl) {
		this.retractionFormElementsUrl = retractionFormElementsUrl;
	}

	public boolean isGoogleMapEnabled() {
		return googleMapEnabled;
	}

	public void setGoogleMapEnabled(boolean googleMapEnabled) {
		this.googleMapEnabled = googleMapEnabled;
	}

	public int getPopupDisplayingDurationSeconds() {
		return popupDisplayingDurationSeconds;
	}

	public void setPopupDisplayingDurationSeconds(int popupDisplayingDurationSeconds) {
		this.popupDisplayingDurationSeconds = popupDisplayingDurationSeconds;
	}

	public boolean isGoogleApiEnabled() {
		return googleApiEnabled;
	}

	public void setGoogleApiEnabled(boolean googleApiEnabled) {
		this.googleApiEnabled = googleApiEnabled;
	}

    public String getCurrentDate() {
        Date currentDate = new Date();
        String currentStringDate = new SimpleDateFormat("ddMMyyyy").format(currentDate); 
        return currentStringDate;
    } 
	
	public String getUrlUserProjects() {
        return urlUserProjects;
    }

    public void setUrlUserProjects(String urlUserProjects) {
        this.urlUserProjects = urlUserProjects;
    }

    public String getUserProjectsLinkName() {
        return userProjectsLinkName;
    }

    public void setUserProjectsLinkName(String userProjectsLinkName) {
        this.userProjectsLinkName = userProjectsLinkName;
    }
	
}
