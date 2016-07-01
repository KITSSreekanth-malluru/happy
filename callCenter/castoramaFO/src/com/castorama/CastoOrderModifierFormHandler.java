package com.castorama;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.transaction.Transaction;

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.OrderManager;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PaymentGroupImpl;
import atg.commerce.order.SimpleOrderManager;
import atg.commerce.pricing.OrderPriceInfo;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.lockmanager.DeadlockException;
import atg.service.lockmanager.LockManagerException;
import atg.service.pipeline.PipelineResult;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.castorama.commande.CastoOrderTools;
import com.castorama.commerce.states.BOOrderStates;
import com.castorama.commerce.states.CastOrderStates;
import com.castorama.constantes.CastoConstantes;
import com.castorama.order.BeanCreateLogAdmin;
import com.castorama.order.ExistingOrderModifierFormHandler;

public class CastoOrderModifierFormHandler extends ExistingOrderModifierFormHandler
{
    public static final String ERROR_COMMIT_SIPS = "error_commit_sips";

    private String m_strCommentaireDeValidation; // Commentaire saisi lors de

    // la validation d'une
    // dommande dans le call
    // center

    private String m_strValidationSuccessURL; // URL de succÃ©s pour la

    // validation d'une dommande
    // dans le call center

    private String m_strMoveToOrderCommitErrorURL;

    private String m_strMoveToOrderCommitSuccessURL;

    private String m_strMoyenDePaiement;
    
    
    // GY stabilisation
    private MutableRepository m_journalisationRepository;
    
    
	private BOOrderStates boOrderStates;
    
    private CastOrderStates mOrderStates;

    /**
     * Référence vers le composant d'utilitaire pour les commandes.
     */
    private CastoOrderTools m_orderTools;

    /**
     * Renvoie une référence vers le composant d'utilitaires des commandes.
     * 
     * @return CastoOrderTools Une référence vers les composant d'utilitaires
     *         des commandes.
     */
    public CastoOrderTools getOrderTools()
    {
        return m_orderTools;
    }

    /**
     * Fixe la référence vers le composant d'utilitaires des commandes.
     * 
     * @param a_orderTools
     *            La nouvelle référence vers le composant d'utilitaires des
     *            commandes.
     */
    public void setOrderTools(CastoOrderTools a_orderTools)
    {
        m_orderTools = a_orderTools;
    }

    /**
     * RÃ©cupÃ©ration du CommentaireDeValidation
     * 
     * @param none
     * @return String
     * @throws none
     */
    public String getCommentaireDeValidation()
    {
        return m_strCommentaireDeValidation;
    }

    /**
     * Modification du CommentaireDeValidation
     * 
     * @param String
     *            CommentaireDeValidation
     * @return none
     * @throws none
     */
    public void setCommentaireDeValidation(String a_strCommentaireDeValidation)
    {
        m_strCommentaireDeValidation = a_strCommentaireDeValidation;
    }

    /**
     * RÃ©cupÃ©ration du MoyenDePaiement
     * 
     * @param none
     * @return none
     * @throws none
     */
    public String getMoyenDePaiement()
    {
        return m_strMoyenDePaiement;
    }

    /**
     * Modification du MoyenDePaiement
     * 
     * @param String
     *            MoyenDePaiement
     * @return none
     * @throws none
     */
    public void setMoyenDePaiement(String a_strMoyenDePaiement)
    {
        m_strMoyenDePaiement = a_strMoyenDePaiement;
    }

    /**
     * RÃ©cupÃ©ration du ValidationSuccessURL
     * 
     * @param none
     * @return none
     * @throws none
     */
    public String getValidationSuccessURL()
    {
        return m_strValidationSuccessURL;
    }

    /**
     * Modification du ValidationSuccessURL
     * 
     * @param String
     *            ValidationSuccessURL
     * @return none
     * @throws none
     */
    public void setValidationSuccessURL(String a_strValidationSuccessURL)
    {
        m_strValidationSuccessURL = a_strValidationSuccessURL;
    }

    private Repository m_RepositoryDesOrdres;

    public void setRepositoryDesOrdres(Repository a_RepositoryDesOrdres)
    {
        m_RepositoryDesOrdres = a_RepositoryDesOrdres;
    }

    public Repository getRepositoryDesOrdres()
    {
        return m_RepositoryDesOrdres;
    }

    private long mWantedQuantity = 0;

    public void setWantedQuantity(long pWantedQuantity)
    {
        mWantedQuantity = pWantedQuantity;
    }

    public long getWantedQuantity()
    {
        return mWantedQuantity;
    }

    private long mTotalQuantity = 0;

    public void setTotalQuantity(long pTotalQuantity)
    {
        mTotalQuantity = pTotalQuantity;
    }

    public long getTotalQuantity()
    {
        return mTotalQuantity;
    }

    // parametres pour les logs
    private String m_strType = "";

    public void setType(String a_strType)
    {
        m_strType = a_strType;
    }

    public String getType()
    {
        return m_strType;
    }

    private String m_strAction = "";

    public void setAction(String a_strAction)
    {
        m_strAction = a_strAction;
    }

    public String getAction()
    {
        return m_strAction;
    }

    private String m_strCommentaire = "";

    public void setCommentaire(String a_strCommentaire)
    {
        m_strCommentaire = a_strCommentaire;
    }

    public String getCommentaire()
    {
        return m_strCommentaire;
    }

    private String m_strIp = "";

    public void setIp(String a_strIp)
    {
        m_strIp = a_strIp;
    }

    public String getIp()
    {
        return m_strIp;
    }

    private String m_strNomLogin = "";

