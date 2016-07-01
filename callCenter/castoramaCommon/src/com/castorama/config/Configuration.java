package com.castorama.config;
import java.io.File;

import atg.dtm.*;
import atg.service.lockmanager.*;

import java.util.Properties;

/**
* Configuration : Castorama 2001
* @author Damien DURIEZ - INTERNENCE (Novembre 2001) 
*/
public class Configuration{
	
	public static Configuration CONFIGURATION;
	
	/**
	* Constructeur
	* @param		none.
	* @return		none
	* @exception	none
	*/
	public Configuration(){
		if(CONFIGURATION==null)
            {
            CONFIGURATION = this;
            }
	}
	
	
	/**
	* @param	none.
	* @return	Configuration
	* @exception none
	*/
	public static Configuration getConfiguration(){
		return CONFIGURATION;
	}
	
	
	/**
	* Parameter LoginInvalidationCaches : 
	*/
	protected String m_strLoginInvalidationCaches;
	
	/**
	* get du LoginInvalidationCaches
	* @param none
	* @return String
	* @throws none
	*/
	public String getLoginInvalidationCaches(){
		return m_strLoginInvalidationCaches;
	}
	
	/**
	* Modification du LoginInvalidationCaches
	* @param String
	* @return none
	* @throws none
	*/
	public void setLoginInvalidationCaches(String a_strLoginInvalidationCaches){
		m_strLoginInvalidationCaches= a_strLoginInvalidationCaches ;
	}
	
	
	/**
	* Parameter PasswordInvalidationCaches
	*/
	protected String m_strPasswordInvalidationCaches;
	
	/**
	* R�cup�ration du PasswordInvalidationCaches
	* @param none
	* @return String
	* @throws none
	*/
	public String getPasswordInvalidationCaches(){
		return m_strPasswordInvalidationCaches;
	}
	
	/**
	* Modification du PasswordInvalidationCaches
	* @param String
	* @return none
	* @throws none
	*/
	public void setPasswordInvalidationCaches(String a_strPasswordInvalidationCaches){
		m_strPasswordInvalidationCaches= a_strPasswordInvalidationCaches ;
	}
	
	
	private	TransactionManagerImpl	m_TransactionManager;


	/**
	* get du TransactionManager
	* @param none
	* @return TransactionManagerImpl
	* @throws none
	*/
	public TransactionManagerImpl getTransactionManager(){
		return m_TransactionManager;
	}
	
	
	/**
	* Modification du TransactionManager
	* @param TransactionManagerImpl TransactionManager
	* @return none
	* @throws none
	*/
	public void   setTransactionManager(TransactionManagerImpl a_TransactionManager){
		m_TransactionManager = a_TransactionManager ;
	}
	
	
	
	private	ClientLockManager	m_ClientLockManager;


	/**
	* get du ClientLockManager
	* @param none
	* @return ClientLockManager
	* @throws none
	*/
	public ClientLockManager getClientLockManager(){
		return m_ClientLockManager;
	}
	
	
	/**
	* Modification du ClientLockManager
	* @param ClientLockManager ClientLockManager
	* @return none
	* @throws none
	*/
	public void   setClientLockManager(ClientLockManager a_ClientLockManager){
		m_ClientLockManager = a_ClientLockManager ;
	}
	
	
	
	Properties m_CodePostalError = new Properties();
	
	public Properties getCodePostalError()
	{
		return m_CodePostalError;
	}
	
	public void setCodePostalError(Properties a_CodePostalError)
	{
		m_CodePostalError=a_CodePostalError;
	}
	
	Properties m_CodePostalInfo = new Properties();
	
	public Properties getCodePostalInfo()
	{
		return m_CodePostalInfo;
	}
	
	public void setCodePostalInfo(Properties a_CodePostalInfo)
	{
		m_CodePostalInfo=a_CodePostalInfo;
	}
	
	
	protected String m_strEmailTeleperformance;
	
	/**
	* @param none
	* @return String
	* @throws none
	*/
	public String getEmailTeleperformance(){
		return m_strEmailTeleperformance;
	}
	
