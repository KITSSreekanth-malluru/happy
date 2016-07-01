
package com.castorama;

import com.castorama.utils.CheckingTools;
import java.io.*;
import java.util.*;
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

import atg.commerce.catalog.*;


/**
* RechercheUserFormHandler : Castorama 2001
* Ce composant est utilis� pour la recherche d'une commande dans le call_center
* @author Sylvain Delettr� - INTERNENCE (Mai 2001) 
*/

public class RechercheCommandeParDateFormHandler extends RepositoryFormHandler
{
	String m_strSubmitSuccessURL;		// URL de redirection en cas de succes du form
	String m_strSubmitErrorURL;		// URL de redirection en cas de probleme du form
	Profile m_Profile;					// Profil
	String m_strDateDeb;
	String m_strDateFin;
	java.sql.Date m_DateDebFormat;
	//******* Dates *****************************
	private java.sql.Date m_strDateFormatDeb;
	private java.sql.Date m_strDateFormatFin;
	
	CatalogTools m_CatalogTools;
	public void setCatalogTools(CatalogTools a_CatalogTools)
	{

	  	m_CatalogTools = a_CatalogTools;

	}
	public CatalogTools getCatalogTools()
	{

	  	return m_CatalogTools;
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
	* Modification du SubmitErrorURL
	* @param String SubmitErrorURL
	* @return none
	* @throws none
	*/
	public void setSubmitErrorURL(String a_strSubmitErrorURL)
	{

		m_strSubmitErrorURL = a_strSubmitErrorURL ;

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
	* R�cup�ration de DateDeb
	* @param none
	* @return String DateDeb
	* @throws none
	*/
	public String getDateDeb(){

		return m_strDateDeb;
	}
	
	/**
	* Modification de DateDeb
	* @param String DateDeb
	* @return none
	* @throws none
	*/
	public void setDateDeb(String a_strDateDeb)
	{

		m_strDateDeb = a_strDateDeb ;

	}
	
	/**
	* R�cup�ration de DateFin
	* @param none
	* @return String DateFin
	* @throws none
	*/
	public String getDateFin(){

		return m_strDateFin;
	}
	
	/**
	* Modification de DateFin
	* @param String DateFin
	* @return none
	* @throws none
	*/
	public void setDateFin(String a_strDateFin)
	{

		m_strDateFin = a_strDateFin ;

	}
	
	private RepositoryItem[] m_ResultatRecherche;
	public RepositoryItem[] getResultatRecherche()
	{

		return m_ResultatRecherche;
	}
	
	
	
	/**
	* Validation formulaire date
	* @param DynamoHttpServletRequest  a_Request
	* @param DynamoHttpServletResponse a_Response
	* @return boolean erreur oui ou non
	* @throws IOException,ServletException
	*/
	public boolean handleSubmit3(	DynamoHttpServletRequest  a_Request,
	   								DynamoHttpServletResponse a_Response)
	throws IOException,ServletException {
		//trace.logOpen(this,".handleSubmit3");
		boolean l_bOk=true;
		
        m_strDateDeb = a_Request.getParameter("dateDebutRecherche");
        m_strDateFin = a_Request.getParameter("dateFinRecherche");
        
        
        try
		{
			if(!CheckingTools.checkString(m_strDateDeb.trim()) && !CheckingTools.checkString(m_strDateFin.trim())){
				addFormException(new DropletException("ErreurDate", "ErreurDate"));
				l_bOk=false;
			}
			if(l_bOk){
				l_bOk=rechercheContribution3(m_strDateDeb,m_strDateFin);
				if(l_bOk) 
                    {
                    a_Response.sendLocalRedirect(getSubmitSuccessURL(), a_Request);
                    }
			}
		}catch(Exception e){
			a_Response.sendLocalRedirect(getSubmitErrorURL(), a_Request);
			//trace.logError(this,e,".handleSubmit3 Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".handleSubmit3");
		}
		return true;
	}
	
	/**
	 * Permet de verifier String a_strDateDeb , String a_strDateFin
	 * @param String a_strDateDeb
	 * @param String a_strDateFin
	 * @return none
	 * @exception none
	 */
	public boolean rechercheContribution3(String a_strDateDeb, String a_strDateFin)
	{
		//trace.logOpen(this,".rechercheContribution3");
		boolean l_bOk=true;
		RepositoryItem[] items = null;
		try
		{		
			RepositoryItemDescriptor l_Item = getRepository().getItemDescriptor("order");
			RepositoryView l_ItemView = l_Item.getRepositoryView();
						
			String l_strStatement="";
			int l_nNbParam = 0;
						
			// dateDeb
			if (a_strDateDeb.compareTo("")!=0 && a_strDateDeb.length()==10)
			{
				if (l_strStatement.equals(""))
				{
					l_strStatement = "submittedDate >= ?" + l_nNbParam;
					
				} else
				{
					l_strStatement = l_strStatement + " and submittedDate >= ?" + l_nNbParam;
				}
				l_nNbParam++;
			}
			
			// dateFin
			if (a_strDateFin.compareTo("")!=0 && a_strDateFin.length()==10)
			{
				if (l_strStatement.equals(""))
				{
					l_strStatement = "submittedDate <= ?" + l_nNbParam;
					
				} else
				{
					l_strStatement = l_strStatement + " and submittedDate <= ?" + l_nNbParam;
				}
				l_nNbParam++;
			}
			
			//order by
			if (!l_strStatement.equals(""))
			{
					l_strStatement = l_strStatement + " order by submittedDate desc";
			}
			RqlStatement statement = RqlStatement.parseRqlStatement(l_strStatement);
						
			Object params[] = new Object[l_nNbParam];
			
			int l_Pos = 0;
			if (m_strDateDeb.compareTo("")!=0 && m_strDateDeb.length()==10)
			{
				m_strDateFormatDeb = transformationDate(m_strDateDeb);
				params[l_Pos] = m_strDateFormatDeb;
				l_Pos++;
			}
			
			if (m_strDateFin.compareTo("")!=0 && m_strDateFin.length()==10)
			{
				m_strDateFormatFin = transformationDate(m_strDateFin);
				params[l_Pos] = m_strDateFormatFin;
				l_Pos++;
			}
			items = statement.executeQuery(l_ItemView, params);
		}catch(RepositoryException e){
			l_bOk=false;
			items= new RepositoryItem[0];
			//trace.logError(this,e,".rechercheContribution3 RepositoryException : "+e.toString());
		}catch(Exception e){
			l_bOk=false;
			items= new RepositoryItem[0];
			//trace.logError(this,e,".rechercheContribution3 Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".rechercheContribution3");
		}
		m_ResultatRecherche = items;
		return l_bOk;	
	}
	
	public boolean rechercheCommandeParDate(String a_strDateDeb,String a_strDateFin)
	{
		//trace.logOpen(this,".rechercheCommandeParDate");
		boolean l_bOk=true;
		RepositoryItem[] items = null;
		try
		{		
			RepositoryItemDescriptor l_Item = getRepository().getItemDescriptor("order");
			RepositoryView l_ItemView = l_Item.getRepositoryView();
			QueryBuilder l_Builder = l_ItemView.getQueryBuilder();
						
			Query[] l_TabPieces;
			Vector l_VPieces = new  Vector();
			// a_strDateDeb
			if (!a_strDateDeb.trim().equals(""))
			{
				java.sql.Date l_DateDeb = getDate(a_strDateDeb);
				QueryExpression l_Query1 =
				  l_Builder.createPropertyQueryExpression("creationDate");
		
				QueryExpression l_Query2 =
				  l_Builder.createConstantQueryExpression(l_DateDeb);				
				
				Query l_Query = l_Builder.createComparisonQuery(l_Query1, l_Query2, QueryBuilder.GREATER_THAN_OR_EQUALS);

				l_VPieces.add(l_Query); 				
			}
			
			// a_strDateFin
			if (!a_strDateFin.trim().equals(""))
			{
				java.sql.Date l_DateFin = getDate(a_strDateFin);
				QueryExpression l_Query1 =
				  l_Builder.createPropertyQueryExpression("creationDate");
		
				QueryExpression l_Query2 =
				  l_Builder.createConstantQueryExpression(l_DateFin);				
				
				Query l_Query = l_Builder.createComparisonQuery(l_Query1, l_Query2, QueryBuilder.LESS_THAN_OR_EQUALS);

				l_VPieces.add(l_Query); 				
			}
			
			l_TabPieces = new Query [l_VPieces.size ()];
			l_VPieces.copyInto (l_TabPieces);				
			
			Query l_AndQuery = l_Builder.createAndQuery(l_TabPieces);
			SortDirectives sortDirectives = new SortDirectives();
			sortDirectives.addDirective(new SortDirective("submittedDate", SortDirective.DIR_DESCENDING));
   			items = l_ItemView.executeQuery(l_AndQuery,sortDirectives);
		}catch(RepositoryException e){
			l_bOk=false;
			items= new RepositoryItem[0];
			//trace.logError(this,e,".rechercheCommandeParDate RepositoryException : "+e.toString());
		}catch(Exception e){
			l_bOk=false;
			items= new RepositoryItem[0];
			//trace.logError(this,e,".rechercheCommandeParDate Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".rechercheCommandeParDate");
		}
		m_ResultatRecherche = items;
		return l_bOk;	
		
	}
	
	
	public boolean handleSubmit(	DynamoHttpServletRequest  a_Request,
	   								DynamoHttpServletResponse a_Response)
	throws IOException,ServletException {
		//trace.logOpen(this,".handleSubmit");
		boolean l_bOk=true;
		try
		{
			if(!CheckingTools.checkString(m_strDateDeb.trim()) && !CheckingTools.checkString(m_strDateFin.trim())){
				addFormException(new DropletException("ErreurDate", "ErreurDate"));
				l_bOk=false;
			}
			if(l_bOk){
				l_bOk=rechercheCommandeParDate(m_strDateDeb,m_strDateFin);
				if(l_bOk) 
                    {
                    a_Response.sendLocalRedirect(getSubmitSuccessURL(), a_Request);
                    }
			}
		}catch(Exception e){
			a_Response.sendLocalRedirect(getSubmitErrorURL(), a_Request);
			//trace.logError(this,e,".handleSubmit Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".handleSubmit");
		}
		return true;
	}
	
	//public java.sql.Date getDate (String a_strDate)
	//parametre (String) date sous forme 'jj/mm/aaaa')
	//renvoie la (String) au format sql.Date
	
	public java.sql.Date getDate(String a_strDateDebFormat)
	{
		//trace.logOpen(this,".getDate");
		try
		{
			if(CheckingTools.checkString(a_strDateDebFormat.trim()))
			{
				Calendar l_Cal = Calendar.getInstance();
				String l_stryyyy = a_strDateDebFormat.substring(6,10);
				String l_strmm = a_strDateDebFormat.substring(3,5);
				String l_strdd = a_strDateDebFormat.substring(0,2);
							
				int l_nyear = Integer.parseInt(l_stryyyy);
				int l_nmonth = Integer.parseInt(l_strmm);
				l_nmonth = l_nmonth - 1;
				int l_nday = Integer.parseInt(l_strdd);
				l_Cal.set(l_Cal.YEAR,l_nyear);
				l_Cal.set(l_Cal.MONTH,l_nmonth);
				l_Cal.set(l_Cal.DAY_OF_MONTH,l_nday);
								
				m_DateDebFormat = new java.sql.Date(l_Cal.getTime().getTime());
			}
		}catch(Exception e){
			m_DateDebFormat=null;
			//trace.logError(this,e,".getDate Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".getDate");
		}
		return m_DateDebFormat;
	}
	
	
	/**
	 * permet de transformer une string jj/mm/aaaa en java.sql.Date
	 * @param String a_strDate
	 * @return java.sql.Date
	 * @exception none
	 */
	public java.sql.Date transformationDate(String a_strDate)
	{
		//trace.logOpen(this,".transformationDate");
		java.sql.Date l_ModifDateFormat = null;
		try
		{
			if(CheckingTools.checkString(a_strDate.trim()) && a_strDate.length()==10)
			{
				Calendar l_Cal = Calendar.getInstance();
				String l_stryyyy = a_strDate.substring(6,10);
				String l_strmm = a_strDate.substring(3,5);
				String l_strdd = a_strDate.substring(0,2);
							
				int l_nyear = Integer.parseInt(l_stryyyy);
				int l_nmonth = Integer.parseInt(l_strmm);
				l_nmonth = l_nmonth - 1;
				int l_nday = Integer.parseInt(l_strdd);
				l_Cal.set(l_Cal.YEAR,l_nyear);
				l_Cal.set(l_Cal.MONTH,l_nmonth);
				l_Cal.set(l_Cal.DAY_OF_MONTH,l_nday);
						
				l_ModifDateFormat = new java.sql.Date(l_Cal.getTime().getTime());
			}
		}catch(Exception e){
			l_ModifDateFormat=null;
			//trace.logError(this,e,".transformationDate Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".transformationDate");
		}
		return l_ModifDateFormat;
	}
	
}
