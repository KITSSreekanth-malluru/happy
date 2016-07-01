package com.castorama.order;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import atg.droplet.DropletException;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
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
 * Permet de Lister les commandes dans le order repository backup en fonction des dates
 * entrees dans le formulaire.
 */
public class CastoRattrapageCdeFormHandler  extends RepositoryFormHandler
{
    /*
     * ------------------------------------------------------------------------
     * Constantes
     * ------------------------------------------------------------------------
     */
    private static final String SLASH = "/";
    
    private static final String ORDER_BACKUP = "order_backup";
    
    private static final String DATE_COMMANDE = "dateCommande";
    
    private static final String TOTAL_COMMANDE = "totalCommande";

    /*
     * ------------------------------------------------------------------------
     * Attributs
     * ------------------------------------------------------------------------
     */
    private Repository m_orderBackupRepository;
    private String m_dateDebut;
    private String m_dateFin;
    private String m_successUrl;
    private String m_errorUrl;
    private String m_montant;
    private List m_resultat;
    
    /*
     * ------------------------------------------------------------------------
     * Accesseurs
     * ------------------------------------------------------------------------
     */
    /**
     * @return the setMontant
     */
    public String getMontant()
    {
        return m_montant;
    }

    /**
     * @param a_montant the a_montant to set
     */
    public void setMontant(String a_montant)
    {
        m_montant = a_montant;
    }
    
    /**
     * @return the resultat
     */
    public List getResultat()
    {
        return m_resultat;
    }

    /**
     * @param a_resultat the resultat to set
     */
    public void setResultat(List a_resultat)
    {
        m_resultat = a_resultat;
    }
    
    /**
     * Renvoie une référence vers le repository des order backup.
     * @return Une référence vers le repository des order backup.
     */
    public Repository getOrderBackupRepository()
    {
        return m_orderBackupRepository;
    }

    /**
     * Fixe la référence du repository des order backup.
     * 
     * @param a_orderBackupRepository
     *            La nouvelle référence du repository des order backup.
     */
    public void setOrderBackupRepository(Repository a_orderBackupRepository)
    {
        m_orderBackupRepository = a_orderBackupRepository;
    }
    
