package com.castorama.contact;

import static com.castorama.commerce.profile.Constants.ADDRESS_1_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.ADDRESS_2_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.ADDRESS_3_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.ADRESSE_STATISTICS_PROP;
import static com.castorama.commerce.profile.Constants.CITY_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.CIVILITE_STATISTICS_PROP;
import static com.castorama.commerce.profile.Constants.CODE_POSTAL_STATISTICS_PROP;
import static com.castorama.commerce.profile.Constants.CONNEXION_STATISTICS_PROP;
import static com.castorama.commerce.profile.Constants.COUNTRY_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.DATE_ENREGISTREMENT_STATISTICS_PROP;
import static com.castorama.commerce.profile.Constants.EMAIL_STATISTICS_PROP;
import static com.castorama.commerce.profile.Constants.FORM_TYPE_MOTIF_PROP;
import static com.castorama.commerce.profile.Constants.LOCALITY_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.MAGASIN_STATISTICS_PROP;
import static com.castorama.commerce.profile.Constants.MESSAGE_STATISTICS_PROP;
import static com.castorama.commerce.profile.Constants.MOTIF_DESCRIPTOR_NAME;
import static com.castorama.commerce.profile.Constants.MOTIF_STATISTICS_PROP;
import static com.castorama.commerce.profile.Constants.NAVIGATEUR_STATISTICS_PROP;
import static com.castorama.commerce.profile.Constants.NOM_STATISTICS_PROP;
import static com.castorama.commerce.profile.Constants.POSTAL_CODE_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.PRENOM_STATISTICS_PROP;
import static com.castorama.commerce.profile.Constants.PROFILE_ID_STATISTICS_PROP;
import static com.castorama.commerce.profile.Constants.QUESTION_STATISTICS_PROP;
import static com.castorama.commerce.profile.Constants.REFERENCE_PRODUIT_STATISTICS_PROP;
import static com.castorama.commerce.profile.Constants.STATISTICS_REPOSITORY_DESCRIPTOR_NAME;
import static com.castorama.commerce.profile.Constants.SYSTEME_EXPLOITATION_STATISTICS_PROP;
import static com.castorama.commerce.profile.Constants.TELEPHONE_STATISTICS_PROP;
import static com.castorama.commerce.profile.Constants.TYPE_STATISTICS_PROP;
import static com.castorama.commerce.profile.Constants.VILLE_STATISTICS_PROP;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_EMPTY_BROWSER;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_EMPTY_CONNECTION;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_EMPTY_EMAIL;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_EMPTY_MOTIF;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_EMPTY_NOM;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_EMPTY_OS;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_EMPTY_PRENOM;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_EMPTY_QUESTION;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_EMPTY_REMARQUE;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_INVALID_EMAIL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;

import atg.droplet.DropletFormException;

import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

import atg.repository.rql.RqlStatement;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import atg.userprofiling.email.TemplateEmailException;

import com.castorama.constantes.CastoConstantes;

import com.castorama.utils.Validator;

/**
 * ContactUsFormHandler class contains logic for handling contactUs.jsp page.
 *
 * @author EPAM team
 */
public class ContactUsFormHandler extends BaseContactUsFormHandler {
    /** PRENOM_EMAIL_PARAM constant */
    private static final String PRENOM_EMAIL_PARAM = "prenom";

    /** NOM_EMAIL_PARAM constant */
    private static final String NOM_EMAIL_PARAM = "nom";

    /** CIVILITE_EMAIL_PARAM constant */
    private static final String CIVILITE_EMAIL_PARAM = "civilite";

    /** MOTIF_EMAIL_PARAM constant */
    private static final String MOTIF_EMAIL_PARAM = "motif";

    /** STATICTICS_TYPE_VALUE_SITE constant */
    private static final String STATICTICS_TYPE_VALUE_SITE = "site";

    /** CONNECTION_TYPE_EMAIL_PARAM constant */
    private static final String CONNECTION_TYPE_EMAIL_PARAM = "connectionType";

    /** BROWSER_EMAIL_PARAM constant */
    private static final String BROWSER_EMAIL_PARAM = "browser";

    /** OPERATING_SYSTEM_EMAIL_PARAM constant */
    private static final String OPERATING_SYSTEM_EMAIL_PARAM = "operatingSystem";

    /** MESSAGE_EMAIL_PARAM constant */
    private static final String MESSAGE_EMAIL_PARAM = "message";