	/**
	* Modification du EmailTeleperformance
	* @param String
	* @return none
	* @throws none
	*/
	public void setEmailTeleperformance(String a_strEmailTeleperformance){
		m_strEmailTeleperformance= a_strEmailTeleperformance ;
	}
	
	
	protected String m_strVpcMailDomaine;
	
	/**
	* @param none
	* @return String
	* @throws none
	*/
	public String getVpcMailDomaine(){
		return m_strVpcMailDomaine;
	}
	
	/**
	* Modification du VpcMailDomaine
	* @param String
	* @return none
	* @throws none
	*/
	public void setVpcMailDomaine(String a_strVpcMailDomaine){
		m_strVpcMailDomaine= a_strVpcMailDomaine ;
	}
	
	
	protected String m_strSiteHttpServerName;
	
	/**
	* @param none
	* @return String
	* @throws none
	*/
	public String getSiteHttpServerName(){
		return m_strSiteHttpServerName;
	}
	
	/**
	* @param String
	* @return none
	* @throws none
	*/
	public void setSiteHttpServerName(String a_strSiteHttpServerName){
		m_strSiteHttpServerName= a_strSiteHttpServerName ;
	}
	
	
	protected String m_strCaptureMode;
	
	/**
	* @param none
	* @return String
	* @throws none
	*/
	public String getCaptureMode(){
		return m_strCaptureMode;
	}
	
	/**
	* Modification du CaptureMode
	* @param String
	* @return none
	* @throws none
	*/
	public void setCaptureMode(String a_strCaptureMode){
		m_strCaptureMode= a_strCaptureMode ;
	}
	
	
	protected String m_strCaptureDay;
	
	/**
	* @param none
	* @return String
	* @throws none
	*/
	public String getCaptureDay(){
		return m_strCaptureDay;
	}
	
	/**
	* Modification du CaptureDay
	* @param String
	* @return none
	* @throws none
	*/
	public void setCaptureDay(String a_strCaptureDay){
		m_strCaptureDay= a_strCaptureDay ;
	}
	
	protected boolean m_bEnvoyerUnMailDeDebugSiErreurExportCommande;
	
	/**
	* @param none
	* @return boolean
	* @throws none
	*/
	public boolean getEnvoyerUnMailDeDebugSiErreurExportCommande() {
	  	return m_bEnvoyerUnMailDeDebugSiErreurExportCommande;
	}
	
	/**
	* Modification de EnvoyerUnMailDeDebugSiErreurExportCommande
	* @param boolean
	* @return none
	* @throws none
	*/
	public void setEnvoyerUnMailDeDebugSiErreurExportCommande(boolean a_bEnvoyerUnMailDeDebugSiErreurExportCommande) {
	  	m_bEnvoyerUnMailDeDebugSiErreurExportCommande= a_bEnvoyerUnMailDeDebugSiErreurExportCommande;
	}
	
	protected String m_strEmailToDebugReportSiErreurExportCommande;
	
	/**
	* @param none
	* @return String
	* @throws none
	*/
	public String getEmailToDebugReportSiErreurExportCommande(){
		return m_strEmailToDebugReportSiErreurExportCommande;
	}
	
	/**
	* Modification du EmailToDebugReportSiErreurExportCommande
	* @param String
	* @return none
	* @throws none
	*/
	public void setEmailToDebugReportSiErreurExportCommande(String a_strEmailToDebugReportSiErreurExportCommande){
		m_strEmailToDebugReportSiErreurExportCommande = a_strEmailToDebugReportSiErreurExportCommande ;
	}
	
	
	protected boolean m_bEnvoyerUnMailDeDebugSiErreurAutomatiqueResponseSIPS;
	
	/**
	* @param none
	* @return boolean
	* @throws none
	*/
	public boolean getEnvoyerUnMailDeDebugSiErreurAutomatiqueResponseSIPS() {
	  	return m_bEnvoyerUnMailDeDebugSiErreurAutomatiqueResponseSIPS;
	}
	
