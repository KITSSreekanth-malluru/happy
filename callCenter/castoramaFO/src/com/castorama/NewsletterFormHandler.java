package com.castorama;

import com.castorama.utils.CheckingTools;
import com.castorama.utils.MailTools;
import com.castorama.xml.*;
import com.castorama.config.Configuration;
import java.io.*;
import javax.servlet.*;
import atg.servlet.*;

import java.util.Map;
import java.util.Hashtable;
import java.util.Enumeration;

import atg.droplet.GenericFormHandler;
import atg.droplet.DropletException;
import atg.repository.servlet.RepositoryFormHandler;

import atg.userprofiling.Profile;
import java.util.Date;
import java.util.Vector;
import java.util.List;

import atg.repository.*;
import atg.repository.rql.RqlStatement;
import java.sql.*;

import atg.nucleus.Nucleus;


/**
* NewsletterFormHandler : Castorama 2001
* Ce composant est utilis� pour l'edition des comptes d'abonnement � la Newsletter de castorama.
* @version 1.0  
* @author Damien DURIEZ - INTERNENCE (Mars 2001) 
* @version 2.0  
* @ Remy Lammertin SOPRA ( Aout 2003 )
*/
public class NewsletterFormHandler extends RepositoryFormHandler {
	
	long m_lDateCreation;				// utilis� comme un checksum avant la suppression d'un abonnement
	String m_strUserId;					// Id du profil de l'utilisateur dans le cas ou il possede un compte Mon casto
	Profile m_Profile;					// Profil � mettre � jour lors de la supression d'un abonnement
	String m_strSubmitSuccessURL;		// URL de redirection en cas de succes d'envoi du mail de confirmation d'inscription
	String m_strSubmitErrorURL;			// URL de redirection en cas d'echec
	
	String m_strDesabonnementCastoSuccessURL;		// URL de redirection en cas de succes de desabonnement castorama
	String m_strDesabonnementPartenaireSuccessURL;		// URL de redirection en cas de succes de desabonnement partenaire
	
	String m_strCanalInscritpion ;// canal d'inscription
	String m_strCanalDesInscritpion; // canal de desinscription

	String m_strEmailFromNewsletter;
		
	int m_nSiteHttpServerPort;
	String m_strSiteHttpServerName;
	String m_strTypeDesabonnement	;
	String m_strOldEmail	;
	protected String	m_strMailValidationXslTemplateURL;	// URL du template xsl pour le mail de validation d'inscription
	protected String	m_strMailSuppressionXslTemplateURL;	// URL du template xsl pour le mail de d�sinscription
  
  
	/**
	* R�cup�ration du MailValidationXslTemplateURL
	* @param none
	* @return String MailValidationXslTemplateURL
	* @throws none
	*/
	public String getMailValidationXslTemplateURL(){
		return m_strMailValidationXslTemplateURL;
	}
	
	/**
	* Modification du MailValidationXslTemplateURL
	* @param String MailValidationXslTemplateURL
	* @return none
	* @throws none
	*/
	public void   setMailValidationXslTemplateURL(String a_strMailValidationXslTemplateURL){
		m_strMailValidationXslTemplateURL = a_strMailValidationXslTemplateURL ;
	}
	
	
	/**
	* R�cup�ration du MailSuppressionXslTemplateURL
	* @param none
	* @return String MailSuppressionXslTemplateURL
	* @throws none
	*/
	public String getMailSuppressionXslTemplateURL(){
		return m_strMailSuppressionXslTemplateURL;
	}
	
	/**
	* Modification du MailSuppressionXslTemplateURL
	* @param String MailSuppressionXslTemplateURL
	* @return none
	* @throws none
	*/
	public void   setMailSuppressionXslTemplateURL(String a_strMailSuppressionXslTemplateURL){
		m_strMailSuppressionXslTemplateURL = a_strMailSuppressionXslTemplateURL ;
	}
	
		
	/**
	* R�cup�ration du SiteHttpServerPort
	* @param none
	* @return int m_nSiteHttpServerPort
	* @throws none
	*/
	public int getSiteHttpServerPort(){
		return m_nSiteHttpServerPort;
	}
	
	/**
	* Modification du SiteHttpServerPort
	* @param int a_nSiteHttpServerPort
	* @return none
	* @throws none
	*/
	public void setSiteHttpServerPort(int a_nSiteHttpServerPort)
	{
		m_nSiteHttpServerPort = a_nSiteHttpServerPort ;
	}
	
		
	/**
	* R�cup�ration du SiteHttpServerName
	* @param none
	* @return int m_strSiteHttpServerName
	* @throws none
	*/
	public String getSiteHttpServerName(){
		return m_strSiteHttpServerName;
	}
	
	/**
	* Modification du SiteHttpServerName
	* @param int a_strSiteHttpServerName
	* @return none
	* @throws none
	*/
	public void setSiteHttpServerName(String a_strSiteHttpServerName)
	{
		m_strSiteHttpServerName = a_strSiteHttpServerName ;
	}



	/**
	* R�cup�ration du type de d�sabonnement
	* @param none
	* @return String m_strTypeDesabonnement
	* @throws none
	*/
	public String getTypeDesabonnement() {
		return m_strTypeDesabonnement;
	}
	
	/**
	* Modification du TypeDesabonnement
	* @param String a_strTypeDesabonnement
	* @return none
	* @throws none
	*/
	public void setTypeDesabonnement(String a_strTypeDesabonnement)
	{
		m_strTypeDesabonnement = a_strTypeDesabonnement ;
	}


	/**
	* R�cup�ration du EmailFromNewsletter
	* @param none
	* @return String EmailFromNewsletter
	* @throws none
	*/
	public String getEmailFromNewsletter(){
		return m_strEmailFromNewsletter;
	}
	
	
	/**
	* Modification du EmailFromNewsletter
	* @param String EmailFromNewsletter
	* @return none
	* @throws none
	*/
	public void setEmailFromNewsletter(String a_strEmailFromNewsletter){
		m_strEmailFromNewsletter = a_strEmailFromNewsletter ;
	}
		
	/**
	* R�cup�ration de la date de cr�ation de l'abonnement
	* @param none
	* @return long DateCreation
	* @throws none
	*/
	public long getDateCreation(){
		return m_lDateCreation;
	}
	
	/**
	* Modification de la date de cr�ation de l'abonnement
	* @param long - DateCreation
	* @return none
	* @throws none
	*/
	public void setDateCreation(long a_lDateCreation){
		m_lDateCreation = a_lDateCreation;
	}
	

	/**
	* R�cup�ration du userId
	* @param none
	* @return String userId
	* @throws none
	*/
	public String getUserId(){
		return m_strUserId;
	}
	
	/**
	* Modification du userId
	* @param String userId
	* @return none
	* @throws none
	*/
	public void setUserId(String a_strUserId){
		m_strUserId = a_strUserId ;
	}
	
	
	/**
	* R�cup�ration du profil
	* @param none
	* @return Profil user
	* @throws none
	*/
	public Profile getProfile() {
	  	return m_Profile;
	}
	
	
	/**
	* Modification du profil
	* @param Profil user
	* @return none
	* @throws none
	*/
	public void setProfile(Profile a_Profile) {
		m_Profile = a_Profile;
	}

	
	
	/**
	* R�cup�ration du SubmitSuccessURL
	* @param none
	* @return String SubmitSuccessURL
	* @throws none
	*/
	public String getSubmitSuccessURL(){
		return m_strSubmitSuccessURL;
	}
	
	/**
	* Modification du SubmitSuccessURL
	* @param String SubmitSuccessURL
	* @return none
	* @throws none
	*/
	public void setSubmitSuccessURL(String a_strSubmitSuccessURL)
	{
		m_strSubmitSuccessURL = a_strSubmitSuccessURL ;
	}
		

