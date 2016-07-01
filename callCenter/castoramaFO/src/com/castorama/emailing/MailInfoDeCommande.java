package com.castorama.emailing;

import java.util.List;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CreditCard;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.RepositoryContactInfo;
import atg.commerce.order.ShippingGroup;
import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.castorama.atout.CastoCarteAtout;
import com.castorama.atout.MoyenDePaiement;
import com.castorama.bean.BeanGetRemise;
import com.castorama.config.Configuration;
import com.castorama.utils.PricingTools;
import com.castorama.xml.XmlDocument;


/**
*	Class charg�e de g�r�rer une Repr�senation XML d'une commande pour les transformation XSL destin�es aux envois de mail.
**/
public class MailInfoDeCommande{	

	protected Repository				m_SIPSRepository ;
	protected Repository				m_ProfileRepository ;
	protected Repository				m_CatalogueRepository ;
	protected BeanGetRemise 			m_BeanGetRemise;
	protected Configuration				m_ConfigurationCasto ; 		// Param�tre g�n�raux du site
	protected CastoCarteAtout	m_CastoCarteAtout ; 	// Manager des options de paiements Atout
	
	
	/**
	* R�cup�ration du CastoCarteAtout
	* @param none
	* @return CastoCarteAtout CastoCarteAtout
	* @throws none
	*/
	public CastoCarteAtout getCastoCarteAtout(){
		return m_CastoCarteAtout;
	}
	
	/**
	* Modification du CastoCarteAtout
	* @param CastoCarteAtout CastoCarteAtout
	* @return none
	* @throws none
	*/
	public void setCastoCarteAtout(CastoCarteAtout a_CastoCarteAtout){
		m_CastoCarteAtout = a_CastoCarteAtout;
	}
	
	
	/**
	* R�cup�ration du SIPSRepository
	* @param none
	* @return Repository SIPSRepository
	* @throws none
	*/
	public Repository getSIPSRepository(){
		return m_SIPSRepository;
	}
	
	/**
	* Modification du SIPSRepository
	* @param Repository SIPSRepository
	* @return none
	* @throws none
	*/
	public void setSIPSRepository(Repository a_SIPSRepository){
		m_SIPSRepository = a_SIPSRepository;
	}
	
	
	/**
	* R�cup�ration du ProfileRepository
	* @param none
	* @return Repository ProfileRepository
	* @throws none
	*/
	public Repository getProfileRepository(){
		return m_ProfileRepository;
	}
	
	/**
	* Modification du ProfileRepository
	* @param Repository ProfileRepository
	* @return none
	* @throws none
	*/
	public void setProfileRepository(Repository a_ProfileRepository){
		m_ProfileRepository = a_ProfileRepository;
	}
	
	
	/**
	* R�cup�ration du CatalogueRepository
	* @param none
	* @return Repository CatalogueRepository
	* @throws none
	*/
	public Repository getCatalogueRepository(){
		return m_CatalogueRepository;
	}
	
	/**
	* Modification du CatalogueRepository
	* @param Repository CatalogueRepository
	* @return none
	* @throws none
	*/
	public void setCatalogueRepository(Repository a_CatalogueRepository){
		m_CatalogueRepository = a_CatalogueRepository;
	}
	
	
	/**
	* R�cup�ration du ConfigurationCasto
	* @param none
	* @return Configuration ConfigurationCasto
	* @throws none
	*/
	public Configuration getConfigurationCasto()
	{
		return m_ConfigurationCasto;
	}
	
