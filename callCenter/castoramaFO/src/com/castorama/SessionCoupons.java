package com.castorama;

import java.util.Vector;
import java.util.List;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.repository.RepositoryException;
import atg.repository.Repository;
import atg.repository.rql.RqlStatement;
import java.util.Date;

public class SessionCoupons
{
	private Repository m_RepositoryCoupons;
	public void setCouponsRepository(Repository a_RepositoryCoupons){

	  	m_RepositoryCoupons = a_RepositoryCoupons;

	}
	public Repository getCouponsRepository(){

	  	return m_RepositoryCoupons;
	}
	private Repository m_ClaimableRepository;
	public void setClaimableRepository(Repository a_ClaimableRepository){

	  	m_ClaimableRepository = a_ClaimableRepository;

	}
	public Repository getClaimableRepository(){

	  	return m_ClaimableRepository;
	}		
	private Vector m_EnsembleCodeCoupons = new Vector();
	public Vector getListeCoupons()
	{

		return m_EnsembleCodeCoupons;
	}
	private int m_NombreCouponsAutorises;
	public int getNombreCouponsAutorises()
	{

		return m_NombreCouponsAutorises;
	}
	public void setNombreCouponsAutorises(int a_NombreCouponsAutorises)
	{

		m_NombreCouponsAutorises = a_NombreCouponsAutorises;

	}			
	
	private String m_Message;
	public String getMessage()
	{
		return m_Message;
	}	
	public void setMessage(String a_Message)
	{
		m_Message=a_Message;
	}	
	
	
	private com.castorama.actioncommerciale.DropletCodeCouponUsed m_oDroplet;
	public com.castorama.actioncommerciale.DropletCodeCouponUsed getDropletCodeCouponUsed()
	{
		return m_oDroplet;
	}	
	public void setDropletCodeCouponUsed(com.castorama.actioncommerciale.DropletCodeCouponUsed a_oDroplet)
	{
		m_oDroplet=a_oDroplet;
	}	

	private atg.commerce.order.Order m_oOrder;
	public atg.commerce.order.Order getOrder()
	{
		return m_oOrder;
	}	
	public void setOrder(atg.commerce.order.Order a_oOrder)
	{
		m_oOrder=a_oOrder;
	}	
	
