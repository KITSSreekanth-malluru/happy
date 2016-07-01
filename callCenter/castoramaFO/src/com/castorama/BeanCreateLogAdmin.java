package com.castorama;

import java.util.*;
import atg.servlet.*;
import atg.repository.MutableRepository;
import atg.repository.Repository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;

/**
* BeanCreateLogAdmin : Castorama 2001
* Ce composant est utilis� pour creer un log admin.
* @version 1.0
* @author Sylvain Delettr� - INTERNENCE (juillet 2001) 
* 
* Recherche des ids
* @param String a_strEnregistrement,String a_strItemDescriptor
* @return none
** @exception none  
*/
public class BeanCreateLogAdmin{

	private Repository m_Repository;
	
	public void setRepository(Repository a_Repository)
	{
	  	m_Repository = a_Repository;
	}
	public Repository getRepository()
	{
	  	return m_Repository;
	}
				
	public void enregistrement(String a_strType, String a_strAction, String a_strCommentaire, String a_strIp, String a_strNomLogin)
	{
		//Trace.logOpen(this,".enregistrement");
		try{
			//date du jour
			java.sql.Date l_strDateDuJour;
			GregorianCalendar todaysDate = new GregorianCalendar ();
			l_strDateDuJour = new java.sql.Date(todaysDate.getTime().getTime());
							
			MutableRepository l_MutableRepository	= (MutableRepository)getRepository();
			MutableRepositoryItem l_logAdministration;
			String l_DescriptorName = "logAdministration";
			l_logAdministration = l_MutableRepository.createItem(l_DescriptorName);
			l_logAdministration = (MutableRepositoryItem)l_MutableRepository.addItem(l_logAdministration);
					
			l_logAdministration.setPropertyValue("type",a_strType);
			l_logAdministration.setPropertyValue("dateCreation",l_strDateDuJour);
			l_logAdministration.setPropertyValue("action",a_strAction);
			l_logAdministration.setPropertyValue("commentaire",a_strCommentaire);
			l_logAdministration.setPropertyValue("adresseIp",a_strIp);
			if (a_strNomLogin.trim().length()>0)
            {
				l_logAdministration.setPropertyValue("nomLogin",a_strNomLogin);
            }
			
			l_MutableRepository.updateItem(l_logAdministration);
			
		}
		catch (RepositoryException e) {
			//Trace.logError(this,e,".enregistrement RepositoryException : "+e.toString());
		}catch(Exception e){
		//	Trace.logError(this,e,".enregistrement Exception : "+e.toString());
		}finally{
		//	Trace.logClose(this,".enregistrement");
		}
	}
	
	public static void createLog(DynamoHttpServletRequest pRequest,DynamoHttpServletResponse pResponse,String a_strType, String a_strAction, String a_strCommentaire)
	{
	//	Trace.logOpen("castorama.BeanCreateLogAdmin.createLog");
		try{
			atg.servlet.security.CurrentUser l_CurrentUser = (atg.servlet.security.CurrentUser) pRequest.resolveName("/atg/dynamo/security/CurrentUser");
			BeanCreateLogAdmin l_BeanCreateLogAdmin = (BeanCreateLogAdmin) pRequest.resolveName("/castorama/BeanCreateLogAdmin");

			String l_strNomLogin = l_CurrentUser.getUserManager().getCurrentUser(pRequest).getPrimaryPersona(l_CurrentUser.getUserAuthority()).getName();
			String l_strIp = pRequest.getRemoteAddr();
			
			l_BeanCreateLogAdmin.enregistrement(a_strType,a_strAction,a_strCommentaire,l_strIp,l_strNomLogin);
		}catch(Exception e){
		//	Trace.logError(e,"castorama.BeanCreateLogAdmin.createLog Exception : "+e.toString());
		}finally{
		//	Trace.logClose("castorama.BeanCreateLogAdmin.createLog");
		}
	}
	
}// fin de classe