	/**
	* Modification de EnvoyerUnMailDeDebugSiErreurAutomatiqueResponseSIPS
	* @param boolean
	* @return none
	* @throws none
	*/
	public void setEnvoyerUnMailDeDebugSiErreurAutomatiqueResponseSIPS(boolean a_bEnvoyerUnMailDeDebugSiErreurAutomatiqueResponseSIPS) {
	  	m_bEnvoyerUnMailDeDebugSiErreurAutomatiqueResponseSIPS = a_bEnvoyerUnMailDeDebugSiErreurAutomatiqueResponseSIPS;
	}
	
	protected String m_strEmailToDebugReportSiErreurAutomatiqueResponseSIPS;
	
	/**
	* @param none
	* @return String
	* @throws none
	*/
	public String getEmailToDebugReportSiErreurAutomatiqueResponseSIPS(){
		return m_strEmailToDebugReportSiErreurAutomatiqueResponseSIPS;
	}
	
	/**
	* Modification du EmailToDebugReportSiErreurAutomatiqueResponseSIPS
	* @param String
	* @return none
	* @throws none
	*/
	public void setEmailToDebugReportSiErreurAutomatiqueResponseSIPS(String a_strEmailToDebugReportSiErreurAutomatiqueResponseSIPS){
		m_strEmailToDebugReportSiErreurAutomatiqueResponseSIPS = a_strEmailToDebugReportSiErreurAutomatiqueResponseSIPS ;
	}
	
	/**
	* activerLogsDeRecherche : <br>
	*/
	protected boolean m_bWithTraceGetSet ;
	
	/**
	* @param none
	* @return boolean m_bWithTraceGetSet
	* @throws none
	*/
	public boolean getWithTraceGetSet(){
		return m_bWithTraceGetSet;
	}
	
	
	/**
	* @param boolean a_bWithTraceGetSet
	* @return none
	* @throws none
	*/
	public void setWithTraceGetSet(boolean a_bWithTraceGetSet){
		m_bWithTraceGetSet = a_bWithTraceGetSet ;
	}

	protected int	m_nExceptionLevel;
	
	
	/**
	* @param none
	* @return int ExceptionLevel
	* @throws none
	*/
	public int getExceptionLevel(){
		return m_nExceptionLevel;
	}
	
	
	/**
	* Modification de ExceptionLevel
	* @param int ExceptionLevel
	* @return none
	* @throws none
	*/
	public void setExceptionLevel(int a_nExceptionLevel){
		m_nExceptionLevel = a_nExceptionLevel ;
	}
	
	
	
	/**
	* activerLogsDeRecherche : <br>
	*/
	protected boolean m_bActiverLogsDeRecherche ;
	
	/**
	* @param none
	* @return boolean ActiverLogsDeRecherche
	* @throws none
	*/
	public boolean getActiverLogsDeRecherche(){
		return m_bActiverLogsDeRecherche;
	}
	
	
	/**
	* Modification de ActiverLogsDeRecherche
	* @param boolean ActiverLogsDeRecherche
	* @return none
	* @throws none
	*/
	public void setActiverLogsDeRecherche(boolean a_bActiverLogsDeRecherche){
		m_bActiverLogsDeRecherche = a_bActiverLogsDeRecherche ;
	}
	
	
	
	protected String m_strVeritySpiderAdress ;
	
	/**
	* @param none
	* @return String
	* @throws none
	*/
	public String getVeritySpiderAdress(){
		return m_strVeritySpiderAdress;
	}
	
	/**
	* Modification du VeritySpiderAdress
	* @param String VeritySpiderAdress
	* @return none
	* @throws none
	*/
	public void setVeritySpiderAdress(String a_strVeritySpiderAdress){
		m_strVeritySpiderAdress = a_strVeritySpiderAdress ;
	}
	
	
	protected String m_strSIPSCurrencyCode ;
	
	/**
	* @param none
	* @return String
	* @throws none
	*/
	public String getSIPSCurrencyCode(){
		return m_strSIPSCurrencyCode;
	}
	
	/**
	* Modification du SIPSCurrencyCode
	* @param String SIPSCurrencyCode
	* @return none
	* @throws none
	*/
	public void setSIPSCurrencyCode(String a_strSIPSCurrencyCode){
		m_strSIPSCurrencyCode = a_strSIPSCurrencyCode ;
	}
	
	protected String m_strEuroSymbol ;
	