	/**
	* R�cup�ration du SubmitErrorURL
	* @param none
	* @return String SubmitErrorURL
	* @throws none
	*/
	public String getSubmitErrorURL(){
		return m_strSubmitErrorURL;
	}
	
	/**
	* Modification du SubmitSuccessURL
	* @param String SubmitSuccessURL
	* @return none
	* @throws none
	*/
	public void setSubmitErrorURL(String a_strSubmitErrorURL)
	{
		m_strSubmitErrorURL = a_strSubmitErrorURL ;
	}
		


	public String getDesabonnementCastoSuccessURL(){
		return m_strDesabonnementCastoSuccessURL;
	}
	
	public void setDesabonnementCastoSuccessURL(String a_strDesabonnementCastoSuccessURL)
	{
		m_strDesabonnementCastoSuccessURL = a_strDesabonnementCastoSuccessURL ;
	}


	public String getDesabonnementPartenaireSuccessURL(){
		return m_strDesabonnementPartenaireSuccessURL;
	}
	
	public void setDesabonnementPartenaireSuccessURL(String a_strDesabonnementPartenaireSuccessURL)
	{
		m_strDesabonnementPartenaireSuccessURL = a_strDesabonnementPartenaireSuccessURL ;
	}




	public String getCanalInscritpion(){
		return m_strCanalInscritpion;
	}
	
	public void setCanalInscritpion(String a_strCanalInscritpion)
	{
		m_strCanalInscritpion = a_strCanalInscritpion ;
	}


	public String getCanalDesInscritpion(){
		return m_strCanalDesInscritpion;
	}
	public void setCanalDesInscritpion(String a_strCanalDesInscritpion)
	{
		m_strCanalDesInscritpion = a_strCanalDesInscritpion ;
	}

	public String getOldEmail(){
		return m_strOldEmail;
	}
	
	public void setOldEmail(String a_strOldEmail)
	{
		m_strOldEmail = a_strOldEmail ;
	}


	
	/**
	* V�rifications d'erreur avant mise � jour de l'abonnement
	* @param DynamoHttpServletRequest  a_Request
	* @param DynamoHttpServletResponse a_Response
	* @return boolean erreur oui ou non
	* @throws IOException,ServletException
	*/
	public boolean handleUpdate(	DynamoHttpServletRequest  a_Request,
	   								DynamoHttpServletResponse a_Response)
	throws IOException,ServletException {

		//trace.logOpen(this,".handleUpdate");
		boolean l_bOk=true;
		try
		{	
			String l_strNom = (String) getValueProperty("nom");
			String l_strPrenom = (String) getValueProperty("prenom");
			String l_strEmail = (String) this.getRepositoryId();
			l_bOk &= CheckingTools.checkString(l_strNom,"NomMissing",(GenericFormHandler)this);
			l_bOk &= CheckingTools.checkString(l_strPrenom,"PrenomMissing",(GenericFormHandler)this);
			l_bOk &= CheckingTools.checkEmail(l_strEmail,"emailSyntaxError",(GenericFormHandler)this);
			
			if(l_bOk){
				//******  Log d'administration  *****
				BeanCreateLogAdmin.createLog(a_Request,a_Response,"Newsletter","Modification","Edition de l'abonnement : "+l_strEmail);
				l_bOk=super.handleUpdate(a_Request, a_Response);
				}
		}catch(Exception e){
			//trace.logError(this,e,".handleUpdate Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".handleUpdate");
		}
		return true;
		
	}
	
	
	/**
	* V�rifications d'erreur avant mise � jour de l'abonnement
	* @param DynamoHttpServletRequest  a_Request
	* @param DynamoHttpServletResponse a_Response
	* @return boolean erreur oui ou non
	* @throws IOException,ServletException
	*/
	public boolean handleUpdateAbonnement(	DynamoHttpServletRequest  a_Request,
	   								DynamoHttpServletResponse a_Response)
	throws IOException,ServletException {

		//trace.logOpen(this,".handleUpdateAbonnement");
		boolean l_bOk=true;
		try
		{	
			String l_strEmail = (String) this.getRepositoryId();
			l_bOk &= CheckingTools.checkEmail(l_strEmail,"emailSyntaxError",(GenericFormHandler)this);
			l_bOk=exist_valide(l_strEmail);
			if(l_bOk) 
			{
				String l_strQuestionDes1 = (String) getValueProperty("QuestionDes1");
				String l_strQuestionDes2 = (String) getValueProperty("QuestionDes2");
				String l_strQuestionDes3 = (String) getValueProperty("QuestionDes3");
				String l_strQuestionDes4 = (String) getValueProperty("QuestionDes4");
				String l_strQuestionDes5 = (String) getValueProperty("QuestionDes5");
				String l_strRemarqueDes = (String) getValueProperty("RemarqueDes");
				Boolean	l_bAccesPartenairesCasto = (Boolean) getValueProperty("accesPartenairesCasto");
				MutableRepository		l_MutableRepository = (MutableRepository) getRepository();
				MutableRepositoryItem	l_MutableAbonnement		= l_MutableRepository.getItemForUpdate(l_strEmail,"abonnementNewsletter");
				Date l_Today = new Date();
				l_MutableAbonnement.setPropertyValue("dateDesincrption",l_Today);
				l_MutableAbonnement.setPropertyValue("dateDerniereModification", l_Today);
				l_MutableAbonnement.setPropertyValue("canalDesinscription", getCanalDesInscritpion());
					
				if (getTypeDesabonnement().equals("partenaire"))
				{
					setCreateSuccessURL(getDesabonnementPartenaireSuccessURL());
					l_MutableAbonnement.setPropertyValue("accesPartenairesCasto", l_bAccesPartenairesCasto);
				}
				if (getTypeDesabonnement().equals("castorama"))
				{
					setCreateSuccessURL(getDesabonnementCastoSuccessURL());
					l_MutableAbonnement.setPropertyValue("isValid",new Boolean("false"));
					l_MutableAbonnement.setPropertyValue("accesPartenairesCasto", new Boolean("false"));
				}
				if ( l_strQuestionDes1!=null )
                    {
                    l_MutableAbonnement.setPropertyValue("QuestionDes1",l_strQuestionDes1);
                    }
				if ( l_strQuestionDes2!=null )
                    {
                    l_MutableAbonnement.setPropertyValue("QuestionDes2",l_strQuestionDes2);
                    }
				if ( l_strQuestionDes3!=null )
                    {
                    l_MutableAbonnement.setPropertyValue("QuestionDes3",l_strQuestionDes3);
                    }
				if ( l_strQuestionDes4!=null )
                {
                    l_MutableAbonnement.setPropertyValue("QuestionDes4",l_strQuestionDes4);
                }
				if ( l_strQuestionDes5!=null )
                    {
                    l_MutableAbonnement.setPropertyValue("QuestionDes5",l_strQuestionDes5);
                    }
				if ( l_strRemarqueDes!=null )
				{
					if (l_strRemarqueDes.length()>499) 
                        {
                        l_strRemarqueDes = l_strRemarqueDes.substring(0,499);
                        }
					l_MutableAbonnement.setPropertyValue("RemarqueDes",l_strRemarqueDes);
				}
				l_MutableRepository.updateItem(l_MutableAbonnement);	
				a_Response.sendLocalRedirect(getCreateSuccessURL(), a_Request);
			}
			else
			{
				addFormException(new DropletException("abonnementSuppNotExist","abonnementSuppNotExist"));
			}
		}catch(RepositoryException e){
				l_bOk=false;
				//trace.logError(this,e,".handleUpdateAbonnement RepositoryException : "+e.toString());			
			
		}catch(Exception e){
			//trace.logError(this,e,".handleUpdateAbonnement Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".handleUpdateAbonnement");
		}
		return true;
		
	}
	
	
	
