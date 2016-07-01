package com.castorama;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.transaction.Transaction;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.CreditCard;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.ItemAddedToOrder;
import atg.commerce.order.Order;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.OrderManager;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.RepositoryContactInfo;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.SoftGoodFormHandler;
import atg.core.util.Address;

import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.email.EmailListener;
import atg.service.pipeline.PipelineResult;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.castorama.bean.BeanGetRemise;
import com.castorama.config.Configuration;
import com.castorama.emailing.MailInfoDeCommande;
import com.castorama.inventory.InventoryTools;
import com.castorama.panier.infosArticles.ArticleInfo;
import com.castorama.utils.CheckingTools;
import com.castorama.utils.MailTools;
import com.castorama.utils.ServletTools;
import com.castorama.xml.XmlDocument;

public class CastoShoppingCartModifier extends SoftGoodFormHandler
{	

	public final static String	TDUID = "tduid";

	protected String	m_strMailCarteXslTemplateURL;		// URL du template xsl pour le mail de confirmation de commande par carte
	protected String	m_strMailChequeXslTemplateURL;		// URL du template xsl pour le mail de confirmation de commande par cheque
	protected String	m_strMailCallCenterXslTemplateURL;	// URL du template xsl pour le mail de confirmation de commande par callCenter
	protected String	m_strMailAtoutXslTemplateURL;		// URL du template xsl pour le mail de confirmation de commande par atout
	protected String	m_strMailFaxXslTemplateURL;			// URL du template xsl pour le mail de confirmation de commande par fax
	private InventoryTools m_inventoryTools;
    
		protected String	m_strBOState;						// �tat de commande back office (non atg)
	protected String	m_strBOStateDetail;					// description de l'�tat de commande back office (non atg)
	protected MailInfoDeCommande	m_MailInfoDeCommande;	// Composant charg� de cr�er des vues XML de commandes pour les mails
	
	/**
	* R�cup�ration du MailInfoDeCommande
	* @param none
	* @return MailInfoDeCommande MailInfoDeCommande
	* @throws none
	*/
	public MailInfoDeCommande getMailInfoDeCommande(){
		return m_MailInfoDeCommande;
	}
	
	/**
	* Modification du MailInfoDeCommande
	* @param MailInfoDeCommande MailInfoDeCommande
	* @return none
	* @throws none
	*/
	public void setMailInfoDeCommande(MailInfoDeCommande a_MailInfoDeCommande){
		m_MailInfoDeCommande = a_MailInfoDeCommande ;
	}
	
	
	/**
	* R�cup�ration du BOState
	* @param none
	* @return String BOState
	* @throws none
	*/
	public String getBOState(){
		return m_strBOState;
	}
	
	/**
	* Modification du BOState
	* @param String BOState
	* @return none
	* @throws none
	*/
	public void   setBOState(String a_strBOState){
		m_strBOState = a_strBOState ;
	}
	
	
	/**
	* R�cup�ration du BOStateDetail
	* @param none
	* @return String BOStateDetail
	* @throws none
	*/
	public String getBOStateDetail(){
		return m_strBOStateDetail;
	}
	
	/**
	* Modification du BOStateDetail
	* @param String BOStateDetail
	* @return none
	* @throws none
	*/
	public void   setBOStateDetail(String a_strBOStateDetail){
		m_strBOStateDetail = a_strBOStateDetail ;
	}
	
	
	
	Order mLastOrder;
	/**
   * Returns the last order that was committed.
   * @beaninfo description: The last order that was committed
   */
  	public Order getLastOrder() {
    	return mLastOrder;
  	}
  	
    public InventoryTools getInventoryTools()
    {
        return m_inventoryTools;
    }
    
    public void setInventoryTools(InventoryTools a_inventoryTools)
    {
        
        m_inventoryTools = a_inventoryTools;
    }
	
	/**
	* R�cup�ration du MailCarteXslTemplateURL
	* @param none
	* @return String MailCarteXslTemplateURL
	* @throws none
	*/
	public String getMailCarteXslTemplateURL(){
		return m_strMailCarteXslTemplateURL;
	}
	
	/**
	* Modification du MailCarteXslTemplateURL
	* @param String MailCarteXslTemplateURL
	* @return none
	* @throws none
	*/
	public void   setMailCarteXslTemplateURL(String a_strMailCarteXslTemplateURL){
		m_strMailCarteXslTemplateURL = a_strMailCarteXslTemplateURL ;
	}
	
	
	/**
	* R�cup�ration du MailChequeXslTemplateURL
	* @param none
	* @return String MailChequeXslTemplateURL
	* @throws none
	*/
	public String getMailChequeXslTemplateURL(){
		return m_strMailChequeXslTemplateURL;
	}
	
	/**
	* Modification du MailChequeXslTemplateURL
	* @param String MailChequeXslTemplateURL
	* @return none
	* @throws none
	*/
	public void   setMailChequeXslTemplateURL(String a_strMailChequeXslTemplateURL){
		m_strMailChequeXslTemplateURL = a_strMailChequeXslTemplateURL ;
	}
	
	/**
	* R�cup�ration du MailCallCenterXslTemplateURL
	* @param none
	* @return String MailXslTemplateURL
	* @throws none
	*/
	public String getMailCallCenterXslTemplateURL(){
		return m_strMailCallCenterXslTemplateURL;
	}
	
	/**
	* Modification du MailCallCenterXslTemplateURL
	* @param String MailCallCenterXslTemplateURL
	* @return none
	* @throws none
	*/
	public void   setMailCallCenterXslTemplateURL(String a_strMailCallCenterXslTemplateURL){
		m_strMailCallCenterXslTemplateURL = a_strMailCallCenterXslTemplateURL ;
	}
	
	/**
	* R�cup�ration du MailAtoutXslTemplateURL
	* @param none
	* @return String MailAtoutXslTemplateURL
	* @throws none
	*/
	public String getMailAtoutXslTemplateURL(){
		return m_strMailAtoutXslTemplateURL;
	}
	
	/**
	* Modification du MailAtoutXslTemplateURL
	* @param String MailAtoutXslTemplateURL
	* @return none
	* @throws none
	*/
	public void   setMailAtoutXslTemplateURL(String a_strMailAtoutXslTemplateURL){
		m_strMailAtoutXslTemplateURL = a_strMailAtoutXslTemplateURL ;
	}
	
	
	/**
	* R�cup�ration du MailFaxXslTemplateURL
	* @param none
	* @return String MailFaxXslTemplateURL
	* @throws none
	*/
	public String getMailFaxXslTemplateURL(){
		return m_strMailFaxXslTemplateURL;
	}
	
	/**
	* Modification du MailFaxXslTemplateURL
	* @param String MailFaxXslTemplateURL
	* @return none
	* @throws none
	*/
	public void   setMailFaxXslTemplateURL(String a_strMailFaxXslTemplateURL){
		m_strMailFaxXslTemplateURL = a_strMailFaxXslTemplateURL ;
	}
	
	
	boolean m_bUseACoupon;
	public boolean getUseACoupon()
	{
		return m_bUseACoupon;
	}
	public void setUseACoupon(boolean a_bUseACoupon)
	{
		m_bUseACoupon=a_bUseACoupon;
	}
	
	
	boolean mCopyShippingAddrToBillingAddr = false;
    public void setCopyShippingAddrToBillingAddr(boolean pCopyShippingAddrToBillingAddr)
    {
    	mCopyShippingAddrToBillingAddr = pCopyShippingAddrToBillingAddr;
    }	
	public boolean getCopyShippingAddrToBillingAddr()
	{
		return mCopyShippingAddrToBillingAddr;
	}
	
	
	String m_strTypePaiement=null;
	public void setTypePaiement (String a_strTypePaiement) 
	{ 
		m_strTypePaiement = a_strTypePaiement; 
	}
	public String getTypePaiement ()
	{
		return m_strTypePaiement;
	}
	
	
	Repository m_CatalogueRepository;
	public void setCatalogueRepository (Repository a_CatalogueRepository)
	{
		m_CatalogueRepository = a_CatalogueRepository;
	}
	public Repository getCatalogueRepository ()
	{
		return m_CatalogueRepository;
	}

	
	String m_strIndexValue=null;
	public void setIndexValue (String a_strIndexValue)
	{
		m_strIndexValue = a_strIndexValue;
	}
	public String getIndexValue ()
	{
		return m_strIndexValue;
	}
	
	
	private boolean m_bAddItemToOrder;
	public void setAddItemToOrder(boolean a_bAddItemToOrder)
	{
		m_bAddItemToOrder = a_bAddItemToOrder;
	}
	public boolean getAddItemToOrder()
	{
		return m_bAddItemToOrder;	
	}
	
	
	private Repository m_RepositoryCoupons;
	public void setCouponsRepository(Repository a_RepositoryCoupons)
	{
	  	m_RepositoryCoupons = a_RepositoryCoupons;
	}
	public Repository getCouponsRepository()
	{
	  return m_RepositoryCoupons;
	}
	
	
	private SessionCoupons m_SessionCoupons;
	public SessionCoupons getSessionCoupons()
	{
		return m_SessionCoupons;
	}	
	public void setSessionCoupons(SessionCoupons a_SessionCoupons)
	{
		m_SessionCoupons=a_SessionCoupons;
	}
	