	/**
	* Modification du ConfigurationCasto
	* @param Configuration ConfigurationCasto
	* @return none
	* @throws none
	*/
	public void setConfigurationCasto(Configuration a_ConfigurationCasto) {
		m_ConfigurationCasto = a_ConfigurationCasto;
	}
		
	
	/**
	* R�cup�ration du BeanGetRemise
	* @param none
	* @return BeanGetRemise BeanGetRemise
	* @throws none
	*/
	public BeanGetRemise getBeanGetRemise(){
		return m_BeanGetRemise;
	}	
	
	
	/**
	* Modification du BeanGetRemise
	* @param BeanGetRemise BeanGetRemise
	* @return none
	* @throws none
	*/
	public void setBeanGetRemise(BeanGetRemise a_BeanGetRemise){
		m_BeanGetRemise=a_BeanGetRemise;
	}
	
	
	/**
	* R�cup�ration d'une repr�sentation XML d'une commande pour g�n�rer un mail d'info sur cette commande.
	* ATTENTION : le XmlDocument est volontaire renvoy� non ferm�, pour les ajouts de noeuds sp�cifiques � la m�thode appelante.
	* @param	Order	Commande
	* @param	String	Type de paiement
	* @return	XmlDocument
	* @throws	none
	*/
	public XmlDocument getOrderXmlView(Order a_Order, String a_strTypePaiement){
		//trace.logOpen(this,".getOrderXmlView");
		XmlDocument	l_XmlRepresentation = new XmlDocument();	// elements xml � passer au template xsl
		try{
		
			String l_strEuroSymbol				= m_ConfigurationCasto.getEuroSymbol();
			String l_strPrixComCallCenterEuros	= m_ConfigurationCasto.getPrixComCallCenterEuros();
			String l_strPrixComCallCenterFrancs	= m_ConfigurationCasto.getPrixComCallCenterFrancs();
			
			RepositoryItem l_Profile = getProfile(a_Order);
		
			Double l_RawSubTotal = new Double (a_Order.getPriceInfo().getRawSubtotal());
			HardgoodShippingGroup l_ShippingGroup = (HardgoodShippingGroup) a_Order.getShippingGroups().get(0);
			Double l_Shipping = new Double (l_ShippingGroup.getPriceInfo().getRawShipping());
			Double l_Preparation = new Double ( ((com.castorama.commande.CastoOrder) (a_Order)).getPreparationPriceInfo().getAmount() ) ;
			double l_dRemise = m_BeanGetRemise.getRemise(a_Order);
			
			Double l_Total_Euro	= new Double (a_Order.getPriceInfo().getTotal());
			Double l_Total		= new Double (l_Total_Euro.doubleValue() * 6.55957);
		
			if (a_strTypePaiement == null){
				RepositoryItemDescriptor l_Item = getSIPSRepository().getItemDescriptor("logsips");
				RepositoryView l_ItemView = l_Item.getRepositoryView();
				String l_strStatement = "response_code=?0 AND order_id=?1";
				RqlStatement statement = RqlStatement.parseRqlStatement(l_strStatement);
				Object params[] = new Object[2];
			
				params[0] = "00";
				params[1] = a_Order.getId();
			
				RepositoryItem[] SipsLog = null;
					
				SipsLog = statement.executeQuery(l_ItemView, params);

				String l_strTransactionId="";
				String l_strMoyenPaiement="";
			
				if (SipsLog != null){
					RepositoryItem LogOrder = SipsLog[0];
					if (LogOrder != null){
						if (LogOrder.getPropertyValue("transaction_id") != null)
                            {
                            l_strTransactionId = LogOrder.getPropertyValue("transaction_id").toString();
                            }
						if (LogOrder.getPropertyValue("payment_means") != null)  
                            {
                            l_strMoyenPaiement = LogOrder.getPropertyValue("payment_means").toString();
                            }
					}
				}
				l_XmlRepresentation.addNode("transactionId",l_strTransactionId);
				l_XmlRepresentation.addNode("moyenPaiement",l_strMoyenPaiement);
			
			}
			else if(a_strTypePaiement.equalsIgnoreCase("CALL_CENTER")){
				l_XmlRepresentation.addNode("moyenPaiement",a_strTypePaiement);
			}
			else if(a_strTypePaiement.equalsIgnoreCase("CHEQUE")){
				l_XmlRepresentation.addNode("moyenPaiement",a_strTypePaiement);
				String l_strAdresseReceptionCheque	= m_ConfigurationCasto.getAdresseReceptionCheque() ;
				l_XmlRepresentation.addNode("adresseReceptionCheque",l_strAdresseReceptionCheque);
			}
			else if(a_strTypePaiement.equalsIgnoreCase("ATOUT")){
				l_XmlRepresentation.addNode("transactionId",a_Order.getId());
				l_XmlRepresentation.addNode("moyenPaiement",a_strTypePaiement);
				String l_strCodeOptionPaiementAtout		= (String) ((OrderImpl)a_Order).getPropertyValue("optionPaiementAtout");
				String l_strLibelleOptionPaiementAtout	= "";
				if(l_strCodeOptionPaiementAtout.equals(CastoCarteAtout.CODE_PAIEMENT_EN_UNE_FOIS))
                    {
                    l_strLibelleOptionPaiementAtout = CastoCarteAtout.PAIEMENT_EN_UNE_FOIS;
                    }
				else if(l_strCodeOptionPaiementAtout.equals(CastoCarteAtout.CODE_PAIEMENT_EN_PETITES_MENSUALITES))
                {
                    l_strLibelleOptionPaiementAtout = CastoCarteAtout.PAIEMENT_EN_PETITES_MENSUALITES;
                }
				else{
					MoyenDePaiement l_MoyenDePaiement = getCastoCarteAtout().getMoyenDePaiement(l_strCodeOptionPaiementAtout);
					if(l_MoyenDePaiement!=null){
						l_strLibelleOptionPaiementAtout	= l_MoyenDePaiement.getLibelle();
					}
				}
				l_XmlRepresentation.addNode("optionPaiementAtout",l_strLibelleOptionPaiementAtout);
			}
			else if(a_strTypePaiement.equalsIgnoreCase("FAX")){
				l_XmlRepresentation.addNode("transactionId",a_Order.getId());
				l_XmlRepresentation.addNode("moyenPaiement",a_strTypePaiement);
				l_XmlRepresentation.addNode("faxCallCenter",m_ConfigurationCasto.getFaxCallCenter());
			}
			
			
			/************** Livraison ***************/
			List ListSG = a_Order.getShippingGroups();
			ShippingGroup sg = (ShippingGroup) ListSG.get(0);
			
			HardgoodShippingGroup hgShippingGroup = (HardgoodShippingGroup) sg;
			RepositoryContactInfo shippingAddress = (RepositoryContactInfo) hgShippingGroup.getShippingAddress();
			
			String l_strShippingFaxNumber = shippingAddress.getFaxNumber();
			if(l_strShippingFaxNumber!=null && l_strShippingFaxNumber.length()>0)
                {
                l_XmlRepresentation.addNode("shippingAddressSociete",l_strShippingFaxNumber);
                }
			l_XmlRepresentation.addNode("shippingAddressFirstName",shippingAddress.getFirstName());
			l_XmlRepresentation.addNode("shippingAddressLastName",shippingAddress.getLastName());
			l_XmlRepresentation.addNode("shippingAddressMiddleName",shippingAddress.getMiddleName());
			l_XmlRepresentation.addNode("shippingAddressAddress1",shippingAddress.getAddress1());
			if (shippingAddress.getAddress2() != null && !shippingAddress.getAddress2().equals("")){
				l_XmlRepresentation.addNode("shippingAddressAddress2",shippingAddress.getAddress2());
			}
			
			l_XmlRepresentation.addNode("shippingAddressPostalCode",shippingAddress.getPostalCode());
			l_XmlRepresentation.addNode("shippingAddressCity",shippingAddress.getCity());
			
			if (shippingAddress.getPhoneNumber() != null && !shippingAddress.getPhoneNumber().equals("")){
				l_XmlRepresentation.addNode("shippingAddressPhoneNumber",shippingAddress.getPhoneNumber());
			}
			
			
			/************** Facturation ***************/
			List ListPG = a_Order.getPaymentGroups();
			CreditCard pg = (CreditCard) ListPG.get(0);
			RepositoryContactInfo billingAddress = (RepositoryContactInfo) pg.getBillingAddress();
			
			String l_strBillingFaxNumber = billingAddress.getFaxNumber();
			if(l_strBillingFaxNumber!=null && l_strBillingFaxNumber.length()>0) 
                {
                l_XmlRepresentation.addNode("billingAddressSociete",l_strBillingFaxNumber);
                }
			l_XmlRepresentation.addNode("billingAddressFirstName",billingAddress.getFirstName());
			l_XmlRepresentation.addNode("billingAddressLastName",billingAddress.getLastName());
			l_XmlRepresentation.addNode("billingAddressMiddleName",billingAddress.getMiddleName());
			l_XmlRepresentation.addNode("billingAddressAddress1",billingAddress.getAddress1());
			
			if (billingAddress.getAddress2() != null && !billingAddress.getAddress2().equals("")){
				l_XmlRepresentation.addNode("billingAddressAddress2",billingAddress.getAddress2());
			}
			
			l_XmlRepresentation.addNode("billingAddressPostalCode",billingAddress.getPostalCode());
			l_XmlRepresentation.addNode("billingAddressCity",billingAddress.getCity());
			
			if (billingAddress.getPhoneNumber() != null && !billingAddress.getPhoneNumber().equals("")){
				l_XmlRepresentation.addNode("billingAddressPhoneNumber",billingAddress.getPhoneNumber());
			}
			
			
			/***************** Liste d'article *******************/
			List ListeArticles = a_Order.getCommerceItems();
			for (int i=0; i < ListeArticles.size(); i++){
				CommerceItem l_Article = (CommerceItem) ListeArticles.get(i);
				
				String l_strCatalogRefID = l_Article.getCatalogRefId();
				long l_nQuantity = l_Article.getQuantity();
				Double l_dPrixUnitaire = new Double(0.0); // prix apr�s remise �ventuelle
				if(l_nQuantity>0)
                    {
                    l_dPrixUnitaire = new Double(l_Article.getPriceInfo().getAmount()/l_nQuantity);
                    }
				Double l_nTotal = new Double (l_Article.getPriceInfo().getRawTotalPrice());
				
				RepositoryItem l_ArticleItem		= getCatalogueRepository().getItem(l_strCatalogRefID, "casto_sku");
				String	l_strCodeArticle			= l_ArticleItem.getPropertyValue("CodeArticle").toString();
				String	l_strLibelle				= l_ArticleItem.getPropertyValue("LibelleDescriptifArticle").toString();
				String	l_strLivraisonEnHeures		= l_ArticleItem.getPropertyValue("GeneralementLivreEnHeures").toString();
        		Boolean	l_Cadeau					= (Boolean) l_ArticleItem.getPropertyValue("cadeau");
				
				l_XmlRepresentation.openNode("article");
					l_XmlRepresentation.addNode("quantite",String.valueOf(l_nQuantity));
					l_XmlRepresentation.addNode("libelle",l_strLibelle);
					l_XmlRepresentation.addNode("prix",PricingTools.formatPrix(l_dPrixUnitaire));
					l_XmlRepresentation.addNode("departEntrepot",l_strLivraisonEnHeures);
					if(l_Cadeau!=null)
                        {
                        l_XmlRepresentation.addNode("cadeau",l_Cadeau.toString());
                        }
				l_XmlRepresentation.closeNode("article");
			}

			l_XmlRepresentation.addNode("orderId",a_Order.getId());
			l_XmlRepresentation.addNode("firstName",billingAddress.getFirstName());
			l_XmlRepresentation.addNode("lastName",billingAddress.getLastName());
			l_XmlRepresentation.addNode("profileId",a_Order.getProfileId());
			l_XmlRepresentation.addNode("email",(String)l_Profile.getPropertyValue("login"));
			l_XmlRepresentation.addNode("date",java.text.DateFormat.getDateInstance(java.text.DateFormat.LONG).format(new java.util.Date()));
			l_XmlRepresentation.addNode("prixComCallCenterEuros",l_strPrixComCallCenterEuros);
			l_XmlRepresentation.addNode("prixComCallCenterFrancs",l_strPrixComCallCenterFrancs);
			l_XmlRepresentation.addNode("euroSymbol",l_strEuroSymbol);
			if(l_dRemise>0.0){
				l_XmlRepresentation.addNode("remise",PricingTools.formatPrix(new Double(l_dRemise)));
			}
			l_XmlRepresentation.addNode("rawSubTotal",PricingTools.formatPrix(l_RawSubTotal));
			l_XmlRepresentation.addNode("shipping",PricingTools.formatPrix(l_Shipping));
			l_XmlRepresentation.addNode("preparation",PricingTools.formatPrix(l_Preparation)); 
			l_XmlRepresentation.addNode("totalEuro",PricingTools.formatPrix(l_Total_Euro));
			l_XmlRepresentation.addNode("totalFranc",PricingTools.formatPrix(l_Total));

		}catch(Exception e){
			//trace.logError(this,e,".getOrderXmlView  : "+e.toString());
		}finally{
			//trace.logClose(this,".getOrderXmlView");
		}
        return l_XmlRepresentation;	
	}
	
	
	/**
	* R�cup�ration du profil depuis la commande
	* @param none
	* @return RepositoryItem Profil
	* @throws none
	*/
	public RepositoryItem getProfile(Order a_Order){
		try{
			return getProfileRepository().getItem(a_Order.getProfileId(),"user");
		}catch(Exception e){
			//trace.logError(this,e,".getProfile : "+e.toString());
		}
		return null;
		
	}
	
	
	/**
	* R�cup�ration du profil depuis la commande
	* @param none
	* @return String
	* @throws none
	*/
	public String getMoyenDePaiementPourUneCommandeDejaCommite(Order a_Order){
		String l_strPaymentMethod = null;
		try{
			List		l_ListPG		= a_Order.getPaymentGroups();
			CreditCard	l_PaymentGroup	= (CreditCard) l_ListPG.get(0);
			l_strPaymentMethod = l_PaymentGroup.getPaymentMethod();
			if(l_strPaymentMethod.equalsIgnoreCase("Call-center")) 
            {
                l_strPaymentMethod = "CALL_CENTER";
            }
		}catch(Exception e){
			//trace.logError(this,e,".getMoyenDePaiementPourUneCommandeDejaCommite : "+e.toString());
		}
		return l_strPaymentMethod;
		
	}

	
	
	
} // end of class