    public void setNomLogin(String a_strNomLogin)
    {
        m_strNomLogin = a_strNomLogin;
    }

    public String getNomLogin()
    {
        return m_strNomLogin;
    }

    private boolean handleAddItemAndLog(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
    {
        logDebug("===>"+this.getClass()+".handleAddItemAndLog");
        boolean l_bOk = true;
        String l_strOrderId = this.getOrderId();
        com.castorama.config.Configuration l_Configuration = com.castorama.config.Configuration.getConfiguration();

        if (getOrder() == null)
        {
            loadOrder(l_strOrderId);
        }
        synchronized (getOrder())
        {
            try
            {

                l_Configuration.getClientLockManager().acquireWriteLock(l_strOrderId);

                createLog(pRequest, pResponse, m_strType, m_strAction, m_strCommentaire, m_strIp, m_strNomLogin);
                l_bOk = handleAddItemToOrder(pRequest, pResponse);
            }
            catch (Exception e)
            {
                logError("===>"+this.getClass()+".handleAddItemAndLog ERROR1 : "+e.toString());
                l_bOk = false;
            }
            finally
            {
                try
                {
                    l_Configuration.getClientLockManager().releaseWriteLock(l_strOrderId);
                }
                catch (Exception e)
                {
                    logDebug("===>"+this.getClass()+".handleAddItemAndLog ERROR2 : "+e.toString());
                }
                logDebug("===>"+this.getClass()+".handleAddItemAndLog FIN");
            }
        }
        return l_bOk;
    }

    private boolean handleRemoveItemAndLog(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
    {
        // trace.logOpen(this,".handleRemoveItemAndLog");
        boolean l_bOk = true;
        String l_strOrderId = this.getOrderId();
        com.castorama.config.Configuration l_Configuration = com.castorama.config.Configuration.getConfiguration();

        if (getOrder() == null)
        {
            loadOrder(l_strOrderId);
        }
        synchronized (getOrder())
        {
            try
            {

                l_Configuration.getClientLockManager().acquireWriteLock(l_strOrderId);

                if (checkQuantity(getQuantity()))
                {
                    createLog(pRequest, pResponse, m_strType, m_strAction, m_strCommentaire, m_strIp, m_strNomLogin);
                    l_bOk = handleRemoveItemFromOrder(pRequest, pResponse);
                }
                else
                {
                    addFormException(new DropletException("notEnougthSkuToRemoveError", "notEnougthSkuToRemoveError"));
                    pResponse.sendLocalRedirect(getSaveOrderFailureURL(), pRequest);
                    l_bOk = false;
                }
            }
            catch (Exception e)
            {
                // trace.logError(this,e,".handleRemoveItemAndLog
                // orderId="+getOrderId()+" : "+e.toString());
                pResponse.sendLocalRedirect(getSaveOrderFailureURL(), pRequest);
                logError("EXCEPTION : " + e);
                l_bOk = false;
            }
            finally
            {
                try
                {
                    l_Configuration.getClientLockManager().releaseWriteLock(l_strOrderId);
                }
                catch (Exception e)
                {
                    // trace.logError(this,e,".handleRemoveItemAndLog
                    // orderId="+l_strOrderId+" : "+e);
                }
                // trace.logClose(this,".handleRemoveItemAndLog");
            }
        }
        return true;
    }

    private boolean handleRemoveDefinitiveItemAndLog(DynamoHttpServletRequest pRequest,
            DynamoHttpServletResponse pResponse) throws ServletException, IOException, CommerceException
            {
        // trace.logOpen(this,".handleRemoveDefinitiveItemAndLog");
        boolean l_Retour = false;
        com.castorama.config.Configuration l_Configuration = com.castorama.config.Configuration.getConfiguration();
        String l_strOrderId = this.getOrderId();
        try
        {
            createLog(pRequest, pResponse, m_strType, m_strAction, m_strCommentaire, m_strIp, m_strNomLogin);

            Order pOrder = getOrder();
            if (pOrder == null)
            {
                String msg = formatUserMessage(MSG_NO_ORDER_TO_MODIFY, pRequest, pResponse);
                throw new ServletException(msg);
            }

            synchronized (getOrder())
            {

                l_Configuration.getClientLockManager().acquireWriteLock(l_strOrderId);

                String pItemId = getCommerceItemId();
                if ((pItemId == null) || (pItemId.trim().length() == 0))
                {
                    String msg = formatUserMessage(MSG_NO_ITEMS_CHOSEN, pRequest, pResponse);
                    String propertyPath = generatePropertyPath("commerceItemId");
                    throw new ServletException(msg);
                }

                l_Retour = removeItemFromOrder(pOrder, pItemId, pRequest, pResponse);
            }
        }
        catch (Exception e)
        {
            // trace.logError(this,e,".handleRemoveDefinitiveItemAndLog
            // orderId="+getOrderId()+" : "+e.toString());
            l_Retour = false;
        }
        finally
        {
            try
            {
                l_Configuration.getClientLockManager().releaseWriteLock(l_strOrderId);
            }
            catch (Exception e)
            {
                // trace.logError(this,e,".handleRemoveDefinitiveItemAndLog
                // orderId="+l_strOrderId+" : "+e);
            }
            // trace.logClose(this,".handleRemoveDefinitiveItemAndLog");
        }

        return true;
            }

    public boolean handleRemoveAndAddItemFromOrder(DynamoHttpServletRequest pRequest,
            DynamoHttpServletResponse pResponse) throws ServletException, IOException
            {
        // trace.logOpen(this,".handleRemoveAndAddItemFromOrder");
        boolean l_modificationOrder = false;
        String l_strOrderId = this.getOrderId();
        com.castorama.config.Configuration l_Configuration = com.castorama.config.Configuration.getConfiguration();

        if (getOrder() == null)
        {
            loadOrder(l_strOrderId);
        }
        synchronized (getOrder())
        {

            try
            {

                long l_Quantity = mTotalQuantity - mWantedQuantity;
                setQuantity(Math.abs(l_Quantity));
                if (mWantedQuantity >= 0)
                {
                    if (l_Quantity != 0)
                    {
                        if (l_Quantity < 0)
                        {
                            l_modificationOrder = handleAddItemAndLog(pRequest, pResponse);
                        }
                        else
                        {
                            l_modificationOrder = handleRemoveItemAndLog(pRequest, pResponse);
                        }
                    }
                    else
                    {
                        l_modificationOrder = true;
                    }

                    if (l_modificationOrder)
                    {
                        l_Configuration.getClientLockManager().acquireWriteLock(l_strOrderId);
                        try
                        {
                            //l_modificationOrder = handleSaveOrder(pRequest, pResponse);

                            setDateOrder(l_strOrderId, false);

                            OrderPriceInfo priceInfo = getOrderPriceInfo();
                            overrideUpdateOrder(l_strOrderId);

                            this.setOrderId(l_strOrderId);
                            l_modificationOrder = handleLoadOrder(pRequest, pResponse);
                        }
                        finally
                        {
                            try
                            {
                                l_Configuration.getClientLockManager().releaseWriteLock(l_strOrderId);
                            }
                            catch (Exception e)
                            {
                                logError(".handleRemoveAndAddItemFromOrder orderId=" + l_strOrderId + " : " + e);
                            }
                            
                            
                            /*
                             * ============================================================================
                             *  GY : stabilisation : journalisation
                             * ============================================================================
                             */
                            try 
                            {
                                MutableRepository l_mutRep = getJournalisationRepository();
                                MutableRepositoryItem l_journalisation = l_mutRep.createItem("journalisationCC");
                                l_journalisation.setPropertyValue("dateAction", new java.util.Date());
                                l_journalisation.setPropertyValue("action","MODIFICATION_CONTENU");
                                l_journalisation.setPropertyValue("userId",pRequest.getParameter("userId"));
                                l_journalisation.setPropertyValue("login",pRequest.getParameter("userLogin"));
                                l_journalisation.setPropertyValue("orderId",pRequest.getParameter("numeroCommande"));
                                l_mutRep.addItem(l_journalisation);
                            }
                            catch (Exception l_e)
                            {
                                logError(l_e);
                            }
                            
                            
                        }
                    }
                }
                else
                {
                    addFormException(new DropletException("negativeQuantityError", "negativeQuantityError"));
                    pResponse.sendLocalRedirect(getSaveOrderFailureURL(), pRequest);
                    return false;
                }
            }
            catch (Exception e)
            {
                logError("EXCEPTION" + e);
                l_modificationOrder = false;
                pResponse.sendLocalRedirect(getSaveOrderFailureURL(), pRequest);
                return false;
            }
            finally
            {
                logDebug(".handleRemoveAndAddItemFromOrder");
                
                
                /*
                 * ============================================================================
                 *  GY : stabilisation : journalisation
                 * ============================================================================
                 */
                try 
                {
                    MutableRepository l_mutRep = getJournalisationRepository();
                    MutableRepositoryItem l_journalisation = l_mutRep.createItem("journalisationCC");
                    l_journalisation.setPropertyValue("dateAction", new java.util.Date());
                    l_journalisation.setPropertyValue("action","MODIFICATION_CONTENU");
                    l_journalisation.setPropertyValue("userId",pRequest.getParameter("userId"));
                    l_journalisation.setPropertyValue("login",pRequest.getParameter("userLogin"));
                    l_journalisation.setPropertyValue("orderId",pRequest.getParameter("numeroCommande"));
                    l_mutRep.addItem(l_journalisation);
                }
                catch (Exception l_e)
                {
                    logError(l_e);
                }
                
                
            }
        }

        pResponse.sendLocalRedirect(getSaveOrderSuccessURL(), pRequest);
        //return true;
        return false;
            }

    /**.
     * 
     * Supprime une commande
     * @param       a_request               DynamoHttpServletRequest
     * @param       a_response              DynamoHttpServletResponse
     * @return      boolean                 Retour
     * @throws      ServletException        ServletException
     * @throws      IOException             IOException
     */
    public boolean handleSupprimeOrder(DynamoHttpServletRequest a_request, DynamoHttpServletResponse a_response)
    throws ServletException, IOException
    {
        // trace.logOpen(this,".handleSupprimeOrder");
        boolean l_bOk = true;
        String l_strOrderId = this.getOrderId();
        com.castorama.config.Configuration l_Configuration = com.castorama.config.Configuration.getConfiguration();
        OrderImpl l_Order = (OrderImpl) getOrder();
        synchronized (l_Order)
        {
            try
            {
                l_Configuration.getClientLockManager().acquireWriteLock(l_strOrderId);

                /*
                 * remplacement if(l_Order.getState() ==
                 * getOrderStates().getStateValue(getOrderStates().INCOMPLETE)){ //
                 * si la commande est toujours un panier elle n'est pas geree
                 * par le fulfillment
                 * l_Order.setState(getOrderStates().getStateValue(getOrderStates().QUOTED)); }
                 */
                if (l_Order.getState() == 0)
                { // si la commande est toujours un panier elle n'est pas
                    // geree par le fulfillment
                    l_Order.setState(9);
                }

                l_bOk = handleCancelOrder(a_request, a_response);
                createLog(a_request, a_response, m_strType, m_strAction, m_strCommentaire, m_strIp, m_strNomLogin);

                if (isLoggingInfo())
                {
                    logInfo("CastoOrderModifierFormHandler.handleSupprimeOrder : Traitement terminé " 
                            + a_request.toString() + a_response.toString() + m_strType + m_strAction
                            + m_strCommentaire + m_strIp + m_strNomLogin);
                }
                
                
                /*
                 * ============================================================================
                 *  GY : stabilisation : journalisation
                 * ============================================================================
                 */
                try 
                {
                    MutableRepository l_mutRep = getJournalisationRepository();
                    MutableRepositoryItem l_journalisation = l_mutRep.createItem("journalisationCC");
                    l_journalisation.setPropertyValue("dateAction", new java.util.Date());
                    l_journalisation.setPropertyValue("action","SUPPRESSION");
                    l_journalisation.setPropertyValue("userId",a_request.getParameter("userId"));
                    l_journalisation.setPropertyValue("login",a_request.getParameter("userLogin"));
                    l_journalisation.setPropertyValue("orderId",a_request.getParameter("numeroCommande"));
                    l_mutRep.addItem(l_journalisation);
                }
                catch (Exception l_e)
                {
                    logError(l_e);
                }
                
                
                
                

            }
            catch (Exception l_exception)
            {
                if (isLoggingError())
                {
                    logError(l_exception);
                }

                l_bOk = false;
            }
            finally
            {
                try
                {
                    l_Configuration.getClientLockManager().releaseWriteLock(l_strOrderId);
                }
                catch (Exception l_exception)
                {
                    if (isLoggingError())
                    {
                        logError(l_exception);
                    }

                }
            }
        }
        return l_bOk;
    }

    private void createLog(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, String a_strType,
            String a_strAction, String a_strCommentaire, String a_strIp, String a_strNomLogin)
    {
        // trace.logOpen(this,".createLog");
        try
        {
            BeanCreateLogAdmin l_BeanCreateLogAdmin = (BeanCreateLogAdmin) pRequest
            .resolveName("/castorama/BeanCreateLogAdmin");
            l_BeanCreateLogAdmin.enregistrement(a_strType, a_strAction, a_strCommentaire, a_strIp, a_strNomLogin);
        }
        catch (Exception e)
        {
            // trace.logError(this,e,".createLog orderId="+getOrderId()+" :
            // "+e.toString());
        }
        finally
        {
            // trace.logClose(this,".createLog");
        }
    }

    private void setDateOrder(String a_OrderId, boolean a_suppression)
    {
        // trace.logOpen(this,".setDateOrder");
        try
        {
            MutableRepository OrderRepository = (MutableRepository) getRepositoryDesOrdres();
            MutableRepositoryItem l_order = (MutableRepositoryItem) OrderRepository
            .getItemForUpdate(a_OrderId, "order");
            if (l_order != null)
            {
                synchronized (l_order)
                {
                    if (a_suppression)
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
        catch (Exception e)
        {
            // trace.logError(this,e,".setDateOrder orderId="+getOrderId()+" :
            // "+e.toString());
        }
        finally
        {
            // trace.logClose(this,".setDateOrder");
        }
    }

    /**
     * VÃ©rification du nombre d'articles retirÃ©s de la commande. Retourne
     * False si on dÃ©sire retirer plus ou autant d'articles contenus dans la
     * commande
     * 
     * @param long
     *            a_lNbSkuToRemove
     * @return boolean erreur True/False
     * @exception none
     */
    private boolean checkQuantity(long a_lNbSkuToRemove)
    {

        // trace.logOpen(this,".checkQuantity");
        boolean l_bOk = false;
        try
        {

            long l_lOrderSkuQuantity = 0;
            Repository l_OrderRepository = (Repository) getRepositoryDesOrdres();
            RepositoryItem l_Order = (RepositoryItem) l_OrderRepository.getItem(getOrderId(), "order");
            ListIterator l_CommerceItems = ((List) l_Order.getPropertyValue("commerceItems")).listIterator();

            while (l_CommerceItems.hasNext())
            {
                RepositoryItem l_CommerceItem = (RepositoryItem) l_CommerceItems.next();
                l_lOrderSkuQuantity += ((Long) l_CommerceItem.getPropertyValue("quantity")).longValue();
            }
            l_bOk = (l_lOrderSkuQuantity > a_lNbSkuToRemove);
        }
        catch (RepositoryException e)
        {
            // trace.logError(this,e,".checkQuantity RepositoryException
            // orderId="+getOrderId()+" : "+e.toString());
        }
        catch (Exception e)
        {
            // trace.logError(this,e,".checkQuantity Exception
            // orderId="+getOrderId()+" : "+e.toString());
        }
        finally
        {
            // trace.logClose(this,".checkQuantity");
        }
        return l_bOk;
    }

    /**
     * Mise à jour d'une commande et changement d'Etat à VALIDE : <br>
     * La date d'export doit être mise à null<br>
     * pour que la commande soit prise en compte lors du prochain export.
     * 
     * @param a_Request
     *            the servlet's request
     * @param a_Response
     *            the servlet's response
     * 
     * @return boolean  true si pas de redirection, false sinon.           
     *            
     * @exception ServletException
     *                if there was an error while executing the code
     * @exception IOException
     *                if there was an error with servlet io
     */
    public boolean handleValider(DynamoHttpServletRequest a_Request, DynamoHttpServletResponse a_Response)
    throws ServletException, IOException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.CastoOrderModifierFormHandler.handleValider");
        }

        boolean l_bOk = true;

        String l_strOrderId = this.getOrderId();
        com.castorama.config.Configuration l_Configuration = com.castorama.config.Configuration.getConfiguration();
        OrderImpl l_Order = (OrderImpl) getOrder();

        synchronized (l_Order)
        {
            try
            {
                l_Configuration.getClientLockManager().acquireWriteLock(l_strOrderId);

                if (isLoggingDebug())
                {
                    logDebug("--> handleValider : acquisition verrou OK.");
                }

                //handleSaveOrder(a_Request, a_Response);

                if (isLoggingDebug())
                {
                    logDebug("--> handleValider : handleSaveOrder OK.");
                }

                loadOrder(l_strOrderId);

                if (isLoggingDebug())
                {
                    logDebug("--> handleValider : loadOrder OK.");
                }

                Date l_Today = new Date();

                l_Order.setPropertyValue("exportdate", null);
                l_Order.setPropertyValue("BOState", "VALIDE");
                l_Order.setPropertyValue("BOStateDetail", "La commande est validée");
                l_Order.setPropertyValue("lastModifiedDate", l_Today);
                l_Order.setPropertyValue("submittedDate", l_Today);

                l_Order.setPropertyValue("BOStateNum", getBoOrderStates().getStateValue(BOOrderStates.VALIDE));

                if (isLoggingDebug())
                {
                    logDebug("--> handleValider : modification etat commande OK.");
                }

                /*
                 * Modification G.P. 29/01/2007
                 * On doit mettre Ã  jour le stock si la commande est payÃ©e...
                 */ 

                getOrderTools().updateInventory(l_strOrderId);

                if (isLoggingDebug())
                {
                    logDebug("--> handleValider : inventory OK.");
                }

                if (m_strCommentaireDeValidation != null)
                {
                    l_Order.setPropertyValue("commentaireValidation", "***" + l_Today + "***\n"
                            + m_strCommentaireDeValidation);
                }

                if (isLoggingDebug())
                {
                    logDebug("--> handleValider : commentaire validation OK.");
                }

                // ****** Log *****
                BeanCreateLogAdmin.createLog(a_Request, a_Response, "CallCenter", "Modification",
                        "Validation de la commande " + l_Order.getId());

                if (isLoggingDebug())
                {
                    logDebug("--> handleValider : BeanCreateLogAdmin OK.");
                }

                updateOrder(l_Order, (SimpleOrderManager) OrderManager.getOrderManager());

                
                
                /*
                 * ============================================================================
                 *  GY : stabilisation : journalisation
                 * ============================================================================
                 */
                try 
                {
                    MutableRepository l_mutRep = getJournalisationRepository();
                    MutableRepositoryItem l_journalisation = l_mutRep.createItem("journalisationCC");
                    l_journalisation.setPropertyValue("dateAction", new java.util.Date());
                    l_journalisation.setPropertyValue("action","validation_commande_"+a_Request.getParameter("EtatBOCommande"));
                    l_journalisation.setPropertyValue("userId",a_Request.getParameter("userId"));
                    l_journalisation.setPropertyValue("login",a_Request.getParameter("userLogin"));
                    l_journalisation.setPropertyValue("orderId",a_Request.getParameter("numeroCommande"));
                    l_mutRep.addItem(l_journalisation);
                }
                catch (Exception l_e)
                {
                    logError(l_e);
                }                
            
                
                
                if (isLoggingDebug())
                {
                    logDebug("--> handleValider : updateOrder OK.");
                }

                //handlePriceOrder(a_Request, a_Response);

                if (isLoggingDebug())
                {
                    logDebug("--> handleValider : handlePriceOrder OK.");
                }

                m_strCommentaireDeValidation = null;
            }
            catch (DeadlockException l_deadlockException)
            {
                logError("--> handleValider [dle] orderId : " + getOrderId() + " : " + l_deadlockException);
            }
            catch(CommerceException l_commerceException)
            {
                logError("--> handleValider [ce] orderId : " + getOrderId() + " : " + l_commerceException);
            }
            catch (Exception l_e)
            {
                logError("--> handleValider [ce] orderId : " + getOrderId() + " : " + l_e, l_e);
            }
            finally
            {
                try
                {
                    l_Configuration.getClientLockManager().releaseWriteLock(l_strOrderId);
                }
                catch(LockManagerException l_lockManagerException)
                {
                    logError("--> handleValider [lme] orderId : " + getOrderId() + " : " + l_lockManagerException);
                }

                if (l_bOk && m_strValidationSuccessURL != null)
                {
                    a_Response.sendLocalRedirect(m_strValidationSuccessURL, a_Request);
                    l_bOk = false;
                }
            }
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.CastoOrderModifierFormHandler.handleValider");
        }
        return l_bOk;
    }

    /**
     * Exporte de la commande : <br>
     * La date d'export doit Ã©tre mise Ã© null<br>
     * pour que la commande soit prise en compte lors du prochain export.
     * 
     * @param a_Request
     *            the servlet's request
     * @param a_Response
     *            the servlet's response
     * @exception ServletException
     *                if there was an error while executing the code
     * @exception IOException
     *                if there was an error with servlet io
     */
    public boolean handleExporter(DynamoHttpServletRequest a_Request, DynamoHttpServletResponse a_Response)
    throws ServletException, IOException
    {

        // trace.logOpen(this,".handleExporter
        // m_strValidationSuccessURL="+m_strValidationSuccessURL);
        boolean l_bOk = true;

        String l_strOrderId = this.getOrderId();
        com.castorama.config.Configuration l_Configuration = com.castorama.config.Configuration.getConfiguration();
        OrderImpl l_Order = (OrderImpl) getOrder();
        synchronized (l_Order)
        {
            try
            {

                l_Configuration.getClientLockManager().acquireWriteLock(l_strOrderId);

                Date l_Today = new Date();
                l_Order.setPropertyValue("exportdate", null);
                l_Order.setPropertyValue("state", "PROCESSING");
                l_Order.setPropertyValue("lastModifiedDate", l_Today);
                BeanCreateLogAdmin.createLog(a_Request, a_Response, "CallCenter", "Modification",
                        "Export de la commande " + l_Order.getId());
                updateOrder(l_Order, (SimpleOrderManager) OrderManager.getOrderManager());
                
                /*
                 * ============================================================================
                 *  GY : stabilisation : journalisation
                 * ============================================================================
                 */
                try 
                {
                    MutableRepository l_mutRep = getJournalisationRepository();
                    MutableRepositoryItem l_journalisation = l_mutRep.createItem("journalisationCC");
                    l_journalisation.setPropertyValue("dateAction", new java.util.Date());
                    l_journalisation.setPropertyValue("action","validation_commande_VALIDE");
                    l_journalisation.setPropertyValue("userId",a_Request.getParameter("userId"));
                    l_journalisation.setPropertyValue("login",a_Request.getParameter("userLogin"));
                    l_journalisation.setPropertyValue("orderId",a_Request.getParameter("numeroCommande"));
                    l_mutRep.addItem(l_journalisation);
                }
                catch (Exception l_e)
                {
                    logError(l_e);
                }                
                
                
                
                

            }
            catch (Exception e)
            {
                // trace.logError(this,e,".handleExporter
                // orderId="+getOrderId()+" : "+e.toString());
            }
            finally
            {
                try
                {
                    l_Configuration.getClientLockManager().releaseWriteLock(l_strOrderId);
                }
                catch (Exception e)
                {
                    // trace.logError(this,e,".handleExporter
                    // orderId="+l_strOrderId+" : "+e);
                }
                if (l_bOk && m_strValidationSuccessURL != null)
                {
                    a_Response.sendLocalRedirect(m_strValidationSuccessURL, a_Request);
                    l_bOk = false;
                }
                // trace.logClose(this,".handleExporter");
            }
        }
        return l_bOk;

    }

    /**
     * RÃ©cupÃ©ration du MoveToOrderCommitErrorURL
     * 
     * @param none
     * @return String
     * @throws none
     */
    public String getMoveToOrderCommitErrorURL()
    {
        return m_strMoveToOrderCommitErrorURL;
    }

    /**
     * Modification du MoveToOrderCommitErrorURL
     * 
     * @param String
     * @return none
     * @throws none
     */
    public void setMoveToOrderCommitErrorURL(String a_strMoveToOrderCommitErrorURL)
    {
        m_strMoveToOrderCommitErrorURL = a_strMoveToOrderCommitErrorURL;
    }

    /**
     * RÃ©cupÃ©ration du MoveToOrderCommitSuccessURL
     * 
     * @param none
     * @return String
     * @throws none
     */
    public String getMoveToOrderCommitSuccessURL()
    {
        return m_strMoveToOrderCommitSuccessURL;
    }

    /**
     * Modification du MoveToOrderCommitSuccessURL
     * 
     * @param String
     * @return none
     * @throws none
     */
    public void setMoveToOrderCommitSuccessURL(String a_strMoveToOrderCommitSuccessURL)
    {
        m_strMoveToOrderCommitSuccessURL = a_strMoveToOrderCommitSuccessURL;
    }

    /**
     * UtilisÃ©e pour de nouveau valider dans le call center une commande qui
     * est restÃ©e en Ã©tat incomplete alors que le client a validÃ© son
     * paiement dans le site
     * 
     * @param a_Request
     *            the servlet's request
     * @param a_Response
     *            the servlet's response
     * @exception ServletException
     *                if there was an error while executing the code
     * @exception IOException
     *                if there was an error with servlet io
     */
    public boolean handleMoveToOrderCommit(DynamoHttpServletRequest a_Request, DynamoHttpServletResponse a_Response)
    throws ServletException, IOException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.CastoOrderModifierFormHandler.handleMoveToOrderCommit()");
        }

        // trace.logOpen(this,".handleMoveToOrderCommit");

        String l_strOrderId = this.getOrderId();
        com.castorama.config.Configuration l_Configuration = com.castorama.config.Configuration.getConfiguration();
        OrderImpl l_Order = (OrderImpl) getOrder();
        boolean l_ret;

        synchronized (l_Order)
        {
            try
            {
                l_Configuration.getClientLockManager().acquireWriteLock(l_strOrderId);

                if (isLoggingDebug())
                {
                    logDebug("|--> MDP : " + getMoyenDePaiement());
                }

                if (getCommentaireDeValidation() != null)
                {
                    l_Order.setPropertyValue("commentaireValidation", getCommentaireDeValidation());
                }
                if (getMoyenDePaiement() != null)
                {
                    List l_PaymentGroups = l_Order.getPaymentGroups();
                    for (int i = 0; i < l_PaymentGroups.size(); i++)
                    {
                        PaymentGroup l_PaymentGroup = (PaymentGroup) l_PaymentGroups.get(i);
                        l_PaymentGroup.setPaymentMethod(getMoyenDePaiement());
                    }
                }

                /* AP if (isLoggingDebug())
                {
                    logDebug("getOrderStates().getStateValue(ERROR_COMMIT_SIPS)="
                            + getOrderStates().getStateValue(ERROR_COMMIT_SIPS));
                }
                */
                
                if (l_Order.getState() != getOrderStates().getStateValue(CastOrderStates.PROCESSING))
                {
                    // Si un commit a dï¿½jï¿½ ï¿½tï¿½ tentï¿½
                    l_Order.setState(getOrderStates().getStateValue(CastOrderStates.INCOMPLETE));
                }
                if (l_Order.getState() == getOrderStates().getStateValue(CastOrderStates.INCOMPLETE))
                {
                	/* AP
                    PipelineResult l_Result = getOrderManager().processOrder(l_Order,
                            getUserLocale(a_Request, a_Response));
                     */

                    PipelineResult l_Result = getOrderManager().processOrder(l_Order,
                    		getOrderManager().getProcessOrderMap(getUserLocale(a_Request, a_Response), null));

                    // trace.logDebug(this,".handleMoveToOrderCommit
                    // processPipelineErrors="+processPipelineErrors(l_Result));
                }

                if (!getFormError())
                {
                    l_Order.setPropertyValue("BOStateDetail",
                    "La panier a ï¿½tï¿½ validï¿½e dans le call center aprï¿½s qu'une anomalie ait ï¿½tï¿½ dï¿½tectï¿½e");

                    if (m_strMoyenDePaiement.equals("creditCard"))
                    {
                        l_Order.setPropertyValue("BOState", "VALIDE");
                        l_Order.setPropertyValue("BOStateNum", getBoOrderStates().getStateValue(BOOrderStates.VALIDE));
                        l_Order.setPropertyValue("exportdate", null);
                        l_Order.setPropertyValue("libelleBanque_avtCodeReview", null);
                        l_Order.setPropertyValue("numcheque_avtCodeReview", null);
                        l_Order.setPropertyValue("montantChequeFrancs_avtCodeReview", null);
                        l_Order.setPropertyValue("montantChequeEuros_avtCodeReview", null);
                        l_Order.setPropertyValue("numCarteAtout_avtCodeReview", null);
                        l_Order.setPropertyValue("dateValidAtout_avtCodeReview", null);
                        
                        
                        /* CR:20/08/2008:Modification du modèle de données */
                        List l_pgs = l_Order.getPaymentGroups();
                        for (int i = 0; i < l_pgs.size(); i++)
                        {
                            PaymentGroupImpl l_pg = (PaymentGroupImpl) l_pgs.get(i);
                            l_pg.setPropertyValue("libelleBanque", null);
                            l_pg.setPropertyValue("numcheque", null);
                            l_pg.setPropertyValue("montantChequeFrancs", null);
                            l_pg.setPropertyValue("montantChequeEuros", null);
                            l_pg.setPropertyValue("dateValidAtout", null);
                        }
                        /* CR:20/08/2008:Modification du modèle de données */

                        // Modification G.P. 29/01/2007
                        // On doit mettre a jour le stock si la commande est
                        // payee...
                        getOrderTools().updateInventory(l_strOrderId);

                    }
                    else if (m_strMoyenDePaiement.equals("Cheque"))
                    { // on rest sur la page de gestion de commande
                        l_Order.setPropertyValue("BOState", "PENDING_CHEQUE");
                        l_Order.setPropertyValue("BOStateNum", getBoOrderStates().getStateValue(BOOrderStates.PENDING_CHEQUE));
                        setMoveToOrderCommitSuccessURL(null);
                    }
                    /* AP 
                    else if (m_strMoyenDePaiement.equals("Atout"))
                    { // on rest sur la page de gestion de commande
                        l_Order.setPropertyValue("BOState", "VALIDE");
                        setMoveToOrderCommitSuccessURL(null);
                    }*/
                    else if (m_strMoyenDePaiement.equals("Call-Center"))
                    { // on rest sur la page de gestion de commande
                        l_Order.setPropertyValue("BOState", "PENDING_CALL_CENTER");
                        l_Order.setPropertyValue("BOStateNum", getBoOrderStates().getStateValue(BOOrderStates.PENDING_CALL_CENTER));
                        setMoveToOrderCommitSuccessURL(null);
                    }
                    else if (m_strMoyenDePaiement.equals("Virement"))
                    { // on rest sur la page de gestion de commande
                        l_Order.setPropertyValue("BOState", "PENDING_VIREMENT");
                        l_Order.setPropertyValue("BOStateNum", getBoOrderStates().getStateValue(BOOrderStates.PENDING_VIREMENT));
                        setMoveToOrderCommitSuccessURL(null);
                    }

                    l_Order.setPropertyValue("lastModifiedDate", new Date());
                    updateOrder(l_Order, (SimpleOrderManager) OrderManager.getOrderManager());
                    
                }
            }
            catch (Exception e)
            {
            	//AP
            	if (isLoggingError()) {
            		logError(e);
            	}
            	//end AP
                // trace.logError(this,e,".handleMoveToOrderCommit
                // orderId="+getOrderId()+" : "+e.toString());
            }
            finally
            {
                /*
                 * ============================================================================
                 *  GY : stabilisation : journalisation
                 * ============================================================================
                 */
                try 
                {
                    MutableRepository l_mutRep = getJournalisationRepository();
                    MutableRepositoryItem l_journalisation = l_mutRep.createItem("journalisationCC");
                    l_journalisation.setPropertyValue("dateAction", new java.util.Date());
                    l_journalisation.setPropertyValue("action","validation_commande_ERROR_COMMIT_SIPS");
                    l_journalisation.setPropertyValue("userId",a_Request.getParameter("userId"));
                    l_journalisation.setPropertyValue("login",a_Request.getParameter("userLogin"));
                    l_journalisation.setPropertyValue("orderId",a_Request.getParameter("numeroCommande"));
                    l_mutRep.addItem(l_journalisation);
                }
                catch (Exception l_e)
                {
                    logError(l_e);
                }
                
                try
                {
                    l_Configuration.getClientLockManager().releaseWriteLock(l_strOrderId);
                }
                catch (Exception e)
                {
                    // trace.logError(this,e,".handleMoveToOrderCommit
                    // orderId="+l_strOrderId+" : "+e);
                }
                // trace.logClose(this,".handleMoveToOrderCommit");
                l_ret = checkFormRedirect(getMoveToOrderCommitSuccessURL(), getMoveToOrderCommitErrorURL(), a_Request,
                        a_Response);
            }

        } // synchronized
        
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.CastoOrderModifierFormHandler.handleMoveToOrderCommit()");
        }

        return l_ret;
    }

