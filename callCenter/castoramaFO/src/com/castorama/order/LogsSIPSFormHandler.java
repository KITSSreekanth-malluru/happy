package com.castorama.order;

import com.castorama.utils.CheckingTools;
import java.io.*;
import java.util.List;

import javax.servlet.*;

import com.castorama.order.BeanLogSips;

import atg.servlet.*;
import atg.userprofiling.Profile;
import atg.droplet.GenericFormHandler;
import atg.droplet.DropletException;
import atg.repository.servlet.RepositoryFormHandler;
import atg.repository.*;
import atg.nucleus.Nucleus ;

import com.castorama.*;
					 

/**
* LogsSIPSFormHandler : Castorama 2001
* Ce composant est utilisé pour créer des logs SIPS dans le call center ... si un paiement n'a pas été loggué correctement.
* @version 1.0  
* @author Damien DURIEZ - LOGICACMG (Juilet 2003) 
*/
public class LogsSIPSFormHandler extends RepositoryFormHandler {
	
	
	private boolean				m_bCreate;
	private String				m_strOrderId ;

	
	
	/**
	* Récupération du create
	* @param	none
	* @return	boolean
	* @throws	none
	*/
	public boolean getCreate(){
		return m_bCreate;
	}
	
	
	/**
	* Modification du Create
	* @param	boolean Create
	* @return	none
	* @throws	none
	*/
	public void   setCreate(boolean a_bCreate){
		m_bCreate = a_bCreate ;
	}
	
	
	/**
	* Récupération du OrderId
	* @param none
	* @return none
	* @throws none
	*/
	public String getOrderId(){
		return m_strOrderId;
	}
	
	
	/**
	* Modification du OrderId
	* @param String OrderId
	* @return none
	* @throws none
	*/
	public void   setOrderId(String a_strOrderId){
		m_strOrderId = a_strOrderId ;
	}

	
	
	/**
	* Creation d'une log SIPS : <br>
	* Vérification d'erreurs de saisie
	* @param a_Request the servlet's request
	* @param a_Response the servlet's response
	* @exception ServletException if there was an error while executing the code
	* @exception IOException if there was an error with servlet io
	*/
	public void preCreateItem(	DynamoHttpServletRequest	a_Request,
	  							DynamoHttpServletResponse	a_Response) 
								throws ServletException, IOException {

		//Trace.logOpen(this,".preCreateItem");
		boolean l_bOk=true;
		try
		{	
			setOrderId((String)getValueProperty("order_id"));
			if(getExists()){
				l_bOk &= false;
				addFormException(new DropletException("existError", "existError"));
			}
			l_bOk &= CheckingTools.checkString((String)getValueProperty("order_id"),				"orderIdError",(GenericFormHandler)this);
			l_bOk &= CheckingTools.checkString((String)getValueProperty("response_code"),			"responseCodeError",(GenericFormHandler)this);
			//l_bOk &= CheckingTools.checkNull((String)getValueProperty("libelle"),				"libelle",(GenericFormHandler)this);
			l_bOk &= CheckingTools.checkIntegerPositif((Integer)getValueProperty("montant"),				"montantError",(GenericFormHandler)this);
			l_bOk &= CheckingTools.checkString((String)getValueProperty("transaction_id"),		"transactionIdError",(GenericFormHandler)this);
			l_bOk &= CheckingTools.checkString((String)getValueProperty("authorisation_id"),		"authorisationIdError",(GenericFormHandler)this);
			l_bOk &= CheckingTools.checkString((String)getValueProperty("payment_means"),			"paymentMeansError",(GenericFormHandler)this);
			l_bOk &= CheckingTools.checkString((String)getValueProperty("card_number"),			"cardNumberError",(GenericFormHandler)this);
			l_bOk &= CheckingTools.checkString((String)getValueProperty("payment_certificate"),	"paymentCertificateError",(GenericFormHandler)this);
			l_bOk &= CheckingTools.checkString((String)getValueProperty("payment_time"),			"paymentTimeError",(GenericFormHandler)this);
			l_bOk &= CheckingTools.checkString((String)getValueProperty("payment_date"),			"paymentDateError",(GenericFormHandler)this);
			l_bOk &= CheckingTools.checkString((String)getValueProperty("devise"),				"deviseError",(GenericFormHandler)this);
			l_bOk &= CheckingTools.checkString((String)getValueProperty("profile_id"),			"profileError",(GenericFormHandler)this);
			l_bOk &= CheckingTools.checkString((String)getValueProperty("date_trans_expire"),		"dateTransExpireError",(GenericFormHandler)this);
			
			/*if(l_bOk){
				
				setValueProperty("libelle", "--création manuelle-- "+BeanLogSips.getLibelle((String) getValueProperty("response_code")));
			}*/
			
		}catch(Exception e){
 			addFormException(new DropletException(e.toString(), "exceptionError"));
		//	Trace.logError(this,e,".preCreateItem : "+e.toString());
		}finally{
			//******  Log d'administration  *****
			if(l_bOk)	BeanCreateLogAdmin.createLog(a_Request,a_Response,"CallCenter","Creation","Creation manuelle d'une ligne de log SIPS pour la commande"+ getValueProperty("order_id"));
		//	Trace.logClose(this,".preCreateItem");
		}

	}
	
	
	/**
	* Vérification: une ligne de log existe-t-elle pour la commmande spécifiée par la property orderId
	* @param	none
	* @return	boolean	Exists
	* @throws	none
	*/
	public boolean getExists(){
		
		//Trace.logOpen(this,".getExists");
		boolean l_bOk = false;
		try{
			if (getOrderId()!=null){
				RepositoryItemDescriptor	l_Item		= getRepository().getItemDescriptor("logsips");
				RepositoryView				l_ItemView	= l_Item.getRepositoryView();
				QueryBuilder				l_Builder	= l_ItemView.getQueryBuilder();
			
				QueryExpression		l_QueryPropertie	= l_Builder.createPropertyQueryExpression("order_id");
				QueryExpression		l_QueryValue		= l_Builder.createConstantQueryExpression(getOrderId());				
				Query				l_Query				= l_Builder.createComparisonQuery(l_QueryPropertie, l_QueryValue, QueryBuilder.EQUALS);
	   			RepositoryItem[]	l_ResultatRecherche	= l_ItemView.executeQuery(l_Query);
	   			
	   			l_bOk = (l_ResultatRecherche!=null && l_ResultatRecherche.length>0);
	   		}
 		}catch(RepositoryException e){
			//Trace.logError(this,e,".getExists RepositoryException : "+e.toString());
 		}catch(Exception e){
			//Trace.logError(this,e,".getExists Exception : "+e.toString());
		}finally{
			//Trace.logClose(this,".getExists");
		}
		return l_bOk;
	}
	
	
	/**
	* Modification du Exists
	* @param	boolean	Exists
	* @return	none
	* @throws	none
	*/
	public void   setExists(boolean a_strExists){
		
	}

	
}// fin de class