	/**
	* @param none
	* @return String
	* @throws none
	*/
	public String getEuroSymbol(){
		return m_strEuroSymbol;
	}
	
	/**
	* Modification du EuroSymbol
	* @param String EuroSymbol
	* @return none
	* @throws none
	*/
	public void setEuroSymbol(String a_strEuroSymbol){
		m_strEuroSymbol = a_strEuroSymbol ;
	}
	
	protected String m_strPrixComCallCenterFrancs ;
	
	/**
	* @param none
	* @return String
	* @throws none
	*/
	public String getPrixComCallCenterFrancs(){
		return m_strPrixComCallCenterFrancs;
	}
	
	/**
	* Modification du PrixComCallCenterFrancs
	* @param String PrixComCallCenterFrancs
	* @return none
	* @throws none
	*/
	public void setPrixComCallCenterFrancs(String a_strPrixComCallCenterFrancs){
		m_strPrixComCallCenterFrancs = a_strPrixComCallCenterFrancs ;
	}
	
	protected String m_strPrixComCallCenterEuros ;
	
	/**
	* @param none
	* @return String
	* @throws none
	*/
	public String getPrixComCallCenterEuros(){
		return m_strPrixComCallCenterEuros;
	}
	
	/**
	* Modification du PrixComCallCenterEuros
	* @param String PrixComCallCenterEuros
	* @return none
	* @throws none
	*/
	public void setPrixComCallCenterEuros(String a_strPrixComCallCenterEuros){
		m_strPrixComCallCenterEuros = a_strPrixComCallCenterEuros ;
	}
	
	protected String m_strAffichage_prix_euros ;
	
	/**
	* @param none
	* @return String
	* @throws none
	*/
	public String getAffichage_prix_euros(){
		return m_strAffichage_prix_euros;
	}
	
	public void setAffichage_prix_euros(String a_strAffichage_prix_euros){
		m_strAffichage_prix_euros = a_strAffichage_prix_euros ;
	}
	
	protected String m_strAffichage_prix_francs ;
	
	public String getAffichage_prix_francs(){
		return m_strAffichage_prix_francs;
	}
	
	public void setAffichage_prix_francs(String a_strAffichage_prix_francs){
		m_strAffichage_prix_francs = a_strAffichage_prix_francs ;
	}

	protected String m_strPrixEurosCapitalCastoramaDirect;
	
	public String getPrixEurosCapitalCastoramaDirect(){
		return m_strPrixEurosCapitalCastoramaDirect;
	}
	
	public void setPrixEurosCapitalCastoramaDirect(String a_strPrixEurosCapitalCastoramaDirect){
		m_strPrixEurosCapitalCastoramaDirect = a_strPrixEurosCapitalCastoramaDirect ;
	}	
	
	protected File m_strCheminEntiteXml;
	
	public File getCheminEntiteXml(){
		return m_strCheminEntiteXml;
	}
	
	public void setCheminEntiteXml(File a_strCheminEntiteXml){
		m_strCheminEntiteXml = a_strCheminEntiteXml ;
	}
	
	protected String m_strDureDeValiditeDesContributionsEnMois;
	
	public String getDureDeValiditeDesContributionsEnMois(){
		return m_strDureDeValiditeDesContributionsEnMois;
	}
	
	public void setDureDeValiditeDesContributionsEnMois(String a_strDureDeValiditeDesContributionsEnMois){
		m_strDureDeValiditeDesContributionsEnMois = a_strDureDeValiditeDesContributionsEnMois ;
	}
	
	protected String m_strDureDeValiditeDesAnnoncesCastotrocEnMois;
	
	public String getDureDeValiditeDesAnnoncesCastotrocEnMois(){
		return m_strDureDeValiditeDesAnnoncesCastotrocEnMois;
	}
	
	public void setDureDeValiditeDesAnnoncesCastotrocEnMois(String a_strDureDeValiditeDesAnnoncesCastotrocEnMois){
		m_strDureDeValiditeDesAnnoncesCastotrocEnMois = a_strDureDeValiditeDesAnnoncesCastotrocEnMois ;
	}
	
	protected String m_strHeuresOuvertureCallCenter;
	
	public String getHeuresOuvertureCallCenter(){
		return m_strHeuresOuvertureCallCenter;
	}
	