    /**
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

        // On verifie que les champs ont bien saisis
        verificationChamps();

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.order.CastoRattrapageCdeFormHandler.handleUpdate().");
        }
        
        if (getFormError())
        {
            a_oResponse.sendLocalRedirect(getErrorUrl() + "?error=true", a_oRequest);
            return true;
        }
        
        // Utilitaire de recherche de commande
        List l_orders = findOrderBackup();
        setResultat(l_orders);
        a_oResponse.sendLocalRedirect(getSuccessUrl() + "?success=true", a_oRequest);
        return false;
    }
    
   /**
    * Retourne les commandes entre les dates demandees et du montant renseigne.
    * .
    */ 
   private List findOrderBackup()
   {
       if (isLoggingDebug())
       {
           logDebug(CastoConstantes.METHODE_ENTREE
                   + "com.castorama.order.CastoRattrapageCdeFormHandler.findOrderBackup().");
       }
       Repository l_repo = getOrderBackupRepository();
       List l_ordersRepo = null;
       if(null!=l_repo)
       {
           try
           {
               RepositoryItemDescriptor l_orderDesc = l_repo.getItemDescriptor(ORDER_BACKUP);
               RepositoryView l_orderView = l_orderDesc.getRepositoryView();
               QueryBuilder l_orderBuilder = l_orderView.getQueryBuilder();
    
               // create a QueryExpression that represents the property userType
               QueryExpression l_orderType = l_orderBuilder.createPropertyQueryExpression(DATE_COMMANDE);
               QueryExpression l_orderTypeMontant = l_orderBuilder.createPropertyQueryExpression(TOTAL_COMMANDE);
    
               // create a QueryExpression that represents the constant 2
               String l_pattern = "dd/MM/yy" ; 
               Date l_dateDeb = null; 
               Date l_dateFin = null; 
               
               /* parsing des dates */
               l_dateDeb = (new SimpleDateFormat( l_pattern )).parse( getDateDebut() ) ; 
               l_dateFin = (new SimpleDateFormat( l_pattern )).parse( getDateFin() ) ; 
               
               /* declaration du tableau qui contient les criteres de la recherche*/
               Query[] l_orderQueryBorne;
               
               /* definition des variables dates */
               QueryExpression l_borneInf;
               QueryExpression l_borneSup;
               
               /* definition de la variables montant */
               QueryExpression l_montant;
               
               /* On fixe les valeurs des dates*/
               if(null!=l_dateDeb)
               {
                    l_borneInf = l_orderBuilder.createConstantQueryExpression(l_dateDeb);
               }
               else
               {
                    l_borneInf = l_orderBuilder.createConstantQueryExpression(new Date());
               }
               if(null!=l_dateFin)
               {
                   l_borneSup = l_orderBuilder.createConstantQueryExpression(l_dateFin);
               }
               else
               {
                   l_borneSup = l_orderBuilder.createConstantQueryExpression(new Date());
               }
               
               /* On fixe la valeur du critere montant si le champ est renseigne*/
               if(null == getMontant() || "".equals(getMontant().trim()))
               {
                   l_orderQueryBorne = new Query[CastoConstantes.DEUX];
               }
               else
               {
                   l_orderQueryBorne = new Query[CastoConstantes.TROIS];
                   /* Le parsing en double est assure de reussite car verifie dans verificationChamps().estBonFormatMontant() */
                   l_montant = l_orderBuilder.createConstantQueryExpression( new Double(getMontant()));
                   l_orderQueryBorne[CastoConstantes.DEUX] = l_orderBuilder.createComparisonQuery(l_orderTypeMontant, l_montant, QueryBuilder.EQUALS);
               }
               
               /* On construit les requetes dates et on les met dans le tableau des criteres */
               l_orderQueryBorne[0] = l_orderBuilder.createComparisonQuery(l_orderType, l_borneInf, QueryBuilder.GREATER_THAN_OR_EQUALS);
               l_orderQueryBorne[1] = l_orderBuilder.createComparisonQuery(l_orderType, l_borneSup, QueryBuilder.LESS_THAN_OR_EQUALS);
               
               /* Creation requete a partir du tableau des criteres */
               Query l_orderQuery = l_orderBuilder.createAndQuery(l_orderQueryBorne);
               
               /* Execution requete */
               RepositoryItem[] l_orders = l_orderView.executeQuery(l_orderQuery);
               
               /* Analyse du resultat */
               if (l_orders == null)
               {
                   if (isLoggingDebug())
                   {
                       logDebug("Aucune Commande trouvee.");
                   }
               }
               else
               {
                   for (int l_i=0; l_i<l_orders.length; l_i++)
                   {
                       logDebug("id: " + l_orders[l_i].getRepositoryId());
                   }
                   l_ordersRepo = Arrays.asList(l_orders);
               }
           } 
           catch (ParseException l_ex) 
           { 
               logError( l_ex.getMessage() ) ; 
           } 
           catch(RepositoryException l_rex)
           {
               logError(l_rex);
           }    
       }
       else
       {
           logDebug("getOrderBackupRepository()==null");
       }
       if (isLoggingDebug())
       {
           logDebug(CastoConstantes.METHODE_SORTIE
                   + "com.castorama.order.CastoRattrapageCdeFormHandler.findOrderBackup().");
       }
       
       return l_ordersRepo;
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
       else if (!estBonFormatMontant(getMontant()))
       {
           addFormException(new DropletException("Le montant n'est pas au bon format"));
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
       if (a_champ.length() != CastoConstantes.HUIT)
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
   
   /**.
    * 
    * Vzrifie que les champs sont bien saisis
    */
   private boolean estBonFormatMontant (String a_champ)
   {
       boolean l_ret = true;
       try
       {
           if(null!=a_champ && !"".equals(a_champ.trim()))
                   new Double(a_champ);
       }
       catch(NumberFormatException l_nfex)
       {
           if(isLoggingDebug())
           {
               logDebug(l_nfex); 
           }
           l_ret=false;
       }
       return l_ret;
   }
}
