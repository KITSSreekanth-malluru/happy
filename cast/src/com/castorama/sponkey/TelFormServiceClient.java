package com.castorama.sponkey;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.xml.rpc.ServiceException;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.AxisClient;
import org.apache.axis.configuration.FileProvider;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class TelFormServiceClient extends DynamoServlet {

	private static final String SEPARATOR = "#";
	/** OUTPUT constant. */
	static final ParameterName OUTPUT = ParameterName.getParameterName("output");
	static final String ACCAUNT_INFORMATION = "accauntInformaton";

	/** Input parameters */
	static final String PHONE_FIX = "phoneFix";
	static final String USER_SESSION = "userSession";

	/** language property. */
	private int mLanguage;

	/** password property. */
	private String mPassword;

	/** id property. */
	private int mId;

	/** ip property. */
	private String mIp;

	/** wsdl property. */
	private String mEndPoint;

	/** timeout property. */
	private int mTimeout = 5000;

	/** proxyHost property. */
	private String mProxyHost;

	/** proxyPort property. */
	private int mProxyPort;

	/** proxyUser property. */
	private String mProxyUser;

	/** proxyPassword property. */
	private String mProxyPassword;

    private boolean mProxyEnabled;

	/**
	 * Returns language property.
	 * 
	 * @return language property.
	 */
	public int getLanguage() {
		return mLanguage;
	}

	/**
	 * Sets the value of the language property.
	 * 
	 * @param pLanguage
	 *            parameter to set.
	 */
	public void setLanguage(int pLanguage) {
		mLanguage = pLanguage;
	}

	/**
	 * Returns password property.
	 * 
	 * @return password property.
	 */
	public String getPassword() {
		return mPassword;
	}

	/**
	 * Sets the value of the password property.
	 * 
	 * @param pPassword
	 *            parameter to set.
	 */
	public void setPassword(String pPassword) {
		mPassword = pPassword;
	}

	/**
	 * Returns id property.
	 * 
	 * @return id property.
	 */
	public int getId() {
		return mId;
	}

	/**
	 * Sets the value of the id property.
	 * 
	 * @param pId
	 *            parameter to set.
	 */
	public void setId(int pId) {
		mId = pId;
	}

	/**
	 * Returns ip property.
	 * 
	 * @return ip property.
	 */
	public String getIp() {
		return mIp;
	}

	/**
	 * Sets the value of the ip property.
	 * 
	 * @param pIp
	 *            parameter to set.
	 */
	public void setIp(String pIp) {
		mIp = pIp;
	}

	/**
	 * Returns EndPoint property.
	 * 
	 * @return EndPoint property.
	 */
	public String getEndPoint() {
		return mEndPoint;
	}

	/**
	 * Sets the value of the EndPoint property.
	 * 
	 * @param pEndPoint
	 *            parameter to set.
	 */
	public void setEndPoint(String pEndPoint) {
		mEndPoint = pEndPoint;
	}

	public int getTimeout() {
		return mTimeout;
	}

	public void setTimeout(int timeout) {
		mTimeout = timeout;
	}

	public String getProxyHost() {
		return mProxyHost;
	}

	public void setProxyHost(String proxyHost) {
		mProxyHost = proxyHost;
	}

	public int getProxyPort() {
		return mProxyPort;
	}

	public void setProxyPort(int proxyPort) {
		mProxyPort = proxyPort;
	}

	public String getProxyUser() {
		return mProxyUser;
	}

	public void setProxyUser(String proxyUser) {
		mProxyUser = proxyUser;
	}

	public String getProxyPassword() {
		return mProxyPassword;
	}

	public void setProxyPassword(String proxyPassword) {
		mProxyPassword = proxyPassword;
	}

    public boolean getProxyEnabled() {
		return mProxyEnabled;
	}

	public void setProxyEnabled(boolean isProxyEnabled) {
		this.mProxyEnabled = isProxyEnabled;
	}

	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {

		TelFormLocator telFormServiceLocator = new TelFormLocator();
		
		StringBuilder sb = new StringBuilder();

		try {
			telFormServiceLocator.setEngine(new AxisClient());
			TelFormSoap_PortType telFormSoap = telFormServiceLocator.getTelFormSoap(new URL(getEndPoint()));
			EngineConfiguration engineConf = new FileProvider(TelFormLocator.class.getResourceAsStream("client-config.wsdd"));
			telFormServiceLocator.setEngineConfiguration(engineConf);          
			telFormServiceLocator.setEngine(new AxisClient(engineConf));
			((TelFormSoap_BindingStub)telFormSoap).setTimeout(getTimeout());
			
			String phoneFix = (String) pRequest.getObjectParameter(PHONE_FIX);
			String userSession = (String) pRequest.getObjectParameter(USER_SESSION);

			//TelFormSoap12Stub stub = new TelFormSoap12Stub(new URL(getEndPoint()), null);
			//stub.setTimeout(getTimeout());
			MaTelFormV2 ma = telFormSoap.getTelFormV2(phoneFix, getId(), getPassword(), getLanguage(), userSession, getIp());

			if (ma.getCodeErreur() == 1) {
				sb.append(ma.getCodeErreur()).append(SEPARATOR);
				sb.append(ma.getPrenom()).append(SEPARATOR);
				sb.append(ma.getNom()).append(SEPARATOR);
				sb.append(ma.getVille()).append(SEPARATOR);
				sb.append(ma.getCodePostal()).append(SEPARATOR);
				sb.append(ma.getPays()).append(SEPARATOR);
				sb.append(ma.getTelFixe()).append(SEPARATOR);
				sb.append(ma.getRaisonsociale()).append(SEPARATOR);
				sb.append(ma.getNumero()).append(SEPARATOR);
				sb.append(ma.getComplementnumero()).append(SEPARATOR);
				sb.append(ma.getTypevoiecourt()).append(SEPARATOR);
				sb.append(ma.getLibelleVoie()).append(SEPARATOR);
				sb.append(ma.getComplementAdresse()).append(SEPARATOR);
				sb.append(ma.getBoitepostale()).append(SEPARATOR);
				sb.append(ma.getCedex()).append(SEPARATOR);
			} else {
				sb.append(ma.getCodeErreur()).append(SEPARATOR);
				sb.append(ma.getMsgErreur()).append(SEPARATOR);
			}
		} catch (ServiceException e) {
			if(isLoggingError()) {
				logError(e);
			}
		}
		pRequest.setParameter(ACCAUNT_INFORMATION, sb.toString());
		pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
	}
}