	/**
	* V�rifications d'erreur avant cr�ation de l'abonnement
	* Envoi de mail de confirmation.
	* @param DynamoHttpServletRequest  a_Request
	* @param DynamoHttpServletResponse a_Response
	* @return boolean erreur oui ou non
	* @throws IOException,ServletException
	*/
	public boolean handleCreate(	DynamoHttpServletRequest  a_Request,
	   								DynamoHttpServletResponse a_Response)
	throws IOException,ServletException {

		//trace.logOpen(this,".handleCreate");
		boolean l_bOk=true;
		try
		{	
			checkAccesPartenaireCasto();
			String l_strNom = (String) getValueProperty("nom");
			String l_strPrenom = (String) getValueProperty("prenom");
			String l_strEmail = (String) getValueProperty("email");
			String[] l_strThemesFavoris = (String[]) this.getValue().get("THEMESFAVORIS");
				
			if (exist(l_strEmail))
			{
				if (exist_valide(l_strEmail))
				{
					addFormException(new DropletException("abonnementAlreadyExist","abonnementAlreadyExist"));
				}
				else
				{	
					l_bOk=handleReInscrption(a_Request, a_Response,l_strEmail);
					if(l_bOk){
						a_Response.sendLocalRedirect(getCreateSuccessURL(), a_Request);
						}
					else{
						//trace.logError(this,".handleCreate erreur de reinscription � la newsletter");	
					}

					if(exist(l_strEmail))
                        {
                        handleSubmit(a_Request,a_Response);
                        }
				}
			}
			else
			{
				l_bOk &= CheckingTools.checkString(l_strNom,"NomMissing",(GenericFormHandler)this);
				l_bOk &= CheckingTools.checkString(l_strPrenom,"PrenomMissing",(GenericFormHandler)this);
				l_bOk &= CheckingTools.checkEmail(l_strEmail,"emailSyntaxError",(GenericFormHandler)this);
			
				if(l_bOk){
					// date de creation de l'abonnement
					Date l_Today = new Date();
					this.getValue().put("DATECREATION",l_Today);
					this.getValue().put("DATEDERNIEREMODIFICATION",l_Today);
					
					// creation de l'abonnement
					l_bOk = super.handleCreate(a_Request,a_Response);
					if(l_bOk){
						// reference de l'abonnement dans le profil utilisateur
						if ( m_strUserId!=null)
						{
							MutableRepository		l_MutableRepository = (MutableRepository) m_Profile.getRepository();
							MutableRepositoryItem	l_MutableUser		= l_MutableRepository.getItemForUpdate(m_strUserId,"user");
							if (l_MutableUser!=null)
							{
								l_MutableUser.setPropertyValue("abonnementNewsletter",getRepositoryItem());
								l_MutableRepository.updateItem(l_MutableUser);
							}
						}
					}
		
					
					// envoi du mail de confimation
					if(exist(l_strEmail)) 
                        {
                        handleSubmit(a_Request,a_Response);
                        }
				}
				   
			}
		}catch(Exception e){
			//trace.logError(this,e,".handleCreate Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".handleCreate");
		}
		return true;
		
	}
	

	/**
	* V�rifications d'erreur avant cr�ation de l'abonnement
	* Envoi de mail de confirmation.
	* @param DynamoHttpServletRequest  a_Request
	* @param DynamoHttpServletResponse a_Response
	* @return boolean erreur oui ou non
	* @throws IOException,ServletException
	*/
	public boolean handleModificationAbonnement(	DynamoHttpServletRequest  a_Request,
	   								DynamoHttpServletResponse a_Response)
	throws IOException,ServletException {

		//trace.logOpen(this,".handleModificationAbonnement");
		boolean l_bOk=true;
		try
		{	
			String l_strNom = (String) getValueProperty("nom");
			String l_strPrenom = (String) getValueProperty("prenom");
			String l_strEmail = (String) getValueProperty("email");
			String[] l_strThemesFavoris = (String[]) this.getValue().get("THEMESFAVORIS");
				
			if (exist(l_strEmail))
			{
				l_bOk=handleReInscrption(a_Request, a_Response,l_strEmail);
				if(l_bOk){
					a_Response.sendLocalRedirect(getCreateSuccessURL(), a_Request);
				}
				else{
					//trace.logError(this,".handleModificationAbonnement erreur de reinscription � la newsletter");	
				}
			}
			else
			{
				addFormException(new DropletException("abonnementNotExist","abonnementNotExist"));
			}
				   
		
		}catch(Exception e){
			//trace.logError(this,e,".handleCreate Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".handleModificationAbonnement");
		}
		return true;
		
	}
	

	/**
	* V�rifications d'erreur avant cr�ation de l'abonnement
	* Envoi de mail de confirmation.
	* @param DynamoHttpServletRequest  a_Request
	* @param DynamoHttpServletResponse a_Response
	* @return boolean erreur oui ou non
	* @throws IOException,ServletException
	*/
	public boolean handleModificationMailAbonnement(	DynamoHttpServletRequest  a_Request,
	   								DynamoHttpServletResponse a_Response)
	throws IOException,ServletException {

		//trace.logOpen(this,".handleModificationMailAbonnement");
		boolean l_bOk=true;
		try
		{	
			String l_strEmail = (String) getValueProperty("email");
			this.getValue().put("THEMESFAVORIS", getAllTHemesNewsletter());
						
			if (!( l_bOk && exist_valide(getOldEmail()) ))
			{
				l_bOk=false;
				addFormException(new DropletException("abonnementnotExist","abonnementnotExist"));
				a_Response.sendLocalRedirect(getCreateErrorURL(), a_Request);
			}		
			
			if (l_bOk) {
				if (exist(l_strEmail))
				{
					if (exist_valide(l_strEmail))
					{
						addFormException(new DropletException("abonnementExist","abonnementExist"));
						l_bOk=false;
						a_Response.sendLocalRedirect(getCreateErrorURL(), a_Request);
					}
					else
					{
					//maj abonnement	
						l_bOk=handleReInscrption(a_Request,a_Response,l_strEmail);
					}		
				}
				else
				{
					//creation abonnement
					l_bOk=handleCreateInscrption(a_Request,a_Response,l_strEmail);
				}
			}
							
			if(l_bOk){
				MutableRepository		l_MutableRepository = (MutableRepository) getRepository();
				MutableRepositoryItem	l_MutableAbonnement		= l_MutableRepository.getItemForUpdate(getOldEmail(),"abonnementNewsletter");
				Date l_Today = new Date();
				Boolean	l_bAccesPartenairesCasto	= (Boolean) getValueProperty("accesPartenairesCasto");
				l_MutableAbonnement.setPropertyValue("dateDesincrption",l_Today);
				l_MutableAbonnement.setPropertyValue("dateDerniereModification", l_Today);
				l_MutableAbonnement.setPropertyValue("canalDesinscription", "modifemail");
				l_MutableAbonnement.setPropertyValue("accesPartenairesCasto", l_bAccesPartenairesCasto);
				l_MutableAbonnement.setPropertyValue("isValid",new Boolean("false"));
				a_Response.sendLocalRedirect(getCreateSuccessURL(), a_Request);
			}
				
					   
		
		}catch(Exception e){
			l_bOk=false;
			//trace.logError(this,e,".handleModificationMailAbonnement Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".handleModificationMailAbonnement");
		}
		return l_bOk;
		
	}







