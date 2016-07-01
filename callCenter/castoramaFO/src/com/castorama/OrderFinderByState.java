package com.castorama;

import java.io.*;
import javax.servlet.*;
import atg.servlet.*;
import java.util.*;
import java.util.Vector;

import atg.droplet.GenericFormHandler;
import atg.droplet.DropletException;
import atg.repository.servlet.RepositoryFormHandler;
import atg.repository.*;
import atg.repository.RepositoryException;
import atg.repository.rql.RqlStatement;
import atg.commerce.order.*;
import atg.commerce.CommerceException;

import com.castorama.inventory.*;

import java.text.SimpleDateFormat;
import java.text.ParsePosition;


/**
* OrderFinderByState : Castorama 2001
* Ce composant de recherche d'une commande par �tat, est associ� � un formulaire du call-center.
* @version 1.0  
* @author Damien DURIEZ - INTERNENCE (JUILLET 2001) 
*/
public class OrderFinderByState extends GenericFormHandler{
	
				
	
	private			Repository			m_Repository ;
	private			RepositoryItem[]	m_ResultatRecherche ;
	
	private			String				m_strState ;
	private			boolean				m_bReservable ;
	
	private String m_strSubmitSuccessURL;		// URL de redirection en cas de succes
	
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
	* R�cup�ration de State
	* @param none
	* @return String - State
	* @throws none
	*/
	public String getState() {

		return m_strState;
	}
	
	
	/**
	* Modification du State
	* @param String - State
	* @return none
	* @throws none
	*/
	public void setState(String a_strState) {

		m_strState = a_strState;

	}
	
	
	
	
	/**
	* R�cup�ration du flag reservable
	* @param none
	* @return Boolean - State
	* @throws none
	*/
	public boolean getReservable() {

		return m_bReservable;
	}
	
	
	/**
	* Modification du flag reservable
	* @param Boolean - State
	* @return none
	* @throws none
	*/
	public void setReservable(boolean a_bReservable) {

		m_bReservable = a_bReservable;

	}
	
	
	/**
	* R�cup�ration du Repository
	* @param none
	* @return Repository - Repository
	* @throws none
	*/
	public Repository getRepository() {

		return m_Repository;
	}
	
	
	/**
	* Modification du Repository
	* @param String - Repository
	* @return none
	* @throws none
	*/
	public void setRepository(Repository a_Repository) {

		m_Repository = a_Repository;

	}

	
	/**
	* R�cup�ration de la liste de r�sultats
	* @param none
	* @return RepositoryItem[] - liste des abonnements trouv�s
	* @throws none
	*/
	public RepositoryItem[] getResultatRecherche()
	{

		return m_ResultatRecherche;
	}
	
	
	/**
	* Recherche de commandes
	* @param DynamoHttpServletRequest  a_Request
	* @param DynamoHttpServletResponse a_Response
	* @return boolean succes oui/non
	* @throws none
	*/	
	public boolean handleFind(	DynamoHttpServletRequest  a_Request,
	   							DynamoHttpServletResponse a_Response)
	throws IOException,ServletException {
		//trace.logOpen(this,".handleFind");
		try
		{	
			
			RepositoryItemDescriptor	l_Item		= m_Repository.getItemDescriptor("order");
			RepositoryView				l_ItemView	= l_Item.getRepositoryView();
			QueryBuilder				l_Builder	= l_ItemView.getQueryBuilder();
			
			Vector l_CriteresVector = new  Vector();	// les criteres de recherches en vector : plus fexible en cas d'ajout de criteres
			
			QueryExpression	l_QueryPropertie ;
			QueryExpression	l_QueryValue ;
			Query			l_Query	;
			
			
			// ****** Critere : S�lectionner uniquement les order avec le bon �tat
			if (m_strState!=null){
				l_QueryPropertie	= l_Builder.createPropertyQueryExpression("BOState");
				l_QueryValue		= l_Builder.createConstantQueryExpression(m_strState);				
				l_Query				= l_Builder.createComparisonQuery(l_QueryPropertie, l_QueryValue, QueryBuilder.EQUALS);
				l_CriteresVector.add(l_Query); 
			}
			
			Query[]				l_CritereList		= new Query [l_CriteresVector.size()];
			l_CriteresVector.copyInto (l_CritereList);	
			
			Query				l_AndQuery			= l_Builder.createAndQuery(l_CritereList);
			
			SortDirectives		l_SortDirectives	= new SortDirectives();
			l_SortDirectives.addDirective(new SortDirective("submittedDate", SortDirective.DIR_DESCENDING));
			
   			m_ResultatRecherche = l_ItemView.executeQuery(l_AndQuery,l_SortDirectives);
			
			/*Vector l_vOrderReservable = new Vector();
			for(int i=0;i<m_ResultatRecherche.length;i++)
			{
				RepositoryItem l_ItemOrder = m_ResultatRecherche[i];
				try
				{
					Order l_Order = OrderManager.getOrderManager().loadOrder(l_ItemOrder.getRepositoryId());
					if(InventoryTools.getInstance().checkOrderAvecReservationStock(l_Order) && m_bReservable){
						l_vOrderReservable.addElement(m_ResultatRecherche[i]);
					}else if(!InventoryTools.getInstance().checkOrderAvecReservationStock(l_Order) && !m_bReservable){
						l_vOrderReservable.addElement(m_ResultatRecherche[i]);
					}
				}
				catch(CommerceException e)
				{
 					//trace.logError(this,e,".handleFind CommerceException : "+e.toString());
				}
			}
			m_ResultatRecherche = new RepositoryItem[l_vOrderReservable.size()];
			l_vOrderReservable.copyInto(m_ResultatRecherche);*/
			a_Response.sendLocalRedirect(getSubmitSuccessURL(), a_Request);
		}catch(RepositoryException e){
 			//trace.logError(this,e,".handleFind RepositoryException : "+e.toString());
		}catch(Exception e){
 			//trace.logError(this,e,".handleFind Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".handleFind");
		}
		return true;
	}
	

}// fin de classe