	public void setHeuresOuvertureCallCenter(String a_strHeuresOuvertureCallCenter){
		m_strHeuresOuvertureCallCenter = a_strHeuresOuvertureCallCenter ;
	}

	boolean m_bAffichageSondageFinSession;

	public boolean getAffichageSondageFinSession(){
		return m_bAffichageSondageFinSession;
	}
	
	public void setAffichageSondageFinSession(boolean a_bAffichageSondageFinSession){
		m_bAffichageSondageFinSession=a_bAffichageSondageFinSession;
	}
	
	String m_CouleurUniversCommunaute;

	public String getCouleurUniversCommunaute(){
		return m_CouleurUniversCommunaute;
	}
	
	public void setCouleurUniversCommunaute(String a_CouleurUniversCommunaute){
		m_CouleurUniversCommunaute=a_CouleurUniversCommunaute;
	}
	
	String m_CouleurUniversConseil;

	public String getCouleurUniversConseil(){
		return m_CouleurUniversConseil;
	}
	
	public void setCouleurUniversConseil(String a_CouleurUniversConseil){
		m_CouleurUniversConseil=a_CouleurUniversConseil;
	}
	
	String m_CouleurUniversMagasin;

	public String getCouleurUniversMagasin(){
		return m_CouleurUniversMagasin;
	}
	
	public void setCouleurUniversMagasin(String a_CouleurUniversMagasin){
		m_CouleurUniversMagasin=a_CouleurUniversMagasin;
	}
	
	String m_SipsSiteAutoResponseUrl;

	public String getSipsSiteAutoResponseUrl(){
		return m_SipsSiteAutoResponseUrl;
	}
	
	public void setSipsSiteAutoResponseUrl(String a_SipsSiteAutoResponseUrl){
		m_SipsSiteAutoResponseUrl=a_SipsSiteAutoResponseUrl;
	}
	
	String m_SipsSiteCancelReturn;

	public String getSipsSiteCancelReturn(){
		return m_SipsSiteCancelReturn;
	}
	
	public void setSipsSiteCancelReturn(String a_SipsSiteCancelReturn){
		m_SipsSiteCancelReturn=a_SipsSiteCancelReturn;
	}
	
	String m_SipsSiteNormalReturn;

	public String getSipsSiteNormalReturn(){
		return m_SipsSiteNormalReturn;
	}
	
	public void setSipsSiteNormalReturn(String a_SipsSiteNormalReturn){
		m_SipsSiteNormalReturn=a_SipsSiteNormalReturn;
	}
	
	String m_SipsCallCenterAutoResponseUrl;

	public String getSipsCallCenterAutoResponseUrl(){
		return m_SipsCallCenterAutoResponseUrl;
	}
	
	public void setSipsCallCenterAutoResponseUrl(String a_SipsCallCenterAutoResponseUrl){
		m_SipsCallCenterAutoResponseUrl=a_SipsCallCenterAutoResponseUrl;
	}
	
	String m_SipsCallCenterCancelReturn;

	public String getSipsCallCenterCancelReturn(){
		return m_SipsCallCenterCancelReturn;
	}
	
	public void setSipsCallCenterCancelReturn(String a_SipsCallCenterCancelReturn){
		m_SipsCallCenterCancelReturn=a_SipsCallCenterCancelReturn;
	}
	
	String m_SipsCallCenterNormalReturn;
	/**
	* @param none
	* @return String
	* @throws none
	*/
	public String getSipsCallCenterNormalReturn(){
		return m_SipsCallCenterNormalReturn;
	}
	
	/**
	* Modification du parametre SIPS
	* @param String m_SipsCallCenterNormalReturn
	* @return none
	* @throws none
	*/
	public void setSipsCallCenterNormalReturn(String a_SipsCallCenterNormalReturn){
		m_SipsCallCenterNormalReturn=a_SipsCallCenterNormalReturn;
	}
	
	String m_SipsPathFile;
	/**
	* @param none
	* @return String
	* @throws none
	*/
	public String getSipsPathFile(){
		return m_SipsPathFile;
	}
	