	/**
	* V�rifications d'erreur avant cr�ation de l'abonnement
	* Envoi de mail de confirmation.
	* @param DynamoHttpServletRequest  a_Request
	* @param DynamoHttpServletResponse a_Response
	* @return boolean erreur oui ou non
	* @throws IOException,ServletException
	*/
	public boolean handleCreateOrigine(	DynamoHttpServletRequest  a_Request,
	   								DynamoHttpServletResponse a_Response)
	throws IOException,ServletException {

		//trace.logOpen(this,".handleCreateOrigine");
		boolean l_bOk=true;
		try
		{	

			String l_strNom = (String) this.getValue().get("NOM");
			String l_strPrenom = (String) this.getValue().get("PRENOM");
			String l_strEmail = (String) this.getValue().get("EMAIL");
			String l_strCivilite = (String) this.getValue().get("CIVILITE");
			String l_strVille = (String) this.getValue().get("VILLE");
			String l_strOrigine = (String) this.getValue().get("ORIGINE");
			String l_strCodePostal = (String) this.getValue().get("CODEPOSTAL");
			Boolean l_bAtout = (Boolean) this.getValue().get("CARTEATOUT");
			Boolean l_bAccesPartenairesCasto = (Boolean) getValueProperty("accesPartenairesCasto");
			Boolean l_bIsValid = (Boolean) getValueProperty("isValid");
			String[] l_strThemesFavoris = (String[]) this.getValue().get("THEMESFAVORIS");
			MutableRepository		l_MutableRepository = (MutableRepository) getRepository();
			if (exist(l_strEmail))
			{
				MutableRepositoryItem	l_MutableAbonnement		= l_MutableRepository.getItemForUpdate(l_strEmail,"abonnementNewsletter");
								
				if (l_strNom!=null && !l_strNom.equals(""))  
                    {
                    l_MutableAbonnement.setPropertyValue("nom", l_strNom);
                    }
				if (l_strPrenom!=null && !l_strPrenom.equals(""))    
                {
                    l_MutableAbonnement.setPropertyValue("prenom", l_strPrenom);
                }
				if (l_strVille!=null && !l_strVille.equals(""))  
                    {        
                    l_MutableAbonnement.setPropertyValue("ville", l_strVille);
                    }
				if (l_strCivilite!=null && !l_strCivilite.equals(""))    
                    {
                    l_MutableAbonnement.setPropertyValue("civilite", l_strCivilite);
                    }
				if (l_strCodePostal!=null && !l_strCodePostal.equals(""))    
                    {
                    l_MutableAbonnement.setPropertyValue("codePostal",l_strCodePostal );
                    }
				if (l_strOrigine!=null && !l_strOrigine.equals("")) 
                    {
                    l_MutableAbonnement.setPropertyValue("origine",l_strOrigine );                                  
                    }
								
				Date l_Today = new Date();
				if(l_bIsValid!=null && l_bIsValid.booleanValue())	
                    {
                    l_MutableAbonnement.setPropertyValue("dateDesincrption",null);
                    }
				l_MutableAbonnement.setPropertyValue("dateDerniereModification",l_Today);
				l_MutableAbonnement.setPropertyValue("isValid",l_bIsValid);
				l_MutableAbonnement.setPropertyValue("accesPartenairesCasto", l_bAccesPartenairesCasto);
				l_MutableRepository.updateItem(l_MutableAbonnement);	
				
				if(exist(l_strEmail)) 
                    {
                    handleSubmit(a_Request,a_Response);
                    }

			}
			else
			{
				
				
				MutableRepositoryItem	l_MutableAbonnement		= l_MutableRepository.createItem(l_strEmail,"abonnementNewsletter");
				Date l_Today = new Date();
				l_MutableAbonnement.setPropertyValue("dateCreation",l_Today);
				l_MutableAbonnement.setPropertyValue("dateDerniereModification",l_Today);
				l_MutableAbonnement.setPropertyValue("isValid", l_bIsValid);
				l_MutableAbonnement.setPropertyValue("accesPartenairesCasto", l_bAccesPartenairesCasto);


				if (l_strNom!=null && !l_strNom.equals(""))	
                    {
                    l_MutableAbonnement.setPropertyValue("nom", l_strNom);
                    }
				if (l_strPrenom!=null && !l_strPrenom.equals(""))     
                    {
                    l_MutableAbonnement.setPropertyValue("prenom", l_strPrenom);
                    }
				if (l_strCivilite!=null && !l_strCivilite.equals(""))    	
                    {
                    l_MutableAbonnement.setPropertyValue("civilite", l_strCivilite);
                    }
				if (l_strVille!=null && !l_strVille.equals(""))  	
                    {
                    l_MutableAbonnement.setPropertyValue("ville", l_strVille);
                    }
				if (l_strCodePostal!=null && !l_strCodePostal.equals(""))    
                    {
                    l_MutableAbonnement.setPropertyValue("codePostal",l_strCodePostal );
                    }
				if (l_strOrigine!=null && !l_strOrigine.equals(""))  
                    {
                    l_MutableAbonnement.setPropertyValue("origine",l_strOrigine );                                  
                    }
				
				
				if ( (l_bAtout!=null) && (l_bAtout.booleanValue()) )
					{
                    l_MutableAbonnement.setPropertyValue("carteAtout",new Boolean("true") );
                    }
				
				if ( (getCanalInscritpion()!=null ) && (!getCanalInscritpion().equals(""))  )
					{
                    l_MutableAbonnement.setPropertyValue("origine", getCanalInscritpion());
                    }
				
				if  (l_strThemesFavoris!=null) 
					{
                    l_MutableAbonnement.setPropertyValue("themesFavoris",l_strThemesFavoris );
                    }
				
				l_MutableRepository.addItem(l_MutableAbonnement);
			
											
				if(l_bOk){
				// reference de l'abonnement dans le profil utilisateur
					if ( m_strUserId!=null)
					{
						MutableRepository		l_MutableRepository2 = (MutableRepository) m_Profile.getRepository();
						if ( l_MutableRepository2!=null)
						{
							MutableRepositoryItem	l_MutableUser		= l_MutableRepository2.getItemForUpdate(m_strUserId,"user");
							if ( l_MutableUser!=null)
							{
								l_MutableUser.setPropertyValue("abonnementNewsletter",getRepositoryItem());
								l_MutableRepository2.updateItem(l_MutableUser);
							}
						}
					}
				}
			// envoi du mail de confimation
			if(exist(l_strEmail))
                {
                handleSubmit(a_Request,a_Response);
                }
			}
			
		}catch(Exception e){
			//trace.logError(this,e,".handleCreateOrigine Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".handleCreateOrigine");
		}
		return true;
		
	}
	




	
	/**
	* V�rifications avant cr�ation de l'abonnement
	* @param DynamoHttpServletRequest  a_Request
	* @param DynamoHttpServletResponse a_Response
	* @param String a_strNom
	* @param String a_strPrenom
	* @param String a_strEmail
	* @return boolean erreur oui ou non
	* @throws IOException,ServletException
	*/
	public boolean createDefault(	DynamoHttpServletRequest  a_Request,
	   								DynamoHttpServletResponse a_Response,
									String a_strNom,
									String a_strPrenom,
									String a_strEmail)
	throws IOException,ServletException {
		//trace.logOpen(this,".createDefault");
		boolean l_bOk=true;
		try
		{	
			checkAccesPartenaireCasto();
			this.getValue().put("PRENOM", a_strPrenom);
			this.getValue().put("NOM", a_strNom);
			this.getValue().put("EMAIL", a_strEmail);
			this.getValue().put("THEMESFAVORIS", getAllTHemesNewsletter());
			Date l_Today = new Date();
			this.getValue().put("DATECREATION",l_Today);
			this.getValue().put("DATEDERNIEREMODIFICATION",l_Today);
			
			l_bOk &=this.handleCreate(a_Request,a_Response);
		}catch(SQLException e){
 			l_bOk=false;
			//trace.logError(this,e,".createDefault SQLException : "+e.toString());
		}catch(Exception e){
 			l_bOk=false;
			//trace.logError(this,e,".createDefault Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".createDefault");
		}
		return l_bOk;
	}
	
	
	/**
	* Si la propriet� accesPartenaireCasto est valid�e dans le formulaire, le champ valid passe � true
	* @param	none
	* @return	none
	* @throws	none
	*/
	public void checkAccesPartenaireCasto(){
		Boolean l_AccesPartenaireCasto = (Boolean) getValueProperty("accesPartenairesCasto");
		if(l_AccesPartenaireCasto!=null){
			if(l_AccesPartenaireCasto.booleanValue()){
				setValueProperty("isValid", Boolean.TRUE);
			}
		}else{
			setValueProperty("accesPartenairesCasto", Boolean.FALSE);
		}
	}

