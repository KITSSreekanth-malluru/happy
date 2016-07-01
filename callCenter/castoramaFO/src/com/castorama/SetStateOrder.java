package com.castorama;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.castorama.config.Configuration;

public class SetStateOrder extends DynamoServlet
{
	public Repository m_OrderRepository = null;
	public void setOrderRepository (Repository a_OrderRepository) {

		m_OrderRepository = a_OrderRepository;

	}
	public Repository getOrderRepository() {

		return m_OrderRepository;
	}
	
	public void service(DynamoHttpServletRequest a_Request, DynamoHttpServletResponse a_Response)
	     throws ServletException, IOException
	{
		//Trace.logOpen(this,".service");
		String l_strOrderID				= a_Request.getParameter("ID");
		String l_strOrderState			= a_Request.getParameter("State");
		String l_strOrderStateNum		= a_Request.getParameter("StateNum");
		String l_strOrderStateDetail	= a_Request.getParameter("Detail");
			
		Configuration	l_Configuration		= Configuration.getConfiguration();
		try{
			
			if (l_strOrderID != null){
				
				l_Configuration.getClientLockManager().acquireWriteLock(l_strOrderID);
					
				MutableRepository		l_OrderRepository	= (MutableRepository) getOrderRepository();
				MutableRepositoryItem	l_Order				= l_OrderRepository.getItemForUpdate(l_strOrderID, "order");
				
				synchronized(l_Order){
					if (l_strOrderState != null){
						l_Order.setPropertyValue("BOState", l_strOrderState);
					}
					
					if (l_strOrderStateNum != null){
						try {
							int stateNim = Integer.valueOf(l_strOrderStateNum);
							l_Order.setPropertyValue("BOStateNum", stateNim);
						} catch (NumberFormatException e) {
							if(isLoggingError()) {
								logError(e);
							}
						}
					}
			
					if (l_strOrderStateDetail != null){
						l_Order.setPropertyValue("BOStateDetail", l_strOrderStateDetail);
					}
					
					l_OrderRepository.updateItem(l_Order);
				}
			}
			
			a_Request.setParameter("element", "true");
			a_Request.serviceParameter("OUTPUT",a_Request,a_Response);
		}catch(Exception e){
			//Trace.logError(this,".service orderId="+l_strOrderID+" : "+e.toString());
		}finally{
			try{
				l_Configuration.getClientLockManager().releaseWriteLock(l_strOrderID);
			}catch(Exception e) {
			//	Trace.logError(this,e,".service orderId="+l_strOrderID+" : "+e);
			}
		//	Trace.logClose(this,".service");
		}
	}


}