	public void resetListeCoupons()
	{
		//Trace.logOpen(this,".resetListeCoupons");
		try{
			m_EnsembleCodeCoupons.clear();
		}catch(Exception e){
			//Trace.logError(this,e,".resetListeCoupons Exception : "+e.toString());
		}finally{
			//.logClose(this,".resetListeCoupons");
		}
	}
	public int ajouterCodeCoupon(String a_CodeCoupon)
	{
		//Trace.logOpen(this,".ajouterCodeCoupon");
		int l_Resultat = 0;
		try{
			if (m_EnsembleCodeCoupons != null)
			{
				m_EnsembleCodeCoupons.add(a_CodeCoupon);
				m_Message=getMessagePromo(a_CodeCoupon);
				//Trace.logDebug(this,".ajouterCodeCoupon Ajout de : " + a_CodeCoupon);
				l_Resultat = 0;
			}	
			else
			{
				//Trace.logDebug(this,".ajouterCodeCoupon NON Ajout de : " + a_CodeCoupon);
				l_Resultat = 1;
			}
		}catch(Exception e){
			//Trace.logError(this,e,".ajouterCodeCoupon Exception : "+e.toString());
		}finally{
			//Trace.logClose(this,".ajouterCodeCoupon");
		}
		return l_Resultat;	
	}	
	public int estAjoutableCodeCoupon(String a_CodeCoupon)
	{
		//Trace.logOpen(this,".estAjoutableCodeCoupon");
		int l_Resultat = 0;
		boolean l_bok=true ;
		setMessage("");
		try
		{
			//Trace.logDebug("m_oDroplet="+m_oDroplet);
			//Trace.logDebug("a_CodeCoupon="+a_CodeCoupon);
			//Trace.logDebug("m_oOrder="+m_oOrder);

			if ((l_bok)&& (!(existeCoupon(a_CodeCoupon))) )
			{
				l_bok =false;	l_Resultat = 7;
				//Trace.logDebug("l_Resultat="+l_Resultat);
			}
			
			if ((l_bok)&& (!(dateCouponOk(a_CodeCoupon))) )
			{ l_bok =false;	l_Resultat = 5;
				//Trace.logDebug("l_Resultat="+l_Resultat);
			}
			
			if ((l_bok)&& !(datePromoOk(a_CodeCoupon)))
			{l_bok =false;l_Resultat = 6;
				//Trace.logDebug("l_Resultat="+l_Resultat);
			}
					
			if ((l_bok) && (m_EnsembleCodeCoupons.contains(a_CodeCoupon)))
			{
				if ((l_bok)&& (m_oDroplet.couponEstApplique(a_CodeCoupon, m_oOrder)) )
				{ 
					l_bok =false;	l_Resultat = 8;
				}
				else
				{
					l_bok =false;	l_Resultat = 1;
				}
			
				//Trace.logDebug("l_Resultat="+l_Resultat);
			}
				
			if ((l_bok) && !(m_EnsembleCodeCoupons.size() <= m_NombreCouponsAutorises-1))
			{	l_bok =false;l_Resultat = 2;
				//Trace.logDebug("l_Resultat="+l_Resultat);
			}
			
			if ((l_bok) &&(estPromotionGenerique(a_CodeCoupon)))
			{ l_bok =false;	l_Resultat = 0;setMessage(getMessagePromo(a_CodeCoupon));	
				//Trace.logDebug("l_Resultat="+l_Resultat);
			}
			
			if ((l_bok) &&(!aDejaEteUtilise(a_CodeCoupon)))
			{	l_bok =false;l_Resultat = 0;	
			
				//Trace.logDebug("l_Resultat="+l_Resultat);
			}
			if ((l_bok) &&(aDejaEteUtilise(a_CodeCoupon)))
			{ l_bok =false;	l_Resultat = 4;	
				//Trace.logDebug("l_Resultat="+l_Resultat);
			}
			
		}catch(Exception e){
			//Trace.logError(this,e,".estAjoutableCodeCoupon Exception : "+e.toString());
			l_Resultat = 3;
		}finally{
			//Trace.logClose(this,".estAjoutableCodeCoupon");
		}
		return l_Resultat;
	}	
	private boolean aDejaEteUtilise(String a_CodeCoupon)
	{
		//Trace.logOpen(this,".aDejaEteUtilise");
		boolean l_bADejaEteUtilise = false;
		Repository l_CouponsRepository;	
		try
		{
			l_CouponsRepository = (Repository) getCouponsRepository();	
			
			RepositoryItemDescriptor l_Item = l_CouponsRepository.getItemDescriptor("casto_coupons");
			RepositoryView l_ItemView = l_Item.getRepositoryView();
			String l_strStatement = "code_coupon=?0";
			RqlStatement l_Statement = RqlStatement.parseRqlStatement(l_strStatement);
			Object params[] = new Object[1];
			params[0] = a_CodeCoupon;
			RepositoryItem[] l_ItemsCoupon = null;
				
			l_ItemsCoupon = l_Statement.executeQuery(l_ItemView, params);
			if (l_ItemsCoupon != null)
			{
				RepositoryItem l_ItemCoupon = l_ItemsCoupon[0];
				if (l_ItemCoupon != null)
				{
					if  (l_ItemCoupon.getPropertyValue("order_id") !=null)
					{
						String l_OrderId = l_ItemCoupon.getPropertyValue("order_id").toString();
						//Trace.logError(this,".aDejaEteUtilise Le coupon "+a_CodeCoupon+" a deja ete utilise dans la commande "+l_OrderId);
					}
					else
					{
					}
					l_bADejaEteUtilise = true;
				}
			}
			else
			{
				l_bADejaEteUtilise = false;
			}			

		}catch(Exception e){
			//Trace.logError(this,e,".aDejaEteUtilise Exception : "+e.toString());
			l_bADejaEteUtilise=false;
		}finally{
			//Trace.logClose(this,".aDejaEteUtilise");
		}
		return l_bADejaEteUtilise;
	}
	
	
	private boolean dateCouponOk(String a_CodeCoupon)
	{
		//Trace.logOpen(this,".dateCouponOk");
		boolean l_bdateCouponOk = true;
		try
		{
			if (m_ClaimableRepository != null)
			{
				java.text.SimpleDateFormat l_oFormater = new  java.text.SimpleDateFormat("dd MMMMMMM yyyy HH:mm");
				RepositoryItem l_ItemClaimable = getClaimableRepository().getItem(a_CodeCoupon,"PromotionClaimable");
				Date l_expirationDate = (Date) l_ItemClaimable.getPropertyValue("expirationDate");
				Date l_now = new Date();
				if (l_expirationDate.after(l_now))
                    {
                    l_bdateCouponOk= true;
                    }
				else
				{
					 l_bdateCouponOk= false;
					 setMessage("Le code n'�tait valable que jusqu'au " + l_oFormater.format(l_expirationDate));
				}
			}
		}catch(Exception e){
			//Trace.logError(this,e,".dateCouponOk Exception : "+e.toString());
			l_bdateCouponOk = false;
		}finally{
			//Trace.logClose(this,".dateCouponOk");
		}
		return l_bdateCouponOk;
	}	