	/**
	* V�rifications avant cr�ation de l'abonnement
	* @param DynamoHttpServletRequest  a_Request
	* @param DynamoHttpServletResponse a_Response
	* @param String a_strEmail
	* @param String a_strOrigine
	* @return boolean erreur oui ou non
	* @throws IOException,ServletException
	*/
	public boolean createAtoutOrigine(	DynamoHttpServletRequest  a_Request,
	   								DynamoHttpServletResponse a_Response,
									String a_strEmail,String a_strOrigine)
	throws IOException,ServletException {
		//trace.logOpen(this,".createAtoutOrigine");
		boolean l_bOk=true;
		try
		{	
			checkAccesPartenaireCasto();
			this.getValue().put("EMAIL", a_strEmail);
			this.getValue().put("ORIGINE", a_strOrigine);
			this.getValue().put("ISVALID", new Boolean("true"));
			
			this.getValue().put("CARTEATOUT", new Boolean("true"));
			this.getValue().put("THEMESFAVORIS", getAllTHemesNewsletter());

			Date l_Today = new Date();
			this.getValue().put("DATECREATION",l_Today);
			this.getValue().put("DATEDERNIEREMODIFICATION",l_Today);
			l_bOk &=this.handleCreateOrigine(a_Request,a_Response);
		}catch(Exception e){
 			l_bOk=false;
			//trace.logError(this,e,".createAtoutOrigine Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".createAtoutOrigine");
		}
		return l_bOk;
	}
	



	/**
	* V�rifications avant cr�ation de l'abonnement
	* @param DynamoHttpServletRequest  a_Request
	* @param DynamoHttpServletResponse a_Response
	* @param String a_strNom
	* @param String a_strPrenom
	* @param String a_strEmail
	* @param String a_strOrigine
	** @param String a_strCivilite
	* @return boolean erreur oui ou non
	* @throws IOException,ServletException
	*/
	public boolean createDefaultOrigine(	DynamoHttpServletRequest  a_Request,
	   								DynamoHttpServletResponse a_Response,
									String a_strNom,String a_strPrenom,
									String a_strEmail,String a_strOrigine,String a_strCivilite)
	throws IOException,ServletException {
		//trace.logOpen(this,".createDefaultOrigine");
		boolean l_bOk=true;
		try
		{	
			checkAccesPartenaireCasto();
			this.getValue().put("PRENOM", a_strPrenom);
			this.getValue().put("NOM", a_strNom);
			this.getValue().put("EMAIL", a_strEmail);
			this.getValue().put("ORIGINE", a_strOrigine);
			
			if ( a_strCivilite.equals("Monsieur") || a_strCivilite.equals("Madame") || a_strCivilite.equals("Mlle") )
			{
                this.getValue().put("CIVILITE", a_strCivilite);
            }
			
			this.getValue().put("THEMESFAVORIS", getAllTHemesNewsletter());
			Date l_Today = new Date();
			this.getValue().put("DATECREATION",l_Today);
			this.getValue().put("DATEDERNIEREMODIFICATION",l_Today);
			l_bOk &=this.handleCreateOrigine(a_Request,a_Response);
		}catch(SQLException e){
 			l_bOk=false;
			//trace.logError(this,e,".createDefaultOrigine SQLException : "+e.toString());
		}catch(Exception e){
 			l_bOk=false;
			//trace.logError(this,e,".createDefaultOrigine Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".createDefaultOrigine");
		}
		return l_bOk;
	}
	


	/**
	* V�rifications avant cr�ation de l'abonnement
	* @param DynamoHttpServletRequest  a_Request
	* @param DynamoHttpServletResponse a_Response
	* @param String a_strNom
	* @param String a_strPrenom
	* @param String a_strEmail
	* @param String a_strVille
	* @param String a_strOrigine
	* @return boolean erreur oui ou non
	* @throws IOException,ServletException
	*/
	public boolean createDefaultOrigine(	DynamoHttpServletRequest  a_Request,
	   								DynamoHttpServletResponse a_Response,
									String a_strNom,String a_strPrenom,
									String a_strEmail,String a_strVille,String a_strOrigine,String a_strCivilite)
	throws IOException,ServletException {
		//trace.logOpen(this,".createDefaultOrigine");
		boolean l_bOk=true;
		try
		{	
			checkAccesPartenaireCasto();
			this.getValue().put("PRENOM", a_strPrenom);
			this.getValue().put("NOM", a_strNom);
			this.getValue().put("EMAIL", a_strEmail);
			this.getValue().put("VILLE", a_strVille);
			this.getValue().put("ORIGINE", a_strOrigine);
			if ( a_strCivilite.equals("Monsieur") || a_strCivilite.equals("Madame") || a_strCivilite.equals("Mlle") )
			{
                this.getValue().put("CIVILITE", a_strCivilite);
            }
			this.getValue().put("THEMESFAVORIS", getAllTHemesNewsletter());
			Date l_Today = new Date();
			this.getValue().put("DATECREATION",l_Today);
			this.getValue().put("DATEDERNIEREMODIFICATION",l_Today);
			l_bOk &=this.handleCreateOrigine(a_Request,a_Response);
		}catch(SQLException e){
 			l_bOk=false;
			//trace.logError(this,e,".createDefaultOrigine SQLException : "+e.toString());
		}catch(Exception e){
 			l_bOk=false;
			//trace.logError(this,e,".createDefaultOrigine Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".createDefaultOrigine");
		}
		return l_bOk;
	}
	