	/**
	* Modification du parametre SIPS
	* @param String m_SipsCallCenterNormalReturn
	* @return none
	* @throws none
	*/
	public void setSipsPathFile(String a_SipsPathFile){
		m_SipsPathFile=a_SipsPathFile;
	}
	
	String m_SipsMerchantId;
	/**
	* @param none
	* @return String
	* @throws none
	*/
	public String getSipsMerchantId(){
		return m_SipsMerchantId;
	}
	
	/**
	* Modification du parametre SIPS
	* @param String m_SipsCallCenterNormalReturn
	* @return none
	* @throws none
	*/
	public void setSipsMerchantId(String a_SipsMerchantId){
		m_SipsMerchantId=a_SipsMerchantId;
	}
	
	File m_logsFilePath;
	/**
	* @param none
	* @return File
	* @throws none
	*/
	public File getLogsFilePath(){
		return m_logsFilePath;
	}
	
	/**
	* @param File a_logsFilePath
	* @return none
	* @throws none
	*/
	public void setLogsFilePath(File a_logsFilePath){
		m_logsFilePath=a_logsFilePath;
	}									
	File m_logsArchivePath;
	/**
	* @param none
	* @return File
	* @throws none
	*/
	public File getLogsArchivePath(){
		return m_logsArchivePath;
	}
	
	/**
	* Modification du parametre chemin d'acc�s aux fichiers de logs
	* @param File a_logsArchivePath
	* @return none
	* @throws none
	*/
	public void setLogsArchivePath(File a_logsArchivePath){
		m_logsArchivePath=a_logsArchivePath;
	}
	
	String m_MailDefaultFrom;
	/**
	* @param none
	* @return String
	* @throws none
	*/
	public String getMailDefaultFrom(){
		return m_MailDefaultFrom;
	}
	
	/**
	* Modification du parametre Email
	* @param String a_MailDefaultFrom
	* @return none
	* @throws none
	*/
	public void setMailDefaultFrom(String a_MailDefaultFrom){
		m_MailDefaultFrom=a_MailDefaultFrom;
	}
	
	String m_MailAvisExpedition;
	/**
	* @param none
	* @return String
	* @throws none
	*/
	public String getMailAvisExpedition(){
		return m_MailAvisExpedition;
	}
	
	/**
	* Modification du parametre Email
	* @param String a_MailAvisExpedition
	* @return none
	* @throws none
	*/
	public void setMailAvisExpedition(String a_MailAvisExpedition){
		m_MailAvisExpedition=a_MailAvisExpedition;
	}
	
	String m_MailWebmasterETO;
	/**
	* @param none
	* @return String
	* @throws none
	*/
	public String getMailWebmasterETO(){
		return m_MailWebmasterETO;
	}
	
	/**
	* Modification du parametre Email
	* @param String a_MailWebmasterETO
	* @return none
	* @throws none
	*/
	public void setMailWebmasterETO(String a_MailWebmasterETO){
		m_MailWebmasterETO=a_MailWebmasterETO;
	}	
				
	String m_MailMessageFromNewsLetter;
	/**
	* @param none
	* @return String
	* @throws none
	*/
	public String getMailMessageFromNewsLetter(){
		return m_MailMessageFromNewsLetter;
	}
	
	/**
	* Modification du parametre Email
	* @param String a_MailMessageFromNewsLetter
	* @return none
	* @throws none
	*/
	public void setMailMessageFromNewsLetter(String a_MailMessageFromNewsLetter){
		m_MailMessageFromNewsLetter=a_MailMessageFromNewsLetter;
	}	
	
	String m_EmailWebmasterForum;
	/**
	* @param none
	* @return String m_EmailWebmasterForum
	* @throws none
	*/
	public String getEmailWebmasterForum(){
		return m_EmailWebmasterForum;
	}
	
	/**
	* Modification du parametre Email
	* @param String a_EmailWebmasterForum
	* @return none
	* @throws none
	*/
	public void setEmailWebmasterForum(String a_EmailWebmasterForum){
		m_EmailWebmasterForum=a_EmailWebmasterForum;
	}	
	String m_VerityHostServeur1;
	/**
	* @param none
	* @return String
	* @throws none
	*/
	public String getVerityHostServeur1(){
		return m_VerityHostServeur1;
	}
	