    /** QUESTION_EMAIL_PARAM constant */
    private static final String QUESTION_EMAIL_PARAM = "question";

    /** MAGASIN_EMAIL_PARAM constant */
    private static final String MAGASIN_EMAIL_PARAM = "magasin";

    /** PRODUCT_EMAIL_PARAM constant */
    private static final String PRODUCT_EMAIL_PARAM = "product";

    /** EMAIL_EMAIL_PARAM constant */
    private static final String EMAIL_EMAIL_PARAM = "email";

    /** NAME_EMAIL_PARAM constant */
    private static final String NAME_EMAIL_PARAM = "name";

    /** MOTIF_NAME_VALUE_WEB_MASTER constant */
    private static final String MOTIF_NAME_WEBMASTER = "WEB_MASTER";

    /** MOTIF_NAME_VALUE_PRODUCT constant */
    private static final String MOTIF_NAME_PRODUCT = "PRODUCT";

    /** MOTIF_NAME_VALUE_PRODUCT constant */
    private static final String MOTIF_NAME_IPHONE = "IPHONE";

    /** OS_PROPERTY constant */
    private static final String OS_PROPERTY = "operation.system";

    /** BROWSER_PROPERTY constant */
    private static final String BROWSER_PROPERTY = "browser.version";

    /** CONNECTION_PROPERTY constant */
    private static final String CONNECTION_PROPERTY = "connection.type";

    /** MOTIF_PARAM constant */
    private static final String MOTIF_REQUEST_PARAM = "motif";

    /** REQUEST_PARAM_PRODUCT constant */
    private static final String REQUEST_PARAM_PRODUCT = "product";

    /** REQUEST_PARAM_IPHONE constant */
    private static final String REQUEST_PARAM_IPHONE = "iphone";

    /** motifObjects property */
    private List<RepositoryItem> motifObjects = new ArrayList<RepositoryItem>();

    /** motifName property */
    private String mMotifName;

    /** motifValue property */
    private String mMotifValue;

    /** product property */
    private String mProduct;

    /** magasin property */
    private String mMagasin;

    /** question property */
    private String mQuestion;

    /** message property */
    private String mMessage;

    /** operatingSystem property */
    private String mOperatingSystem;

    /** browser property */
    private String mBrowser;

    /** connectionType property */
    private String mConnectionType;

    /** formInitialized property */
    private boolean mFormInitialized;

    /** formSubmitted property */
    private boolean mFormSubmitted;

    /** formRedirects property */
    private boolean mFormRedirects;

    /** webMasterForm property */
    private boolean mWebMasterForm;

    /** iphoneForm property */
    private boolean mIphoneForm;

    /** motifRepository property */
    private Repository mMotifRepository = null;

    /** statisticsRepository property */
    private Repository mStatisticsRepository = null;

    /** productTemplateEmailInfo property */
    private CastTemplateEmailInfoImpl mProductTemplateEmailInfo;

    /** webmasterTemplateEmailInfo property */
    private CastTemplateEmailInfoImpl mWebmasterTemplateEmailInfo;

    /** mIphoneTemplateEmailInfo property */
    private CastTemplateEmailInfoImpl mIphoneTemplateEmailInfo;

    /**
     * Creates a new ContactUsFormHandler object.
     */
    public ContactUsFormHandler() {
    }

