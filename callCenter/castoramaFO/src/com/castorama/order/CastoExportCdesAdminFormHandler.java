package com.castorama.order;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import atg.droplet.DropletException;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.servlet.RepositoryFormHandler;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.castorama.constantes.CastoConstantes;

/**
 * .
 * @author Logica
 * 
 * @version 0.1
 * 
 * Description :
 * 
 * Permet d esporter les commandes des admins 
 */
public class CastoExportCdesAdminFormHandler  extends RepositoryFormHandler
{
    /*
     * ------------------------------------------------------------------------
     * Constantes
     * ------------------------------------------------------------------------
     */
    private static final String SLASH = "/";
    
    private static final String POINT_VIRGULE = ";";
    
    private static final String ENTETE_CSV = 
        "Compte de l admin;Nom du detenteur du compte;Contenu de la commande;Date d export de la commande;Montant;Telephone du client;Mode de Paiement;";
    
    /*
     * ------------------------------------------------------------------------
     * Attributs
     * ------------------------------------------------------------------------
     */
    
    private Repository m_profileRepository;
    private Repository m_orderRepository;
    private String m_dateDebut;
    private String m_dateFin;
    private String m_successUrl;
    private String m_errorUrl;
    private String m_chemin;
    private String m_fichier;
    private String m_urlResultat;
    private String m_nomFichierResultat;
    
    /*
     * ------------------------------------------------------------------------
     * Accesseurs
     * ------------------------------------------------------------------------
     */
    /**
     * .
     * @return nom du fichier résultat
     */
    public String getNomFichierResultat()
    {
        return m_nomFichierResultat;
    }

    /**
     * @param a_nomFichierResultat a_nomFichierResultat
     */
    public void setNomFichierResultat(String a_nomFichierResultat)
    {
        m_nomFichierResultat = a_nomFichierResultat;
    }
    
    /**
     * .
     * @return urlResultat
     */
    public String getUrlResultat()
    {
        return m_urlResultat;
    }

    /**
     * @param a_urlResultat a_urlResultat
     */
    public void setUrlResultat(String a_urlResultat)
    {
        m_urlResultat = a_urlResultat;
    }
    
    /**
     * .
     * @return chemi du fichier
     */
    public String getFichier()
    {
        return m_fichier;
    }

    /**
     * @param a_fichier the fichier to set
     */
    public void setFichier(String a_fichier)
    {
        m_fichier = a_fichier;
    }
    
    /**.
     * Recuperation du Repository
     * @param none
     * @return Repository Repository
     * @throws none
     */
    public Repository getProfileRepository()
    {
        return m_profileRepository;
    }

    /**.
     * Modification du Profile Repository
     * @param        a_profileRepository       a_profileRepository
     * @throws       none
     */
    public void setProfileRepository(Repository a_profileRepository)
    {
        m_profileRepository = a_profileRepository;
    }
    
    /**
     * .
     * @return chemin de generation du fichier
     */
    public String getChemin()
    {
        return m_chemin;
    }

    /**
     * @param a_chemin the chemin  to set
     */
    public void setChemin(String a_chemin)
    {
        m_chemin = a_chemin;
    }
    
    /**
     * .
     * @return the dateDebut
     */
    public String getDateDebut()
    {
        return m_dateDebut;
    }

    /**
     * @param a_dateDebut the dateDebut to set
     */
    public void setDateDebut(String a_dateDebut)
    {
        m_dateDebut = a_dateDebut;
    }

    /**
     * @return the dateFin
     */
    public String getDateFin()
    {
        return m_dateFin;
    }

    /**
     * @param a_dateFin the dateFin to set
     */
    public void setDateFin(String a_dateFin)
    {
        m_dateFin = a_dateFin;
    }
    
    /**
     * @return the errorUrl
     */
    public String getErrorUrl()
    {
        return m_errorUrl;
    }

    /**
     * @param a_errorUrl the errorUrl to set
     */
    public void setErrorUrl(String a_errorUrl)
    {
        m_errorUrl = a_errorUrl;
    }

