
package com.castorama;

import com.castorama.utils.CheckingTools;
import java.io.*;
import javax.servlet.*;
import atg.servlet.*;

import java.util.Map;
import java.util.Enumeration;

import atg.userprofiling.Profile;
import atg.repository.RepositoryItem;

import java.util.List;
import java.util.Vector;


import atg.droplet.GenericFormHandler;
import atg.droplet.DropletException;
import atg.repository.servlet.RepositoryFormHandler;
import atg.repository.*;
import atg.userprofiling.Profile;
import atg.repository.RepositoryException;
import atg.repository.rql.RqlStatement;


/**
* RechercheUserFormHandler : Castorama 2001
* Ce composant est utilis� pour la recherche d'un user dans le call_center
* @author Sylvain Delettr� - INTERNENCE (Mai 2001) 
*/
public class RechercheUserFormHandler extends RepositoryFormHandler {
	
	String m_strSubmitSuccessURL;		// URL de redirection en cas de succes d'envoi du mail
	Profile m_Profile;					// Profil
	String m_strLogin;
	String m_strId;
	String m_strNom;
	String m_strPrenom;
	String m_strCodePostal;
	String m_strVille;
	
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
	* R�cup�ration du Login
	* @param none
	* @return String Login
	* @throws none
	*/
	public String getLogin(){

		return m_strLogin;
	}
	
	/**
	* Modification du Login
	* @param String Login
	* @return none
	* @throws none
	*/
	public void setLogin(String a_strLogin)
	{

		m_strLogin = a_strLogin.trim() ;

	}
	
	/**
	* R�cup�ration Id
	* @param none
	* @return String Id
	* @throws none
	*/
	public String getId(){

		return m_strId;
	}
	
	/**
	* Modification du Id
	* @param String Id
	* @return none
	* @throws none
	*/
	public void setId(String a_strId)
	{

		m_strId = a_strId.trim() ;

	}
	
	/**
	* R�cup�ration Nom
	* @param none
	* @return String Nom
	* @throws none
	*/
	public String getNom(){

		return m_strNom;
	}
	
	/**
	* Modification du Nom
	* @param String Nom
	* @return none
	* @throws none
	*/
	public void setNom(String a_strNom)
	{

		m_strNom = a_strNom.trim();

	}
	
	/**
	* R�cup�ration Prenom
	* @param none
	* @return String Prenom
	* @throws none
	*/
	public String getPrenom(){

		return m_strPrenom;
	}
	
	/**
	* Modification du Prenom
	* @param String Prenom
	* @return none
	* @throws none
	*/
	public void setPrenom(String a_strPrenom)
	{

		m_strPrenom = a_strPrenom.trim() ;

	}
	
	/**
	* R�cup�ration CodePostal
	* @param none
	* @return String CodePostal
	* @throws none
	*/
	public String getCodePostal(){

		return m_strCodePostal;
	}
	
	/**
	* Modification du CodePostal
	* @param String CodePostal
	* @return none
	* @throws none
	*/
	public void setCodePostal(String a_strCodePostal)
	{

		m_strCodePostal = a_strCodePostal.trim() ;

	}
	
	/**
	* R�cup�ration Ville
	* @param none
	* @return String Ville
	* @throws none
	*/
	public String getVille(){

		return m_strVille;
	}
	
	/**
	* Modification du Ville
	* @param String Ville
	* @return none
	* @throws none
	*/
	public void setVille(String a_strVille)
	{

		m_strVille = a_strVille.trim() ;

	}
	
	private RepositoryItem[] m_ResultatRecherche;
	public RepositoryItem[] getResultatRecherche()
	{

		return m_ResultatRecherche;
	}
	
	
    