    /**
     * Examine the pipeline result looking for any error messages, creates
     * DropletExceptions and adds them as form exceptions
     * 
     * @return true if errors were found
     */
    protected boolean processPipelineErrors(PipelineResult pResult)
    {
        if ((pResult == null) || (!pResult.hasErrors()))
        {
            return false;
        }
        boolean foundError = false;
        Object[] errorKeys = pResult.getErrorKeys();
        int length = errorKeys.length;
        for (int c = 0; c < length; c++)
        {
            Object error = pResult.getError(errorKeys[c]);
            if (isLoggingDebug())
            {
                logDebug("PipelineError: key=" + errorKeys[c] + "; error=" + error);
            }
            if (error == null)
            {
                continue;
            }

            String errorKey = errorKeys[c].toString();
            if (error instanceof Map)
            {
                Iterator iterator = ((Map) error).values().iterator();
                while (iterator.hasNext())
                {
                    String msg = iterator.next().toString();
                    addFormException(new DropletException(msg, errorKey));
                    foundError = true;
                }
            }
            else if (error instanceof Collection)
            {
                Iterator iterator = ((Collection) error).iterator();
                while (iterator.hasNext())
                {
                    String msg = iterator.next().toString();
                    addFormException(new DropletException(msg, errorKey));
                    foundError = true;
                }
            }

            else
            {
                String msg = error.toString();
                addFormException(new DropletException(msg, errorKey));
            }
            foundError = true;
        }
        return foundError;
    }