	protected BeanGetRemise m_BeanGetRemise;
	public BeanGetRemise getBeanGetRemise()
	{
		return m_BeanGetRemise;
	}	
	public void setBeanGetRemise(BeanGetRemise a_BeanGetRemise)
	{
		m_BeanGetRemise=a_BeanGetRemise;
	}

	
	protected BeanPartenaires m_BeanPartenaires;
	public BeanPartenaires getBeanPartenaires()
	{
		return m_BeanPartenaires;
	}	
	public void setBeanPartenaires(BeanPartenaires a_BeanPartenaires)
	{
		m_BeanPartenaires=a_BeanPartenaires;
	}
	

	protected Configuration m_ConfigurationCasto ; // Param�tre g�n�raux du site
	public Configuration getConfigurationCasto()
	{
		return m_ConfigurationCasto;
	}
	public void setConfigurationCasto(Configuration a_ConfigurationCasto) {
		m_ConfigurationCasto = a_ConfigurationCasto;
	}


	protected boolean m_bCopyAdressesIntoProfile ;	// Faut-il copier les adresses du shoppingcart dans le profil ?
	public boolean getCopyAdressesIntoProfile()
	{
		return m_bCopyAdressesIntoProfile;
	}
	public void setCopyAdressesIntoProfile(boolean a_bCopyAdressesIntoProfile)
	{
		m_bCopyAdressesIntoProfile = a_bCopyAdressesIntoProfile;
	}

	
	protected int m_nNumeroAdresseLivraison ;		// Le num�ro de l'adresse de livraison �dit�e: l'adresse de liv courante doit �craser l'adresse de livraison point�e par ce num�ro dans le profil
	public int getNumeroAdresseLivraison()
	{
		return m_nNumeroAdresseLivraison;
	}
	public void setNumeroAdresseLivraison(int a_nNumeroAdresseLivraison)
	{
		//trace.logOpen(this,".setNumeroAdresseLivraison("+a_nNumeroAdresseLivraison+")");
		m_nNumeroAdresseLivraison = a_nNumeroAdresseLivraison;
		//trace.logClose(this,".setNumeroAdresseLivraison("+m_nNumeroAdresseLivraison+")");
	}
	
	
	boolean mCopyAddrToProfile = false;
	public void setCopyAddrToProfile(boolean pCopyAddrToProfile) 
	{
		mCopyAddrToProfile = pCopyAddrToProfile;
	}
	public boolean getCopyAddrToProfile() 
	{
		return mCopyAddrToProfile;
	}

	
	private double m_ValeurEnFrancs;
	public void setValeurEnFrancs(double a_ValeurEnFrancs)
	{
		m_ValeurEnFrancs = a_ValeurEnFrancs;
	}
	public double getValeurEnEuros()
	{
		return m_ValeurEnFrancs/6.55957;	
	}
	
	EmailListener m_EmailListener;
	public EmailListener getEmailListener()
	{
		return m_EmailListener;
	}
	public void setEmailListener(EmailListener a_EmailListener)
	{
		m_EmailListener = a_EmailListener;
	}
	
	
	public Repository m_SIPSRepository = null;
	public void setSIPSRepository(Repository a_SIPSRepository)
	{
		m_SIPSRepository = a_SIPSRepository;
	}
	public Repository getSIPSRepository()
	{
		return m_SIPSRepository;
	}


	String strCommentaire;
	public String getCommentaire()
	{
		return strCommentaire;
	}
	public String getRecupCommentaire() {
		if (getOrder() != null)
		{
			strCommentaire=(String)((OrderImpl)getOrder()).getPropertyValue("Commentaire");
		}
		return strCommentaire;
	}
	public void setCommentaire (String a_strCommentaire) 
	{
		strCommentaire = a_strCommentaire;
	}
	
	
	String strMessageTransporteur;
	public String getMessageTransporteur()
	{
		return strMessageTransporteur;
	}
	public String getRecupMessageTransporteur() {
		if (getOrder() != null)
		{
			strMessageTransporteur=(String)((OrderImpl)getOrder()).getPropertyValue("messageTransporteur");
		}
		return strMessageTransporteur;
	}
	public void setMessageTransporteur (String a_strMessageTransporteur) 
	{
		strMessageTransporteur = a_strMessageTransporteur;
	}


	private String m_strSupprimeCommerceItem;	
	public String getSupprimeCommerceItem()
	{
		return m_strSupprimeCommerceItem;
	}
	public void setSupprimeCommerceItem(String a_strSupprimeCommerceItem)
	{
		//trace.logOpen(this,".setSupprimeCommerceItem");
		try
		{
			Order l_order = this.getOrder();
			OrderManager l_orderManager = this.getOrderManager();
			l_orderManager.getCommerceItemManager().removeItemFromOrder(l_order,a_strSupprimeCommerceItem);
		}catch(Exception e){
			//trace.logError(this,e,".setSupprimeCommerceItem Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".setSupprimeCommerceItem");
		}
	}	

	private int m_Quantite = 0;
	public String getCalculQuantite()
	{
		calculQuantite();
		return "";
	}

 	public int getQuantite()
	{
		return m_Quantite;
	}	

	private void resetListeCoupons()
	{
		//trace.logOpen(this,".calculFraisPortExceptionnel");
		try
		{
			if (m_SessionCoupons != null)
			{
				m_SessionCoupons.resetListeCoupons();
			}
		}catch(Exception e){
			//trace.logError(this,e,".resetListeCoupons Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".resetListeCoupons");
		}
	}