	public String getMessagePromo(String a_CodeCoupon)
	{
		//Trace.logOpen(this,".getMessage");
		String message = new String();
		try
		{
			if (m_ClaimableRepository != null)
			{
				RepositoryItem l_ItemClaimable = getClaimableRepository().getItem(a_CodeCoupon,"PromotionClaimable");
				RepositoryItem l_ItemPromotion = (RepositoryItem) l_ItemClaimable.getPropertyValue("promotion");
				
				message = (String) l_ItemPromotion.getPropertyValue("messageCoupon");
				if (message == null || message.equals(""))
					{
                    message = (String) l_ItemPromotion.getPropertyValue("displayName");
                    }
			}
		}catch(Exception e){
			//Trace.logError(this,e,".getMessage Exception : "+e.toString());
		}finally{
			//Trace.logClose(this,".getMessage");
		}
		return message;
	}	


	private boolean datePromoOk(String a_CodeCoupon)
	{
		//Trace.logOpen(this,".datePromoOk");
		boolean l_bdatePromoOk = true;
		try
		{
			if (m_ClaimableRepository != null)
			{
				java.text.SimpleDateFormat l_oFormater = new  java.text.SimpleDateFormat("dd MMMMMMM yyyy HH:mm");
				RepositoryItem l_ItemClaimable = getClaimableRepository().getItem(a_CodeCoupon,"PromotionClaimable");
				RepositoryItem l_ItemPromotion = (RepositoryItem) l_ItemClaimable.getPropertyValue("promotion");
				
				Boolean l_enable = (Boolean) l_ItemPromotion.getPropertyValue("enabled");
				if (!(l_enable.booleanValue())) 
				{	setMessage("L'offre commerciale n'est plus valable");
					return false;
				}
				
				Date l_expirationDate = (Date) l_ItemPromotion.getPropertyValue("endUsable");
				Date l_now = new Date();
				if (l_expirationDate.after(l_now))
                    {
                    l_bdatePromoOk= true;
                    }
				else
				{ 	l_bdatePromoOk= false;
					setMessage("L'offre commerciale n'�tait valable que jusqu'au " + l_oFormater.format(l_expirationDate));
				}
			}
		}catch(Exception e){
			//Trace.logError(this,e,".datePromoOk Exception : "+e.toString());
			l_bdatePromoOk = false;
		}finally{
			//Trace.logClose(this,".datePromoOk");
		}
		return l_bdatePromoOk;
	}	
		
	
	
	private boolean estPromotionGenerique(String a_CodeCoupon)
	{
		//Trace.logOpen(this,".estPromotionGenerique");
		boolean l_bEstPromotionGenerique = true;
		try
		{
			if (m_ClaimableRepository != null)
			{
				// Pour savoir si un coupon peut �tre utilis� de fa�on g�n�rique
				// on regarde si la promotion associ�e a la property uses � Infinite (-1)
				RepositoryItem l_ItemClaimable = getClaimableRepository().getItem(a_CodeCoupon,"PromotionClaimable");
				RepositoryItem l_ItemPromotion = (RepositoryItem) l_ItemClaimable.getPropertyValue("promotion");
				Integer l_Uses = (Integer) l_ItemPromotion.getPropertyValue("uses");
				//Trace.logDebug(this,".estPromotionGenerique l_Uses = " + l_Uses.toString());
				if (l_Uses.equals(new Integer(-1)))
				{
					l_bEstPromotionGenerique = true;
				}
				else
				{
					l_bEstPromotionGenerique = false;
				}
			}
		}catch(Exception e){
			//Trace.logError(this,e,".estPromotionGenerique Exception : "+e.toString());
				l_bEstPromotionGenerique = false;
		}finally{
			//Trace.logClose(this,".estPromotionGenerique");
		}
		return l_bEstPromotionGenerique;
	}	


	private boolean existeCoupon(String a_CodeCoupon)
	{
	//	Trace.logOpen(this,".existeCoupon");
		boolean l_bExisteCoupon = true;
		try
		{
			if (m_ClaimableRepository != null)
			{
				RepositoryItem l_ItemClaimable = getClaimableRepository().getItem(a_CodeCoupon,"PromotionClaimable");
				if (l_ItemClaimable==null)
                    {
                    l_bExisteCoupon=false;
                    }
			}
		}catch(Exception e){
			//Trace.logError(this,e,".existeCoupon Exception : "+e.toString());
				l_bExisteCoupon = false;
		}finally{
			//Trace.logClose(this,".existeCoupon");
		}
		return l_bExisteCoupon;
	}	


}