	/**
	* V�rifications avant cr�ation de l'abonnement
	* @param DynamoHttpServletRequest  a_Request
	* @param DynamoHttpServletResponse a_Response
	* @param String a_strNom
	* @param String a_strPrenom
	* @param String a_strEmail
	* @param String a_strVille
	* @param String a_strCodePostal
	* @param String a_strOrigine
	* @return boolean erreur oui ou non
	* @throws IOException,ServletException
	*/
	public boolean createDefaultOrigine(	DynamoHttpServletRequest  a_Request,
	   								DynamoHttpServletResponse a_Response,
									String a_strNom,String a_strPrenom,
									String a_strEmail,String a_strVille,
									String a_strCodePostal,String a_strOrigine ,String a_strCivilite)
	throws IOException,ServletException {
		//trace.logOpen(this,".createDefaultOrigine");
		boolean l_bOk=true;
		try
		{	
			checkAccesPartenaireCasto();
			this.getValue().put("PRENOM", a_strPrenom);
			this.getValue().put("NOM", a_strNom);
			this.getValue().put("EMAIL", a_strEmail);
			this.getValue().put("VILLE", a_strVille);
			this.getValue().put("CODEPOSTAL", a_strCodePostal);
			this.getValue().put("ORIGINE", a_strOrigine);
			if ( a_strCivilite.equals("Monsieur") || a_strCivilite.equals("Madame") || a_strCivilite.equals("Mlle") )
			{
                this.getValue().put("CIVILITE", a_strCivilite);
            }
			this.getValue().put("THEMESFAVORIS", getAllTHemesNewsletter());
			Date l_Today = new Date();
			this.getValue().put("DATECREATION",l_Today);
			this.getValue().put("DATEDERNIEREMODIFICATION",l_Today);
			l_bOk &=this.handleCreateOrigine(a_Request,a_Response);
		}catch(SQLException e){
 			l_bOk=false;
			//trace.logError(this,e,".createDefaultOrigine SQLException : "+e.toString());
		}catch(Exception e){
 			l_bOk=false;
			//trace.logError(this,e,".createDefaultOrigine Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".createDefaultOrigine");
		}
		return l_bOk;
	}
	
	
	/**
	* Cr�ation de l'abonnement avec tous les th�mes choisis
	* @param DynamoHttpServletRequest  a_Request
	* @param DynamoHttpServletResponse a_Response
	* @return boolean erreur oui ou non
	* @throws IOException,ServletException
	*/
	public boolean handleCreateDefault(	DynamoHttpServletRequest  a_Request,
	   								DynamoHttpServletResponse a_Response)
	throws IOException,ServletException {
		//trace.logOpen(this,".handleCreateDefault");
		boolean l_bOk=true;
		try
		{	
			this.getValue().put("THEMESFAVORIS", getAllTHemesNewsletter());
			l_bOk &=this.handleCreate(a_Request,a_Response);
		}catch(SQLException e){
			//trace.logError(this,e,".handleCreateDefault SQLException : "+e.toString());
		}catch(Exception e){
 			l_bOk=false;
			//trace.logError(this,e,".handleCreateDefault Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".handleCreateDefault");
		}
		return true;		
	}
	
	
	/**
	* R�cup�ration de tous les th�mes de la Newsletter
	* @param none
	* @return String[] - tous les themes
	* @throws SQLException
	*/
	public String[] getAllTHemesNewsletter() throws SQLException{
		
		//trace.logOpen(this,".getAllTHemesNewsletter");
		String[] l_Liste = null;
		try{
			RepositoryItemDescriptor	l_Item		= this.getRepository().getItemDescriptor("themeNewsletter");
			RepositoryView				l_ItemView	= l_Item.getRepositoryView();
			QueryBuilder				l_Builder	= l_ItemView.getQueryBuilder();
		
			QueryExpression		l_QueryProperty		= l_Builder.createPropertyQueryExpression("id");
			QueryExpression		l_QueryConstant		= l_Builder.createConstantQueryExpression(" ");
			Query				l_Query				= l_Builder.createComparisonQuery(l_QueryProperty, l_QueryConstant, QueryBuilder.NOT_EQUALS);
			
			RepositoryItem[]	l_Items				= l_ItemView.executeQuery(l_Query);
		
			l_Liste = new String[l_Items.length];
			for(int i=0;i<l_Items.length;i++){
				 l_Liste[i] = (String) l_Items[i].getPropertyValue("id");
			}
		}catch(RepositoryException e){
			//trace.logError(this,e,".getAllTHemesNewsletter RepositoryException : "+e.toString());
		}catch(Exception e){
 			//trace.logError(this,e,".getAllTHemesNewsletter Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".getAllTHemesNewsletter");
		}
		return l_Liste;
	}
	
	
	
	/**
	* V�rifications avant cr�ation de l'abonnement
	* @param DynamoHttpServletRequest  a_Request
	* @param DynamoHttpServletResponse a_Response
	* @return boolean erreur oui ou non
	* @throws IOException,ServletException
	*/
	public boolean handleValidAbonnement(	DynamoHttpServletRequest  a_Request,
	   										DynamoHttpServletResponse a_Response)
	throws IOException,ServletException {

		//trace.logOpen(this,".handleValidAbonnement");
		// validation de l'abonnement
		boolean l_bOk = true;
		try{
			l_bOk =  super.handleUpdate(a_Request,a_Response);
			if(l_bOk){
				// reference de l'abonnement dans le profil utilisateur
					MutableRepository		l_MutableRepository = (MutableRepository) m_Profile.getRepository();
					MutableRepositoryItem	l_MutableUser		= l_MutableRepository.getItemForUpdate(m_strUserId,"user");
					l_MutableUser.setPropertyValue("abonnementNewsletter",getRepositoryItem());
					l_MutableRepository.updateItem(l_MutableUser);
				a_Response.sendLocalRedirect(getCreateSuccessURL(), a_Request);
			}
		}catch(RepositoryException e){
			//trace.logError(this,e,".handleValidAbonnement RepositoryException : "+e.toString());
		}catch(Exception e){
 			//trace.logError(this,e,".handleValidAbonnement Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".handleValidAbonnement");
		}
		return true;
		
	}
		

	/**
	* Envoi de mail de confirmation d'abonnement
	* @param DynamoHttpServletRequest  a_Request
	* @param DynamoHttpServletResponse a_Response
	* @return boolean erreur oui ou non
	* @throws IOException,ServletException
	*/
	public boolean handleSubmit(	DynamoHttpServletRequest  a_Request,
	   								DynamoHttpServletResponse a_Response)
	throws IOException,ServletException {


		String l_strPrenom	= (String) this.getValue().get("PRENOM");
		String l_strNom		= (String) this.getValue().get("NOM");
		String l_strEmail	= (String) this.getValue().get("EMAIL");
		String l_strCivilte	= (String) this.getValue().get("CIVILITE");
		try{

			XmlDocument	l_XmlRepresentation = new XmlDocument();
			l_XmlRepresentation.addNode("siteHttpServerName",String.valueOf(m_strSiteHttpServerName));
			l_XmlRepresentation.addNode("siteHttpServerPort",String.valueOf(m_nSiteHttpServerPort));
			l_XmlRepresentation.addNode("profileId",m_Profile.getRepositoryId());
			l_XmlRepresentation.addNode("email",l_strEmail);
			l_XmlRepresentation.addNode("nom",l_strNom);
			l_XmlRepresentation.addNode("prenom",l_strPrenom);
			l_XmlRepresentation.addNode("civilite",l_strCivilte);

			MailTools.sendXSLMail(m_strEmailFromNewsletter,l_strEmail,"=?iso-8859-1?Q?Inscription =E0 la Newsletter Castorama.fr?=",m_strMailValidationXslTemplateURL,l_XmlRepresentation.closeDocument());

			if(getSubmitSuccessURL()!=null) 
                {
                a_Response.sendLocalRedirect(getSubmitSuccessURL(), a_Request);
                }
		}catch(Exception e){
 			//trace.logError(this,e,".handleSubmit Exception : "+e.toString());
		}
		return true;
		
	}
	
	
	
