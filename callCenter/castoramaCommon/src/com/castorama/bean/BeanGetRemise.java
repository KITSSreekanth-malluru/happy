package com.castorama.bean;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;

import com.castorama.commande.CastoOrder;
import com.castorama.order.pricing.CastoShippingCalculator;

/**
* BeanGetRemise : Castorama 2001
* Ce composant de recherche d'un castotroc, est associ� � un formulaire de l'administration.
* @version 1.0  
* @author Damien DURIEZ - INTERNENCE (AOUT 2001) 
*/
public class BeanGetRemise {
	
	private	CastoShippingCalculator	m_CastoShippingCalculator ;
	
	
	/**
	* R�cup�ration du ShippingCalculator
	* @param none
	* @return ShippingCalculator - ShippingCalculator
	* @throws none
	*/
	public void setShippingCalculator(CastoShippingCalculator a_CastoShippingCalculator){
		m_CastoShippingCalculator = a_CastoShippingCalculator;
	}
	
	/**
	* Modification du ShippingCalculator
	* @param ShippingCalculator - ShippingCalculator
	* @return none
	* @throws none
	*/
	public CastoShippingCalculator getShippingCalculator(){
		return m_CastoShippingCalculator;
	}
	
	
	
	
	/**
	* R�cup�ration de la remise d'une commande s'il y en a.
	* Renvoie 0.0 si pas de remise
	* @param	String	OrderId
	* @return	double	Remise
	* @throws none
	*/	
	public double getRemise(String a_strOrderId)
	throws IOException,ServletException {
		//trace.logOpen(this,".getRemise(String a_strOrderId)");
		double l_dRemise = 0.00 ;
		
		try {
			Order l_Order = OrderManager.getOrderManager().loadOrder(a_strOrderId);
			l_dRemise = getRemise(l_Order);
		}catch(CommerceException e){
			//trace.logError(this,e,".getRemise(String a_strOrderId) CommerceException : "+e.toString());
		}catch(Exception e){
			//trace.logError(this,e,".getRemise(String a_strOrderId) Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".getRemise(String a_strOrderId)");
		}
		return l_dRemise ;
	}
	
	
	/**
	* R�cup�ration de la remise d'une commande s'il y en a.
	* Renvoie 0.0 si pas de remise
	* @param	Order	a_Order
	* @return	double	Remise
	* @throws none
	*/	
	public double getRemise(Order a_Order)
	{
		//trace.logOpen(this,".getRemise(Order a_Order)");
		double l_Promo = 0.0;
		try
		{
			if (a_Order != null)
			{
				CastoOrder l_Order  = (CastoOrder)a_Order;
				atg.commerce.pricing.OrderPriceInfo l_PriceInfo = a_Order.getPriceInfo();
				if ( l_PriceInfo != null)
				{
					double l_RealShipping = 0.0;
					double l_Preparation = 0.0;
					List l_ShippingGroups = a_Order.getShippingGroups();
					if (l_ShippingGroups != null)
					{
						HardgoodShippingGroup l_ShippingGroup = (HardgoodShippingGroup) l_ShippingGroups.get(0);
						if (l_ShippingGroup != null)
						{
							if (m_CastoShippingCalculator != null)
							{
								if(l_ShippingGroup.getPriceInfo()!=null)
								{
									l_RealShipping = l_ShippingGroup.getPriceInfo().getRawShipping();
									l_Promo = l_PriceInfo.getRawSubtotal() + l_RealShipping  - l_PriceInfo.getTotal();
									if (l_Order.getPreparationPriceInfo()!=null) {
									  l_Preparation = l_Order.getPreparationPriceInfo().getAmount();
									  l_Promo += l_Preparation;
									} else {
									  //trace.logWarning(this,".getRemise(Order a_Order) a_Order.getPreparationPriceInfo() == null");
                  					}
								}
								else
								{
									//trace.logWarning(this,".getRemise(Order a_Order) l_ShippingGroup.getPriceInfo() == null");
								}
							}
							else
							{
								//trace.logWarning(this,".getRemise(Order a_Order) m_ShippingCalculator == null");
							}
						}
						else
						{
							//trace.logWarning(this,".getRemise(Order a_Order) l_ShippingGroup == null");
						}
					}
					else
					{
						//trace.logWarning(this,".getRemise(Order a_Order) l_ShippingGroups == null");
					}

				}
				else
				{
					//trace.logWarning(this,".getRemise(Order a_Order) l_PriceInfo == null");
				}
			}
			else
			{
				//trace.logWarning(this,".getRemise(Order a_Order) l_ShoppingCart.getOrder() == null");
			}
		}catch(Exception e){
			//trace.logError(this,e,".getRemise(Order a_Order) Exception : "+e.toString());
			l_Promo=0.0;
		}finally{
			//trace.logClose(this,".getRemise(Order a_Order)");
		}
		return round(l_Promo);
	}
	



