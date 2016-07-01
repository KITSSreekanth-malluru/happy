package com.castorama.actioncommerciale;


import java.io.*;
import java.util.*;
import atg.commerce.catalog.*;
import atg.repository.*;
import javax.servlet.*;
import atg.servlet.*;
import atg.nucleus.naming.ParameterName;
import atg.repository.*;
import atg.commerce.claimable.ClaimableManager;
import atg.commerce.order.*;

public class DropletCodeCouponUsed extends DynamoServlet{


//param de sortie	
	public final static String	TRUE			=	"true";
	public final static String	FALSE			=	"false";
	public final static String	CODECOUPON		=	"codeCoupon";
	public final static String	ORDER			=	"order";
	public final static String	ELEMENTNAME		=	"elementName";

	ClaimableManager mClaimableManager;
	public ClaimableManager getClaimableManager()
	{
		return mClaimableManager;
	}

	public void setClaimableManager(ClaimableManager claimablemanager)
	{
		mClaimableManager = claimablemanager;
	}

	protected boolean m_bUsed =false;
	public boolean getCouponIsUsed()
	{
		return m_bUsed;
	}

	public void setCouponIsUsed(boolean a_bUsed)
	{
		m_bUsed = a_bUsed;
	}
	
	/**
	* Service<br>
	* @param	DynamoHttpServletRequest
	* @param	DynamoHttpServletResponse
	* @return	none
	* @throws	ServletException
    * @throws   IOException
	*
	* Damien est sur que le parameter ORDER sera de type Order 
	* et non de type repositoryItem donc je ne fais pas le test
	*/
	public void service(DynamoHttpServletRequest a_Request,
	                    DynamoHttpServletResponse a_Response)
	     throws ServletException, IOException
	{

		//trace.logOpen(this,".service");
		try{
			boolean l_bUsed=false;
			int l_nType=0;
			String l_strElementName = (String)a_Request.getParameter(ELEMENTNAME);
			if (l_strElementName == null) 
                {
                l_strElementName="element";
                }
			Order l_oOrder = (Order)a_Request.getObjectParameter(ORDER);
			//trace.logDebug(this,".service l_oOrder="+l_oOrder);
			String l_strCodeCoupon = a_Request.getParameter(CODECOUPON);
			
			RepositoryItem l_oPromotion = promotionApliquee(l_strCodeCoupon,l_oOrder);		
			a_Request.setParameter(l_strElementName,l_oPromotion);
			//trace.logDebug(this,".service l_oPromotion="+l_oPromotion);
			if(m_bUsed)
			{
				a_Request.serviceParameter(TRUE,a_Request,a_Response);				
			}
			else
			{
				a_Request.serviceParameter(FALSE,a_Request,a_Response);
			}
	
		}catch(Exception e){
			//trace.logError(this,e,".service : "+e.toString());
		}
		finally{
			//trace.logClose(this,".service");
		}
	}
	
	public boolean couponEstApplique(String a_strCodeCoupon,Order a_oOrder)
	{
		boolean l_bUsed=false;
		int l_nType=0;
		//trace.logOpen(this,".couponEstApplique");
		RepositoryItem l_oPromotion=null;
		try
		{
			//trace.logDebug(this,".service l_strCodeCoupon="+a_strCodeCoupon);
			RepositoryItem l_oCoupon = getClaimableManager().claimItem(a_strCodeCoupon);
			//trace.logDebug(this,".service l_oCoupon="+l_oCoupon);
			//trace.logDebug(this,".service l_oCoupon.getRepositoryId()="+l_oCoupon.getRepositoryId());
			l_oPromotion = (RepositoryItem)l_oCoupon.getPropertyValue("promotion");
			//trace.logDebug(this,".service l_oPromotion="+l_oPromotion);
			//trace.logDebug(this,".service l_oPromotion.getRepositoryId()="+l_oPromotion.getRepositoryId());
			//trace.logDebug(this,".service le type de promotion="+((Integer)l_oPromotion.getPropertyValue("type")).intValue());
			m_bUsed=promotionEstAppliquee(l_oPromotion,a_oOrder);
			
		}catch(Exception e){
			//trace.logError(this,e,".couponEstApplique : "+e.toString());
		}
		finally{
			//trace.logClose(this,".couponEstApplique");
		}
		//trace.logDebug(this,".promotionInAdjustments m_bUsed="+m_bUsed);
		return m_bUsed;
	}
	
	public RepositoryItem promotionApliquee(String a_strCodeCoupon,Order a_oOrder)
	{
		boolean l_bUsed=false;
		int l_nType=0;
		//trace.logOpen(this,".couponEstApplique");
		RepositoryItem l_oPromotion=null;
		try
		{
			//trace.logDebug(this,".service l_strCodeCoupon="+a_strCodeCoupon);
			RepositoryItem l_oCoupon = getClaimableManager().claimItem(a_strCodeCoupon);
			//trace.logDebug(this,".service l_oCoupon="+l_oCoupon);
			//trace.logDebug(this,".service l_oCoupon.getRepositoryId()="+l_oCoupon.getRepositoryId());
			l_oPromotion = (RepositoryItem)l_oCoupon.getPropertyValue("promotion");
			//trace.logDebug(this,".service l_oPromotion="+l_oPromotion);
			//trace.logDebug(this,".service l_oPromotion.getRepositoryId()="+l_oPromotion.getRepositoryId());
			//trace.logDebug(this,".service le type de promotion="+((Integer)l_oPromotion.getPropertyValue("type")).intValue());
			m_bUsed=promotionEstAppliquee(l_oPromotion,a_oOrder);
			
		}catch(Exception e){
			//trace.logError(this,e,".couponEstApplique : "+e.toString());
		}
		finally{
			//trace.logClose(this,".couponEstApplique");
		}
		//trace.logDebug(this,".promotionInAdjustments m_bUsed="+m_bUsed);
		return l_oPromotion;
	}
	