    /**
     * @return the successUrl
     */
    public String getSuccessUrl()
    {
        return m_successUrl;
    }

    /**
     * @param a_successUrl the successUrl to set
     */
    public void setSuccessUrl(String a_successUrl)
    {
        m_successUrl = a_successUrl;
    }
    
    /**.
     * Recuperation du Repository
     * @param none
     * @return Repository Repository
     * @throws none
     */
    public Repository getOrderRepository()
    {
        return m_orderRepository;
    }

    /**.
     * Modification du Order Repository
     * @param        a_orderRepository       a_orderRepository
     * @throws       none
     */
    public void setOrderRepository(Repository a_orderRepository)
    {
        m_orderRepository = a_orderRepository;
    }
    
    /*
     * ------------------------------------------------------------------------
     * Méthodes
     * ------------------------------------------------------------------------
     */
    /**
     * .
     * @param a_oRequest a_oRequest
     * @param a_oResponse a_oResponse
     * @return boolean 
     * @throws IOException IOException
     */
    public boolean handleUpdate (DynamoHttpServletRequest a_oRequest, DynamoHttpServletResponse a_oResponse) 
        throws IOException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.order.CastoRattrapageCdeFormHandler.handleUpdate().");
        }

        boolean l_retour = false;
        
        // On verifie que les champs ont bien saisis
        verificationChamps();

        // Construction fichier export
        if (getFormError())
        {
            l_retour = true;
        }
        else
        {
            Repository l_orderRepo = getOrderRepository();
            if(null!=l_orderRepo)
            {
                RepositoryView l_oView;
                try
                {
                    l_oView = l_orderRepo.getView("order");
                    if(null!=l_oView)
                    {
                        if(null!=getDateDebut() && null!=getDateFin())
                        {
                            Date l_dateDeb = null;
                            Date l_dateFin = null;
                            SimpleDateFormat l_sdf = new SimpleDateFormat("dd/MM/yyyy");
                            try
                            {
                                // On definit les bornes inferieures && superieures de recherche
                                l_dateDeb = l_sdf.parse(getDateDebut());
                                l_dateFin = l_sdf.parse(getDateFin());
                                
                                
                                //Construction du fichier d export
                                if(null!=l_dateDeb && null!=l_dateFin)
                                {
                                    l_retour = exportCdes(a_oRequest, a_oResponse, l_oView, l_dateDeb,l_dateFin);
                                }
                                else
                                {
                                    if(isLoggingDebug())
                                    {
                                        logDebug("null==l_dateDeb || null==l_dateFin");
                                    }
                                    l_retour = true;
                                }
                            }
                            catch (ParseException l_ex)
                            {
                               logError(l_ex);
                               l_retour = true;
                            }
                        }
                        else
                        {
                            if(isLoggingDebug())
                            {
                                logDebug("null==l_dateDeb || null==l_dateFin");
                            }
                            l_retour = true;
                        }
                    }
                    else
                    {
                        if(isLoggingDebug())
                        {
                            logDebug("l_orderRepo.getView(order)==null");
                        }
                        l_retour = true;
                    }
                }
                catch (RepositoryException l_ex1)
                {
                    logError(l_ex1);
                    l_retour = true;
                }
            }
        }
        
        if(l_retour)
        {
            a_oResponse.sendLocalRedirect(getErrorUrl() + "?error=true", a_oRequest);
            if (isLoggingDebug())
            {
                logDebug(CastoConstantes.METHODE_SORTIE
                        + "com.castorama.order.CastoRattrapageCdeFormHandler.handleUpdate().return true");
            }
            return true;
        }
        
        a_oResponse.sendLocalRedirect(getSuccessUrl() + "?success=true", a_oRequest);
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.order.CastoRattrapageCdeFormHandler.handleUpdate().return false");
        }
        return false;
    }
    
    /**.
     * Méthode qui va récupérer les commandes à exporter
     * @param a_oRequest a_oRequest+
     * @param a_oResponse a_oResponse
     * @param a_dateDeb a_dateDeb
     * @param a_dateFin a_dateFin
     * @param a_oView a_oView
     * @return boolean
     */
    private boolean exportCdes (DynamoHttpServletRequest a_oRequest, DynamoHttpServletResponse a_oResponse, 
            RepositoryView a_oView, Date a_dateDeb, Date a_dateFin)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.order.CastoRattrapageCdeFormHandler.exportCdes");
        }
        
        boolean l_retour = false;
        try
        {
            
            if(isLoggingDebug())
            {
                logDebug("l_dateDeb : " + a_dateDeb.toString());
                logDebug("l_dateFin : " + a_dateFin.toString());
            }        
             
            QueryBuilder l_orderBuilder = a_oView.getQueryBuilder();
            
            // create a QueryExpression that represents the property userType
            QueryExpression l_orderExportDate = l_orderBuilder.createPropertyQueryExpression("exportDate");
            QueryExpression l_orderCdeAdmin = l_orderBuilder.createPropertyQueryExpression("cdeAdmin");
            
            /* declaration du tableau qui contient les criteres de la recherche*/
            Query[] l_orderQueryBorne;
            
            /* definition des variables dates */
            QueryExpression l_borneInf = null;
            QueryExpression l_borneSup = null;
            
            QueryExpression l_cdeAdmin = null;
            
            //a_dateFin.setDate(a_dateFin.getDate()+1);
            Calendar l_dFin = Calendar.getInstance();
            l_dFin.setTime(a_dateFin);
            l_dFin.add(Calendar.DAY_OF_MONTH, CastoConstantes.UN);
            a_dateFin = l_dFin.getTime();
            
            /*GregorianCalendar l_gregorianCalendar = new GregorianCalendar(); 
            l_gregorianCalendar.setGregorianChange(a_dateFin); 
            l_gregorianCalendar.add(GregorianCalendar.DATE,CastoConstantes.UN); 
            a_dateFin = l_gregorianCalendar.getGregorianChange();*/ 
            
            l_orderQueryBorne = new Query[CastoConstantes.TROIS];
            l_cdeAdmin = l_orderBuilder.createConstantQueryExpression(Boolean.TRUE);
            l_borneInf = l_orderBuilder.createConstantQueryExpression(a_dateDeb);
            l_borneSup = l_orderBuilder.createConstantQueryExpression(a_dateFin);
            
            /* On construit les requetes dates et on les met dans le tableau des criteres */
            l_orderQueryBorne[CastoConstantes.ZERO] = l_orderBuilder.createComparisonQuery(l_orderExportDate, l_borneInf, QueryBuilder.GREATER_THAN_OR_EQUALS);
            l_orderQueryBorne[CastoConstantes.UN] = l_orderBuilder.createComparisonQuery(l_orderExportDate, l_borneSup, QueryBuilder.LESS_THAN_OR_EQUALS);
            l_orderQueryBorne[CastoConstantes.DEUX] = l_orderBuilder.createComparisonQuery(l_orderCdeAdmin, l_cdeAdmin, QueryBuilder.EQUALS);
            
            /* Creation requete a partir du tableau des criteres */
            Query l_orderQuery = l_orderBuilder.createAndQuery(l_orderQueryBorne);
            
            /* Execution requete */
            RepositoryItem[] l_aList = a_oView.executeQuery(l_orderQuery);
            
            
            /*RqlStatement l_oRequeteRQL = RqlStatement.parseRqlStatement(
                "cdeAdmin=true and exportdate >= ?0 and exportdate <= ?1 ORDER BY exportdate SORT DESC");
            Object[] l_aRqlparams = new Object[CastoConstantes.DEUX];
            l_aRqlparams[0] = a_dateDeb;
            l_aRqlparams[1] = a_dateFin;
                    
            RepositoryItem[] l_aList = l_oRequeteRQL.executeQuery (a_oView, l_aRqlparams);*/
            if(null != l_aList && l_aList.length > 0)
            {
                if(isLoggingDebug())
                {
                    for(int l_i=0;l_i<l_aList.length;l_i++)
                    {
                        logDebug("---> : " + l_aList[l_i].toString());
                    }     
                }
                try
                {
                    Date l_date = new Date();
                    SimpleDateFormat l_sdfNomFichier = new SimpleDateFormat("yyyyMMddHHmmss");
                    setNomFichierResultat("CallCenter_SuiviCdesAdmin"+l_sdfNomFichier.format(l_date)+".csv");
                    String l_nomFichier =getChemin() + SLASH + getNomFichierResultat();
                    setUrlResultat(getUrlResultat()+getNomFichierResultat());
                    PrintWriter l_printwriter = 
                        new PrintWriter(new BufferedWriter(new FileWriter(l_nomFichier, false)));
                    RepositoryItem l_order = null;
                    if(isLoggingDebug())
                    {
                        logDebug("------>"+l_aList.length);
                    }
                    l_printwriter.println(ENTETE_CSV);
                    String l_ligne = null;
                    for(int l_i=0;l_i<l_aList.length;l_i++)
                    {
                        l_order = l_aList[l_i];
                        if(isLoggingDebug())
                        {
                            logDebug("------>"+l_order.toString());
                        }
                        l_ligne = constructionLigneCSV(l_order);
                        l_printwriter.println(l_ligne);
                    }
                    l_printwriter.close();
                    setFichier(l_nomFichier);
                }
                catch(IOException l_ioexception)
                {
                    logError(l_ioexception);
                    l_retour = true;
                }
            }
            else
            {
                if(isLoggingDebug())
                {
                    logDebug("Aucune commande remontée!");
                }
                l_retour = true;
            }        
                    
        }
        catch(RepositoryException l_rex)
        {
            logError(l_rex);
            l_retour = true;
        }
    
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.order.CastoRattrapageCdeFormHandler.handleUpdate().return false");
        }
        
        return l_retour;
    }
    
    /**.
     * Méthode qui va récupérer les commandes à exporter
     * @param a_order a_order
     */
    private String constructionLigneCSV(RepositoryItem a_order)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.order.CastoRattrapageCdeFormHandler.constructionLigneCSV");
        }

        StringBuffer l_line = new StringBuffer();
        List l_pg = null;
        List l_cce = null;
        RepositoryItem l_pGroup = null;
        RepositoryItem l_cceItem = null;
        RepositoryItem l_profile = null;
        RepositoryItem l_billingAddress = null;
        Object l_billingAdd = null;
        Repository l_profileRepo = getProfileRepository();
        String l_phoneNumber = "";
        Object l_amount = null;
        Object l_paymentMethod = null;
        Object l_adminLogin = a_order.getPropertyValue("adminLogin");
        if(null!=l_adminLogin)
        {
            l_line.append(l_adminLogin.toString()+POINT_VIRGULE);
        }
        else
        {
            l_line.append(POINT_VIRGULE);
        }
        
        if(null!=l_profileRepo)
        {
            String l_profileId = a_order.getPropertyValue("profileId").toString();
            if(null!=l_profileId && !"".equals(l_profileId.trim()))
            {
                try
                {
                    l_profile = l_profileRepo.getItem(l_profileId,CastoConstantes.DESCRIPTEUR_UTILISATEUR);
                }
                catch (RepositoryException l_ex)
                {
                   logError(l_ex);
                }
                l_line.append(l_profile.getPropertyValue("login")+POINT_VIRGULE);
                l_billingAddress = (RepositoryItem)l_profile.getPropertyValue("billingAddress");
                if(null!=l_billingAddress)
                {
                    l_billingAdd = l_billingAddress.getPropertyValue("phoneNumber");
                    if(null!=l_billingAdd)
                    {
                        l_phoneNumber = l_billingAddress.getPropertyValue("phoneNumber").toString();
                    }
                    else
                    {
                        l_phoneNumber="";
                    }
                }
                else
                {
                    l_phoneNumber="";
                }
            }
            else
            {
                l_line.append(POINT_VIRGULE);
            }
        }
        else
        {
            l_line.append(POINT_VIRGULE);
        }
        
        l_cce = (List)a_order.getPropertyValue("commerceItems");
        l_line.append(a_order.getRepositoryId()+" : ");
        for(Iterator l_it=l_cce.iterator();l_it.hasNext();)
        {
            l_cceItem = (RepositoryItem)l_it.next();
            l_line.append("->"+l_cceItem.getPropertyValue("quantity")+"*"+l_cceItem.getPropertyValue("catalogRefId"));
        }
        l_line.append(POINT_VIRGULE);
        
        Object l_exportDate = a_order.getPropertyValue("exportdate");
        if(null!=l_exportDate)
        {
            l_line.append(l_exportDate+POINT_VIRGULE);
        }
        else
        {
            l_line.append(POINT_VIRGULE);
        }
        
        l_pg = (List)a_order.getPropertyValue("paymentGroups");
        for(Iterator l_it=l_pg.iterator();l_it.hasNext();)
        {
            l_pGroup = (RepositoryItem)l_it.next();
            l_amount = l_pGroup.getPropertyValue("amount");
            if(null!=l_amount)
            {
                l_line.append(l_amount+POINT_VIRGULE);
            }
            else
            {
                l_line.append(POINT_VIRGULE);
            }
            
            l_line.append(l_phoneNumber+POINT_VIRGULE);
            
            l_paymentMethod = l_pGroup.getPropertyValue("paymentMethod");
            if(null!=l_paymentMethod)
            {
                l_line.append(l_paymentMethod+POINT_VIRGULE);
            }
            else
            {
                l_line.append(POINT_VIRGULE);
            }
        }
        
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.order.CastoRattrapageCdeFormHandler.constructionLigneCSV");
        }
        return l_line.toString();
    }
    
    
   /**.
    * 
    * Verifie que les champs sont bien saisis
    */
   private void verificationChamps ()
   {
       if (isLoggingDebug())
       {
           logDebug(CastoConstantes.METHODE_ENTREE
                   + "com.castorama.order.CastoRattrapageCdeFormHandler.verificationChamps().");
       }
       if (champNonSaisie(getDateDebut()))
       {
           addFormException(new DropletException("Veuillez saisir une date de début ET de fin"));
       }
       else if (!estBonFormat(getDateDebut()))
       {
           addFormException(new DropletException("La date de début n'est pas au bon format"));
       }
       else if (champNonSaisie(getDateFin()))
       {
           addFormException(new DropletException("Veuillez saisir une date de début ET de fin"));
       }
       else if (!estBonFormat(getDateFin()))
       {
           addFormException(new DropletException("La date de fin n'est pas au bon format"));
       }
       
       if (isLoggingDebug())
       {
           logDebug(CastoConstantes.METHODE_SORTIE
                   + "com.castorama.order.CastoRattrapageCdeFormHandler.verificationChamps().");
       }
   }

   /**.
    * 
    * Verifie que les champs sont bien saisis
    */
   private boolean champNonSaisie (String a_champ)
   {
           return a_champ == null || (a_champ != null && a_champ.length() == 0);
   }
   
   /**.
    * 
    * Vzrifie que les champs sont bien saisis
    */
   private boolean estBonFormat (String a_champ)
   {
       boolean l_ret = true;
       if (isLoggingDebug()) 
       {
           logDebug(a_champ);
       }
       if (a_champ.length() != CastoConstantes.DIX)
       {
           l_ret = false;
       }
       else if (a_champ.indexOf(SLASH) == CastoConstantes.NEG || a_champ.indexOf(SLASH) != CastoConstantes.DEUX)
       {
           l_ret = false;
       }
       else if (a_champ.lastIndexOf(SLASH) == CastoConstantes.NEG || a_champ.lastIndexOf(SLASH) != CastoConstantes.CINQ)
       {
           l_ret = false;
       }
      
       return l_ret;
   }
}
