package com.castorama;
import java.util.*;
import atg.servlet.*;
import atg.service.dynamo.Configuration;

public class BeanSession
{
	boolean m_bAVote;
	String m_sProfileRequestId;
	boolean m_bAVoteAuSondage;
	Vector m_vQuestionId=new Vector();
	String m_sResolution="";
	String m_sPanierType	="";
	String m_strWithCoupon="";
	Vector m_vNavig=new Vector();
	
	boolean m_bEstAuthentifieAtout;
	public boolean getAuthentificationAtout(){
		return m_bEstAuthentifieAtout;
	}
	
	public void setAuthentificationAtout(boolean a_bEstAuthentifieAtout){
		m_bEstAuthentifieAtout=a_bEstAuthentifieAtout;
	}
			
	public boolean getAVote(){
		return m_bAVote;
	}
	
	public void setAVote(boolean a_bAVote){
		m_bAVote=a_bAVote;
	}
	
	public String getResolution(){
		return m_sResolution;
	}
	
	public void setResolution(String a_sResolution){
		m_sResolution=a_sResolution;
	}

	public void setPanierType(String a_sPanierType){
		m_sPanierType=a_sPanierType;
	}
	
		public String getPanierType(){
		return m_sPanierType;
	}
	
	

	
	public String getProfileRequestId(){
		return m_sProfileRequestId;
	}
	
	public void setProfileRequestId(String a_sProfileRequestId){
		m_sProfileRequestId=a_sProfileRequestId;
	}
	public boolean getAVoteAuSondage(){
		return m_bAVoteAuSondage;
	}
	
	public void setAVoteAuSondage(boolean a_bAVote){
		m_bAVoteAuSondage=a_bAVote;
	}
	
	/**
	* UseACoupon	 : <br>
	* Variable du flag d'utilisattion d'un coupon
	*/
	boolean m_bUseACoupon;

	/**
	* R�cup�ration du flag d'utilisattion d'un coupon
	* @param none
	* @return boolean
	* @throws none
	*/
	public boolean getUseACoupon(){
		return m_bUseACoupon;
	}
	
	/**
	* Modification du flag d'utilisattion d'un coupon
	* @param boolean a_bUseACoupon
	* @return none
	* @throws none
	*/
	public void setUseACoupon(boolean a_bUseACoupon){
		m_bUseACoupon=a_bUseACoupon;
	}
	
	
	
	public boolean aDejaVote(DynamoHttpServletRequest a_oRequest,int a_nQuestionId){
		//Trace.logOpen(this,".aDejaVote");
		try{
			CookieManager l_oCookie=new CookieManager();
			String l_sCookie="";
			StringTokenizer l_oSt;
			l_sCookie=l_oCookie.getStringCookie("vote",a_oRequest,"");
			if("".equals(l_sCookie)==false){
				l_oSt=new StringTokenizer(l_sCookie,"_");
				while(l_oSt.hasMoreTokens()){
					m_vQuestionId.add(new Integer(l_oSt.nextToken()));
				}
			}
			if(m_vQuestionId.contains(new Integer(a_nQuestionId)))
                {
                return true;
                }
		}catch(Exception e){
			//Trace.logError(this,e,".aDejaVote Exception : "+e.toString());
			return false;
		}finally{
			//Trace.logClose(this,".aDejaVote");
		}
		return false;
	}
	
	
	
	public boolean aDejaVoteAuSondage(DynamoHttpServletRequest a_oRequest){
		//Trace.logOpen(this,".aDejaVoteAuSondage");
		try{
		CookieManager l_oCookie=new CookieManager();
		String l_sCookie="";
		l_sCookie=l_oCookie.getStringCookie("sondage",a_oRequest,"");
		if("oui".equals(l_sCookie)){
			return true;
		}else{
			return false;
		}
		}catch(Exception e){
			//Trace.logError(this,e,".aDejaVoteAuSondage Exception : "+e.toString());
			return false;
		}finally{
			//Trace.logClose(this,".aDejaVoteAuSondage");
		}
	}
	
	/**
	* Nombre de tentatives d'authentification.<br>
	*/
	protected int m_nNbTentativesAuthentification=0;
	
	/**
	* R�cup�ration du nombre de tentatives d'authentification.<br>
	* @return int Le nombre de tentatives d'authentification.
	*/
	public int getNbTentativesAuthentification()
	{
		return m_nNbTentativesAuthentification;
	}
	
	/**
	* Mise � jour du nombre de tentatives d'authentification.<br>
	* @param int Le nombre de tentatives d'authentification.
	*/
	public void setNbTentativesAuthentification(int a_nNbTentativesAuthentification)
	{
		m_nNbTentativesAuthentification = a_nNbTentativesAuthentification;
	}
	
	/**
	* Incr�mentation du nombre de tentatives d'authentification.<br>
	* @param int rapport d'incr�mentation.
	*/
	public void setIncrementationNbTentativesAuthentification(int a_nRapport)
	{
		m_nNbTentativesAuthentification+=a_nRapport;
	}
	
	/**
	* Nombre de tentatives de r�ponse � la question myst�re.<br>
	*/
	protected int m_nNbTentativesReponseQuestionMystere=0;
	
	/**
	* R�cup�ration du nombre de tentatives de r�ponse � la question myst�re.<br>
	* @return int Le nombre de tentatives de r�ponse � la question myst�re.
	*/
	public int getNbTentativesReponseQuestionMystere()
	{
		return m_nNbTentativesReponseQuestionMystere;
	}
	
	/**
	* Mise � jour du nombre de tentatives de r�ponse � la question myst�re.<br>
	* @param int Le nombre de tentatives de r�ponse � la question myst�re.
	*/
	public void setNbTentativesReponseQuestionMystere(int a_nNbTentativesReponseQuestionMystere)
	{
		m_nNbTentativesReponseQuestionMystere = a_nNbTentativesReponseQuestionMystere;
	}
	
	/**
	* Incr�mentation du nombre de tentatives de r�ponse � la question myst�re.<br>
	* @param int rapport d'incr�mentation.
	*/
	public void setIncrementationNbTentativesReponseQuestionMystere(int a_nRapport)
	{
		m_nNbTentativesReponseQuestionMystere+=a_nRapport;
	}
}