    /*
     * CETTE Methode a été retouchée pour la migration front office. 
     * La version retouchée prte le même nom.
     */
   /* public boolean rechercheContact(String a_strNom, String a_strPrenom, String a_strCodePostal, String a_strVille)
	{
		//trace.logOpen(this,".rechercheContact");
		boolean l_bOk=true;
		RepositoryItem[] items = null;			
		try
		{	
			RepositoryItemDescriptor l_Item = getRepository().getItemDescriptor("user");
			RepositoryView l_ItemView = l_Item.getRepositoryView();
						
			String l_strStatement="";
			int l_nNbParam = 0;
			
			// nom
			if (CheckingTools.checkString(a_strNom))
			{
				if (l_strStatement.equals(""))
				{
					l_strStatement = "adresseFacturation.lastName STARTS WITH IGNORECASE ?" + l_nNbParam;
					
				} else
				{
					l_strStatement = l_strStatement + " and adresseFacturation.lastName STARTS WITH IGNORECASE ?" + l_nNbParam;
				}
				l_nNbParam++;
			}
			// prenom
			if (CheckingTools.checkString(a_strPrenom))
			{
				if (l_strStatement.equals(""))
				{
					l_strStatement = "adresseFacturation.firstName STARTS WITH IGNORECASE ?" + l_nNbParam;
					
				} else
				{
					l_strStatement = l_strStatement + " and adresseFacturation.firstName STARTS WITH IGNORECASE ?" + l_nNbParam;
				}
				l_nNbParam++;
			}
			// codePostal
			if (CheckingTools.checkString(a_strCodePostal))
			{
				if (l_strStatement.equals(""))
				{
					l_strStatement = "adresseFacturation.postalCode STARTS WITH ?" + l_nNbParam;
					
				} else
				{
					l_strStatement = l_strStatement + " and adresseFacturation.postalCode STARTS WITH ?" + l_nNbParam;
				}
				l_nNbParam++;
			}
			
			// ville
			if (CheckingTools.checkString(a_strVille))
			{
				if (l_strStatement.equals(""))
				{
					l_strStatement = "adresseFacturation.city STARTS WITH IGNORECASE ?" + l_nNbParam;
					
				} else
				{
					l_strStatement = l_strStatement + " and adresseFacturation.city STARTS WITH IGNORECASE ?" + l_nNbParam;
				}
				l_nNbParam++;
			}
						
			RqlStatement statement = RqlStatement.parseRqlStatement(l_strStatement);
						
			Object params[] = new Object[l_nNbParam];
			
			int l_Pos = 0;
									
			if (CheckingTools.checkString(a_strNom))
			{
				params[l_Pos] = a_strNom;
				l_Pos++;
			}
			
			if (CheckingTools.checkString(a_strPrenom))
			{
				params[l_Pos] = a_strPrenom;
				l_Pos++;
			}
			
			if (CheckingTools.checkString(a_strCodePostal))
			{
				params[l_Pos] = a_strCodePostal;
				l_Pos++;
			}
			
			if (CheckingTools.checkString(a_strVille))
			{
				params[l_Pos] = a_strVille;
				l_Pos++;
			}
			items = statement.executeQuery(l_ItemView, params);
		}catch(RepositoryException e){
			l_bOk=false;
			items= new RepositoryItem[0];
			//trace.logError(this,e,".rechercheContact RepositoryException : "+e.toString());
		}catch(Exception e){
			l_bOk=false;
			items= new RepositoryItem[0];
			//trace.logError(this,e,".rechercheContact Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".rechercheContact");
		}
		m_ResultatRecherche = items;
		return l_bOk;	
	}*/
	
    
    public boolean rechercheContact(String a_strNom, String a_strPrenom, String a_strCodePostal, String a_strVille)
    {
        if (isLoggingDebug())
        {
            logDebug(this.getClass() + ".rechercheContact");
        }
        boolean l_bOk=true;
        RepositoryItem[] items = null;          
        try
        {   
            RepositoryItemDescriptor l_Item = getRepository().getItemDescriptor("user");
            RepositoryView l_ItemView = l_Item.getRepositoryView();
                        
            String l_strStatement="";
            int l_nNbParam = 0;
            
            // nom
            if (CheckingTools.checkString(a_strNom))
            {
                if (l_strStatement.equals(""))
                {
                    l_strStatement = "lastName STARTS WITH IGNORECASE ?" + l_nNbParam;
                    
                } else
                {
                    l_strStatement = l_strStatement + " and lastName STARTS WITH IGNORECASE ?" + l_nNbParam;
                }
                l_nNbParam++;
            }
            // prenom
            if (CheckingTools.checkString(a_strPrenom))
            {
                if (l_strStatement.equals(""))
                {
                    l_strStatement = "firstName STARTS WITH IGNORECASE ?" + l_nNbParam;
                    
                } else
                {
                    l_strStatement = l_strStatement + " and firstName STARTS WITH IGNORECASE ?" + l_nNbParam;
                }
                l_nNbParam++;
            }
            // codePostal
            if (CheckingTools.checkString(a_strCodePostal))
            {
                if (l_strStatement.equals(""))
                {
                    l_strStatement = "billingAddress.postalCode STARTS WITH ?" + l_nNbParam;
                    
                } else
                {
                    l_strStatement = l_strStatement + " and billingAddress.postalCode STARTS WITH ?" + l_nNbParam;
                }
                l_nNbParam++;
            }
            
            // ville
            if (CheckingTools.checkString(a_strVille))
            {
                if (l_strStatement.equals(""))
                {
                    l_strStatement = "billingAddress.city STARTS WITH IGNORECASE ?" + l_nNbParam;
                    
                } else
                {
                    l_strStatement = l_strStatement + " and billingAddress.city STARTS WITH IGNORECASE ?" + l_nNbParam;
                }
                l_nNbParam++;
            }
            
            
            /*
             * =========================================================================
             *  GY : stabilisidation : on ne peut plus modifier le profil d'un user FO
             * =========================================================================
             */
            l_strStatement = l_strStatement + " AND (estUtilisateurFo = false OR estUtilisateurFo is null)";
            
                        
                        
            RqlStatement statement = RqlStatement.parseRqlStatement(l_strStatement);
                        
            Object params[] = new Object[l_nNbParam];
            
            int l_Pos = 0;
                                    
            if (CheckingTools.checkString(a_strNom))
            {
                params[l_Pos] = a_strNom;
                l_Pos++;
            }
            
            if (CheckingTools.checkString(a_strPrenom))
            {
                params[l_Pos] = a_strPrenom;
                l_Pos++;
            }
            
            if (CheckingTools.checkString(a_strCodePostal))
            {
                params[l_Pos] = a_strCodePostal;
                l_Pos++;
            }
            
            if (CheckingTools.checkString(a_strVille))
            {
                params[l_Pos] = a_strVille;
                l_Pos++;
            }
            items = statement.executeQuery(l_ItemView, params);
        }catch(RepositoryException e){
            l_bOk=false;
            items= new RepositoryItem[0];
            //trace.logError(this,e,".rechercheContact RepositoryException : "+e.toString());
        }catch(Exception e){
            l_bOk=false;
            items= new RepositoryItem[0];
            //trace.logError(this,e,".rechercheContact Exception : "+e.toString());
        }finally{
            //trace.logClose(this,".rechercheContact");
        }
        m_ResultatRecherche = items;
        return l_bOk;   
    }
    
    
    
    
    
    
	