	/**
	* Modification du parametre Verity
	* @param String m_VerityHostServeur1
	* @return none
	* @throws none
	*/
	public void setVerityHostServeur1(String a_VerityHostServeur1){
		m_VerityHostServeur1=a_VerityHostServeur1;
	}

	String m_VerityHostServeur2;
	/**
	* @param none
	* @return String
	* @throws none
	*/
	public String getVerityHostServeur2(){
		return m_VerityHostServeur2;
	}
	
	/**
	* Modification du parametre Verity
	* @param String m_VerityHostServeur2
	* @return none
	* @throws none
	*/
	public void setVerityHostServeur2(String a_VerityHostServeur2){
		m_VerityHostServeur2=a_VerityHostServeur2;
	}
	
	int m_VerityPortServeur1;

	public int getVerityPortServeur1(){
		return m_VerityPortServeur1;
	}
	
	/**
	* Modification du parametre Verity
	* @param String m_VerityPortServeur1
	* @return none
	* @throws none
	*/
	public void setVerityPortServeur1(int a_VerityPortServeur1){
		m_VerityPortServeur1=a_VerityPortServeur1;
	}
		
	int m_VerityPortServeur2;

	public int getVerityPortServeur2(){
		return m_VerityPortServeur2;
	}
	
	public void setVerityPortServeur2(int a_VerityPortServeur2){
		m_VerityPortServeur2=a_VerityPortServeur2;
	}
	
	java.util.Properties m_VirtualDirectoryMap;

	public java.util.Properties  getVirtualDirectoryMap(){
		return m_VirtualDirectoryMap;
	}
	
	public void setVirtualDirectoryMap(java.util.Properties a_VirtualDirectoryMap){
		m_VirtualDirectoryMap=a_VirtualDirectoryMap;
	}
	
	/* FRAIS DE PORTS */
	private double m_PrixFraisPortFixe;	
	public void setPrixFraisPortFixe(double a_PrixFraisPortFixe)
	{
		m_PrixFraisPortFixe = a_PrixFraisPortFixe;
	}
	public double getPrixFraisPortFixe()
	{
		return m_PrixFraisPortFixe;
	}
	private double m_PrixFraisPortEnPlusPourNiveau1;	
	public void setPrixFraisPortEnPlusPourNiveau1(double a_PrixFraisPortEnPlusPourNiveau1)
	{
		m_PrixFraisPortEnPlusPourNiveau1 = a_PrixFraisPortEnPlusPourNiveau1;
	}
	public double getPrixFraisPortEnPlusPourNiveau1()
	{
		return m_PrixFraisPortEnPlusPourNiveau1;
	}
	
	private double m_PrixFraisPortEnPlusPourNiveau2;	
	public void setPrixFraisPortEnPlusPourNiveau2(double a_PrixFraisPortEnPlusPourNiveau2)
	{
		m_PrixFraisPortEnPlusPourNiveau2 = a_PrixFraisPortEnPlusPourNiveau2;
	}
	public double getPrixFraisPortEnPlusPourNiveau2()
	{
		return m_PrixFraisPortEnPlusPourNiveau2;
	}	
	/* FIN FRAIS DE PORTS */
			
	String m_UrlDirectorySetState;

	public String getUrlDirectorySetState(){
		return m_UrlDirectorySetState;
	}
	
	public void setUrlDirectorySetState(String a_UrlDirectorySetState2){
		m_UrlDirectorySetState=a_UrlDirectorySetState2;
	}
	
	String m_EmailFromOrderConfirmation;

	public String getEmailFromOrderConfirmation(){
		return m_EmailFromOrderConfirmation;
	}
	
	public void setEmailFromOrderConfirmation(String a_EmailFromOrderConfirmation){
		m_EmailFromOrderConfirmation=a_EmailFromOrderConfirmation;
	}
	
	String m_IpMachineMaintenance;

	public String getIpMachineMaintenance(){
		return m_IpMachineMaintenance;
	}
	
	public void setIpMachineMaintenance(String a_IpMachineMaintenance){
		m_IpMachineMaintenance=a_IpMachineMaintenance;
	}
	
	
	String m_strEmailWebmasterNewsletter;