	private void traceReferer(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
	{
		//trace.logOpen(this,".calculFraisPortExceptionnel");
		try 
		{
			Order l_LastOrder = getLastOrder();
			if (l_LastOrder != null)
			{
				String l_OrderId = l_LastOrder.getId();
				double l_Montant = 0.0;
				if (l_LastOrder.getPriceInfo() != null)
				{
					l_Montant = l_LastOrder.getPriceInfo().getTotal();
					if (m_BeanPartenaires != null)
					{
						String l_strMontant = FormatNumber(new Double(l_Montant));
						m_BeanPartenaires.traceCommande(pRequest,pResponse,l_OrderId,l_strMontant);
					}
				}
			}
		}catch(Exception e){
			//trace.logError(this,e,".//traceReferer Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".//traceReferer");
		}
	}
	
	private void sauvegardeCodesCoupons()
	{
		//trace.logOpen(this,".sauvegardeCodesCoupons");
		try
		{
			if (m_SessionCoupons != null)
			{
				Vector l_EnsembleCodeCoupons = m_SessionCoupons.getListeCoupons();
				// Memorissation des codes coupons utilis�s
				if (l_EnsembleCodeCoupons != null)
				{ 	
					double l_dRemise = 0.0;
					try{
						Order l_LastOrder = getLastOrder();
						l_dRemise = m_BeanGetRemise.getRemise(l_LastOrder);
					}catch(Exception e){
						//trace.logError(this,e,".sauvegardeCodesCoupons Exception : "+e.toString());
					}
					if (l_EnsembleCodeCoupons.size() > 0 && l_dRemise>0)
					{
						MutableRepository l_CouponsRepository = (MutableRepository) getCouponsRepository();	
						String l_OrderId = getOrderId();
						for (int i=0;i<l_EnsembleCodeCoupons.size();i++)
						{
							String l_CodeCoupon = (String) l_EnsembleCodeCoupons.elementAt(i);
							//trace.logInfo(this,".sauvegardeCodesCoupons Sauvegarde du code coupon : " + l_CodeCoupon + " pour la commande : " + l_OrderId);
							MutableRepositoryItem l_CouponItem = l_CouponsRepository.createItem("casto_coupons");
							l_CouponItem.setPropertyValue("code_coupon",l_CodeCoupon);
							l_CouponItem.setPropertyValue("order_id",l_OrderId);
							l_CouponsRepository.updateItem(l_CouponItem);
							l_CouponItem = (MutableRepositoryItem)l_CouponsRepository.addItem(l_CouponItem);
						}
					}
					// Retrait de tous les coupons
					m_SessionCoupons.resetListeCoupons();
				}
				else
				{
					//trace.logWarning(this,".sauvegardeCodesCoupons m_SessionCoupons.getListeCoupons() == null");
				}
			}
		}catch(Exception e){
			//trace.logError(this,e,".sauvegardeCodesCoupons Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".sauvegardeCodesCoupons");
		}
	}
			

	public CastoShoppingCartModifier() 
	{
		super();
	}
	

	
	/**************************************************************************************************************/
	/**************************************************************************************************************/
	/**************************************************************************************************************/
	
	public void copyShippingAddrToBillingAddr(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
	{
		
		//trace.logOpen(this,".copyShippingAddrToBillingAddr");
		try 
		{
			ShippingGroup sg = getShippingGroup();
			if (sg == null) 
			{
				String msg = formatUserMessage(MSG_UNABLE_TO_COPY_ADDRESS, pRequest, pResponse);
				addFormException(new DropletException(msg, MSG_UNABLE_TO_COPY_ADDRESS));
				return;
			}

			PaymentGroup pg = getPaymentGroup();
			if (pg == null) 
			{
				String msg = formatUserMessage(MSG_UNABLE_TO_COPY_ADDRESS, pRequest, pResponse);
				addFormException(new DropletException(msg, MSG_UNABLE_TO_COPY_ADDRESS));
				return;
			}
      
			HardgoodShippingGroup hgShippingGroup = (HardgoodShippingGroup) sg;
			CreditCard ccBillingGroup = (CreditCard) pg;

			Address billingAddress = ccBillingGroup.getBillingAddress();
			Address shippingAddress = hgShippingGroup.getShippingAddress();
			if(billingAddress!=null && shippingAddress!=null)
			{
				getOrderManager().getOrderTools().copyAddress(shippingAddress, billingAddress);
			}
			else
			{
				if(billingAddress==null && shippingAddress==null)
				{
					CommerceException e = new CommerceException("Les adresses de facturation et de livraison sont null");
					throw e;
				}
				else if(billingAddress!=null)
				{
					CommerceException e = new CommerceException("L'adresse de facturation est null");
				}
				else if(shippingAddress!=null)
				{
					CommerceException e = new CommerceException("L'adresse de livraison est null");
				}
			}
			
		} 
		catch (ClassCastException e) 
		{
			processException(e, MSG_UNABLE_TO_COPY_ADDRESS, pRequest, pResponse);
			//trace.logError(this,e,".copyShippingAddrToBillingAddr ClassCastException : "+e.toString());
		} 
		catch (CommerceException e) 
		{
			processException(e, MSG_UNABLE_TO_COPY_ADDRESS, pRequest, pResponse);
			//trace.logError(this,e,".copyShippingAddrToBillingAddr CommerceException : "+e.toString());
		}catch(Exception e){
			//trace.logError(this,e,".copyShippingAddrToBillingAddr Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".copyShippingAddrToBillingAddr");
		}
	}
	
	/**
	* V�rification de l'adresse de livraison
	* En cas d'erreur, g�n�re une FormException
	* @param none
	* @return boolean erreur True/False
	* @exception none
	*/
	public boolean checkShipping(){
		//trace.logOpen(this,".checkShipping");
		boolean l_bOk=false;
		try 
		{
			HardgoodShippingGroup l_ShippingGroup = (HardgoodShippingGroup) getShippingGroup();
			if(l_ShippingGroup!=null && !getRechercherVille()){
				Address l_ShippingAddress = l_ShippingGroup.getShippingAddress();
				if(l_ShippingAddress!=null){
					l_bOk= CheckingTools.checkCodePostal(l_ShippingAddress.getPostalCode(),"codePostalSyntaxError",this);
					if(l_ShippingAddress.getCity()==null || l_ShippingAddress.getCity().equals("RECHERCHEVILLE")){
						addFormException(new DropletException("RECHERCHEVILLEException","RECHERCHEVILLEException"));
						l_bOk = false;
					}
					for(Enumeration e=Configuration.getConfiguration().getCodePostalError().keys();e.hasMoreElements();)
					{
						String l_strKeys = (String)e.nextElement();
						if(l_ShippingAddress.getPostalCode().startsWith(l_strKeys))
						{
							l_bOk=false;
							addFormException(new DropletException((String)Configuration.getConfiguration().getCodePostalError().get(l_strKeys),"CodePostalLivraison"));
						}
						
					}
				}
			}
		}catch(Exception e){
			//trace.logError(this,e,".checkShipping Exception : "+e.toString());
			l_bOk=false;
		}finally{
			//trace.logClose(this,".checkShipping");
		}
		return l_bOk;
	}
	
	
	/**
	* V�rification du de l'adresse de facturation
	* En cas d'erreur, g�n�re une FormException
	* @param none
	* @return boolean erreur True/False
	* @exception none
	*/
	public boolean checkBilling(){
		//trace.logOpen(this,".checkBillingPostalCode");
		boolean l_bOk=false;
		try 
		{
			CreditCard l_BillingGroup = (CreditCard)  getPaymentGroup();
			if(l_BillingGroup!=null && !getRechercherVille()){
				Address l_BillingAddress = l_BillingGroup.getBillingAddress();
				if(l_BillingAddress!=null){
					l_bOk= CheckingTools.checkCodePostal(l_BillingAddress.getPostalCode(),"codePostalSyntaxError",this);
					if(l_BillingAddress.getCity()==null || l_BillingAddress.getCity().equals("RECHERCHEVILLE")){
						addFormException(new DropletException("RECHERCHEVILLEException","RECHERCHEVILLEException"));
						l_bOk = false;
					}
				}
			}
		}catch(Exception e){
			//trace.logError(this,e,".checkBillingPostalCode Exception : "+e.toString());
			l_bOk=false;
		}finally{
			//trace.logClose(this,".checkBillingPostalCode");
		}
		return l_bOk;
	}
	
	public void preMoveToConfirmation(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
	{
		//trace.logOpen(this,".preMoveToConfirmation");

		String l_strOrderId = null;
		try 
		{
			
			checkBilling();
			checkShipping();
			
			// see if we need to perform any copying of address object
			if (getCopyShippingAddrToBillingAddr()) 
			{
				if (isLoggingDebug()) 
                    {
                    logDebug("going to copy billing address to shipping address");
                    }
	      		copyShippingAddrToBillingAddr(pRequest, pResponse);
			}
			//trace.logDebug(this,".preMoveToConfirmation getCopyAddrToProfile()="+getCopyAddrToProfile());
			if (getCopyAddrToProfile()) 
			{
				CopyAddrToProfile(pRequest, pResponse, true, true);
			}
			if (getOrder() != null){
				l_strOrderId	= getOrder().getId();
				
				
				//getOrder().setDescription(getCommentaire());
				
				//DynamicBeans.setPropertyValue(getOrder(), "description", getCommentaire());
				((OrderImpl)getOrder()).setPropertyValue("Commentaire",getCommentaire());
				if(getCommentaire()!=null){
					((OrderImpl)getOrder()).setPropertyValue("commandecadeau",new Boolean(true));
				}
				((OrderImpl)getOrder()).setPropertyValue("messageTransporteur",getMessageTransporteur());
				
				
			}
				
		}catch(Exception e){
			//trace.logError(this,e,".preMoveToConfirmation orderId="+l_strOrderId+" : "+e.toString());
			throw new ServletException(e);
		}finally{
			//trace.logClose(this,".preMoveToConfirmation");
		}
	}
	
	public void postMoveToConfirmation(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
	{
		//trace.logOpen(this,".postMoveToConfirmation");
		try 
		{
			RepositoryItem Profile = getProfile();
			boolean bCopyBillingAddr = true;
			boolean bCopyShippingAddr = true;
			
			if (Profile != null)
			{
				if (m_bCopyAdressesIntoProfile)
				{
					CopyAddrToProfile(pRequest,pResponse, bCopyBillingAddr, bCopyShippingAddr);
				}
			}
		}catch(Exception e){
			//trace.logError(this,e,".postMoveToConfirmation Exception : "+e.toString());
			throw new ServletException(e);
		}finally{
			//trace.logClose(this,".postMoveToConfirmation");
		}
	}
	
	private boolean m_bRechercherVille = false;
	
	public boolean getRechercherVille()
	{
		return m_bRechercherVille;
	}
	
	public void setRechercheVille(boolean a_bRechercherVille)
	{
		m_bRechercherVille = a_bRechercherVille;
	}
	
	public boolean handleMoveToConfirmation(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
	{
		//trace.logOpen(this,".handleMoveToConfirmation");
		boolean l_bOk=true;
		Configuration	l_Configuration		= Configuration.getConfiguration();
		String l_strOrderId = getOrder().getId();
		try {

			l_Configuration.getClientLockManager().acquireWriteLock(l_strOrderId);
			
			if (!checkFormRedirect(null, getMoveToConfirmationErrorURL(), pRequest, pResponse))
                {
                return false;
                }
			  
			preMoveToConfirmation(pRequest, pResponse);
			  
			try 
			{
				//trace.logDebug(this,".handleMoveToConfirmation m_bCopyAdressesIntoProfile="+m_bCopyAdressesIntoProfile);
				//Run the appropriate Pipeline Chain:
				
				if(!getRechercherVille())
				{
					runProcessMoveToConfirmation(getOrder(),getUserPricingModels(),
				                                 getUserLocale(pRequest,pResponse),
				                                 getProfile(),null);
				}
			    
			//Save the updated Order in the repository:
			getOrderManager().updateOrder(getOrder());
			}
			catch (Exception e1) 
			{
				//trace.logError(this,e1,".handleMoveToConfirmation Exception : "+e1.toString());
				processException(e1, MSG_ERROR_MOVE_TO_CONFIRMATION, pRequest, pResponse);
				throw e1;
			}
			
			postMoveToConfirmation(pRequest, pResponse);
			
			l_bOk= checkFormRedirect (getMoveToConfirmationSuccessURL(), getMoveToConfirmationErrorURL(), pRequest, pResponse);

		}catch(Exception e2){
			//trace.logError(this,e2,".handleMoveToConfirmation orderId="+l_strOrderId+" : "+e2.toString());
			l_bOk=false;
		}finally{
			try{
				l_Configuration.getClientLockManager().releaseWriteLock(l_strOrderId);
			}catch(Exception e) {
				//trace.logError(this,e,".handleMoveToConfirmation orderId="+l_strOrderId+" : "+e);
			}
			//trace.logClose(this,".handleMoveToConfirmation l_bOk="+l_bOk);
		}
		return l_bOk;
	
	}
	

	
	public void CopyAddrToProfile (DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, boolean CopyBillingAddr, boolean CopyShippingAddr)
	throws ServletException, IOException
	{
		//trace.logOpen(this,".CopyAddrToProfile");
		try 
		{
			String l_strAdresseDeLivraison = "adresseLivraison1";
			if(m_nNumeroAdresseLivraison==2) 
                {
                l_strAdresseDeLivraison = "adresseLivraison2";
                }
			//trace.logDebug(this,".CopyAddrToProfile m_nNumeroAdresseLivraison="+m_nNumeroAdresseLivraison);
			//trace.logDebug(this,".CopyAddrToProfile l_strAdresseDeLivraison="+l_strAdresseDeLivraison);
			
			RepositoryItem Profile = getProfile();
			if (Profile != null)
			{
			
				String id = Profile.getRepositoryId();
			
				MutableRepository ProfileRepository = (MutableRepository) Profile.getRepository();
				MutableRepositoryItem ProfileItem = ProfileRepository.getItemForUpdate(id, "user");
			
				if (CopyShippingAddr)
				{
					ShippingGroup sg = getShippingGroup();
					if (sg == null) 
					{
						String msg = formatUserMessage(MSG_UNABLE_TO_COPY_ADDRESS, pRequest, pResponse);
						addFormException(new DropletException(msg, MSG_UNABLE_TO_COPY_ADDRESS));
						return;
					}
							
					HardgoodShippingGroup hgShippingGroup = (HardgoodShippingGroup) sg;
					Address shippingAddress = hgShippingGroup.getShippingAddress();
				
					if (shippingAddress != null)
					{
						MutableRepositoryItem AdresseLivraison = ProfileRepository.createItem("contactInfo");
						CopyContactInfoAddress ((RepositoryContactInfo) shippingAddress, AdresseLivraison);
						ProfileRepository.addItem(AdresseLivraison);
						ProfileItem.setPropertyValue (l_strAdresseDeLivraison, AdresseLivraison);
					}
				}
			
				if (CopyBillingAddr)
				{
					PaymentGroup pg = getPaymentGroup();
					if (pg == null) 
					{
						String msg = formatUserMessage(MSG_UNABLE_TO_COPY_ADDRESS, pRequest, pResponse);
						addFormException(new DropletException(msg, MSG_UNABLE_TO_COPY_ADDRESS));
						return;
					}
      					
					CreditCard ccBillingGroup = (CreditCard) pg;
					Address billingAddress = ccBillingGroup.getBillingAddress();
					
					if (billingAddress != null)
					{
						MutableRepositoryItem AdresseFacturation = ProfileRepository.createItem("contactInfo");
						CopyContactInfoAddress ((RepositoryContactInfo) billingAddress, AdresseFacturation);
						ProfileRepository.addItem(AdresseFacturation);
						ProfileItem.setPropertyValue ("adresseFacturation", AdresseFacturation);
					}
				}			

				ProfileRepository.updateItem(ProfileItem);
			}
		} 
		catch (ClassCastException e) 
		{
			//trace.logError(this,e,".CopyAddrToProfile ClassCastException : "+e.toString());
			processException(e, MSG_UNABLE_TO_COPY_ADDRESS, pRequest, pResponse);
		}
		catch (RepositoryException e)
		{
			//trace.logError(this,e,".CopyAddrToProfile RepositoryException : "+e.toString());
			processException(e, MSG_UNABLE_TO_COPY_ADDRESS, pRequest, pResponse);
		}catch(Exception e){
			//trace.logError(this,e,".CopyAddrToProfile Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".CopyAddrToProfile");
		}
	}	
	

	public void CopyContactInfoAddress (RepositoryContactInfo contactInfo, MutableRepositoryItem Addr)
	{
		//trace.logOpen(this,".CopyContactInfoAddress");
		try 
		{
			Addr.setPropertyValue ("civilite", contactInfo.getMiddleName());
			Addr.setPropertyValue ("lastName", contactInfo.getLastName());
			Addr.setPropertyValue ("firstName", contactInfo.getFirstName());
			Addr.setPropertyValue ("middleName", contactInfo.getMiddleName());
			Addr.setPropertyValue ("societe", contactInfo.getFaxNumber());
			Addr.setPropertyValue ("address1", contactInfo.getAddress1());
			Addr.setPropertyValue ("address2", contactInfo.getAddress2());
			Addr.setPropertyValue ("postalCode", contactInfo.getPostalCode());
			Addr.setPropertyValue ("city", contactInfo.getCity());
			Addr.setPropertyValue ("state", contactInfo.getState());
			Addr.setPropertyValue ("country", contactInfo.getCountry());
			Addr.setPropertyValue ("phoneNumber", contactInfo.getPhoneNumber());
			Addr.setPropertyValue ("faxNumber", contactInfo.getFaxNumber());
		}catch(Exception e){
			//trace.logError(this,e,".CopyContactInfoAddress Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".CopyContactInfoAddress");
		}
	}
	
	private void calculQuantite()
	{
		//trace.logOpen(this,".calculQuantite");
		m_Quantite = 0;
		try
		{
			if (this.getOrder() != null)
			{
				if (this.getOrder().getShippingGroups() != null)
				{
					List l_ListeDesShippingGroups = this.getOrder().getShippingGroups();
					ShippingGroup l_ShippingGroup;
			
					for (int i=0;i<l_ListeDesShippingGroups.size();i++)
					{
						l_ShippingGroup = (ShippingGroup)l_ListeDesShippingGroups.get(i);
						if (l_ShippingGroup != null)
						{
							List l_ListeDesArticles = l_ShippingGroup.getCommerceItemRelationships();
							CommerceItem l_Article;
							if (l_ListeDesArticles != null)
							{
								for (int j=0;j<l_ListeDesArticles.size();j++)
								{
									m_Quantite ++;	
								}
							}
						}
					}
				}
			}	
		}catch(Exception e){
			//trace.logError(this,e,".calculQuantite Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".calculQuantite");
		}
	}	
	//delete all items
	public boolean handleDeleteAllItems(DynamoHttpServletRequest pRequest,
                                           DynamoHttpServletResponse pResponse)
	throws IOException, ServletException
	{
		//trace.logOpen(this,".handleDeleteAllItems");
		boolean l_bOk=false;
		Configuration	l_Configuration		= Configuration.getConfiguration();
		String l_strOrderId = null;
		try
		{
			if(getOrder()!=null) 
                {
                l_strOrderId = getOrder().getId();
                }
			
			l_Configuration.getClientLockManager().acquireWriteLock(l_strOrderId);
			
			OrderManager l_orderManager = this.getOrderManager();
			l_orderManager.getCommerceItemManager().removeAllCommerceItemsFromOrder(this.getOrder());
			l_bOk=checkFormRedirect (getRemoveItemFromOrderSuccessURL(), getRemoveItemFromOrderErrorURL(), pRequest, pResponse);				
		
		}catch (Exception e){
		    processException(e, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
			//trace.logError(this,e,".handleDeleteAllItems orderId="+l_strOrderId+" : "+e.toString());
			l_bOk=false;
		}finally{
			try{
				l_Configuration.getClientLockManager().releaseWriteLock(l_strOrderId);
			}catch(Exception e) {
				//trace.logError(this,e,".handleDeleteAllItems orderId="+l_strOrderId+" : "+e);
			}
			//trace.logClose(this,".handleDeleteAllItems");
		}
		return true;
	}
	//delete item
	public boolean handleDeleteItem(DynamoHttpServletRequest pRequest,
                                           DynamoHttpServletResponse pResponse)
	throws IOException, ServletException
	{
		//trace.logOpen(this,".handleDeleteItem");
		boolean l_bOk=false;
		Configuration	l_Configuration		= Configuration.getConfiguration();
		String l_strOrderId = null;
		try {
			Order l_order = this.getOrder();
			if(l_order!=null)
                {
                l_strOrderId = l_order.getId();
                }
			
			l_Configuration.getClientLockManager().acquireWriteLock(l_strOrderId);
			
			//trace.logDebug(this,".handleDeleteItem DEBUT Supprimer un article du panier");
			try{
				int l_intIndex = Integer.parseInt(m_strIndexValue);
				
				List l_listCommerceId = l_order.getCommerceItems();
				CommerceItem l_CommerceItem = (CommerceItem)l_listCommerceId.get(l_intIndex);
				String l_strCommerceId = l_CommerceItem.getId();
				OrderManager l_orderManager = this.getOrderManager();
				l_orderManager.getCommerceItemManager().removeItemFromOrder(l_order,l_strCommerceId);
			}
			catch (Exception e1)
			{
				////trace.logError(this,e1,".handleDeleteItem Exception : "+e1.toString());
			    processException(e1, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
			    throw e1;
			}
			//trace.logDebug(this,".handleDeleteItem FIN Supprimer un article du panier");
			l_bOk=checkFormRedirect (getRemoveItemFromOrderSuccessURL(), getRemoveItemFromOrderErrorURL(), pRequest, pResponse);
			
		}catch(Exception e2){
			//trace.logError(this,e2,".handleDeleteItem Exception : "+e2.toString());
		}finally{
			try{
				l_Configuration.getClientLockManager().releaseWriteLock(l_strOrderId);
			}catch(Exception e) {
				//trace.logError(this,e,".handleDeleteItem orderId="+l_strOrderId+" : "+e);
			}
			//trace.logClose(this,".handleDeleteItem");
		}

		return true;
	}
	
	//delete item
	
		
	
	private void Envoyer_Mail (DynamoHttpServletRequest  a_Request, DynamoHttpServletResponse a_Response)
	{
		//trace.logOpen(this,".Envoyer_Mail");
		try
		{
		  	if(!getFormError()){
		  		
				RepositoryItem	Profile		= getProfile();
				Order			LastOrder	= getLastOrder();
				
				XmlDocument	l_XmlRepresentation = getMailInfoDeCommande().getOrderXmlView(LastOrder, getTypePaiement());	
                
				
				String		l_strXslTemplate = null;
				if (getTypePaiement() == null){
					l_strXslTemplate = m_strMailCarteXslTemplateURL;
				}
				else if(getTypePaiement().equals("CALL_CENTER")){
					l_strXslTemplate = m_strMailCallCenterXslTemplateURL;
				}
				else if(getTypePaiement().equals("CHEQUE")){
					l_strXslTemplate = m_strMailChequeXslTemplateURL;
				}
				else if(getTypePaiement().equals("ATOUT")){
					l_strXslTemplate = m_strMailAtoutXslTemplateURL;
				}
				else if(getTypePaiement().equals("FAX")){
					l_strXslTemplate = m_strMailFaxXslTemplateURL;
				}

				String l_strFrom="";
				if (getConfigurationCasto().getEmailFromOrderConfirmation() != null){
					l_strFrom = getConfigurationCasto().getEmailFromOrderConfirmation();
				}
				String l_strVpcMailDomaine			= getConfigurationCasto().getVpcMailDomaine();
				String l_strEmailTeleperformance	= getConfigurationCasto().getEmailTeleperformance();
			
				String l_strTo = Profile.getPropertyValue("login").toString();
				if(l_strTo.endsWith(l_strVpcMailDomaine))
                    {
                    l_strTo = l_strEmailTeleperformance;
                    }
				
				String l_strSubject = "=?iso-8859-1?Q?Confirmation de votre commande (n=BA " + LastOrder.getId() + ")?=";
				
				MailTools.sendXSLMail(l_strFrom,l_strTo,l_strSubject,l_strXslTemplate,l_XmlRepresentation.closeDocument());
				
				
			}
		}catch(Exception e){
			//trace.logError(this,e,".Envoyer_Mail Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".Envoyer_Mail");
		}
	
	}
	

	
	/**
   * This method is used to submit the order.  It will first call the preMoveToOrderCommit
   * method.  After this, it will ensure that the person is not trying to double submit the
   * order by seeing if the users lastOrder is is equal to the current order id.
   * If the order is okay to submit, the OrderManagers processOrder method will get called.
   * If there are no errors with processing the order, then the current order in the users
   * OrderHolder will get set to null and the submitted order will get assigned to the last
   * order property.

   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @throws ServletException if there was an error while executing the code
   * @throws IOException if there was an error with servlet io
   */
  public boolean superHandleMoveToOrderCommit(DynamoHttpServletRequest pRequest,
                                         DynamoHttpServletResponse pResponse)
    throws ServletException,
    IOException
  {
	
	//trace.logOpen(this,".superHandleMoveToOrderCommit");
		boolean l_bOk=false;
		Transaction tr = null;
		try {
			tr = ensureTransaction();
		  	if (!checkFormRedirect(null, getMoveToOrderCommitErrorURL(), pRequest, pResponse)){
		    	l_bOk=false;
		  	}else{
			  	preMoveToOrderCommit(pRequest, pResponse);
			  	// make sure they are not trying to double submit an order
			  	Order lastOrder = getShoppingCart().getLast();
			  	if (getOrderId() != null && lastOrder != null &&getOrderId().equals(lastOrder.getId())) {
			    	// invalid number given for quantity of item to add
			    	String msg = formatUserMessage(MSG_ORDER_ALREADY_SUBMITTED, pRequest, pResponse);
			    	addFormException(new DropletException(msg, MSG_ORDER_ALREADY_SUBMITTED));
			  	} else {
			   	 	try {
			      		synchronized (getOrder()) {
			        		Order order = getOrder();
			        		Locale userLocale = getUserLocale(pRequest,pResponse);            
			        		PipelineResult result = getOrderManager().processOrder(order, userLocale);
			        		if (! processPipelineErrors(result)) {
			          			mLastOrder = order;
			          			if (getShoppingCart() != null) {
			            			getShoppingCart().setLast(mLastOrder);
			            			getShoppingCart().setCurrent(null);
			          			}
			        		}
			      		}
			    	}catch (Exception e1) {
						//trace.logError(this,e1,".superHandleMoveToOrderCommit Exception : "+e1.toString());
			    		  processException(e1, MSG_ERROR_MOVE_TO_ORDER_COMMIT, pRequest, pResponse);
			   	 	}
				}
		  		//If NO form errors are found, redirect to the success URL.
		  		//If form errors are found, redirect to the error URL.
		  		postMoveToOrderCommit(pRequest, pResponse);
		  		l_bOk=checkFormRedirect (getMoveToOrderCommitSuccessURL(), getMoveToOrderCommitErrorURL(), pRequest, pResponse);
			}
		}catch(Exception e2){
			//trace.logError(this,e2,".superHandleMoveToOrderCommit Exception : "+e2.toString());
			l_bOk=false;
		}finally{
			if (tr != null)
            {
                commitTransaction(tr);
            }
			//trace.logClose(this,".superHandleMoveToOrderCommit");
		}
		
	return checkFormRedirect (getMoveToOrderCommitSuccessURL(), getMoveToOrderCommitErrorURL(), pRequest, pResponse);

  }	
  
	  	
  	
	public boolean handleMoveToOrderCommit(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
	{

		//trace.logOpen(this,".handleMoveToOrderCommit");
		boolean l_bresult=false;
		try{
		
			l_bresult  = superHandleMoveToOrderCommit(pRequest, pResponse);
			if (!getFormError())
			{
				Envoyer_Mail (pRequest, pResponse);
				sauvegardeCodesCoupons();
				traceReferer(pRequest,pResponse);
				setUseACoupon(false);
			}else{
				for (int i=0;i<getFormExceptions().size();i++) {
         			//trace.logError(this,".handleMoveToOrderCommit "+getFormExceptions().elementAt(i));
     			}
			}
		}catch(Exception e){
			//trace.logError(this,e,".handleMoveToOrderCommit Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".handleMoveToOrderCommit");
		}
		return l_bresult;
	}
	
	
	
	/**
	* preMoveToOrderCommit<br>
	* TRADE DOUBLER :R�cup�ration du TDUID en cookie ou en session et enregistrement si TDUID existe
	* @param	DynamoHttpServletRequest
	* @param	DynamoHttpServletResponse
	* @return	none
	* @throws	ServletException    erreur 
    * @throws   IOException         erreur
	*/
	public void preMoveToOrderCommit(	DynamoHttpServletRequest	a_Request,
	                    				DynamoHttpServletResponse	a_Response)
		throws ServletException, IOException{
		
		boolean l_bOk = true;
		try{
			String	l_strTDUID			= new CookieManager().getStringCookie(TDUID,a_Request, null);
			boolean	l_bTryToRemoveCookie	= false;
			if(l_strTDUID!=null){
				l_bTryToRemoveCookie = true;
			}else{
				l_strTDUID = (String) a_Request.getSession().getAttribute(TDUID);				
			}
			OrderImpl l_Order = (OrderImpl) getOrder();
			synchronized(l_Order){
				
				if(l_strTDUID!=null){
					
					l_Order.setPropertyValue(TDUID,l_strTDUID);
					/*
					* On tente de supprimer le cookie sur le navigateur mais nous avons conscience que le cookie ne sera pas
					* supprim� pour les paiement par Carte car le commit se fait sur une requ�te HTTP en provenance de SIPS
					*/
					if(l_bTryToRemoveCookie){
						Cookie l_Cookie = new Cookie("tduid",null);
						l_Cookie.setMaxAge(0);
						l_Cookie.setPath("/");
						a_Response.addCookie(l_Cookie);
					}
				}
				if(getBOState()!=null){
					//trace.logDebug("***************** getBOState()="+getBOState());
					l_Order.setPropertyValue("BOState",getBOState());
					l_Order.setPropertyValue("BOStateDetail",getBOStateDetail());
				}
			}
		}catch(Exception e){
			//trace.logError(this,e,".preMoveToOrderCommit "+e.toString());
			l_bOk = false;
		}
	}
	
	
	
	/**
	* preAddItemToOrder<br>
	* V�rification si le stock pour un article du panier n'est pas �puis� dans quel cas une formException est lev�e. 
	* @param	DynamoHttpServletRequest
	* @param	DynamoHttpServletResponse
	* @return	none
	* @throws	ServletException    erreur 
    * @throws   IOException         erreur
	*/
	/*public void preAddItemToOrder(	DynamoHttpServletRequest a_Request,
	                    			DynamoHttpServletResponse a_Response)
	     throws ServletException, IOException{
	     	
	     	String[]	l_SkuIds	= getCatalogRefIds();
	     	if(l_SkuIds!=null){
	     		for(int i=0; i<l_SkuIds.length; i++){
	     			int		l_nDisponibilite	= InventoryTools.getInstance().checkAvailability(l_SkuIds[i]);
	     			if(l_nDisponibilite==InventoryTools.EPUISE){
	     				addFormException(new DropletFormException("articleEpuise","articleEpuise",l_SkuIds[i]));
	     			}
	     		}
	     	}
	
	}*/
	
	
	/**
	* postAddItemToOrder<br>
	* V�rification si la quantit� pour un article ajout� au panier n'ex�de pas le stock disponible,<br>
	* dans quel cas la quantit� du panier pour cet article devient la quantit� disponible en stock et une formException est lev�e.<br>
	* De m�me si le stock est �puis� pour cet article, la ligne est retir�e du panier et une formExceptio est lev�e.
	* @param	DynamoHttpServletRequest
	* @param	DynamoHttpServletResponse
	* @return	none
	* @throws	ServletException       erreur
    * @throws   IOException            erreur
	*/
	public void postAddItemToOrder(	DynamoHttpServletRequest a_Request,
	                    			DynamoHttpServletResponse a_Response)
	     throws ServletException, IOException{
	     	
     	checkQuantitiesInOrder(a_Request, a_Response);
	}
	

	/**
	* les sku sp�cifi� par l'id en param�tre est-il en train d'�tre ajout� au panier ?
	* @param	String sku id
	* @return	boolean
	* @throws	none
	*/
	public boolean isCurrentlyBeingAdded(String a_strSkuId){
		String[] l_strSkuBeingAddedIds = getCatalogRefIds();
		for(int i=0; i<l_strSkuBeingAddedIds.length; i++){
			if(l_strSkuBeingAddedIds[i].equals(a_strSkuId)) 
                {
                return true;
                }
		}
		return false;
	}
	
	
	/**
	* V�rification si la quantit� pour un article du panier n'ex�de pas le stock disponible,<br>
	* dans quel cas la quantit� du panier pour cet article devient la quantit� disponible en stock et une formException est lev�e.<br>
	* De m�me si le stock est �puis� pour cet article, la ligne est retir�e du panier et une formExceptio est lev�e.<br>
	* Si getAddItemToOrder()==true, alors v�rification uniquement sur les articles qui son en train d'�tre ajout� au panier.
	* @param	DynamoHttpServletRequest
	* @param	DynamoHttpServletResponse
	* @return	none
	* @throws	none
	*/
	public void checkQuantitiesInOrder(	DynamoHttpServletRequest a_Request,
	                    				DynamoHttpServletResponse a_Response)
	           throws ServletException, IOException{
		try{
	     	boolean	l_bStockInsuffisantPourAuMoinsUnArticle	= false;
     		List	l_CommerceItemRelationships	= getShippingGroupCommerceItemRelationships(getOrder());
     		for(int i=0; i<l_CommerceItemRelationships.size(); i++){
     			CommerceItemRelationship	l_CommerceItemRelationship	= (CommerceItemRelationship)l_CommerceItemRelationships.get(i);
     			String						l_strSkuId					= l_CommerceItemRelationship.getCommerceItem().getCatalogRefId();
	     		long						l_lQuantity					= l_CommerceItemRelationship.getQuantity();	// quantit� de la'article dans le panier
	     		a_Request.setParameter(l_CommerceItemRelationship.getId(),new Long(l_lQuantity)); // ON GARDE LA MEME QUANTITE PAR DEFAUT
     			if(!getAddItemToOrder() || isCurrentlyBeingAdded(l_strSkuId)){	// Si l'article est en train d'�tre ajout� au panier, on test le stock
	     			
                    
                    
                    
                    long	l_lStockLevel		= getInventoryTools().getInventoryAdapter().getInventoryManager().queryStockLevel(l_strSkuId);	// quantit� du stock pour l'article
                    
	     			int		l_nDisponibilite	= getInventoryTools().checkAvailability(l_strSkuId);				// disponibilit� de l'article
     				
                    
                    
                    
                    if(l_lStockLevel>=0 && l_lQuantity>l_lStockLevel){ // le stock n'est pas infini et insuffisant
     					a_Request.setParameter(l_CommerceItemRelationship.getId(),new Long(l_lStockLevel)); // CHANGEMENT DE QUANTITY
     					l_bStockInsuffisantPourAuMoinsUnArticle = true;
     					if(l_nDisponibilite==InventoryTools.EPUISE){ // le stock est �puis�
     						addFormException(new DropletFormException(l_strSkuId,"articleEpuise","articleEpuise"));
     						if(getAddItemToOrderErrorURL()!=null)					
                                {
                                setAddItemToOrderErrorURL(ServletTools.addQueryString(getAddItemToOrderErrorURL(),l_strSkuId+"auMoinsUnArticleEpuise","true"));
                                }
     						else if(getSetOrderByRelationshipIdErrorURL()!=null)	
                                {
                                setSetOrderByRelationshipIdErrorURL(ServletTools.addQueryString(getSetOrderByRelationshipIdErrorURL(),l_strSkuId+"auMoinsUnArticleEpuise","true"));
                                }
     						else													
                                {
                                a_Request.setParameter("auMoinsUnArticleEpuise","true");
                                }
     					}else{ // le stock n'est pas �puis� mais insuffisant
     						addFormException(new DropletFormException(l_strSkuId,"quantiteSuperieureAuStock","quantiteSuperieureAuStock"));
     						if(getAddItemToOrderErrorURL()!=null)					
                                {
                                setAddItemToOrderErrorURL(ServletTools.addQueryString(getAddItemToOrderErrorURL(),l_strSkuId+"NewQuantityValue",String.valueOf(l_lStockLevel)));
                                }
     						else if(getSetOrderByRelationshipIdErrorURL()!=null)	
                                {
                                setSetOrderByRelationshipIdErrorURL(ServletTools.addQueryString(getSetOrderByRelationshipIdErrorURL(),l_strSkuId+"NewQuantityValue",String.valueOf(l_lStockLevel)));
                                }
     						else													
                                {
                                a_Request.setParameter(l_strSkuId+"NewQuantityValue", String.valueOf(l_lStockLevel));
                                }
     					}
     				}
     				
				}
     		}
	     	if(l_bStockInsuffisantPourAuMoinsUnArticle){ // EVENTUELLEMENT CHANGEMENT DE QUANTITE OU SUPPRESSION DE LIGNE
	     		setRemovalRelationshipIds(new String[0]);
	     		handleSetOrderByRelationshipId(a_Request, a_Response);	
	     	}
     	}catch(Exception e){
			//trace.logError(this,e,".checkQuantitiesInOrder :"+getOrder().getId()+" "+e.toString());
		}
	}
	
	
	
	public boolean handleAddItemToOrder (DynamoHttpServletRequest pRequest,
                                       DynamoHttpServletResponse pResponse)
    throws ServletException, 
    IOException
	{
		//trace.logOpen(this,".handleAddItemToOrder");
		boolean l_bOk=true;
		String l_strOrderId = null;
		Configuration	l_Configuration		= Configuration.getConfiguration();
		
		try{
			
			if(getOrder()!=null) 
                {
                l_strOrderId = getOrder().getId();
                }
			
			l_Configuration.getClientLockManager().acquireWriteLock(l_strOrderId);
			
  			synchronized(getOrder()){
				if (! super.checkFormRedirect(null, super.getAddItemToOrderErrorURL(), pRequest, pResponse)){
					l_bOk=false;
				}else{
					preAddItemToOrder(pRequest, pResponse);
			
					addItemToOrder(pRequest, pResponse, false);
			
					postAddItemToOrder(pRequest, pResponse);
					l_bOk= checkFormRedirect (super.getAddItemToOrderSuccessURL(), 
														super.getAddItemToOrderErrorURL(), 
														pRequest, 
														pResponse);
				}
			}
		}catch(Exception e2){
			//trace.logError(this,e2,".handleAddItemToOrder orderId="+l_strOrderId+" : "+e2.toString());
		}finally{
			try{
				l_Configuration.getClientLockManager().releaseWriteLock(l_strOrderId);
			}catch(Exception e) {
				//trace.logError(this,e,".handleAddItemToOrder orderId="+l_strOrderId+" : "+e);
			}
			//trace.logClose(this,".handleAddItemToOrder");
		}
		return l_bOk;
	}
	
	
	protected void addItemToOrder(DynamoHttpServletRequest pRequest,
							DynamoHttpServletResponse pResponse,
							boolean pIsGift)
								throws ServletException, IOException{
		//trace.logOpen(this,".addItemToOrder");
		//Fetch the order
		Order order = super.getOrder();
		if (order == null) {
			String msg = super.formatUserMessage(MSG_NO_ORDER_TO_MODIFY, pRequest, pResponse);
			throw new ServletException(msg);
		}
		String [] skuIds = super.getCatalogRefIds();
		if ((skuIds == null) || (skuIds.length == 0)) {
			String msg = super.formatUserMessage(MSG_NO_ITEMS_TO_ADD, pRequest, pResponse);
			String propertyPath = super.generatePropertyPath("catalogRefIds");
			addFormException(new DropletFormException(msg, propertyPath, MSG_NO_ITEMS_TO_ADD));
			//trace.logClose(this,".addItemToOrder");
			return;
		}
		ShippingGroup sg = super.getShippingGroup();
		if (sg == null) {
			String msg = super.formatUserMessage(MSG_ERROR_ADDING_ITEM, pRequest, pResponse);
			addFormException(new DropletFormException(msg, MSG_ERROR_ADDING_ITEM));
			//trace.logClose(this,".addItemToOrder");
			return;
		}
		try {
			synchronized (order) {
				for (int c=0; c<skuIds.length; c++) {
					long quantity = super.getQuantity(skuIds[c], pRequest, pResponse);
					if (quantity <= 0) {
						String msg = super.formatUserMessage(MSG_QUANTITY_LESSTHAN_OR_EQUALTO_ZERO, pRequest, pResponse);
						String propertyPath = super.generatePropertyPath("quantity");
						addFormException(new DropletFormException(msg, propertyPath, MSG_QUANTITY_LESSTHAN_OR_EQUALTO_ZERO));
						continue;
						//NOTE: Somewhere the QTY gets reset to 1.  Not sure we should show
						//an error about QTY being less then 0, and change the QTY. It
						//is confusing...
					}
					boolean l_b_valid1 = true;
					boolean l_b_valid2 = false;
					try{
						String l_str_prodId = super.getProductId();
						//trace.logDebug(this,".addItemToOrder prod_id : '"+l_str_prodId+"'");
						//-- Le prod_id doit etre # "" et # null
						//-- le sku_id doit appartenir au prod_id
						if (l_str_prodId==null)
                            {
                            l_b_valid1 = false;
                            }
						else{
							if (l_str_prodId.equals(""))
							{
                                l_b_valid1 = false;
                            }
						}
						if (l_b_valid1){
							RepositoryItem l_itemProduct;
							l_itemProduct = getCatalogueRepository().getItem(l_str_prodId, "product");
							List l_skus = (List) l_itemProduct.getPropertyValue("childSKUs");
							Iterator l_iterator = l_skus.iterator();
							while (l_iterator.hasNext()){
								RepositoryItem l_itemSku = (RepositoryItem)l_iterator.next();
								String l_str_skuId = (String) l_itemSku.getRepositoryId();
								if(skuIds[c].equals(l_str_skuId))
								    {
                                    l_b_valid2 = true ;
                                    }
							}
						}
					}catch(Exception e1){
						l_b_valid2=false;
						l_b_valid1=false;
						//trace.logError(this,e1,".addItemToOrder Exception : "+e1.toString());
					}
					if (l_b_valid1 && l_b_valid2){
						CommerceItem ci = super.getOrderManager().getCommerceItemManager().createCommerceItem(skuIds[c], super.getProductId(), quantity, super.getCatalogKey(pRequest, pResponse));
						ci = super.getOrderManager().getCommerceItemManager().addItemToOrder(super.getOrder(), ci);
						super.getOrderManager().getCommerceItemManager().addItemQuantityToShippingGroup(super.getOrder(), ci.getId(), 
						sg.getId(), quantity);
						// gifthandling
						if(pIsGift) {
							super.getGiftlistManager().addGiftToOrder(super.getProfile(), super.getOrder(), ci.getId(), 
							sg, quantity, super.getGiftlistId(), 
							super.getGiftlistItemId());
						}
						super.runProcessRepriceOrder(super.getAddItemToOrderPricingOp(), order, super.getUserPricingModels(), 
						super.getUserLocale(pRequest,pResponse),super.getProfile(),null);
						super.runProcessAddItemToOrder(order, null, super.getUserPricingModels(), super.getUserLocale(pRequest, pResponse), super.getProfile(), null);
						if (isTransactionMarkedAsRollBack()){
							//trace.logClose(this,".addItemToOrder");
							return;
						}
						super.runProcessSendScenarioEvent(order, ci, ItemAddedToOrder.TYPE);
					}else{
						//trace.logError(this,".addItemToOrder !! INCOHERENCE DETECTE !!");
						if (!l_b_valid1) 
                        {
                            //trace.logError(this,".addItemToOrder Le product est vide ou null");
                        }
						else
                        {
							if (!l_b_valid2) 
                            {
                                //trace.logError(this,".addItemToOrder Le sku ne fait pas parti du product");
                            }
						}
						//trace.logClose(this,".addItemToOrder");
						return;
					}
				} // for
				//stores changes to the order in the repository:
				super.getOrderManager().updateOrder(order); 
			} // synchronized
		} catch (NumberFormatException e2) {
			//trace.logError(this,e2,".addItemToOrder Exception : "+e2.toString());
			// invalid number given for quantity of item to add
			String msg = super.formatUserMessage(MSG_INVALID_QUANTITY, pRequest, pResponse);
			addFormException(new DropletException(msg, e2, MSG_INVALID_QUANTITY));
		} catch (Exception e2) {
			//trace.logError(this,e2,".addItemToOrder Exception : "+e2.toString());
			processException(e2, MSG_ERROR_ADDING_TO_ORDER, pRequest, pResponse);
		}finally{
			//trace.logClose(this,".addItemToOrder");
		}
	}
	
	
	public String FormatNumber (Double a_Value)
	{
		//trace.logOpen(this,".FormatNumber");
		String l_strRetour="";
		try{
			double l_dValue				= a_Value.doubleValue();
			double l_dArrondi			= (new Long(Math.round(l_dValue * (Math.pow(10, 2))))).doubleValue() / Math.pow(10, 2);
			DecimalFormat l_Formateur	= new DecimalFormat("0.00");
			l_strRetour=l_Formateur.format(l_dArrondi);
		} catch (Exception e) {
			//trace.logError(this,e,".FormatNumber Exception : "+e.toString());
		}finally{
			//trace.logClose(this,".FormatNumber");
		}
		return l_strRetour;
	}
	/**
	 * 
	 * @return une liste de bean ArticleInfo
	 * @author Sylvain Deroullers
	 */
	public List getSKUSPanier()
	{
		List listeArticleInfos = new java.util.LinkedList();
		List listeCommerceItems = this.getOrder().getCommerceItems();

		for (int i = 0; i < listeCommerceItems.size(); i++)
		{
			CommerceItem cm = (CommerceItem) listeCommerceItems.get(i);
			ArticleInfo articleInfo = new ArticleInfo();
			RepositoryItem ri_Article = null;
			
			try
			{
				ri_Article = (RepositoryItem) getCatalogueRepository().getItem(cm.getCatalogRefId(), "casto_sku");
			}
			catch (RepositoryException e)
			{
				logError("Exception" + e.getMessage());
			}
			articleInfo.setReference( ((Integer) ri_Article.getPropertyValue("CodeArticle")).intValue() );
			articleInfo.setDesignation( (String) ri_Article.getPropertyValue("LibelleDescriptifArticle") );
			
			if ( ri_Article.getPropertyValue("typeExpedition") != null)
			{
				articleInfo.setTypeExpedition( ((Integer) ri_Article.getPropertyValue("typeExpedition")).intValue() );
			}
			if( ri_Article.getPropertyValue("CodeFournisseur") != null)
			{
				articleInfo.setCodeFournisseur( new Integer ( (String) ri_Article.getPropertyValue("CodeFournisseur") ) );
			}
			
			articleInfo.setDelaiBrut( ((Integer) ri_Article.getPropertyValue("GeneralementLivreEnHeures")).intValue() );

			if (isLoggingDebug())
			{
				logDebug("----> APPEL DE LA METHODE getSKUSPanier()   <-----");
				logDebug("Reference Article  		  " + ri_Article.getPropertyValue("CodeArticle"));
				logDebug("libelle Article    		  " + ri_Article.getPropertyValue("LibelleDescriptifArticle"));
				logDebug("typeExpedition     		  " + ri_Article.getPropertyValue("typeExpedition"));
				logDebug("CodeFournisseur    		  " + ri_Article.getPropertyValue("CodeFournisseur"));
				logDebug("GeneralementLivreEnHeures   " + ri_Article.getPropertyValue("GeneralementLivreEnHeures"));
			}
			listeArticleInfos.add(articleInfo);
		}
		return listeArticleInfos;
	}
} // end of CastoShoppingCartModifier class