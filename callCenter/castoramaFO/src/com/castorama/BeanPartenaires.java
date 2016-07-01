package com.castorama;

import atg.servlet.*;
import atg.nucleus.logging.LogListener;

public class BeanPartenaires
{
	public static final String SITE_DEFAUT = "http://www.castorama.fr";
	public void setLogListener(LogListener a_LogListener) {
	  	m_LogListener = a_LogListener;
	}
	public LogListener getLogListener() {
	  return m_LogListener;
	}
	private LogListener m_LogListener;
	public void setReferer(String a_Referer) {
	  	m_Referer = a_Referer;
	}	
	public String getReferer() {
	  return m_Referer;
	}
	private String m_Referer;
	
	public void setPartenaire(String a_Partenaire) {
	  	m_Partenaire = a_Partenaire;
	}
	public String getPartenaire() {
	  return m_Partenaire;
	}
	private String m_Partenaire;
	
	public void traceCommande(DynamoHttpServletRequest a_Request,DynamoHttpServletResponse a_Response,String a_CommandeId, String a_Montant)
	{
		//trace.logOpen(this,".//traceCommande");
		try
		{
			String l_Event;
			String l_strIdSession = a_Request.getSession().getId();
			String l_Referer = getReferer();
			if (getPartenaire() == null)
			{
				l_Event = "session:"+l_strIdSession +" --- " + "COMMANDE ---- commande:"+ a_CommandeId + " --- montant:" + a_Montant + " --- referer:" + l_Referer + " --- " ;
			}
			else
			{
				l_Event = "session:"+l_strIdSession +" --- " + "COMMANDE PARTENAIRE---- commande:"+ a_CommandeId + " --- montant:" + a_Montant + " --- referer:" + l_Referer + " --- partenaire:" + getPartenaire();
			}
			println(l_Event);		
		}catch(Exception e){
			//trace.logError(this,e,".//traceCommande Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".//traceCommande");
		}
	}
	public void setPartenaire(DynamoHttpServletRequest a_Request,DynamoHttpServletResponse a_Response)
	{
		//trace.logOpen(this,".setPartenaire");
		try
		{
			String l_Host = "";
			String l_UrlTarget = "";
			String l_UrlDestination = a_Request.getParameter("urldestination");
			if (l_UrlDestination != null && !l_UrlDestination.equals(""))
			{
				
			}
			else
			{
				l_UrlDestination="/homepage/index.jhtml";
			}
			String l_Test = a_Request.getParameter("test");
			if (l_Test != null)
			{
				if (l_Test.equals("interne"))
				{
					l_Host = "";	
				}
				else
				{
					l_Host = SITE_DEFAUT;
				}
			}
			else
			{
				l_Host = SITE_DEFAUT;
			}
			
			l_UrlTarget = l_Host + l_UrlDestination;
			String l_IdDuPartenaire=a_Request.getParameter("idpartenaire");
			String l_UrlDeDestination=a_Request.getParameter("urldestination");
			String l_UrlDuPartenaire=a_Request.getParameter("urlpartenaire");
			String l_numCarteAtout=a_Request.getParameter("numcarteatout");
			String l_EmailDuPartenaire=a_Request.getParameter("emailpartenaire");
			String l_NewLetter=a_Request.getParameter("newletter");
			if(l_numCarteAtout==null){
				l_numCarteAtout="";
				l_NewLetter="";
				l_EmailDuPartenaire="";
			}else{
				l_numCarteAtout=" --- numCarteAtout:"+l_numCarteAtout;
				if(l_NewLetter==null||l_NewLetter.equals("")) 
                    {
                    l_NewLetter="false";
                    }
				l_NewLetter=" --- newsLetter:"+l_NewLetter;
				l_EmailDuPartenaire=" --- emailpart:"+l_EmailDuPartenaire;
			}
			String l_IpAppelant=a_Request.getRemoteAddr().toString();
			String l_strIdSession = a_Request.getSession().getId();
			String l_Event;
			l_Event = "session:"+l_strIdSession +" --- ip:" + l_IpAppelant +" --- partenaire:"+l_IdDuPartenaire + " --- urldest:" + l_UrlDeDestination + " --- urlpart:" + l_UrlDuPartenaire+l_numCarteAtout+l_EmailDuPartenaire+l_NewLetter;
			println(l_Event);
			if (l_IdDuPartenaire != null)
			{
				setPartenaire(l_IdDuPartenaire);
			}
			a_Response.sendRedirect(a_Response.encodeURL(l_UrlTarget));
		}
		catch(Exception e1)
		{
			//trace.logError(this,e1,".setPartenaire Exception : "+e1.toString());
			try
			{
				a_Response.sendRedirect(SITE_DEFAUT);
			}
			catch(Exception e2)
			{
			//trace.logError(this,e2,".setPartenaire Exception : "+e2.toString());
			}
		}finally{
			//trace.logClose(this,".setPartenaire");
		}
	}
	private void println(String a_sLog){
		atg.nucleus.logging.LogEvent l_ev = new atg.nucleus.logging.LogEvent(a_sLog);
		m_LogListener.logEvent(l_ev);
	}	
}