    /**
     * This method is called when the page is loaded. It initializes the fields.
     *
     * @param  pRequest  DynamoHttpServletRequest object
     * @param  pResponse DynamoHttpServletResponse object
     *
     * @return <code>true</code> if initial successful.
     *
     * @throws Exception exception
     */
    public boolean handleInitForm(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                           throws Exception {
        String motifName;

        if (!isFormInitialized()) {
            String motifParam = (String) pRequest.getObjectParameter(MOTIF_REQUEST_PARAM);
            if (REQUEST_PARAM_PRODUCT.equals(motifParam)) {
                motifName = MOTIF_NAME_PRODUCT;
                setWebMasterForm(false);
            } else if (REQUEST_PARAM_IPHONE.equals(motifParam)) {
                motifName = MOTIF_NAME_IPHONE;
                setWebMasterForm(false);
                setIphoneForm(true);
            } else {
                motifName = MOTIF_NAME_WEBMASTER;
                setWebMasterForm(true);
            }
        } else {
            if (isIphoneForm()) {
                motifName = MOTIF_NAME_IPHONE;
            } else if (isWebMasterForm()) {
                motifName = MOTIF_NAME_WEBMASTER;
            } else {
                motifName = MOTIF_NAME_PRODUCT;
            }
        }  // end if-else

        RepositoryView view = getMotifRepository().getView(MOTIF_DESCRIPTOR_NAME);
        RqlStatement statement = RqlStatement.parseRqlStatement(" formType = ?0 ");
        RepositoryItem[] items = statement.executeQuery(view, new Object[] {motifName});

        setMotifObjects(Arrays.asList(items));
        setFormInitialized(true);

        return true;
    }

    /**
     * Handler method which validates input fields and sends e-mail if no errors
     * were found.
     *
     * @param  pRequest  DynamoHttpServletRequest object
     * @param  pResponse DynamoHttpServletResponse object
     *
     * @return If redirect to a new page occurred, return false. If NO redirect
     *         occurred, return true.
     *
     * @throws Exception exception
     */
    public boolean handleSend(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws Exception {
        validateUserData(pRequest, pResponse);
        if (!checkFormRedirect(null, getErrorUrl(), pRequest, pResponse)) {
            setFormRedirects(true);
            return false;
        }
        sendEmails();
        saveStatistics();

        setFormSubmitted(true);
        setFormRedirects(true);
        clearForm();
        return checkFormRedirect(getSuccessUrl(), getErrorUrl(), pRequest, pResponse);
    }

    /**
     * Populates the e-mail template and sends e-mail.
     *
     * @throws TemplateEmailException exception
     * @throws RepositoryException    exception
     */
    private void sendEmails() throws TemplateEmailException, RepositoryException {
        Map<String, String> params = new HashMap<String, String>();

        String name = getCivilite() + " " + getNom() + " " + getPrenom();
        String messageFrom = getNom() + " " + getPrenom() + "<" + getEmail() + ">";
        params.put(NAME_EMAIL_PARAM, name);
        params.put(CIVILITE_EMAIL_PARAM, getCivilite());
        params.put(NOM_EMAIL_PARAM, getNom());
        params.put(PRENOM_EMAIL_PARAM, getPrenom());
        params.put(EMAIL_EMAIL_PARAM, getEmail());
        params.put(ADDRESS_1_ADDRESS_PROP, getAddress1());
        params.put(ADDRESS_2_ADDRESS_PROP, getAddress2());
        params.put(ADDRESS_3_ADDRESS_PROP, getAddress3());
        params.put(LOCALITY_ADDRESS_PROP, getLocality());
        params.put(POSTAL_CODE_ADDRESS_PROP, getPostalCode());
        params.put(CITY_ADDRESS_PROP, getCity());
        params.put(COUNTRY_ADDRESS_PROP, getCountry());
        params.put(CastoConstantes.TELEPHONE, getPhone());

        List<String> recipents = new ArrayList<String>();
        CastTemplateEmailInfoImpl templateEmailInfo = null;

        if (isProductForm()) {
            templateEmailInfo = getProductTemplateEmailInfo().copy();
            String subject = templateEmailInfo.getMessageSubject() + " - " + getMotifValue();
            templateEmailInfo.setMessageSubject(subject);
            templateEmailInfo.setMessageFrom(messageFrom);
            params.put(PRODUCT_EMAIL_PARAM, getProduct());
            params.put(MAGASIN_EMAIL_PARAM, getMagasin());
            params.put(QUESTION_EMAIL_PARAM, getQuestion());
            templateEmailInfo.setTemplateParameters(params);
            recipents.add(getMessageAddress(templateEmailInfo));
        } else if (isIphoneForm()) {
            templateEmailInfo = getIphoneTemplateEmailInfo().copy();
            String subject = templateEmailInfo.getMessageSubject() + " - " + getMotifValue();
            templateEmailInfo.setMessageSubject(subject);
            templateEmailInfo.setMessageFrom(messageFrom);
            params.put(MESSAGE_EMAIL_PARAM, getMessage());
            params.put(MOTIF_EMAIL_PARAM, getMotifValue());
            templateEmailInfo.setTemplateParameters(params);
            recipents.add(getMessageAddress(templateEmailInfo));
        } else if (isWebMasterForm()) {
            templateEmailInfo = getWebmasterTemplateEmailInfo().copy();
            String subject = templateEmailInfo.getMessageSubject() + " - " + getMotifValue();
            templateEmailInfo.setMessageSubject(subject);
            templateEmailInfo.setMessageFrom(messageFrom);
            params.put(MESSAGE_EMAIL_PARAM, getMessage());
            params.put(OPERATING_SYSTEM_EMAIL_PARAM, getOperatingSystem());
            params.put(BROWSER_EMAIL_PARAM, getBrowser());
            params.put(CONNECTION_TYPE_EMAIL_PARAM, getConnectionType());
            templateEmailInfo.setTemplateParameters(params);
            recipents.add(getMessageAddress(templateEmailInfo));
        }  // end if-else
        getEmailSender().sendEmailMessage(templateEmailInfo, recipents, true, false);
    }

    /**
     * Saves fields' values into appropriate Repository
     *
     * @throws RepositoryException exception
     */
    private void saveStatistics() throws RepositoryException {
        MutableRepository repository = (MutableRepository) getStatisticsRepository();
        MutableRepositoryItem item = repository.createItem(STATISTICS_REPOSITORY_DESCRIPTOR_NAME);

        item.setPropertyValue(TYPE_STATISTICS_PROP, STATICTICS_TYPE_VALUE_SITE);
        item.setPropertyValue(MOTIF_STATISTICS_PROP, getSelectedMotifId());
        item.setPropertyValue(PROFILE_ID_STATISTICS_PROP, getProfileId());

        item.setPropertyValue(CIVILITE_STATISTICS_PROP, getCivilite());
        item.setPropertyValue(NOM_STATISTICS_PROP, getNom());
        item.setPropertyValue(PRENOM_STATISTICS_PROP, getPrenom());
        item.setPropertyValue(EMAIL_STATISTICS_PROP, getEmail());
        item.setPropertyValue(ADRESSE_STATISTICS_PROP,
                              getAddress1() + " " + getAddress2() + " " + getAddress3() + " " + getLocality());
        item.setPropertyValue(VILLE_STATISTICS_PROP, getCity());
        item.setPropertyValue(CODE_POSTAL_STATISTICS_PROP, getPostalCode());
        item.setPropertyValue(TELEPHONE_STATISTICS_PROP, getPhone());

        item.setPropertyValue(DATE_ENREGISTREMENT_STATISTICS_PROP, new java.util.Date());

        if (isProductForm()) {
            item.setPropertyValue(REFERENCE_PRODUIT_STATISTICS_PROP,
                                  StringUtils.isBlank(getProduct()) ? "" : getProduct());
            item.setPropertyValue(MAGASIN_STATISTICS_PROP, StringUtils.isBlank(getMagasin()) ? "" : getMagasin());
            item.setPropertyValue(QUESTION_STATISTICS_PROP, getQuestion());
        } else if (!isProductForm()) {
            item.setPropertyValue(MESSAGE_STATISTICS_PROP, getMessage());
            item.setPropertyValue(SYSTEME_EXPLOITATION_STATISTICS_PROP, getOperatingSystem());
            item.setPropertyValue(NAVIGATEUR_STATISTICS_PROP, getBrowser());
            item.setPropertyValue(CONNEXION_STATISTICS_PROP, getConnectionType());
        }

        repository.addItem(item);
    }

    /**
     * Clears the form after successful submit
     */
    private void clearForm() {
        super.clearAfterSubmission();
        setProduct(null);
        setMagasin(null);
        setQuestion(null);
        setMessage(null);
        setOperatingSystem(null);
        setBrowser(null);
        setConnectionType(null);
    }

    /**
     * Validates user' input data.
     *
     * @param  pRequest  DynamoHttpServletRequest object
     * @param  pResponse DynamoHttpServletResponse object
     *
     * @return <code>false</code>
     *
     * @throws RepositoryException exception
     * @throws ServletException    exception
     */
    protected boolean validateUserData(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                throws RepositoryException, ServletException {
        if (StringUtils.isBlank(getMotifName())) {
            String msg = formatMessage(MSG_EMPTY_MOTIF, pRequest, pResponse);
            addFormException(new DropletFormException(msg, MSG_EMPTY_MOTIF));
        }
        if (StringUtils.isBlank(getNom())) {
            String msg = formatMessage(MSG_EMPTY_NOM, pRequest, pResponse);
            addFormException(new DropletFormException(msg, MSG_EMPTY_NOM));
        }
        if (StringUtils.isBlank(getPrenom())) {
            String msg = formatMessage(MSG_EMPTY_PRENOM, pRequest, pResponse);
            addFormException(new DropletFormException(msg, MSG_EMPTY_PRENOM));
        }
        if (StringUtils.isBlank(getEmail())) {
            String msg = formatMessage(MSG_EMPTY_EMAIL, pRequest, pResponse);
            addFormException(new DropletFormException(msg, MSG_EMPTY_EMAIL));
        }

        validateAddress(convertPrimaryAddress(), this);

        if (isIphoneForm()) {
            if (StringUtils.isBlank(getMessage())) {
                String msg = formatMessage(MSG_EMPTY_REMARQUE, pRequest, pResponse);
                addFormException(new DropletFormException(msg, MSG_EMPTY_REMARQUE));
            }
        } else if (isProductForm()) {
            if (StringUtils.isBlank(getQuestion())) {
                String msg = formatMessage(MSG_EMPTY_QUESTION, pRequest, pResponse);
                addFormException(new DropletFormException(msg, MSG_EMPTY_QUESTION));
            }
        } else if (isWebMasterForm()) {
            if (StringUtils.isBlank(getMessage())) {
                String msg = formatMessage(MSG_EMPTY_REMARQUE, pRequest, pResponse);
                addFormException(new DropletFormException(msg, MSG_EMPTY_REMARQUE));
            }
            if (StringUtils.isBlank(getOperatingSystem())) {
                String msg = formatMessage(MSG_EMPTY_OS, pRequest, pResponse);
                addFormException(new DropletFormException(msg, MSG_EMPTY_OS));
            }
            if (StringUtils.isBlank(getBrowser())) {
                String msg = formatMessage(MSG_EMPTY_BROWSER, pRequest, pResponse);
                addFormException(new DropletFormException(msg, MSG_EMPTY_BROWSER));
            }
            if (StringUtils.isBlank(getConnectionType())) {
                String msg = formatMessage(MSG_EMPTY_CONNECTION, pRequest, pResponse);
                addFormException(new DropletFormException(msg, MSG_EMPTY_CONNECTION));
            }
        }  // end if-else

        if (!StringUtils.isBlank(getEmail()) && !Validator.validateEmail(getEmail())) {
            String msg = formatMessage(MSG_INVALID_EMAIL, pRequest, pResponse);
            addFormException(new DropletFormException(msg, MSG_INVALID_EMAIL));
        }

        return false;

    }

    /**
     * Returns productForm property.
     *
     * @return productForm property.
     *
     * @throws RepositoryException - exception
     */
    private boolean isProductForm() throws RepositoryException {
        String result = (String) getSelectedMotif().getPropertyValue(FORM_TYPE_MOTIF_PROP);
        return result.equals(MOTIF_NAME_PRODUCT);
    }

    /**
     * Returns selectedMotifId property.
     *
     * @return selectedMotifId property.
     *
     * @throws RepositoryException - exception
     */
    private String getSelectedMotifId() throws RepositoryException {
        return getSelectedMotif().getRepositoryId();
    }

    /**
     * Returns selectedMotif property.
     *
     * @return selectedMotif property.
     *
     * @throws RepositoryException - exception
     */
    public RepositoryItem getSelectedMotif() throws RepositoryException {
        return getMotifRepository().getItem(getMotifName(), MOTIF_DESCRIPTOR_NAME);
    }

    /**
     * Returns messageAddress property.
     *
     * @param  templateEmailInfo parameter to set.
     *
     * @return messageAddress property.
     *
     * @throws RepositoryException - exception
     */
    public String getMessageAddress(CastTemplateEmailInfoImpl templateEmailInfo) throws RepositoryException {
        String messageTo;
        RepositoryItem motif = getMotifRepository().getItem(getMotifName(), MOTIF_DESCRIPTOR_NAME);
        boolean sendToCastoDirect = (Boolean) motif.getPropertyValue("sendToCastoDirect");
        if (sendToCastoDirect) {
            messageTo = templateEmailInfo.getMessageToCastodirect();
            templateEmailInfo.setMessageTo(messageTo);
        } else {
            messageTo = templateEmailInfo.getMessageTo();
        }
        return messageTo;
    }

    /**
     * Returns browsers property.
     *
     * @return browsers property.
     */
    public List<String> getBrowsers() {
        return getPropertiesList(BROWSER_PROPERTY);
    }

    /**
     * Returns connectionTypes property.
     *
     * @return connectionTypes property.
     */
    public List<String> getConnectionTypes() {
        return getPropertiesList(CONNECTION_PROPERTY);
    }

    /**
     * Returns productTemplateEmailInfo property.
     *
     * @return productTemplateEmailInfo property.
     */
    public CastTemplateEmailInfoImpl getProductTemplateEmailInfo() {
        return mProductTemplateEmailInfo;
    }

    /**
     * Sets the value of the productTemplateEmailInfo property.
     *
     * @param pProductTemplateEmailInfo parameter to set.
     */
    public void setProductTemplateEmailInfo(CastTemplateEmailInfoImpl pProductTemplateEmailInfo) {
        this.mProductTemplateEmailInfo = pProductTemplateEmailInfo;
    }

    /**
     * Returns webmasterTemplateEmailInfo property.
     *
     * @return webmasterTemplateEmailInfo property.
     */
    public CastTemplateEmailInfoImpl getWebmasterTemplateEmailInfo() {
        return mWebmasterTemplateEmailInfo;
    }

    /**
     * Sets the value of the webmasterTemplateEmailInfo property.
     *
     * @param pWebmasterTemplateEmailInfo parameter to set.
     */
    public void setWebmasterTemplateEmailInfo(CastTemplateEmailInfoImpl pWebmasterTemplateEmailInfo) {
        this.mWebmasterTemplateEmailInfo = pWebmasterTemplateEmailInfo;
    }

    /**
     * Returns operatingSystems property.
     *
     * @return operatingSystems property.
     */
    public List<String> getOperatingSystems() {
        return getPropertiesList(OS_PROPERTY);
    }

    /**
     * Returns message property.
     *
     * @return message property.
     */
    public String getMessage() {
        return mMessage;
    }

    /**
     * Sets the value of the message property.
     *
     * @param pMessage parameter to set.
     */
    public void setMessage(String pMessage) {
        this.mMessage = pMessage;
    }

    /**
     * Returns product property.
     *
     * @return product property.
     */
    public String getProduct() {
        return mProduct;
    }

    /**
     * Sets the value of the product property.
     *
     * @param pProduct parameter to set.
     */
    public void setProduct(String pProduct) {
        this.mProduct = pProduct;
    }

    /**
     * Returns magasin property.
     *
     * @return magasin property.
     */
    public String getMagasin() {
        return mMagasin;
    }

    /**
     * Sets the value of the magasin property.
     *
     * @param pMagasin parameter to set.
     */
    public void setMagasin(String pMagasin) {
        this.mMagasin = pMagasin;
    }

    /**
     * Returns question property.
     *
     * @return question property.
     */
    public String getQuestion() {
        return mQuestion;
    }

    /**
     * Sets the value of the question property.
     *
     * @param pQuestion parameter to set.
     */
    public void setQuestion(String pQuestion) {
        this.mQuestion = pQuestion;
    }

    /**
     * Returns operatingSystem property.
     *
     * @return operatingSystem property.
     */
    public String getOperatingSystem() {
        return mOperatingSystem;
    }

    /**
     * Sets the value of the operatingSystem property.
     *
     * @param pOperatingSystem parameter to set.
     */
    public void setOperatingSystem(String pOperatingSystem) {
        this.mOperatingSystem = pOperatingSystem;
    }

    /**
     * Returns browser property.
     *
     * @return browser property.
     */
    public String getBrowser() {
        return mBrowser;
    }

    /**
     * Sets the value of the browser property.
     *
     * @param pBrowser parameter to set.
     */
    public void setBrowser(String pBrowser) {
        this.mBrowser = pBrowser;
    }

    /**
     * Returns connectionType property.
     *
     * @return connectionType property.
     */
    public String getConnectionType() {
        return mConnectionType;
    }

    /**
     * Sets the value of the connectionType property.
     *
     * @param pConnectionType parameter to set.
     */
    public void setConnectionType(String pConnectionType) {
        this.mConnectionType = pConnectionType;
    }

    /**
     * Returns motifRepository property.
     *
     * @return motifRepository property.
     */
    public Repository getMotifRepository() {
        return mMotifRepository;
    }

    /**
     * Sets the value of the motifRepository property.
     *
     * @param motifRepository parameter to set.
     */
    public void setMotifRepository(Repository motifRepository) {
        this.mMotifRepository = motifRepository;
    }

    /**
     * Returns statisticsRepository property.
     *
     * @return statisticsRepository property.
     */
    public Repository getStatisticsRepository() {
        return mStatisticsRepository;
    }

    /**
     * Sets the value of the statisticsRepository property.
     *
     * @param statisticsRepository parameter to set.
     */
    public void setStatisticsRepository(Repository statisticsRepository) {
        this.mStatisticsRepository = statisticsRepository;
    }

    /**
     * Returns motifName property.
     *
     * @return motifName property.
     */
    public String getMotifName() {
        return mMotifName;
    }

    /**
     * Sets the value of the motifName property.
     *
     * @param pMotifName parameter to set.
     */
    public void setMotifName(String pMotifName) {
        this.mMotifName = pMotifName;
    }

    /**
     * Returns formInitialized property.
     *
     * @return formInitialized property.
     */
    public boolean isFormInitialized() {
        return mFormInitialized;
    }

    /**
     * Sets the value of the formInitialized property.
     *
     * @param pFormInitialized parameter to set.
     */
    public void setFormInitialized(boolean pFormInitialized) {
        this.mFormInitialized = pFormInitialized;
    }

    /**
     * Returns formSubmitted property.
     *
     * @return formSubmitted property.
     */
    public boolean isFormSubmitted() {
        return mFormSubmitted;
    }

    /**
     * Sets the value of the formSubmitted property.
     *
     * @param pFormSubmitted parameter to set.
     */
    public void setFormSubmitted(boolean pFormSubmitted) {
        this.mFormSubmitted = pFormSubmitted;
    }

    /**
     * Returns formRedirects property.
     *
     * @return formRedirects property.
     */
    public boolean isFormRedirects() {
        return mFormRedirects;
    }

    /**
     * Sets the value of the formRedirects property.
     *
     * @param pFormRedirects parameter to set.
     */
    public void setFormRedirects(boolean pFormRedirects) {
        this.mFormRedirects = pFormRedirects;
    }

    /**
     * Returns motifValue property.
     *
     * @return motifValue property.
     */
    public String getMotifValue() {
        return mMotifValue;
    }

    /**
     * Sets the value of the motifValue property.
     *
     * @param pMotifValue parameter to set.
     */
    public void setMotifValue(String pMotifValue) {
        this.mMotifValue = pMotifValue;
    }

    /**
     * Returns motifObjects property.
     *
     * @return motifObjects property.
     */
    public List<RepositoryItem> getMotifObjects() {
        return motifObjects;
    }

    /**
     * Sets the value of the motifObjects property.
     *
     * @param pMotifObjects parameter to set.
     */
    public void setMotifObjects(List<RepositoryItem> pMotifObjects) {
        this.motifObjects = pMotifObjects;
    }

    /**
     * Returns webMasterForm property.
     *
     * @return webMasterForm property.
     */
    public boolean isWebMasterForm() {
        return mWebMasterForm;
    }

    /**
     * Sets the value of the webMasterForm property.
     *
     * @param pWebMasterForm parameter to set.
     */
    public void setWebMasterForm(boolean pWebMasterForm) {
        this.mWebMasterForm = pWebMasterForm;
    }

    /**
     * Returns iphoneForm property.
     *
     * @return iphoneForm property.
     */
    public boolean isIphoneForm() {
        return mIphoneForm;
    }

    /**
     * Sets the value of the iphoneForm property.
     *
     * @param pIphoneForm parameter to set.
     */
    public void setIphoneForm(boolean pIphoneForm) {
        this.mIphoneForm = pIphoneForm;
    }

    /**
     * Sets template email for contact us iphone.
     *
     * @return the mIphoneTemplateEmailInfo
     */
    public CastTemplateEmailInfoImpl getIphoneTemplateEmailInfo() {
        return mIphoneTemplateEmailInfo;
    }

    /**
     * Gets template email for contact us iphone.
     *
     * @param iphoneTemplateEmailInfo the mIphoneTemplateEmailInfo to set
     */
    public void setIphoneTemplateEmailInfo(CastTemplateEmailInfoImpl iphoneTemplateEmailInfo) {
        mIphoneTemplateEmailInfo = iphoneTemplateEmailInfo;
    }
}