	/**
	* Suppression d'un abonnement
	* @param DynamoHttpServletRequest  a_Request
	* @param DynamoHttpServletResponse a_Response
	* @return boolean erreur oui ou non
	* @throws IOException,ServletException
	*/
	public boolean handleDeleteAbonnement(	DynamoHttpServletRequest  a_Request,
	   										DynamoHttpServletResponse a_Response)
	throws IOException,ServletException {
		

		boolean l_bOk = true;
		try{
			Date	l_DateCreation	= null;
			String	l_strEmail		= null;
			
			RepositoryItem l_Abonnement = getRepositoryItem();
			if(l_Abonnement==null) 
                {
                l_bOk=false;
                }
			if(l_bOk){
				l_DateCreation	= (Date)	l_Abonnement.getPropertyValue("dateCreation");
				l_strEmail		= (String)	l_Abonnement.getPropertyValue("email");
			}
	
			// v�rification des erreurs
			if(l_DateCreation!=null)
				{
                    if(l_DateCreation.getTime() != m_lDateCreation)                 
                    {
                    l_bOk=false;
                    }
			}else {
                l_bOk=false;
            }
			if(l_strEmail==null)
            {
                l_bOk=false;
            }
			// si la date de cr�ation pass�e en param�tre est la bonne
			if(l_bOk){
				MutableRepository		l_MutableRepository = (MutableRepository) m_Profile.getRepository();
				MutableRepositoryItem	l_MutableUser		= l_MutableRepository.getItemForUpdate(m_Profile.getRepositoryId(),"user");
				l_MutableUser.setPropertyValue("abonnementNewsletter", null);
				l_MutableRepository.updateItem(l_MutableUser);
				l_bOk=super.handleDelete(a_Request,a_Response);
				

				XmlDocument	l_XmlRepresentation = new XmlDocument();
				l_XmlRepresentation.addNode("email",l_strEmail);
	
				String l_strFrom = Configuration.getConfiguration().getMailDefaultFrom();
				MailTools.sendXSLMail(l_strEmail,m_strEmailFromNewsletter,"Desabonnement Newsletter Castorama",m_strMailSuppressionXslTemplateURL,l_XmlRepresentation.closeDocument());
				
			}else{
				addFormException(new DropletException("noAbonnementFound", "noAbonnementFound"));	
			}
		}catch(RepositoryException e){
 			//trace.logError(this,e,".handleDeleteAbonnement RepositoryException : "+e.toString());
		}catch(Exception e){
 			//trace.logError(this,e,".handleDeleteAbonnement Exception : "+e.toString());
		}
		return true;
		
	}
	
	
	/**
   * V�rification si un abonnement existe deja
   * @param String a_strEmail : email � v�rifier.
   * @return boolean erreur True/False
   * @exception none
   */
  	public boolean exist(String a_strEmail){
			
		boolean l_bOk=false;
		try{
			if(a_strEmail!=null){
				RepositoryItem l_Abonnement = this.getRepository().getItem(a_strEmail,"abonnementNewsletter");
				if(l_Abonnement!=null){
					if(m_Profile.getPropertyValue("abonnementNewsLetter")==null){
						m_Profile.setPropertyValue("abonnementNewsLetter",l_Abonnement);
					}
					l_bOk=true;
				}
			}
		}catch(RepositoryException e){
			l_bOk=true;
 			//trace.logError(this,e,".exist RepositoryException : "+e.toString());
		}catch(Exception e){
			l_bOk=true;
 			//trace.logError(this,e,".exist Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".exist");
		}
		return l_bOk;
  	}
  	
	/**
	* V�rification si un abonnement existe deja
	* et si il est valdie
	* @param String a_strEmail : email � v�rifier.
	* @return boolean erreur True/False
	* @exception none
	*/
  	public boolean exist_valide(String a_strEmail){
		//trace.logOpen(this,".exist_valide");
		boolean l_bOk=false;
		try{
			if(a_strEmail!=null){
				RepositoryItem l_Abonnement = this.getRepository().getItem(a_strEmail,"abonnementNewsletter");
				if(l_Abonnement!=null){
					if (((Boolean)l_Abonnement.getPropertyValue("isValid")).booleanValue())
						{l_bOk=true;}
					else
					{
						l_bOk=false;
					}
				}
			}
		}catch(RepositoryException e){
			l_bOk=true;
 			//trace.logError(this,e,".exist_valide RepositoryException : "+e.toString());
		}catch(Exception e){
			l_bOk=true;
 			//trace.logError(this,e,".exist_valide Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".exist_valide");
		}
		return l_bOk;
  	}
  	
  	
  	/**
	* V�rification si le profil courant est d�j� inscrit
	* @param	none
	* @return	boolean
	* @exception none
	*/
  	public boolean getEstInscrit(){
		//trace.logOpen(this,".estInscrit");
		boolean l_bEstInscrit=false;
		try{
			String l_strEmail=(String) getProfile().getPropertyValue("login");
			if(l_strEmail!=null&&l_strEmail.equals("")==false){
				l_bEstInscrit=exist_valide(l_strEmail);
		}
		}catch(Exception e){
 			//trace.logError(this,e,".estInscrit : "+e.toString());
		}finally{
			//trace.logClose(this,".estInscrit");
		}
		return l_bEstInscrit;
  	}
  	
  	/**
	* M�thode inutilis�e. N'existe que pour la notion de property "estInscrit"
	* @param	boolean
	* @return	none
	* @exception none
	*/
  	public void setEstInscrit(boolean a_bEstInscrit){
  	}
  	
  	
  	
  	/**
	* Si la propriet� accesPartenaireCasto est valid�e dans le formulaire, le champ valid passe � true
	* @param	none
	* @return	none
	* @throws	none
	*/
	public void checkReponseLoiLEN(){
		Boolean l_AccesPartenaireCasto	= (Boolean) getValueProperty("accesPartenairesCasto");
		Boolean l_IsValid				= (Boolean) getValueProperty("isValid");
		if(l_AccesPartenaireCasto!=null && l_AccesPartenaireCasto.booleanValue()){
			setValueProperty("reponseLoiLEN", "castoramaPartenaires");
		}else{
			if(l_IsValid!=null && l_IsValid.booleanValue()){
				setValueProperty("reponseLoiLEN", "castorama");
			}
		}
	}
  	
  	/**
	* V�rifications d'erreur avant cr�ation de l'abonnement
	* Envoi de mail de confirmation.
	* @param DynamoHttpServletRequest  a_Request
	* @param DynamoHttpServletResponse a_Response
	* @return boolean erreur oui ou non
	* @throws IOException,ServletException
	*/
	public boolean handleUpdateLoiLEN(	DynamoHttpServletRequest  a_Request,
	   									DynamoHttpServletResponse a_Response)
	throws IOException,ServletException {

		//trace.logOpen(this,".handleUpdateLoiLEN");
		boolean l_bOk=true;
		try
		{
			checkReponseLoiLEN();
			setValueProperty("dateReponseLoiLEN", new Date());
			l_bOk = handleModificationAbonnement(a_Request, a_Response);
			
		}catch(Exception e){
 			l_bOk=false;
			//trace.logError(this,e,".handleUpdateLoiLEN : "+e.toString());
		}finally{
			//trace.logClose(this,".handleUpdateLoiLEN");
		}
		return l_bOk;
		
	}
  	