	public boolean promotionEstAppliquee(RepositoryItem a_oPromotion,Order a_oOrder)
	{
		
		boolean l_bUsed=false;
		int l_nType=0;
		//trace.logOpen(this,".promotionEstAppliquee");
		try
		{
			switch(((Integer)a_oPromotion.getPropertyValue("type")).intValue())
			{
				case 0:{l_nType=1; break;}
				case 1:{l_nType=1; break;}
				case 2:{l_nType=1; break;}
				case 3:{l_nType=1; break;}
				case 4:{l_nType=1; break;}
				case 5:{l_nType=2; break;}
				case 6:{l_nType=2; break;}
				case 7:{l_nType=2; break;}
				case 8:{l_nType=2; break;}
				case 9:{l_nType=3; break;}
				case 10:{l_nType=3; break;}
				case 11:{l_nType=3; break;}
				case 12:{l_nType=3; break;}
                default :;
			}
			//trace.logDebug(this,".promotionEstAppliquee l_nType="+l_nType);
			atg.commerce.pricing.PricingAdjustment l_oAdjustment = null;
			switch (l_nType)
			{
				case 1:
				{
					java.util.List l_oCommerceItems=a_oOrder.getCommerceItems();
					//trace.logDebug(this,".promotionEstAppliquee l_oCommerceItems="+l_oCommerceItems);
					int l_nBcl1 = 0;
					int l_nBcl2 = 0;
					for (l_nBcl1=0;l_nBcl1<l_oCommerceItems.size();l_nBcl1++)
					{
						atg.commerce.order.CommerceItem l_oCommerceItem = (atg.commerce.order.CommerceItem)l_oCommerceItems.get(l_nBcl1);
						//trace.logDebug(this,".promotionEstAppliquee l_oCommerceItem="+l_oCommerceItem);
						java.util.List l_oAdjustments=l_oCommerceItem.getPriceInfo().getAdjustments();
						//trace.logDebug(this,".promotionEstAppliquee l_oAdjustments="+l_oAdjustments);
						l_bUsed |= promotionInAdjustments(a_oPromotion,l_oAdjustments);
					}
					break;
				}
				case 2:
				{
					java.util.List l_oShippingGroups=a_oOrder.getShippingGroups();
					//trace.logDebug(this,".promotionEstAppliquee l_oShippingGroups="+l_oShippingGroups);
					int l_nBcl1 = 0;
					int l_nBcl2 = 0;
					for (l_nBcl1=0;l_nBcl1<l_oShippingGroups.size();l_nBcl1++)
					{
						atg.commerce.order.ShippingGroup l_oShippingGroup = (atg.commerce.order.ShippingGroup)l_oShippingGroups.get(l_nBcl1);
						//trace.logDebug(this,".promotionEstAppliquee l_oShippingGroup="+l_oShippingGroup);
						java.util.List l_oAdjustments=l_oShippingGroup.getPriceInfo().getAdjustments();
						//trace.logDebug(this,".promotionEstAppliquee l_oAdjustments="+l_oAdjustments);
						l_bUsed |= promotionInAdjustments(a_oPromotion,l_oAdjustments);
					}
					break;
				}
				case 3:
				{
					int l_nBcl1 = 0;
					java.util.List l_oAdjustments=a_oOrder.getPriceInfo().getAdjustments();
					//trace.logDebug(this,".promotionEstAppliquee l_oAdjustments="+l_oAdjustments);
					l_bUsed |= promotionInAdjustments(a_oPromotion,l_oAdjustments);
					break;
				}
                default:;
			}
		}catch(Exception e){
			//trace.logError(this,e,".promotionEstAppliquee : "+e.toString());
		}
		finally{
			//trace.logClose(this,".promotionEstAppliquee");
		}
		//trace.logDebug(this,".promotionEstAppliquee l_bUsed="+l_bUsed);
		return l_bUsed;
	}
	
	private boolean promotionInAdjustments(RepositoryItem a_oPromotion, java.util.List a_oAdjustments)
	{
		boolean l_bUsed=false;
		//trace.logOpen(this,".promotionInAdjustments");
		try
		{
			int l_nBcl1=0;
			atg.commerce.pricing.PricingAdjustment l_oAdjustment = null;
			for(l_nBcl1=0;l_nBcl1<a_oAdjustments.size();l_nBcl1++)
			{
				l_oAdjustment = (atg.commerce.pricing.PricingAdjustment)a_oAdjustments.get(l_nBcl1);
				//trace.logDebug(this,".promotionInAdjustments l_oAdjustment="+l_oAdjustment);
				atg.repository.RepositoryItem l_PricingModel = l_oAdjustment.getPricingModel();
				//trace.logDebug(this,".promotionInAdjustments l_PricingModel="+l_PricingModel);
				if (l_PricingModel != null)
				{
					//trace.logDebug(this,".promotionInAdjustments l_PricingModel.getRepositoryId()="+l_PricingModel.getRepositoryId());
					if (l_PricingModel.getRepositoryId().equals(a_oPromotion.getRepositoryId()))
					{
						l_bUsed=true;
					}
				}
				//trace.logDebug(this,".promotionInAdjustments l_bUsed="+l_bUsed);
			}
		}catch(Exception e){
			//trace.logError(this,e,".promotionInAdjustments : "+e.toString());
		}
		finally{
			//trace.logClose(this,".promotionInAdjustments");
		}
		
		//trace.logDebug(this,".promotionInAdjustments l_bUsed="+l_bUsed);
		return l_bUsed;
	}
}
