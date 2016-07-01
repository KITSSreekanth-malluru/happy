package com.castorama;

import java.util.*;
import atg.repository.*;

import com.castorama.config.*;

public class BeanSetDateOrder
{
	
	public Repository m_OrderRepository;
	public void setOrderRepository (Repository a_OrderRepository) {
		m_OrderRepository = a_OrderRepository;
	}
	public Repository getOrderRepository () {
		return m_OrderRepository;
	}
	
	public String  m_OrderID="";
	public void setOrderID (String a_OrderID) {
		m_OrderID = a_OrderID;
	}
	public String getOrderID () 
	{ 
		if (m_OrderID == null) 
            {
            return "";
            }
		return m_OrderID; 
	}
	
	String m_test="";
	
	public void setDateOrder(String a_strTest)
	{
		//Trace.logOpen(this,".setDateOrder");
		
		try{

			if (getOrderID() != null && !getOrderID().equals(""))
			{
				Configuration.getConfiguration().getClientLockManager().acquireWriteLock(getOrderID());
							
				MutableRepository OrderRepository = (MutableRepository) getOrderRepository();
				MutableRepositoryItem l_order = (MutableRepositoryItem) OrderRepository.getItemForUpdate(getOrderID(), "order");
				synchronized(l_order){
					if (l_order != null)
					{
						if (a_strTest.equals("suppression"))
						{
							Calendar l_Calendar = Calendar.getInstance();
							java.sql.Date l_Today = new java.sql.Date(l_Calendar.getTime().getTime());
						
							l_order.setPropertyValue("datesuppression", l_Today);
						}
						l_order.setPropertyValue("exportdate", null);
						OrderRepository.updateItem(l_order);
					}
				}
			}
		}catch(Exception e){
			//Trace.logError(this,e,".setDateOrder Exception : "+e.toString());
		}finally{
			try{
				Configuration.getConfiguration().getClientLockManager().releaseWriteLock(getOrderID());
			}catch(Exception e) {
				//Trace.logError(this,e,".setDateOrder orderId="+getOrderID()+" : "+e);
			}
			//Trace.logClose(this,".setDateOrder");
		}
	}
	
	public String getDateSuppression() {
		return null;
	}

}