  	/**
	* R�inscription 
	* on passe les param valide et partenaire � true
	* @param DynamoHttpServletRequest  a_Request
	* @param DynamoHttpServletResponse a_Response
	* @param String a_strEmail
	* @return boolean erreur oui ou non
	* @throws IOException,ServletException
	*/
	public boolean handleReInscrption(	DynamoHttpServletRequest  a_Request,
	   								DynamoHttpServletResponse a_Response,
									String a_strEmail)
	throws IOException,ServletException {
		//trace.logOpen(this,".handleReInscrption");
		
		
		checkAccesPartenaireCasto();
		String l_strNom = (String) getValueProperty("nom");
		String l_strPrenom = (String) getValueProperty("prenom");
		String l_strVille = (String) getValueProperty("ville");				
		
		String l_strCivilite = (String) getValueProperty("Civilite");
		String l_strformat = (String) getValueProperty("format");
		String l_strage = (String) getValueProperty("age");
		String l_strcodePostal = (String) getValueProperty("codePostal");
		String l_strproprietaire_locataire = (String) getValueProperty("proprietaire_locataire");
		String l_strnbPersonnes = (String) getValueProperty("nbPersonnes");
		String l_strorigine = (String) getValueProperty("origine");
		
		Boolean l_bmaisonCampagne=(Boolean)getValueProperty("maisonCampagne");
		Boolean l_bmaison=(Boolean)getValueProperty("maison");
		Boolean l_bjardin=(Boolean)getValueProperty("jardin");
		Boolean l_bcarteAtout=(Boolean)getValueProperty("carteAtout");
		
		String l_strMagasin = (String) getValueProperty("magasin");
		String l_strReponseLoiLEN = (String) getValueProperty("reponseLoiLEN");
		Date l_DateReponseLoiLEN = (Date) getValueProperty("dateReponseLoiLEN");
		Boolean l_bAccesPartenairesCasto = (Boolean)getValueProperty("accesPartenairesCasto");
					
		boolean l_bOk=true;
		if(a_strEmail==null)
            {
            l_bOk=false;
            }
			// si la date de cr�ation pass�e en param�tre est la bonne
		if(l_bOk){
			try
			{	
				MutableRepository		l_MutableRepository = (MutableRepository) getRepository();
				MutableRepositoryItem	l_MutableAbonnement		= l_MutableRepository.getItemForUpdate(a_strEmail,"abonnementNewsletter");
				Date l_Today = new Date();
				l_MutableAbonnement.setPropertyValue("dateDesincrption",null);
				l_MutableAbonnement.setPropertyValue("dateDerniereModification",l_Today);
				l_MutableAbonnement.setPropertyValue("isValid", new Boolean("true"));
				l_MutableAbonnement.setPropertyValue("accesPartenairesCasto", l_bAccesPartenairesCasto);
				l_MutableAbonnement.setPropertyValue("nom", l_strNom);
				l_MutableAbonnement.setPropertyValue("prenom", l_strPrenom);
				l_MutableAbonnement.setPropertyValue("ville", l_strVille);
				l_MutableAbonnement.setPropertyValue("age", l_strage);
				l_MutableAbonnement.setPropertyValue("civilite", l_strCivilite);
				l_MutableAbonnement.setPropertyValue("format", l_strformat);
				l_MutableAbonnement.setPropertyValue("codePostal",l_strcodePostal );
				l_MutableAbonnement.setPropertyValue("proprietaire_locataire",l_strproprietaire_locataire );
				l_MutableAbonnement.setPropertyValue("nbPersonnes", l_strnbPersonnes);
				l_MutableAbonnement.setPropertyValue("origine",l_strorigine );
				l_MutableAbonnement.setPropertyValue("maisonCampagne",l_bmaisonCampagne );
				l_MutableAbonnement.setPropertyValue("maison",l_bmaison );
				l_MutableAbonnement.setPropertyValue("jardin",l_bjardin );
				l_MutableAbonnement.setPropertyValue("carteAtout",l_bcarteAtout );
				if(l_strMagasin!=null)	
                    {
                    l_MutableAbonnement.setPropertyValue("magasin",l_strMagasin );
                    }
				if(l_strReponseLoiLEN!=null){
					l_MutableAbonnement.setPropertyValue("reponseLoiLEN",l_strReponseLoiLEN );
				}
				if(l_DateReponseLoiLEN!=null){
					l_MutableAbonnement.setPropertyValue("dateReponseLoiLEN",l_DateReponseLoiLEN );
				}
				//l_MutableAbonnement.setPropertyValue("origine", getCanalInscritpion());
				l_MutableRepository.updateItem(l_MutableAbonnement);
			}catch(RepositoryException e){
				l_bOk=false;
				//trace.logError(this,e,".handleReInscrption RepositoryException : "+e.toString());
			}catch(Exception e){
 			l_bOk=false;
			//trace.logError(this,e,".handleReInscrption Exception : "+e.toString());
			}finally{
			//trace.logClose(this,".handleReInscrption");
			}		
		}
		return l_bOk;
	}
	
   	
   	/**
 	* R�inscription 
	* on passe les param valide et partenaire � true
	* @param DynamoHttpServletRequest  a_Request
	* @param DynamoHttpServletResponse a_Response
	* @param String a_strEmail
	* @return boolean erreur oui ou non
	* @throws IOException,ServletException
	*/
	public boolean handleCreateInscrption(	DynamoHttpServletRequest  a_Request,
	   								DynamoHttpServletResponse a_Response,
									String a_strEmail)
	throws IOException,ServletException {
		//trace.logOpen(this,".handleReInscrption");
		
		String l_strNom = (String) getValueProperty("nom");
		String l_strPrenom = (String) getValueProperty("prenom");
		String l_strVille = (String) getValueProperty("ville");				
		
		String l_strCivilite = (String) getValueProperty("Civilite");
		String l_strformat = (String) getValueProperty("format");
		String l_strage = (String) getValueProperty("age");
		String l_strcodePostal = (String) getValueProperty("codePostal");
		String l_strproprietaire_locataire = (String) getValueProperty("proprietaire_locataire");
		String l_strnbPersonnes = (String) getValueProperty("nbPersonnes");
		String l_strorigine = (String) getValueProperty("origine");
		
		Boolean l_bmaisonCampagne=(Boolean)getValueProperty("maisonCampagne");
		Boolean l_bmaison=(Boolean)getValueProperty("maison");
		Boolean l_bjardin=(Boolean)getValueProperty("jardin");
		Boolean l_bcarteAtout=(Boolean)getValueProperty("carteAtout");
		Boolean	l_bAccesPartenairesCasto=(Boolean)getValueProperty("accesPartenairesCasto");
		
		String[] l_strThemesFavoris = (String[]) this.getValue().get("THEMESFAVORIS");
					
		boolean l_bOk=true;
		if(a_strEmail==null) 
            {
            l_bOk=false;
            }
			// si le mail de cr�ation pass�e en param�tre est la bonne
		if(l_bOk){
			try
			{	
				MutableRepository		l_MutableRepository = (MutableRepository) getRepository();
				MutableRepositoryItem	l_MutableAbonnement		= l_MutableRepository.createItem(a_strEmail,"abonnementNewsletter");

				Date l_Today = new Date();
				
				l_MutableAbonnement.setPropertyValue("dateCreation",l_Today);
				l_MutableAbonnement.setPropertyValue("dateDerniereModification",l_Today);
				l_MutableAbonnement.setPropertyValue("isValid", new Boolean("true"));
				l_MutableAbonnement.setPropertyValue("accesPartenairesCasto", l_bAccesPartenairesCasto);
				l_MutableAbonnement.setPropertyValue("nom", l_strNom);
				l_MutableAbonnement.setPropertyValue("prenom", l_strPrenom);
				l_MutableAbonnement.setPropertyValue("ville", l_strVille);
				l_MutableAbonnement.setPropertyValue("age", l_strage);
				l_MutableAbonnement.setPropertyValue("civilite", l_strCivilite);
				l_MutableAbonnement.setPropertyValue("format", l_strformat);
				l_MutableAbonnement.setPropertyValue("codePostal",l_strcodePostal );
				l_MutableAbonnement.setPropertyValue("proprietaire_locataire",l_strproprietaire_locataire );
				l_MutableAbonnement.setPropertyValue("nbPersonnes", l_strnbPersonnes);
				l_MutableAbonnement.setPropertyValue("origine",l_strorigine );
				l_MutableAbonnement.setPropertyValue("maisonCampagne",l_bmaisonCampagne );
				l_MutableAbonnement.setPropertyValue("maison",l_bmaison );
				l_MutableAbonnement.setPropertyValue("jardin",l_bjardin );
				l_MutableAbonnement.setPropertyValue("carteAtout",l_bcarteAtout );
				if ( (getCanalInscritpion()!=null ) && (!getCanalInscritpion().equals(""))  ) 
					{
                    l_MutableAbonnement.setPropertyValue("origine", getCanalInscritpion());
                    }
				
				l_MutableAbonnement.setPropertyValue("themesFavoris",l_strThemesFavoris );
				
				l_MutableRepository.addItem(l_MutableAbonnement);
			}catch(RepositoryException e){
				l_bOk=false;
				//trace.logError(this,e,".handleReInscrption RepositoryException : "+e.toString());
			}catch(Exception e){
 			l_bOk=false;
			//trace.logError(this,e,".handleReInscrption Exception : "+e.toString());
			}finally{
			//trace.logClose(this,".handleReInscrption");
			}		
		}
		return l_bOk;
	}
 	
  	
}// fin de classe