    /**
     * On sauvegarde la commande dans le reprice.
     * 
     * @param a_Request
     *            the servlet's request
     * @param a_Response
     *            the servlet's response
     * @exception ServletException
     *                if there was an error while executing the code
     * @exception IOException
     *                if there was an error with servlet io
     */
    public boolean handlePriceOrder(DynamoHttpServletRequest a_Request, DynamoHttpServletResponse a_Response)
    throws ServletException, IOException
    {
        boolean l_bOk = super.handlePriceOrder(a_Request, a_Response);
        try
        {
            if (!getFormError())
            {
                updateOrder(getOrder(), getOrderManager());
            }
        }
        catch (Exception e)
        {

            logError(".handlePriceOrder erreur lors de l'enregistrement de la commande : " + e.toString());
        }

        return l_bOk;
    }

    /**
     * Loads an order from the order repository
     * 
     * @param pRequest
     *            the servlet's request
     * @param pResponse
     *            the servlet's response
     * @exception ServletException
     *                if there was an error while executing the code
     * @exception IOException
     *                if there was an error with servlet io
     */
    public boolean handleLoadOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
    {
        Transaction tr = null;
        try
        {
            setEnsureTransaction(true);

            tr = ensureTransaction();

            String id = getOrderId();
            if ((id == null) || (id.trim().length() == 0))
            {
                String msg = formatUserMessage(MSG_NO_ORDER_ID, pRequest, pResponse);
                String propertyPath = generatePropertyPath("orderId");
                addFormException(new DropletFormException(msg, propertyPath, MSG_NO_ORDER_ID));
            }

            // if (!checkFormRedirect(null, getLoadOrderFailureURL(), pRequest,
            // pResponse))
            // {
            // return false;
            // }

            if (isLoggingDebug())
            {
                logDebug("Attempting to Load Order #" + id);
            }

            if (!loadOrder(id))
            {
                String msg = formatUserMessage(MSG_NO_ORDER_TO_MODIFY, id, pRequest, pResponse);
                String propertyPath = generatePropertyPath("order");
                addFormException(new DropletFormException(msg, propertyPath, MSG_NO_ORDER_TO_MODIFY));
            }

            if (!getFormError())
            {
                setMoyenDePaiement(((PaymentGroup) getOrder().getPaymentGroups().get(0)).getPaymentMethod());
            }

            // return checkFormRedirect(getLoadOrderSuccessURL(),
            // getLoadOrderFailureURL(), pRequest, pResponse);
            return true;
        }
        finally
        {
            if (tr != null)
            {
                commitTransaction(tr);
            }
        }

    }// end class

    /**
     * @return the journalisationRepository
     */
    public MutableRepository getJournalisationRepository()
    {
        return m_journalisationRepository;
    }

    /**
     * @param a_journalisationRepository the journalisationRepository to set
     */
    public void setJournalisationRepository(MutableRepository a_journalisationRepository)
    {
        m_journalisationRepository = a_journalisationRepository;
    }

	public BOOrderStates getBoOrderStates() {
		return boOrderStates;
	}

	public void setBoOrderStates(BOOrderStates boOrderStates) {
		this.boOrderStates = boOrderStates;
	}
	
    public void setOrderStates(CastOrderStates orderstates)
    {
        mOrderStates = orderstates;
    }

    public CastOrderStates getOrderStates()
    {
        return mOrderStates;
    }
	
}