	public String getEmailWebmasterNewsletter(){
		return m_strEmailWebmasterNewsletter;
	}
	
	public void setEmailWebmasterNewsletter(String a_strEmailWebmasterNewsletter){
		m_strEmailWebmasterNewsletter = a_strEmailWebmasterNewsletter ;
	}
	
	
	String m_strEmailWebmasterSatisfaction;

	public String getEmailWebmasterSatisfaction(){
		return m_strEmailWebmasterSatisfaction;
	}
	
	public void setEmailWebmasterSatisfaction(String a_strEmailWebmasterSatisfaction){
		m_strEmailWebmasterSatisfaction = a_strEmailWebmasterSatisfaction ;
	}

	String m_strAdresseReceptionCheque;

	public String getAdresseReceptionCheque(){
		return m_strAdresseReceptionCheque;
	}
	
	public void setAdresseReceptionCheque(String a_strAdresseReceptionCheque){
		m_strAdresseReceptionCheque = a_strAdresseReceptionCheque ;
	}
	
	String m_OrderExportDirectory;

	public String getOrderExportDirectory(){
		return m_OrderExportDirectory;
	}
	
	public void setOrderExportDirectory(String a_OrderExportDirectory){
		m_OrderExportDirectory=a_OrderExportDirectory;
	}
	String m_CastotrocPubliAuto;

	public String getCastotrocPubliAuto(){
		return m_CastotrocPubliAuto;
	}
	
	public void setCastotrocPubliAuto(String a_CastotrocPubliAuto){
		m_CastotrocPubliAuto=a_CastotrocPubliAuto;
	}
	
	
	int m_SiteSecurePort;

	public int getSiteSecurePort(){
		return m_SiteSecurePort;
	}
	
	public void setSiteSecurePort(int a_SiteSecurePort){
		m_SiteSecurePort=a_SiteSecurePort;
	}
		
	int m_SiteNonSecurePort;

	public int getSiteNonSecurePort(){
		return m_SiteNonSecurePort;
	}
	
	public void setSiteNonSecurePort(int a_SiteNonSecurePort){
		m_SiteNonSecurePort=a_SiteNonSecurePort;
	}	
	
	int m_AdministrationsSecurePort;

	public int getAdministrationsSecurePort(){
		return m_AdministrationsSecurePort;
	}
	
	public void setAdministrationsSecurePort(int a_AdministrationsSecurePort){
		m_AdministrationsSecurePort=a_AdministrationsSecurePort;
	}
		
	int m_AdministrationsNonSecurePort;

	public int getAdministrationsNonSecurePort(){
		return m_AdministrationsNonSecurePort;
	}
	
	public void setAdministrationsNonSecurePort(int a_AdministrationsNonSecurePort){
		m_AdministrationsNonSecurePort=a_AdministrationsNonSecurePort;
	}
	
	
	String m_strFaxCallCenter;
	
	public String getFaxCallCenter(){
		return m_strFaxCallCenter;
	}
	public void setFaxCallCenter(String a_strFaxCallCenter){
		m_strFaxCallCenter = a_strFaxCallCenter ;
	}
	

	String m_strInscriptionNewsletter;

	public String getInscriptionNewsletter(){
		return m_strInscriptionNewsletter;
	}
	
	public void setInscriptionNewsletter(String a_strInscriptionNewsletter){
		m_strInscriptionNewsletter = a_strInscriptionNewsletter ;
	}
	

	String m_strConditionsInscriptionNewsletter;

	public String getConditionsInscriptionNewsletter(){
		return m_strConditionsInscriptionNewsletter;
	}
	
	public void setConditionsInscriptionNewsletter(String a_strConditionsInscriptionNewsletter){
		m_strConditionsInscriptionNewsletter = a_strConditionsInscriptionNewsletter ;
	}
    
    private boolean m_destockExcluRecherche;
    
    public boolean getDestockExcluRecherche()
    {
        return m_destockExcluRecherche;
    }

    public void setDestockExcluRecherche(boolean a_destockExcluRecherche)
    {
        m_destockExcluRecherche = a_destockExcluRecherche;
    }
	
}