	/**
	* R�cup�ration de la remise du montant d'une commande s'il y en a.
	* Renvoie 0.0 si pas de remise
	* @param	String	OrderId
	* @return	double	Remise
	* @throws none
	*/	
	public double getRemiseMontant(String a_strOrderId)
	throws IOException,ServletException {
		//trace.logOpen(this,".getRemiseMontant(String a_strOrderId)");
		double l_dRemise = 0.00 ;
		
		try {
			Order l_Order = OrderManager.getOrderManager().loadOrder(a_strOrderId);
			l_dRemise = getRemiseMontant(l_Order);
		}catch(CommerceException e){
			//trace.logError(this,e,".getRemiseMontant(String a_strOrderId) CommerceException : "+e.toString());
		}catch(Exception e){
			//trace.logError(this,e,".getRemiseMontant(String a_strOrderId) Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".getRemiseMontant(String a_strOrderId)");
		}
		return l_dRemise ;
	}
	
	
	/**
	* R�cup�ration de la remise du montant d'une commande s'il y en a.
	* Renvoie 0.0 si pas de remise
	* @param	Order	a_Order
	* @return	double	Remise
	* @throws none
	*/	
	public double getRemiseMontant(Order a_Order)
	{
		//trace.logOpen(this,".getRemiseMontant(Order a_Order)");
		double l_Promo = 0.0;
		try
		{
			if (a_Order != null)
			{
				CastoOrder l_Order  = (CastoOrder)a_Order;
				atg.commerce.pricing.OrderPriceInfo l_PriceInfo = a_Order.getPriceInfo();
				if ( l_PriceInfo != null)
				{
					double l_Shipping = 0.0;
					double l_Preparation = 0.0;
					List l_ShippingGroups = a_Order.getShippingGroups();
					if (l_ShippingGroups != null)
					{
						HardgoodShippingGroup l_ShippingGroup = (HardgoodShippingGroup) l_ShippingGroups.get(0);
						if (l_ShippingGroup != null)
						{
							if (m_CastoShippingCalculator != null)
							{
								if(l_ShippingGroup.getPriceInfo()!=null)
								{
									l_Shipping = l_ShippingGroup.getPriceInfo().getAmount();
									l_Promo = l_PriceInfo.getRawSubtotal() + l_Shipping - l_PriceInfo.getTotal();
									if (l_Order.getPreparationPriceInfo()!=null) {
									  l_Preparation = l_Order.getPreparationPriceInfo().getAmount();
									  l_Promo += l_Preparation;
									} else {
									  //trace.logWarning(this,".getRemiseMontant(Order a_Order) a_Order.getPreparationPriceInfo() == null");
	              					}
								}
								else
								{
									//trace.logWarning(this,".getRemiseMontant(Order a_Order) l_ShippingGroup.getPriceInfo() == null");
								}
							}
							else
							{
								//trace.logWarning(this,".getRemiseMontant(Order a_Order) m_ShippingCalculator == null");
							}
						}
						else
						{
							//trace.logWarning(this,".getRemiseMontant(Order a_Order) l_ShippingGroup == null");
						}
					}								
								
				}
				else
				{
					//trace.logWarning(this,".getRemiseMontant(Order a_Order) l_PriceInfo == null");
				}
			}
			else
			{
				//trace.logWarning(this,".getRemiseMontant(Order a_Order) l_ShoppingCart.getOrder() == null");
			}
		}catch(Exception e){
			//trace.logError(this,e,".getRemiseMontant(Order a_Order) Exception : "+e.toString());
			l_Promo=0.0;
		}finally{
			//trace.logClose(this,".getRemiseMontant(Order a_Order)");
		}
		return round(l_Promo);
	}






	
	/**
	* Arrondi d'un double.
	* @param	double pNumber
	* @return	double	nombre arrondi
	* @throws none
	*/
	private static double round (double pNumber) {
		//trace.logOpen("castorama.BeanGetRemise.round");
		double l_result=0.0;
		try{
			l_result=(new Long(Math.round(pNumber * (Math.pow(10, 2))))).doubleValue() / Math.pow(10, 2);
		}catch(Exception e){
			//trace.logError(e,"castorama.BeanGetRemise.round Exception : "+e.toString());
			l_result=0.0;
		}finally{
			//trace.logClose("castorama.BeanGetRemise.round");
		}
		return l_result;
	}
	

}// fin de classe