	/**
	* Validation du formulaire 
	* @param DynamoHttpServletRequest  a_Request
	* @param DynamoHttpServletResponse a_Response
	* @return boolean erreur oui ou non
	* @throws IOException,ServletException
	*/
	public boolean handleSubmit(	DynamoHttpServletRequest  a_Request,
	   								DynamoHttpServletResponse a_Response)
	throws IOException,ServletException {
		
		//trace.logOpen(this,".handleSubmit");
		boolean l_bOk=true;
		try
		{
			if(!CheckingTools.checkString(m_strNom.trim())&&!CheckingTools.checkString(m_strPrenom.trim())&&!CheckingTools.checkString(m_strCodePostal.trim())&&!CheckingTools.checkString(m_strVille.trim())){
				addFormException(new DropletException("ErreurForm", "ErreurForm"));
				l_bOk=false;
			}
			if(l_bOk){
				l_bOk=rechercheContact(m_strNom,m_strPrenom,m_strCodePostal,m_strVille);
				if(l_bOk) 
                    {
                    a_Response.sendLocalRedirect(getSubmitSuccessURL(), a_Request);
                    }
			}
		}catch(Exception e){
			//trace.logError(this,e,".handleSubmit Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".handleSubmit");
		}
		return true;
	}
	
	
	
