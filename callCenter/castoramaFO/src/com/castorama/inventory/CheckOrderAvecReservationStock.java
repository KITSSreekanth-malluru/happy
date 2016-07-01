package com.castorama.inventory;

import java.io.*;
import javax.servlet.*;
import atg.servlet.*;
import java.util.*;

import atg.nucleus.Nucleus;
import atg.repository.*;
import atg.commerce.catalog.*;
import atg.commerce.inventory.*;
import atg.commerce.order.*;
import atg.commerce.CommerceException;
import atg.commerce.states.OrderStates;




/**
* CheckOrderAvecReservationStock : Castorama 2003 <br>
* Cette droplet permet de tester si la commande contient au moins un article contraint � une limite de stock.<br>
* Utilis�e notamment pour d�sactiver le paiement par ch�que<br>
* @version 1.0  
* @author Damien DURIEZ - LOGICACMG (JUIN 2003) 
*/
public class CheckOrderAvecReservationStock extends DynamoServlet{
	
	
	public final static String	TRUE	=	"true";
	public final static String	FALSE	=	"false";
	public final static String	ORDER	=	"order";

    private InventoryTools m_inventoryTools;
	
	/**
	* Service<br>
	* @param	DynamoHttpServletRequest
	* @param	DynamoHttpServletResponse
	* @return	none
	* @throws	ServletException
    * @throws   IOException
	*/
	public void service(DynamoHttpServletRequest a_Request,
	                    DynamoHttpServletResponse a_Response)
	     throws ServletException, IOException{
		try{
			Object l_Obj = a_Request.getObjectParameter(ORDER);
			Order l_Order = null;
			if(l_Obj instanceof RepositoryItem)
			{
				l_Order = OrderManager.getOrderManager().loadOrder((String)((RepositoryItem)l_Obj).getRepositoryId());
			}
			else
			{
				l_Order = (Order)l_Obj;
			}
			if(l_Order!=null){
				if(getInventoryTools().checkOrderAvecReservationStock(l_Order)){
					a_Request.serviceLocalParameter(TRUE, a_Request, a_Response);
				}else{
					a_Request.serviceLocalParameter(FALSE, a_Request, a_Response);
				}
			}
		}catch(Exception e){
			//Trace.logError(this,e,".service : "+e.toString());
		}
	}
	
    public InventoryTools getInventoryTools()
    {
        return m_inventoryTools;
    }
    
    public void setInventoryTools(InventoryTools a_inventoryTools)
    {
        
        m_inventoryTools = a_inventoryTools;
    }
	
}// fin de class