	/**
	* Validation du premier formulaire (m_strLogin)
	* @param DynamoHttpServletRequest  a_Request
	* @param DynamoHttpServletResponse a_Response
	* @return boolean erreur oui ou non
	* @throws IOException,ServletException
	*/
	public boolean handleSubmit1(	DynamoHttpServletRequest  a_Request,
	   								DynamoHttpServletResponse a_Response)
	throws IOException,ServletException {
		
		
		//trace.logOpen(this,".handleSubmit1");
		boolean l_bOk=true;
		try
		{
			l_bOk&=CheckingTools.checkString(m_strLogin,"LoginMissing",(GenericFormHandler)this);
			if(l_bOk){
				l_bOk=rechercheContact1(m_strLogin);				
				if(l_bOk) 
                    {
                    a_Response.sendLocalRedirect(getSubmitSuccessURL(), a_Request);
                    }
			}
		}catch(Exception e){
			//trace.logError(this,e,".handleSubmit1 Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".handleSubmit1");
		}
		return true;
	}
	
	
	/**
	* Validation du premier formulaire
	* @param DynamoHttpServletRequest  a_Request
	* @param DynamoHttpServletResponse a_Response
	* @return boolean erreur oui ou non
	* @throws IOException,ServletException
	*/
	public boolean rechercheContact1(String a_strLogin)
	{
		//trace.logOpen(this,".rechercheContact1");
		boolean l_bOk=true;
		RepositoryItem[] items = null;			
		try
		{	
			//declarations
			RepositoryItemDescriptor l_Item = getRepository().getItemDescriptor("user");
			RepositoryView l_ItemView = l_Item.getRepositoryView();
			
			Object params[] = new Object[1];
			String l_strStatement="";
			int l_nNbParam = 0;
			
			// login
			if (CheckingTools.checkString(a_strLogin))
			{
				l_strStatement = "login STARTS WITH ?0";
                
                /*
                 * =========================================================================
                 *  GY : stabilisidation : on ne peut plus modifier le profil d'un user FO
                 * =========================================================================
                 */
                l_strStatement = l_strStatement + " AND (estUtilisateurFo = false OR estUtilisateurFo is null)";
				params[l_nNbParam] = a_strLogin;
			}
			RqlStatement statement = RqlStatement.parseRqlStatement(l_strStatement);

			items = statement.executeQuery (l_ItemView, params);
		}catch(RepositoryException e){
			l_bOk=false;
			items= new RepositoryItem[0];
			//trace.logError(this,e,".rechercheContact1 RepositoryException : "+e.toString());
		}catch(Exception e){
			l_bOk=false;
			items= new RepositoryItem[0];
			//trace.logError(this,e,".rechercheContact1 Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".rechercheContact1");
		}
		m_ResultatRecherche = items;
		return l_bOk;	
	}
	
	
	/**
	* Validation du deuxieme formulaire (m_strId)
	* @param DynamoHttpServletRequest  a_Request
	* @param DynamoHttpServletResponse a_Response
	* @return boolean erreur oui ou non
	* @throws IOException,ServletException
	*/
	public boolean handleSubmit2(	DynamoHttpServletRequest  a_Request,
	   								DynamoHttpServletResponse a_Response)
	throws IOException,ServletException {
		
		//trace.logOpen(this,".handleSubmit2");
		boolean l_bOk=true;
		try
		{
			l_bOk&=CheckingTools.checkString(m_strId,"ProfileIdMissing",(GenericFormHandler)this);
			if(l_bOk){
				l_bOk=rechercheContact2(m_strId);				
				if(l_bOk) 
                     {
                    a_Response.sendLocalRedirect(getSubmitSuccessURL(), a_Request);
                     }
			}
		}catch(Exception e){
			//trace.logError(this,e,".handleSubmit2 Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".handleSubmit2");
		}
		return true;
	}
	
	/**
	* Validation du deuxieme formulaire
	* @param DynamoHttpServletRequest  a_Request
	* @param DynamoHttpServletResponse a_Response
	* @return boolean erreur oui ou non
	* @throws IOException,ServletException
	*/
	public boolean rechercheContact2(String a_strId)
	{
		//trace.logOpen(this,".rechercheContact2");
		boolean l_bOk=true;
		RepositoryItem[] items = null;			
		try
		{	
			//declarations
			RepositoryItemDescriptor l_Item = getRepository().getItemDescriptor("user");
			RepositoryView l_ItemView = l_Item.getRepositoryView();
			
			Object params[] = new Object[1];
			String l_strStatement="";
			int l_nNbParam = 0;
			
			// id
			if (CheckingTools.checkString(a_strId))
			{
				l_strStatement = "id STARTS WITH IGNORECASE ?0";
                /*
                 * =========================================================================
                 *  GY : stabilisidation : on ne peut plus modifier le profil d'un user FO
                 * =========================================================================
                 */
                l_strStatement = l_strStatement + " AND (estUtilisateurFo = false OR estUtilisateurFo is null)";
				params[l_nNbParam] = a_strId;
			}
			RqlStatement statement = RqlStatement.parseRqlStatement(l_strStatement);

			items = statement.executeQuery (l_ItemView, params);
		}catch(RepositoryException e){
			l_bOk=false;
			items= new RepositoryItem[0];
			//trace.logError(this,e,".rechercheContact2 RepositoryException : "+e.toString());
		}catch(Exception e){
			l_bOk=false;
			items= new RepositoryItem[0];
			//trace.logError(this,e,".rechercheContact2 Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".rechercheContact2");
		}
		m_ResultatRecherche = items;
		return l_bOk;	
	}
	
}// fin de classe


